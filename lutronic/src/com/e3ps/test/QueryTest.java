package com.e3ps.test;

import com.ptc.reportview.ClassAttributes;

import ilog.jlm.o;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.part.WTPart;
import wt.pds.DatabaseInfoUtilities;
import wt.query.ClassAttribute;
import wt.query.KeywordExpression;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;

public class QueryTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		test();
	}
	
	public static void test() {
		
			try{
				QuerySpec query = new QuerySpec();
				int idx = query.addClassList(WTPart.class, true);
				
				
				//KeywordExpression condiitionKey1 = new KeywordExpression("length("+WTPart.NUMBER+")");//A0.WTPartNumber
				ClassAttribute ca = new ClassAttribute(WTPart.class, WTPart.NUMBER);
				//ClassAttribute ca = new ClassAttributes(WTPart.NUMBER);
				new ClassAttributes(WTPart.NUMBER);
				SQLFunction sql = new SQLFunction(SQLFunction.LENGTH,ca)
				
				
				//KeywordExpression condiitionKey1 = new KeywordExpression("LENGTH(A0.master>number)");
				KeywordExpression condiitionKey1 = new KeywordExpression(sql.toString());
				KeywordExpression valueKey1 = new KeywordExpression("10");
				//KeywordExpression ke1 = new KeywordExpression("(length(REGEXP_REPLACE("+task_seqColumnName+", '[^0-9]'))=10 and length("+task_seqColumnName+")=10)");
				//KeywordExpression ke2 = new KeywordExpression("(length(REGEXP_REPLACE("+task_seqColumnName+", '[^0-9]'))=10 and length("+task_seqColumnName+")=10)");
				
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				SearchCondition sc = new SearchCondition(condiitionKey1, SearchCondition.EQUAL , valueKey1);
				
				//query.
				query.appendWhere(sc, new int[] { idx });
				
				//System.out.println(query);
				
				QueryResult rt =PersistenceHelper.manager.find(query);
				while(rt.hasMoreElements()){
					Object[] oo = (Object[])rt.nextElement();
					WTPart part = (WTPart)oo[0];
					
					//System.out.println(part.getNumber());
				}
				
				//System.out.println(query);
				
				
				//더미 제외 숫자로 구성 되고 10자리
				//KeywordExpression ke1 = new KeywordExpression("(length(REGEXP_REPLACE("+task_seqColumnName+", '[^0-9]'))=10 and length("+task_seqColumnName+")=10)");
				//KeywordExpression ke2 = new KeywordExpression("(length(REGEXP_REPLACE("+task_seqColumnName+", '[^0-9]'))=10 and length("+task_seqColumnName+")=10)");
				if(ischeckDummy){
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					ClassAttribute ca = new ClassAttribute(WTPart.class, WTPart.NUMBER);
					//SQLFunction sql = new SQLFunction(SQLFunction.LENGTH,ca.);
					//new SQLFunction
					KeywordExpression condiitionKey1 = new KeywordExpression("LENGTH(A0.WTPartNumber)");
					KeywordExpression valueKey1 = new KeywordExpression("10");
					SearchCondition sc = new SearchCondition(condiitionKey1, SearchCondition.EQUAL,valueKey1);
					query.appendWhere(sc, new int[] { idx });
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		
	}

}
