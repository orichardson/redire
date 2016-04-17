package wan2006;

import java.util.ArrayList;

public class DepTree {

	
	
	ArrayList<Node> nodes;
	Node root;
	
	
	public DepTree()
	{
		nodes = new ArrayList<Node>();
		root = null;
	}
	
	public void addNodes(ArrayList<Node> ns)
	{
		nodes = ns;
	}
	
	/**
	 * Assume that parent and child will be off by one.
	 */
	public void link(int parent, int child)
	{
		nodes.get(parent-1).addChild(nodes.get(child-1));
	}
	
	public void link(Dependency d)
	{
		if(d.gov_pos== 0)
			this.root = this.nodes.get(d.dep_pos - 1);
		else
			this.link(d.gov_pos, d.dep_pos);
	}
	
	public DepTree(Node _root)
	{
		nodes = new ArrayList<Node>();
		root = _root;
	}
	
	public DepTree(String _val)
	{
		nodes = new ArrayList<Node>();
		root = new Node(_val);
	}
	
	public void setRoot(Node _r)
	{
		root = _r;
	}
	
	public String explore(Node r)
	{
		String res = "{"+r;
		for(Node c : r.children)
		{
			res += explore(c);
		}
		res += "}";
		
		return res;
	}
	
	public String toString()
	{
		return explore(this.root);
	}
	
	
	
	public class Node
	{
		ArrayList<Node> children;
		Node parent;
		
		String value;
		
		public Node(String _val)
		{
			value = _val;
			children = new ArrayList<Node>();
		}
		
		public Node(String _val, ArrayList<Node> _children)
		{
			value = _val;
			children = _children;
		}
		
		
		public void addChild(Node child)
		{
			children.add(child);
		}
		
		public void addChild(String val)
		{
			children.add(new Node(val));
		}
		
		public Node getLeftMost()
		{
			return null;
		}
		
		public Node getKeyRoot()
		{
			return null;
		}
		
		public String toString()
		{
			return value;
		}
	}
	
}



