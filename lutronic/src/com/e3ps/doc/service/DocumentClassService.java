package com.e3ps.doc.service;

import java.util.HashMap;

import wt.method.RemoteInterface;

@RemoteInterface
public interface DocumentClassService {

	/**
	 * 문서 채번 저장
	 */
	public abstract void save(HashMap<String, Object> dataMap) throws Exception;

}
