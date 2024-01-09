package com.e3ps;

import java.util.ArrayList;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.service.PartHelper;

import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class Test {

	public static void main(String[] args) throws Exception {

		String after = "wt.part.WTPart:1474443";

		String pre = "wt.part.WTPart:1474233";
		WTPart afterPart = (WTPart) CommonUtil.getObject(after);
		WTPart prePart = (WTPart) CommonUtil.getObject(pre);

		ArrayList<WTPart> preList = descendants(prePart);
		for (WTPart p : preList) {
			System.out.println(p.getNumber());
		}
	}

	public static ArrayList<Object[]> descendants(WTPart part) throws Exception {
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		// root 추가
		list.add(part);
		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			list.add(obj);
			descendants(p, list);
		}
		return list;
	}

	private static void descendants(WTPart part, ArrayList<Object[]> list) throws Exception {
		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			list.add(obj);
			descendants(p, list);
		}
	}

}