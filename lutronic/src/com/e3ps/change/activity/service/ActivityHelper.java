package com.e3ps.change.activity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.activity.dto.ActDTO;
import com.e3ps.change.activity.dto.DefDTO;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.service.DocumentHelper;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class ActivityHelper {

	public static final ActivityService service = ServiceFactory.getService(ActivityService.class);
	public static final ActivityHelper manager = new ActivityHelper();

	/**
	 * 루트 별 설계변경 활동 리스트
	 */
	public ArrayList<DefDTO> root() throws Exception {
		ArrayList<DefDTO> list = new ArrayList<DefDTO>();

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EChangeActivityDefinitionRoot.class, true);
		QuerySpecUtils.toOrderBy(query, idx, EChangeActivityDefinitionRoot.class,
				EChangeActivityDefinitionRoot.SORT_NUMBER, false);
		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) o[0];
			DefDTO dto = new DefDTO(def);
			list.add(dto);
		}
		return list;
	}

	/**
	 * 설계변경 활동 검색
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ActDTO> list = new ArrayList<>();
		String root = (String) params.get("root");

		if (!StringUtil.checkString(root)) {
			map.put("list", list);
			map.put("topListCount", 1);
			map.put("pageSize", 30);
			map.put("total", 0);
			map.put("sessionid", 0L);
			map.put("curPage", 1);
			return map;
		}

		EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(root);

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EChangeActivityDefinition.class, true);

		QuerySpecUtils.toEquals(query, idx, EChangeActivityDefinition.class, "rootReference.key.id", def);
		QuerySpecUtils.toOrderBy(query, idx, EChangeActivityDefinition.class, EChangeActivityDefinition.SORT_NUMBER,
				false);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EChangeActivityDefinition act = (EChangeActivityDefinition) obj[0];
			ActDTO dto = new ActDTO(act);
			list.add(dto);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 설변 활동 담기
	 */
	public Map<String, String> getActMap() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ORDER_NUMBER", "진채번");
		map.put("REVISE_BOM", "개정/BOM 변경");
		map.put("DOCUMENT", "산출물 등록");
		return map;
	}

	/**
	 * 설변 활동 JSON
	 */
	public JSONArray toJsonActMap() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> actMap = getActMap();

		Iterator it = actMap.keySet().iterator();
		while (it.hasNext()) {
			Map<String, String> map = new HashMap<>();
			String key = (String) it.next();
			String value = actMap.get(key);
			map.put("key", key);
			map.put("value", value);
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 설변 활동 타입에 맞는 설변타입명 반환
	 */
	public String getActName(String act) throws Exception {
		Map<String, String> map = getActMap();
		return map.get(act);
	}

	/**
	 * 루트에 활동이 있는지 확인, true 있음 , false 없음
	 */
	public boolean dependency(String oid) throws Exception {
		EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EChangeActivityDefinition.class, true);
		QuerySpecUtils.toEquals(query, idx, EChangeActivityDefinition.class, "rootReference.key.id", def);
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() > 0 ? true : false;
	}

	/**
	 * ECA 활동 목록 가져오기
	 */
	public ArrayList<ActDTO> activityList(String oid) throws Exception {
		ArrayList<ActDTO> list = new ArrayList<ActDTO>();
		ECOChange e = (ECOChange) CommonUtil.getObject(oid);
		ArrayList<EChangeActivity> result = colletActivity(e);

		for (EChangeActivity eca : result) {
//			ECAData data = new ECAData(eca);
//			ImageIcon icon = ChangeUtil.getECAStateImg(eca.getFinishDate(), data.state);
//			data.setIcon(icon);
//			list.add(data);
		}
		return list;
	}

	/**
	 * ECO 및 EO에 관련된 ECA 리스트
	 */
	public ArrayList<EChangeActivity> colletActivity(ECOChange eo) throws Exception {

		ArrayList<EChangeActivity> list = new ArrayList<EChangeActivity>();

		long id = eo.getPersistInfo().getObjectIdentifier().getId();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeActivity.class, true);
		int idx_n = query.appendClassList(NumberCode.class, false);

		SearchCondition sc = null;

		ClassAttribute ca = new ClassAttribute(EChangeActivity.class, EChangeActivity.STEP);
		ClassAttribute ca_n = new ClassAttribute(NumberCode.class, NumberCode.CODE);
		sc = new SearchCondition(ca, "=", ca_n);
		sc.setFromIndicies(new int[] { idx, idx_n }, 0);
		sc.setOuterJoin(0);
		query.appendWhere(sc, new int[] { idx, idx_n });

		query.appendAnd();
		query.appendWhere(new SearchCondition(EChangeActivity.class, "eoReference.key.id", SearchCondition.EQUAL, id),
				new int[] { idx });

		query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, "sort"), false), new int[] { idx_n });
		query.appendOrderBy(new OrderBy(new ClassAttribute(EChangeActivity.class, "sortNumber"), false),
				new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EChangeActivity eca = (EChangeActivity) obj[0];
			list.add(eca);
		}
		return list;
	}

	/**
	 * 접속한 사용자의 ECA 활동 리스트
	 */
	public Map<String, Object> eca(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		WTUser sessionUser = CommonUtil.sessionUser();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeActivity.class, true);
		// 관리자가 아닐경우
		if (!CommonUtil.isAdmin()) {
			QuerySpecUtils.toEqualsAnd(query, idx, EChangeActivity.class, "activeUserReference.key.id", sessionUser);
		}
		QuerySpecUtils.toEqualsAnd(query, idx, EChangeActivity.class, "state.state", "INWORK");
		QuerySpecUtils.toOrderBy(query, idx, EChangeActivity.class, EChangeActivity.CREATE_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EChangeActivity eca = (EChangeActivity) obj[0];
			EChangeOrder eo = (EChangeOrder) eca.getEo();
			Map<String, Object> data = new HashMap<>();
			data.put("oid", eca.getPersistInfo().getObjectIdentifier().getStringValue());
			data.put("number", eo.getEoNumber());
			data.put("name", eo.getEoName());
			data.put("step", eca.getStep());
			data.put("finishDate", eca.getFinishDate());
			data.put("state", eca.getLifeCycleState().getDisplay());
			data.put("activityName", getActName(eca.getActiveType()));
			data.put("activityType", eca.getActiveType());
			data.put("activityUser", eca.getActiveUser().getFullName());
			data.put("createdDate", eca.getCreateTimestamp());
			list.add(data);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 설변활동 - 산출물 링크
	 */
	public JSONArray docList(String oid) throws Exception {
		return docList((EChangeActivity) CommonUtil.getObject(oid));
	}

	/**
	 * 설변활동 - 산출물 링크
	 */
	public JSONArray docList(EChangeActivity eca) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QueryResult qr = PersistenceHelper.manager.navigate(eca, "doc", DocumentActivityLink.class, false);
		while (qr.hasMoreElements()) {
			DocumentActivityLink link = (DocumentActivityLink) qr.nextElement();
			WTDocumentMaster m = link.getDoc();
			WTDocument doc = DocumentHelper.manager.latest(m);
			Map<String, String> map = new HashMap<>();
			map.put("oid", link.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", doc.getName());
			map.put("number", doc.getNumber());
			map.put("version", doc.getVersionIdentifier().getSeries().getValue() + "."
					+ doc.getIterationIdentifier().getSeries().getValue());
			map.put("creator", doc.getCreatorFullName());
			map.put("state", doc.getLifeCycleState().getDisplay());
			map.put("createdDate_txt", doc.getCreateTimestamp().toString().substring(0, 10));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

}
