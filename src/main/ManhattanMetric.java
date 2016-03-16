package main;

import static org.simmetrics.builders.StringMetricBuilder.with;
import static org.simmetrics.tokenizers.Tokenizers.whitespace;

import org.simmetrics.StringMetric;

/**
 * 
 * Takes two strings and turns them into multisets, them computes the Manhattan distance.
 *
 */
public class ManhattanMetric {

	/**
	 * Takes two strings and turns them into multisets, them computes the Manhattan distance.
	 * @return
	 */
	public static StringMetric manhattanDistance() {
		return with(new ManhattanDistance<String>()).tokenize(whitespace())
				.build();
	}
	
}
