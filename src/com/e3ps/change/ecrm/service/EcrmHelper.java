package com.e3ps.change.ecrm.service;

import wt.services.ServiceFactory;

public class EcrmHelper {

	public static final EcrmService service = ServiceFactory.getService(EcrmService.class);
	public static final EcrmHelper manager = new EcrmHelper();

}
