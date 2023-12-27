package com.e3ps.erp.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import wt.util.WTProperties;

import com.e3ps.common.jdf.config.ConfigImpl;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class SAPTESTConnection {
	
	protected static String properties_filename = "sap.properties";
    protected static Properties sapProps = null;
    private static SAPTESTConnection INSTANCE = null;
		
	static String ABAP_AS = "ABAP_AS_WITHOUT_POOL";
    static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";
    static String ABAP_MS = "ABAP_MS_WITHOUT_POOL";
    
    static String FUNTION_ECO="ZIFFM_PDM_PP001";
    static String FUNTION_MATERIAL="ZIFFM_PDM_PP002";
    static String FUNTION_BOOM="ZIFFM_PDM_PP003";
    /*
    static{
    	//System.out.println("  SAPConnection  start");
    	try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//System.out.println("  SAPConnection end");
    }
    */
  
    
    
    public static Properties getLoginProperties()
    {
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
          //System.out.println("path : " + sb.toString());
          sapProps.load(new FileInputStream(sb.toString()));
          //System.out.println("sapProps : " + sapProps);
        }
        catch (IOException ex) {
          //System.out.println(ex);
        }
      }
      
      return sapProps;
    }  
    
    private static void createDestinationDataFile(String destinationName,Properties connectProperties){
    	
    
    	File destCfg = new File(destinationName+".jcoDestination");
    	if(destCfg.isFile()){
    		return;
    	}
    	
    	//System.out.println("destCfg.isFile() =" + destCfg.isFile());
    	//System.out.println(destCfg.getAbsolutePath());
    	try
    	{
    	FileOutputStream fos = new FileOutputStream(destCfg,false);
    	connectProperties.store(fos, "for tests only !");
    	//System.out.println("connectProperties =" +connectProperties);
    	//System.out.println("fos =" + fos.getFD().toString());
    	fos.close();
    	}
    	catch (Exception e)
    	{
    	throw new RuntimeException("Unable to create the destination files", e);
    	}
    }
    		
    public static void step2ConnectUsingPool() throws JCoException
    {
    JCoDestination destination =
    JCoDestinationManager.getDestination("e3ps");
    destination.ping();
    //System.out.println("Attributes:");
    //System.out.println(destination.getAttributes());
    //System.out.println();
    }
   
	
	
	public static void step1Connect() throws JCoException
    {
        JCoDestination destination = JCoDestinationManager.getDestination("e3ps");
        //System.out.println("Attributes:");
        //System.out.println(destination.getAttributes());
        //System.out.println();
        /*
        destination = JCoDestinationManager.getDestination(ABAP_MS);
        //System.out.println("Attributes:");
        //System.out.println(destination.getAttributes());
        //System.out.println();
        */
    }
	
	public static void step3SimpleCall() throws JCoException
	{		//.getStructure
		//table
		//단일 필드 
			JCoDestination destination =JCoDestinationManager.getDestination("e3ps");
			
			JCoFunction function = destination.getRepository().getFunction(FUNTION_ECO);
			if(function == null){
				throw new RuntimeException("STFC_CONNECTION not found in SAP.");
			}
			
			JCoParameterList  list = function.getTableParameterList();
			//JCoParameterList  exportList =function.getExportParameterList();
			
		
		
		
		
		JCoFieldIterator it= list.getFieldIterator();
		while(it.hasNextField()){
			
			//System.out.println(it.nextField().getName());
		}
		
		JCoParameterList list2 = function.getImportParameterList();
		
		JCoTable codes = function.getTableParameterList().getTable("IT_INPUT");
		//System.out.println("codes1 =" + codes.getNumRows());
		for (int i = 1;  i < 3 ; i++)
		{
		


			//codes.appendRows(i);
			codes.insertRow(i);
			codes.setValue("ZIFNO", "EE"+i);
			codes.setValue("AENNR", "test");
			codes.setValue("AETXT", "111");
			codes.setValue("DATUV", "111");
			codes.setValue("DATUV", "111");
			codes.setValue("AEGRU", "111");
			codes.setValue("ZECMID", "111");
			
			
		}
		
		//codes.deleteRow(10);
		//System.out.println("codes2 =" + codes.getNumRows());
		////System.out.println("IT_INPUT =" + codes.ge);
		
		
		//function.getTableParameterList().setValue("IT_INPUT", codes);//setValue("IT_INPUT",codes.getTable("IT_INPUT"));
		
		//
		function.execute(destination);
		
		
		
				
		
		/*
		//System.out.println("STFC_CONNECTION finished:");
		//System.out.println(" Echo: " +
		function.getExportParameterList().getString("ECHOTEXT"));
		//System.out.println(" Response: " +
		function.getExportParameterList().getString("RESPTEXT"));
		//System.out.println();
		*/
	}
	
	public static void step4WorkWithTable() throws JCoException
	{
	
		JCoDestination destination =JCoDestinationManager.getDestination("e3ps");
		
		JCoFunction function =destination.getRepository().getFunction(FUNTION_ECO);
		
		
		if (function == null)
			throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
		try
		{
			function.execute(destination);
		}
		catch(AbapException e)
		{
			//System.out.println(e.toString());
			return;
		}
		
		/*
		JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
		
		if (!(returnStructure.getString("TYPE").equals("")||returnStructure.getString("TYPE").equals("S")) )
		{
			throw new
			RuntimeException(returnStructure.getString("MESSAGE"));
		}
		*/
		JCoTable codes = function.getTableParameterList().getTable("IT_INPUT");
		//System.out.println("codes.getNumRows()="+ codes.getNumRows());
		//System.out.println("codes.getRow()="+ codes.getRow());
		for (int i = 0; i < codes.getNumRows(); i++)
		{
			codes.setRow(i);
			//System.out.println(codes.getString("AENNR") + '\t'	+ codes.getString("AETXT"));
		
		}
		
		codes.firstRow();
		for (int i = 0; i < codes.getNumRows(); i++,codes.nextRow())
		{
			function =destination.getRepository().getFunction("BAPI_COMPANYCODE_GETDETAIL");
			
			if (function == null)
				throw new	RuntimeException("BAPI_COMPANYCODE_GETDETAIL not found in SAP.");
			function.getImportParameterList().setValue("COMPANYCODEID",	codes.getString("COMP_CODE"));
			function.getExportParameterList().setActive("COMPANYCODE_ADDRESS",false);
			
			try
			{
				function.execute(destination);
			}
			catch (AbapException e)
			{
				//System.out.println(e.toString());
				return;
			}
			/*
			returnStructure =	function.getExportParameterList().getStructure("RETURN");
			if (! (returnStructure.getString("TYPE").equals("") ||
					returnStructure.getString("TYPE").equals("S") ||
					returnStructure.getString("TYPE").equals("W")) )
			{
				throw new
				RuntimeException(returnStructure.getString("MESSAGE"));
			}
			*/
			JCoStructure detail = function.getExportParameterList().getStructure("COMPANYCODE_DETAIL");
			//System.out.println(detail.getString("COMP_CODE") + '\t'	+detail.getString("COUNTRY") + '\t' +detail.getString("CITY"));
		}
	}
			

	
	public static void main(String[] args) {
		try {
			//ET_OUTPUT
			//IT_INPUT
			getLoginProperties();
			createDestinationDataFile("e3ps", sapProps);
			step3SimpleCall();
			//step4WorkWithTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

}
