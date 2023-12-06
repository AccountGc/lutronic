package com.e3ps.rohs.util;

import java.util.Comparator;
import java.util.Map;

public class RohsPartComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		Map<String, Object> map1 = (Map<String, Object>) o1;
		Map<String, Object> map2 = (Map<String, Object>) o2;
		String level1 = (String) map1.get("level");
		String level2 = (String) map2.get("level");
		return level1.compareTo(level2);
	}
}
