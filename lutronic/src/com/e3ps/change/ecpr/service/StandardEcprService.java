package com.e3ps.change.ecpr.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEcprService extends StandardManager implements EcprService {

	public static StandardEcprService newStandardEcprService() throws WTException {
		StandardEcprService instance = new StandardEcprService();
		instance.initialize();
		return instance;
	}

}
