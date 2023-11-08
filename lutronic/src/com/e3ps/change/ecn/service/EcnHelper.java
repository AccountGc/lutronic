package com.e3ps.change.ecn.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EcnToPartLink;
import com.e3ps.change.PartToSendLink;
import com.e3ps.change.ecn.column.EcnColumn;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;

import net.sf.json.JSONArray;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class EcnHelper {

	public static final EcnService service = ServiceFactory.getService(EcnService.class);
	public static final EcnHelper manager = new EcnHelper();

	/**
	 * ECN 검색 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<EcnColumn> list = new ArrayList<>();

		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String creatorOid = (String) params.get("creatorOid");
		String model = (String) params.get("model");
		String state = (String) params.get("state");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeNotice.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, EChangeNotice.class, EChangeNotice.EO_NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, EChangeNotice.class, EChangeNotice.EO_NUMBER, number);
		QuerySpecUtils.toState(query, idx, EChangeNotice.class, state);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, EChangeNotice.class, EChangeNotice.CREATE_TIMESTAMP,
				createdFrom, createdTo);
		QuerySpecUtils.toCreator(query, idx, EChangeNotice.class, creatorOid);
		QuerySpecUtils.toLikeAnd(query, idx, EChangeNotice.class, EChangeNotice.MODEL, model);
		QuerySpecUtils.toOrderBy(query, idx, EChangeNotice.class, EChangeNotice.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EcnColumn data = new EcnColumn(obj);
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
	 * ECN 이랑 그룹핑된 품목정보
	 */
	public JSONArray getEcnGroupPart(EChangeNotice ecn) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		EChangeOrder eco = ecn.getEco();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EcnToPartLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, EcnToPartLink.class, "roleAObjectRef.key.id", ecn);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EcnToPartLink link = (EcnToPartLink) obj[0];
			WTPart part = link.getPart();
			Map<String, Object> map = new HashMap<>();

			map.put("oid", ecn.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("part_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("ecoNumber", eco.getEoNumber());
			map.put("number", part.getNumber());
			map.put("name", part.getName());
			map.put("version", part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue());
			map.put("state", part.getLifeCycleState().getDisplay());

			ArrayList<Map<String, String>> countrys = NumberCodeHelper.manager.getCountry();
			for (Map<String, String> country : countrys) {
				String dataField = country.get("code");
				PartToSendLink sLink = getSendLink(ecn, part, dataField);
				if (sLink != null) {
					map.put(dataField + "_isSend", sLink.getIsSend());
					map.put(dataField + "_date", sLink.getSendDate().toString().substring(0, 10));
				}
			}

			list.add(map);
		}

		return JSONArray.fromObject(list);
	}

	/**
	 * 국가별 SAP 전송 이력 데이터 가져오기
	 */
	private PartToSendLink getSendLink(EChangeNotice ecn, WTPart part, String nation) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartToSendLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, PartToSendLink.class, "partReference.key.id", part);
		QuerySpecUtils.toEqualsAnd(query, idx, PartToSendLink.class, "ecnReference.key.id", ecn);
		QuerySpecUtils.toEqualsAnd(query, idx, PartToSendLink.class, PartToSendLink.NATION, nation);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartToSendLink link = (PartToSendLink) obj[0];
			return link;

		}
		return null;
	}

}
