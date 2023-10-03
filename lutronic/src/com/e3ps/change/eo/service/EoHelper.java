package com.e3ps.change.eo.service;

import java.util.Map;

import wt.services.ServiceFactory;

public class EoHelper {

	public static final EoService service = ServiceFactory.getService(EoService.class);
	public static final EoHelper manager = new EoHelper();
	
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
