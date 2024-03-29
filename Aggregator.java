package hw2;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hw1.Field;
import hw1.IntField;
import hw1.StringField;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * A class to perform various aggregations, by accepting one tuple at a time
 * @author Doug Shook
 *
 */
public class Aggregator {

	public  AggregateOperator ag;
	public boolean group;
	public TupleDesc tupleDesc;
	public HashMap tuples;
	public HashMap<Field,Integer> count;

	public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
		this.ag = o;
		this.group = groupBy;
		this.tupleDesc = td;
		if(groupBy) {
			if(td.getType(0)==Type.INT) {
				tuples = new HashMap<Integer,Integer>();
			}
			else {
				tuples = new HashMap<String,Integer>();
			}
		}
		else if(td.getType(0)==Type.INT) {
				tuples = new HashMap<Integer,Integer>();
			}
		else if(o==AggregateOperator.COUNT) {
					tuples = new HashMap<String,Integer>();
				}
		tuples = new HashMap<String,String>();
			
		
		//your code here

	}

	/**
	 * Merges the given tuple into the current aggregation
	 * @param t the tuple to be aggregated
	 */
	public void merge(Tuple t) {
		if(!group) {
			if(tupleDesc.getType(0)==Type.INT) {
				switch (ag) {
		        case MAX:
		        	if(tuples.get(0)!=null){
		        		if((int)tuples.get(0)<((IntField)t.getField(0)).getValue()) {
		        			tuples.put(0, ((IntField)t.getField(0)).getValue());
		        		}
		        	}
		        	else {
		        		tuples.put(0, ((IntField)t.getField(0)).getValue());
		        	}
		            break;
		            
		        case MIN:
		        	if(tuples.get(0)!=null){
		        		if((int)tuples.get(0)>((IntField)t.getField(0)).getValue()) {
		        			tuples.put(0, ((IntField)t.getField(0)).getValue());
		        		}
		        	}
		        	else {
		        		tuples.put(0, ((IntField)t.getField(0)).getValue());
		        	}
		            break;
		            
		        case SUM:
		        	if(tuples.get(0)!=null){
		        		tuples.put(0,(int)tuples.get(0)+((IntField)t.getField(0)).getValue());
		        	}
		        	else {
		        		tuples.put(0, ((IntField)t.getField(0)).getValue());
		        	}
		            break;

		        case AVG:
		        	if(tuples.get(0)!=null){
		        		tuples.put(tuples.size(),((IntField)t.getField(0)).getValue());
		        		tuples.put(0, ((tuples.size()-1*((int)tuples.get(0)))+(((IntField)t.getField(0)).getValue()))/tuples.size());
		        	}
		        	else {
		        		tuples.put(0, ((IntField)t.getField(0)).getValue());
		        	}
		            break;

		        case COUNT:
		        	if(tuples.get(0)!=null){
		        		tuples.put(0,(int)tuples.get(0)+1);
		        	}
		        	else {
		        		tuples.put(0, 1);
		        	}
		            break;
				}
			}
			if(tupleDesc.getType(0)==Type.STRING) {
				switch (ag) {
		        case MAX:
		        	if(tuples.get(0)!=null & ((StringField)t.getField(0)).getValue().compareTo(tuples.get(0).toString())==1){
		        		tuples.put(0, ((StringField)t.getField(0)).getValue());}
		        	else {
		        		tuples.put(0, ((StringField)t.getField(0)).getValue());
		        	}
		            break;
		            
		        case MIN:
		        	if(tuples.get(0)!=null & ((StringField)t.getField(0)).getValue().compareTo(tuples.get(0).toString())==-1){
		        		tuples.put(0, ((StringField)t.getField(0)).getValue());}
		        	else {
		        		tuples.put(0, ((StringField)t.getField(0)).getValue());
		        	}
		            break;
		            
		        case COUNT:
		        	if(tuples.get(0)!=null){
		        		tuples.put(0,(int)tuples.get(0)+1);
		        	}
		        	else {
		        		tuples.put(0, 1);
		        	}
		            break;
				case AVG:
					break;
				case SUM:
					break;
				}
			}
		}
		
		else {
			if(tupleDesc.getType(1)==Type.INT) {
				switch (ag) {
		        case MAX:
		        	if(tuples.get(t.getField(0))!=null & (int)tuples.get(t.getField(0))<((IntField)t.getField(1)).getValue()){
		        		tuples.put(t.getField(0), ((IntField)t.getField(1)).getValue());}
		        	else {
		        		tuples.put(t.getField(0), ((IntField)t.getField(1)).getValue());
		        	}
		            break;
		            
		        case MIN:
		        	if(tuples.get(t.getField(0))!=null & (int)tuples.get(t.getField(0))>((IntField)t.getField(1)).getValue()){
		        		tuples.put(t.getField(0), ((IntField)t.getField(1)).getValue());}
		        	else {
		        		tuples.put(t.getField(0), ((IntField)t.getField(1)).getValue());
		        	}
		            break;
		            
		        case SUM:
		        	if(tuples.get(t.getField(0))!=null){
		        		tuples.put(t.getField(0),(int)tuples.get(t.getField(0))+((IntField)t.getField(1)).getValue());
		        	}
		        	else {
		        		tuples.put(t.getField(0), ((IntField)t.getField(1)).getValue());
		        	}
		            break;

		        case AVG:
		        	if(tuples.get(t.getField(0))!=null){
		        		
		        		count.put(t.getField(0), (int)count.get(t.getField(0))+1);
		        		tuples.put(t.getField(0), ((count.get(t.getField(0))-1*((int)tuples.get(t.getField(0)))+(((IntField)t.getField(1)).getValue()))/count.get(t.getField(0))));
		        	}
		        	else {
		        		tuples.put(t.getField(0), ((IntField)t.getField(0)).getValue());
		        		count.put(t.getField(0), 1);
		        	}
		            break;

		        case COUNT:
		        	if(tuples.get(t.getField(0))!=null){
		        		tuples.put(t.getField(0),(int)tuples.get(t.getField(0))+1);
		        	}
		        	else {
		        		tuples.put(t.getField(0), 1);
		        	}
		            break;
				}
			}
			if(tupleDesc.getType(1)==Type.STRING) {
				switch (ag) {
		        case MAX:
		        	if(tuples.get(t.getField(0))!=null & ((StringField)t.getField(0)).getValue().compareTo(tuples.get(t.getField(0)).toString())==1){
		        		tuples.put(t.getField(0), ((StringField)t.getField(0)).getValue());}
		        	else {
		        		tuples.put(t.getField(0), ((StringField)t.getField(0)).getValue());
		        	}
		            break;
		            
		        case MIN:
		        	if(tuples.get(t.getField(0))!=null & ((StringField)t.getField(0)).getValue().compareTo(tuples.get(t.getField(0)).toString())==-1){
		        		tuples.put(t.getField(0), ((StringField)t.getField(0)).getValue());}
		        	else {
		        		tuples.put(t.getField(0), ((StringField)t.getField(0)).getValue());
		        	}
		            break;
		            
		        case COUNT:
		        	if(tuples.get(t.getField(0))!=null){
		        		tuples.put(t.getField(0),(int)tuples.get(t.getField(0))+1);
		        	}
		        	else {
		        		tuples.put(t.getField(0), 1);
		        	}
		            break;
				case AVG:
					break;
				case SUM:
					break;
				}
			}
		}
		//your code here
	}
	
	/**
	 * Returns the result of the aggregation
	 * @return a list containing the tuples after aggregation
	 */
	public ArrayList<Tuple> getResults() {
		
		ArrayList<Tuple> myTuples = new ArrayList<Tuple>();
		if(!group) {
			for(int i=0;i<tuples.size();i++) {
				Tuple tp = new Tuple(tupleDesc);
				if(tupleDesc.getType(0)==Type.INT) {
					tp.setField(i, new IntField((int)tuples.get(i)));
				}
				else {
					tp.setField(i, new StringField(tuples.get(i).toString()));
				}
				myTuples.add(tp);
			}
		}
		else {
			Iterator it = tuples.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        it.remove();

				Tuple tp = new Tuple(tupleDesc);
				if(tupleDesc.getType(0)==Type.INT) {
					tp.setField(0, new IntField((int)pair.getValue()));
				}
				else {
					tp.setField(0, new StringField(pair.getValue().toString()));
				}
				if(tupleDesc.getType(1)==Type.INT) {
					tp.setField(1, new IntField((int)pair.getValue()));
				}
				else {
					tp.setField(1, new StringField(pair.getValue().toString()));
				}
				myTuples.add(tp);
			}
		}
		return myTuples;
		//your code here
		
	}

}
