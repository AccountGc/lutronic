package com.e3ps;

import java.sql.Timestamp;
import java.util.Date;

public class Test {

	public static void main(String[] args) throws Exception {
//		WTPart p = (WTPart) CommonUtil.getObject("wt.part.WTPart:1278829");
//
//		IBAUtils.createIBA(p, "SPECIFICATION", "사양 SAP 전달", "s");
//		IBAUtils.createIBA(p, "WEIGHT", "2", "f");
//
//		System.out.println("종료");

		String today = new Timestamp(new Date().getTime()).toString().substring(0, 10);
		System.out.println(today);
		String suffixYear = today.substring(2, 4);
		System.out.println(year);
		String month = today.substring(5, 7);
		System.out.println(month);
		
		System.exit(0);
	}
}