package main;

import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.classify.RVFDataset;
import framework.MSR;
import utensils.LOG;
import utensils.Util;

public class Console {
	
	
	public static void main(String[] args)
	{
		Util.initialize();
		
		String training_file = "data/msr/msr_paraphrase_train.txt";
		String testing_file = "data/msr/msr_paraphrase_test.txt";

		List<MSR> trainMSR = MSR.read(training_file), testMSR = MSR.read(testing_file);
		LOG.m("Data Loaded.");

		// actual classifier
		LOG.m("Making Full Feature Vectors...");
		//System.setErr(DEVNULL);
		RVFDataset<Integer, String> fv_train = Main.makeFeatureData(trainMSR, 3),
				fv_test = Main.makeFeatureData(testMSR, 3);
		//System.setErr(NORMERR);
		LOG.m("...done");
		
		Scanner s = new Scanner(System.in);
		
		
		String read = s.next();
		
		while(!read.equals("quit"))
		{
			
			
			
			read = s.next();
		}
		
		
		
	}

}
