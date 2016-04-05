package baseline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import framework.NormalizedTypedDependency;
import framework.StringSimCalculator;
import utensils.Soundex;
import utensils.StopWatch;
import utensils.Util;

/**
 * 
 * @author Maks Cegielski-Johnson and Oliver Richardson
 * 
 *         Implementation of the necessary features of Method 1 of the Malakasiotis paper.
 *
 */
public class Features {

	public static Pattern NEGATE = Pattern.compile("(.*(n't)($|\\s))|([N|n]ot)");

	/**
	 * Computes the transformations of a string for the two sentences, s1 and s2.<br/>
	 * <br/>
	 * All the strings return[n][0] are transformations of s1.<br/>
	 * All the strings return[n][1] are transformations of s2.<br/>
	 * <br/>
	 * 
	 * The rows are as follows: (all the original word orders are maintained) <br/>
	 * 0. Original tokens<br/>
	 * 1. Stemmed tokens<br/>
	 * 2. POS tags<br/>
	 * 3. Soundex codes<br/>
	 * 4. Original tokens (nouns)<br/>
	 * 5. Stemmed tokens (nouns)<br/>
	 * 6. Soundex codes (nouns)<br/>
	 * 7. Original tokens (verbs)<br/>
	 * 8. Stemmed tokens (verbs)<br/>
	 * 9. Soundex codes (verbs)
	 */

	public static StopWatch tokenTime = new StopWatch(),
			stemTime = new StopWatch(),
			soundexTime = new StopWatch(),
			posTime = new StopWatch(),
			distTime = new StopWatch(),
			annoTime = new StopWatch(),
			maskTime = new StopWatch(),
			depTime = new StopWatch(),
			wnTime = new StopWatch();

	public static List<String> generateFeatures(Annotation annotation) {
		List<String> features = new ArrayList<>(10);

		// Original tokens with order maintained
		//Annotation annotation = Util.annotate(str);

		tokenTime.go();
		List<String> tokens = Util.tokenizePIPE(annotation);
		features.add(String.join(" ", tokens));
		tokenTime.stop();

		// Tokens replaced by their stems.
		stemTime.go();
		List<String> stemmed = Util.lemmaPIPE(annotation);
		features.add(String.join(" ", stemmed));
		stemTime.stop();

		// Tokens replaced by their POS tags.
		posTime.go();
//		List<String> postags = Collections.nCopies(tokens.size(), "POSTAG");//Util.tagPOS(tokens);
		List<String> postags = Util.tagPosPIPE(annotation);
		features.add(String.join(" ", postags));
		posTime.stop();

		// All the soundex codes
		soundexTime.go();
		List<String> soundex = Soundex.codeAll(tokens);
		features.add(String.join(" ", soundex));
		soundexTime.stop();

		maskTime.go();
		// Positions of verbs and nouns in both sentences
		ArrayList<Integer> nounsi = new ArrayList<Integer>();
		ArrayList<Integer> verbsi = new ArrayList<Integer>();

		// Find all nouns and verbs in s1
		for (int i = 0; i < postags.size(); i++) {
			if (postags.get(i).startsWith("NN"))
				nounsi.add(i);
			else if (postags.get(i).startsWith("VB"))
				verbsi.add(i);
		}

		// Mask off nouns
		features.add(String.join(" ", maskIndices(tokens, nounsi)));
		features.add(String.join(" ", maskIndices(stemmed, nounsi)));
		features.add(String.join(" ", maskIndices(soundex, nounsi)));

		// Mask off verbs.
		features.add(String.join(" ", maskIndices(tokens, verbsi)));
		features.add(String.join(" ", maskIndices(stemmed, verbsi)));
		features.add(String.join(" ", maskIndices(soundex, verbsi)));
		maskTime.stop();

		return features;
	}

	/**
	 * return an array of values which correspond to the values in the argument array, but where the indices kept are
	 * given in the index ArrayList.
	 */
	private static List<String> maskIndices(List<String> list, ArrayList<Integer> indices) {
		List<String> ret = new ArrayList<>(indices.size());

		for (int idx : indices)
			ret.add(list.get(idx));

		return ret;
	}

	/**
	 * For two string arrays s1 and s2, if len(s1) != len(s2), then compute all the subsets of the larger array that are
	 * the same length as the smaller array, maintaining order.<br/>
	 * All the elements in the return array are the elements of the larger array joined with a space. <br/>
	 * Returns null if the arrays are the same length. <br/>
	 * <br/>
	 * 
	 * Example:<br/>
	 * s1 = [A, B, C, D, E]<br/>
	 * s2 = [X, Y, Z]<br/>
	 * <br/>
	 * return [A B C, B C D, C D E]
	 */
	public static String[] computeSubsets(String[] s1, String[] s2) {
		String[] longer;
		String[] shorter;

		if (s1.length > s2.length) {
			longer = s1;
			shorter = s2;
		} else if (s1.length < s2.length) {
			longer = s2;
			shorter = s1;
		} else // Strings same length
			return null;

		String[] ret = new String[longer.length - shorter.length + 1];
		for (int i = 0; i < ret.length; i++) {
			String[] substring = new String[shorter.length];
			for (int j = i; j < i + shorter.length; j++) {
				substring[j - i] = longer[j];
			}
			ret[i] = String.join(" ", substring);
		}

		return ret;
	}

	/**
	 * Finds the subset of argmax(len(s1),len(s2)) that maximizes the similarity over the sum of all the metrics used in
	 * the paper.<br/>
	 * Returns null if the arrays are the same length. <br/>
	 * <br/>
	 * 
	 * Returns the subset that achieves this maximum, to be used in the feature vector.
	 */
	public static String subsetArgMax(String[] s1, String[] s2) {
		String[] subsets = computeSubsets(s1, s2);
		if (subsets == null) {
			return null;
		}
		String[] smaller = s1.length < s2.length ? s1 : s2;
		String str2 = String.join(" ", smaller);

		double max = 0;
		String argmax = "";
		for (String subset : subsets) {
			double sum = 0;
			for (Double d : StringSimCalculator.computeAll(subset, str2)) {
				sum += d;
			}
			if (sum > max) {
				max = sum;
				argmax = subset;
			}
		}
		return argmax;
	}

	/**
	 * Compute the baseline feature (Levenshtein Edit Distance) for two strings.
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static List<Double> computeBaselineFV(String s1, String s2) {
		return Arrays.asList((double) StringSimCalculator.LEV.compare(s1, s2));
	}

	/**
	 * Computes the feature vector used for Method1 of the paper. Each vector is 133 dimensions. The mode selects
	 * whether we are making vectors for "INIT", "INIT+WN" or "INIT+WN+DEP" from the paper. <br/>
	 * <br/>
	 * Mode:<br/>
	 * 1. INIT <br/>
	 * 2. INIT+WN <br/>
	 * 3. INIT+WN+DEP<br/>
	 */
	public static Counter<String> computeFullFeatureVector(String s1, String s2, int mode) {
		// Generates all the string for which we compute metrics.
		annoTime.go();
		Annotation a1 = Util.annotate(s1), a2 = Util.annotate(s2);
		annoTime.stop();

		List<String> trans1 = generateFeatures(a1), trans2 = generateFeatures(a2);
		int N = trans1.size();

		// create the return vector; the number is a guess of the required length
		ClassicCounter<String> vector = new ClassicCounter<>(134);

		distTime.go();
		// Compute the distances for each pair of transformed sentences.
		for (int i = 0; i < N; i++) {
			double[] dists = StringSimCalculator.computeAll(trans1.get(i), trans2.get(i));
			for (int j = 0; j < dists.length; j++)
				vector.incrementCount("Dist" + i + "|" + j, dists[j]);
		}

		// For the first 4 transformations, find the subset that maximizes the
		// similarity. Compute these values and store it.
		for (int i = 0; i < 4; i++) {
			double[] distances;
			String t1 = trans1.get(i), t2 = trans2.get(i);

			//TODO: determine if this should be a tokenize or just a split.
			//Pretty sure we already tokenized them.
			String[] tokens1 = t1.split(" "), tokens2 = t2.split(" ");
			// Compute the subset.
			String subset = subsetArgMax(tokens1, tokens2);

			// If they are the same length, then just compute the similarity again.
			if (subset == null)
				distances = StringSimCalculator.computeAll(t1, t2);
			else {
				String smaller = tokens1.length < tokens2.length ? t1 : t2;
				distances = StringSimCalculator.computeAll(subset, smaller);
			}

			// Also compute the average of all the similarities.
			double tot = 0;

			for (int j = 0; j < distances.length; j++) {
				tot += distances[j];
				vector.incrementCount("Sub" + i + "|" + j, distances[j]);
			}
			vector.incrementCount("Sub" + i + "~AVG", (tot / distances.length));
		}
		distTime.stop();

		// Regular expression to search for negation in a sentence.
		// looks for instances of n't and for Not.

		// Negation in S1
		vector.incrementCount("NegFirst", NEGATE.matcher(s1).find() ? 1d : 0d);
		// Negation in S2
		vector.incrementCount("NegLast", NEGATE.matcher(s2).find() ? 1d : 0d);

		// ratio = min(|S1|,|S2|)/max(|S1|,|S2|)
		double t1len = trans1.get(0).length(), t2len = trans2.get(0).length();
		vector.incrementCount("Ratio", Math.min(t1len, t2len) / Math.max(t1len, t2len));

		if (mode == 2 || mode == 3) {

		}

		if (mode == 3) {
			depTime.go();
			HashSet<NormalizedTypedDependency> dep1 = Util.getDependencySetPIPE(a1),
					dep2 = Util.getDependencySetPIPE(a2);

			double dep1size = dep1.size();
			double dep2size = dep2.size();

			dep1.addAll(dep2);
			depTime.stop();

			double common = dep1.size();

			double R1 = common / dep1size;
			double R2 = common / dep2size;
			double FR = (2 * R1 * R2) / (R1 + R2);

			vector.incrementCount("DepR1", R1);
			vector.incrementCount("DepR2", R2);
			vector.incrementCount("DepFR", FR);
		}

		wnTime.go();
		//"leacock", "lesk", "wupalmer", "resnik", "jiang", "lin", "path"
		String[] toCompute = new String[] { "lesk"};
		ArrayList<Double> wordnet = Util.lemmatizedWSPIPE(a1, a2, toCompute);

		for (int i = 0; i < wordnet.size(); i++) {
			vector.incrementCount(toCompute[i], wordnet.get(i));
		}
		wnTime.stop();

		return vector;
	}
}
