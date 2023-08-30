package com.e3ps;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.org.People;

import wt.fc.PersistenceHelper;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.session.SessionHelper;

public class Test {

	public static void main(String[] args) throws Exception {

		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin1");

		numberCode();

		System.exit(0);
	}

	public static void numberCode() throws Exception {
		NumberCode numberCode = NumberCode.newNumberCode();
		numberCode.setName("테스트 문서");
		numberCode.setDescription("테스트 문서");
		numberCode.setSort("0");
		numberCode.setCode("A");
		numberCode.setCodeType(NumberCodeType.toNumberCodeType("DOCFORMTYPE"));
		PersistenceHelper.manager.save(numberCode);
		System.out.println("저장 완료!");
	}
}
