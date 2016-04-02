package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import utensils.Util;
import edu.stanford.nlp.trees.TypedDependency;
import framework.MSR;


public class MaksSandbox {

	
	public static void main(String[] args)
	{
		ArrayList<Pair> pairs = read();
		
		
		
		Util.initialize(); 
//		String s1 = "Google bought YouTube";
//		String s2 = "YouTube was sold to Google";
//		Collection<TypedDependency> td1 = Util.getTypedDependency(s1);
//		Collection<TypedDependency> td2 = Util.getTypedDependency(s2);
		
		for(Pair pair : pairs)
		{
			System.out.println("===============================================================================================");
			System.out.println(pair.first);
			System.out.println(Util.getTypedDependency(pair.first));
			System.out.println("-----------------------------------------------------------------------------------------------");
			System.out.println(pair.second);
			System.out.println(Util.getTypedDependency(pair.second));
		}
		
		
	}
	
	private static ArrayList<Pair> read()
	{
		ArrayList<Pair> pairs = new ArrayList<Pair>();
		try {

			MaksSandbox mks = new MaksSandbox();
			BufferedReader br = new BufferedReader(new FileReader("./src/paraphrases.txt"));
			String line = br.readLine();
			int count = 0;
			String prev = "";
			while (line != null) {
//				String[] splt = line.split("\t");// Split on tabs, not any
				
				if(count == 1)
				{
					pairs.add(mks.new Pair(prev, line));
				}
				else
				{
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

	public class Pair
	{
		String first;
		String second;
		public Pair(String _f, String _s)
		{
			first = _f;
			second = _s;
		}
		
		public String toString()
		{
			return first + "\n" + second;
		}
	}

	
}

