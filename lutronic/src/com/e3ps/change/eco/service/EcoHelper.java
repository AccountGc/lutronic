package com.e3ps.change.eco.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.eco.column.EcoColumn;
import com.e3ps.change.eo.column.EoColumn;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.service.PartHelper;

import wt.fc.PagingQueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class EcoHelper {

	public static final EcoService service = ServiceFactory.getService(EcoService.class);
	public static final EcoHelper manager = new EcoHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {

		Map<String, Object> map = new HashMap<>();
		ArrayList<EcoColumn> list = new ArrayList<>();

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
			QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, "CHANGE");
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

//		if (sortValue != null && sortValue.length() > 0) {
//			// System.out.println("sortCheck="+sortCheck+"\tsortValue="+sortValue);
//			if ("true".equals(sortCheck)) {
//
//				if (!"creator.key.id".equals(sortValue)) {
//					if (!"PROCESSDATE".equals(sortValue)) {
//						qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeOrder.class, sortValue), true),
//								new int[] { ecoIdx });
//					}
//				} else {
//
//					if (qs.getConditionCount() > 0)
//						qs.appendAnd();
//					int idx_user = qs.appendClassList(WTUser.class, false);
//					int idx_people = qs.appendClassList(People.class, false);
//
//					ClassAttribute ca = new ClassAttribute(EChangeOrder.class, "creator.key.id");
//					ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
//					qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[] { ecoIdx, idx_user });
//					ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
//					qs.appendAnd();
//					qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
//					SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort", true);
//				}
//
//			} else {
//
//				if (!"creator.key.id".equals(sortValue)) {
//					if (!"PROCESSDATE".equals(sortValue)) {
//						qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeOrder.class, sortValue), false),
//								new int[] { ecoIdx });
//					}
//				} else {
//
//					if (qs.getConditionCount() > 0)
//						qs.appendAnd();
//					int idx_user = qs.appendClassList(WTUser.class, false);
//					int idx_people = qs.appendClassList(People.class, false);
//
//					ClassAttribute ca = new ClassAttribute(EChangeOrder.class, "creator.key.id");
//					ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
//					qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[] { ecoIdx, idx_user });
//					ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
//					qs.appendAnd();
//					qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
//					SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort", false);
//				}
//			}
//		} else {
//			qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass, EChangeOrder.EO_APPROVE_DATE), true),
//					new int[] { ecoIdx });
//			qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass, EChangeOrder.CREATE_TIMESTAMP), true),
//					new int[] { ecoIdx });
//		}

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EcoColumn data = new EcoColumn(obj);
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
	 * 설변관련 대상들 가져오기
	 */
	public Map<String, Object> dataMap(ArrayList<Map<String, String>> rows500) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<WTPart> clist = new ArrayList<WTPart>(); // 설변 대상 리스트
		ArrayList<WTPart> plist = new ArrayList<WTPart>(); // 완제품 리스트
		ArrayList<String> mlist = new ArrayList<String>(); // 제품 리스트
		String model = "";
		for (Map<String, String> row500 : rows500) {
			String part_oid = row500.get("part_oid");
			WTPart part = (WTPart) CommonUtil.getObject(part_oid);
			clist.add(part);

			// 제품명 담기
			model = putModel(model, part, mlist);

			plist = PartHelper.manager.collectEndItem(part, plist);
		}

		for (int i = 0; i < plist.size(); i++) {
			WTPart pp = (WTPart) plist.get(i);

			if (isSkip(pp)) {
				// 그냥 여기서 제외 하면 되는거 아닌가?
				plist.remove(i);
				continue;
			}
			putModel(model, pp, mlist);
		}
		map.put("model", model);
		map.put("plist", plist);
		map.put("clist", clist);
		return map;
	}

	/**
	 * 제품명 담기
	 */
	private String putModel(String model, WTPart part, ArrayList<String> mlist) throws Exception {
		String m = IBAUtil.getAttrValue(part, "MODEL");
		if (!mlist.contains(m)) {
			model = model + "," + m;
			mlist.add(m);
		}
		return model;
	}

	/**
	 * 설변 대상 품목 제외
	 */
	public boolean isSkip(WTPart pp) throws Exception {
		String version = pp.getVersionIdentifier().getSeries().getValue();
		String state = pp.getLifeCycleState().toString();
		if (version.equals("A") && state.equals("INWORK")) {
			return true;
		}
		return false;
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