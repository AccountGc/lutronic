package com.e3ps.migration.beans;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.epm.E3PSRENameObject;
import wt.epm.EPMDocument;
import wt.epm.structure.EPMDescribeLink;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.part.PartType;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.series.MultilevelSeries;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.VersionInfo;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigEx;
import com.e3ps.common.jdf.config.ConfigExImpl;
import com.e3ps.common.util.AuthHandler;
import com.e3ps.common.util.JExcelUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.part.service.PartHelper;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class MigrationPartHelper implements wt.method.RemoteAccess, java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
    public static MigrationPartHelper manager = new MigrationPartHelper();
 
	String lifecycle = "LC_Part";
    String wtPartType = "separable";
    String source = "make";
    String view = "Design";
    String unit = "ea";
    int totalCount = 0;
	int sCount = 0;
	int eCount = 0;
    public static void main(String[] args)throws Exception{
    	
    	
    	
    	String userId = "wcadmin";
		wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer.getDefault();
		methodServer.setAuthenticator(AuthHandler.getMethodAuthenticator(userId));
    	
		String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
        String sFilePath = sWtHome + "\\loadFiles\\e3ps\\Part_Loader_151127.xls" ;
		
        if(args!=null && args.length>0 && args[0].trim().length()>0){
        	sFilePath = args[0];
        }
        
        //System.out.println("sFilePath =" + sFilePath);
        MigrationPartHelper.manager.loadPart(sFilePath);
        //windchill com.e3ps.migration.beans.MigrationPartHelper D:\ptc\Windchill_10.2\Windchill\loadFiles\e3ps\Part_Loader_151127.xls
        //windchill com.e3ps.migration.beans.MigrationPartHelper D:\ptc\Windchill_10.2\Windchill\loadFiles\e3ps\Mig\part\PART_ERROR.xls
    }
	
    public void loadPart(String sFilePath)throws Exception{

        if(!SERVER) {
            Class argTypes[] = new Class[]{String.class};
            Object args[] = new Object[]{sFilePath};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "loadPart",
                        null,
                        this,
                        argTypes,
                        args);
               return;
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

        //Transaction trx = new Transaction();
        WTPart part = null;
        long start = System.currentTimeMillis();
        try {
        	//trx.start();
        	
        	File newfile =  new File(sFilePath);
    		Workbook wb = JExcelUtil.getWorkbook(newfile);
    		Sheet[] sheets = wb.getSheets();
        	int rows = sheets[0].getRows();
        	
        	for (int j = 1; j < rows; j++)
            {
        		totalCount++;
        		Cell[] cell = sheets[0].getRow(j);
        		String partNumber = JExcelUtil.getContent(cell, 0).trim();
        		partNumber = partNumber.toUpperCase();
        		String partVersion = JExcelUtil.getContent(cell, 1).trim();
        		String partName = JExcelUtil.getContent(cell, 2).trim();
        		String productType = JExcelUtil.getContent(cell, 4).trim();
        		String productSubType = JExcelUtil.getContent(cell, 5).trim();
        		String productDimensionGroup = JExcelUtil.getContent(cell, 6).trim();
        		String location = JExcelUtil.getContent(cell, 7).trim();
        		
        		
        		if(partVersion.equals("") || partVersion.equals("-")){
        			partVersion ="0";
        		}
        		
        		boolean isCreate = false;
        		part = PartHelper.service.getPart(partNumber);
        		if(part == null){
        			part = WTPart.newWTPart();
        			isCreate = true;
        		}
        		
        		HashMap<String, String> hash = new HashMap<String, String>();
	            
        		hash.put("partNumber", partNumber);
        		hash.put("partVersion", partVersion);
        		hash.put("partName", partName);
        		hash.put("productType", productType);
        		hash.put("productSubType", productSubType);
        		hash.put("productDimensionGroup", productDimensionGroup);
        		hash.put("location", "location");
        		
        	
        		
        		try{
        			if(isCreate){
        				//System.out.println(j +"," + partNumber +","+partVersion+","+partName+", CREATE");
            			part = createPart(part, hash);
            			
            		}else {
            			//System.out.println(j +"," + partNumber +", Modify");
            			IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_SEQCHECK, "Y", "boolean");
            			//part = modifyPart(part,hash);
            		}
        			
        			sCount++;
        		}catch(Exception e){
        			 e.printStackTrace();
        	         //System.out.println("for in Error");
        			 eCount ++;
        		}
        		
	            
        		
        		
        		
	            //trx.commit();
	            //trx = null;
             }
       } catch(Exception e) {
           e.printStackTrace();
           //System.out.println("for out Error");
           
       } 
        long end = System.currentTimeMillis();
        //System.out.println("totalCount = " + totalCount);
        //System.out.println("sCount = " + sCount);
        //System.out.println("eCount = " + eCount);
        //System.out.println("####  [MigrationPartHelper] Run Time : " + (end - start) / 1000.0f + " sec");
       
	}
    
    public WTPart createPart(WTPart part,HashMap<String,String> hash) throws Exception{
    	
    		
    		String partNumber 		= (String)hash.get("partNumber");
            String partName			= (String)hash.get("partName");
            String partVersion		= (String)hash.get("partVersion");
            String location			= (String)hash.get("location");
            String productType			= (String)hash.get("productType");
            String productSubType			= (String)hash.get("productSubType");
            String productDimensionGroup			= (String)hash.get("productDimensionGroup");
    		if(!partVersion.equals("0")){
    			MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier", partVersion);
            	part.setVersionInfo( VersionInfo.newVersionInfo(multilevelseries));
    		}
    		
        	
        	ConfigExImpl conf = ConfigEx.getInstance("eSolution");
        	String product = conf.getString("product.context.name");
            PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct(product);//MigrationDataHelper.manager.getProduct();
            WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
            part.setContainer(e3psProduct);
            part.setNumber(partNumber);
            part.setName(partName.trim());
            location = "/Default/Drawing_PART/Mig";
            //단위 정보 셋팅
            if(unit != null && unit.length() > 0)
            	part.setDefaultUnit(QuantityUnit.toQuantityUnit(unit));
            else 
            	part.setDefaultUnit(QuantityUnit.getQuantityUnitDefault());
            
            part.setPartType(PartType.toPartType(wtPartType));
            part.setSource(Source.toSource(source));
            ViewHelper.assignToView(part, ViewHelper.service.getView(view));

            
            Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
            FolderHelper.assignLocation((FolderEntry)part, folder);
           
            //라이프사이클 셋팅
            LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef);
            part = (WTPart) LifeCycleHelper.setLifeCycle(part, tmpLifeCycle);
            
            //품목 등록
            part = (WTPart) PersistenceHelper.manager.save(part);
            
            LifeCycleHelper.service.setLifeCycleState(part, State.toState("APPROVED"));
            
            IBAUtil.changeIBAValue(part, AttributeKey.PartKey.IBA_PRODUCTTYPE, productType);
            IBAUtil.changeIBAValue(part, AttributeKey.PartKey.IBA_PRODUCTSUBTYPE, productSubType);
            IBAUtil.changeIBAValue(part, AttributeKey.PartKey.IBA_PRODUCTDIMENSIONGROUP, productDimensionGroup);
            IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_SEQCHECK, "Y", "boolean");
        
    	
    	return part;
    }
    
    
    public WTPart modifyPart(WTPart part,HashMap<String,String> hash) throws Exception{
    	
    	
    		
    		String partNumber 		= (String)hash.get("partNumber");
            String partName			= (String)hash.get("partName");
            String partVersion		= (String)hash.get("partVersion");
            String location			= StringUtil.checkNull((String)hash.get("location"));
           
           
            
           
        	
            
        	if(!partName.equals(part.getName())){
        		//E3PSRENameObject.manager.PartReName(part, partNumber, partName, false);
                //PersistenceHelper.manager.refresh(part);
        	}
            
            String nowVersion = part.getVersionIdentifier().getValue();
            
            //System.out.println(part.getNumber() +partVersion +"," +nowVersion);
            if(partVersion != nowVersion){
            	//MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier", partVersion);
           // 	MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.series.HarvardSeries.intellian", partVersion);
           // 	part.getMaster().setSeries("wt.series.HarvardSeries.intellian");
           // 	VersionIdentifier vi = VersionIdentifier.newVersionIdentifier(multilevelseries);
           // 	VersionControlHelper.setVersionIdentifier(part, vi);
            	
            	//part.setVersionInfo( VersionInfo.newVersionInfo(multilevelseries));
            	//VersionControlHelper.setVersionIdentifier(paramVersioned, paramVersionIdentifier, paramBoolean);
            	 //System.out.println("CHANGE VERSION : " + part.getNumber() +partVersion +"," +nowVersion);
            }
            
            if(location.length()>0){
           	 //location = "/Default/Drawing_PART/Mig";
           	 
           	 Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
             part = (WTPart) FolderHelper.service.changeFolder((FolderEntry) part, folder);
             PersistenceServerHelper.manager.update(part);
           }
           
            
    	
    	
    	return part;
    	
    }
    
 
    
	

}
