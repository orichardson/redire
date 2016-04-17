package wan2006;

import java.util.List;

import edu.stanford.nlp.trees.TypedDependency;

public class Dependency
{
	String gov;
	String dep;
	
	int gov_pos;
	int dep_pos;
	
	public Dependency(String _gov, String _dep, int _gpos, int _dpos)
	{
		gov = _gov;
		dep = _dep;
		gov_pos = _gpos;
		dep_pos = _dpos;
		
	}
	
	public Dependency(TypedDependency td)
	{
		gov = td.gov().originalText();
		dep = td.dep().originalText();
		
		gov_pos = td.gov().index();
		dep_pos = td.dep().index();
	}
	
	public Dependency(TypedDependency td, List<String> lemma)
	{			
		gov_pos = td.gov().index();
		dep_pos = td.dep().index();
		if(gov_pos == 0)
			gov = "";
		else
			gov = lemma.get(gov_pos - 1);
		
		dep = lemma.get(dep_pos - 1);
	}
	
	public boolean equals(Object o)
	{
		Dependency other = (Dependency)o;
		
		return (this.gov.equals(other.gov)) && (this.dep.equals(other.dep));
	}
	
	public String toString()
	{
		return String.format("(%1$s->%2$s)", gov, dep);
	}
	
	public int hashCode()
	{
		return (gov + dep).hashCode();
	}
	
}