package com.e3ps.common.code.service;

public interface GenNumberService {

	boolean checkCode(String codeType, String code);

	boolean checkCode(String codeType, String parentOid, String code);

}
