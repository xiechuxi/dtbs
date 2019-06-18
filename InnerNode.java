package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class InnerNode implements Node {
	protected int degree;
	protected ArrayList<Node> children;
	protected ArrayList<Field> Keys;
	private int size;
	protected Node parent;
	
	
	
	public InnerNode(int degree) {
		this.size=0;
		this.degree=degree;
		this.children=new ArrayList<Node>(degree+1);
		this.Keys=new ArrayList<Field>(degree);
		
		
		//your code here
	}
	
	
	
	public void setChildren(ArrayList<Node> ch) {
		this.children=ch;
	}
	
	
	
	public void setKeys(ArrayList<Field> keys) {
		this.Keys=keys;
	}
	
	
	
	public ArrayList<Field> getKeys() {
		//your code here
		return Keys;
	}
	
	public ArrayList<Node> getChildren() {
		//your code here
		return children;
	}
	
	
	public int getDegree() {
		//your code here
		return degree;
	}
	
	
	
	public boolean isLeafNode() {
		return false;
	}
	
	
	public ArrayList<Node> split(Node in){
	    	
	    	
	    	ArrayList<Node> inners = new ArrayList<Node>();
	    	InnerNode in1 = new InnerNode(getDegree());
	    	InnerNode in2 = new InnerNode(getDegree());
	    	int n;
	    	
	    	ArrayList<Field> k1 = new ArrayList<Field>(getDegree());
	    	ArrayList<Field> k2 = new ArrayList<Field>(getDegree());
	    	n = ((InnerNode) in).getKeys().size();
	    	for(int i=0;i<n;i++) {
	    		if(i<(n+1)/2) {
	    			k1.add(((InnerNode) in).getKeys().get(i));
	    		}
	    		else {
	    			k2.add(((InnerNode) in).getKeys().get(i));
	    		}
	    	}
	    	if(n%2==1) {
	    		k1.remove(k1.size()-1);
	    	}
	    	in1.setKeys(k1);
	    	in2.setKeys(k2);
	    	
	    	
	    	ArrayList<Node> c1 = new ArrayList<Node>(getDegree()+1);
	    	ArrayList<Node> c2 = new ArrayList<Node>(getDegree()+1);
	    	n = ((InnerNode) in).getChildren().size();
	    	for(int i=0;i<n;i++) {
	    		if(i<(n+1)/2) {
	    			c1.add(((InnerNode) in).getChildren().get(i));
	    		}
	    		else {
	    			c2.add(((InnerNode) in).getChildren().get(i));
	    		}
	    	}
	    	in1.setChildren(c1);
	    	in2.setChildren(c2);

	    	inners.add(in1);
	    	inners.add(in2);
	    	return inners;
	}
	
	
	public Node merge(Node in1){
    	InnerNode in = new InnerNode(getDegree());
    	ArrayList<Node> c = new ArrayList<Node>();
    	c.addAll(((InnerNode) in1).getChildren());
    	c.addAll(this.getChildren());
    	in.setChildren(c);
    	
    	ArrayList<Field> k = new ArrayList<Field>();
    	k.addAll(((InnerNode) in1).getKeys());
    	if(((InnerNode) in1).getChildren().get(0).isLeafNode()) {
    		
    		k.add(((LeafNode)((InnerNode) in1).getChildren().get(((InnerNode) in1).getChildren().size()-1)).getEntries().get(((LeafNode)((InnerNode) in1).getChildren().get(((InnerNode) in1).getChildren().size()-1)).getEntries().size()-1).getField());
    	}
    	else {
    		
    		k.add(((InnerNode) ((InnerNode) in1).getChildren().get(((InnerNode) in1).getChildren().size()-1)).getKeys().get(((InnerNode) ((InnerNode) in1).getChildren().get(((InnerNode) in1).getChildren().size()-1)).getKeys().size()-1));
    	}
    	
    	k.addAll(this.getKeys());
    	in.setKeys(k);
    	return in;
    }
	  
	  
	  
	  




	




	
	
	
	
}