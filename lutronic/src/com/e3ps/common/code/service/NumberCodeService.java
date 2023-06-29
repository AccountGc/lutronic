package com.e3ps.common.code.service;

import com.e3ps.common.code.NumberCode;

import wt.method.RemoteInterface;

@RemoteInterface
public interface NumberCodeService {

	String getValue(String codeType, String code) throws Exception;

	NumberCode getNumberCode(String codeType, String code);
	
	NumberCode getNumberCode(String codeType, String code, String parentOid);
	
	NumberCode getNumberCodeFormName(String codeType, String name);

}
