package baseline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import utensils.Util;

public class MaksSandbox {

	public static void main(String[] args) {
		ArrayList<Pair> pairs = read();

		String word1 = "driving";

		String word2 = "driver";

		String text = "Tom ran to the store to buy some milk";
		String text1 = "Oliver doesn't believe in my code.";

		Sentence s1 = new Sentence(text),
				s2 = new Sentence(text1);

		System.out.println(Util.lemmae(s1));
		System.out.println(Util.lemmae(s2));

		Util.lemmatizedWS(new Sentence(text), new Sentence(text1));

//		WSUtils.computeAllWS(word1, word2);

		//Util.initialize(); 
		//		String s1 = "Google bought YouTube";
		//		String s2 = "YouTube was sold to Google";
		//		Collection<TypedDependency> td1 = Util.getTypedDependency(s1);
		//		Collection<TypedDependency> td2 = Util.getTypedDependency(s2);

		//		for(Pair pair : pairs)
		//		{
		//			System.out.println("===============================================================================================");
		//			System.out.println(pair.first);
		//			System.out.println(Util.getTypedDependency(pair.first));
		//			System.out.println("-----------------------------------------------------------------------------------------------");
		//			System.out.println(pair.second);
		//			System.out.println(Util.getTypedDependency(pair.second));
		//		}

//		Properties props;
//		props = new Properties();
//		props.put("annotators", "tokenize, ssplit, pos, lemma");
//
//		// StanfordCoreNLP loads a lot of models, so you probably
//		// only want to do this once per execution
//		pipeline = new StanfordCoreNLP(props);
//		List<String> lem = lemmatize("I am running to the park.");
//
//		System.out.println(lem);

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
		String first;
		String second;

		public Pair(String _f, String _s) {
			first = _f;
			second = _s;
		}

		public String toString() {
			return first + "\n" + second;
		}
	}

}
