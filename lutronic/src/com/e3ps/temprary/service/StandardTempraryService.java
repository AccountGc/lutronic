package com.e3ps.temprary.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardTempraryService extends StandardManager implements TempraryService {

	public static StandardTempraryService newStandardTempraryService() throws WTException {
		StandardTempraryService instance = new StandardTempraryService();
		instance.initialize();
		return instance;
	}
}
