package com.e3ps.workspace.service;

import java.util.Map;

import wt.services.ServiceFactory;

public class WorkspaceHelper {

	public static final WorkspaceService service = ServiceFactory.getService(WorkspaceService.class);
	public static final WorkspaceHelper manager = new WorkspaceHelper();

	/*
	 * 라이프사이클 관련 상태값
	 */
	public static final String STATE_APPROVAL_APPROVING = "승인중";
	public static final String STATE_APPROVAL_REJECT = "반려됨";
	public static final String STATE_APPROVAL_COMPLETE = "결재완료";

	public Map<String, Object> complete(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> approval(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> receive(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> progress(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> reject(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}
}
