package main;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LogisticClassifier;
import edu.stanford.nlp.classify.LogisticClassifierFactory;
import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.util.Pair;
import framework.Features;
import framework.MSR;
import framework.ParaExample;
import utensils.LOG;
import utensils.Util;

/**
 * 
 * @author Maks Cegielski-Johnson & Oliver Richardson
 *
 */
class Main {

	/**
	 * Determines which features are selected for classification.
	 */
	public static int MODE = 1;

	/**
	 * Creates a dataset for machine learning.
	 * 
	 * @param examples
	 * @param mode
	 * @return
	 */
	public static RVFDataset<Integer, String> makeFeatureData(Collection<? extends ParaExample> examples, int mode) {

		RVFDataset<Integer, String> ds = new RVFDataset<>();
		for (ParaExample ex : examples)
			ds.add(dumbConversion(Features.computeFullFeatureVector(ex.first(), ex.second(), mode),
					ex.isPara() ? 1 : -1));

		return ds;
	}

	/**
	 * Creates the baseline dataset for machine learning.
	 * 
	 * @param examples
	 *            - training examples
	 * @return
	 */
	public static RVFDataset<Integer, String> makeBaselineData(Collection<? extends ParaExample> examples) {
		RVFDataset<Integer, String> ds = new RVFDataset<>();
		for (ParaExample ex : examples) {
			ds.add(dumbConversion(Features.computeBaselineFV(ex.first(), ex.second()), ex.isPara() ? 1 : -1));
		}

		return ds;
	}

	public static <E> RVFDatum<E, String> dumbConversion(List<Double> list, E label) {
		ClassicCounter<String> counter = new ClassicCounter<String>();

		for (int i = 0; i < list.size(); i++) {
			counter.incrementCount("F" + i, list.get(i));
		}

		return new RVFDatum<E, String>(counter, label);
	}

	/**
	 * Prints the statistics from the machine learning classifier.
	 * 
	 * @param c
	 *            - Machine learning classifier
	 * @param testd
	 *            - test data set
	 */
	public static <A, B> void printStats(Classifier<A, B> c, GeneralDataset<A, B> testd) {
		LOG.m("Accuracy: " + c.evaluateAccuracy(testd));
		LOG.m("Precision & Recall & F-measure");
		for (A val : testd.labelIndex()) {
			Pair<Double, Double> pr = c.evaluatePrecisionAndRecall(testd, val);
			LOG.m(pr.first + " & " + pr.second + " & " + ((2 * pr.first * pr.second) / (pr.first + pr.second)));
		}
	}

	public static void main(String[] args) throws Exception {
		Util.initialize();

		// The MSR training file and test file	
		String training_file = "data/msr/msr_paraphrase_train.txt";
		String testing_file = "data/msr/msr_paraphrase_test.txt";

		List<MSR> trainMSR = MSR.read(training_file), testMSR = MSR.read(testing_file);
		LOG.m("Data Loaded.");

		//Calculate statistics on the training set.
		int paras = 0, count = 0;
		for (MSR r : trainMSR) {
			paras += r.isPara() ? 1 : 0;
			count += 1;
		}
		System.out.println(paras + "\t" + count + "\t" + (paras / (double) count));

		//Make the baseline data feature vectors
		RVFDataset<Integer, String> fv0_train = makeBaselineData(trainMSR), fv0_test = makeBaselineData(testMSR);

		// baseline
		LOG.m("Base Line: ");
		LogisticClassifier<Integer, String> classifier0 = new LogisticClassifierFactory<Integer, String>()
				.trainClassifier(fv0_train);
		LOG.m("Classifier Trained");
		printStats(classifier0, fv0_test);
		
		LOG.m(Arrays.toString(classifier0.getWeights()));

		// actual classifier
		RVFDataset<Integer, String> fv_train = makeFeatureData(trainMSR, MODE),
				fv_test = makeFeatureData(testMSR, MODE);
		Classifier<Integer, String> classifier = new LogisticClassifierFactory<Integer, String>()
				.trainClassifier(fv_train);
		LOG.m("Full classifier trained");
		printStats(classifier, fv_test);

		System.out.println("\n\nToken: " + Features.tokenTime.checkS());
		System.out.println("Stem: " + Features.stemTime.checkS());
		System.out.println("Soundex: " + Features.soundexTime.checkS());
		System.out.println("POS: " + Features.posTime.checkS());

//		System.out.println(Arrays.deepToString(fv0_train.getDataArray()));
//		fv0_train.printSparseFeatureMatrix();
//		System.out.println("\n***********************************\n");
//		fv_train.printSparseFeatureMatrix();

		fv0_train.printSVMLightFormat(new PrintWriter("out/msr_para0.train"));
		fv0_test.printSVMLightFormat(new PrintWriter("out/msr_para0.test"));
		fv_train.printSVMLightFormat(new PrintWriter("out/msr_para.train"));
		fv_test.printSVMLightFormat(new PrintWriter("out/msr_para.test"));
	}
}
