package wan2006;

import java.util.Arrays;


public class Ngram
{
	String[] gram;
	
	public Ngram(int n)
	{
		gram = new String[n];
	}
	
	public Ngram(String[] _g)
	{
		gram = _g.clone();
	}
	
	public Ngram(String s1)
	{
		gram = new String[]{s1};
	}
	
	public Ngram(String s1, String s2)
	{
		gram = new String[]{s1, s2};
	}
	
	public Ngram(String s1, String s2, String s3)
	{
		gram = new String[]{s1, s2, s3};
	}
	
	public Ngram(String s1, String s2, String s3, String s4)
	{
		gram = new String[]{s1, s2, s3, s4};
	}
	
	public boolean equals(Object o)
	{
		Ngram other = (Ngram)o;
		if(gram.length != other.gram.length)
			throw new IllegalArgumentException("N-grams are different lengths");
		
		for(int i = 0; i < gram.length; i++)
		{
			if(!gram[i].equals(other.gram[i]))
			{
				return false;
			}
		}
		return true;
	}
	
	public int hashCode()
	{
		return Arrays.toString(gram).hashCode();
	}
	
	
	public String toString()
	{
		return Arrays.toString(gram);
	}
	
	
}