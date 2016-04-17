package baseline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import utensils.Util;
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
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;

public class MaksSandbox {

	private static HashMap<String, RelatednessCalculator> WSMETRICES = new HashMap<>();
	private static ILexicalDatabase db = new NictWordNet();
	public static void main(String[] args) {
		
		WSMETRICES.put("hirst", new HirstStOnge(db));
		WSMETRICES.put("leacock", new LeacockChodorow(db));
		WSMETRICES.put("lesk", new Lesk(db));
		WSMETRICES.put("wupalmer", new WuPalmer(db));
		WSMETRICES.put("resnik", new Resnik(db));
		WSMETRICES.put("jiang", new JiangConrath(db));
		WSMETRICES.put("lin", new Lin(db));
		WSMETRICES.put("path", new Path(db));
		
		String[] names = {"hirst", "leacock", "lesk", "wupalmer", "resnik", "jiang", "lin", "path"};
		
		String text = "Tom ran to the store to buy some milk";
		String text1 = "Oliver doesn't believe in my code.";

//		Sentence s1 = new Sentence(text),
//				s2 = new Sentence(text1);
//
//		//System.out.println(s1.dependencyGraph().getFirstRoot().lemma());
//		//System.out.println(s1.lemmas());
//		s1.lemmas();
//		s2.lemmas();
		//System.out.println(s1.dependencyGraph().getFirstRoot().sentIndex());

		
		for(Pair pair : read())
		{
			Sentence s1 = new Sentence(pair.first);
			Sentence s2 = new Sentence(pair.second);
			s1.lemmas();
			s2.lemmas();
			String le1 = s1.dependencyGraph().getFirstRoot().lemma();
			String le2 = s2.dependencyGraph().getFirstRoot().lemma();
			System.out.println(le1);
			System.out.println(le2);
			
			ArrayList<Double> similarities = new ArrayList<Double>();
			
			for(String name : names)
				similarities.add(WSMETRICES.get(name).calcRelatednessOfWords(le1, le2));
			System.out.println(similarities);
			System.out.println("-----------------------------------------------");
		}
		


	}

	protected static StanfordCoreNLP pipeline;

	public static List<String> lemmatize(String documentText) {
		List<String> lemmas = new LinkedList<String>();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);

		// run all Annotators on this text
		pipeline.annotate(document);

		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the list of lemmas
				lemmas.add(token.get(LemmaAnnotation.class));
			}
		}

		return lemmas;
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

	public class Pair {
		public String first;
		public String second;

		public Pair(String _f, String _s) {
			first = _f;
			second = _s;
		}

		public String toString() {
			return first + "\n" + second;
		}
	}

}
