package com.e3ps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.PartToPartLink;
import com.e3ps.part.service.PartHelper;
import com.ptc.windchill.cadx.remove.removeResource;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;

public class Test2 {

	public static void main(String[] args) throws Exception {
		String s = "COSMETIC2401-00";
		int idx = s.lastIndexOf("-");
		System.out.println(s.substring(idx + 1));
		System.exit(0);
	}
}