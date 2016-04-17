package wan2006;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utensils.Util;
import baseline.MaksSandbox;
import baseline.MaksSandbox.Pair;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.trees.TypedDependency;
import framework.MSR;

public class Testing {

	public static void main(String[] args)
	{
		
		
		int[] bad = {124,157,187,191,209,215,224,294,296,304,319,323,350,368,401,414,416,429,500,526,536,546,561,574,581,589,650,655,682,720,729,751,801,855,864,875,896,908,934,972,977,978,1014,1092,1103,1115,1141,1160,1181,1194,1251,1305,1398,1403,1423,1501,1508,1522,1527,1533,1564,1572,1637,1667,1748,1753,1770,1794,1845,1859,1862,1886,1888,1937,1939,1974,2010,2011,2014,2015,2035,2038,2068,2193,2200,2220,2223,2224,2275,2282,2313,2317,2334,2361,2417,2485,2526,2544,2563,2596,2633,2745,2762,2799,2805,2817,2856,2865,2899,2900,2909,2915,2970,2971,2986,2993,3000,3009,3022,3042,3045,3107,3125,3163,3192,3229,3247,3297,3318,3324,3332,3354,3365,3387,3402,3411,3437,3453,3477,3504,3562,3586,3601,3603,3642,3674,3709,3734,3746,3749,3762,3897,3997,4023,4044,4067,4071};
		
		ArrayList<MSR> train_msr = MSR.read("data/msr/msr_paraphrase_train.txt");

		System.out.println(train_msr.get(48));
		
		for(int i : bad)
		{
			//System.out.print(train_msr.get(i-1));
		}

//		Util.initialize();
//		for(Pair pair : read())
//		{
//			
//			Annotation a1 = Util.annotate(pair.first);
//			Annotation a2 = Util.annotate(pair.second);
//			
//			List<String> tokens1 = Util.tokenizePIPE(a1);
//			List<String> tokens2 = Util.tokenizePIPE(a2);
//			
//			Collection<TypedDependency> tds1 = Util.dependencyPIPE(a1);
//			Collection<TypedDependency> tds2 = Util.dependencyPIPE(a2);
//			
//			DepTree tree1 = Main.buildTree(tds1, tokens1);
//			DepTree tree2 = Main.buildTree(tds2, tokens2);
//			
//			System.out.println(pair.first);
//			System.out.println(pair.second);
//			System.out.println(tree1);
//			System.out.println(tree2);
//			
//			System.out.println("-------------------");
//			
//		}

	}
	private static ArrayList<Pair> read() {
		ArrayList<Pair> pairs = new ArrayList<Pair>();
		try {

			MaksSandbox mks = new MaksSandbox();
			BufferedReader br = new BufferedReader(new FileReader("./src/paraphrases.txt"));
			String line = br.readLine();
			int count = 0;
			String prev = "";
			while (line != null) {
				//				String[] splt = line.split("\t");// Split on tabs, not any

				if (count == 1) {
					pairs.add(mks.new Pair(prev, line));
				} else {
					prev = line;
				}
				count = (count + 1) % 2;

				line = br.readLine();
			}

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return pairs;
	}



}
