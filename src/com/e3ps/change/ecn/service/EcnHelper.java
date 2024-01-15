package com.e3ps.change.ecn.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcnToPartLink;
import com.e3ps.change.PartToSendLink;
import com.e3ps.change.ecn.column.EcnColumn;
import com.e3ps.change.util.EcnComparator;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.part.service.StandardPartService;

import net.sf.json.JSONArray;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.StandardWTPartService;
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

		boolean isAdmin = CommonUtil.isAdmin();
		People people = CommonUtil.sessionPeople();
		PeopleDTO dto = new PeopleDTO(people);
		String department_name = dto.getDepartment_name();

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
		QuerySpecUtils.toCreatorQuery(query, idx, EChangeNotice.class, creatorOid);
		QuerySpecUtils.toLikeAnd(query, idx, EChangeNotice.class, EChangeNotice.MODEL, model);

		boolean isRA = false;
		if ("".equals(department_name)) {
			isRA = true;
		}
		// 관리자 아니고 RA 부서 일경우
		if (!CommonUtil.isAdmin() && isRA) {
			System.out.println("A");
			WTUser sessionUser = CommonUtil.sessionUser();
			QuerySpecUtils.toEquals(query, idx, EChangeNotice.class, "workerReference.key.id", sessionUser);
		}

		QuerySpecUtils.toOrderBy(query, idx, EChangeNotice.class, EChangeNotice.MODIFY_TIMESTAMP, true);

		System.out.println(query);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EcnColumn data = new EcnColumn(obj);
			data.setRowNum(rowNum++);
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
	 * 국가별 SAP 전송 이력 데이터 가져오기
	 */
	public PartToSendLink getSendLink(EChangeNotice ecn, WTPart part, String nation, EChangeRequest cr)
			throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartToSendLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, PartToSendLink.class, "partReference.key.id", part);
		QuerySpecUtils.toEqualsAnd(query, idx, PartToSendLink.class, "ecnReference.key.id", ecn);
		QuerySpecUtils.toEqualsAnd(query, idx, PartToSendLink.class, "ecrReference.key.id", cr);
		QuerySpecUtils.toEqualsAnd(query, idx, PartToSendLink.class, PartToSendLink.NATION, nation);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartToSendLink link = (PartToSendLink) obj[0];
			return link;

		}
		return null;
	}

	/**
	 * ECN 관련 대상 품목
	 */
	public JSONArray viewData(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		EChangeNotice ecn = (EChangeNotice) CommonUtil.getObject(oid);
		EChangeOrder eco = ecn.getEco();
		// 1차적으로 CR 단위로 가져온다

		QueryResult qr = PersistenceHelper.manager.navigate(ecn, "part", EcnToPartLink.class, false);
		while (qr.hasMoreElements()) {
			EcnToPartLink link = (EcnToPartLink) qr.nextElement();
			EChangeRequest cr = link.getEcr();
			WTPart part = link.getPart();

			Map<String, Object> map = new HashMap<>();

			map.put("oid", link.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("workEnd", link.getWorkEnd());
//			map.put("rate", Math.round(link.getRate()));
			map.put("partName", ecn.getPartName());
			map.put("partNumber", ecn.getPartNumber());
			map.put("ecoNumber", eco.getEoNumber());
			map.put("eoid", eco.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("coid", cr.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("crNumber", cr.getEoNumber());
			map.put("poid", part.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("number", part.getNumber());
			map.put("name", part.getName());
			map.put("version", part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue());

			ArrayList<Map<String, String>> countrys = NumberCodeHelper.manager.getCountry();
			for (Map<String, String> country : countrys) {
				String code = country.get("code");
				PartToSendLink sendLink = getSendLink(ecn, part, code, cr);
				if (sendLink != null) {
					String ss = sendLink.getSendDate().toString().substring(0, 10);
					if ("3000-12-31".equals(ss)) {
						map.put(code + "_date", "N/A");
					} else {
						map.put(code + "_date", sendLink.getSendDate().toString().substring(0, 10));
					}

					map.put(code + "_isSend", sendLink.getIsSend());
				} else {
					map.put(code + "_date", "");
					map.put(code + "_isSend", false);
				}
			}

			list.add(map);
		}
		Collections.sort(list, new EcnComparator());
		return JSONArray.fromObject(list);
	}

	/**
	 * EcnToPartLink 가져오기
	 */
	public EcnToPartLink getEcnToPartLink(EChangeNotice ecn, WTPart part, EChangeRequest cr) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EcnToPartLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, EcnToPartLink.class, "roleAObjectRef.key.id", ecn);
		QuerySpecUtils.toEqualsAnd(query, idx, EcnToPartLink.class, "roleBObjectRef.key.id", part);
		QuerySpecUtils.toEqualsAnd(query, idx, EcnToPartLink.class, "ecrReference.key.id", cr);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EcnToPartLink link = (EcnToPartLink) obj[0];
			return link;
		}
		return null;
	}
}
