package com.e3ps;

import com.e3ps.org.People;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.session.SessionHelper;

public class Test {

	public static void main(String[] args) throws Exception {

		RemoteMethodServer.getDefault().setUserName("wcadmin");
//		RemoteMethodServer.getDefault().setPassword("wcadmin1");
//
//		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
//
//		People p = People.newPeople();
//		p.setName("김준호");
//		p.setUser(user);
//
//		PersistenceHelper.manager.save(p);

//		String s = "asdsadad&ASDASD";
//		int start = s.indexOf("&");
//		System.out.println(s.substring(0, start));


		String a = "나의업무, 문서관리";
		System.out.println(a.contains("문서관리"));
		
		System.exit(0);
	}
}
