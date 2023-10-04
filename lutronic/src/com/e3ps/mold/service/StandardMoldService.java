package com.e3ps.mold.service;

import wt.services.StandardManager;
import wt.util.WTException;

@SuppressWarnings("serial")
public class StandardMoldService extends StandardManager implements MoldService {
	
	public static StandardMoldService newStandardMoldService() throws WTException {
		final StandardMoldService instance = new StandardMoldService();
		instance.initialize();
		return instance;
	}
	
	
}
