package baseline;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LogisticClassifier;
import edu.stanford.nlp.classify.LogisticClassifierFactory;
import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import edu.stanford.nlp.util.Pair;
import framework.MSR;
import framework.ParaExample;
import framework.StringSimCalculator;
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

	// to suppress classifier output.
	public static final PrintStream NORMERR = System.out;
	public static final PrintStream DEVNULL = new PrintStream(new OutputStream() {
		public void write(int b) {}
	});

	public static HashMap<String, Classifier<?, String>> RECOG = new HashMap<>();

	/**
	 * Creates a dataset for machine learning.
	 * 
	 * @param examples
	 * @param mode
	 * @return
	 */
	public static RVFDataset<Integer, String> makeFeatureData(Collection<? extends ParaExample> examples, int mode) {

		RVFDataset<Integer, String> ds = new RVFDataset<>();
		int count = 0;
		for (ParaExample ex : examples) {
			ds.add(new RVFDatum<>(Features.computeFullFeatureVector(ex.first(), ex.second(), mode),
					ex.isPara() ? 1 : -1));

			if (count++ % 10 == 0) {
				System.out.print("\n" + count + "\tToken: " + Features.tokenTime.checkSF());
				System.out.print("\tAnnot: " + Features.annoTime.checkSF());
				System.out.print("\twordnet: " + Features.wnTime.checkSF());
				System.out.print("\tSoundex: " + Features.soundexTime.checkSF());
				System.out.print("\n\tdist: " + Features.distTime.checkSF());
				System.out.print("\tPOS: " + Features.posTime.checkSF());
				System.out.print("\tDEP: " + Features.depTime.checkSF());
				System.out.print("\tmask: " + Features.maskTime.checkSF());
				System.out.print("\tstem: " + Features.stemTime.checkSF());
			}
		}
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
		counter.incrementCount("BIAS");

		for (int i = 0; i < list.size(); i++)
			counter.incrementCount("F" + i, list.get(i));

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
	public static <A, B> String stats(Classifier<A, B> c, GeneralDataset<A, B> testd) {
		double acc = c.evaluateAccuracy(testd);
		double prec = -1, rec = -1;

		LOG.m("Accuracy: " + acc);
		LOG.m("Precision & Recall & F-measure");
		for (A val : testd.labelIndex()) {
			Pair<Double, Double> pr = c.evaluatePrecisionAndRecall(testd, val);
			if (prec < 0) {
				prec = pr.first;
				rec = pr.second;
			}
			LOG.m(pr.first + " & " + pr.second + " & " + ((2 * pr.first * pr.second) / (pr.first + pr.second)));
		}
		return acc + " & " + prec + " & " + rec + " & " + (2 * prec * rec / (rec + prec));
	}

	public static <A> LogisticClassifier<A, String> makeClassifier(String name, GeneralDataset<A, String> train) {
		LogisticClassifierFactory<A, String> factory = new LogisticClassifierFactory<>();
		//System.setErr(DEVNULL);
		LogisticClassifier<A, String> classifier = factory.trainClassifier(train);
		//System.setErr(NORMERR);
		RECOG.put(name, classifier);

		return classifier;
	}

	public static <A> String runAblativeTest(GeneralDataset<A, String> trainFull,
			GeneralDataset<A, String> testFull, String spec, String name) {

		System.out.println();
		GeneralDataset<A, String> train = trainFull, test = testFull;

		if (spec != null) {
			LOG.m("Starting ablative test " + spec);
			train = trainFull.sampleDataset(0, 1.0, false);
			test = trainFull.sampleDataset(0, 1.0, false);

			Set<String> toKeep = new HashSet<>(Arrays.asList(spec.split(", ")));
			train.retainFeatures(toKeep);
			test.retainFeatures(toKeep);
		} else
			LOG.m("Running full classifier...");

		LogisticClassifier<A, String> classifier = makeClassifier(name, train);
		LOG.m("... Trained");
		LOG.m("Weights: " + Arrays.toString(classifier.getWeights()));

		return name + " & " + stats(classifier, test);
	}

	public static HashMap<String, String> formAblations(Index<String> featureLabels) {
		// form descriptions of all ablations
		HashMap<String, String> ablations = new LinkedHashMap<>();

		ablations.put("Dist + Sub + Neg + Ratio + Dep", null);
		ablations.put("Dist + Sub + Neg + Ratio", "");
		ablations.put("Sub + Neg + Ratio + Dep", "");
		ablations.put("Dist + Neg", "");
		ablations.put("Dist", "");
		ablations.put("Sub", "");

		for (String metric : StringSimCalculator.NAMES)
			ablations.put("Metric:" + metric, "");

		for (String feature : featureLabels) {
			for (String key : ablations.keySet()) {
				int bar = feature.indexOf("|");
				if ((bar > 0 && key.startsWith("Metric:")
						&& key.contains(StringSimCalculator.NAMES[Integer.parseInt(feature.substring(bar + 1))]))
						|| key.contains(feature.substring(0, 3)))
					ablations.put(key, ablations.get(key) + ", " + feature);
			}
		}

		return ablations;

	}

	public static boolean LOAD = true;

	public static void main(String[] args) throws Exception {
		RVFDataset<Integer, String> fv_train, fv_test;

		if (LOAD) {
//			fv_train = new RVFDataset<>(); fv_test = new RVFDataset<>();
//			fv_train.readSVMLightFormat(new File("./out/train_lightsvm.txt"));
//			fv_train.readSVMLightFormat(new File("./out/test_lightsvm.txt"));

			RVFDataset<String, String> loaded = RVFDataset.readSVMLightFormat("./out/train_lightsvm.txt");
			Index<Integer> hi = new HashIndex<>(Arrays.asList(1, -1));
			Index<String> fi = HashIndex.loadFromFilename("./out/feature_index.txt");

			fv_train = new RVFDataset<Integer, String>(hi, loaded.getLabelsArray(), fi,
					loaded.getDataArray(), loaded.getValuesArray());

			loaded = RVFDataset.readSVMLightFormat("./out/test_lightsvm.txt");
			fv_test = new RVFDataset<Integer, String>(hi, loaded.getLabelsArray(), fi,
					loaded.getDataArray(), loaded.getValuesArray());

			LOG.m("loaded both files.");

		} else {
			//Util.initialize();
			Util.wordnet();

			// The MSR training file and test file	
			String training_file = "data/msr/msr_paraphrase_train.txt";
			String testing_file = "data/msr/msr_paraphrase_test.txt";

			List<MSR> trainMSR = MSR.read(training_file), testMSR = MSR.read(testing_file);
			LOG.m("Data Loaded.");
			Util.initialize();
			// actual classifier
			LOG.m("Making Full Feature Vectors...");

//		System.setErr(DEVNULL);
			fv_train = makeFeatureData(trainMSR, MODE);
			fv_test = makeFeatureData(testMSR, MODE);
//		System.setErr(NORMERR);
			LOG.m("writing vectors to files...");

			fv_train.writeSVMLightFormat(new File("./out/train_lightsvm.txt"));
			fv_train.featureIndex().saveToFilename("./out/feature_index.txt");
			fv_train.labelIndex().saveToFilename("./out/label_index.txt");
			fv_train.writeSVMLightFormat(new File("./out/test_lightsvm.txt"));
			LOG.m("...done");
		}
		
		Set<String> s = new HashSet<String>(fv_train.featureIndex.objectsList());
		System.out.println(s);
		System.out.println(s.remove("lesk"));
		System.out.println(s.remove("Dist0|0"));
		fv_train.retainFeatures(s);
		
		System.out.println(fv_train.numFeatures());

		StringBuilder results = new StringBuilder();

		for (Entry<String, String> test : formAblations(fv_train.featureIndex).entrySet())
			results.append(runAblativeTest(fv_train, fv_test, test.getValue(), test.getKey()) + "\\\\\n");


		System.out.println("\n\n" + results);
	}
}