package hw3;



import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class BPlusTree {
	private Node root;//root node
	private int pInner;
	private int pLeaf;
	private Node head;
	
    
    public BPlusTree(int pI, int pL) {
    	this.pInner=pI;
    	this.pLeaf=pL;
    	this.root=null;
    	this.head=root;
    	
    	
    	//your code here
    }
    
    public int getpl() {
    	return this.pLeaf;
    }
    
    public int getpi() {
    	return this.pInner;
    }
    
    
    public void sethead(LeafNode hd) {
    	this.head=hd;
    }
    
    public Node getHead() {
    	return head;
    }
    
    
    
    public void setroot(Node rt) {
    	this.root=rt;
    }
    
    
    public LeafNode search(Field f) {
    	if(this.root==null) {
    		return null;
    	}
    	Node tnode;
    	if(this.root.isLeafNode()) {
    		 tnode=(LeafNode)this.root;
    	}
    	else {
    		tnode=(InnerNode)this.root;
    	}
    	//transspassing down through the tree
    	while(!this.root.isLeafNode()) {
    		ArrayList<Field> keys=((InnerNode) this.root).getKeys();
    		for(int i=0;i<keys.size();i++) {
    			if(f.compare(RelationalOperator.LTE, keys.get(i))) {
    				tnode=((InnerNode) tnode).getChildren().get(i);
    				break;
    			}
    			if(f.compare(RelationalOperator.GT, keys.get(keys.size()-1))) {
    				tnode=((InnerNode) tnode).getChildren().get(((InnerNode) tnode).getChildren().size()-1);
    				break;
    			}
    		}
    	}
    	//check if the Field f is actually inside the node
    	ArrayList<Entry> bent=((LeafNode) tnode).getEntries();
    	for(Entry entry:bent) {
    		if(f.compare(RelationalOperator.EQ, entry.getField())) {
    			return (LeafNode)tnode;
    		}
    	}
    	
    	return null;
    }
    
    
   
    

	public void insert(Entry e) {  
		root.setisroot(true);
		root.insertorupdate(e, this);
		//your code here
    }
    
	
	
    public void delete(Entry e) {
    	//your code here
    }
    
    public Node getRoot() {
    	
    	//your code here
    	return this.root;
    }
    
    
	


	
}
