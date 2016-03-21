package main;

public class NormalizedTypedDependency {

	private String rel;
	private String gov;
	private String dep;
	
	public NormalizedTypedDependency(String _rel, String _gov, String _dep)
	{	
		rel = _rel;
		gov = _gov;
		dep = _dep;
	}
	
	
	public int hashCode()
	{
		return (rel + gov + dep).hashCode();
	}
	
	public boolean equals(Object other)
	{
		NormalizedTypedDependency o = (NormalizedTypedDependency) other;
		
		return this.rel.equals(o.rel) && this.gov.equals(o.gov) && this.dep.equals(o.dep);
	}
	
	public String toString()
	{
		return this.rel + "(" + this.gov + ", " + this.dep + ")";
	}
}
