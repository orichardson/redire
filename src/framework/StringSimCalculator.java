package framework;

import org.simmetrics.StringMetric;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import static org.simmetrics.builders.StringMetricBuilder.with;
import static org.simmetrics.metrics.StringMetrics.*;
import static org.simmetrics.tokenizers.Tokenizers.whitespace;

import org.simmetrics.MultisetDistance;
import org.simmetrics.MultisetMetric;

/**
 * 
 * Wrapper class for Simmetrics
 *
 */
public class StringSimCalculator {
	/**
	 * 
	 */
	public static final StringMetric[] METRICS;
	
	/**
	 * Access for Levenshtein distance.
	 */
	public static final StringMetric LEV;

	static {
		System.out.print("Initializing String  Distances..");
		LEV = levenshtein();
		METRICS = new StringMetric[] { LEV, jaroWinkler(), Manhattan.makeStringMetric(),
				euclideanDistance(), cosineSimilarity(), qGramsDistance(), overlapCoefficient(),
				dice(), jaccard() };
		System.out.print(".. done.\n");
	}

	/**
	 * Computes all the metric values required for Method1 of paraphrasing. <br/>
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
		double[] ret = new double[METRICS.length];

		for (int i = 0; i < ret.length; i++)
			ret[i] = METRICS[i].compare(s1, s2);

		return ret;
	}

	/**
	 * 
	 * @author Maks Cegielski-Johnson & Oliver Richardson
	 *
	 * Allows for computing the Manhattan similarity between two strings. 
	 *
	 * @param <T>
	 */
	public static final class Manhattan<T> implements MultisetMetric<T>, MultisetDistance<T> {

		@Override
		public float compare(Multiset<T> a, Multiset<T> b) {

			if (a.isEmpty() && b.isEmpty()) {
				return 1.0f;
			}

			float maxDistance = (float) (a.size() + b.size());
			return 1.0f - distance(a, b) / maxDistance;
		}

		@Override
		public float distance(Multiset<T> a, Multiset<T> b) {
			float distance = 0.0f;

			for (T token : Multisets.union(a, b).elementSet()) {
				float frequencyInA = a.count(token);
				float frequencyInB = b.count(token);

				distance += Math.abs((frequencyInA - frequencyInB));
			}

			return distance;
		}

		@Override
		public String toString() {
			return "ManhattanDistance";
		}

		public static StringMetric makeStringMetric() {
			return with(new Manhattan<String>()).tokenize(whitespace()).build();
		}
	}
}
