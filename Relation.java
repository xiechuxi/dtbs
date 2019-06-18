package hw2;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * This class provides methods to perform relational algebra operations. It will be used
 * to implement SQL queries.
 * @author Doug Shook
 *
 */
public class Relation {

	private ArrayList<Tuple> tuples;
	private TupleDesc td;
	
	public Relation(ArrayList<Tuple> l, TupleDesc td) {
		this.td=td;
		this.tuples=l;
		
		//your code here
	}
	
	/**
	 * This method performs a select operation on a relation
	 * @param field number (refer to TupleDesc) of the field to be compared, left side of comparison
	 * @param op the comparison operator
	 * @param operand a constant to be compared against the given column
	 * @return
	 */
	public Relation select(int field, RelationalOperator op, Field operand) {
		if(tuples.isEmpty()) {
			return new Relation(new ArrayList<Tuple>(),td);
		}
		Relation R = new Relation(new ArrayList<Tuple>(),td);
		for(Tuple T:tuples) {
			if(T.getField(field).compare(op, operand)) {
				R.tuples.add(T);
			}
		}
		return R;
		
		//your code here
	
	}
	
	/**
	 * This method performs a rename operation on a relation
	 * @param fields the field numbers (refer to TupleDesc) of the fields to be renamed
	 * @param names a list of new names. The order of these names is the same as the order of field numbers in the field list
	 * @return
	 */
	public Relation rename(ArrayList<Integer> fields, ArrayList<String> names) {
		Relation R=this;
		String[] s=R.td.getFields();
		int j=0;
		for(int i:fields) {
			s[i]=names.get(j);
			j++;
		}
		R.td.setFields(s);
		return R;
		//your code here
		
	}
	
	/**
	 * This method performs a project operation on a relation
	 * @param fields a list of field numbers (refer to TupleDesc) that should be in the result
	 * @return
	 */
	public Relation project(ArrayList<Integer> fields) {
		String[] s=new String[fields.size()];
		Type[] t=new Type[fields.size()];
		TupleDesc tupledesc= new TupleDesc(t,s);
		Relation r = new Relation(new ArrayList<Tuple>(), tupledesc);
		int i=0;
		while(i<fields.size()) {
			s[i]=this.td.getFieldName(fields.get(i));
			t[i]=this.td.getType(fields.get(i));
			i++;
		}
		tupledesc.setFields(s);
		tupledesc.setTypes(t);
		
		int j=0;
		while(j<fields.size()) {
			for(Tuple iTup:tuples) {
				Tuple tr=new Tuple(tupledesc);
				tr.setField(j, iTup.getField(fields.get(j)));
				r.tuples.add(tr);
			}
		j++;
		}
		//your code here
		return r;
	}
	
	/**
	 * This method performs a join between this relation and a second relation.
	 * The resulting relation will contain all of the columns from both of the given relations,
	 * joined using the equality operator (=)
	 * @param other the relation to be joined
	 * @param field1 the field number (refer to TupleDesc) from this relation to be used in the join condition
	 * @param field2 the field number (refer to TupleDesc) from other to be used in the join condition
	 * @return
	 */
	public Relation join(Relation other, int field1, int field2) {
		String[] s=new String[this.td.numFields()+other.td.numFields()];
		Type[] t=new Type[this.td.numFields()+other.td.numFields()];
		TupleDesc tupledesc=new TupleDesc(t,s);
	
		for(int i=0;i<this.td.numFields();i++) {			
				s[i]=this.td.getFieldName(i);
				t[i]=this.td.getType(i);
			}
		for(int i=this.td.numFields();i<s.length;i++) {
				s[i]=other.td.getFieldName(i-this.td.numFields());
				t[i]=other.td.getType(i-this.td.numFields());
			}
		
		tupledesc.setFields(s);
		tupledesc.setTypes(t);
		Relation r = new Relation(new ArrayList<Tuple>(), tupledesc);
		for (Tuple t0 :  this.tuples) {
			for(Tuple t1 : other.tuples) {
				if(t0.getField(field1).compare(RelationalOperator.EQ, t1.getField(field2))) {
					Tuple tr = new Tuple(tupledesc);
					tr.setField(field1, t0.getField(field1));
					tr.setField(this.td.numFields()+field2, t1.getField(field2));
					r.tuples.add(tr);	
				}
				
				}
		}
		
		//your code here
		return r;
	}
	
	/**
	 * Performs an aggregation operation on a relation. See the lab write up for details.
	 * @param op the aggregation operation to be performed
	 * @param groupBy whether or not a grouping should be performed
	 * @return
	 */
	public Relation aggregate(AggregateOperator op, boolean groupBy) {
		Relation r = new Relation(new ArrayList<>(), getDesc());
		
		if(groupBy) {
			if(op==AggregateOperator.COUNT) {
				this.td.setTypes(new Type[] {this.td.getType(0),Type.INT});
			}
		}
		else {
			if(op==AggregateOperator.COUNT) {
		
				this.td.setTypes(new Type[] {Type.INT});
			
		}}
		
		Aggregator aggregator = new Aggregator(op, groupBy, this.td);
		for (Tuple tuple : getTuples()) {
			aggregator.merge(tuple);
		}
		r.tuples = aggregator.getResults();
		
		//your code here
		return r;
		
	}
	
	public TupleDesc getDesc() {
		
		//your code here
		return this.td;
	}
	
	public ArrayList<Tuple> getTuples() {
		//your code here
		return this.tuples;
	}
	
	/**
	 * Returns a string representation of this relation. The string representation should
	 * first contain the TupleDesc, followed by each of the tuples in this relation
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		TupleDesc desc = td;
    	for(int i =0;i<desc.numFields();i++) {
    		sb.append(desc.getFields()[i]+"("+desc.getType(i)+")");
    	}
    	for (Tuple tuple : tuples) {
    		for(int i = 0;i<desc.numFields();i++){
    			sb.append(tuple.getField(i).toString());
			}
    		sb.append("\n");
		}
    	
		//your code here
		return sb.toString();
	}
}
