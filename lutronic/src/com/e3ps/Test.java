package com.e3ps;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.org.People;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.part.WTPartDescribeLink;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.vc.VersionControlHelper;

public class Test {

	public static void main(String[] args) throws Exception {

		Class<?> target = WTPartDescribeLink.class;
		System.out.println(target.equals(WTPartDescribeLink.class));
		System.exit(0);

	}
}