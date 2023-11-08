package com.e3ps;

import java.sql.Timestamp;
import java.util.Date;

public class Test {

	public static void main(String[] args) throws Exception {

		Timestamp t = new Timestamp(new Date().getTime());
		System.out.println(t);
		
//		// String decryptedPass = WTKeyStoreUtil.decryptProperty(propertyValue,
//		// "encrypted." + propertyValue, ));
//		String s = WTKeyStoreUtil.decryptProperty("wt.pom.dbPassword", "encrypted.wt.pom.dbPassword",
//				WAUtil.getWTHome());
//		System.out.println(s);

		System.exit(0);
	}
}