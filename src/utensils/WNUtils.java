package utensils;

import java.io.IOException;
import java.net.URL;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class WNUtils {
	public static IDictionary DICT;

	static {
		try {
			DICT = new Dictionary(new URL("file:./lib/wordnet/dict"));
			DICT.open();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	

	public static void main(String[] args) throws IOException {
		 // look up first sense of the word "dog"
		IIndexWord idxWord = DICT.getIndexWord("dispatched", POS.VERB);
		
		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = DICT.getWord(wordID);
		System.out.println("Id = " + wordID);
		System.out.println("Lemma = " + word.getLemma());
		System.out.println("Gloss = " + word.getSynset().getGloss());
	}
}
