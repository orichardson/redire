package main;


import static java.lang.Math.sqrt;

import org.simmetrics.MultisetDistance;
import org.simmetrics.MultisetMetric;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;


/**
 * 
 * Implements methods required required for ManhattanDistance.
 * 
 * Based off the Simmetrics Euclidean distance class.
 *
 * @param <T>
 */
public final class ManhattanDistance<T> implements MultisetMetric<T>, MultisetDistance<T> {

	@Override
	public float compare(Multiset<T> a, Multiset<T> b) {

		if (a.isEmpty() && b.isEmpty()) {
			return 1.0f;
		}

		float maxDistance = (float) Math.abs(a.size() + b.size());
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

}

