package com.e3ps.sap;

import com.e3ps.sap.conn.SAPConnection;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;

public class SAPTest {

	public static void main(String[] args) throws Exception {
		JCoDestination destination = JCoDestinationManager.getDestination("aaa");

		System.out.println("destination=" + destination);

		// System.out.println("yhkim8");
		JCoFunction function = destination.getRepository().getFunction(SAPConnection.FUNTION_MATERIAL);

		System.exit(0);
	}

}
