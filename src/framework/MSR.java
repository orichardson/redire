package framework;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * Wrapper class for the MSR data. Each MSR object contains all the data for one
 * example.
 *
 */
public class MSR implements ParaExample{
	private boolean isParaphrase;
	private int id1;
	private int id2;
	private String sentence1;
	private String sentence2;

	public MSR(boolean _para, int _id1, int _id2, String _sent1, String _sent2) {
		isParaphrase = _para;
		id1 = _id1;
		id2 = _id2;
		sentence1 = _sent1;
		sentence2 = _sent2;
	}

	public MSR(String _para, String _id1, String _id2, String _sent1, String _sent2) {
		this(_para.equals("1"), Integer.parseInt(_id1), Integer.parseInt(_id2), _sent1, _sent2);
	}

	public boolean isPara() {
		return isParaphrase;
	}

	public String[] getSentences() {
		return new String[] { sentence1, sentence2 };
	}

	public String first() {
		return sentence1;
	}

	public String second() {
		return sentence2;
	}

	public String toString() {
		String ret = "---------------------------------------------\n";

		ret += isParaphrase ? "PARAPHRASES" : "NOT PARAPHRASES";

		ret += "\n";

		ret += id1 + "\t" + sentence1 + "\n";
		ret += id2 + "\t" + sentence2 + "\n";
		ret += "---------------------------------------------";

		return ret;
	}

	public boolean isEqual(Object other) {
		MSR o = (MSR) other;
		return this.id1 == o.id1 && this.id2 == o.id2;
	}

	public int hashCode() {
		return (sentence1 + sentence2).hashCode();
	}
	
	public boolean containsID(int id)
	{
		return id1==id || id2 == id;
	}
	
	/**
	 * Reads all the MSR training/testing data and creates a list of MSR objects.
	 */
	public static ArrayList<MSR> read(String filename) {
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

}
