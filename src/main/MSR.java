package main;

public class MSR {

	
	
	private boolean isParaphrase;
	private int id1;
	private int id2;
	private String sentence1;
	private String sentence2;
	
	
	public MSR(boolean _para, int _id1, int _id2, String _sent1, String _sent2)
	{
		isParaphrase = _para;
		id1 = _id1;
		id2 = _id2;
		sentence1 = _sent1;
		sentence2 = _sent2;
	}
	
	public MSR(String _para, String _id1, String _id2, String _sent1, String _sent2)
	{
		this(_para.equals("1"), Integer.parseInt(_id1), Integer.parseInt(_id2), _sent1, _sent2);
	}
	
	
	public boolean isParaphrase()
	{
		return isParaphrase;
	}
	
	public String[] getSentences()
	{
		return new String[] {sentence1, sentence2};
	}
	
	public String first()
	{
		return sentence1;
	}
	
	public String second()
	{
		return sentence2;
	}
	
	public String toString()
	{
		String ret = "---------------------------------------------\n";
		
		ret += isParaphrase ? "PARAPHRASES" : "NOT PARAPHRASES";
		
		ret += "\n";
		
		ret += id1 + "\t" + sentence1 + "\n";
		ret += id2 + "\t" + sentence2 + "\n";
		ret += "---------------------------------------------";
		
		return ret;
	}
	
	public boolean isEqual(Object other)
	{
		MSR o = (MSR)other;
		return this.id1 == o.id1 && this.id2 == o.id2;
	}
	
	public int hashCode()
	{
		return (sentence1 + sentence2).hashCode();
	}
}
