package com.e3ps.change.ecpr.service;

import wt.services.ServiceFactory;

public class EcprHelper {

	public static final EcprService service = ServiceFactory.getService(EcprService.class);
	public static final EcprHelper manager = new EcprHelper();
}
