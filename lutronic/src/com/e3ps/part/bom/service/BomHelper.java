package com.e3ps.part.bom.service;

import java.util.Map;

import wt.services.ServiceFactory;

public class BomHelper {

	public static final BomService service = ServiceFactory.getService(BomService.class);
	public static final BomHelper manager = new BomHelper();
	
	/**
	 * BOM 뷰 화면
	 */
	public void loadStructure(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
	}
}
