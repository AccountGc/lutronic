package com.e3ps.org.service;

import wt.services.ServiceFactory;

public class UserHelper {
	public static final UserService service = ServiceFactory.getService(UserService.class);
}
