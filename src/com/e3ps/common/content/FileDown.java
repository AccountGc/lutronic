package com.e3ps.common.content;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import wt.content.ApplicationData;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.util.WTException;
import wt.util.WTProperties;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.drawing.beans.EpmUtil;

public class FileDown implements wt.method.RemoteAccess{

	/**
	 * @param args
	 */
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String oid = args[0];
		try {
			HashMap map = new HashMap();
			map.put("oid", oid);
			pdfDown(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static HashMap pdfDown( HashMap  map ) throws Exception{
		
		if(!SERVER) {
			Class argTypes[] = new Class[]{HashMap.class};
			Object args[] = new Object[]{map};
			try {
				return (HashMap)wt.method.RemoteMethodServer.getDefault().invoke("pdfDown",FileDown.class.getName(),null,argTypes,args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}
		
		HashMap mapRe = new HashMap();
		try{
			String tempPath = "";
			String oid 			= (String)map.get("oid");
			String empOid 		= (String)map.get("empOid");
			String tempDir 		= (String)map.get("tempDir");
			String pdfFileName 	= (String)map.get("pdfFileName");
			
			
			String epmType		= StringUtil.checkNull((String)map.get("epmType"));
			if(tempDir == null){
				tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
			}
			
			File tempFolder = new File(tempDir);
			if(!tempFolder.isDirectory()){
				tempFolder.mkdirs();
			}
			EPMDocument epm = (EPMDocument)CommonUtil.getObject(empOid);
			String epmName = epm.getName();
			ApplicationData adata = (ApplicationData)CommonUtil.getObject(oid);
			
			byte[] buffer = new byte[1024];
			    InputStream is = null;
			    if(epmType.equals("AutoCad")){
			    	
			    	//is = ContentServerHelper.service.findContentStream(adata);
			    	is = ContentServerHelper.service.findLocalContentStream(adata);
			    	//System.out.println("::::::::::::::::::::: InputStream is : " + is);
			    	//InputStream is2 = ContentServerHelper.service.findLocalContentStream(adata);
			    	//System.out.println("::::::::::::::::::::: InputStream is2 : " + is2);
			    }else{
			    	is = ContentServerHelper.service.findLocalContentStream(adata);
			    }
			    
			   
			    if(pdfFileName == null ) pdfFileName = adata.getFileName();
			    
			   
			    /*
			    pdfFileName = EpmUtil.getFileNameNonExtension(epm.getCADName());
			    String extension1 = EpmUtil.getExtension(epm.getCADName());
			    pdfFileName = pdfFileName+"_"+epm.getName();
			    System.out.println("::::::::::::::::::::: pdfDown2 pdfFileName : " + pdfFileName);
			    String extension2 = EpmUtil.getExtension(adata.getFileName());
			    pdfFileName = pdfFileName+"."+extension2;
			    System.out.println("::::::::::::::::::::: pdfDown4 pdfFileName : " + pdfFileName);
			    */
			    pdfFileName = EpmUtil.getPublishFile(epm, pdfFileName);
			    
			    //System.out.println("::::::::::::::::::::: pdfDown1 pdfFileName : " + pdfFileName);
			    File tempfile = new File(tempDir + File.separator + pdfFileName);
		        //System.out.println("::::::::::::::::::::: pdfDown2 tempfile : " + tempfile.getName());
		        FileOutputStream fos = new FileOutputStream(tempfile);
		        tempPath = tempfile.getPath();
		        
		        int j = 0;
		        while ((j = is.read(buffer, 0, 1024)) > 0){
		        	
		        	fos.write(buffer, 0, j);
		        }
		            
		        
		        fos.close();
		        is.close();
		        
		        mapRe.put("tempPath", tempPath);
//		        mapRe.put("result", ERPUtil.PDF_SEND_SUCCESS);
		        mapRe.put("message", "����");
		}catch(Exception e){
			mapRe.put("tempPath", "");
//	        mapRe.put("result", ERPUtil.PDF_SEND_FAILE);
	        mapRe.put("message", e.getMessage());
			e.printStackTrace();
		}
		
		return mapRe;
		
	}
	
public static HashMap drawingDown( Map<String, Object>  map ) throws Exception{
		
		if(!SERVER) {
			Class argTypes[] = new Class[]{HashMap.class};
			Object args[] = new Object[]{map};
			try {
				return (HashMap)wt.method.RemoteMethodServer.getDefault().invoke("pdfDown",FileDown.class.getName(),null,argTypes,args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}
		
		HashMap mapRe = new HashMap();
		try{
			String tempPath = "";
			String tempDir 		= (String)map.get("tempDir");
			String cadFileName 	= (String)map.get("cadFileName");
			
			
			String epmType		= StringUtil.checkNull((String)map.get("epmType"));
			if(tempDir == null){
				tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
			}
			
			File tempFolder = new File(tempDir);
			if(!tempFolder.isDirectory()){
				tempFolder.mkdirs();
			}
			EPMDocument epm = (EPMDocument)map.get("epm");
			String epmName = epm.getName();
			ApplicationData adata = (ApplicationData)map.get("adata");
			
			byte[] buffer = new byte[64];
			    InputStream is = null;
			    if(epmType.equals("AutoCad")){
			    	
			    	//is = ContentServerHelper.service.findContentStream(adata);
			    	is = ContentServerHelper.service.findContentStream(adata);
			    	//System.out.println("::::::::::::::::::::: InputStream is : " + is);
			    	//InputStream is2 = ContentServerHelper.service.findLocalContentStream(adata);
			    	//System.out.println("::::::::::::::::::::: InputStream is2 : " + is2);
			    }else{
			    	is = ContentServerHelper.service.findContentStream(adata);
			    }
			    
			   
			    /*
			    pdfFileName = EpmUtil.getFileNameNonExtension(epm.getCADName());
			    String extension1 = EpmUtil.getExtension(epm.getCADName());
			    pdfFileName = pdfFileName+"_"+epm.getName();
			    System.out.println("::::::::::::::::::::: pdfDown2 pdfFileName : " + pdfFileName);
			    String extension2 = EpmUtil.getExtension(adata.getFileName());
			    pdfFileName = pdfFileName+"."+extension2;
			    System.out.println("::::::::::::::::::::: pdfDown4 pdfFileName : " + pdfFileName);
			    */
			    File tempfile = new File(tempDir + File.separator + cadFileName);
		        FileOutputStream fos = new FileOutputStream(tempfile);
		        tempPath = tempfile.getPath();
		        
		        int j = 0;
		        while ((j = is.read(buffer)) > 0){
		        	
		        	fos.write(buffer, 0, j);
		        }
		            
		        
		        fos.close();
		        is.close();
		        
		        mapRe.put("tempPath", tempPath);
//		        mapRe.put("result", ERPUtil.PDF_SEND_SUCCESS);
		        mapRe.put("message", "success");
		}catch(Exception e){
			mapRe.put("tempPath", "");
//	        mapRe.put("result", ERPUtil.PDF_SEND_FAILE);
	        mapRe.put("message", e.getMessage());
			e.printStackTrace();
		}
		
		return mapRe;
		
	}
	
	public static void pdfd( HashMap map,HttpServletResponse response ) throws Exception{
		
		if(!SERVER) {
			Class argTypes[] = new Class[]{HashMap.class,HttpServletResponse.class};
			Object args[] = new Object[]{map,response};
			try {
				wt.method.RemoteMethodServer.getDefault().invoke("pdfd",FileDown.class.getName(),null,argTypes,args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			
			return;
		}
		
		try{
			String oid =(String)map.get("oid");
			String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
			ContentItem item = null;
			
			
			ApplicationData adata = (ApplicationData)CommonUtil.getObject(oid);
			//System.out.println("ApplicationData :" + adata.getFileName());
	        byte[] buffer = new byte[1024];
			    InputStream in = ContentServerHelper.service.findLocalContentStream(adata);
			   
			    BufferedInputStream fin = null;
                fin = new BufferedInputStream(in);
                
                BufferedOutputStream outs = new BufferedOutputStream(response.getOutputStream());
			  
		        int read = 0;
                byte b[] = new byte[4096];
                try {
                    while ((read = fin.read(b)) != -1){
                        outs.write(b,0,read);
                    }
                    outs.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(outs != null)
                            outs.close();
                        if(in != null)
                            fin.close();
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
		        
		       
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
