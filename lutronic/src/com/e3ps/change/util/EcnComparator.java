package com.e3ps.change.util;

import java.util.Comparator;
import java.util.Map;

public class EcnComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {

		Map<String, Object> obj1 = (Map<String, Object>) o1;
		Map<String, Object> obj2 = (Map<String, Object>) o2;

		String crNumber1 = (String) obj1.get("crNumber");
		String crNumber2 = (String) obj2.get("crNumber");

		return crNumber1.compareTo(crNumber2);
	}

}
