package com.e3ps.workspace.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface AsmService {

	/**
	 * 일괄결재 등록
	 */
	public abstract void register(Map<String, Object> params) throws Exception;

	/**
	 * 일괄결재 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * 일괄 결재 수정
	 */
	public abstract void modify(Map<String, Object> params)throws Exception;

}
