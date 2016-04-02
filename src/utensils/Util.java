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
import framework.MSR;
import framework.NormalizedTypedDependency;

/**
 * 
 * Provides wrappers for any NLP utility we need. Primarily using Stanford NLP files.
 *
 */
public class Util {

	private static MaxentTagger tagger;
	private static Stemmer stemmer;
	private static DependencyParser parser;

	/**
	 * Initialize any objects/models we need. This is due to the fact that a model takes a long time to initialize and
	 * if not done so, will create a bottleneck.
	 */
	public static void initialize() {
		tagger = new MaxentTagger("lib/stanford-postagger/models/english-bidirectional-distsim.tagger");
		stemmer = new Stemmer();
		parser = DependencyParser.loadFromModelFile(DependencyParser.DEFAULT_MODEL);
	}

	public static Collection<TypedDependency> getTypedDependency(String sentence)
	{
		List<TaggedWord> tagged = tagger.tagSentence(createWordList(sentence));
		GrammaticalStructure gs = parser.predict(tagged);
		Collection<TypedDependency> tds = gs.allTypedDependencies();
		
		return tds;
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
	 * God this method is awful. WHy make so many objects just to throw them away?
	 * 
	 * Takes a String sentence and tokenizes it.
	 * 
	 * @param sentence
	 *            - The sentence to tokenize
	 * @return - String array of the tokens.
	 */
	public static List<String> tokenize(String sentence) {
		PTBTokenizer<CoreLabel> ptb = new PTBTokenizer<CoreLabel>(new StringReader(sentence),
				new CoreLabelTokenFactory(), "");

		ArrayList<String> ret = new ArrayList<String>();
		for (CoreLabel label; ptb.hasNext();) {
			label = (CoreLabel) ptb.next();

			ret.add(label.toString());
		}
		return ret;
	}

	/**
	 * Creates a list of Word objects.
	 * 
	 * @param sentence
	 * @return
	 */
	public static List<Word> createWordList(String sentence) {
		return createWordList(tokenize(sentence));
	}

	/**
	 * Creates a list of Word objects.
	 * 
	 * @param tokens
	 * @return
	 */
	public static List<Word> createWordList(List<String> tokens) {
		List<Word> words = new ArrayList<Word>();
		for (String token : tokens)
			words.add(new Word(token));

		return words;
	}

	/**
	 * Returns an array of all the POS tags. The position of the POS tag matches the position of a word in the tokenized
	 * sentence array.
	 */
	public static List<String> tagPOS(String sentence) {
		List<TaggedWord> tagged = tagger.tagSentence(createWordList(sentence));

		return getTags(tagged);
	}

	/**
	 * Returns an array of all the POS tags. The position of the POS tag matches the position of a word in the tokenized
	 * sentence array.
	 */
	public static List<String> tagPOS(List<String> tokens) {
		List<TaggedWord> tagged = tagger.tagSentence(createWordList(tokens));

		return getTags(tagged);
	}

	/**
	 * Returns an array of all the POS tags. The position of the POS tag matches the position of a word in the tokenized
	 * sentence array.
	 */
	public static List<String> getTags(List<TaggedWord> tagged) {
		List<String> ret = new ArrayList<>();

		for (TaggedWord w : tagged)
			ret.add(w.tag());

		return ret;
	}

	/**
	 * Stems a sentence, returning an array of the stems. The position of the stem matches the position of a word in the
	 * tokenized sentence array.
	 */
	public static List<String> stemSentence(String sentence) {
		return stemSentence(Util.tokenize(sentence));
	}

	/**
	 * Stems a sentence, returning an array of the stems. The position of the stem matches the position of a word in the
	 * tokenized sentence array.
	 */
	public static List<String> stemSentence(List<String> tokens) {
		List<String> ret = new ArrayList<>();
		for (String token : tokens)
			ret.add(stemmer.stem(token));
		
		return ret;
	}

}
