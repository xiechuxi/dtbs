package hw1;

import java.sql.Types;
import java.util.HashMap;

/**
 * This class represents a tuple that will contain a single row's worth of information
 * from a table. It also includes information about where it is stored
 * @author Sam Madden modified by Doug Shook
 *
 */
public class Tuple {
	
	private TupleDesc schema;
	private int npid;
	private Field[] nfields;
	private int ntid;
	/**
	 * Creates a new tuple with the given description
	 * @param t the schema for this tuple
	 */
	public Tuple(TupleDesc t) {
		assert(t instanceof TupleDesc);
		assert(t.numFields()>0);
		this.schema=t;
		this.nfields=new Field[t.numFields()];
		//your code here
	}
	
	public TupleDesc getDesc() {
		
		//your code here
		return schema;
	}
	
	/**
	 * retrieves the page id where this tuple is stored
	 * @return the page id of this tuple
	 */
	public int getPid() {
		//your code here
		
		return npid;
	}

	public void setPid(int pid) {
		//your code here
		npid=pid;
	}

	/**
	 * retrieves the tuple (slot) id of this tuple
	 * @return the slot where this tuple is stored
	 */
	public int getId() {
		//your code here	
		return ntid;
	}

	public void setId(int id) {
		//your code here
		ntid=id;
	}
	
	public void setDesc(TupleDesc td) {
		this.schema = td;
		//your code here;
		
	}
	
	/**
	 * Stores the given data at the i-th field
	 * @param i the field number to store the data
	 * @param v the data
	 */
	public void setField(int i, Field v) {
		//your code here
		assert(i<=nfields.length);
		assert(i>=0);
		nfields[i]=v;
	}
	
	public Field getField(int i) {
		//your code here
		assert(i>=0);
		assert(i<=nfields.length);
		return nfields[i];
	}
	
	/**
	 * Creates a string representation of this tuple that displays its contents.
	 * You should convert the binary data into a readable format (i.e. display the ints in base-10 and convert
	 * the String columns to readable text).
	 */
	public String toString() {
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i<nfields.length; i++) {
			temp = temp.append(nfields[i].toString() + "\n");
		}
		//your code here
		
		return temp.toString();
	}
}
	