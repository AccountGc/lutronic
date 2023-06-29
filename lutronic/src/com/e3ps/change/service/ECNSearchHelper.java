package com.e3ps.change.service;

import wt.method.RemoteInterface;
import wt.services.ServiceFactory;

@RemoteInterface
public class ECNSearchHelper {
	public static final ECNSearchService service = ServiceFactory.getService(ECNSearchService.class);
}
