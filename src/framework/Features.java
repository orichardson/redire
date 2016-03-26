package framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import utensils.Soundex;
import utensils.Util;

/**
 * 
 * @author Maks Cegielski-Johnson and Oliver Richardson
 * 
 *         Implementation of the necessary features of Method 1 of the Malakasiotis paper.
 *
 */
public class Features {

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

	public static List<String> generateFeatures(String str) {
		List<String> features = new ArrayList<>(10);

		// Original tokens with order maintained
		List<String> tokens = Util.tokenize(str);
		features.add(String.join(" ", tokens));

		// Tokens replaced by their stems.
		List<String> stemmed = Util.stemSentence(tokens);
		features.add(String.join(" ", stemmed));

		// Tokens replaced by their POS tags.
		List<String> postags = Util.tagPOS(tokens);
		features.add(String.join(" ", postags));

		// All the soundex codes
		List<String> soundex = Soundex.codeAll(tokens);
		features.add(String.join(" ", soundex));

		// Positions of verbs and nouns in both sentences
		ArrayList<Integer> nounsi = new ArrayList<Integer>();
		ArrayList<Integer> verbsi = new ArrayList<Integer>();

		// Find all nouns and verbs in s1
		for (int i = 0; i < postags.size(); i++) {
			if (postags.get(i).matches("NN.*"))
				nounsi.add(i);
			else if (postags.get(i).matches("VB.*"))
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

	public static List<Double> computeBaselineFV(String s1, String s2) {
		return Collections.nCopies(1, (double) StringSimCalculator.LEV.compare(s1, s2));
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
	public static List<Double> computeFullFeatureVector(String s1, String s2, int mode) {
		// Generates all the string for which we compute metrics.
		List<String> trans1 = generateFeatures(s1), trans2 = generateFeatures(s2);
		int N = trans1.size();

		// create the return vector; the number is a guess of the required length
		ArrayList<Double> vector = new ArrayList<Double>(133);

		// Compute the distances for each pair of transformed sentences.
		for (int i = 0; i < N; i++)
			for (double d : StringSimCalculator.computeAll(trans1.get(i), trans2.get(i)))
				vector.add(d);

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

			for (double d : distances) {
				tot += d;
				vector.add(d);
			}
			vector.add(tot / distances.length);
		}
		// Regular expression to search for negation in a sentence.
		Pattern negate = Pattern.compile("(.*(n't)($|\\s))|([N|n]ot)");
		// looks for instances of n't and for Not.

		// Negation in S1
		vector.add(negate.matcher(s1).find() ? 1d : 0d);
		// Negation in S2
		vector.add(negate.matcher(s2).find() ? 1d : 0d);

		// ratio = min(|S1|,|S2|)/max(|S1|,|S2|)
		double t1len = trans1.get(0).length(), t2len = trans2.get(0).length();
		vector.add(Math.min(t1len, t2len) / Math.max(t1len, t2len));

		if (mode == 2 || mode == 3) {

		}

		if (mode == 3) {
			HashSet<NormalizedTypedDependency> dep1 = Util.getDependencySet(s1),
					dep2 = Util.getDependencySet(s2);

			double dep1size = dep1.size();
			double dep2size = dep2.size();

			dep1.addAll(dep2);

			double common = dep1.size();

			double R1 = common / dep1size;
			double R2 = common / dep2size;
			double FR = (2 * R1 * R2) / (R1 + R2);

			Collections.addAll(vector, R1, R2, FR);
		}

		return vector;
	}
}
