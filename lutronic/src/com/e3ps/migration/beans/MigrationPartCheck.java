package com.e3ps.migration.beans;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.AuthHandler;
import com.e3ps.common.util.JExcelUtil;
import com.e3ps.part.service.PartHelper;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import wt.part.WTPart;
import wt.util.WTException;

public class MigrationPartCheck implements wt.method.RemoteAccess, java.io.Serializable{
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
    public static MigrationPartCheck manager = new MigrationPartCheck();
    
    int totalCount = 0;
	int sCount = 0;
	int eCount = 0;

	public static void main(String[] args)throws Exception{
		
		String userId = "wcadmin";
		wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer.getDefault();
		methodServer.setAuthenticator(AuthHandler.getMethodAuthenticator(userId));
    	
		String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
        String sFilePath = sWtHome + "\\loadFiles\\e3ps\\Part_Loader_160119.xls" ;
		
        if(args!=null && args.length>0 && args[0].trim().length()>0){
        	sFilePath = args[0];
        }
        //D:\ptc\Windchill_10.2\Windchill\loadFiles\e3ps\Part_Loader_160109.xls
        
        String isTest = args[1];
        
        //System.out.println("sFilePath = " + sFilePath);
        //System.out.println("isTEST = " + sFilePath);
        MigrationPartCheck.manager.changePartCheck(sFilePath, isTest);
		
	}
	
	public void changePartCheck(String sFilePath, String isTest)throws Exception{
		
        if(!SERVER) {
            Class argTypes[] = new Class[]{String.class, String.class};
            Object args[] = new Object[]{sFilePath, isTest};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "changePartCheck",
                        null,
                        this,
                        argTypes,
                        args);
               return;
            } catch(RemoteException e) {
                e.printStackTrace();
                throw new WTException(e);
            } catch(InvocationTargetException e) {
                e.printStackTrace();
                throw new WTException(e);
            } catch(Exception e){
                e.printStackTrace();
                throw e;
            }
        }

        WTPart part = null;
        long start = System.currentTimeMillis();
        try {
        	File newfile =  new File(sFilePath);
    		Workbook wb = JExcelUtil.getWorkbook(newfile);
    		Sheet[] sheets = wb.getSheets();
        	int rows = sheets[0].getRows();
        	
        	for (int j = 1; j < rows; j++)
            {
        		totalCount++;
        		Cell[] cell = sheets[0].getRow(j);
        		String partNumber = JExcelUtil.getContent(cell, 0).trim();
        		partNumber = partNumber.toUpperCase();
        		String partCheck = JExcelUtil.getContent(cell, 8);
        		
        		part = PartHelper.service.getPart(partNumber);
        		
        		try{
        			//System.out.println("["+j+"]partNumber = "+partNumber+", partCheck = "+partCheck+", Modify");
        			if(isTest.equalsIgnoreCase("TRUE")){
        				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_PARTCHECK, partCheck, "string");
        			}
        			sCount++;
        		}catch(Exception e){
        			 e.printStackTrace();
        	         //System.out.println("for in Error");
        			 eCount ++;
        		}
             }
       } catch(Exception e) {
           e.printStackTrace();
           //System.out.println("for out Error");
           
       } 
        long end = System.currentTimeMillis();
        //System.out.println("totalCount = " + totalCount);
        //System.out.println("sCount = " + sCount);
        //System.out.println("eCount = " + eCount);
        //System.out.println("####  [MigrationPartHelper] Run Time : " + (end - start) / 1000.0f + " sec");
       
	}
}
