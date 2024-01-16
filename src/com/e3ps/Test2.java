package com.e3ps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.PartToPartLink;
import com.e3ps.part.service.PartHelper;
import com.ptc.windchill.cadx.remove.removeResource;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;

public class Test2 {

	public static void main(String[] args) throws Exception {

		String s = ",CP,NE11,RC09";
		String[] ss = s.split(",");
		for (String a : ss) {
			System.out.println(a);
		}

		System.exit(0);
	}
}