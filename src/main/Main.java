package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;


public class Main {




	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		//Initialize all the models in Util.
		Util.initialize();
		
		//The MSR training file
		String training_file = "data/msr/msr_paraphrase_train.txt";
		String testing_file = "data/msr/msr_paraphrase_test.txt";

		//Read the training file
		ArrayList<MSR> training = Util.readMSRFile(training_file);
		ArrayList<MSR> testing = Util.readMSRFile(testing_file);
		

		/*
		//Sample sentence to test Stanford NLP system.
		String s = "This is a sample sentence.";

		//Tokenize the sentence
		String[] tokens = Util.tokenizer(s);
//		System.out.println(Arrays.toString(tokens));

		//Get all the POS tags for the sentence
		String[] tags = Util.tagPOS(s);		
//		System.out.println(Arrays.toString(tags));
		
		//Stem the sentence
		String[] stems = Util.stemSentence(s);
//		System.out.println(Arrays.toString(stems));
		
		String[][] ret = Method1.generateTransformations("This is what one string looks like.", "Jake is going to the store to buy an apple.");
		
//		for(int i = 0; i < ret.length; i++)
//		{
//			System.out.println(ret[i][1]);
//		}
//		
//		System.out.println(Arrays.toString(MyStringDistance.computeAll(ret[0][0], ret[0][1])));
//		
//		String[] te1 = "A B C D E F G".split(" ");
//		String[] te2 = "D E F".split(" ");
//		
//		System.out.println(Arrays.toString(Method1.computeSubsets(te1, te2)));
//		System.out.println(Method1.subsetArgMax(te1, te2));
//		
		
		double[] fv = Method1.computeFeatureVector("This is what one string looks like.", "Jake is going to the store to buy an apple.");
		System.out.println(fv.length);
		System.out.println(Arrays.toString(fv));
		
		String te3 = "This sentence contains negation";
		String te4 = "This sentence does not contain negation";
		String te5 = "This isn't a good example of negation";
		Pattern negate = Pattern.compile("(.*(n't)($|\\s))|([N|n]ot)");
		if(negate.matcher(te3).find())
			System.out.println(te3);
		if(negate.matcher(te4).find())
			System.out.println(te4);
		if(negate.matcher(te5).find())
			System.out.println(te5);
		*/
		
		PrintWriter writer = new PrintWriter("msr_para.train", "UTF-8");
		int wtf = 0;
		for(MSR msr : training)
		{
			String output = "" + (msr.isParaphrase() ? -1 : 1);
			
			double[] features = Method1.computeFeatureVector(msr.first(), msr.second());
			
			for(int i = 0; i < features.length; i++)
			{
				output += " " + (i+1) + ":" + features[i];
			}
			writer.println(output);
			System.out.println(wtf++);
		}
		writer.close();
		wtf = 0;
		writer = new PrintWriter("msr_para.test", "UTF-8");
		for(MSR msr : testing)
		{
			String output = "" + (msr.isParaphrase() ? -1 : 1);
			
			double[] features = Method1.computeFeatureVector(msr.first(), msr.second());
			
			for(int i = 0; i < features.length; i++)
			{
				output += " " + (i+1) + ":" + features[i];
			}
			writer.println(output);
			System.out.println(wtf++);
		}
		writer.close();
		
	}



}
