package com.e3ps.doc.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EtcService {

	/**
	 * 기타 문서 등록 및 임시저장
	 */
	public abstract void create(Map<String, Object> params) throws Exception;

	/**
	 * 기타 문서 수정 및 임시저장
	 */
	public abstract void modify(Map<String, Object> params) throws Exception;

	/**
	 * 기타 문서 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * 기타 문서 개정
	 */
	public abstract void revise(Map<String, Object> params) throws Exception;

}
