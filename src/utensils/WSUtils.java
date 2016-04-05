package utensils;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class WSUtils {
	
	
	private static ILexicalDatabase db = new NictWordNet();
	
	private static RelatednessCalculator[] WSMetric = {
		new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db), 
		new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
		};
	
	static{
		WS4JConfiguration.getInstance().setMFS(true);
	}
	
	public static void computeAllWS(String word1, String word2)
	{
		for(RelatednessCalculator rc : WSMetric)
		{
			System.out.println(rc.calcRelatednessOfWords(word1, word2));
		}
	}
	
	

}
