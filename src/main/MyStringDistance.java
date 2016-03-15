package main;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

public class MyStringDistance {
	
	/**
	 * 
	 * 0 - levenshtein
	 * 1 - jaroWinkler
	 * 2 - manhattan
	 * 3 - euclidean
	 * 4 - cosine
	 * 5 - ngram (3)
	 * 6 - matching/overlap 
	 * 7 - dice
	 * 8 - jaccard
	 */
	public static double[] computeAll(String s1, String s2)
	{
		double[] ret = new double[9];
		
		ret[0] = levenshtein(s1, s2);
		ret[1] = jaroWinkler(s1, s2);
		ret[2] = manhattan(s1, s2);
		ret[3] = euclidean(s1, s2);
		ret[4] = cosine(s1,s2);
		ret[5] = ngram(s1, s2);
		ret[6] = matching(s1, s2);
		ret[7] = dice(s1, s2);
		ret[8] = jaccard(s1, s2);
		
		return ret;
	}

	
	public static double levenshtein(String s1, String s2)
	{
		StringMetric metric = StringMetrics.levenshtein();
		
		return metric.compare(s1, s2);
		
	}
	
	public static double jaroWinkler(String s1, String s2)
	{
		StringMetric metric = StringMetrics.jaroWinkler();
		
		return metric.compare(s1, s2);
	}
	
	public static double manhattan(String s1, String s2)
	{
		StringMetric metric = ManhattanMetric.manhattanDistance();
		
		return metric.compare(s1, s2);
	}
	
	public static double euclidean(String s1, String s2)
	{
		StringMetric metric = StringMetrics.euclideanDistance();
		
		return metric.compare(s1, s2);
	}
	
	public static double cosine(String s1, String s2)
	{
		StringMetric metric = StringMetrics.cosineSimilarity();
		
		return metric.compare(s1, s2);
	}
	
	public static double ngram(String s1, String s2)
	{
		StringMetric metric = StringMetrics.qGramsDistance();
		
		return metric.compare(s1, s2);
	}
	
	public static double matching(String s1, String s2)
	{
		StringMetric metric = StringMetrics.overlapCoefficient();
		
		return metric.compare(s1, s2);
	}
	
	public static double dice(String s1, String s2)
	{
		StringMetric metric = StringMetrics.dice();
		
		return metric.compare(s1, s2);
	}
	
	public static double jaccard(String s1, String s2)
	{
		StringMetric metric = StringMetrics.jaccard();
		
		return metric.compare(s1, s2);
	}
}
