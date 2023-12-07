package com.e3ps.system.service;

import wt.services.ServiceFactory;

public class SystemHelper {

	public static final SystemService service = ServiceFactory.getService(SystemService.class);
	public static final SystemHelper manager = new SystemHelper();
}
