package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class LeafNode implements Node {
	protected ArrayList<Entry> entries;
	protected int degree;
	protected Node parent;
	protected int size;
	
	
	
	public LeafNode(int degree) {
		this.degree=degree;
		this.entries=new ArrayList<Entry>(degree);
		this.parent=null;
		this.size=0;

		//your code here
	}
	
	
	
	
	public ArrayList<Entry> getEntries() {
		//your code here
		return entries;
	}
	
	
	public void setEntries(ArrayList<Entry> nentries) {
		this.entries = nentries;
	}

	public int getDegree() {
		//your code here
		return degree;
	}

	
	public boolean isLeafNode() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void setparent(Node n) {
		this.parent=n;
		
		
	}

	public Node getparent() {
		
		return this.parent;
	}




	public ArrayList<Node> split(Node lf){
    	ArrayList<Node> leafs = new ArrayList<Node>();
    	LeafNode lf1 = new LeafNode(getDegree());
    	LeafNode lf2 = new LeafNode(getDegree());
    	ArrayList<Entry> en1 = new ArrayList<Entry>(getDegree());
    	ArrayList<Entry> en2 = new ArrayList<Entry>(getDegree());
    	
    	int n = ((LeafNode) lf).getEntries().size();
    	for(int i=0;i<n;i++) {
    		if(i<(n+1)/2) {
    			en1.add(((LeafNode) lf).getEntries().get(i));
    		}
    		else {
    			en2.add(((LeafNode) lf).getEntries().get(i));
    		}
    	}
    	lf1.setEntries(en1);
    	lf2.setEntries(en2);
    	leafs.add(lf1);
    	leafs.add(lf2);
    	return leafs;
    }
	
	
	public Node merge(Node lf1){
    	LeafNode lf = new LeafNode(getDegree());
    	ArrayList<Entry> entries = new ArrayList<Entry>();
    	entries.addAll(((LeafNode) lf1).getEntries());
    	entries.addAll(this.getEntries());
    	lf.setEntries(entries);
    	return lf;
    }
	
	
	
	
	
	
	

     
	
			
			
			
			
	
	
	
	

	

}