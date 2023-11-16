package com.e3ps;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import wt.vc.wip.WorkInProgressHelper;

public class Test {

	public static void main(String[] args) throws Exception {

		
		String a = "8010319900";

		int preNum = Integer.parseInt(a.substring(8));
		System.out.println(a.substring(0, 8));
		System.out.println(a.substring(8));
		
		DecimalFormat df = new DecimalFormat("00");

		System.exit(0);
	}
}