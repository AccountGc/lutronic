package com.e3ps.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.AuthHandler;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.migration.service.MigrationHelper;
import com.e3ps.part.dto.PartTreeData;
import com.e3ps.part.service.BomSearchHelper;
import com.e3ps.part.service.PartHelper;
import com.ptc.core.foundation.associativity.common.BOMHelper;

import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMMemberLink;
import wt.epm.util.EPMConfigSpecFilter;
import wt.epm.util.EPMSearchHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.vc.VersionControlHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class AssemblyPartCheck {
	
	public static HashMap<String , String> doubleMap = new HashMap<String, String>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			
			doubleMap = new HashMap<String, String>();
			String userId = "wcadmin";
			wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer
					.getDefault();
			methodServer.setAuthenticator(AuthHandler.getMethodAuthenticator(userId));
			String cadType="CADASSEMBLY";///CADCOMPONENT,CADASSEMBLY,CADDRAWING
			long startTime = System.currentTimeMillis();
			getEPMdocument(cadType);
			long endTime = System.currentTimeMillis();;
			long time = endTime - startTime;
			//System.out.println("Total time =" + time);
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
			
			//String predate_modify="2018-03-12";
			
			SearchUtil.addLastVersionCondition(query, EPMDocument.class, idx);;
			
			
			if(cadDivision.length()>0){
				query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , cadDivision, false), new int[]{idx});
			}
			
			
			if(cadType.length()>0){
				query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , cadType, false), new int[]{idx});
			}
			
			//if(predate_modify.length() > 0) {
			//	if(query.getConditionCount() > 0) { query.appendAnd(); }
			//	query.appendWhere(new SearchCondition(EPMDocument.class,"thePersistInfo.modifyStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate_modify)), new int[]{idx});
			//}
			
			
			QueryResult rt  = PersistenceHelper.manager.find(query);
			
			int count =1;
			
			while(rt.hasMoreElements()){
				Object[] obj = (Object[])rt.nextElement();
				
				EPMDocument epm = (EPMDocument)obj[0];
				String number = epm.getNumber();
				
				String log = count+" ," + number;
				createLog(log, "Assembly");
				if(doubleMap.containsKey(number)){
					
					String skipLog =number;
					createLog(skipLog, "SKIPEPMDocument");
					continue;
				}else{
					String addLog =number;
					createLog(addLog, "ADDEPMDocument");
					doubleMap.put(number, number);
				}
				log = count+"  ::::: ASSEMBLY START :" + number;
				createLog(log, "AssemblyPartCheck");
				getEPMChild2(epm);
				log = count+ "  :::::  ASSEMBLY End :" + number;
				createLog(log, "AssemblyPartCheck");
				//MigrationHelper.service.publish(epm);
					
				
				
				count++;
			
				//}
			}
			//System.out.println(query.toString());
			//System.out.println("rt.size ="+rt.size());
			
			String totalcount = "Total Assembly Count =" + rt.size();
			createLog(query.toString(), "AssemblyPartCheck");
			
			createLog(totalcount, "AssemblyPartCheck");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void getEPMChild(EPMDocument pEpm){
		
		try{
			
			
			
			long oidLong = CommonUtil.getOIDLongValue(pEpm);
			QuerySpec qs = new QuerySpec();
			Class cls = EPMMemberLink.class;
			int idx = qs.addClassList(cls, true);
			
			qs.appendWhere(new SearchCondition(EPMMemberLink.class,"roleAObjectRef.key.id", SearchCondition.EQUAL, oidLong), new int[]{idx});
			
			QueryResult rt = PersistenceHelper.manager.find(qs);
			//System.out.println(qs.toString());
			while(rt.hasMoreElements()){
				Object[] obj = (Object[])rt.nextElement();
				
				EPMMemberLink link = (EPMMemberLink)obj[0];
				
				EPMDocumentMaster master = (EPMDocumentMaster)link.getRoleBObject();
				
				EPMDocument epm = EpmSearchHelper.service.getLastEPMDocument(master);
				
				
				String number = epm.getNumber();
				if(doubleMap.containsKey(number)){
					
					String skipLog =number;
					createLog(skipLog, "SKIPEPMDocument");
					continue;
				}else{
					String addLog =number;
					createLog(addLog, "ADDEPMDocument");
					doubleMap.put(number, number);
				}
				
				WTPart part = DrawingHelper.service.getWTPart(pEpm);
				
				if(part == null){
					String log = "NON Part Parent epm ," + pEpm.getNumber() +"," + epm.getNumber();
					createLog(log, "NOnPart");
					createLog(log, "AssemblyPartCheck");
				}
				getEPMChild(epm);
				//
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

public static void getEPMChild2(EPMDocument pEpm){
		
		try{
			
		
				WTPart part = DrawingHelper.service.getWTPart(pEpm);
				
				if(part != null){
					String log = "";
					//String log = "Start Parent Part Number : " + part.getNumber() ;
					//createLog(log, "OneLevel_List");
					//log = "Start Parent Part Number : " + part.getNumber() ;
					//createLog(log, "OneLevel_ExistNo");
					View view = ViewHelper.service.getView("Design");
					List<Object[]> list = BomSearchHelper.service.descentLastPart(part, view, null);
					if(list.size()==0){
						log = "Top Assy : "+part.getNumber() ;
						createLog(log, "OneLevel_ExistNo");
					}
					//log = "End Parent Part Number : " + part.getNumber() ;
					//createLog(log, "OneLevel_List");
					//createLog(log, "OneLevel_ExistNo");
					//createLog(log, "AssemblyPartCheck");
				}
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
