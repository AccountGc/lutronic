package com.e3ps.change.eco.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEcoService extends StandardManager implements EcoService {

	public static StandardEcoService newStandardEcoService() throws WTException {
		StandardEcoService instance = new StandardEcoService();
		instance.initialize();
		return instance;
	}

}
