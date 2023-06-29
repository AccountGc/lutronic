package com.e3ps.load;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Date;
import java.util.StringTokenizer;

import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteMethodServer;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.*;
import wt.util.*;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.query.*;
import com.e3ps.change.*;
import com.e3ps.common.util.*;
import java.text.*;
import jxl.*;
import com.e3ps.org.*;
import java.io.*;

public class ChangeLoader {

	public static void main(String[] args)throws Exception{
		
		String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
        String sFilePath = sWtHome + "\\loadFiles\\e3ps\\ChangeActivityDefinition.xls" ;
		
        if(args!=null && args.length>0 && args[0].trim().length()>0){
        	sFilePath = args[0];
        }
		
        setUser(args[1], args[2]);
        	
		new ChangeLoader().loadDefinition(sFilePath);
	}
	
	public static void setUser(final String id, final String pw)
    {
        RemoteMethodServer.getDefault().setUserName(id);
        RemoteMethodServer.getDefault().setPassword(pw);
    }
	
	public void loadDefinition(String sFilePath)throws Exception{
		

		
		File newfile =  new File(sFilePath);

		//System.out.println(newfile.exists());

		Workbook wb = JExcelUtil.getWorkbook(newfile);

		Sheet[] sheets = wb.getSheets();

		String oid[] = new String[6];
		int[] sortnum = new int[6];
 
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat dformat = new DecimalFormat("000000");

        for (int i = 0; i < sheets.length; i++)
        {
            int rows = sheets[i].getRows();
            for (int j = 1; j < rows; j++)
            {
                if (JExcelUtil.checkLine(sheets[i].getRow(j),0))
                {
                    Cell[] cell = sheets[i].getRow(j);
                    String eoType = JExcelUtil.getContent(cell, 0).trim();
                    String name = JExcelUtil.getContent(cell, 1).trim();
                    String name_eng = JExcelUtil.getContent(cell, 2).trim();
                    String description = JExcelUtil.getContent(cell, 3).trim();
                    String activeType = JExcelUtil.getContent(cell, 4).trim();
                    String step = JExcelUtil.getContent(cell, 5).trim();
                    String activeCode = JExcelUtil.getContent(cell, 6).trim();
                    String activeGroupName = JExcelUtil.getContent(cell, 7).trim();
                    String activeGroupCode = JExcelUtil.getContent(cell, 8).trim();
                    boolean isApprove = JExcelUtil.getContent(cell, 9).trim().toUpperCase().equals("TRUE")?true:false;
                    boolean isNeed  = JExcelUtil.getContent(cell, 10).trim().toUpperCase().equals("TRUE")?true:false;
                    boolean isDisabled = JExcelUtil.getContent(cell, 11).trim().toUpperCase().equals("TRUE")?true:false;
                    String activeState = JExcelUtil.getContent(cell, 12).trim();
                    boolean isDocument = JExcelUtil.getContent(cell, 13).trim().toUpperCase().equals("TRUE")?true:false;
                    boolean isDocState = JExcelUtil.getContent(cell, 14).trim().toUpperCase().equals("TRUE")?true:false;
                    boolean isAttach = JExcelUtil.getContent(cell, 15).trim().toUpperCase().equals("TRUE")?true:false;
                    //System.out.println(">> eoType" + eoType +":"+ name +":" + isNeed +"==" + JExcelUtil.getContent(cell, 10).trim().toUpperCase());
                    EChangeActivityDefinition def = getActivity(eoType,name);
                    
                    if(def==null){
                    	def = EChangeActivityDefinition.newEChangeActivityDefinition();
                    }
                    def.setEoType(eoType);
                    def.setName(name);
                    def.setName_eng(name_eng);
                    def.setDescription(description);
                    def.setActiveType(activeType);
                    def.setStep(step);
                    def.setSortNumber(j);
                    def.setActiveCode(activeCode);
                    def.setIsAprove(isApprove);
                    def.setIsNeed(isNeed);
                    def.setActiveGroupName(activeGroupName);
                    def.setActiveGroup(activeGroupCode);
                    def.setIsDisabled(isDisabled);
                    def.setActiveState(activeState);
                    def.setIsDocument(isDocument);
                    def.setIsDocState(isDocState);
                    def.setIsAttach(isAttach);
                    def = (EChangeActivityDefinition)PersistenceHelper.manager.save(def);
                    
                }
            }
        }
	}
	
	public EChangeActivityDefinition getActivity(String eoType,String name)throws Exception{
		QuerySpec qs = new QuerySpec(EChangeActivityDefinition.class);
		qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class,"name","=",name),new int[]{0});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class,EChangeActivityDefinition.EO_TYPE
				,"=",eoType),new int[]{0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if(qr.hasMoreElements()){
			return (EChangeActivityDefinition)qr.nextElement();
		}
		return null;
	}
	
	public Department getDepartment(String name)throws Exception{
		QuerySpec qs = new QuerySpec(Department.class);
		qs.appendWhere(new SearchCondition(Department.class,"name","=",name),new int[]{0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if(qr.hasMoreElements()){
			return (Department)qr.nextElement();
		}
		return null;
	}
}

