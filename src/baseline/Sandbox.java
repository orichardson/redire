package baseline;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.stanford.nlp.classify.LogisticClassifier;
import edu.stanford.nlp.classify.LogisticClassifierFactory;
import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;

public class Sandbox {

	public static void main(String[] args) throws Exception {
//		Util.initialize();
//
//		String text = "Tom ran to the store to buy some milk";
//		//String text1 = "Oliver doesn't believe in my code.";
//
//		Annotation a = Util.annotate(text);
//		System.out.println(Util.rootLemPIPE(a));
		RVFDataset<Integer, String> x = makeRandomData(150);
		x.writeSVMLightFormat(new File("./out/RANDOM.txt"));
		x.featureIndex().saveToFilename("./out/RANDOM_F_index.txt");

		RVFDataset<String, String> loaded = RVFDataset.readSVMLightFormat("./out/RANDOM.txt");
		Index<Integer> hi = new HashIndex<>(Arrays.asList(1, -1));
		Index<String> fi = HashIndex.loadFromFilename("./out/RANDOM_F_index.txt");

		RVFDataset<Integer, String> y = new RVFDataset<Integer, String>(hi,
				loaded.getLabelsArray(), fi,
				loaded.getDataArray(), loaded.getValuesArray());

		y.summaryStatistics();
		LogisticClassifierFactory<Integer, String> factory = new LogisticClassifierFactory<>();
		//System.setErr(DEVNULL);
		LogisticClassifier<Integer, String> classifier = factory.trainClassifier(y);

	}

	public static RVFDataset<Integer, String> makeRandomData(int nfeat) {
		RVFDataset<Integer, String> data = new RVFDataset<>();
		for (int l = 0; l < 2000; l++) {
			Counter<String> thing = new ClassicCounter<String>(134);
			for (int i = 0; i < nfeat; i++)
				thing.incrementCount("Thing~|" + i, Math.random());

			data.add(new RVFDatum<Integer, String>(thing, Math.random() > 0.5 ? 1 : -1));
		}
		return data;
	}

	public static void playWithData() {
		RVFDataset<Integer, String> ds = new RVFDataset<Integer, String>();

		ds.add(makeDatum(0, 0.1, 0.9, 0.2, 0.8));
		ds.add(makeDatum(2, 0.12, 0.9, 0.2, 0.8));
		ds.add(makeDatum(1, 0.13, 0.9, 0.2, 0.8));
		ds.add(makeDatum(5, 0.14, 0.9, 0.2, 0.8));

		System.out.println(Arrays.deepToString(ds.getValuesArray()));
		System.out.println(ds.featureIndex());
		ds.printFullFeatureMatrixWithValues(new PrintWriter(System.out));
		ds.ensureRealValues();

		System.out.println();
		ds.printSVMLightFormat();
//		System.out.println(ds.);

//		Dataset<Integer, Double> ds2 = new Dataset<>();
//		ds2.add(Arrays.asList(0.1, 0.9, 0.2, 0.8), 5);
//		ds2.add(Arrays.asList(0.11, 0.9, 0.2, 0.8), 6);
//		ds2.add(Arrays.asList(0.12, 0.9, 0.2, 0.8), 7);
//		ds2.add(Arrays.asList(0.13, 0.9, 0.2, 0.8), 8);
//
//		System.out.println(Arrays.deepToString(ds2.getDataArray()));
//		System.out.println(ds2.featureIndex());
//		ds2.printSparseFeatureMatrix();
	}

	public static RVFDatum<Integer, String> makeDatum(int l, double... arr) {
		Counter<String> c = new ClassicCounter<>();
		for (int i = 0; i < arr.length; i++)
			c.incrementCount("F" + i, arr[i]);
		return new RVFDatum<>(c, l);
	}

}
