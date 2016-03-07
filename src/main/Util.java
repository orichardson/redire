package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Util {

	private static MaxentTagger tagger;
	private static Stemmer stemmer;

	public static void initialize()
	{
		tagger = new MaxentTagger("models/pos/english-bidirectional-distsim.tagger");
		stemmer = new Stemmer();
	}

	public static ArrayList<MSR> readMSRFile(String filename)
	{
		ArrayList<MSR> ret = new ArrayList<MSR>();
		try 
		{

			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			line = br.readLine();//Read line again because first line is just column headers.
			while(line != null)
			{
				String[] splt = line.split("\t");//Split on tabs, not any whitespace
				ret.add(new MSR(splt[0], splt[1], splt[2], splt[3], splt[4]));
				line = br.readLine();
			}

		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 
	 * @param sentence - The sentence to tokenize
	 * @return - String array of the tokens.
	 */
	public static String[] tokenizer(String sentence)
	{
		PTBTokenizer<CoreLabel> ptb = new PTBTokenizer<CoreLabel>(new StringReader(sentence), new CoreLabelTokenFactory(), "");
		ArrayList<String> ret = new ArrayList<String>();
		for(CoreLabel label; ptb.hasNext();)
		{
			label = (CoreLabel) ptb.next();

			ret.add(label.toString());
		}
		String[] retArr = new String[ret.size()];
		retArr =  ret.toArray(retArr);

		return retArr;
	}

	public static List<Word> createWordList(String sentence)
	{
		String[] tokens = tokenizer(sentence);
		return createWordList(tokens);
	}
	
	public static List<Word> createWordList(String[] tokens)
	{
		List<Word> words = new ArrayList<Word>();
		for(String token : tokens)
		{
			words.add(new Word(token));
		}
		return words;
	}

	public static String[] tagPOS(String sentence)
	{		
		List<TaggedWord> tagged = tagger.tagSentence(createWordList(sentence));

		return tagPOS(tagged);
	}
	
	public static String[] tagPOS(String[] tokens)
	{		
		List<TaggedWord> tagged = tagger.tagSentence(createWordList(tokens));

		return tagPOS(tagged);
	}
	
	public static String[] tagPOS(List<TaggedWord> tagged)
	{		
		String[] ret = new String[tagged.size()];

		int pos = 0;
		for(TaggedWord w : tagged)
		{
			ret[pos++] = w.tag();
		}
		return ret;
	}


	public static String[] stemSentence(String sentence)
	{
		String[] tokens = Util.tokenizer(sentence);
		return stemSentence(tokens);
	}

	public static String[] stemSentence(String[] tokens)
	{
		String[] ret = new String[tokens.length];
		int pos = 0;
		for(String token : tokens)
		{
			ret[pos++] = stemmer.stem(token);
		}
		return ret;
	}

}
