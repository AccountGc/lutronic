package com.e3ps.sap;

import java.util.ArrayList;

import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.conn.SAPConnection;
import com.e3ps.sap.conn.SAPDevConnection;
import com.e3ps.sap.service.SAPHelper;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterFieldIterator;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoTable;

import wt.part.WTPart;

public class SAPTest {

	public static void main(String[] args) throws Exception {

		SAPHelper.manager.ZPPIF_PDM_001_TEST(637125, "C2507114");
		System.exit(0);
	}

}
