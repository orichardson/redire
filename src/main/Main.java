package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * 
 * The main program that runs the funcionality of the paraphrase recognizer.
 * 
 * Currently implements Method1 (INIT) of the Greek recognition paper.
 *
 */
public class Main {

	public static void main(String[] args) throws IOException {
		// Initialize all the models in Util.
		Util.initialize();

		// The MSR training file and test file
		String training_file = "data/msr/msr_paraphrase_train.txt";
		String testing_file = "data/msr/msr_paraphrase_test.txt";

		// Read the training file and test file
		ArrayList<MSR> training = Util.readMSRFile(training_file);
		ArrayList<MSR> testing = Util.readMSRFile(testing_file);


		// construct the URL to the Wordnet dictionary directory
//		String path = "C:/Program Files (x86)/WordNet/2.1/dict";
//		URL url = new URL("file", null , path);
//
//		// construct the dictionary object and open it
//		IDictionary dict = new Dictionary( url);
//		dict.open();
//		
//		
//
//		IIndexWord idxWord = dict.getIndexWord("dispatch", POS.VERB);
//		IWordID wordID = idxWord.getWordIDs().get(0);//first meaning
//		IWord word = dict.getWord(wordID);
//		ISynset synset = word.getSynset();
//		
//		for(IWord w : synset.getWords())
//		{
//			System.out.println(w.getLemma());
//		}




		/*
		 * Writing the training file
		 */
				PrintWriter writer = new PrintWriter("out/msr_para.train", "UTF-8");
				int progress = 0;
				for (MSR msr : training) {
					// The label
					String output = "" + (msr.isParaphrase() ? -1 : 1);
		
					// Compute the features --- most of the work is done here
					double[] features = FeatureVectors.computeFeatureVector(msr.first(), msr.second(), 3);
		
					// Each dimension of the vector
					for (int i = 0; i < features.length; i++) {
						output += " " + (i + 1) + ":" + features[i];
					}
					writer.println(output);
					// Print the progress to make sure it's working.
					System.out.println(progress++);
				}
				writer.close();
		
				/*
				 * Writing the testing file
				 */
				progress = 0;
				writer = new PrintWriter("out/msr_para.test", "UTF-8");
				for (MSR msr : testing) {
					// The label
					String output = "" + (msr.isParaphrase() ? -1 : 1);
		
					// Compute the features --- most of the work is done here
					double[] features = FeatureVectors.computeFeatureVector(msr.first(), msr.second(), 3);
		
					// Each dimension of the vector
					for (int i = 0; i < features.length; i++) {
						output += " " + (i + 1) + ":" + features[i];
					}
					writer.println(output);
					// Print the progress to make sure it's working
					System.out.println(progress++);
				}
				writer.close();


	}

}
