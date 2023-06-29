package com.e3ps.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.AuthHandler;
import com.e3ps.common.util.StringUtil;
import com.e3ps.drawing.util.EpmPublishUtil;
import com.e3ps.migration.service.MigrationHelper;
import com.ptc.wvs.server.util.PublishUtils;

import wt.epm.EPMDocument;
import wt.epm.EPMDocumentHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.vc.VersionControlHelper;

public class PublishCheck {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			String userId = "wcadmin";
			wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer
					.getDefault();
			methodServer.setAuthenticator(AuthHandler.getMethodAuthenticator(userId));
			String cadType="CADASSEMBLY";///CADCOMPONENT,CADASSEMBLY,CADDRAWING
			
			getEPMdocument(cadType);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void getEPMdocument(String cadType){
		
		try{
			Vector<EPMDocument> vec = new Vector<EPMDocument>();
			String cadDivision ="PROE";
			
			QuerySpec query = new QuerySpec();
			
			int idx = query.addClassList(EPMDocument.class, true);
			
			if(query.getConditionCount() > 0) { query.appendAnd(); }
			query.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[]{idx});
			
			
			
			SearchUtil.addLastVersionCondition(query, EPMDocument.class, idx);;
			
			
			if(cadDivision.length()>0){
				query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , cadDivision, false), new int[]{idx});
			}
			
			//CAD Ÿ��
			if(cadType.length()>0){
				query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , cadType, false), new int[]{idx});
			}
			
			//System.out.println(query.toString());
			QueryResult rt  = PersistenceHelper.manager.find(query);
			createLog("=================START =================", cadType);
			int count =1;
			while(rt.hasMoreElements()){
				Object[] obj = (Object[])rt.nextElement();
				
				EPMDocument epm = (EPMDocument)obj[0];
				
				Representation representation = PublishUtils.getRepresentation(epm);
				if(representation == null){
					
					vec.addElement(epm);
					
					//boolean isPublish = false;
					//boolean isPublish = MigrationHelper.service.publish(epm);
					MigrationHelper.service.publish(epm);
					//EpmPublishUtil.publish(epm);
					
					
					String log = count+"."+epm.getNumber() + "," + epm.getCADName();
					//System.out.println(log);
					createLog(log, cadType);
					count++;
				}
			}
			
			String msg ="========== Total NoN pulbish = "+ vec.size() +"==========";
			createLog(msg, cadType);
			createLog("=================END =================", cadType);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void createLog(String log,String fileName) {
		//System.out.println("========== createLog ===========");
		String filePath = "D:\\e3ps\\ECDocument";
		//System.out.println(log);
		File folder = new File(filePath);
		
		if(!folder.isDirectory()){
			
			folder.mkdirs();
		}
		
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
		out.write("\n");
		out.close();
	}
	
}
