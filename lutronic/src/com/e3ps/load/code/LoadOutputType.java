/*
 * @(#) LoadOutputTypeStep.java  Create on 2004. 12. 20.
 * Copyright (c) e3ps. All rights reserverd
 */
package com.e3ps.load.code;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;

import com.e3ps.common.util.AuthHandler;
import com.e3ps.common.util.JExcelUtil;

public class LoadOutputType
{
    /**
     * 
     */
    private static String TYPE;
    public static void main(String[] args) throws IOException
    {
    	String userId = "wcadmin";
		try {
			wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer.getDefault();
			methodServer.setAuthenticator(AuthHandler.getMethodAuthenticator(userId));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
        if(args.length < 1)
        {
            //System.out.println("> input output.xls file location");
            System.exit(0);
        }
        
        LoadOutputType loader = new LoadOutputType();
        String file = args[0];
        
        File newfile = null;
        try
        {
            newfile = new File(file);
            //System.out.println("File : " + newfile.getName());
            if (!newfile.getName().endsWith(".xls"))
                return;
        }
        catch (Exception e)
        {
            return;
        }
        Workbook wb = JExcelUtil.getWorkbook(newfile);
        loader.load(wb);
        System.exit(0);
    }
    
    private void load(Workbook wb)
    {
        Sheet[] sheets = wb.getSheets();
        int rows = sheets[0].getRows();
        for (int j = 1; j < rows; j++)
        {
        	//createOutputType(sheets[0].getRow(j));
        }
    }

}