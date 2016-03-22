package utensils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import framework.NormalizedTypedDependency;
import main.MSR;

/**
 * 
 * Provides wrappers for any NLP utility we need. Primarily using Stanford NLP
 * files.
 *
 */
public class Util {

	private static MaxentTagger tagger;
	private static Stemmer stemmer;
	private static DependencyParser parser;

	/**
	 * Initialize any objects/models we need. This is due to the fact that a
	 * model takes a long time to initialize and if not done so, will create a
	 * bottleneck.
	 */
	public static void initialize() {
		tagger = new MaxentTagger("lib/stanford-postagger/models/english-bidirectional-distsim.tagger");
		stemmer = new Stemmer();
		parser = DependencyParser.loadFromModelFile(DependencyParser.DEFAULT_MODEL);
	}

	public static HashSet<NormalizedTypedDependency> getDependencySet(String sentence) {
		List<TaggedWord> tagged = tagger.tagSentence(createWordList(sentence));
		GrammaticalStructure gs = parser.predict(tagged);
		Collection<TypedDependency> tds = gs.allTypedDependencies();
		HashSet<NormalizedTypedDependency> tdset = new HashSet<NormalizedTypedDependency>();

		for (TypedDependency td : tds) {
			NormalizedTypedDependency ntd = new NormalizedTypedDependency(td.reln().toString(), td.gov().toString(),
					td.dep().toString());
			tdset.add(ntd);
		}

		return tdset;

	}

	/**
	 * Reads all the MSR training/testing data and creates a list of MSR
	 * objects.
	 */
	public static ArrayList<MSR> readMSRFile(String filename) {
		ArrayList<MSR> ret = new ArrayList<MSR>();
		try {

			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			line = br.readLine();// Read line again because first line is just
									// column headers.
			while (line != null) {
				String[] splt = line.split("\t");// Split on tabs, not any
													// whitespace
				ret.add(new MSR(splt[0], splt[1], splt[2], splt[3], splt[4]));
				line = br.readLine();
			}

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * God this method is awful. WHy make so many objects just to throw them
	 * away?
	 * 
	 * Takes a String sentence and tokenizes it.
	 * 
	 * @param sentence
	 *            - The sentence to tokenize
	 * @return - String array of the tokens.
	 */
	public static String[] tokenizer(String sentence) {
		PTBTokenizer<CoreLabel> ptb = new PTBTokenizer<CoreLabel>(new StringReader(sentence),
				new CoreLabelTokenFactory(), "");

		ArrayList<String> ret = new ArrayList<String>();
		for (CoreLabel label; ptb.hasNext();) {
			label = (CoreLabel) ptb.next();

			ret.add(label.toString());
		}
		String[] retArr = new String[ret.size()];
		retArr = ret.toArray(retArr);

		return retArr;
	}

	/**
	 * Creates a list of Word objects.
	 * 
	 * @param sentence
	 * @return
	 */
	public static List<Word> createWordList(String sentence) {
		String[] tokens = tokenizer(sentence);
		return createWordList(tokens);
	}

	/**
	 * Creates a list of Word objects.
	 * 
	 * @param tokens
	 * @return
	 */
	public static List<Word> createWordList(String[] tokens) {
		List<Word> words = new ArrayList<Word>();
		for (String token : tokens) {
			words.add(new Word(token));
		}
		return words;
	}

	/**
	 * Returns an array of all the POS tags. The position of the POS tag matches
	 * the position of a word in the tokenized sentence array.
	 */
	public static String[] tagPOS(String sentence) {
		List<TaggedWord> tagged = tagger.tagSentence(createWordList(sentence));

		return tagPOS(tagged);
	}

	/**
	 * Returns an array of all the POS tags. The position of the POS tag matches
	 * the position of a word in the tokenized sentence array.
	 */
	public static String[] tagPOS(String[] tokens) {
		List<TaggedWord> tagged = tagger.tagSentence(createWordList(tokens));

		return tagPOS(tagged);
	}

	/**
	 * Returns an array of all the POS tags. The position of the POS tag matches
	 * the position of a word in the tokenized sentence array.
	 */
	public static String[] tagPOS(List<TaggedWord> tagged) {
		String[] ret = new String[tagged.size()];

		int pos = 0;
		for (TaggedWord w : tagged) {
			ret[pos++] = w.tag();
		}
		return ret;
	}

	/**
	 * Stems a sentence, returning an array of the stems. The position of the
	 * stem matches the position of a word in the tokenized sentence array.
	 */
	public static String[] stemSentence(String sentence) {
		String[] tokens = Util.tokenizer(sentence);
		return stemSentence(tokens);
	}

	/**
	 * Stems a sentence, returning an array of the stems. The position of the
	 * stem matches the position of a word in the tokenized sentence array.
	 */
	public static String[] stemSentence(String[] tokens) {
		String[] ret = new String[tokens.length];
		int pos = 0;
		for (String token : tokens) {
			ret[pos++] = stemmer.stem(token);
		}
		return ret;
	}

}
