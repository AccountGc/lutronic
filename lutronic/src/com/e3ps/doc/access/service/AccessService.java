package com.e3ps.doc.access.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface AccessService {

	/**
	 * 권한 저장
	 */
	public abstract void save(Map<String, Object> params) throws Exception;
}
