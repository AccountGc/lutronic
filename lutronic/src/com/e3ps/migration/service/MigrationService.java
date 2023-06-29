package com.e3ps.migration.service;

import wt.epm.EPMDocument;
import wt.method.RemoteInterface;
import wt.part.WTPart;

@RemoteInterface
public interface MigrationService {

	void createBom(WTPart parentPart, WTPart childPart, String qstr)
			throws Exception;

	void publish(EPMDocument epm) throws Exception;

}
