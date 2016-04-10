package baseline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

	public static final DecimalFormat FORMATTER = new DecimalFormat("0.0000");
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

		LOG.q("Accuracy: " + acc);
		LOG.q("Precision & Recall & F-measure");
		for (A val : testd.labelIndex()) {
			Pair<Double, Double> pr = c.evaluatePrecisionAndRecall(testd, val);
			if (prec < 0) {
				prec = pr.first;
				rec = pr.second;
			}
			LOG.q(pr.first + " & " + pr.second + " & " + ((2 * pr.first * pr.second) / (pr.first + pr.second)));
		}
		return FORMATTER.format(acc) + " & " +
				FORMATTER.format(prec) + " & " +
				FORMATTER.format(rec) + " & " +
				FORMATTER.format((2 * prec * rec / (rec + prec)));
	}

	public static <A> String runAblativeXVal(String name, GeneralDataset<A, String> traind, Set<String> toKeep,
			int crossN,
			A wrt) {
		if (toKeep != null) {
			traind = traind.sampleDataset(0, 1.0, false);
			traind.retainFeatures(toKeep);
		}

		int Q = traind.numClasses();
		int[][] sysgold = new int[Q][Q];
		int total = 0;

		//index of the label with respect to which we take the precision and recall scores.
		int wrti = traind.labelIndex.indexOf(wrt);

		RVFDataset<A, String> wrong = new RVFDataset<A, String>();
		LogisticClassifierFactory<A, String> factory = new LogisticClassifierFactory<>();

		for (int v = 0; v < crossN; v++) {
			Pair<GeneralDataset<A, String>, GeneralDataset<A, String>> fold = traind.splitOutFold(v, crossN);

			System.setErr(DEVNULL);
			LogisticClassifier<A, String> classifier = factory.trainClassifier(fold.first);
			System.setErr(NORMERR);

			for (RVFDatum<A, String> dat : fold.second) {
				A sys = classifier.classOf(dat.asFeaturesCounter());
				A gold = dat.label();

				sysgold[traind.labelIndex.indexOf(sys)][traind.labelIndex.indexOf(gold)]++;
				total++;

				if (!sys.equals(gold))
					wrong.add(dat);
			}
		}

		try {
			wrong.writeSVMLightFormat(new File("./out/WRONG_lightsvm.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// accuracy = total correct / total
		int correct = 0;
		for (int i = 0; i < Q; i++)
			correct += sysgold[i][i];
		double acc = correct / (double) total;

		// precision = correctWRT / tried WRT
		int attempted = 0;
		for (int i = 0; i < Q; i++)
			attempted += sysgold[wrti][i];
		double prec = sysgold[wrti][wrti] / (double) attempted;

		// recall = correct WRT / true WRT
		int trueW = 0;
		for (int i = 0; i < Q; i++)
			trueW += sysgold[i][wrti];
		double rec = sysgold[wrti][wrti] / (double) trueW;

		double fscore = 2 * rec * prec / (rec + prec);

		return name + " & " + FORMATTER.format(acc) + " & " +
				FORMATTER.format(prec) + " & " +
				FORMATTER.format(rec) + " & " +
				FORMATTER.format(fscore);

	}

	public static <A> LogisticClassifier<A, String> makeClassifier(String name, GeneralDataset<A, String> train) {
		LogisticClassifierFactory<A, String> factory = new LogisticClassifierFactory<>();
		System.setErr(DEVNULL);
		LogisticClassifier<A, String> classifier = factory.trainClassifier(train);
		System.setErr(NORMERR);
		RECOG.put(name, classifier);

		return classifier;
	}

	public static <A> String runAblativeTest(GeneralDataset<A, String> trainFull,
			GeneralDataset<A, String> testFull, Set<String> toKeep, String name) {

		GeneralDataset<A, String> train = trainFull, test = testFull;

		if (toKeep != null) {
			LOG.q("\nStarting ablative test " + name);
			train = trainFull.sampleDataset(0, 1.0, false);
			test = trainFull.sampleDataset(0, 1.0, false);

			train.retainFeatures(toKeep);
			test.retainFeatures(toKeep);
		} else
			LOG.q("\nRunning full classifier...");

		LogisticClassifier<A, String> classifier = makeClassifier(name, train);
		LOG.q("... Trained");
		LOG.q("Weights: " + Arrays.toString(classifier.getWeights()));

		return name + " & " + stats(classifier, test);
	}

	public static HashMap<String, Set<String>> formAblations(Index<String> featureLabels) {
		// form descriptions of all ablations
		HashMap<String, Set<String>> ablations = new LinkedHashMap<>();

		ablations.put("FULL", null);
		ablations.put("Dist + Sub + Neg + Ratio", new HashSet<String>());
		ablations.put("Sub + Neg + Ratio + Dep", new HashSet<String>());
		ablations.put("Dist + Neg", new HashSet<String>());
		ablations.put("Dist", new HashSet<String>());
		ablations.put("Sub", new HashSet<String>());

		for (String metric : StringSimCalculator.NAMES)
			ablations.put("Metric:" + metric, new HashSet<String>());

		for (String feature : featureLabels) {
			for (String key : ablations.keySet()) {
				int bar = feature.indexOf("|");
				if ((bar > 0 && key.startsWith("Metric:")
						&& key.contains(StringSimCalculator.NAMES[Integer.parseInt(feature.substring(bar + 1))]))
						|| key.contains(feature.substring(0, 3)))
					ablations.get(key).add(feature);
			}
		}

		//pad all names to same length with spaces
		int maxl = -1;
		Set<String> keys = new LinkedHashSet<>(ablations.keySet());
		for (String s : keys)
			if (s.length() > maxl)
				maxl = s.length();

		for (String s : keys) {
			ablations.put(pad(s, maxl), ablations.remove(s));
		}

		return ablations;
	}

	public static String pad(String s, int n) {
		char[] chars = new char[n];
		int len = s.length();

		for (int i = 0; i < n; i++)
			chars[i] = i < len ? s.charAt(i) : ' ';

		return new String(chars);
	}

	public static boolean LOAD = true;

	public static void main(String[] args) throws Exception {
		RVFDataset<Integer, String> fv_train, fv_test;

		if (LOAD) {
			RVFDataset<String, String> loaded = RVFDataset.readSVMLightFormat("./out/train_lightsvm.txt");
			Index<Integer> hi = new HashIndex<>(Arrays.asList(1, -1));
			Index<String> fi = HashIndex.loadFromFilename("./out/feature_index.txt");

			fv_train = new RVFDataset<Integer, String>(hi, loaded.getLabelsArray(), fi,
					loaded.getDataArray(), loaded.getValuesArray());

			RVFDataset<String, String> loaded2 = RVFDataset.readSVMLightFormat("./out/test_lightsvm.txt");
			fv_test = new RVFDataset<Integer, String>(hi, loaded2.getLabelsArray(), fi,
					loaded2.getDataArray(), loaded2.getValuesArray());

			LOG.m("loaded both files.");

		} else {
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
			fv_test.writeSVMLightFormat(new File("./out/test_lightsvm.txt"));
			LOG.m("...done");
		}

		StringBuilder results = new StringBuilder();
		Map<String, Set<String>> ablations = formAblations(fv_train.featureIndex);

		for (Entry<String, Set<String>> test : ablations.entrySet()) {
			results.append(runAblativeTest(fv_train, fv_test, test.getValue(), test.getKey()) + " \\\\\n");
			System.out.println(runAblativeXVal("XVAL" + test.getKey(), fv_train, test.getValue(), 6, 1) + " \\\\");
		}
		System.out.println("\n\n" + results);
	}
}
