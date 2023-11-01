package com.e3ps.part.bom.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardBomService extends StandardManager implements BomService {

	public static StandardBomService newStandardBomService() throws WTException {
		StandardBomService instance = new StandardBomService();
		instance.initialize();
		return instance;
	}
}
