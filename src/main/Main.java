package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 
 * The main program that runs the funcionality of the paraphrase recognizer.
 * 
 * Currently implements Method1 (INIT) of the Greek recognition paper. 
 *
 */
public class Main {


	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		//Initialize all the models in Util.
		Util.initialize();
		
		//The MSR training file and test file
		String training_file = "data/msr/msr_paraphrase_train.txt";
		String testing_file = "data/msr/msr_paraphrase_test.txt";

		//Read the training file and test file
		ArrayList<MSR> training = Util.readMSRFile(training_file);
		ArrayList<MSR> testing = Util.readMSRFile(testing_file);
		
		/*
		 * Writing the training file
		 */
		PrintWriter writer = new PrintWriter("msr_para.train", "UTF-8");
		int progress = 0;
		for(MSR msr : training)
		{
			//The label
			String output = "" + (msr.isParaphrase() ? -1 : 1);
			
			//Compute the features --- most of the work is done here
			double[] features = Method1.computeFeatureVector(msr.first(), msr.second());
			
			//Each dimension of the vector
			for(int i = 0; i < features.length; i++)
			{
				output += " " + (i+1) + ":" + features[i];
			}
			writer.println(output);
			//Print the progress to make sure it's working.
			System.out.println(progress++);
		}
		writer.close();
		
		/*
		 * Writing the testing file
		 */
		progress = 0;
		writer = new PrintWriter("msr_para.test", "UTF-8");
		for(MSR msr : testing)
		{
			//The label
			String output = "" + (msr.isParaphrase() ? -1 : 1);
			
			//Compute the features --- most of the work is done here
			double[] features = Method1.computeFeatureVector(msr.first(), msr.second());
			
			//Each dimension of the vector
			for(int i = 0; i < features.length; i++)
			{
				output += " " + (i+1) + ":" + features[i];
			}
			writer.println(output);
			//Print the progress to make sure it's working
			System.out.println(progress++);
		}
		writer.close();
		
	}

}
