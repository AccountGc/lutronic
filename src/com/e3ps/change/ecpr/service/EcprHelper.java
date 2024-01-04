package com.e3ps.change.ecpr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.CrToEcprLink;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcoToEcprLink;
import com.e3ps.change.EcprToDocumentLink;
import com.e3ps.change.cr.column.CrColumn;
import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.change.eco.column.EcoColumn;
import com.e3ps.change.ecpr.column.EcprColumn;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.column.DocumentColumn;
import com.ibm.icu.text.DecimalFormat;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class EcprHelper {

	public static final EcprService service = ServiceFactory.getService(EcprService.class);
	public static final EcprHelper manager = new EcprHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<EcprColumn> list = new ArrayList<>();

		try {
			String name = StringUtil.checkNull((String) params.get("name"));
			String number = StringUtil.checkNull((String) params.get("number"));
			String state = StringUtil.checkNull((String) params.get("state"));
			String creator = StringUtil.checkNull((String) params.get("creatorOid"));
			String createdFrom = StringUtil.checkNull((String) params.get("createdFrom"));
			String createdTo = StringUtil.checkNull((String) params.get("createdTo"));
			String approveFrom = StringUtil.checkNull((String) params.get("approveFrom"));
			String approveTo = StringUtil.checkNull((String) params.get("approveTo"));
			String writer = StringUtil.checkNull((String) params.get("writerOid"));
			String createDepart = StringUtil.checkNull((String) params.get("createDepart"));
			String writedFrom = StringUtil.checkNull((String) params.get("writedFrom"));
			String writedTo = StringUtil.checkNull((String) params.get("writedTo"));
//			String proposer = StringUtil.checkNull((String)params.get("proposer"));
			String changeSection = StringUtil.checkNull((String) params.get("changeSection"));
			String model = StringUtil.checkNull((String) params.get("model"));

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(ECPRRequest.class, true);

			// 상태 임시저장 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			if (!CommonUtil.isAdmin()) {
				query.appendWhere(new SearchCondition(ECPRRequest.class, ECPRRequest.LIFE_CYCLE_STATE,
						SearchCondition.NOT_EQUAL, "LINE_REGISTER"), new int[] { idx });
			}

			// 제목
			QuerySpecUtils.toLikeAnd(query, idx, ECPRRequest.class, ECPRRequest.EO_NAME, name);
			// 번호
			QuerySpecUtils.toLikeAnd(query, idx, ECPRRequest.class, ECPRRequest.EO_NUMBER, number);
			// 상태
			QuerySpecUtils.toState(query, idx, ECPRRequest.class, state);
			// 등록자
			QuerySpecUtils.toCreatorQuery(query, idx, ECPRRequest.class, creator);
			// 등록일
			QuerySpecUtils.toTimeGreaterAndLess(query, idx, ECPRRequest.class, ECPRRequest.CREATE_TIMESTAMP,
					createdFrom, createdTo);
			// 승인일
			if (approveFrom.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(ECPRRequest.class, ECPRRequest.APPROVE_DATE,
						SearchCondition.GREATER_THAN_OR_EQUAL, approveFrom), new int[] { idx });
			}
			if (approveTo.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(ECPRRequest.class, ECPRRequest.APPROVE_DATE,
						SearchCondition.LESS_THAN_OR_EQUAL, approveTo), new int[] { idx });
			}
			// 작성자
			if (writer != "") {
				QuerySpecUtils.toEqualsAnd(query, idx, ECPRRequest.class, ECPRRequest.WRITER,
						Long.toString(CommonUtil.getOIDLongValue(writer)));
			}
			// 작성부서
			QuerySpecUtils.toEqualsAnd(query, idx, ECPRRequest.class, ECPRRequest.CREATE_DEPART, createDepart);
			// 작성일
			if (writedFrom.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(ECPRRequest.class, ECPRRequest.CREATE_DATE,
						SearchCondition.GREATER_THAN_OR_EQUAL, writedFrom), new int[] { idx });
			}
			if (writedTo.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(ECPRRequest.class, ECPRRequest.CREATE_DATE,
						SearchCondition.LESS_THAN_OR_EQUAL, writedTo), new int[] { idx });
			}
			// 제안자
//			QuerySpecUtils.toLikeAnd(query, idx, ECPRRequest.class, ECPRRequest.PROPOSER, proposer);
			// 변경구분
			QuerySpecUtils.toLikeAnd(query, idx, ECPRRequest.class, ECPRRequest.CHANGE_SECTION, changeSection);
			// 제품명
			QuerySpecUtils.toLikeAnd(query, idx, ECPRRequest.class, ECPRRequest.MODEL, model);

			QuerySpecUtils.toOrderBy(query, idx, ECPRRequest.class, ECPRRequest.MODIFY_TIMESTAMP, true);

			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EcprColumn column = new EcprColumn(obj);
				list.add(column);
			}
			map.put("list", list);
			map.put("topListCount", pager.getTotal());
			map.put("pageSize", pager.getPsize());
			map.put("total", pager.getTotalSize());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 모델명 복수개로 인해서 처리 하는 함수
	 */
	public String displayToModel(String model) throws Exception {
		String display = "";
		if (model != null) {
			String[] ss = model.split(",");
			for (int i = 0; i < ss.length; i++) {
				String s = ss[i];
				if (ss.length - 1 == i) {
					display += NumberCodeHelper.manager.getNumberCodeName(s, "MODEL");
				} else {
					display += NumberCodeHelper.manager.getNumberCodeName(s, "MODEL") + ",";
				}
			}
		}
		return display;
	}

	/**
	 * 변경구분 복수개로 인해서 처리 하는 함수
	 */
	public String displayToSection(String section) throws Exception {
		String display = "";
		if (section != null) {
			String[] ss = section.split(",");
			for (int i = 0; i < ss.length; i++) {
				String s = ss[i];
				if (ss.length - 1 == i) {
					display += NumberCodeHelper.manager.getNumberCodeName(s, "CHANGESECTION");
				} else {
					display += NumberCodeHelper.manager.getNumberCodeName(s, "CHANGESECTION") + ",";
				}
			}
		}
		return display;
	}

	/**
	 * 작성부서 코드 -> 값
	 */
	public String displayToDept(String dept) throws Exception {
		if (dept == null) {
			return "";
		}
		return NumberCodeHelper.manager.getNumberCodeName(dept, "DEPTCODE");
	}

	/**
	 * 관련 CR
	 */
	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		ECPRRequest ecpr = (ECPRRequest) CommonUtil.getObject(oid);
		if ("cr".equalsIgnoreCase(type)) {
			// CR
			return JSONArray.fromObject(referenceCr(ecpr, list));
		} else if ("MODEL".equalsIgnoreCase(type)) {
			// 제품명
			return JSONArray.fromObject(referenceCode(ecpr, list));
		} else if ("doc".equalsIgnoreCase(type)) {
			return JSONArray.fromObject(referenceDoc(ecpr, list));
		} else if ("eco".equalsIgnoreCase(type)) {
			return JSONArray.fromObject(referenceEco(ecpr, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * ECPR과 관련된 ECO
	 */
	private Object referenceEco(ECPRRequest ecpr, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(ecpr, "eco", EcoToEcprLink.class);
		while (result.hasMoreElements()) {
			EChangeOrder eco = (EChangeOrder) result.nextElement();
			EcoColumn dto = new EcoColumn(eco);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * ECPR과 관련된 DOC
	 */
	private Object referenceDoc(ECPRRequest ecpr, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(ecpr, "doc", EcprToDocumentLink.class);
		while (result.hasMoreElements()) {
			WTDocument doc = (WTDocument) result.nextElement();
			DocumentColumn dto = new DocumentColumn(doc);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * ECPR과 관련된 CR
	 */
	private Object referenceCr(ECPRRequest ecpr, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(ecpr, "cr", CrToEcprLink.class);
		while (result.hasMoreElements()) {
			EChangeRequest cr = (EChangeRequest) result.nextElement();
			CrColumn dto = new CrColumn(cr);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * ECPR과 관련된 제품
	 */
	private Object referenceCode(ECPRRequest ecpr, ArrayList<Map<String, Object>> list) throws Exception {
		String[] codes = ecpr.getModel() != null ? ecpr.getModel().split(",") : null;

		if (codes != null) {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(NumberCode.class, true);
			for (int i = 0; i < codes.length; i++) {
				QuerySpecUtils.toEqualsOr(query, idx, NumberCode.class, NumberCode.CODE, codes[i]);
			}
			QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, "MODEL");
			QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				NumberCode n = (NumberCode) obj[0];
				NumberCodeDTO dto = new NumberCodeDTO(n);
				Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 링크 관계 확인 ( true : 연결, false : 미연결 )
	 */
	public boolean isConnect(String oid) throws Exception {
		boolean isConnect = false;
		ECPRRequest ecpr = (ECPRRequest) CommonUtil.getObject(oid);
		QueryResult qr = PersistenceHelper.manager.navigate(ecpr, "cr", CrToEcprLink.class);
		isConnect = qr.size() > 0;
		return isConnect;
	}

	/**
	 * ECPR 다음번호
	 */
	public String getNextNumber(String number) throws Exception {
		DecimalFormat df = new DecimalFormat("000");
		String rtn = null;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ECPRRequest.class, true);
		SearchCondition sc = new SearchCondition(ECPRRequest.class, ECPRRequest.EO_NUMBER, "LIKE", number + "%");
		query.appendWhere(sc, new int[] { idx });
		QuerySpecUtils.toOrderBy(query, idx, ECPRRequest.class, ECPRRequest.CREATE_TIMESTAMP, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		// E2312N45
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			ECPRRequest ecpr = (ECPRRequest) obj[0];
			String ecprNumber = ecpr.getEoNumber();
			String next = ecprNumber.substring(ecprNumber.length() - 3);
			int n = Integer.parseInt(next) + 1;
			rtn = number + df.format(n);
		} else {
			rtn = number + "001";
		}
		return rtn;
	}
}
