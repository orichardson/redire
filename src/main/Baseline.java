package main;

import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory.LinearClassifierCreator;

public class Baseline {

	public static void main(String[] args) {
		LinearClassifier<Boolean, Double> c = new LinearClassifierCreator();
	}

}
