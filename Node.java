package hw3;

import hw1.Field;

public interface Node {
	public void setparent(Node pt);
	public Node getparent();
	public int getDegree();
	public boolean isLeafNode();
	public void insertorupdate(Entry e,BPlusTree tree);
	public boolean isroot();
	public void setisroot(boolean k);
	
}
