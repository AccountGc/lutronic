package com.e3ps.change.eco.service;

import java.util.Map;

import wt.services.ServiceFactory;

public class EcoHelper {

	public static final EcoService service = ServiceFactory.getService(EcoService.class);
	public static final EcoHelper manager = new EcoHelper();
	public Map<String, Object> list(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}
}
