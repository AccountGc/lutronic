package com.e3ps.change.ecrm.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEcrmService extends StandardManager implements EcrmService {

	public static StandardEcrmService newStandardEcrmService() throws WTException {
		StandardEcrmService instance = new StandardEcrmService();
		instance.initialize();
		return instance;
	}
}
