package com.e3ps.workspace.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface AsmService {

	/**
	 * 일괄결재 등록
	 */
	public abstract void register(Map<String, Object> params) throws Exception;

}
