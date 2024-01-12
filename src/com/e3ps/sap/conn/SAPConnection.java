package com.e3ps.sap.conn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

import wt.util.WTProperties;

public class SAPConnection {

	protected static String properties_filename = "sap.properties";
	protected static Properties sapProps = null;

	public static String DESTINATION_NAME = "";
	public static String FUNTION_ECO = "ZIFFM_PDM_PP001";
	public static String FUNTION_MATERIAL = "ZIFFM_PDM_PP002";
	public static String FUNTION_BOM = "ZIFFM_PDM_PP003";
	public static String FUNTION_PDM_RETURN = "ZIFFM_PDM_RETURN";
	public static boolean isERPSend = true;

	static {
		setERPInit();
	}

	public static void setERPInit() {

		Config conf = ConfigImpl.getInstance();
		System.out.println("erp.destination = " + conf.getString("erp.destination"));
		System.out.println("erp.send = " + conf.getString("erp.send"));
		DESTINATION_NAME = conf.getString("erp.destination");

		isERPSend = conf.getString("erp.send").equals("true") ? true : false;

		System.out.println("==== setERPInit ====");
		System.out.println("DESTINATION_NAME =" + DESTINATION_NAME);
		System.out.println("isERPSend =" + isERPSend);
		if (isERPSend) {
			getLoginProperties();
			createDestinationDataFile(DESTINATION_NAME, sapProps);
		}

	}

	public static Properties getLoginProperties() {
		if (sapProps == null) {
			sapProps = new Properties();
			try {
				String wt_home = WTProperties.getServerProperties().getProperty("wt.home");

				StringBuffer sb = new StringBuffer();
				sb.append(wt_home);
				sb.append(File.separator);
				sb.append("codebase");
				sb.append(File.separator);
				sb.append("com");
				sb.append(File.separator);
				sb.append("e3ps");
				sb.append(File.separator);
				sb.append(properties_filename);
				// System.out.println("path : " + sb.toString());
				sapProps.load(new FileInputStream(sb.toString()));
				// System.out.println("sapProps : " + sapProps);
			} catch (IOException ex) {
				ex.printStackTrace();
				// System.out.println(ex);
			}
		}

		return sapProps;
	}

	public static void createDestinationDataFile(String destinationName, Properties connectProperties) {
		File destCfg = new File(destinationName + ".jcoDestination");
		System.out.println(destCfg.isFile());
//		if (!destCfg.isFile()) {
//			return;
//		}
		try {
			FileOutputStream fos = new FileOutputStream(destCfg, false);
			connectProperties.store(fos, "for tests only !");
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException("Unable to create the destination files", e);
		}
	}

	public static void step3SimpleCall() throws JCoException { // .getStructure
		// table
		// 단일 필드
		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME);

		JCoFunction function = destination.getRepository().getFunction(FUNTION_ECO);
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		JCoParameterList list = function.getTableParameterList();
		// JCoParameterList exportList =function.getExportParameterList();

		JCoFieldIterator it = list.getFieldIterator();
		while (it.hasNextField()) {

			// System.out.println(it.nextField().getName());
		}

		JCoParameterList list2 = function.getImportParameterList();

		JCoTable codes = function.getTableParameterList().getTable("IT_INPUT");
		// System.out.println("codes1 =" + codes.getNumRows());
		for (int i = 1; i < 3; i++) {

			// codes.appendRows(i);
			codes.insertRow(i);
			codes.setValue("ZIFNO", "EE" + i);
			codes.setValue("AENNR", "test");
			codes.setValue("AETXT", "111");
			codes.setValue("DATUV", "111");
			codes.setValue("DATUV", "111");
			codes.setValue("AEGRU", "111");
			codes.setValue("ZECMID", "111");

		}

		// codes.deleteRow(10);
		// System.out.println("codes2 =" + codes.getNumRows());
		// System.out.println("IT_INPUT =" + codes.ge);

		// function.getTableParameterList().setValue("IT_INPUT",
		// codes);//setValue("IT_INPUT",codes.getTable("IT_INPUT"));

		//
		function.execute(destination);

	}

	public static void main(String[] args) {
		try {
			// ET_OUTPUT
			// System.out.println("TEST");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
