package com.e3ps;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.org.People;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
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

		
		
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();
			// 강제로 관리자 설정
			SessionHelper.manager.setAdministrator();
			
			
			// 다 끝난후 수정자 변경
			WTUser sessionUser = (WTUser)SessionHelper.manager.getPrincipal();
			VersionControlHelper.setIterationModifier(epm, sessionUser);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
		
		
		
		
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
