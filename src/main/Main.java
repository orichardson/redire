package main;

import java.io.FileNotFoundException;

import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.LogisticClassifier;
import edu.stanford.nlp.classify.LogisticClassifierFactory;
import edu.stanford.nlp.util.Pair;
import framework.FeatureVectors;
import utensils.Util;

class Main {
	public static int MODE = 3;
	public static boolean DEBUG = true;

	public static void log(String str) {
		if (DEBUG)
			System.out.println(str);
	}

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
		log("Training Data Loaded");
		Dataset<Boolean, Double> fv_test = loadMSR(testing_file);
		log("Test Data Loaded");

		LogisticClassifier<Boolean, Double> classifier = new LogisticClassifierFactory<Boolean, Double>()
				.trainClassifier(fv_train);
		log("Classifier Trained");
		
		Pair<Double, Double> pr = classifier.evaluatePrecisionAndRecall(fv_test, true);
		log("Precision and recall: "+pr);

		// fv_train.printSVMLightFormat(new PrintWriter("out/msr_para.train"));
		// fv_test.printSVMLightFormat(new PrintWriter("out/msr_para.test"));
	}
}
