package main;

import static org.simmetrics.builders.StringMetricBuilder.with;
import static org.simmetrics.tokenizers.Tokenizers.whitespace;

import org.simmetrics.StringMetric;

public class ManhattanMetric {

	
	public static StringMetric manhattanDistance() {
		return with(new ManhattanDistance<String>()).tokenize(whitespace())
				.build();
	}
	
}
