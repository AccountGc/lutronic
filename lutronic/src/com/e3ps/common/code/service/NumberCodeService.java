package com.e3ps.common.code.service;

import java.util.HashMap;
import java.util.List;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface NumberCodeService {

	String getValue(String codeType, String code) throws Exception;

	NumberCode getNumberCode(String codeType, String code);

	NumberCode getNumberCode(String codeType, String code, String parentOid);

	NumberCode getNumberCodeFormName(String codeType, String name);

	/**
	 * 코드 저장
	 */
	public abstract void save(HashMap<String, List<NumberCodeDTO>> dataMap) throws Exception;

	/**
	 * 국가코드 로더
	 */
	public abstract void loaderNation(String path) throws Exception;

}
