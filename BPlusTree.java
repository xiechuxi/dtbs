package hw3;



import java.util.ArrayList;


import hw1.Field;
import hw1.RelationalOperator;

public class BPlusTree {
	private Node root;//root node
	private int pInner;
	private int pLeaf;
	
	
    
    public BPlusTree(int pI, int pL) {
    	this.pInner=pI;
    	this.pLeaf=pL;
    	this.root=null;
    	
    	
    	
    	//your code here
    }
    
    public int getpl() {
    	return this.pLeaf;
    }
    
    public int getpi() {
    	return this.pInner;
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
    	while(!tnode.isLeafNode()) {
    		ArrayList<Field> keys=((InnerNode) tnode).getKeys();
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
		if(this.root==null) {
			this.root=new LeafNode(this.pLeaf);
			ArrayList<Entry> eties=new ArrayList<Entry>();
			eties.add(e);
			((LeafNode) root).setEntries(eties);
			
			return;
		}
		
		if(this.root.isLeafNode()) {
			if(((LeafNode) this.root).getEntries().size()<this.pLeaf) {
				((LeafNode) this.root).setEntries(addthensort(((LeafNode) this.root).getEntries(),e));
				return;
			}
			else {
				LeafNode splitleft=new LeafNode(this.root.getDegree());
				LeafNode splitright=new LeafNode(this.root.getDegree());
				ArrayList<Entry> newarray=addthensort(((LeafNode) this.root).getEntries(),e);
				setroot(new InnerNode(pInner));
				splitleft.setEntries(new ArrayList<Entry>(newarray.subList(0, (newarray.size()+1)/2)));
				splitright.setEntries(new ArrayList<Entry>(newarray.subList((newarray.size()+1)/2,newarray.size())));
				ArrayList<Node>children=new ArrayList<Node>();
				children.add(splitleft);
				children.add(splitright);
				((InnerNode) this.root).setChildren(children);
				ArrayList<Field> nck=new ArrayList<Field>();
				nck.add(splitleft.getEntries().get(splitleft.getEntries().size()-1).getField());
				((InnerNode) this.root).setKeys(nck);
				return;
			}
		}
		
		Node node =new InnerNode(pInner);
		((InnerNode) node).setKeys(((InnerNode) this.root).getKeys());
		((InnerNode) node).setChildren(((InnerNode) this.root).getChildren());
		ArrayList<Node> nnode=new ArrayList<Node>();
		while(!node.isLeafNode()) {
			nnode.add(node);
			int l=((InnerNode) node).getChildren().size();
			ArrayList<Field> k=((InnerNode) node).getKeys();
			for (int i=0;i<k.size();i++) {
    			if(e.getField().compare(RelationalOperator.LTE, k.get(i))) {
    				node = ((InnerNode)node).getChildren().get(i);
    				break;
    			}
				if(e.getField().compare(RelationalOperator.GT, k.get(k.size()-1))) {
    				node = ((InnerNode)node).getChildren().get(l-1);
    				break;
    			}
    		}
		}
		
		ArrayList<Entry> entries=new ArrayList<Entry>();
		
		if(((LeafNode) node).getEntries().size()<((LeafNode) node).getDegree()) {
			entries=((LeafNode) node).getEntries();
			for(int j=0;j<entries.size();j++) {
				if(e.getField().compare(RelationalOperator.LT, entries.get(j).getField())) {
    				entries.add(j, e);
    				((LeafNode)node).setEntries(entries);
    				return;
    			}
			}
			
			if(e.getField().compare(RelationalOperator.GT, entries.get(entries.size()-1).getField())) {
    			entries.add(e);
    			((LeafNode)node).setEntries(entries);
    			return;
			}
		}else {
			LeafNode ln=new LeafNode(pLeaf+1);
			entries=addthensort(((LeafNode) node).getEntries(),e);
			for(int i=0;i<((LeafNode) node).getEntries().size();i++) {
				if(e.getField().compare(RelationalOperator.LT, entries.get(i).getField())) {
					entries.add(i, e);
					break;
				}
			}
			
			if(e.getField().compare(RelationalOperator.GT, entries.get(entries.size()-1).getField())) {
				entries.add(e);
			}
			
			ArrayList<Entry> enl=new ArrayList<Entry>();
			ArrayList<Entry> enr=new ArrayList<Entry>();
			
			for(int j=0;j<entries.size();j++) {
				if(j<(entries.size()+1)/2){
					enl.add(entries.get(j));
				}
				else {
					enr.add(entries.get(j));
				}
			}
			
			((LeafNode) node).setEntries(enl);
			ln.setEntries(enr);
			
			

			LeafNode ll=new LeafNode(pLeaf);
			LeafNode lr=new LeafNode(pLeaf);
			ArrayList<Entry> ar=new ArrayList<Entry>();
			ar= addthensort(((LeafNode) node).getEntries(),e);
			ArrayList<Entry> lla=new ArrayList<Entry>(ar.subList(0, (ar.size()+1)/2));
			ArrayList<Entry> lra=new ArrayList<Entry>(ar.subList((ar.size()+1)/2,ar.size()));
			ll.setEntries(lla);
			lr.setEntries(lra);
			LeafNode nn=new LeafNode(pLeaf);
			nn=(LeafNode) node;
			node=nnode.get(nnode.size()-1);
			
			if(((InnerNode) node).getChildren().size()<((InnerNode) node).getDegree()+1) {
				ArrayList<Node> child = ((InnerNode)node).getChildren();
				child.add(child.indexOf(nn),ll);
				child.add(child.indexOf(nn),lr);
				child.remove(nn);
				((InnerNode)node).setChildren(child);
				
				((InnerNode)node).setKeys(addthenSortField(((InnerNode)node).getKeys(), ll.getEntries().get(ll.getEntries().size()-1).getField()));
				
				return;
			}else {
				boolean o=true;
				while(!nnode.isEmpty()) {
					node=nnode.get(nnode.size()-1);
					nnode.remove(nnode.size()-1);
					if(o) {
						ArrayList<Node> child = new ArrayList<Node>();
						child.addAll(((InnerNode)node).getChildren());
						int num = child.indexOf(nn);
						child.add(num,ll);
						child.add(num+1,lr);
						child.remove(nn);
						((InnerNode)node).setChildren(child);
						ArrayList<Field> fff = ((InnerNode)node).getKeys();
						fff.remove(num);
						((InnerNode)node).setKeys(addthenSortField(fff, ll.getEntries().get(ll.getEntries().size()-1).getField()));
						o=false;
					}
					
					
					InnerNode i1 = new InnerNode(pInner);
					InnerNode i2 = new InnerNode(pInner);
					ArrayList<Field> ik = new ArrayList<Field>();
					ik.addAll(((InnerNode)node).getKeys());
					ArrayList<Node> ic = new ArrayList<Node>();
					ic.addAll(((InnerNode)node).getChildren());
					
					node = new InnerNode(pInner);
					
					i1.Keys.clear();
					i2.children.clear();
					i1.Keys.clear();
					i2.children.clear();
					System.arraycopy(ik, 0, i1.Keys, 0, (ik.size()+1)/2);
					System.arraycopy(ik, (ik.size()+1)/2, i2.Keys, 0, (ik.size()-1)/2);
					System.arraycopy(ic, 0, i1.children, 0, (ic.size()+1)/2);
					System.arraycopy(ic, (ic.size()+1)/2, i2.children, 0, (ic.size()-1)/2);
	    			
					
	    			Field h = i1.getKeys().get((i1.getKeys()).size()-1);
	    			
	    			
	    			ik = i1.getKeys();
	    			ik.remove(i1.getKeys().get((i1.getKeys()).size()-1));
	    			i1.setKeys(ik);
	    			ArrayList<Node> child1 = new ArrayList<Node>();
	    			child1.add(i1);
	    			child1.add(i2);
	    			
	    			((InnerNode)node).setChildren(child1);
	    			if(nnode.size()!=0) {
		    			((InnerNode)nnode.get(nnode.size()-1)).setKeys(addthenSortField(((InnerNode)nnode.get(nnode.size()-1)).getKeys(), h));
	    			}
	    			else {
	    				((InnerNode)node).setChildren(child1);
	    				((InnerNode)node).Keys.clear();
	    				ArrayList<Field> lh = new ArrayList<>();
	    				lh.add(h);
	    				((InnerNode)node).setKeys(lh);
	    			}
	    			
	    			((InnerNode)node).setKeys(addthenSortField(((InnerNode)node).getKeys(), h));
	    			
	    			
	    			
				}
				
				setroot((InnerNode) node);
			}
			
			
			
			
		}
		
		
		
    }
	
	
	private ArrayList<Entry> addthensort(ArrayList<Entry> el, Entry entry) {
    	for(Entry e : el) {
    		if(entry.getField().compare(RelationalOperator.LT, e.getField())) {
    			el.add(el.indexOf(e),entry);
    			return el;
    		}
    	}
    	el.add(entry);
    	return el;
    }
    
	private ArrayList<Field> addthenSortField(ArrayList<Field> el, Field field) {
    	for(Field e : el) {
    		if(field.compare(RelationalOperator.LT, e)) {
    			el.add(el.indexOf(e),field);
    			return el;
    		}
    	}
    	el.add(field);
    	return el;
    }
	
	
    public void delete(Entry e) {
    	LeafNode leafNode = new LeafNode(pLeaf);
    	leafNode = search(e.getField());
    	if(leafNode==null) {
    		
    		return;
    	}
    	
    	ArrayList<Entry> entries = new ArrayList<Entry>();
    	entries.addAll(leafNode.getEntries());
    	
    	if(getRoot().isLeafNode()) {
    		entries.remove(e);
    		if(entries.equals(null)) {
    			setroot(null);
    			return;
    		}
    		leafNode.setEntries(entries);
    		return;
    	}
    	
    	Field f = e.getField();
    	Field replace = null;
    	int l = 0;
    	for (int i=0;i<entries.size();i++) {
    		if(f.compare(RelationalOperator.EQ, entries.get(i).getField())) {
    			l=i;
    			if (i==entries.size()-1 && i>0){
    				replace = entries.get(i-1).getField();
    			}
    			
    			entries.remove(l);
    			break;
    		}
    	}
    	
    	InnerNode in = getparent(f);
    	
    	if(entries.size()>=(pLeaf+1)/2) {
    		if(l!=entries.size()) {
    			leafNode.setEntries(entries);
    			return;
    		}
    		else {
    			while(in!=null) {
    	    		if(in.getKeys().contains(f)) {
    	    			ArrayList<Field> keys = new ArrayList<Field>();
    	    			keys.addAll(in.getKeys());
    	    			keys.add(keys.indexOf(f),replace);
    	    			keys.remove(f);
    	    			in.setKeys(keys);
    	    			leafNode.setEntries(entries);
    	    			return;
    	    		}
    	    		in = getparent(in);
    	    	}
    		}
    		
    	}
    	else {
			ArrayList<Node> c = new ArrayList<Node>(pInner);
    		ArrayList<Field> k = new ArrayList<Field>(pInner);
    		c = in.getChildren();
    		k = in.getKeys();
    		LeafNode lf = new LeafNode(pLeaf);
    		lf = (LeafNode)getprevious(leafNode);
    		if(lf!=null) {
    			if(lf.getEntries().size()>(pLeaf+1)/2) {
    				borrow(leafNode, in, lf);
    				entries = leafNode.getEntries();
    				for (int i=0;i<entries.size();i++) {
    		    		if(f.compare(RelationalOperator.EQ, entries.get(i).getField())) {
    		    			entries.remove(i);
    		    			break;
    		    		}
    		    	}
    				leafNode.setEntries(entries);
    				ArrayList<Field> temp = new ArrayList<Field>();
    				temp = in.getKeys();
    				if(temp.indexOf(f)!=-1) {
    					temp.add(temp.indexOf(f), entries.get(entries.size()-1).getField());
    					temp.remove(f);
    				}
    				in.setKeys(temp);
    				return;
    			}
    			else {
    				lf = null;
    			}
    		}
    		
    		if(lf==null) {
    			lf = ((LeafNode)getnext(leafNode));
    			if(lf!=null) {
        			if(lf.getEntries().size()>(pLeaf+1)/2) {
        				borrow(leafNode, in, lf);
        				entries = leafNode.getEntries();
        				for (int i=0;i<entries.size();i++) {
        		    		if(f.compare(RelationalOperator.EQ, entries.get(i).getField())) {
        		    			entries.remove(i);
        		    			break;
        		    		}
        		    	}
        				leafNode.setEntries(entries);
        				ArrayList<Field> temp = new ArrayList<Field>();
        				temp = in.getKeys();
        				if(temp.indexOf(f)!=-1) {
        					temp.add(temp.indexOf(f), entries.get(entries.size()-1).getField());
        					temp.remove(f);
        				}
        				in.setKeys(temp);
        				return;
        			}
        		}
    		}
    		
    		if(c.size()>(pInner+2)/2) {
        		LeafNode lfnew = new LeafNode(pLeaf);
        		LeafNode sibling = new LeafNode(pLeaf);
        		sibling = (LeafNode)getnext(leafNode);
        		lfnew = (LeafNode) leafNode.merge(sibling);
        		ArrayList<Entry> temp = new ArrayList<Entry>();
        		temp.addAll(sibling.getEntries());
        		temp.addAll(entries);
        		lfnew.setEntries(temp);
        		c.add(c.indexOf(sibling), lfnew);
        		c.remove(leafNode);
        		c.remove(sibling);
        		in.setChildren(c);
        		
        		
        		k.remove(leafNode.getEntries().get(leafNode.getEntries().size()-1).getField());
        		in.setKeys(k);
        		return;
    		}
    		
    		while(in!=null) {
        		/*Borrow from parent's sibling*/
        		InnerNode inn = new InnerNode(pInner);
        		if(getprevious(in)!=null) {
        			if(!getprevious(in).isLeafNode()) {
        				inn = (InnerNode)getprevious(in);
        			}
        		}
        		if(inn !=null) {
	        		if(inn.getChildren().size()>(pInner+2)/2) {
	        			borrow(in, getparent(in), inn);
	        		}
	        		else {
	        			inn = null;
	        		}
        		}
        		if(inn == null) {
        			if(getnext(in)!=null) {
        				if(!getnext(in).isLeafNode()) {
        					inn = (InnerNode)getnext(in);
        				}
        			}
        			if(inn!=null) {
        				if(inn.getChildren().size()>(pInner+2)/2) {
        					borrow(in, getparent(in), inn);
        				}
	        		}
        		}
        		
        		
        		if(c.size()>(pInner+2)/2) {
	        		LeafNode lfnew = new LeafNode(pLeaf);
	        		LeafNode sibling = new LeafNode(pLeaf);
	        		sibling = (LeafNode)getprevious(leafNode);
	        		lfnew = (LeafNode) leafNode.merge(sibling);
	        		ArrayList<Entry> temp = new ArrayList<Entry>();
	        		temp.addAll(sibling.getEntries());
	        		temp.addAll(entries);
	        		lfnew.setEntries(temp);
	        		c.add(c.indexOf(sibling), lfnew);
	        		c.remove(leafNode);
	        		c.remove(sibling);
	        		in.setChildren(c);
	        		
	        		
	        		k.remove(leafNode.getEntries().get(leafNode.getEntries().size()-1).getField());
	        		in.setKeys(k);
	        		return;
        		}
        		
        		else {
        			InnerNode innn = new InnerNode(pInner);
        			InnerNode sibling = new InnerNode(pInner);
        			if(getprevious(in)!=null) {
        				if(!getprevious(in).isLeafNode()) {
        					sibling = (InnerNode)getprevious(in);
        				}
        			}
        			if(sibling!=null) {
        				innn = (InnerNode) in.merge(sibling);
        				return;
        			}
        			in = getparent(in);
        		}
    		}
    	}
    	return;
    	//your code here
    }
    
    public Node getRoot() {
    	
    	//your code here
    	return this.root;
    }
    
    private Node getprevious(Node n) {
    	Field f=null;
    	if(n.isLeafNode()) {
    		f=((LeafNode)n).getEntries().get(0).getField();
    	}
    	else {
    		f= ((InnerNode)n).getKeys().get(0);
    	}
    	InnerNode in = getparent(f);
    	if (in==null) {
    		return null;
    	}
    	ArrayList<Field> field = in.getKeys();
    	
    	if(f.compare(RelationalOperator.LTE, field.get(0))) {
    		return null;
    	}
		for (int i=1;i<in.getKeys().size();i++) {
			if(f.compare(RelationalOperator.LTE, field.get(i))) {
				return in.getChildren().get(i-1);
			}
		}
		if(f.compare(RelationalOperator.GTE, field.get(in.getKeys().size()-1))) {
			return in.getChildren().get(in.getKeys().size()-1);
		}
    	return null;
    }
    
    private InnerNode getparent(Field f) {
    	if (getRoot()==null) {
    		return null;
    	}
    	Node node;
    	if(getRoot().isLeafNode()) {
    		node = this.root;
    	}
    	else {
    		node = this.root;
    	}
    	ArrayList <InnerNode> s = new ArrayList<InnerNode>();
    	while(!node.isLeafNode()) {
    		s.add((InnerNode)node);
    		ArrayList<Field> keys = ((InnerNode)node).getKeys();
    		for (int i=0;i<keys.size();i++) {
    			if(f.compare(RelationalOperator.LTE, keys.get(i))) {
    				node = ((InnerNode)node).getChildren().get(i);
    				break;
    			}
				if(f.compare(RelationalOperator.GT, keys.get(keys.size()-1))) {
    				node = (((InnerNode)node).getChildren().get((((InnerNode)node).getChildren()).size()-1));
    				break;
    			}
    		}
    	}
		ArrayList<Entry> entries = ((LeafNode)node).getEntries();
		for (Entry entry : entries) {
			if(f.compare(RelationalOperator.EQ, entry.getField())) {
				InnerNode k=s.get(s.size()-1);
				s.remove(s.size()-1);
				return k;
			}
		}
    	return null;
    }
    
    private Node getnext(Node n) {
    	Field f = null;
    	if(n.isLeafNode()) {
    		f=((LeafNode)n).getEntries().get(0).getField();
    	}
    	else {
    		f= ((InnerNode)n).getKeys().get(0);
    	}
    	InnerNode in = getparent(f);
    	if (in==null) {
    		return null;
    	}
    	ArrayList<Field> field = in.getKeys();
    	
    	if(f.compare(RelationalOperator.GT, field.get(in.getKeys().size()-1))) {
    		return null;
    	}
		for (int i=0;i<in.getKeys().size();i++) {
			if(f.compare(RelationalOperator.LTE, field.get(i))) {
				return in.getChildren().get(i+1);
			}
		}
    	return null;
    }
    
    private InnerNode getparent(Node n) {
    	if (getRoot()==null) {
    		return null;
    	}
    	
    	if(getRoot().isLeafNode()) {
    		return null;
    	}
    	Node node = getRoot();
    	if(n.isLeafNode()) {
	    	Field f = ((LeafNode)n).getEntries().get(0).getField();
	    	ArrayList<InnerNode> s = new ArrayList<InnerNode>();
	    	while(!node.isLeafNode()) {
	    		s.add((InnerNode)node);
	    		ArrayList<Field> keys = ((InnerNode)node).getKeys();
	    		for (int i=0;i<keys.size();i++) {
	    			if(f.compare(RelationalOperator.LTE, keys.get(i))) {
	    				node = ((InnerNode)node).getChildren().get(i);
	    				break;
	    			}
					if(f.compare(RelationalOperator.GT, keys.get(keys.size()-1))) {
	    				node = (((InnerNode)node).getChildren().get((((InnerNode)node).getChildren()).size()-1));
	    				break;
	    			}
	    		}
	    	}
			ArrayList<Entry> entries = ((LeafNode)node).getEntries();
			for (Entry entry : entries) {
				if(f.compare(RelationalOperator.EQ, entry.getField())) {
					InnerNode k=s.get(s.size()-1);
					s.remove(s.size()-1);
					return k;
				}
			}
    	}
    	else {
    		Field f = ((InnerNode)n).getKeys().get(0);
    		ArrayList<InnerNode> s = new ArrayList<InnerNode>();
	    	while(!node.isLeafNode()) {
	    		s.add((InnerNode)node);
	    		ArrayList<Field> keys = ((InnerNode)node).getKeys();
	    		for (int i=0;i<keys.size();i++) {
	    			if(f.compare(RelationalOperator.EQ, keys.get(i))) {
	    				InnerNode k=s.get(s.size()-1);
	    				s.remove(s.size()-1);
	    				return k;
	    			}
	    			if(f.compare(RelationalOperator.LT, keys.get(i))) {
	    				node = ((InnerNode)node).getChildren().get(i);
	    				break;
	    			}
					if(f.compare(RelationalOperator.GT, keys.get(keys.size()-1))) {
	    				node = (((InnerNode)node).getChildren().get((((InnerNode)node).getChildren()).size()-1));
	    				break;
	    			}
	    		}
	    	}
    	}
    	return null;
    }
    
    
    private void borrow(LeafNode child, InnerNode parent, LeafNode sibling) {
    	if(getprevious(child).equals(sibling)) {
    		
    		ArrayList<Entry> en = new ArrayList<Entry>(pLeaf);
    		en.add(sibling.getEntries().get(sibling.getEntries().size()-1));
    		en.addAll(child.getEntries());
    		child.setEntries(en);
    		
    		en = null;
    		
    		en = sibling.getEntries();
    		en.remove(en.size()-1);
    		sibling.setEntries(en);
    		
    		ArrayList<Field> key = new ArrayList<Field>(pInner);
    		key.addAll(parent.getKeys());
    		key.add(key.indexOf(child.getEntries().get(0).getField()),sibling.getEntries().get(sibling.getEntries().size()-1).getField());
    		key.remove(child.getEntries().get(0).getField());
    		parent.setKeys(key);
    		return;
    	}
    	else {
    		
    		ArrayList<Entry> en = new ArrayList<Entry>(pLeaf);
    		en.addAll(child.getEntries());
    		en.add(sibling.getEntries().get(0));
    		child.setEntries(en);
    		
    		en = null;
    		en = sibling.getEntries();
    		en.remove(0);
    		sibling.setEntries(en);
    		
    		ArrayList<Field> key = new ArrayList<Field>(pInner);
    		key.addAll(parent.getKeys());
    		key.add(key.indexOf(child.getEntries().get(child.getEntries().size()-2).getField()), child.getEntries().get(child.getEntries().size()-1).getField());
    		key.remove(child.getEntries().get(child.getEntries().size()-2).getField());
    		parent.setKeys(key);
    		return;
    	}
    }
    
    private void borrow(InnerNode child, InnerNode parent, InnerNode sibling) {
    	if(getprevious(child).equals(sibling)) {
    		
    		ArrayList<Node> en = new ArrayList<Node>(pInner);
    		en.add(sibling.getChildren().get(sibling.getChildren().size()-1));
    		en.addAll(child.getChildren());
    		child.setChildren(en);
    		
    		en = null;
    		en = sibling.getChildren();
    		en.remove(en.size()-1);
    		sibling.setChildren(en);
    		
    		ArrayList<Field> key = new ArrayList<Field>(pInner);
    		key.addAll(parent.getKeys());
    		key.add(key.indexOf(child.getKeys().get(0)),sibling.getKeys().get(sibling.getKeys().size()-1));
    		key.remove(child.getKeys().get(0));
    		parent.setKeys(key);
    		return;
    	}
    	else {
    		
    		ArrayList<Node> en = new ArrayList<Node>(pInner);
    		en.addAll(child.getChildren());
    		en.add(sibling.getChildren().get(0));
    		child.setChildren(en);
    		
    		en= null;
    		en = sibling.getChildren();
    		en.remove(0);
    		sibling.setChildren(en);
    		
    		ArrayList<Field> key = new ArrayList<Field>(pInner);
    		key.addAll(parent.getKeys());
    		key.add(key.indexOf(child.getKeys().get(child.getKeys().size()-2)), child.getKeys().get(child.getKeys().size()-1));
    		key.remove(child.getKeys().get(child.getKeys().size()-2));
    		parent.setKeys(key);
    		return;
    	}
    }
    
    
    
    
    
    
    
   
	
    
	


	
}
