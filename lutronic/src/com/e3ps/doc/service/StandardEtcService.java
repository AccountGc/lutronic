package com.e3ps.doc.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEtcService extends StandardManager implements EtcService {

	public static StandardEtcService newStandardEtcService() throws WTException {
		StandardEtcService instance = new StandardEtcService();
		instance.initialize();
		return instance;
	}
}
