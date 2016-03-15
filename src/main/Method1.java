package main;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.simmetrics.StringDistance;


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
		
		String[] tokens1 = Util.tokenizer(s1);
		String[] tokens2 = Util.tokenizer(s2);
		
		//Original tokens with order maintained
		ret[0][0] = String.join(" ", tokens1);
		ret[0][1] = String.join(" ", tokens2);
		
		String[] stem1 = Util.stemSentence(tokens1);
		String[] stem2 = Util.stemSentence(tokens2);
		
		//Tokens replaced by their stems.
		ret[1][0] = String.join(" ", stem1);
		ret[1][1] = String.join(" ", stem2);

		String[] pos1 = Util.tagPOS(tokens1);
		String[] pos2 = Util.tagPOS(tokens2);

		//Tokens replaced by their POS tags.
		ret[2][0] = String.join(" ", pos1);
		ret[2][1] = String.join(" ", pos2);
		
		String[] soundex1 = Soundex.sentenceSoundex(tokens1);
		String[] soundex2 = Soundex.sentenceSoundex(tokens2);
		
		ret[3][0] = String.join(" ", soundex1);
		ret[3][1] = String.join(" ", soundex2);
		
		ArrayList<Integer> nounsindex1 = new ArrayList<Integer>();
		ArrayList<Integer> nounsindex2 = new ArrayList<Integer>();
		ArrayList<Integer> verbsindex1 = new ArrayList<Integer>();
		ArrayList<Integer> verbsindex2 = new ArrayList<Integer>();
		for(int i = 0; i < pos1.length; i++)
		{			
			if(pos1[i].matches("NN.*"))
				nounsindex1.add(i);
			else if(pos1[i].matches("VB.*"))
				verbsindex1.add(i);				
		}
		for(int i = 0; i < pos2.length; i++)
		{			
			if(pos2[i].matches("NN.*"))
				nounsindex2.add(i);
			else if(pos2[i].matches("VB.*"))
				verbsindex2.add(i);				
		}
		
		ret[4][0] = String.join(" ", maskIndices(tokens1, nounsindex1));
		ret[4][1] = String.join(" ", maskIndices(tokens2, nounsindex2));
		
		ret[5][0] = String.join(" ", maskIndices(stem1, nounsindex1));
		ret[5][1] = String.join(" ", maskIndices(stem2, nounsindex2));
		
		ret[6][0] = String.join(" ", maskIndices(soundex1, nounsindex1));
		ret[6][1] = String.join(" ", maskIndices(soundex2, nounsindex2));
		
		ret[7][0] = String.join(" ", maskIndices(tokens1, verbsindex1));
		ret[7][1] = String.join(" ", maskIndices(tokens2, verbsindex2));
		
		ret[8][0] = String.join(" ", maskIndices(stem1, verbsindex1));
		ret[8][1] = String.join(" ", maskIndices(stem2, verbsindex2));
		
		ret[9][0] = String.join(" ", maskIndices(soundex1, verbsindex1));
		ret[9][1] = String.join(" ", maskIndices(soundex2, verbsindex2));
		
		return ret;
	}
	
	private static String[] maskIndices(String[] array, ArrayList<Integer> indices)
	{
		String[] ret = new String[indices.size()];
		
		for(int i = 0; i < indices.size(); i++)
		{
			ret[i] = array[indices.get(i)];
		}
		
		return ret;
	}
	
	public static String[] computeSubsets(String[] s1, String[] s2)
	{
		String[] longer;
		String[] shorter;
		
		if(s1.length > s2.length)
		{
			longer = s1;
			shorter = s2;
		}
		else if(s1.length < s2.length)
		{
			longer = s2;
			shorter = s1;
		}
		else // Strings same length
			return null;
		
		String[] ret = new String[longer.length - shorter.length + 1];
		for(int i = 0; i < ret.length; i++)
		{
			String[] substring = new String[shorter.length];
			for(int j = i; j < i + shorter.length; j++)
			{
				substring[j-i] = longer[j];
			}
			ret[i] = String.join(" ", substring);
		}
		
		
		return ret;
	}
	
	public static String subsetArgMax(String[] s1, String[] s2)
	{
		String[] subsets = computeSubsets(s1, s2);
		if(subsets == null)
		{
			return null;
		}
		String[] smaller = s1.length < s2.length ? s1 : s2;
		String str2 = String.join(" ", smaller);
		
		double max = 0;
		String argmax = "";
		for(String subset : subsets)
		{
			double sum = 0;
			for(Double d : MyStringDistance.computeAll(subset, str2))
			{
				sum += d;
			}
			if(sum > max)
			{
				max = sum;
				argmax = subset;
			}
		}
		return argmax;
	}
	
	public static double[] computeFeatureVector(String s1, String s2)
	{
		String[][] transformations = generateTransformations(s1, s2);
		
		double[] vector = new double[133];
		
		int position = 0;
		for(String[] pair : transformations)
		{
			double[] distances = MyStringDistance.computeAll(pair[0],pair[1]);
			int j = 0;
			for(int i = position; i < position + distances.length; i++)
			{
				vector[i] = distances[j++]; 
			}
			position += distances.length;
		}
		
		for(int i = 0; i < 4; i++)
		{
			double[] distances;
			String[] tokens1 = Util.tokenizer(transformations[i][0]);
			String[] tokens2 = Util.tokenizer(transformations[i][1]);
			String subset = subsetArgMax(tokens1, tokens2);
			if(subset == null)
				distances = MyStringDistance.computeAll(transformations[i][0], transformations[i][1]);
			else
			{
				String smaller = tokens1.length < tokens2.length ? transformations[i][0] : transformations[i][1];
				distances = MyStringDistance.computeAll(subset, smaller);
			}
			int counter = 0;
			double avg = 0;
			for(int j = position; j < position + distances.length; j++)
			{
				avg += distances[counter];
				vector[j] = distances[counter++];
			}
			position += distances.length;
			vector[position++] = avg/distances.length;
			
		}
		Pattern negate = Pattern.compile("(.*(n't)($|\\s))|([N|n]ot)");
		vector[position++] = negate.matcher(s1).find() ? 1 : 0;
		vector[position++] = negate.matcher(s2).find() ? 1 : 0;
		double ratio = (1.0*Math.min(transformations[0][0].length(), transformations[0][1].length()))/(1.0*Math.max(transformations[0][0].length(), transformations[0][1].length()));
		vector[position++] = ratio;
		return vector;
	}
}
