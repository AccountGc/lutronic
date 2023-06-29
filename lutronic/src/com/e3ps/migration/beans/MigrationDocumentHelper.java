package com.e3ps.migration.beans;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.LoadFolder;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.StringSearch;
import wt.series.MultilevelSeries;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionInfo;
import wt.vc.wip.WorkInProgressHelper;

import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.jdf.config.ConfigEx;
import com.e3ps.common.jdf.config.ConfigExImpl;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.JExcelUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.drawing.WTDocEPMDocLink;
import com.e3ps.drawing.beans.EpmUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.part.service.PartHelper;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class MigrationDocumentHelper implements wt.method.RemoteAccess, java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
    public static MigrationDocumentHelper manager = new MigrationDocumentHelper();
    
    int totalcount = 0;
    int sCount = 0;
    int fCount = 0;
	String lifecycle = "LC_Default";
	
	
	public static void main(String[] args)throws Exception{
    	
      String uploadType = args[0]; //TEMPLATE,ORCAD
        
        if(uploadType.equals("TEMPLATE")){
        	MigrationDocumentHelper.manager.loadFolder();
        
        }else{
        	
        	MigrationDocumentHelper.manager.loadOrcadFolder();
        }
       
        //windchill com.e3ps.migration.beans.MigrationDocumentHelper TEMPLATE 문서 
        //windchill com.e3ps.migration.beans.MigrationDocumentHelper ORCAD orcad문ㅅ서
    }
	
	public void loadFolder(){
		
		if(!SERVER) {
            Class argTypes[] = new Class[]{};
            Object args[] = new Object[]{};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "loadFolder",
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
		
		try{
			
			String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
	        String sFilePath = sWtHome + "\\loadFiles\\e3ps\\Mig\\doc\\Template" ;
	        
	        File folder = new File(sFilePath);
	        
	        if(!folder.isDirectory()){
	        	//System.out.println(sFilePath +"의 Folder 가 존재 하지 않습니다.");
	        	
	        	return;
	        }
	        
	        searchFile(folder);
	        
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadOrcadFolder(){
		
		if(!SERVER) {
            Class argTypes[] = new Class[]{};
            Object args[] = new Object[]{};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "loadOrcadFolder",
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
		
		try{
			
			String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
	        String sFilePath = sWtHome + "\\loadFiles\\e3ps\\Mig\\doc\\oraCade" ;
	        
	        File folder = new File(sFilePath);
	        
	        if(!folder.isDirectory()){
	        	//System.out.println(sFilePath +"의 Folder 가 존재 하지 않습니다.");
	        	
	        	return;
	        }
	        
	        searchOrcadFile(folder);
	        
	    	//System.out.println("totalcount =" + totalcount);
			//System.out.println("sCount =" + sCount);
			//System.out.println("fCount =" + fCount);
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	} 
	
	public void searchOrcadFile(File folder){
		////System.out.println("folder.isDirectory() ::: " +folder.getName()  +" :::::: "+folder.isDirectory());
		try{
			
			//String rootFolder = "oraCad";
			
			if(folder.isDirectory()){
				File[] folers =folder.listFiles();
				
				for(int i = 0 ;  i <folers.length  ; i++){ //22
					File subChilder = folers[i];
					
					//if(rootFolder.equals(subChilder.getParentFile().getName())){
					//	location = location+subChilder.getName();
					//}
					if(subChilder.isFile()){
						String location = "/Default/Document/RandD/제어문서/";
						String fileName = subChilder.getName();
						String check1 = "BRD.zip".toUpperCase();
						String[] check2 ={"brd","DSN"}; //".brd",.DSN
						String extension = EpmUtil.getExtension(fileName);
						boolean isSkip = false;
						
						if("Thumbs.db".equals(fileName)){
							continue;
						}
						
						for(int ii =0 ; ii < check2.length ;ii++){
							////System.out.println(extension.toUpperCase() +"," + check2[ii].toUpperCase() +"="+ extension.toUpperCase().equals(check2[ii].toUpperCase()));
							if(extension.toUpperCase().equals(check2[ii].toUpperCase())){
								isSkip = true;
								break;
							}
						}
						if(isSkip){
							continue;
						}
						
						if(fileName.toUpperCase().lastIndexOf(check1) >0 ){
							continue;
						}
						
						String partNumber = subChilder.getParentFile().getName();
						
						location= location + subChilder.getParentFile().getParentFile().getName();
						Map<String,String> hash = new HashMap<String, String>();
			    		String docName =  fileName.substring(0,fileName.lastIndexOf("."));
			    		hash.put("docName", docName);
			    	    
			    		hash.put("location", location);
			    		hash.put("fileLocation", subChilder.getAbsolutePath());
			    		hash.put("partNumber", partNumber);
			    		if(extension.toUpperCase().equals("PDF")){
			    			hash.put("docType", "$$Document");
			    		}else{
			    			hash.put("docType", "$$OraCadDoc");
			    		}
			    		
			    		//System.out.println( totalcount +",[" + location +"],"+partNumber +","+ fileName +","+ extension);
			    		totalcount++;
			    		WTDocument doc = null;
			    		doc = createDoc(hash);
			    		
			    		if(doc == null){
			    			fCount++;
			    			//System.out.println(totalcount+","+fCount+" ,ERROR " + subChilder.getAbsolutePath());
			    		}else{
			    			sCount++;
			    			////System.out.println(totalcount+","+sCount+" , Sucess " + subChilder.getAbsolutePath());
			    		}
			    		
					}else{
						searchOrcadFile(subChilder);
					}
					
				}
				
			}
			
		
		}catch(Exception e){
			e.printStackTrace();;
		}
		
		
	}
	
	public void searchFile(File folder){
		////System.out.println("folder.isDirectory() ::: " +folder.getName()  +" :::::: "+folder.isDirectory());
		try{
			String location = "/Default/Document/RandD/Management/08Template";
			if(folder.isDirectory()){
				File[] folers =folder.listFiles();
				
				for(int i = 0 ;  i <folers.length  ; i++){ //22
					File subChilder = folers[i];
					if(subChilder.isFile()){
						String fileName = subChilder.getName();
						
						Map<String,String> hash = new HashMap<String, String>();
			    		String docName =  fileName.substring(0,fileName.lastIndexOf("."));
			    		hash.put("docName", docName);
			    	    
			    		hash.put("location", location);
			    		hash.put("fileLocation", subChilder.getAbsolutePath());
			    		WTDocument doc =createDoc(hash);
			    		totalcount++;
			    		if(doc == null){
			    			fCount++;
			    			//System.out.println(totalcount+","+fCount+"ERROR " + subChilder.getAbsolutePath());
			    		}else{
			    			sCount++;
			    			//System.out.println(totalcount+","+sCount+"ERROR " + subChilder.getAbsolutePath());
			    		}
			    		
					}else{
						searchFile(subChilder);
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();;
		}
		
		
	}
	
	public void loadExcel(String sFilePath){
		
		if(!SERVER) {
            Class argTypes[] = new Class[]{String.class};
            Object args[] = new Object[]{sFilePath};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "loadExcel",
                        null,
                        this,
                        argTypes,
                        args);
            } catch(RemoteException e) {
                e.printStackTrace();
               
            } catch(InvocationTargetException e) {
                e.printStackTrace();
               
            } catch(Exception e){
                
                
            }
        }
		
		try{
			
			File newfile =  new File(sFilePath);
			Workbook wb = JExcelUtil.getWorkbook(newfile);
			Sheet[] sheets = wb.getSheets();
	    	int rows = sheets[0].getRows();
	    	
	    	String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
	        String docFilePath = sWtHome + "\\loadFiles\\e3ps\\Mig\\doc\\" ;
	    	String defaultLocation = "/Default/Document/RandD/제어문서/ ";
	    	
	    	for (int j = 1; j < rows; j++)
	        {
	    		Cell[] cell = sheets[0].getRow(j);
	    		
	    		String docName = JExcelUtil.getContent(cell, 0).trim();
	    		String comment = JExcelUtil.getContent(cell, 1).trim();
	    		String location = JExcelUtil.getContent(cell, 2).trim();
	    		location = defaultLocation+location;
	    		String fileName = JExcelUtil.getContent(cell, 3).trim();
	    		String fileLocation = docFilePath+fileName;
	    		String partNumber= JExcelUtil.getContent(cell, 4).trim();
	    		
	    		
	    		Map<String,String> hash = new HashMap<String, String>();
	    		
	    		hash.put("docName", docName);
	    		hash.put("comment", comment);
	    		hash.put("location", location);
	    		hash.put("fileLocation", fileLocation);
	    		hash.put("partNumber", partNumber);
	    		createDoc(hash);
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public WTDocument createDoc(Map<String,String> hash)throws Exception{

        

        Transaction trx = new Transaction();
        WTDocument doc = null;
        
        try {
        	
        	trx.start();
 	
        	
            String docName				= StringUtil.checkNull((String)hash.get("docName"));
            String comment				= StringUtil.checkNull((String)hash.get("comment"));
            String location				= StringUtil.checkNull((String)hash.get("location"));
            String fileLocation			= StringUtil.checkNull((String)hash.get("fileLocation"));
            String partNumber			= StringUtil.checkNull((String)hash.get("partNumber"));
            String docType				= StringUtil.checkNull((String)hash.get("docType"));
           
            if(docName.length()==0){
            	return null;
            }
            
            if(fileLocation.length()==0){
            	return null;
            }
            
            doc = WTDocument.newWTDocument();
            doc =DocumentHelper.service.setNumberSeq(doc);
			doc.setName(docName);
			if(docType.length()>0){
				DocumentType documetType = DocumentType.toDocumentType(docType);
				doc.setDocType(documetType);
			}
			
			//Folder  && LifeCycle  Setting
			ConfigExImpl conf = ConfigEx.getInstance("eSolution");
        	String product = conf.getString("product.context.name");
            PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct(product);
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			doc.setContainer(e3psProduct);
			
			LifeCycleHelper.setLifeCycle(doc, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); //Lifecycle
			
			Folder folder = FolderTaskLogic.getFolder(location, wtContainerRef);
			FolderHelper.assignLocation((FolderEntry) doc, folder);
		
			doc = (WTDocument)PersistenceHelper.manager.save(doc);
			
			LifeCycleHelper.service.setLifeCycleState(doc, State.toState("APPROVED"));
			
			//등급
			IBAUtil.changeIBAValue(doc, "Grade", "G003", "string");
			
			//주첨부파일
            CommonContentHelper.service.attachPrimary(doc, fileLocation);
            
            //관련 부품 연결
            if(partNumber.length()>0){
            	WTPart part = PartHelper.service.getPart(partNumber);
            	if(part == null){
            		throw new Exception(partNumber+" PART가 존재 하지 않습니다.");
            	}
				WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
				PersistenceServerHelper.manager.insert(link);
            }
			
			trx.commit();
            trx = null;

       } catch(Exception e) {
           e.printStackTrace();
       } finally {
           if(trx!=null){
                trx.rollback();
           }
       }
        return doc;
	}
	
   
}
