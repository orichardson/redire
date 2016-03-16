package main;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

/**
 * 
 * Wrapper class for Simmetrics
 *
 */
public class MyStringDistance {

	/**
	 * Computes all the metric values required for Method1 of paraphrasing.
	 * <br/>
	 * <br/>
	 * 0 - levenshtein<br/>
	 * 1 - jaroWinkler<br/>
	 * 2 - manhattan<br/>
	 * 3 - euclidean<br/>
	 * 4 - cosine<br/>
	 * 5 - ngram (3)<br/>
	 * 6 - matching/overlap <br/>
	 * 7 - dice<br/>
	 * 8 - jaccard
	 */
	public static double[] computeAll(String s1, String s2) {
		double[] ret = new double[9];

		ret[0] = levenshtein(s1, s2);
		ret[1] = jaroWinkler(s1, s2);
		ret[2] = manhattan(s1, s2);
		ret[3] = euclidean(s1, s2);
		ret[4] = cosine(s1, s2);
		ret[5] = ngram(s1, s2);
		ret[6] = matching(s1, s2);
		ret[7] = dice(s1, s2);
		ret[8] = jaccard(s1, s2);

		return ret;
	}

	public static double levenshtein(String s1, String s2) {
		StringMetric metric = StringMetrics.levenshtein();

		return metric.compare(s1, s2);

	}

	public static double jaroWinkler(String s1, String s2) {
		StringMetric metric = StringMetrics.jaroWinkler();

		return metric.compare(s1, s2);
	}

	public static double manhattan(String s1, String s2) {
		// We had to write our own Manhattan metric since Simmetrics did not
		// contain it.
		StringMetric metric = ManhattanMetric.manhattanDistance();

		return metric.compare(s1, s2);
	}

	public static double euclidean(String s1, String s2) {
		StringMetric metric = StringMetrics.euclideanDistance();

		return metric.compare(s1, s2);
	}

	public static double cosine(String s1, String s2) {
		StringMetric metric = StringMetrics.cosineSimilarity();

		return metric.compare(s1, s2);
	}

	public static double ngram(String s1, String s2) {
		StringMetric metric = StringMetrics.qGramsDistance();

		return metric.compare(s1, s2);
	}

	public static double matching(String s1, String s2) {
		StringMetric metric = StringMetrics.overlapCoefficient();

		return metric.compare(s1, s2);
	}

	public static double dice(String s1, String s2) {
		StringMetric metric = StringMetrics.dice();

		return metric.compare(s1, s2);
	}

	public static double jaccard(String s1, String s2) {
		StringMetric metric = StringMetrics.jaccard();

		return metric.compare(s1, s2);
	}
}
