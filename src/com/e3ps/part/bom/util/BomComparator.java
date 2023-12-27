package com.e3ps.part.bom.util;

import java.util.Comparator;

import net.sf.json.JSONObject;

public class BomComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		JSONObject node1 = (JSONObject) o1;
		JSONObject node2 = (JSONObject) o2;

		String number1 = (String) node1.get("number");
		String number2 = (String) node2.get("number");
		return number1.compareTo(number2);
	}
}
