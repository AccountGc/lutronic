package com.e3ps.change.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.amazonaws.services.route53domains.model.ContactType;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.service.PartHelper;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;

/**
 * 설변관련 공통으로 사용된 함수 모음
 */
public class EChangeUtils {

	public static final EChangeUtils manager = new EChangeUtils();

	/**
	 * 중복 제거한 베이스 라인 가져오기??
	 */
	public ArrayList<Map<String, String>> getBaseline(String oid) throws Exception {
		ArrayList<Map<String, String>> list = arrayBaseLine(oid);
		ArrayList<Map<String, String>> baseLine = new ArrayList<Map<String, String>>();
		Map<String, String> tempMap = new HashMap<String, String>();
		for (Map<String, String> data : list) {
			Map<String, String> groupMap = new HashMap<String, String>();
			String baseLine_name = data.get("baseLine_name");
			String temp_name = baseLine_name.substring(0, baseLine_name.indexOf(":"));
			String baseLine_oid = data.get("baseLine_oid");
			String part_oid = data.get("part_oid");
			System.out.println(baseLine_name + " , " + temp_name + tempMap.containsKey(temp_name));
			if (tempMap.containsKey(temp_name)) {
				continue;
			} else {
				tempMap.put(temp_name, temp_name);
				groupMap.put("baseLine_name", temp_name);
				groupMap.put("baseLine_oid", baseLine_oid);
				groupMap.put("part_oid", part_oid);
			}
			baseLine.add(groupMap);
		}
		return baseLine;
	}

	/**
	 * 부품과 관련된 베이스 라인 가져오는 함수
	 */
	private ArrayList<Map<String, String>> arrayBaseLine(String oid) throws Exception {
		WTPart part = (WTPart) CommonUtil.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx_l = query.appendClassList(ManagedBaseline.class, true);
		int idx_m = query.appendClassList(BaselineMember.class, false);
		int idx_p = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toInnerJoin(query, ManagedBaseline.class, BaselineMember.class,
				"thePersistInfo.theObjectIdentifier.id", "roleAObjectRef.key.id", idx_l, idx_m);
		QuerySpecUtils.toInnerJoin(query, BaselineMember.class, WTPart.class, "roleBObjectRef.key.id",
				"thePersistInfo.theObjectIdentifier.id", idx_m, idx_p);
		QuerySpecUtils.toEqualsAnd(query, idx_p, WTPart.class, "masterReference.key.id", part.getMaster());
		QuerySpecUtils.toOrderBy(query, idx_l, ManagedBaseline.class, ManagedBaseline.CREATE_TIMESTAMP, true);

		QueryResult result = PersistenceHelper.manager.find(query);

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ManagedBaseline baseLine = (ManagedBaseline) obj[0];
			WTPart p = (WTPart) obj[1];
			Map<String, String> map = new HashMap<String, String>();
			map.put("part_oid", p.getPersistInfo().getObjectIdentifier().toString());
			map.put("baseLine_oid", baseLine.getPersistInfo().getObjectIdentifier().toString());
			map.put("baseLine_name", baseLine.getName());
			list.add(map);
		}
		return list;
	}

	/**
	 * 베이스라인에 처리된 부품 목록
	 */
	public void collectBaseLineParts(WTPart part, Vector v) throws Exception {
		ArrayList<WTPart> list = PartHelper.manager.descentsPart(part, (View) part.getView().getObject());

		for (WTPart p : list) {
			String state = p.getLifeCycleState().toString();
			boolean isApproved = state.equals("APPROVED");
			// 승인된거 패스
			if (isApproved) {
				continue;
			}

			WTPart prev = null;
			prev = (WTPart) VersionControlHelper.service.predecessorOf(p);

			if (prev == null) {
				prev = p;
			}
			v.add(prev);
			collectBaseLineParts(prev.v);
		}
	}
}
