package com.e3ps.common.code.service;

import java.util.ArrayList;
import java.util.List;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.beans.NumberCodeData;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class NumberCodeHelper {
	public static final NumberCodeHelper manager = new NumberCodeHelper();
	public static final NumberCodeService service = ServiceFactory.getService(NumberCodeService.class);
	
	
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
	 * @param codeType
	 * @param parentOid
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public List<NumberCodeData> getArrayPartTypeList(String codeType, String parentOid, boolean search) throws Exception {
		ArrayList<NumberCodeData> list = new ArrayList<>();
		long parentLongOid = 0;
		if(StringUtil.checkString(parentOid)) {
			parentLongOid = CommonUtil.getOIDLongValue(parentOid);
		}
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		query.appendAnd();
		query.appendWhere(new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL, parentLongOid), new int[] { idx });
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.NAME, false);
		
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode commonCode = (NumberCode) obj[0];
			NumberCodeData data = new NumberCodeData(commonCode);
			list.add(data);
		}
		return list;
	}
	
	public List<NumberCodeData> autoSearchName(String codeType, String name) throws Exception{
		ArrayList<NumberCodeData> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		if(query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(new SearchCondition(NumberCode.class, NumberCode.NAME, SearchCondition.LIKE, "%" + name + "%", false), new int[] {idx});
		QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.NAME, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode code = (NumberCode) obj[0];
			NumberCodeData data = new NumberCodeData(code);
			list.add(data);
		}
		return list;
	}
}
