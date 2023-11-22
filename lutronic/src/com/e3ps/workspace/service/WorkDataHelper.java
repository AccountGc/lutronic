package com.e3ps.workspace.service;

import wt.services.ServiceFactory;

public class WorkDataHelper {

	public static final WorkDataService service = ServiceFactory.getService(WorkDataService.class);
	public static final WorkDataHelper manager = new WorkDataHelper();
}
