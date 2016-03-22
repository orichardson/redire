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
 *         Implementation of the necessary features of Method 1 of the
 *         Malakasiotis paper.
 *
 */
public class FeatureVectors {

	/**
	 * Computes the transformations of a string for the two sentences, s1 and
	 * s2.<br/>
	 * <br/>
	 * All the strings return[n][0] are transformations of s1.<br/>
	 * All the strings return[n][1] are transformations of s2.<br/>
	 * <br/>
	 * 
	 * The rows are as follows: (all the original word orders are maintained)
	 * <br/>
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
	
	public static String[][] generateTransformations(String s1, String s2) {
		String[][] ret = new String[10][2];

		String[] tokens1 = Util.tokenizer(s1);
		String[] tokens2 = Util.tokenizer(s2);

		// Original tokens with order maintained
		ret[0][0] = String.join(" ", tokens1);
		ret[0][1] = String.join(" ", tokens2);

		String[] stem1 = Util.stemSentence(tokens1);
		String[] stem2 = Util.stemSentence(tokens2);

		// Tokens replaced by their stems.
		ret[1][0] = String.join(" ", stem1);
		ret[1][1] = String.join(" ", stem2);

		String[] pos1 = Util.tagPOS(tokens1);
		String[] pos2 = Util.tagPOS(tokens2);

		// Tokens replaced by their POS tags.
		ret[2][0] = String.join(" ", pos1);
		ret[2][1] = String.join(" ", pos2);

		// All the soundex codes
		String[] soundex1 = Soundex.sentenceSoundex(tokens1);
		String[] soundex2 = Soundex.sentenceSoundex(tokens2);

		ret[3][0] = String.join(" ", soundex1);
		ret[3][1] = String.join(" ", soundex2);

		// Positions of verbs and nouns in both sentences
		ArrayList<Integer> nounsindex1 = new ArrayList<Integer>();
		ArrayList<Integer> nounsindex2 = new ArrayList<Integer>();
		ArrayList<Integer> verbsindex1 = new ArrayList<Integer>();
		ArrayList<Integer> verbsindex2 = new ArrayList<Integer>();

		// Find all nouns and verbs in s1
		for (int i = 0; i < pos1.length; i++) {
			if (pos1[i].matches("NN.*"))
				nounsindex1.add(i);
			else if (pos1[i].matches("VB.*"))
				verbsindex1.add(i);
		}

		// Find all nouns and verbs in s2
		for (int i = 0; i < pos2.length; i++) {
			if (pos2[i].matches("NN.*"))
				nounsindex2.add(i);
			else if (pos2[i].matches("VB.*"))
				verbsindex2.add(i);
		}
		// All the tokens which are nouns
		ret[4][0] = String.join(" ", maskIndices(tokens1, nounsindex1));
		ret[4][1] = String.join(" ", maskIndices(tokens2, nounsindex2));

		// All the stems which are nouns
		ret[5][0] = String.join(" ", maskIndices(stem1, nounsindex1));
		ret[5][1] = String.join(" ", maskIndices(stem2, nounsindex2));

		// All the soundex codes which are nouns
		ret[6][0] = String.join(" ", maskIndices(soundex1, nounsindex1));
		ret[6][1] = String.join(" ", maskIndices(soundex2, nounsindex2));

		// All the tokens which are verbs.
		ret[7][0] = String.join(" ", maskIndices(tokens1, verbsindex1));
		ret[7][1] = String.join(" ", maskIndices(tokens2, verbsindex2));

		// All the stems which are verbs.
		ret[8][0] = String.join(" ", maskIndices(stem1, verbsindex1));
		ret[8][1] = String.join(" ", maskIndices(stem2, verbsindex2));

		// All the soundex codes which are verbs.
		ret[9][0] = String.join(" ", maskIndices(soundex1, verbsindex1));
		ret[9][1] = String.join(" ", maskIndices(soundex2, verbsindex2));

		return ret;
	}

	/**
	 * return an array of values which correspond to the values in the argument
	 * array, but where the indices kept are given in the index ArrayList.
	 */
	private static String[] maskIndices(String[] array, ArrayList<Integer> indices) {
		String[] ret = new String[indices.size()];

		for (int i = 0; i < indices.size(); i++) {
			ret[i] = array[indices.get(i)];
		}

		return ret;
	}

	/**
	 * For two string arrays s1 and s2, if len(s1) != len(s2), then compute all
	 * the subsets of the larger array that are the same length as the smaller
	 * array, maintaining order.<br/>
	 * All the elements in the return array are the elements of the larger array
	 * joined with a space. <br/>
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
	 * Finds the subset of argmax(len(s1),len(s2)) that maximizes the similarity
	 * over the sum of all the metrics used in the paper.<br/>
	 * Returns null if the arrays are the same length. <br/>
	 * <br/>
	 * 
	 * Returns the subset that achieves this maximum, to be used in the feature
	 * vector.
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
			for (Double d : StringDistanceCalculator.computeAll(subset, str2)) {
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
	 * Computes the feature vector used for Method1 of the paper. Each vector is
	 * 133 dimensions. The mode selects whether we are making vectors for
	 * "INIT", "INIT+WN" or "INIT+WN+DEP" from the paper. <br/>
	 * <br/>
	 * Mode:<br/>
	 * 1. INIT <br/>
	 * 2. INIT+WN <br/>
	 * 3. INIT+WN+DEP<br/>
	 */
	public static List<Double> computeFeatureVector(String s1, String s2, int mode) {
		// Generates all the string for which we compute metrics.
		String[][] transformations = generateTransformations(s1, s2);

		// create the return vector; the number is a guess of the required length
		ArrayList<Double> vector = new ArrayList<Double>(133);

		// Compute the distances for each pair of transformed sentences.
		for (String[] pair : transformations) {
			for( double d : StringDistanceCalculator.computeAll(pair[0], pair[1]))
				vector.add(d);
		}

		// For the first 4 transformations, find the subset that maximizes the
		// similarity. Compute these values and store it.
		for (int i = 0; i < 4; i++) {
			double[] distances;
			String trans1 = transformations[i][0];
			String trans2 = transformations[i][1];
			String[] tokens1 = Util.tokenizer(trans1);
			String[] tokens2 = Util.tokenizer(trans2);
			// Compute the subset.
			String subset = subsetArgMax(tokens1, tokens2);
			// Compute the similarity.
			if (subset == null)
				// If they are the same length, then just compute the similarity
				// again.
				distances = StringDistanceCalculator.computeAll(trans1, trans2);
			else {
				String smaller = tokens1.length < tokens2.length ? trans1 : trans2;
				distances = StringDistanceCalculator.computeAll(subset, smaller);
			}
			// Counter is to read from the correct position.
			int counter = 0;
			// Also compute the average of all the similarities.
			double avg = 0;
			
			for( double d : distances ) {
				avg += d;
				vector.add(d);
			}
			vector.add(avg / distances.length);
		}
		// Regular expression to search for negation in a sentence.
		Pattern negate = Pattern.compile("(.*(n't)($|\\s))|([N|n]ot)");
		// looks for instances of n't and for Not.
		
		
		// Negation in S1
		vector.add(negate.matcher(s1).find() ? 1d : 0d);
		// Negation in S2
		vector.add(negate.matcher(s2).find() ? 1d : 0d);

		// ratio = min(|S1|,|S2|)/max(|S1|,|S2|)
		double ratio = (1.0 * Math.min(transformations[0][0].length(), transformations[0][1].length()))
				/ (1.0 * Math.max(transformations[0][0].length(), transformations[0][1].length()));
		vector.add(ratio);

		if (mode == 2 || mode == 3) {

		}

		if (mode == 3) {
			HashSet<NormalizedTypedDependency> dep1 = Util.getDependencySet(s1);
			HashSet<NormalizedTypedDependency> dep2 = Util.getDependencySet(s2);

			double dep1size = dep1.size();
			double dep2size = dep2.size();

			dep1.addAll(dep2);

			double common = dep1.size();

			double R1 = common / dep1size;
			double R2 = common / dep2size;
			double FR = (2 * R1 * R2) / (R1 + R2);
			
			Collections.addAll(vector,R1,R2,FR);
		}
		
		return vector;
	}
}
