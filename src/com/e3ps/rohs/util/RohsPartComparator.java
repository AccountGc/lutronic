package com.e3ps.rohs.util;

import java.util.Comparator;
import java.util.Map;

public class RohsPartComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		Map<String, Object> map1 = (Map<String, Object>) o1;
		Map<String, Object> map2 = (Map<String, Object>) o2;
		int level1 = (int) map1.get("level");
		int level2 = (int) map2.get("level");
		return level1 - level2;
	}
}
