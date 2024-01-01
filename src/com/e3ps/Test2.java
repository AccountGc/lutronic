package com.e3ps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.PartToPartLink;
import com.e3ps.part.service.PartHelper;
import com.ptc.windchill.cadx.remove.removeResource;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;

public class Test2 {

	public static void main(String[] args) throws Exception {
		WTPart right = (WTPart) CommonUtil.getObject("wt.part.WTPart:1450627");
		WTPart left = (WTPart) CommonUtil.getObject("wt.part.WTPart:1450485");

		ArrayList<Map<String, Object>> mergedList = new ArrayList<>();

		ArrayList<Map<String, Object>> deleteList = new ArrayList<>();
		ArrayList<WTPart> rights = PartHelper.manager.descendants(right);
		ArrayList<WTPart> lefts = PartHelper.manager.descendants(left);
		
		
		ArrayList<WTPart> addList = addList(lefts, rights);
		ArrayList<WTPart> removeList = removeList(lefts, rights);
		
				
				
				
		

		System.exit(0);
	}

	private static ArrayList<WTPart> removeList(ArrayList<WTPart> lefts, ArrayList<WTPart> rights) throws Exception {
		ArrayList<Map<String, Object>> mergedList = new ArrayList<>();
		ArrayList<Map<String, Object>> compList = new ArrayList<Map<String, Object>>();

		ArrayList<WTPart> removeList = new ArrayList<WTPart>();

		for (WTPart l : rights) {
			Map<String, Object> mergedData = new HashMap<>();
			mergedData.put("number", l.getNumber());
			mergedData.put("oid", l.getPersistInfo().getObjectIdentifier().getStringValue());
			mergedList.add(mergedData);
		}

		for (WTPart r : lefts) {
			String key = r.getPersistInfo().getObjectIdentifier().getStringValue();
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
				mergedData.put("number", r.getNumber());
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
	
	private static ArrayList<WTPart> addList(ArrayList<WTPart> lefts, ArrayList<WTPart> rights) throws Exception {
		ArrayList<Map<String, Object>> mergedList = new ArrayList<>();
		ArrayList<Map<String, Object>> compList = new ArrayList<Map<String, Object>>();

		ArrayList<WTPart> addList = new ArrayList<WTPart>();

		for (WTPart l : lefts) {
			Map<String, Object> mergedData = new HashMap<>();
			mergedData.put("number", l.getNumber());
			mergedData.put("oid", l.getPersistInfo().getObjectIdentifier().getStringValue());
			mergedList.add(mergedData);
		}

		for (WTPart r : rights) {
			String key = r.getPersistInfo().getObjectIdentifier().getStringValue();
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
				mergedData.put("number", r.getNumber());
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
			addList.add(pp);
		}
		return addList;
	}
}