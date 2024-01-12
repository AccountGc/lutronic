package com.e3ps.common.code.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;

import net.sf.json.JSONArray;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class NumberCodeHelper {
	public static final NumberCodeHelper manager = new NumberCodeHelper();
	public static final NumberCodeService service = ServiceFactory.getService(NumberCodeService.class);

	/**
	 * AUI 그리드에서 사용하기 위한함수 JSON 형태로 리턴
	 */
	public JSONArray toJson(String codeType) throws Exception {
		return toJson(codeType, "KEY");
	}

	/**
	 * AUI 그리드에서 사용하기 위한함수 JSON 형태로 리턴, CODE OR OID 형태
	 */
	public JSONArray toJson(String codeType, String view) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			Map<String, String> map = new HashMap<>();
			if ("KEY".equals(view)) {
				map.put("key", n.getCode());
			} else if ("OID".equals(view)) {
				map.put("key", n.getPersistInfo().getObjectIdentifier().getStringValue());
			}
			map.put("value", n.getName());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 코드 & 코드타입으로 값 가져오기
	 */
	public String getNumberCodeName(String code, String codeType) throws Exception {
		String numberCodeNames = "";
		if (!StringUtil.checkString(codeType) || code == null || code.equals("")) {
			return numberCodeNames;
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);

		if (code.indexOf(",") > -1) {
			String[] codes = code.split(",");
			for (int i = 0; i < codes.length; i++) {
				QuerySpecUtils.toEqualsOr(query, idx, NumberCode.class, NumberCode.CODE, codes[i]);
			}
		} else {
			QuerySpecUtils.toEqualsOr(query, idx, NumberCode.class, NumberCode.CODE, code);
		}

		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		int i = 0;

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode numberCode = (NumberCode) obj[0];
			if (result.size() - 1 == i) {
				numberCodeNames += numberCode.getName();
			} else {
				numberCodeNames += numberCode.getName() + ",";
			}
			i++;
		}
		return numberCodeNames;
	}

	/**
	 * 코드 & 코드타입으로 코드 객체 찾아오기
	 */
	public NumberCode getNumberCode(String code, String codeType) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE, code);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode numberCode = (NumberCode) obj[0];
			return numberCode;
		}
		return null;
	}

	/**
	 * 코드타입과 일치하는 NumberCode 배열로 가져오기
	 */
	public ArrayList<NumberCode> getArrayCodeList(String codeType) throws Exception {
		ArrayList<NumberCode> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode commonCode = (NumberCode) obj[0];
			list.add(commonCode);
		}
		return list;
	}

	/**
	 * PartType 입력값에 따른 PartTypeList 가져오기
	 * 
	 * @param codeType
	 * @param parentOid
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public List<NumberCodeDTO> getArrayPartTypeList(String codeType, String parentOid) throws Exception {
		ArrayList<NumberCodeDTO> list = new ArrayList<>();
		long parentLongOid = 0;
		if (StringUtil.checkString(parentOid)) {
			parentLongOid = CommonUtil.getOIDLongValue(parentOid);
		}
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		query.appendAnd();
		query.appendWhere(
				new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL, parentLongOid),
				new int[] { idx });
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode commonCode = (NumberCode) obj[0];
			NumberCodeDTO data = new NumberCodeDTO(commonCode);
			list.add(data);
		}
		return list;
	}

	public List<NumberCodeDTO> autoSearchName(String codeType, String name) throws Exception {
		ArrayList<NumberCodeDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(
				new SearchCondition(NumberCode.class, NumberCode.NAME, SearchCondition.LIKE, "%" + name + "%", false),
				new int[] { idx });
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.NAME, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode code = (NumberCode) obj[0];
			NumberCodeDTO data = new NumberCodeDTO(code);
			list.add(data);
		}
		return list;
	}

	/**
	 * 코드 & 코드타입으로 코드 객체 찾아오기
	 */
	public NumberCodeDTO getStepNumberCode(String codeType, String code) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE, code);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		NumberCodeDTO data = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			data = new NumberCodeDTO((NumberCode) obj[0]);
		}
		return data;
	}

	/**
	 * 코드타입 트리 함수
	 */
	public Map<String, Object> tree() throws Exception {
		Map<String, Object> result = new HashMap<>();

		NumberCodeType[] arr = NumberCodeType.getNumberCodeTypeSet();
		ArrayList<Map<String, String>> list = new ArrayList<>();
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("display", "코드체계");
		for (NumberCodeType n : arr) {
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
		String code = (String) params.get("code");
		String description = (String) params.get("description");
//		boolean enabled = "true".equals((String) params.get("enabled")) ? true : false;
		String codeType = (String) params.get("codeType");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.CODE, code);
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.DESCRIPTION, description);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
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
			recursive(n, data, params);
			list.add(data);
		}
		map.put("list", list);
		return map;
	}

	/**
	 * 재귀 함수
	 */
	private void recursive(NumberCode parent, Map<String, Object> data, Map<String, Object> params) throws Exception {
		String name = (String) params.get("name");
		String code = (String) params.get("code");
		String description = (String) params.get("description");
//		boolean enabled = "true".equals((String) params.get("enabled")) ? true : false;
		String codeType = (String) params.get("codeType");

		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, "parentReference.key.id", parent);
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.CODE, code);
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.DESCRIPTION, description);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
//		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, enabled);
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
			recursive(n, map, params);
			list.add(map);
		}
		data.put("children", list);
	}

	/**
	 * 코드 사용 여부 체크
	 */
	public boolean check(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		NumberCode n = (NumberCode) CommonUtil.getObject(oid);
		boolean check = false;

		QuerySpec query = new QuerySpec();
		QueryResult result = null;
		if (n != null) {
			String code = n.getCode();
			if (code != null) {
				String type = code.toString();
				switch (type) {
				case "EOTYPE":
					query.appendClassList(EChangeOrder.class, true);
					QuerySpecUtils.toEquals(query, 0, EChangeOrder.class, EChangeOrder.ECO_TYPE, type);
					result = PersistenceHelper.manager.find(query);
					check = result.size() > 0 ? true : false;
					break;
				case "CHANGEPURPOSE":
					// ???
//			query.appendClassList(EChangeOrder.class, true);
//			QuerySpecUtils.toEquals(query, 0, EChangeOrder.class, EChangeOrder., type);
//			result = PersistenceHelper.manager.find(query);

					// 필드가 없는거 같은데?
//			query.appendClassList(EChangeRequest.class, true);
//			QuerySpecUtils.toLike(query, 0, EChangeRequest.class, "purpose", type);
//			result = PersistenceHelper.manager.find(query);
//			check = result.size() > 0 ? true : false;

					break;
				case "STOCKMANAGEMENT":
					// 이것도 필드가 없는거 같은데
//			query.appendClassList(EChangeOrder.class, true);
//			QuerySpecUtils.toLike(query, 0, EChangeOrder.class, "stockPart", type);
//			result = PersistenceHelper.manager.find(query);
//			check = result.size() > 0 ? true : false;
					break;
				}
			}
		}
		return check;
	}

	/**
	 * AXISJ 넘버코드 파인더
	 */
	public ArrayList<Map<String, String>> finder(Map<String, String> params) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		String value = params.get("value");
		String valueType = params.get("valueType");
		String codeType = params.get("codeType");
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		
		query.appendOpenParen();
		QuerySpecUtils.toLike(query, idx, NumberCode.class, NumberCode.CODE, value);
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.NAME, value);
		query.appendCloseParen();
		
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			Map<String, String> map = new HashMap<>();
			if (valueType.equals("code")) {
				map.put("oid", n.getCode());
			} else if (valueType.equals("oid")) {
				map.put("oid", n.getPersistInfo().getObjectIdentifier().getStringValue());
			}
			map.put("name", "[" + n.getCode() + "]&nbsp;" + n.getName());
			list.add(map);
		}
		return list;
	}

	/**
	 * 나라 코드 가져오기
	 */
	public ArrayList<Map<String, String>> getCountry() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, "NATION");
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("code", n.getCode());
			map.put("name", n.getName());
			list.add(map);
		}
		return list;
	}

	/**
	 * 품목분류 1레벨
	 */
	public JSONArray toPartType1() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, "PARTTYPE");
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, "parentReference.key.id", 0L);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("value", "[" + n.getCode() + "] " + n.getName());
			map.put("key", n.getPersistInfo().getObjectIdentifier().getStringValue());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 자식 코드 가여괴 부모코드값이 있는
	 */
	public ArrayList<Map<String, String>> getChildCodeByParent(Map<String, Object> params) throws Exception {
		String codeType = (String) params.get("codeType");
		String parentOid = (String) params.get("parentOid");
		NumberCode parent = (NumberCode) CommonUtil.getObject(parentOid);
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, "parentReference.key.id", parent);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("value", "[" + n.getCode() + "] " + n.getName());
			map.put("key", n.getPersistInfo().getObjectIdentifier().getStringValue());
			list.add(map);
		}
		return list;
	}

	/**
	 * 이름 & 코드타입으로 코드 객체 찾아오기
	 */
	public String getCodeName(String name, String codeType) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.NAME, name);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode numberCode = (NumberCode) obj[0];
			return numberCode.getCode();
		}
		return null;
	}

	/**
	 * 품목 분류 1레벨
	 */
	public ArrayList<Map<String, String>> getOneLevel(String codeType) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, "parentReference.key.id", 0L);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("name", "[" + n.getCode() + "] " + n.getName());
			map.put("oid", n.getPersistInfo().getObjectIdentifier().getStringValue());
			list.add(map);
		}
		return list;
	}
}
