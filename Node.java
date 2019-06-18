package hw3;

import java.util.ArrayList;

import hw1.Field;

public interface Node {
	public int getDegree();
	public boolean isLeafNode();
	public Node merge(Node l);
	
}
