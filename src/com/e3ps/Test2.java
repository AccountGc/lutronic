package com.e3ps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.PartToPartLink;
import com.e3ps.part.service.PartHelper;
import com.ptc.windchill.cadx.remove.removeResource;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.FileUtil;

public class Test2 {

	public static void main(String[] args) throws Exception {

		ArrayList<String> list = new ArrayList();
		list.add("NP21");
		String s = "NP21";

		if (!list.contains(s)) {
			list.add(s);
		}

		System.out.println(list);

	}
}