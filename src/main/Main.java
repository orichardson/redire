package main;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LogisticClassifierFactory;
import edu.stanford.nlp.util.Pair;
import framework.Features;
import framework.MSR;
import framework.ParaExample;
import utensils.LOG;
import utensils.Util;

class Main {
	public static int MODE = 3;

	public static Dataset<Boolean, Double> makeFeatureData(
			Collection<? extends ParaExample> examples, int mode) {

		Dataset<Boolean, Double> ds = new Dataset<Boolean, Double>();
		for (ParaExample ex : examples)
			ds.add(Features.computeFullFeatureVector(ex.first(), ex.second(), mode), ex.isPara());

		return ds;
	}
	public static Dataset<Boolean, Double> makeBaselineData(Collection<? extends ParaExample> examples) {
		Dataset<Boolean, Double> ds = new Dataset<Boolean, Double>();
		for (ParaExample ex : examples)
			ds.add(Features.computeBaselineFV(ex.first(), ex.second()), ex.isPara());

		return ds;
	}
	
	public static void printStats(Classifier<Boolean,Double> c, GeneralDataset<Boolean, Double> testd) {
		Pair<Double, Double> pr = c.evaluatePrecisionAndRecall(testd, true);
		LOG.m("Precision and recall: " + pr);
		LOG.m("F-score: " + ((2 * pr.first * pr.second) / (pr.first + pr.second)));
		LOG.m("Accuracy : " + c.evaluateAccuracy(testd));
	}

	public static void main(String[] args) throws FileNotFoundException {
		Util.initialize();

		// The MSR training file and test file	
		String training_file = "data/msr/msr_paraphrase_train.txt";
		String testing_file = "data/msr/msr_paraphrase_test.txt";

		List<MSR> trainMSR = MSR.read(training_file), testMSR = MSR.read(testing_file);
		LOG.m("Data Loaded.");

		int paras = 0, count = 0;
		for (MSR r : trainMSR) {
			paras += r.isPara() ? 1 : 0;
			count += 1;
		}
		System.out.println(paras + "\t" + count + "\t" + (paras / (double) count));

		Dataset<Boolean, Double> fv0_train = makeBaselineData(trainMSR), fv0_test = makeBaselineData(testMSR);

		// baseline
		LOG.m("Base Line: ");
		Classifier<Boolean, Double> classifier0 = new LogisticClassifierFactory<Boolean, Double>()
				.trainClassifier(fv0_train);
		LOG.m("Classifier Trained");
		printStats(classifier0, fv0_test);

		// actual classifier
		Dataset<Boolean, Double> fv_train = makeFeatureData(trainMSR, MODE), fv_test = makeFeatureData(testMSR, MODE);
		Classifier<Boolean, Double> classifier = new LogisticClassifierFactory<Boolean, Double>()
				.trainClassifier(fv_train);
		LOG.m("Full classifier trained");
		printStats(classifier, fv_test);

		// fv_train.printSVMLightFormat(new PrintWriter("out/msr_para.train"));
		// fv_test.printSVMLightFormat(new PrintWriter("out/msr_para.test"));
	}
}
