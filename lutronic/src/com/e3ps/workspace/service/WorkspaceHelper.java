package com.e3ps.workspace.service;

import wt.services.ServiceFactory;

public class WorkspaceHelper {

	public static final WorkspaceService service = ServiceFactory.getService(WorkspaceService.class);
	public static final WorkspaceHelper manager = new WorkspaceHelper();
}
