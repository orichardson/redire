package main;

import java.io.FileNotFoundException;

import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.LogisticClassifier;
import edu.stanford.nlp.classify.LogisticClassifierFactory;
import edu.stanford.nlp.util.Pair;
import framework.FeatureVectors;
import utensils.LOG;
import utensils.Util;

class Main {
	public static int MODE = 3;

	public static Dataset<Boolean, Double> loadMSR(String filename) {
		Dataset<Boolean, Double> ds = new Dataset<Boolean, Double>();
		for (MSR msr : Util.readMSRFile(filename))
			ds.add(FeatureVectors.computeFeatureVector(msr.first(), msr.second(), MODE), msr.isParaphrase());

		return ds;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Util.initialize();

		// The MSR training file and test file
		String training_file = "data/msr/msr_paraphrase_train.txt";
		String testing_file = "data/msr/msr_paraphrase_test.txt";

		Dataset<Boolean, Double> fv_train = loadMSR(training_file);
		LOG.m("Training Data Loaded");
		Dataset<Boolean, Double> fv_test = loadMSR(testing_file);
		LOG.m("Test Data Loaded");

		LogisticClassifier<Boolean, Double> classifier = new LogisticClassifierFactory<Boolean, Double>()
				.trainClassifier(fv_train);
		LOG.m("Classifier Trained");
		
		Pair<Double, Double> pr = classifier.evaluatePrecisionAndRecall(fv_test, true);
		LOG.m("Precision and recall: "+pr);

		// fv_train.printSVMLightFormat(new PrintWriter("out/msr_para.train"));
		// fv_test.printSVMLightFormat(new PrintWriter("out/msr_para.test"));
	}
}
