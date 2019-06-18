package hw3;

import hw1.Field;

public class split {
	public Field key;
	public Node left;
	public Node right;

	public split(Field k, Node l, Node r) {
	    key = k; left = l; right = r;
	}
}