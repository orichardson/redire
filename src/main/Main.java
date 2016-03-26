package main;

import java.io.FileNotFoundException;
import java.util.Collection;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.Dataset;
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
			Collection<? extends ParaExample> examples,	int mode) {

		Dataset<Boolean, Double> ds = new Dataset<Boolean, Double>();
		for (ParaExample ex : examples)
			ds.add(Features.computeFeatureVector(ex.first(), ex.second(), mode), ex.isPara());

		return ds;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Util.initialize();

		// The MSR training file and test file	
		String training_file = "data/msr/msr_paraphrase_train.txt";
		String testing_file = "data/msr/msr_paraphrase_test.txt";

		Dataset<Boolean, Double> fv_train = makeFeatureData(MSR.read(training_file), MODE);
		LOG.m("Training Data Loaded");
		Dataset<Boolean, Double> fv_test = makeFeatureData(MSR.read(testing_file), MODE);
		LOG.m("Test Data Loaded");
		
		LOG.m("Base Line: ");

		Classifier<Boolean, Double> classifier = new LogisticClassifierFactory<Boolean, Double>()
				.trainClassifier(fv_train);
		LOG.m("Classifier Trained");

		Pair<Double, Double> pr = classifier.evaluatePrecisionAndRecall(fv_test, true);
		LOG.m("Precision and recall: " + pr);

		// fv_train.printSVMLightFormat(new PrintWriter("out/msr_para.train"));
		// fv_test.printSVMLightFormat(new PrintWriter("out/msr_para.test"));
	}
}
