package main;

import java.io.PrintWriter;
import java.util.Arrays;

import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.Counters;

public class Sandbox {

	public static void main(String[] args) {
		RVFDataset<Integer, Double> ds = new RVFDataset<Integer, Double>();

		ds.add(new RVFDatum<>(Counters.asCounter(Arrays.asList(0.1, 0.9, 0.2, 0.8)), 0));
		ds.add(new RVFDatum<>(Counters.asCounter(Arrays.asList(0.15, 0.9, 0.2, 0.8)), 1));
		ds.add(new RVFDatum<>(Counters.asCounter(Arrays.asList(0.13, 0.9, 0.2, 0.8)), 0));
		ds.add(new RVFDatum<>(Counters.asCounter(Arrays.asList(0.12, 0.9, 0.2, 0.8)), -2));
		
		System.out.println(Arrays.deepToString(ds.getDataArray()));
		System.out.println(ds.featureIndex());
		ds.printSparseFeatureMatrix();
		
		Dataset<Integer, Double> ds2 = new Dataset<>();
		ds2.add(Arrays.asList(0.1, 0.9, 0.2, 0.8), 5);
		ds2.add(Arrays.asList(0.11, 0.9, 0.2, 0.8), 6);
		ds2.add(Arrays.asList(0.12, 0.9, 0.2, 0.8), 7);
		ds2.add(Arrays.asList(0.13, 0.9, 0.2, 0.8), 8);

		System.out.println(Arrays.deepToString(ds2.getDataArray()));
		System.out.println(ds2.featureIndex());
		ds2.printSparseFeatureMatrix();
	}

}
