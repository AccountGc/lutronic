package com.e3ps.workspace.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.workspace.AppPerLink;
import com.e3ps.workspace.AsmApproval;
import com.ptc.windchill.enterprise.doc.docsb.client.internal.WTDocPasteDelegate;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.vc.VersionControlHelper;

public class AsmHelper {

	public static final AsmService service = ServiceFactory.getService(AsmService.class);
	public static final AsmHelper manager = new AsmHelper();

	/**
	 * 일괄결재 조회 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String numberType = (String) params.get("numberType");
		String creatorOid = (String) params.get("creatorOid");
		String state = (String) params.get("state");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");

		// 정렬
		String sortKey = (String) params.get("sortKey");
		String sortType = (String) params.get("sortType");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(AsmApproval.class, true);

		// 검색 조건 추가
		// 구분
		QuerySpecUtils.toLike(query, idx, AsmApproval.class, AsmApproval.NAME, name);
		QuerySpecUtils.toLikeRightAnd(query, idx, AsmApproval.class, AsmApproval.NUMBER, numberType);
		QuerySpecUtils.toLikeAnd(query, idx, AsmApproval.class, AsmApproval.NUMBER, number);
		QuerySpecUtils.toState(query, idx, AsmApproval.class, state);
		QuerySpecUtils.toCreatorQuery(query, idx, AsmApproval.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, AsmApproval.class, AsmApproval.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		boolean sort = QuerySpecUtils.toSort(sortType);
		QuerySpecUtils.toOrderBy(query, idx, AsmApproval.class, toSortKey(sortKey), sort);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			AsmApproval asm = (AsmApproval) obj[0];
			Map<String, String> data = new HashMap<>();
			data.put("rowNum", String.valueOf(rowNum++));
			data.put("oid", asm.getPersistInfo().getObjectIdentifier().getStringValue());
			data.put("number", asm.getNumber());
			data.put("name", asm.getName());
			data.put("type", AsmHelper.manager.getAsmType(asm));
			data.put("state", asm.getLifeCycleState().getDisplay());
			data.put("creator", asm.getCreatorFullName());
			data.put("description", asm.getDescription());
			data.put("createdDate_txt", asm.getCreateTimestamp().toString().substring(0, 16));
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

	private String toSortKey(String sortKey) throws Exception{
		if("number".equals(sortKey)) {
			return AsmApproval.NUMBER;
		} else if("name".equals(sortKey)) {
			return AsmApproval.NAME;
		} else if("state".equals(sortKey)) {
			return AsmApproval.LIFE_CYCLE_STATE;
		} else if("creator".equals(sortKey)) {
			return AsmApproval.CREATOR_FULL_NAME;
		} else if("createdDate_txt".equals(sortKey)) {
			return AsmApproval.CREATE_TIMESTAMP;
		}
		return AsmApproval.CREATE_TIMESTAMP;
	}

	/**
	 * 일괄결재 타입
	 */
	public String getAsmType(AsmApproval asm) throws Exception {
		String number = asm.getNumber();
		String type = "";
		if (number.startsWith("NDBT")) {
			type = "문서";
		} else if (number.startsWith("ROHSBT")) {
			type = "RoHS";
		} else if (number.startsWith("MMBT")) {
			type = "금형문서";
		}
		return type;
	}

	/**
	 * 일괄겱재 객체 데이터
	 */
	public JSONArray data(AsmApproval asm) throws Exception {
		String viewUrl = "";
		String context = "/Windchill/plm";
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		QueryResult qr = PersistenceHelper.manager.navigate(asm, "persistable", AppPerLink.class);

		while (qr.hasMoreElements()) {
			Persistable per = (Persistable) qr.nextElement();
			Map<String, String> map = new HashMap<>();
			if (per instanceof WTDocument) {
				WTDocument doc = (WTDocument) per;
				String t = doc.getDocType().toString();
				String oid = doc.getPersistInfo().getObjectIdentifier().getStringValue();
				map.put("viewUrl", context + "/doc/view?oid=" + oid);
				map.put("oid", oid);
				map.put("name", doc.getName());
				map.put("number", doc.getNumber());
				map.put("creator", doc.getCreatorFullName());
				map.put("state", doc.getLifeCycleState().getDisplay());
				map.put("createdDate_txt", doc.getCreateTimestamp().toString().substring(0, 16));
				map.put("version", doc.getVersionIdentifier().getSeries().getValue() + "."
						+ doc.getIterationIdentifier().getSeries().getValue());
			} else if (per instanceof ROHSMaterial) {
				ROHSMaterial rohs = (ROHSMaterial) per;
				String oid = rohs.getPersistInfo().getObjectIdentifier().getStringValue();
				map.put("viewUrl", context + "/doc/view?oid=" + oid);
				map.put("oid", oid);
				map.put("name", rohs.getName());
				map.put("number", rohs.getNumber());
				map.put("creator", rohs.getCreatorFullName());
				map.put("createdDate_txt", rohs.getCreateTimestamp().toString().substring(0, 16));
				map.put("state", rohs.getLifeCycleState().getDisplay());
				map.put("version", rohs.getVersionIdentifier().getSeries().getValue() + "."
						+ rohs.getIterationIdentifier().getSeries().getValue());
			}
			map.put("type", getAsmType(asm));
			list.add(map);
		}

		return JSONArray.fromObject(list);
	}
}
