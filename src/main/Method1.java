package main;


/**
 * 
 * @author Maks Cegielski-Johnson and Oliver Richardson
 * 
 * Implementation of the necessary features of Method 1 of the Malakasiotis paper.
 *
 */
public class Method1 
{
	
	/*
	 * Tools Necessary:
	 * Tokenizer
	 * POS Tagger
	 */

	
	/*
	 * Similarity Measures (measured in terms of tokens, not characters)
	 */	
	
	//Levenshtein (edit distance in terms of tokens)	
	
	//Jaro-Winkler 	
	
	//Manhattan
	//n is number of distinct words in s1 and s2
	//xi and yi show how many times each one of these distinct words occurs in s1 and s2 respectively
	
	//Euclidean
	
	//cosine similarity
	
	//n-gram distance (n = 3)
	
	//matching coefficient
	
	//Dice coefficient
	
	//Jaccard coefficient
	
	
	/*
	 * String Transformations
	 */
	public static String[][] generateTransformations(String s1, String s2)
	{
		String[][] ret = new String[10][2];
		
		String[] t1 = Util.tokenizer(s1);
		String[] t2 = Util.tokenizer(s2);
		
		//Original tokens with order maintained
		ret[0][0] = String.join(" ", t1);
		ret[0][1] = String.join(" ", t2);
		
		String[] stem1 = Util.stemSentence(t1);
		String[] stem2 = Util.stemSentence(t2);
		
		//Tokens replaced by their stems.
		ret[1][0] = String.join(" ", stem1);
		ret[1][1] = String.join(" ", stem2);

		String[] pos1 = Util.tagPOS(t1);
		String[] pos2 = Util.tagPOS(t2);

		//Tokens replaced by their POS tags.
		ret[2][0] = String.join(" ", pos1);
		ret[2][1] = String.join(" ", pos2);
		
		//TODO Soundex
		
		//TODO Nouns
		
		//TODO Verbs
		
		return ret;
	}
	
	
	
	//'' Tokens are replaced by their stems
	
	//'' Tokens are replaced by their POS tags
	
	//'' Tokens are replaced by their soundex codes
	
	//'' consiting of only the nouns identified from POS tag, original order maintained
	
	//nouns replaced by their stems
	
	//nouns replaced by their soundex codes
	
	// '' consisting of only verbs identified from POS tag, order maintained
	
	//verbs replaced by their stems
	
	//verbs replaced by heir soundex codes
	
	/*
	 * Length problem
	 */
}
