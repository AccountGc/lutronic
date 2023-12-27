package com.e3ps.doc.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.DocumentClass;
import com.e3ps.doc.DocumentClassType;
import com.e3ps.doc.dto.DocumentClassDTO;
import com.ptc.wpcfg.deliverables.library.WTDocumentMaker;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
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

	/**
	 * 문서 중분류
	 */
	public ArrayList<Map<String, String>> classType2(String classType1) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(DocumentClass.class, true);
		QuerySpecUtils.toEquals(query, idx, DocumentClass.class, DocumentClass.CLASS_TYPE, classType1);
		QuerySpecUtils.toEqualsAnd(query, idx, DocumentClass.class, "parentReference.key.id", 0L);
		QuerySpecUtils.toOrderBy(query, idx, DocumentClass.class, DocumentClass.SORT, false);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			DocumentClass classType2 = (DocumentClass) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("name", classType2.getName());
			map.put("value", classType2.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("clazz", classType2.getClazz());
			list.add(map);
		}

		return list;
	}

	/**
	 * 문서 소분류
	 */
	public ArrayList<Map<String, String>> classType3(String classType1, String classType2) throws Exception {
		DocumentClass parent = (DocumentClass) CommonUtil.getObject(classType2);
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(DocumentClass.class, true);
		QuerySpecUtils.toEquals(query, idx, DocumentClass.class, DocumentClass.CLASS_TYPE, classType1);
		QuerySpecUtils.toEqualsAnd(query, idx, DocumentClass.class, "parentReference.key.id", parent);
		QuerySpecUtils.toOrderBy(query, idx, DocumentClass.class, DocumentClass.SORT, false);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			DocumentClass classType3 = (DocumentClass) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("name", classType3.getName());
			map.put("value", classType3.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("clazz", classType3.getClazz());
			list.add(map);
		}

		return list;
	}

	/**
	 * 클래스타입1
	 */
	public ArrayList<Map<String, String>> getClassTypes1() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();

		DocumentClassType[] classTypes1 = DocumentClassType.getDocumentClassTypeSet();
		for (DocumentClassType classType1 : classTypes1) {
			Map<String, String> map = new HashMap<>();
			map.put("value", classType1.toString());
			map.put("name", classType1.getDisplay());
			map.put("clazz", getClazz(classType1.toString()));
			list.add(map);
		}

		return list;
	}

	/**
	 * 문서 채번 타입에 따라 다른 값 리턴
	 */
	private String getClazz(String key) throws Exception {
		String clazz = "";
		if ("DEV".equals(key)) {
			clazz = "RD-";
		} else if ("INSTRUCTION".equals(key)) {
			clazz = "R";
		} else if ("VALIDATION".equals(key)) {
			clazz = "LR-";
		}
		return clazz;
	}

	/**
	 * 전체 채번 목록
	 */
	public JSONArray numberView(String classType) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		DocumentClassType classType1 = DocumentClassType.toDocumentClassType(classType);
		String classType1Key = classType1.toString();
		String classType1Name = classType1.getDisplay();

		ArrayList<NumberCode> models = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		// 개발문서
		if ("DEV".equals(classType1Key)) {
			ArrayList<Map<String, String>> classTypes2 = classType2(classType1Key);
			for (Map<String, String> classType2 : classTypes2) {
				String classType2Name = classType2.get("name");
				String classType2Key = classType2.get("clazz");

				for (NumberCode model : models) {
					Map<String, String> map = new HashMap<>();
					String number = classType2Key + "-" + model.getCode() + "-";
					String lastNumber = lastNumber(number);
					map.put("classType1Name", classType1Name);
					map.put("classType2Name", classType2Name);
					map.put("classType3Name", "");
					map.put("model", model.getCode());
					map.put("lastNumber", lastNumber);
					list.add(map);
				}
			}
			// 지침서
		} else if ("INSTRUCTION".equals(classType1Key)) {
			ArrayList<Map<String, String>> classTypes2 = classType2(classType1Key);
			for (Map<String, String> classType2 : classTypes2) {
				String classType2Name = classType2.get("name");
				String classType2Key = classType2.get("clazz");

				for (NumberCode model : models) {
					Map<String, String> map = new HashMap<>();
					String number = classType2Key + "-" + model.getCode() + "-";
					String lastNumber = lastNumber(number);
					map.put("classType1Name", classType1Name);
					map.put("classType2Name", classType2Name);
					map.put("classType3Name", "");
					map.put("model", model.getCode());
					map.put("lastNumber", lastNumber);
					list.add(map);
				}
			}
			// 보고서
		} else if ("REPORT".equals(classType1Key)) {

			String today = new Timestamp(new Date().getTime()).toString().substring(0, 10);
			// 2023-23-12
			String suffixYear = today.substring(2, 4);
			String month = today.substring(5, 7);

			ArrayList<Map<String, String>> classTypes2 = classType2(classType1Key);
			for (Map<String, String> classType2 : classTypes2) {
				String classType2Name = classType2.get("name");
				String classType2Key = classType2.get("clazz");
				String classType2Oid = classType2.get("value");

				// 중분류-월달-소분류-
				ArrayList<Map<String, String>> classTypes3 = classType3(classType1Key, classType2Oid);
				for (Map<String, String> classType3 : classTypes3) {
					Map<String, String> map = new HashMap<>();
					String classType3Name = classType3.get("name");
					String classType3Key = classType3.get("clazz");
					String number = classType2Key + "-" + suffixYear + month + classType3Key + "-";
					String lastNumber = lastNumber(number);
					map.put("classType1Name", classType1Name);
					map.put("classType2Name", classType2Name);
					map.put("classType3Name", classType3Name);
					map.put("model", "");
					map.put("lastNumber", lastNumber);
					list.add(map);
				}
			}
			// 벨리데이션
		} else if ("VALIDATION".equals(classType1Key)) {

			ArrayList<Map<String, String>> classTypes2 = classType2(classType1Key);
			for (Map<String, String> classType2 : classTypes2) {
				String classType2Name = classType2.get("name");
				String classType2Oid = classType2.get("value");

				// 중분류-월달-소분류-
				ArrayList<Map<String, String>> classTypes3 = classType3(classType1Key, classType2Oid);
				for (Map<String, String> classType3 : classTypes3) {
					Map<String, String> map = new HashMap<>();
					String classType3Name = classType3.get("name");
					String classType3Key = classType3.get("clazz");
					String number = "LR-" + classType3Key + "-";
					String lastNumber = lastNumber(number);
					map.put("classType1Name", classType1Name);
					map.put("classType2Name", classType2Name);
					map.put("classType3Name", classType3Name);
					map.put("model", "");
					map.put("lastNumber", lastNumber);
					list.add(map);
				}
			}
			// 회의록
		} else if ("MEETING".equals(classType1Key)) {
			String today = new Timestamp(new Date().getTime()).toString().substring(0, 10);
			// 2023-23-12
			String suffixYear = today.substring(2, 4);
			String month = today.substring(5, 7);
			ArrayList<Map<String, String>> classTypes2 = classType2(classType1Key);
			for (Map<String, String> classType2 : classTypes2) {
				String classType2Name = classType2.get("name");
				String classType2Key = classType2.get("clazz");

				Map<String, String> map = new HashMap<>();
				String number = classType2Key + "-" + suffixYear + "-" + month + "-";
				String lastNumber = lastNumber(number);
				map.put("classType1Name", classType1Name);
				map.put("classType2Name", classType2Name);
				map.put("classType3Name", "");
				map.put("model", "");
				map.put("lastNumber", lastNumber);
				list.add(map);
			}
		}
		return JSONArray.fromObject(list);
	}

	private String lastNumber(String number) throws Exception {
		if (!StringUtil.checkString(number)) {
			return null;
		}
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocumentMaster.class, true);
		SearchCondition sc = new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NUMBER, "LIKE", number + "%");
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(WTDocumentMaster.class, WTDocumentMaster.NUMBER);
		OrderBy by = new OrderBy(ca, true);
		query.appendOrderBy(by, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocumentMaster m = (WTDocumentMaster) obj[0];
			return m.getNumber();
		}
		return null;
	}
}
