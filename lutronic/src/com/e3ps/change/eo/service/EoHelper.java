package com.e3ps.change.eo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.eo.column.EoColumn;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.part.column.PartColumn;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class EoHelper {

	public static final EoService service = ServiceFactory.getService(EoService.class);
	public static final EoHelper manager = new EoHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<EoColumn> list = new ArrayList<>();

		ArrayList<Map<String, String>> rows104 = (ArrayList<Map<String, String>>) params.get("rows104");

		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String eoType = (String) params.get("eoType");

		String predate = (String) params.get("predate");
		String postdate = (String) params.get("postdate");

		String creator = (String) params.get("creator");
		String state = (String) params.get("state");

		String licensing = (String) params.get("licensing");

		String model = (String) params.get("model");

		String sortCheck = (String) params.get("sortCheck");
		String sortValue = (String) params.get("sortValue");
		String riskType = (String) params.get("riskType");
		String preApproveDate = (String) params.get("preApproveDate");
		String postApproveDate = (String) params.get("postApproveDate");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeOrder.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_NAME, name);

		QuerySpecUtils.toLikeAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_NUMBER, number);

		QuerySpecUtils.toTimeGreaterAndLess(query, idx, EChangeOrder.class, EChangeOrder.CREATE_TIMESTAMP, predate,
				postdate);

		QuerySpecUtils.toTimeGreaterAndLess(query, idx, EChangeOrder.class, EChangeOrder.EO_APPROVE_DATE,
				preApproveDate, postApproveDate);

		// 등록자
//		if (creator.length() > 0) {
//			if (qs.getConditionCount() > 0) {
//				qs.appendAnd();
//			}
//			People pp = (People) CommonUtil.getObject(creator);
//			long longOid = CommonUtil.getOIDLongValue(pp.getUser());
//			qs.appendWhere(new SearchCondition(ecoClass, "creator.key.id", SearchCondition.EQUAL, longOid),
//					new int[] { ecoIdx });
//		}

		if (StringUtil.checkString(eoType)) {
			QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, eoType);
		} else {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendOpenParen();
			QuerySpecUtils.toEquals(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, "DEV");
			query.appendOr();
			QuerySpecUtils.toEquals(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, "PRODUCT");
			query.appendCloseParen();
		}

		// 인허가 구분
//		if (licensing.length() > 0) {
//			if (qs.getConditionCount() > 0) {
//				qs.appendAnd();
//			}
//			if (licensing.equals("NONE")) {
//				qs.appendWhere(
//						new SearchCondition(ecoClass, EChangeOrder.LICENSING_CHANGE, SearchCondition.IS_NULL, true),
//						new int[] { ecoIdx });
//			} else {
//				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LICENSING_CHANGE, SearchCondition.EQUAL,
//						licensing, false), new int[] { ecoIdx });
//			}
//
//		}

		// 인허가 구분
//		if (riskType.length() > 0) {
//			if (qs.getConditionCount() > 0) {
//				qs.appendAnd();
//			}
//			if (riskType.equals("NONE")) {
//				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.RISK_TYPE, SearchCondition.IS_NULL, true),
//						new int[] { ecoIdx });
//			} else {
//				qs.appendWhere(
//						new SearchCondition(ecoClass, EChangeOrder.RISK_TYPE, SearchCondition.EQUAL, riskType, false),
//						new int[] { ecoIdx });
//			}
//
//		}

		QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.MODEL, model);

		if (rows104.size() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			int idx_p = query.appendClassList(EOCompletePartLink.class, false);
			query.appendWhere(
					new SearchCondition(EChangeOrder.class, "thePersistInfo.theObjectIdentifier.id",
							EOCompletePartLink.class, EOCompletePartLink.ROLE_BOBJECT_REF + ".key.id"),
					new int[] { idx, idx_p });
			query.appendAnd();
			query.appendOpenParen();
			for (int i = 0; i < rows104.size(); i++) {
				Map<String, String> row = (Map<String, String>) rows104.get(i);
				if (i != 0) {
					query.appendOr();
				}
				String oid = row.get("oid");
				WTPart part = (WTPart) CommonUtil.getObject(oid);
				long ids = part.getMaster().getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(new SearchCondition(EOCompletePartLink.class,
						EOCompletePartLink.ROLE_AOBJECT_REF + ".key.id", SearchCondition.EQUAL, ids),
						new int[] { idx_p });
			}
			query.appendCloseParen();
		}

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EoColumn data = new EoColumn(obj);
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
	 * EO와 연결된 완제품 리스트
	 */
	public ArrayList<EOCompletePartLink> completeParts(Object obj) throws Exception {
		ArrayList<EOCompletePartLink> list = new ArrayList<EOCompletePartLink>();
		QueryResult result = null;
		if (obj instanceof EChangeOrder) {
			EChangeOrder eo = (EChangeOrder) obj;
			result = PersistenceHelper.manager.navigate(eo, "completePart", EOCompletePartLink.class, false);
		} else if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			result = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EOCompletePartLink.class, false);
		}

		while (result.hasMoreElements()) {
			EOCompletePartLink link = (EOCompletePartLink) result.nextElement();
			list.add(link);
		}
		return list;
	}

	/**
	 * 진행중인 EO에 완제품이 있는지
	 */
	public Map<String, Object> checkerCompletePart(WTPart part) throws Exception {
		ArrayList<EOCompletePartLink> list = completeParts(part);
		Map<String, Object> map = new HashMap<>();
		for (EOCompletePartLink link : list) {
			EChangeOrder eo = link.getEco();

			// ECO 경우 패스
			if (eo.getEoType().equals("CHANGE")) {
				continue;
			}

			// 승인된 EO 혹은 취소된 EO 패스
			String state = eo.getState().toString();
			if (state.equals("APPROVED") || state.equals("CANCELLED")) {
				continue;
			}

			String msg = part.getNumber() + " 품목이 EO = " + eo.getEoNumber() + "에서 작업중인 업무가 있습니다.";
			map.put("msg", msg);
			map.put("result", false);
			return map;
		}
		map.put("result", true);
		return map;
	}

	/**
	 * ECO에서 진행중인 품목인지
	 */
	public Map<String, Object> validatePart(WTPart part) throws Exception {
		Map<String, Object> map = new HashMap<>();
		QueryResult result = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class);
		while (result.hasMoreElements()) {
			EChangeOrder eco = (EChangeOrder) result.nextElement();
			String state = eco.getState().toString();
			if (state.equals("APPROVED") || state.equals("CANCELLED")) {
				continue;
			}
			String msg = part.getNumber() + " 품목이 ECO = " + eco.getEoNumber() + "에서 작업중인 업무가 있습니다.";
			map.put("msg", msg);
			map.put("result", false);
			return map;
		}
		map.put("result", true);
		return map;
	}

	/**
	 * EO 관련 객체
	 */
	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		EChangeOrder eo = (EChangeOrder) CommonUtil.getObject(oid);
		if ("doc".equalsIgnoreCase(type)) {
			// 문서
			return JSONArray.fromObject(referenceDoc(eo, list));
		} else if ("part".equalsIgnoreCase(type)) {
			// 완제품
			return JSONArray.fromObject(referencePart(eo, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * EO 관련 문서
	 */
	private Object referenceDoc(EChangeOrder eo, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eo, "document", DocumentEOLink.class);
		while (result.hasMoreElements()) {
			WTDocument doc = (WTDocument) result.nextElement();
			DocumentColumn dto = new DocumentColumn(doc);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * EO 관련 완제품
	 */
	private Object referencePart(EChangeOrder eo, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eo, "completePart", EOCompletePartLink.class);
		while (result.hasMoreElements()) {
			WTPart part = (WTPart) result.nextElement();
			PartColumn dto = new PartColumn(part);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * 모델명 복수개로 인해서 처리 하는 함수
	 */
	public String displayToModel(String model) throws Exception {
		String display = "";
		String[] ss = model.split(",");
		for (int i = 0; i < ss.length; i++) {
			String s = ss[i];
			if (ss.length - 1 == i) {
				display += NumberCodeHelper.manager.getNumberCodeName(s, "MODEL");
			} else {
				display += NumberCodeHelper.manager.getNumberCodeName(s, "MODEL") + ",";
			}
		}
		return display;
	}
}