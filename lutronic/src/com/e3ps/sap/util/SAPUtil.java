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

	public String splitToCommaValue(String value, String codeType) throws Exception {
		// IBA코드값이 여러개로, 처리된것들
		String rtn = "";
		if (!StringUtil.checkString(value)) {
			return "";
		}

		String[] ss = value.split(",");
		for (String s : ss) {
			NumberCode n = NumberCodeHelper.manager.getNumberCode(s.trim(), codeType);
			if(n != null) {
				rtn += n.getdi
			}
		}

		return rtn;
	}
}
