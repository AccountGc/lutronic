package com.e3ps.common.code.service;

import java.util.ArrayList;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.util.QuerySpecUtils;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
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
}
