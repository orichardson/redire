package eisenstein;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.trees.TypedDependency;
import framework.MSR;
import utensils.LOG;
import utensils.Util;

public class DistribFeature {
	public static Counter<String> makeNGramFeatures(int n, Annotation a) {
		n = n - 1;
		Counter<String> count = new ClassicCounter<>();

		List<String> tokens = Util.tokenizePIPE(a);
		for (int i = n; i < tokens.size(); i++) {
			String name = tokens.get(i);

			for (int j = i - n; j < i; j++)
				name += "==>" + tokens.get(j);

			count.incrementCount(name);
		}

		return count;
	}

	public static Counter<String> makeDepFigures(Annotation a) {
		Counter<String> count = new ClassicCounter<>();

		for (TypedDependency td : Util.dependencyPIPE(a))
			count.incrementCount(td.dep().word() + "--" + td.gov().word());

		return count;
	}

	public static Counter<String> computeFeatures(String s) {
		Annotation a = Util.annotate(s);
		Counter<String> counter = makeNGramFeatures(1, a);
		counter.addAll(makeNGramFeatures(2, a));
		counter.addAll(makeDepFigures(a));

		return counter;
	}

	public static void write(Dataset<Integer, String> ds, String fn) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(fn));
			ds.printSVMLightFormat(pw);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String training_file = "data/msr/msr_paraphrase_train.txt";
		String testing_file = "data/msr/msr_paraphrase_test.txt";
		Util.initialize();

		List<MSR> trainMSR = MSR.read(training_file), testMSR = MSR.read(testing_file);

		Dataset<Integer, String> trainfeatures = new Dataset<>(), testfeatures = new Dataset<>();

		int counter = 0;
		for (MSR msr : trainMSR) {
			int isp = msr.isPara() ? 1 : 0;
			trainfeatures.add(new BasicDatum<Integer, String>(computeFeatures(msr.first()).keySet(), isp));
			trainfeatures.add(new BasicDatum<Integer, String>(computeFeatures(msr.second()).keySet(), isp));
			if (++counter % 100 == 0)
				System.out.println(counter);
		}
		for (MSR msr : testMSR) {
			int isp = msr.isPara() ? 1 : 0;
			testfeatures.add(new BasicDatum<Integer, String>(computeFeatures(msr.first()).keySet(), isp));
			testfeatures.add(new BasicDatum<Integer, String>(computeFeatures(msr.second()).keySet(), isp));
		}

		for (String str : testfeatures.featureIndex())
			if (!trainfeatures.featureIndex.contains(str))
				trainfeatures.featureIndex.addToIndex(str);

		testfeatures.changeFeatureIndex(trainfeatures.featureIndex);

		write(trainfeatures, "./out/train_distsim2.txt");
		trainfeatures.featureIndex.saveToFilename("./out/train_f_index_distsim2.txt");
		write(testfeatures, "./out/test_distsim2.txt");

		LOG.m("...done");
		Thread.sleep(1000);
	}

}
