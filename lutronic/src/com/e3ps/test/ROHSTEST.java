package com.e3ps.test;

import java.io.File;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import com.e3ps.common.util.AuthHandler;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.JExcelUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.rohs.ROHSContHolder;

public class ROHSTEST {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			String userId = "wcadmin";
			wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer.getDefault();
			methodServer.setAuthenticator(AuthHandler.getMethodAuthenticator(userId));
	    	
			String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
	        String sFilePath = sWtHome + "\\loadFiles\\e3ps\\ROHS\\발행일수정3.xls" ;
	        updateROHS(sFilePath);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void updateROHS(String filepath){
		
		
		
		File newfile =  new File(filepath);
		Workbook wb = JExcelUtil.getWorkbook(newfile);
		Sheet[] sheets = wb.getSheets();
    	int rows = sheets[0].getRows();
    	
    	for (int j = 1; j < rows; j++){
    		Cell[] cell = sheets[0].getRow(j);
    		String publicationdate = JExcelUtil.getContent(cell, 0).trim();
    		
    		String fileName = JExcelUtil.getContent(cell, 1);
    		
    		
    		//System.out.println(fileName + "===============" +publicationdate);
    		publicationdate = StringUtil.checkNull(publicationdate);
    		getROHSHolder( fileName, publicationdate, j);
    	}
    		
	}
	
	public static void getROHSHolder(String fileName,String publicationdate,int j){
		
		try{
			QuerySpec qs = new QuerySpec(ROHSContHolder.class);
			
			qs.appendWhere(new SearchCondition(ROHSContHolder.class, ROHSContHolder.FILE_NAME, SearchCondition.EQUAL , fileName), new int[] {0});
			
			//System.out.println(qs.toString());
			QueryResult rt = PersistenceHelper.manager.find(qs);
			
			while(rt.hasMoreElements()){
				ROHSContHolder holder = (ROHSContHolder)rt.nextElement();
				//System.out.println(j+" . "+CommonUtil.getOIDString(holder)+"::::::::"+fileName + "===============" +publicationdate );
				if(publicationdate.length()>0){
					holder.setPublicationDate(publicationdate);
					PersistenceHelper.manager.modify(holder);
				}
				
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
