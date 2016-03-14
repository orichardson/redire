package main;

import java.util.ArrayList;
import java.util.Arrays;

import edu.stanford.nlp.ling.Word;

public class Main {




	public static void main(String[] args)
	{
		//Initialize all the models in Util.
		Util.initialize();
		
		//The MSR training file
		String training_file = "data/msr/msr_paraphrase_train.txt";

		//Read the training file
		ArrayList<MSR> training = Util.readMSRFile(training_file);


		//Sample sentence to test Stanford NLP system.
		String s = "This is a sample sentence.";

		//Tokenize the sentence
		String[] tokens = Util.tokenizer(s);
		System.out.println(Arrays.toString(tokens));

		//Get all the POS tags for the sentence
		String[] tags = Util.tagPOS(s);		
		System.out.println(Arrays.toString(tags));
		
		//Stem the sentence
		String[] stems = Util.stemSentence(s);
		System.out.println(Arrays.toString(stems));
		
		
	}



}
