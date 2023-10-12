package com.e3ps.change.cr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcrToEcrLink;
import com.e3ps.change.cr.column.CrColumn;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.column.DocumentColumn;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.util.WTException;

public class CrHelper {

	public static final CrService service = ServiceFactory.getService(CrService.class);
	public static final CrHelper manager = new CrHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<CrColumn> list = new ArrayList<>();

		String name = (String) params.get("name");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeRequest.class, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CrColumn data = new CrColumn(obj);
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
	 * 모델명 복수개로 인해서 처리 하는 함수
	 */
	public String displayToModel(String model) throws Exception {
		String display = "";
		if(model != null) {
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
		if(section != null) {
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
		return NumberCodeHelper.manager.getNumberCodeName(dept, "DEPTCODE");
	}

	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		EChangeRequest cr = (EChangeRequest) CommonUtil.getObject(oid);
		if ("cr".equalsIgnoreCase(type)) {
			// CR
			return JSONArray.fromObject(referenceCr(cr, list));
		} else if ("MODEL".equalsIgnoreCase(type)) {
			// 제품명
			return JSONArray.fromObject(referenceCode(cr, list));
		}
		return JSONArray.fromObject(list);
	}

	private Object referenceCode(EChangeRequest cr, ArrayList<Map<String, Object>> list) throws Exception {
		
		String[] codes = cr.getModel() != null ? cr.getModel().split(",") : null;
		
		if(codes != null) {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(NumberCode.class, true);
			for(int i = 0; i < codes.length; i++) {
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

	private Object referenceCr(EChangeRequest cr, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(cr, "useBy", EcrToEcrLink.class);
		while (result.hasMoreElements()) {
			EChangeRequest doc = (EChangeRequest) result.nextElement();
			CrColumn dto = new CrColumn(doc);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}
}
