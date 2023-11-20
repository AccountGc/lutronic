package com.e3ps;

import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.CommonUtil;

import wt.part.WTPart;

public class Test {

	public static void main(String[] args) throws Exception {
		WTPart p = (WTPart) CommonUtil.getObject("wt.part.WTPart:690497");

		UwgmNDParamValue.java
		
		IBAUtils.createIBA(p, "PREORDER", "true", "b");
		System.out.println("END");
		System.exit(0);
	}
}