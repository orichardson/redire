package baseline;

import java.util.Scanner;

import edu.stanford.nlp.classify.LogisticClassifier;
import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.stats.Counter;
import framework.MSR;
import utensils.LOG;
import utensils.Util;

public class Console {

	public static void main(String[] args) {
		//Util.initialize();

		String training_file = "data/msr/msr_paraphrase_train.txt";

		RVFDataset<Integer, String> fv_train = Main.makeFeatureData(MSR.read(training_file), 3);
		LogisticClassifier<Integer, String> allah = Main.makeClassifier("FULL", fv_train);

		LOG.m("...done");

		Scanner scan = new Scanner(System.in);
		while (true) {
			System.out.println("First sentence: ");
			String r1 = scan.nextLine();
			if (r1.isEmpty() || r1.equalsIgnoreCase("quit"))
				break;

			System.out.println("Second sentence: ");
			String r2 = scan.nextLine();
			if (r2.isEmpty() || r2.equalsIgnoreCase("quit"))
				break;

			Counter<String> data = Features.computeFullFeatureVector(r1, r2, Main.MODE);
			int n = allah.classOf(data);
			double score = allah.scoreOf(data);

			if (n > 0)
				System.out.println(n + "\t" + score + "\tPARAPHASES!");
			else
				System.err.println(n + "\t" + score + "\tNOT A PARAPHRASE!");

		}
		scan.close();

	}

}
