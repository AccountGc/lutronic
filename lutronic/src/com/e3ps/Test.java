package com.e3ps;

import java.sql.Timestamp;
import java.util.Date;

public class Test {

	public static void main(String[] args) throws Exception {

		Timestamp t = new Timestamp(new Date().getTime());
		System.out.println(t.toString().substring(0, 10).replaceAll("-", ""));

		System.exit(0);
	}
}