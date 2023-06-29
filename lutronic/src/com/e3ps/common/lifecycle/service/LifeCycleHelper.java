package com.e3ps.common.lifecycle.service;

import wt.services.ServiceFactory;

public class LifeCycleHelper {
	public static final LifeCycleService service = ServiceFactory.getService(LifeCycleService.class);
}
