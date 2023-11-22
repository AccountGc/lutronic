package com.e3ps.workspace.service;

import com.e3ps.workspace.dto.WorkDataDTO;

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
	public abstract void _submit(WorkDataDTO dto) throws Exception;

	/**
	 * 작업함 데이터 읽음 처리
	 */
	public abstract void read(String oid)throws Exception;
}
