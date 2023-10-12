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
import com.e3ps.org.People;

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
	 * AUI 그리드에서 사용하기 위한함수 JSO N형태로 리턴
	 */
	public JSONArray toJson(String codeType) throws Exception {
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
			map.put("key", n.getCode());
			map.put("value", n.getName());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 코드 & 코드타입으로 값 가져오기
	 */
	public String getNumberCodeName(String code, String codeType) throws Exception {
		
		if(code == null) {
			return null;
		}
		String[] codes = code.split(",");
		
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		for(int i = 0; i < codes.length; i++) {
			QuerySpecUtils.toEqualsOr(query, idx, NumberCode.class, NumberCode.CODE, codes[i]);			
		}
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, "CHANGESECTION");
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		int i = 0;
		String numberCodeNames = "";
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode numberCode = (NumberCode) obj[0];
			if(result.size() -1 == i) {
				numberCodeNames += numberCode.getName();
			}else {
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
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.NAME, false);

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
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.NAME, false);

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
		Map<String, Object> dataMap = new HashMap<>();
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		// 페이징 처리 안하는것으로 한다.
		String type = (String) params.get("type");
		
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, type);
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
			recursive(n, data);
			list.add(data);
		}
		dataMap.put("children", list);
		map.put("list", dataMap);
		return map;
	}

	/**
	 * 재귀 함수
	 */
	private void recursive(NumberCode parent, Map<String, Object> data) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, parent.getCodeType().toString());
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, "parentReference.key.id", parent);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
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
			recursive(n, map);
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
		String type = n.getCode().toString();
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
		return check;
	}

	/**
	 * AXISJ 넘버코드 파인더
	 */
	public ArrayList<Map<String, String>> finder(Map<String, String> params) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		String value = params.get("value");
		String codeType = params.get("codeType");
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.NAME, value);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("oid", n.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", n.getName());
			list.add(map);
		}
		return list;
	}
}
