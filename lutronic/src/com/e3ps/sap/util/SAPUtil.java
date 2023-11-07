package com.e3ps.sap.util;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.StringUtil;

/**
 * SAP 연동에서만 사용할 함수들 모음 다른곳에서 사용 용도 없어야 한다. 필요시 쿼리문도 해당 클래스에 작성
 */
public class SAPUtil {

	/**
	 * 객체 생성 방지
	 */
	private SAPUtil() {

	}

	/**
	 * NUMBER 코드의 값을 코드:값 < 으로 변경하여 SAP로 전송
	 */
	public static String sapValue(String code, String codeType) throws Exception {
		String rtn = code;
		NumberCode n = NumberCodeHelper.manager.getNumberCode(code, codeType);
		if (n != null) {
			String value = n.getName();
			rtn += ":" + value;
		}
		return rtn;
	}
}