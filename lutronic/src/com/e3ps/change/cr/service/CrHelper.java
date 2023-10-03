package com.e3ps.change.cr.service;

import wt.services.ServiceFactory;

public class CrHelper {

	public static final CrService service = ServiceFactory.getService(CrService.class);
	public static final CrHelper manager = new CrHelper();
}
