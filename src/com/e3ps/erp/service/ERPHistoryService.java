package com.e3ps.erp.service;

import java.util.HashMap;

import com.sap.conn.jco.JCoTable;

import wt.method.RemoteInterface;


@RemoteInterface
public interface ERPHistoryService {

	
	void createHistoryPart(JCoTable codes, JCoTable returnCodes)throws Exception;
	
	void createHistoryECO(JCoTable codes,JCoTable returnCodes)throws Exception;
	
	void updateHistory(Class cls, String startZifno, String endZifno,JCoTable returnCodes) throws Exception;

	void createHistoryBOM(JCoTable codes, JCoTable returnCodes,
			HashMap<String, Object> map) throws Exception;
			

	

}
