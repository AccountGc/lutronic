package com.e3ps.doc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.doc.DocumentClass;
import com.e3ps.doc.DocumentClassType;
import com.e3ps.doc.dto.DocumentClassDTO;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class DocumentClassHelper {

	public static final DocumentClassService service = ServiceFactory.getService(DocumentClassService.class);
	public static final DocumentClassHelper manager = new DocumentClassHelper();

	/**
	 * 문서타입 트리 함수
	 */
	public Map<String, Object> tree() throws Exception {
		Map<String, Object> result = new HashMap<>();

		DocumentClassType[] arr = DocumentClassType.getDocumentClassTypeSet();
		ArrayList<Map<String, String>> list = new ArrayList<>();
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("display", "문서타입");
		for (DocumentClassType n : arr) {
			Map<String, String> map = new HashMap<>();
			map.put("display", n.getDisplay());
			map.put("type", n.toString());
			list.add(map);
		}
		dataMap.put("children", list);
		result.put("list", dataMap);
		return result;
	}

	/**
	 * 코드체계 리스트
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		// 페이징 처리 안하는것으로 한다.
		String name = (String) params.get("name");
		String clazz = (String) params.get("class");
		String description = (String) params.get("description");
		String classType = (String) params.get("classType");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(DocumentClass.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, DocumentClass.class, DocumentClass.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, DocumentClass.class, DocumentClass.CLAZZ, clazz);
		QuerySpecUtils.toLikeAnd(query, idx, DocumentClass.class, DocumentClass.DESCRIPTION, description);
		QuerySpecUtils.toEqualsAnd(query, idx, DocumentClass.class, DocumentClass.CLASS_TYPE, classType);
		QuerySpecUtils.toEqualsAnd(query, idx, DocumentClass.class, "parentReference.key.id", 0L);
		QuerySpecUtils.toOrderBy(query, idx, DocumentClass.class, DocumentClass.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			DocumentClass n = (DocumentClass) obj[0];
			DocumentClassDTO dto = new DocumentClassDTO(n);
			Map<String, Object> data = new HashMap<>();
			data.put("oid", dto.getOid());
			data.put("name", dto.getName());
			data.put("clazz", dto.getClazz());
			data.put("description", dto.getDescription());
			data.put("sort", dto.getSort());
			data.put("enabled", dto.isEnabled());
			recursive(n, data, params);
			list.add(data);
		}
		map.put("list", list);
		return map;
	}

	/**
	 * 재귀 함수
	 */
	private void recursive(DocumentClass parent, Map<String, Object> data, Map<String, Object> params)
			throws Exception {
		String name = (String) params.get("name");
		String clazz = (String) params.get("clazz");
		String description = (String) params.get("description");
		String classType = (String) params.get("classType");

		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(DocumentClass.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, DocumentClass.class, "parentReference.key.id", parent);
		QuerySpecUtils.toLikeAnd(query, idx, DocumentClass.class, DocumentClass.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, DocumentClass.class, DocumentClass.CLAZZ, clazz);
		QuerySpecUtils.toLikeAnd(query, idx, DocumentClass.class, DocumentClass.DESCRIPTION, description);
		QuerySpecUtils.toEqualsAnd(query, idx, DocumentClass.class, DocumentClass.CLASS_TYPE, classType);
		QuerySpecUtils.toOrderBy(query, idx, DocumentClass.class, DocumentClass.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			DocumentClass n = (DocumentClass) obj[0];
			DocumentClassDTO dto = new DocumentClassDTO(n);
			Map<String, Object> map = new HashMap<>();
			map.put("oid", dto.getOid());
			map.put("name", dto.getName());
			map.put("clazz", dto.getClazz());
			map.put("description", dto.getDescription());
			map.put("sort", dto.getSort());
			map.put("enabled", dto.isEnabled());
			recursive(n, map, params);
			list.add(map);
		}
		data.put("children", list);
	}
}
