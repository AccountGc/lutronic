package com.e3ps;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.etc.service.EtcHelper;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.rohs.ROHSAttr;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.workspace.ApprovalLine;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test {

	public static void main(String[] args) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> dataMap = new HashMap<>();
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		// 페이징 처리 안하는것으로 한다.
		String name = null;
		String code = null;
		String description = null;
		boolean enabled = true;
		String codeType = "DEPTCODE";

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.CODE, code);
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.DESCRIPTION, description);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, enabled);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, "parentReference.key.id", 0L);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			NumberCodeDTO dto = new NumberCodeDTO(n);
			Map<String, Object> data = new HashMap<>();
			data.put("oid", dto.getOid());
			data.put("name", dto.getName());
			data.put("code", dto.getCode());
			data.put("description", dto.getDescription());
			data.put("sort", dto.getSort());
			data.put("enabled", dto.isEnabled());
			recursive(n, data);
			list.add(data);
		}
		dataMap.put("children", list);			
		map.put("list", dataMap);		

	}
	
	private static void recursive(NumberCode parent, Map<String, Object> data) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, "parentReference.key.id", parent);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, true);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			NumberCodeDTO dto = new NumberCodeDTO(n);
			Map<String, Object> map = new HashMap<>();
			map.put("oid", dto.getOid());
			map.put("name", dto.getName());
			map.put("code", dto.getCode());
			map.put("description", dto.getDescription());
			map.put("sort", dto.getSort());
			map.put("enabled", dto.isEnabled());
			recursive(n, data);
			list.add(map);
		}
		data.put("children", list);
	}
}