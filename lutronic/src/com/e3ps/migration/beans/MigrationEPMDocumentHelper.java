package com.e3ps.migration.beans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.epm.EPMApplicationType;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMContextHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.structure.EPMDescribeLink;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.query.SearchCondition;
import wt.series.FileBasedSeries;
import wt.series.HarvardSeries;
import wt.series.MultilevelSeries;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionInfo;
import wt.vc.wip.WorkInProgressHelper;

import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.jdf.config.ConfigEx;
import com.e3ps.common.jdf.config.ConfigExImpl;
import com.e3ps.common.util.JExcelUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.drawing.beans.EpmUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.util.EpmPublishUtil;
import com.e3ps.part.service.PartHelper;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class MigrationEPMDocumentHelper implements wt.method.RemoteAccess, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
    public static MigrationEPMDocumentHelper manager = new MigrationEPMDocumentHelper();
	
	String lifecycle = "LC_Part";
    String wtPartType = "separable";
    String source = "make";
    String view = "Design";
    String applicationType = "MANUAL";
    
    int totalcount = 0;
    int sCount = 0;
    int fCount = 0;
    
    
    public static void main(String[] args)throws Exception{
    	
        String uploadType = args[0]; //PDF,ORCAD
         
          if(uploadType.equals("PDF")){
        	 
        	  MigrationEPMDocumentHelper.manager.excelLoader();
          }else{
          	String sFilePath = "";
          	
          	MigrationEPMDocumentHelper.manager.loadOrcadFolder();
          }
         
          //windchill com.e3ps.migration.beans.MigrationEPMDocumentHelper PDF 첵셀 경로
          //windchill com.e3ps.migration.beans.MigrationEPMDocumentHelper ORCAD orcad문ㅅ서
    }
    
    public void excelLoader( ){
    	
    	
    	try{
    		String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
	        String sPdfFolder = sWtHome + "\\loadFiles\\e3ps\\Mig\\pdf\\" ;
	        String sFilePath = sPdfFolder + "업로드_PLM_160302.xls";
    		
	        //System.out.println("sFilePath =" + sFilePath);
	        File newfile =  new File(sFilePath);
    		
    		Workbook wb = JExcelUtil.getWorkbook(newfile);
    		Sheet[] sheets = wb.getSheets();
        	int rows = sheets[0].getRows();
        	String log ="";
        	
        	
        	for (int j = 1; j < rows; j++)
            {
        		boolean isCheck = true;
        		Cell[] cell = sheets[0].getRow(j);
        		//String partNumber = JExcelUtil.getContent(cell, 0).trim();
        		//partNumber = partNumber.toUpperCase();
        		String location = JExcelUtil.getContent(cell, 1).trim();   //분류체계
        		String partNumber = JExcelUtil.getContent(cell, 2).trim();	//연관 부품
        		String version = JExcelUtil.getContent(cell, 3).trim();	//버전
        		String fileName = JExcelUtil.getContent(cell, 4).trim();	//파일명
        	
        		//System.out.println(j+","+fileName);
        		//분류체계 검증
        		location = location.replace("/intellian", "/Default");
        		Folder folder = null;
        		try{
        			folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
        		}catch(Exception e){
        			//e.printStackTrace();
        		}
        		
        		if(folder == null){
        			log =j +" , Folder ERROR =" + fileName + "," + location;
        			isCheck = false;
        			createLog(log, "PDF_FAIL");
        			
        		}

        		//부품 검증
        		WTPart part =PartHelper.service.getPart(partNumber);
        		if(part == null){
        			log =j +" , PART ERROR =" + fileName + "," + partNumber;
        			isCheck = false;
        			createLog(log, "PDF_FAIL");
        		}else{
        			//버전 검증
        			String partVerson = part.getVersionIdentifier().getValue();
        			if(!partVerson.equals(version)){
        				log =j +" , VERSION ERROR =" + fileName + ",version=" + version +" ,partNumber="+partNumber+" ,partVerson=" +partVerson;
        				isCheck = false;
        				createLog(log, "PDF_FAIL");
        			}
        		}

        		//파일 검증
        		String pdfFile  = sPdfFolder + fileName;
        		File file = new File(pdfFile);
        		if(!file.isFile()){
        			log =j +" , File ERROR =" + fileName + "," + version;
        			isCheck = false;
        			createLog(log, "PDF_FAIL");
        		}
        		
        		if(isCheck){
        			
        			
        			Map<String,String> hash = new HashMap<String, String>();
		    		//String docName =  fileName.substring(0,fileName.lastIndexOf("."));
		    		
					hash.put("fileName", fileName);
		    		hash.put("partNumber", partNumber);
					hash.put("location", location);
		    		hash.put("fileLocation", pdfFile);
		    		
		    		EPMDocument doc = null;
		    		doc =createEPMDocument(hash);
		    		if(doc ==null){
		    			log = j + "CREATE_FAIL = " + fileName + "," + partNumber;
		    			createLog(log, "PDF_FAIL");
		    			fCount++;
		    		}else{
		    			log = j + "CREATE_SUCCESS = " + fileName + "," + partNumber;
		    			createLog(log, "PDF_SUCCESS");
		    			sCount++;
		    		}
		    		
		    		//sCount++;
        		}else{
        			fCount++;
        		}
        		
        		totalcount++;
            }
        	
        	//System.out.println("totalcount = " + totalcount);
        	//System.out.println("sCount = " + sCount);
        	//System.out.println("fCount = " + fCount);
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
	//System.out.println("folder.isDirectory() ::: " +folder.getName()  +" :::::: "+folder.isDirectory());
		try{
			
			//String rootFolder = "oraCad";
			
			if(folder.isDirectory()){
				File[] folers =folder.listFiles();
				
				for(int i = 0 ;  i <folers.length  ; i++){ //22
					File subChilder = folers[i];
					
					//if(rootFolder.equals(subChilder.getParentFile().getName())){
					//	location = location+subChilder.getName();
					//}
					///System.out.println("===="+ subChilder.getName());
					if(subChilder.isFile()){
						String location = "Default/Drawing_PART/제어도면/";
						String fileName = subChilder.getName();
						String partNumber = subChilder.getParentFile().getName();
						location= location + subChilder.getParentFile().getParentFile().getName();
						String check1 = "BRD.zip".toUpperCase();
						String[] check2 ={"brd","DSN","ZIP"}; //".brd",.DSN
						String extension = EpmUtil.getExtension(fileName);
						boolean isSkip = false;
						//System.out.println( "All "+totalcount +",[" + location +"],"+partNumber +","+ fileName +","+ extension);
						if("Thumbs.db".equals(fileName)){
							continue;
						}
						
					
						for(int ii =0 ; ii < check2.length ;ii++){
							//System.out.println(fileName+","+extension.toUpperCase() +"," + check2[ii].toUpperCase() +"="+ extension.toUpperCase().equals(check2[ii].toUpperCase()));
							if(extension.toUpperCase().equals(check2[ii].toUpperCase())){
								isSkip = true;
								break;
							}
						}
						if(!isSkip){
							continue;
						}
						if(extension.toUpperCase().equals("ZIP")){
							if(fileName.toUpperCase().lastIndexOf(check1) < 0 ){
								continue;
							}
						}
						
						
						//System.out.println(totalcount +",[" + location +"],"+partNumber +","+ fileName +","+ extension);
						Map<String,String> hash = new HashMap<String, String>();
			    		//String docName =  fileName.substring(0,fileName.lastIndexOf("."));
			    		
						hash.put("fileName", fileName);
			    		hash.put("partNumber", partNumber);
						hash.put("location", location);
			    		hash.put("fileLocation", subChilder.getAbsolutePath());
			    		EPMDocument doc = null;
			    		doc =createEPMDocument(hash);
			    		totalcount++;
			    		if(doc == null){
			    			fCount++;
			    			//System.out.println(totalcount+","+fCount+" -ERROR- " + subChilder.getAbsolutePath());
			    		}else{
			    			sCount++;
			    			//System.out.println(totalcount+","+sCount+" -Sucess- " + subChilder.getAbsolutePath());
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
    
    public EPMDocument createEPMDocument(Map<String,String> hash)throws Exception{

        if(!SERVER) {
            Class argTypes[] = new Class[]{Map.class};
            Object args[] = new Object[]{hash};
            try {
                return (EPMDocument)wt.method.RemoteMethodServer.getDefault().invoke(
                        "createEPMDocument",
                        null,
                        this,
                        argTypes,
                        args);
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

        Transaction trx = new Transaction();
        EPMDocument epm = null;
        try {
        	trx.start();

        	
        	String partNumber  		= (String)hash.get("partNumber");
            String location			= (String)hash.get("location");
                    
            String fileLocation		= (String)hash.get("fileLocation");
            String fileName			= (String)hash.get("fileName");
            
            String authoringType = EpmUtil.getExtension(fileName);
            authoringType = authoringType.toUpperCase();
            
            if (authoringType.equals("DSN") || authoringType.equals("BRD") || authoringType.equals("ZIP")){
				authoringType = "ORCAD";
            }else if(authoringType.equals("DWG")){
            	authoringType = "ACAD";
            }else{
				authoringType = "OTHER";
			}
            
            WTPart part = PartHelper.service.getPart(partNumber);
    		String epmName = part.getName();
    		String epmNumber = part.getNumber()+"."+DrawingHelper.service.getPrefix(fileName);
            String epmVersion = part.getVersionIdentifier().getValue();
            
            
            //Folder  && LifeCycle  Setting
			ConfigExImpl conf = ConfigEx.getInstance("eSolution");
        	String product = conf.getString("product.context.name");
            PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct(product);
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			Folder folder = FolderTaskLogic.getFolder(location, wtContainerRef);
            
			
			
			
			epm = EPMDocument.newEPMDocument();
			if(!epmVersion.equals("0")){
				MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier", epmVersion);
				epm.setVersionInfo( VersionInfo.newVersionInfo(multilevelseries));
			}
			epm.setNumber(epmNumber);
			epm.setName(epmName);
			epm.setCADName(fileName);
			EPMDocumentMaster epmMaster = (EPMDocumentMaster)epm.getMaster();
			EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType(applicationType));
			epmMaster.setOwnerApplication(EPMContextHelper.getApplication());
			EPMAuthoringAppType appType = EPMAuthoringAppType.toEPMAuthoringAppType(authoringType);
			epmMaster.setAuthoringApplication(appType);
			
			
			FolderHelper.assignLocation((FolderEntry) epm, folder);
			epm.setContainer(e3psProduct);
			
		   
            
		    EPMDocumentType docType = getEPMDocumentType(authoringType);
			epm.setDocType(docType);
           
			LifeCycleHelper.setLifeCycle(epm, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef));		
			epm = (EPMDocument)PersistenceHelper.manager.save(epm);
			
			LifeCycleHelper.service.setLifeCycleState(epm, State.toState("APPROVED"));
			
			//주첨부파일
            CommonContentHelper.service.attachPrimary(epm, fileLocation);
            if(partNumber.length()>0){
            	part = PartHelper.service.getPart(partNumber);
            	if(part == null){
            		throw new Exception(partNumber+" PART가 존재 하지 않습니다.");
            	}
            	
            	EPMDescribeLink describeLink = EPMDescribeLink.newEPMDescribeLink(part, epm);
      			PersistenceServerHelper.manager.insert(describeLink);
            }
            
            if(authoringType.equals("ACAD")){
            	EpmPublishUtil.publish(epm);
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
       return epm;
	}
    
    public EPMDocumentType getEPMDocumentType(String fileName){
    	
    	fileName = fileName.toLowerCase();
		/*
		CADASSEMBLY.value=�����
		CADCOMPONENT.value=CAD ��ǰ
		CADDRAWING.value=�����
		DIAGRAM.value=���̾�׷�
		ECAD-ASSEMBLY.value=ECAD - �����
		ECAD-BOARD.value=ECAD - ����
		ECAD-COMPONENT.value=ECAD - ������Ʈ
		ECAD-CONTENT.value=ECAD - ����Ʈ
		ECAD-SCHEMATIC.value=ECAD - ����
		ECAD-SOURCE.value=ECAD - �ҽ�
		FORMAT.value=����
		LAYOUT.value=���̾ƿ�
		MANUFACTURING.value=����
		MARKUP.value=��ũ��
		OTHER.value=��Ÿ
		REPORT.value=����
		SKETCH.value=����ġ
		UDF.value=����� ���� ���
		DESIGN.value=����
		RENDERING.value=������
		PUB_COMPOUNDTEXT.value=�Խ� �ҽ�
		PUB_GRAPHIC.value=�Խ� �׷���
		PUB_CADVIEWABLE.value=���� ������ CAD �׸� �Խ� 
		CADDRAWINGTEMPL.value=����� ���ø�
		ANALYSIS.value=�м�
		IGES.value=Iges
		STEP.value=Step
		VDA.value=VDA-FS
		ACIS.value=ACIS
		PARASOLID.value=Parasolid
		ZIP.value=Zip ����
		DXF.value=DXF
		NOTE.value=�޸�
		WORKSHEET.value=Mathcad ��ũ��Ʈ
		EDA_DIFF_CONFIG.value=ECAD Compare ���� ������
		EDA_DIFF_REPORT.value=ECAD Compare IDX ������
		CALCULATION_DATA.value=��� ������
		MANIKIN_POSTURE.value=��ü ���� �ڼ�
		WORKPLANE.value=�۾� ���
		WORKPLANE_SET.value=�۾� ��� ��Ʈ
		CUTTER_LOCATION.value=Ŀ�� ��ġ
		MACHINE_CONTROL.value=�ӽ� ���� ������
		INSTANCEDATA.value=�ν��Ͻ� ������
		MECHANICARESULTS.value=�м� �� ���� ���� ���
		MECHANICAREPORT.value=HTML ��� ����
		*/
		
		String type = "OTHER";

		if (".drw".equals(fileName)){
			type = "CADDRAWING";
		}
		else if (".prt".equals(fileName)){
			type = "CADCOMPONENT";
		}
		else if (".asm".equals(fileName)){
			type = "CADASSEMBLY";
		}
		else if (".frm".equals(fileName)){
			type = "FORMAT";
		}
		else if (".dwg".equals(fileName)){
			type = "CADDRAWING";
		}
		else if (".igs".equals(fileName)){
			type = "IGES";
		}
		else if (".iges".equals(fileName)){
			type = "IGES";
		}
		else if(".gif".equals(fileName)){
			type = "PUB_GRAPHIC";
		}
		else if(".jpg".equals(fileName)){
			type = "PUB_GRAPHIC";
		}
		else if(".zip".equals(fileName)){
			type = "ZIP";
		}
		
        return EPMDocumentType.toEPMDocumentType(type);
    }
    
    public void createLog(String log,String fileName) {
		//System.out.println("========== "+fileName+" ===========");
		String filePath = "D:\\e3ps\\";
		
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
