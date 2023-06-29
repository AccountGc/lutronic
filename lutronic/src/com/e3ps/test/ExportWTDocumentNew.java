package com.e3ps.test;


import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.content.FileDown;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.util.WTException;

public class ExportWTDocumentNew implements wt.method.RemoteAccess, java.io.Serializable{
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	public static final ExportWTDocumentNew manager = new ExportWTDocumentNew();
	
	public static void main(String[] args)throws Exception{
		
		//windchill com.e3ps.test.ExportWTDocumentNew wcadmin lutadmin12# MO
		
		RemoteMethodServer.getDefault().setUserName(args[0]);
		RemoteMethodServer.getDefault().setPassword(args[1]);
		 
		// String userName = args[0];
		// String password = args[1];
		 String rootfolderName = args[2];
		//
		try {
			ExportWTDocumentNew.manager.extrract(/*userName,password,*/rootfolderName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void extrract(/*String userName,String password,*/String rootfolderName) throws SQLException {
		if (!SERVER) {
			Class argTypes[] = new Class[]{String.class};
			Object args[] = new Object[]{ rootfolderName};
			try {
				RemoteMethodServer.getDefault().invoke("extrract", null, this, argTypes, args);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String output = "D:\\epmFileBackup\\";
		//System.out.println("Given Output Directory is :: "+output);
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

        PreparedStatement st = null;

		if(null==rootfolderName || rootfolderName.length()==0){
			//System.out.println("Given Root Folder insert Please.... ex)OLD");
		}
		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			con = wtconnection.getConnection();
			if(con != null) {
				stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			}
			StringBuffer sb = new StringBuffer();
			
			sb.append("SELECT A3.NAME FOLDERNAM, A0.IDA2A2 EPMOID ");
			sb.append("FROM EPMDocument A0,ITERFOLDERMEMBERLINK A1,SUBFOLDER A3,EPMDOCUMENTMASTER A4  ");
			sb.append("WHERE (A1.branchIdA3B5 = A0.branchIditerationInfo) ");
			sb.append("AND (A4.IDA2A2 = A0.IDA3MASTERREFERENCE)  ");
			sb.append("AND A3.ida2a2 in (select ida2a2 from subfolder connect by prior ida2a2 = IDA3B2FOLDERINGINFO start with name='"+rootfolderName+"' ) ");
			sb.append("AND (A0.LATESTITERATIONINFO=1)");
			sb.append("AND (A3.IDA2A2=A1.idA3A5)");
			//System.out.println(sb.toString());
			if(stmt != null) {
				rs = stmt.executeQuery(sb.toString());
				String epmoid = null;
				String fileNAME = null;
				while(rs.next()) {
					epmoid = "wt.epm.EPMDocument:"+rs.getString("EPMOID");
					
					EPMDocument epm = (EPMDocument) CommonUtil.getObject(epmoid);
					epm = (EPMDocument) ObjectUtil.getLatestVersion(epm);
					fileNAME = epm.getCADName();
					String location = epm.getLocation().substring("/Default".length());
					//System.out.println("lc="+ location);
					//System.out.println("fileNAME="+ fileNAME);
					StringBuffer sb2 = new StringBuffer(location);
					for(int i=0;i<sb2.toString().length();i++){
						char tmp = sb2.toString().charAt(i);
						char dataC = '/';
						char dataA = '\\';
						if(tmp == dataC){
							sb2.setCharAt(i, dataA);
						}
					}
					location = sb2.toString();
					//System.out.println("lc after"+ location);
					String currDatestr = DateUtil.getCurrentDateString("d");
					//System.out.println("fOLDER NAME : "+(output+currDatestr+location));
					File dir = new File(output+currDatestr+location);
					
					if(!dir.isDirectory()){
						dir.mkdirs();
					}
					extractContent(epm, output+currDatestr+location,fileNAME);
				}
			}
		}catch(Exception e){
		    e.printStackTrace();
		}finally{
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if (DBProperties.FREE_CONNECTION_IMMEDIATE
					&& !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
	}

	public void extractContent(EPMDocument epm, String output,String fileNAME) throws WTException {
		if (!SERVER) {

			Class argTypes[] = new Class[]{EPMDocument.class, String.class,String.class};
			Object args[] = new Object[]{ epm,output,fileNAME};
			try {
				RemoteMethodServer.getDefault().invoke("extractContent", null, this, argTypes, args);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		try{
			EPMDocument seed = (EPMDocument) PersistenceHelper.manager.refresh(epm);
			seed = (EPMDocument) ContentHelper.service.getContents (seed);
			ContentItem primaryContent = ContentHelper.getPrimary (seed);
			if (primaryContent!= null) {
				ApplicationData appdata = (ApplicationData) primaryContent;
			
			
			String errorMessage = "";
			EPMDocument syHold = null;
			//System.out.println("Value of syHold:: before "+syHold);
			syHold = (EPMDocument)wt.content.ContentHelper.service.getContents(epm);
			//System.out.println("Value of syHold:: after "+syHold);
			 
			java.util.Vector vectorOfAppData = wt.content.ContentHelper.getContentListAll( syHold );
			
			 
				if( vectorOfAppData != null){
					java.util.Iterator appIter = vectorOfAppData.iterator();
					while (appIter.hasNext()){
						wt.content.ApplicationData syAppData = (wt.content.ApplicationData)(appIter.next());
						String docName = syAppData.getFileName();
						long fileSize = syAppData.getFileSize();
						//System.out.println("docNAME = "+docName+"\tOutput="+output+"\tfileNAME="+fileNAME);
					    Map<String, Object> map = new HashMap<String, Object>();
						map.put("epm", epm);
						map.put("adata", appdata);
						map.put("cadFileName", docName);
						map.put("tempDir",output);
					   // FileDown.drawingDown(map);
						 //    Files.copy(in, Paths.get(output+"\\"+docName),StandardCopyOption.REPLACE_EXISTING);
						/*// Save the content file to disk on the Windchill server
						 ContentServerHelper.service.writeContentStream((ApplicationData) syAppData,
							        new File(output, docName).getCanonicalPath());*/
					}
				}
			}
			
		}catch (Exception e){
			//System.out.println("--> WTException");
			
			e.printStackTrace();
		}
	}
}
