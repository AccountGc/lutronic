package com.e3ps;

import java.util.ArrayList;

import com.e3ps.common.util.CommonUtil;

import wt.part.WTPart;

public class Test2 {

	public static void main(String[] args) throws Exception {

		WTPart part1 = (WTPart) CommonUtil.getObject("wt.part.WTPart:1414096");
		WTPart part2 = (WTPart) CommonUtil.getObject("wt.part.WTPart:1131060");
		WTPart part3 = (WTPart) CommonUtil.getObject("wt.part.WTPart:1131061");
		WTPart part4 = (WTPart) CommonUtil.getObject("wt.part.WTPart:1131062");
		WTPart part5 = (WTPart) CommonUtil.getObject("wt.part.WTPart:1131063");
		WTPart part6 = (WTPart) CommonUtil.getObject("wt.part.WTPart:1131064");
		WTPart part7 = (WTPart) CommonUtil.getObject("wt.part.WTPart:1131065");
		WTPart part8 = (WTPart) CommonUtil.getObject("wt.part.WTPart:1131066");

		ArrayList<WTPart> list1 = new ArrayList<>();
		ArrayList<WTPart> list2 = new ArrayList<>();

		list1.add(part1);
		list1.add(part2);
		list1.add(part3);
		list1.add(part4);
		list1.add(part5);
		list1.add(part6);
		list1.add(part7);

		list1.add(part1);
		list1.add(part2);
		list1.add(part3);
		list1.add(part4);
		list1.add(part8);

		
        for (WTPart part : list1) {
            if (!list2.contains(part)) {
                list2.add(part);
            }
        }
        
        
        System.out.println(list2.size());
        System.exit(0);
		
	}
}
