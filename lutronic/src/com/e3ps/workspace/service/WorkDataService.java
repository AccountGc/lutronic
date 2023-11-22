package com.e3ps.workspace.service;

import java.util.Map;

import wt.fc.Persistable;
import wt.method.RemoteInterface;

@RemoteInterface
public interface WorkDataService {

	/**
	 * 작업함으로 이동
	 */
	public abstract void create(Persistable per) throws Exception;

	/**
	 * 기안
	 */
	public abstract void _submit(Map<String, Object> params) throws Exception;
}
