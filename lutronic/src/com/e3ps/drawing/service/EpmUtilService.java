package com.e3ps.drawing.service;

import wt.epm.EPMDocument;
import wt.method.RemoteInterface;

@RemoteInterface
public interface EpmUtilService {

	void copyIBA(String oid);

	void changeEPMName(EPMDocument epm, String changeName) throws Exception;

	void changeEPMState(EPMDocument epm, String state) throws Exception;

	void epmReName(EPMDocument epm, String changeName) throws Exception;

}
