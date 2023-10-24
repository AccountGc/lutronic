package com.e3ps.sap;

import com.e3ps.sap.conn.SAPConnection;
import com.e3ps.sap.service.SAPHelper;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;

public class SAPTest {

	public static void main(String[] args) throws Exception {

		SAPHelper.manager.ZPPIF_PDM_001_TEST(436906, "C2207004");
		

		System.exit(0);
	}

}
