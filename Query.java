package hw2;

import java.util.ArrayList;
import java.util.List;

import hw1.Database;
import hw1.Tuple;
import hw1.TupleDesc;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;

public class Query {

	private String q;
	
	public Query(String q) {
		this.q = q;
	}
	
	public Relation execute()  {
		Statement statement = null;
		try {
			statement = CCJSqlParserUtil.parse(q);
		} catch (JSQLParserException e) {
			System.out.println("Unable to parse query");
			e.printStackTrace();
		}
		Select selectStatement = (Select) statement;
		PlainSelect sb = (PlainSelect)selectStatement.getSelectBody();
		List<SelectItem> items = sb.getSelectItems();
		FromItem fItem = sb.getFromItem();
		int tab = Database.getCatalog().getTableId(fItem.toString());
		ArrayList<Tuple> Tuples = Database.getCatalog().getDbFile(tab).getAllTuples();
		TupleDesc td = Tuples.get(0).getDesc();
		Relation r = new Relation(Tuples,td);
		List<Join> Joins= sb.getJoins();
		
		if(sb.getWhere()!=null) {
			WhereExpressionVisitor whereExpressionVisitor = new WhereExpressionVisitor();
			sb.getWhere().accept(whereExpressionVisitor);
			r=r.select(td.nameToId(whereExpressionVisitor.getLeft()), whereExpressionVisitor.getOp(), whereExpressionVisitor.getRight());
		}
		
		else if(Joins!=null) {
			for(Join j : Joins) {
				FromItem j0 = j.getRightItem();
				int tab1 = Database.getCatalog().getTableId(j0.toString());
				ArrayList<Tuple> Tuples1 = Database.getCatalog().getDbFile(tab1).getAllTuples();
				TupleDesc td1 = Tuples1.get(0).getDesc();
				Relation r1 = new Relation(Tuples1,td1);
				String S[] = j.getOnExpression().toString().split(" = ");
				String S2[] = S[0].split("\\.");
				String S3[] = S[1].split("\\.");
				int f1=0;
				int f2=0;
				if(Database.getCatalog().getTableId(S2[0].trim())==tab) {
					String[] f = td.getFields();
					for (int i =0;i<f.length;i++) {
						if(f[i].equalsIgnoreCase(S2[1].trim())) {
							f1=i;
						}
					}
					
					String[] f0 = td1.getFields();
					for (int i=0;i<f0.length;i++) {
						if(f0[i].equalsIgnoreCase(S3[1].trim())) {
							f2=i;
						}
					}
				}
				else {
					String[] f4 = td1.getFields();
					for (int i =0;i<f4.length;i++) {
						if(f4[i].equals(S2[1].trim())) {
							f2=i;
						}
					}
					
					String[] f5 = td.getFields();
					for (int i=0;i<f5.length;i++) {
						if(f5[i].equals(S3[1].trim())) {
							f1=i;
						}
					}
				}
				r = r.join(r1, f1, f2);
			}
		}
		
		
		else if(!items.get(0).toString().equals("*")) {
			ArrayList<Integer> pItems = new ArrayList<>();
			for(SelectItem i : items) {
				ColumnVisitor columnVisitor = new ColumnVisitor();
				i.accept(columnVisitor);
				
				if(!columnVisitor.isAggregate()) {
					pItems.add(td.nameToId(columnVisitor.getColumn()));
				}
				
				else if(sb.getGroupByColumnReferences()!=null & columnVisitor.isAggregate()) {
					r=r.aggregate(columnVisitor.getOp(), true);
					
				}
				
				else if(sb.getGroupByColumnReferences()==null & columnVisitor.isAggregate()){
					
					r=r.aggregate(columnVisitor.getOp(), false);	
				}
			
				
			}
			r=r.project(pItems);
		}
		
		
		
		//your code here
		return r;
		
	}
		
	
}
