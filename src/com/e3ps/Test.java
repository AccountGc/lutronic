package com.e3ps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.PartToPartLink;
import com.e3ps.part.service.PartHelper;

import wt.fc.PersistenceHelper;
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
		String afterOid = "wt.part.WTPart:1474443";
		String preOid = "wt.part.WTPart:1474233";
		WTPart afterPart = (WTPart) CommonUtil.getObject(afterOid);
		WTPart prePart = (WTPart) CommonUtil.getObject(preOid);
		ArrayList<WTPart> removeList = removeList(prePart, afterPart);
		ArrayList<WTPart> addList = addList(prePart, afterPart);

		for (WTPart remove : removeList) {
			System.out.println("remove=" + remove.getNumber());
		}

		for (WTPart add : addList) {
			System.out.println("add=" + add.getNumber());
		}
		
		
		
	}

	private static ArrayList<WTPart> addList(WTPart prePart, WTPart afterPart) throws Exception {
		ArrayList<Object[]> afters = descendants(afterPart);
		ArrayList<Object[]> pres = descendants(prePart);

		ArrayList<Map<String, Object>> mergedList = new ArrayList<>();
		ArrayList<Map<String, Object>> compList = new ArrayList<Map<String, Object>>();

		ArrayList<WTPart> removeList = new ArrayList<WTPart>();

		for (Object[] pre : pres) {
			Map<String, Object> mergedData = new HashMap<>();
			WTPart a = (WTPart) pre[1];
			mergedData.put("number", a.getNumber());
			mergedData.put("oid", a.getPersistInfo().getObjectIdentifier().getStringValue());
			mergedList.add(mergedData);
		}

		for (Object[] after : afters) {
			WTPart b = (WTPart) after[1];
			String key = b.getPersistInfo().getObjectIdentifier().getStringValue();
			boolean isExist = false;

			for (Map<String, Object> mergedData : mergedList) {
				String oid = (String) mergedData.get("oid");
				String _key = oid;
				if (key.equals(_key)) {
					isExist = true;
					break;
				}
			}

			if (!isExist) {
				// partNo가 동일한 데이터가 없으면 mergedList에 데이터를 추가
				Map<String, Object> mergedData = new HashMap<>();
				mergedData.put("oid", key);
				mergedData.put("number", b.getNumber());
				compList.add(mergedData);
			}
		}

		for (Map<String, Object> m : compList) {
			String oid = (String) m.get("oid");
			WTPart pp = (WTPart) CommonUtil.getObject(oid);

			QueryResult qr = PersistenceHelper.manager.navigate(pp.getMaster(), "prev", PartToPartLink.class);
			if (qr.size() > 0) {
				continue;
			}
			removeList.add(pp);
		}
		return removeList;
	}

	private static ArrayList<WTPart> removeList(WTPart prePart, WTPart afterPart) throws Exception {
		ArrayList<Object[]> afters = descendants(afterPart);
		ArrayList<Object[]> pres = descendants(prePart);

		ArrayList<Map<String, Object>> mergedList = new ArrayList<>();
		ArrayList<Map<String, Object>> compList = new ArrayList<Map<String, Object>>();

		ArrayList<WTPart> removeList = new ArrayList<WTPart>();

		for (Object[] after : afters) {
			Map<String, Object> mergedData = new HashMap<>();
			WTPart a = (WTPart) after[1];
			mergedData.put("number", a.getNumber());
			mergedData.put("oid", a.getPersistInfo().getObjectIdentifier().getStringValue());
			mergedList.add(mergedData);
		}

		for (Object[] pre : pres) {
			WTPart b = (WTPart) pre[1];
			String key = b.getPersistInfo().getObjectIdentifier().getStringValue();
			boolean isExist = false;

			for (Map<String, Object> mergedData : mergedList) {
				String oid = (String) mergedData.get("oid");
				String _key = oid;
				if (key.equals(_key)) {
					isExist = true;
					break;
				}
			}

			if (!isExist) {
				// partNo가 동일한 데이터가 없으면 mergedList에 데이터를 추가
				Map<String, Object> mergedData = new HashMap<>();
				mergedData.put("oid", key);
				mergedData.put("number", b.getNumber());
				compList.add(mergedData);
			}
		}

		for (Map<String, Object> m : compList) {
			String oid = (String) m.get("oid");
			WTPart pp = (WTPart) CommonUtil.getObject(oid);

			QueryResult qr = PersistenceHelper.manager.navigate(pp.getMaster(), "after", PartToPartLink.class);
			if (qr.size() > 0) {
				continue;
			}
			removeList.add(pp);
		}
		return removeList;
	}

	public static ArrayList<Object[]> descendants(WTPart part) throws Exception {
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		// root 추가
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