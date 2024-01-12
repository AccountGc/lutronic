package com.e3ps;

import com.e3ps.sap.conn.SAPConnection;
import com.e3ps.sap.conn.SAPDev600Connection;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;

public class SapConTest {

	public static void main(String[] args) throws Exception {
		JCoDestination destination = JCoDestinationManager.getDestination(SAPConnection.DESTINATION_NAME);
		System.out.println("destination=" + destination);
	}

}
