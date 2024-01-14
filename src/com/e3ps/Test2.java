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

		byte[] s = Base64.decodeBase64(
				"creoview://?wcparams=eyJhdHRyaWJ1dGVzIjp7InVybGJhc2UiOiJodHRwOi8vcGxtZGV2Lmx1dHJvbmljLmNvbS9XaW5kY2hpbGwiLCJzZXNzaW9uaWQiOiJ1OFlTalhkNnFmVG1qbGExUnVZZzhuSDdrUjQuMjNwIiwidXNlcmlkIjoid2NhZG1pbiJ9LCJpZCI6ImN2In0=");
		System.out.println(s);

		System.exit(0);
	}
}