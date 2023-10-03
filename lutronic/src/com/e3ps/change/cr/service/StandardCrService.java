package com.e3ps.change.cr.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardCrService extends StandardManager implements CrService {

	public static StandardCrService newStandardCrService() throws WTException {
		StandardCrService instance = new StandardCrService();
		instance.initialize();
		return instance;
	}

}
