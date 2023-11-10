package com.e3ps.temprary.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface TempraryService {

	/**
	 * OID 값으로 주소 구분 하기
	 */
	String getViewIdentity(String oid);

}
