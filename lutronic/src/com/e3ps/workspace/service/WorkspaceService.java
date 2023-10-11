package com.e3ps.workspace.service;

import java.util.ArrayList;
import java.util.Map;

import wt.doc.WTDocument;
import wt.fc.Persistable;

public interface WorkspaceService {

	/**
	 * 결재 데이터 데이터 컨버팅
	 */
	public abstract void convert() throws Exception;

	/**
	 * 결재 등록
	 */
	public abstract void register(Persistable per, ArrayList<Map<String, String>> agreeRows,
			ArrayList<Map<String, String>> approvalRows, ArrayList<Map<String, String>> receiveRows)throws Exception:;

	/**
	 * 자가결재
	 */
	public abstract void self(Persistable per) throws Exception;
}
