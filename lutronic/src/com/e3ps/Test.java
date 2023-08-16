package com.e3ps;

import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.rule.init.Attr;

public class Test {

	public static void main(String[] args) throws Exception {

		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(Attr.class, true);
		int idx_master = query.appendClassList(WTPartMaster.class, false);
		int idx_part = query.appendClassList(WTPart.class, true);
//		int idx_color = query.appendClassList(PARTCOLOR.class, true);

		SearchCondition sc = null;
		ClassAttribute ca = null;
//		sc = new SearchCondition(Attr.class, "cad_key", WTPartMaster.class, "name");
//		sc.setOuterJoin(SearchCondition.RIGHT_OUTER_JOIN);
//		query.appendAnd();

		ClassAttribute ca1 = new ClassAttribute(WTPartMaster.class, "name");
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca1);

		ClassAttribute ca2 = new ClassAttribute(WTPartMaster.class, "name");
		SQLFunction function2 = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca2);

		sc = new SearchCondition(function, "=", function2);
		query.appendWhere(sc, new int[] { idx_master, idx_master });

		System.out.println(query);
	}
}
