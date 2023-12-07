package com.e3ps.system.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardSystemService extends StandardManager implements SystemService {

	public static StandardSystemService newStandardSystemService() throws WTException {
		StandardSystemService instance = new StandardSystemService();
		instance.initialize();
		return instance;
	}
}
