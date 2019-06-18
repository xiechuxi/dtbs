package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class InnerNode implements Node {
	protected int degree;
	protected ArrayList<Node> children;
	protected ArrayList<Field> Keys;
	protected Node parent;
	protected boolean isroot;
	
	
	
	public InnerNode(int degree) {
		this.degree=degree;
		this.children=new ArrayList<Node>();
		this.Keys=new ArrayList<Field>();
		this.parent=null;
		this.isroot=isroot;
		//your code here
	}
	
	public boolean isroot() {
		return isroot;
	}
	
	public void setChildren(ArrayList<Node> ch) {
		this.children=ch;
	}
	
	public Node getparent() {
		return parent;
	}
	
	public void setKeys(ArrayList<Field> keys) {
		this.Keys=keys;
	}
	
	public void setparent(Node pt) {
		this.parent=pt;
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
	
	public void setisroot(boolean k) {
		this.isroot=k;
	}
	
	public boolean isLeafNode() {
		return false;
	}
	
	public void updateinsert(BPlusTree tree) {
		validate(this,tree);
		
		if(this.children.size()>tree.getpi()) {
			
			InnerNode left=new InnerNode(0);
			InnerNode right=new InnerNode(0);
			int leftsize = (tree.getpi() + 1) / 2 + (tree.getpi() + 1) % 2; 
			int rightsize = (tree.getpi() + 1) / 2; 
			for(int i=0;i<leftsize;i++) {
				left.getChildren().add(this.children.get(i));
				left.getKeys().add(((InnerNode) this.children.get(i)).getKeys().get(0));
				((InnerNode)this.children.get(i)).setparent(left);
			}
			for(int i=0;i<rightsize;i++) {
				right.getChildren().add(children.get(leftsize+i));
				right.getKeys().add(((InnerNode) children.get(leftsize+i)).getKeys().get(0));
				((InnerNode) this.children.get(leftsize+i)).setparent(right);
			}
			
			if(parent!=null) {
				int index = ((InnerNode) parent).getChildren().indexOf(this); 
                ((InnerNode) parent).getChildren().remove(index); 
                ((InnerNode) left).setparent(parent); 
                right.setparent(parent); 
                ((InnerNode) parent).getChildren().add(index,left); 
                ((InnerNode) parent).getChildren().add(index + 1, right); 
                this.setKeys(null); 
                this.setChildren(null); 
                
                ((InnerNode) parent).updateinsert(tree);
                this.setparent(null);
			}
			else {
				isroot = false; 
				InnerNode parent = new InnerNode(1); 
				parent.isroot=true;
				tree.setroot(parent); 
				left.setparent(parent); 
				right.setparent(parent); 
				parent.getChildren().add(left); 
				parent.getChildren().add(right); 
				setKeys(null);
				setChildren(null); 
	                 
	               
	            
				parent.updateinsert(tree); 
			}
		}
	}
	
	public void validate(Node node,BPlusTree tree) {
		if(!node.isLeafNode()) {
			if(((InnerNode) node).getKeys().size()==((InnerNode) node).getChildren().size()) {
				for(int i=0;i<((InnerNode) node).getKeys().size();i++) {
					Field k=null;
					if(((InnerNode) node).getChildren().get(i).isLeafNode()) {
						k=((LeafNode) ((InnerNode) node).getChildren().get(i)).getEntries().get(0).getField();
					}
					else{ 
						k=((InnerNode) ((InnerNode) node).getChildren().get(i)).getKeys().get(0);
					}
					if(((InnerNode) node).getKeys().get(i).compare(RelationalOperator.NOTEQ, k)) {
						((InnerNode) node).getKeys().remove(i);
						((InnerNode) node).getKeys().add(i, k);
						if(!node.isroot()) {
							validate(node.getparent(),tree);
						}
					}
					
				}
				
			}
			else if(node.isroot()&&((InnerNode) node).getChildren().size()>=2 ||
					((InnerNode) node).getChildren().size()>=tree.getpi()/2
					&& ((InnerNode) node).getChildren().size()<=tree.getpi()
					&& ((InnerNode) node).getChildren().size()>=2) {
				((InnerNode) node).getKeys().clear();
				for(int i=0;i<((InnerNode) node).getChildren().size();i++) {
					Field key=null;
					if(((InnerNode) node).getChildren().get(i).isLeafNode()) {
						key=((LeafNode) ((InnerNode) node).getChildren().get(i)).getEntries().get(0).getField();
					}
					else{ 
						key=((InnerNode) ((InnerNode) node).getChildren().get(i)).getKeys().get(0);
					}
					
					((InnerNode) node).getKeys().add(key);
					if(!node.isroot()) {
						validate(node.getparent(),tree);
					}
				}
			}
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	 
	 

	

}