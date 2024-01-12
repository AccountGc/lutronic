package com.e3ps;

import com.e3ps.sap.conn.SAPConnection;
import com.e3ps.sap.conn.SAPDev600Connection;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;

public class SapConTest {

	public static void main(String[] args) throws Exception {
		JCoDestination destination = JCoDestinationManager.getDestination(SAPConnection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_001");
		System.out.println(function);
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}
	}

}
