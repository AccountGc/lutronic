package com.e3ps.migration.beans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.drawing.service.DrawingHelper;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.epm.E3PSRENameObject;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.vc.VersionControlHelper;

public class MigrationRename implements wt.method.RemoteAccess, java.io.Serializable{
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
	public static MigrationRename manager = new MigrationRename();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String change = args[0];
		
		
		
		MigrationRename.manager.getRenameList(change);
		//windchill com.e3ps.migration.beans.MigrationRename false;
		//windchill com.e3ps.migration.beans.MigrationRename true;
	}
	
	
	
	public void getRenameList(String  change){
		
		if(!SERVER) {
            Class argTypes[] = new Class[]{String.class};
            Object args[] = new Object[]{change};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "getRenameList",
                        null,
                        this,
                        argTypes,
                        args);
               return;
            } catch(RemoteException e) {
                e.printStackTrace();
               
            } catch(InvocationTargetException e) {
                e.printStackTrace();
               
            } catch(Exception e){
                
                
            }
        }
		//cadDivision
		
		//System.out.println("============= getRenameList ====================" );
		//DrawingHelper.service.getListQuery(hash);
		boolean isChange = "true".equals(change);
		//System.out.println("============= getRenameList isChange =" + isChange );
		try{
			QuerySpec query = new QuerySpec();
			int idx = query.addClassList(EPMDocument.class, true);
			
			//최신 iteration
			query.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[]{idx});
			
			//최신 버전
			SearchUtil.addLastVersionCondition(query, EPMDocument.class, idx);
			
			query.appendAnd();
			query.appendOpenParen();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , "OTHER", false), new int[]{idx});
				query.appendOr();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , "ACAD", false), new int[]{idx});
				query.appendOr();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , "ORCAD", false), new int[]{idx});
				
			query.appendCloseParen();
			
			query.appendAnd();
			query.appendWhere(new SearchCondition(EPMDocument.class,"master>number", SearchCondition.NOT_LIKE , "PTC_WM%", false), new int[]{idx});
			
			//query.
			//System.out.println(query);
			QueryResult rt = PersistenceHelper.manager.find(query);
			int totalCount = rt.size();
			int errorCount = 0;
			int normalCount = 0;
			while(rt.hasMoreElements()){
				boolean check = true;
				
				Object[] o = (Object[]) rt.nextElement();
				EPMDocument epm = (EPMDocument) o[0];
				
				String fileName= getCADName(epm);
				String cadName = epm.getCADName();
		    	String log = epm.getNumber()+","+epm.getAuthoringApplication().toString()+","+cadName+","+fileName;
		    	if(!fileName.equals(cadName)){
		    		check = false;
		    		errorCount++;
		    		if(isChange){
		    			E3PSRENameObject.manager.EPMReName(epm, "", "", fileName, false);
		    		}
		    		
		    		
		    		createLog(log, "FileName_ERROR");
		    	}else{
		    		normalCount++;
		    		createLog(log, "FileName_NORMAL");
		    	}
		    	
		    	//System.out.println(log);
			}
			
			//System.out.println("totalCount = " + totalCount);
			//System.out.println("errorCount = " + errorCount);
			//System.out.println("normalCount = " + normalCount);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	
	}
	
	public String getCADName(EPMDocument epm) throws Exception{
    	
    	
    	
    	QueryResult result = ContentHelper.service.getContentsByRole ((ContentHolder)epm ,ContentRoleType.PRIMARY );
    	String fileName = "";
    	ContentItem primaryFile = null;
    	while (result.hasMoreElements ()) {
    		primaryFile = (ContentItem) result.nextElement();
    		fileName = ((ApplicationData)primaryFile).getFileName();
    	}
    	
    	return fileName;
    }
	
	public void createLog(String log,String fileName) {
		//System.out.println("========== "+fileName+" ===========");
		String filePath = "D:\\e3ps\\CadName";
		
		File folder = new File(filePath);
		
		if(!folder.isDirectory()){
			
			folder.mkdirs();
		}
		fileName = fileName.replace(",", "||");
		String toDay = com.e3ps.common.util.DateUtil.getCurrentDateString("date");
		toDay = com.e3ps.common.util.StringUtil.changeString(toDay, "/", "-");
		String logFileName = fileName+"_" + toDay.concat(".log");
		String logFilePath = filePath.concat(File.separator).concat(logFileName);
		File file = new File(logFilePath);
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PrintWriter out = new PrintWriter(new BufferedWriter(fw), true);
		out.write(log);
		//System.out.println(log);
		out.write("\n");
		out.close();
	}
	
}
