package com.e3ps.part.bom.service;

import wt.services.ServiceFactory;

public class BomHelper {

	public static final BomService service = ServiceFactory.getService(BomService.class);
	public static final BomHelper manager = new BomHelper();
}
