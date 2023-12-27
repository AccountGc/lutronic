package com.e3ps.common.code.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;

import wt.fc.QueryResult;
import wt.method.RemoteInterface;

@RemoteInterface
public interface CodeService {

	/**
	 * 
	 * LUTRONIC 추가 시작
	 * 
	 * 
	 */

	List<NumberCodeDTO> numberCodeList(String codeType, String parentOid, boolean search);

	List<NumberCodeDTO> autoSearchName(String CodeType, String name);

	Map<String, Object> popup_numberCodeAction(HttpServletRequest request, HttpServletResponse response,
			boolean disabled) throws Exception;

	/**
	 * 
	 * LUTRONIC 추가 끝
	 * 
	 * 
	 */

	List<NumberCodeDTO> topCodeToList(String key) throws Exception;

	QueryResult getTopCode(String key);

	String getName(String key, String code);

	QueryResult getCode(String key);

	List<NumberCodeDTO> childNumberCodeList(String type, String parentCode);

	QueryResult getChildCode(String key, String parentoid);

	NumberCode getNumberCode(String key, String code);

	HashMap<String, String> getCodeMap(String key);

	Vector<NumberCode> getCodeVec(String key);

	List<NumberCodeDTO> numberParentCodeList(String codeType, String parentOid, boolean search);

	List<String> autoSearchNameRtnName(String codeType, String name) throws Exception;

}
