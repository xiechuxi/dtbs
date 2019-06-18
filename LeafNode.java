package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class LeafNode implements Node {
	protected ArrayList<Entry> entries;
	protected int degree;
	protected Node parent;
	protected Node previous;
	protected Node next;
	protected ArrayList<Node> children;
	protected boolean isroot;
	
	
	public LeafNode(int degree) {
		this.degree=degree;
		this.entries=new ArrayList<Entry>(degree);
		this.parent=null;
		this.children=null;
		this.isroot=false;

		//your code here
	}
	
	
	public void setisroot(boolean k) {
		this.isroot=k;
	}
	public boolean isroot() {
		return isroot;
	}
	
	protected void setChildren(ArrayList<Node> ch) {
		this.children=ch;
	}
	
	public void setprevious(Node pr) {
		this.previous=pr;
	}
	
	public void setnext(Node nt) {
		this.next=nt;
	}
	
	public void setparent(Node pt) {
		this.parent=pt;
	}
	
	public Node getparent() {
		return parent;
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

	@Override
	public boolean isLeafNode() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public boolean contains(Field key) {
		for(Entry entry:this.entries) {
			if(entry.getField().compare(RelationalOperator.EQ, key)) {
				return true;
			}
		}
		
		return false;
		
	}
	
	public void insertorupdate(Entry e,BPlusTree tree) {
		if(this.isLeafNode()) {
			if(this.contains(e.getField())||this.entries.size()<tree.getpl()){
				insertorupdate(e);
				if(this.parent!=null) {
					((InnerNode) this.parent).updateinsert(tree);;
				}
			}
			else {
				LeafNode left=new LeafNode(1);
				LeafNode right=new LeafNode(1);
				if(this.previous!=null) {
					((LeafNode) this.previous).setnext(left);
					left.setprevious(this.previous);
				}
				if(next!=null) {
					((LeafNode) this.next).setprevious(right);
					right.setnext(this.next);
				}
				if(this.previous==null) {
					tree.sethead(left);
				}
				left.setnext(right);
				right.setprevious(left);
				this.previous=null;
				this.next=null;
				
				int leftsize= (tree.getpl()+1)/2 + (tree.getpl()+1)%2;
				int rightsize = (tree.getpl() + 1)/2;
				insertorupdate(e);
				for(int i=0;i<leftsize;i++) {
					left.getEntries().add(this.entries.get(i));
				}
				for(int i=0;i<rightsize;i++) {
					right.getEntries().add(this.entries.get(leftsize+i));
				}
				
				if(parent!=null) {
					int index=((InnerNode) parent).getChildren().indexOf(this);
					((InnerNode) parent).getChildren().remove(this);
					left.setparent(parent);
					right.setparent(parent);
					((InnerNode) parent).getChildren().add(index, left);
					((InnerNode) parent).getChildren().add(index+1,right);
					setEntries(null);
					setChildren(null);
					
					
					((InnerNode) parent).updateinsert(tree);
					setparent(null);
				}
				else {
					isroot=false;
					InnerNode parent=new InnerNode(1);
					tree.setroot(parent);
					left.setparent(parent);
					right.setparent(parent);
					((InnerNode) parent).getChildren().add(left);
					((InnerNode) parent).getChildren().add(right);
					setEntries(null);
					setChildren(null);
					
					
					parent.updateinsert(tree);
				}
			}
		}
		
	}
	
	
	private void insertorupdate(Entry e) {
		Entry entry=new Entry(null,0);
		if(entries.size()==0) {
			entries.add(entry);
			return;
		}
		for(int i=0;i<entries.size();i++) {
			if(entries.get(i).getField().compare(RelationalOperator.EQ, e.getField())) {
				entries.set(i, e);
				return;
			}
			else if(entries.get(i).getField().compare(RelationalOperator.GT, e.getField())){
				if(i==0) {
					entries.add(0,entry);
					return;
				}
				else {
					entries.add(i, entry);
					return;
				}
				
			}
		}
		
		entries.add(entries.size(), entry);
	}
	
	

     
	
			
			
			
			
	
	
	
	

	

}