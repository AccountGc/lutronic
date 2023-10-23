package com.e3ps.sap.service;

import wt.services.ServiceFactory;

public class SAPHelper {

	public static final SAPService service = ServiceFactory.getService(SAPService.class);
	public static final SAPHelper manager = new SAPHelper();

}
