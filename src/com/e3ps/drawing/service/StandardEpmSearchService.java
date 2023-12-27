package com.e3ps.drawing.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wt.content.ContentRoleType;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.epm.structure.EPMVariantLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.services.StandardManager;
import wt.vc.VersionControlHelper;

import com.e3ps.common.beans.ResultData;
import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.drawing.EpmLocation;
import com.e3ps.drawing.REFDWGLink;
import com.e3ps.drawing.WTDocEPMDocLink;
import com.e3ps.drawing.util.EpmPublishUtil;
import com.e3ps.migration.service.MigrationHelper;
import com.e3ps.part.service.PartHelper;
import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

@SuppressWarnings("serial")
public class StandardEpmSearchService extends StandardManager implements EpmSearchService {

	public static StandardEpmSearchService newStandardEpmSearchService() throws Exception {
		final StandardEpmSearchService instance = new StandardEpmSearchService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public  Vector getReferenceDependency(EPMDocument doc, String role) {
    	Vector references = new Vector();
    	try {
	    	QueryResult queryReferences = null;
	    	System.out.println("StanardEpmSearchS getReferenceDependency ::: role="+ role);
	        if(role.equals("referencedBy")) //참조항목
	            queryReferences = EPMStructureHelper.service.navigateReferencedBy((EPMDocumentMaster)doc.getMaster(), null, false);
	        else //참조
	            queryReferences = EPMStructureHelper.service.navigateReferences(doc, null, false);
	        
	        EPMReferenceLink referenceLink = null;
	        while(queryReferences.hasMoreElements()) {
	        	referenceLink = (EPMReferenceLink)queryReferences.nextElement();
	        	references.add(referenceLink);
               
	        }
    	}
    	catch(Exception e) {
    		 System.out.println("Error getting the Instance Type");
             e.printStackTrace();
             return new Vector();
    	}
        return references;
    }
	 
	//참조 
	@Override
    public Vector getRef(String oid){
    	EPMDocument epm = (EPMDocument)CommonUtil.getObject(oid);
    	Vector vec = getReferenceDependency(epm, "references");
    	Vector vecRefby = new Vector();
   	 	for(int i = 0 ; i < vec.size() ;i ++){
   	 		EPMReferenceLink link = (EPMReferenceLink)vec.get(i);
   	 		vecRefby.add(link.getReferences());
   	 	} 
   	 return vecRefby;
    }
    
    //참조 항목(EPMDocumentMaster)
	@Override
    public Vector getRefBy(String oid){
	EPMDocument epm = (EPMDocument)CommonUtil.getObject(oid);
		Vector vec = getReferenceDependency(epm, "referencedBy");
		Vector vecRefby = new Vector();
		for(int i = 0 ; i < vec.size() ;i ++){
			EPMReferenceLink link = (EPMReferenceLink)vec.get(i);
			vecRefby.add(link.getReferencedBy());
		} 
		return vecRefby;
    }
    
    //관련 문서 (EPMDocument)
	@Override
    public Vector getWTDocumentLink(String oid){
    	EPMDocument epm = (EPMDocument)CommonUtil.getObject(oid);
    	Vector vecDoc = new Vector();
    	try{
    		QueryResult linkQr = PersistenceHelper.manager.navigate((EPMDocumentMaster)epm.getMaster(), "wtDoc", WTDocEPMDocLink.class);
        	while(linkQr.hasMoreElements()) {
        		vecDoc.add(linkQr.nextElement());
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return vecDoc;
    }
    
    
	@Override
    public EPMDocument  getEPM2D(EPMDocumentMaster master){
		
		Vector<EPMReferenceLink> vec = getEPMReferenceList(master);
		for(int h = 0 ; h < vec.size() ; h++){
			EPMReferenceLink epmlink = vec.get(h);
			EPMDocument epm2d = epmlink.getReferencedBy();
			if(epm2d.getDocType().toString().equals("CADDRAWING")){
				return epm2d;
			}
			
		}
		
		return null;
		/*
		EPMDocument epm2D = null;
	    try{
	    	QuerySpec qs = new QuerySpec();
    		int idxA = qs.addClassList(EPMReferenceLink.class, false);
    		int idxB = qs.addClassList(EPMDocument.class, true);
    		
    		//Join
    		qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"roleAObjectRef.key.id",
    											EPMDocument.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{idxA,idxB});
    		qs.appendAnd();
    		qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"roleBObjectRef.key.id",
    											SearchCondition.EQUAL,CommonUtil.getOIDLongValue(master)),new int[]{idxA});
    		
    		//qs.appendAnd();
    		//qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"referenceType",
    		//									SearchCondition.EQUAL,"DRAWING"),new int[]{idxA}); //DRAWING
    		//최신 이터레이션
    		qs.appendAnd();
    		qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idxB });
    		
    		//최신 버전
    		SearchUtil.addLastVersionCondition(qs, EPMDocument.class, idxB);

			QueryResult rt =PersistenceHelper.manager.find(qs);
			while(rt.hasMoreElements()){
				Object[] oo = (Object[]) rt.nextElement();
				epm2D = (EPMDocument)oo[0];
			}

	    }catch(Exception e){
	    		e.printStackTrace();
	    }
	   
	    return epm2D;
	     */
	}
	
	
    /**
     * 도면의 참조 항목  (사용 하지 않음)
     * @param master
     * @return
     */
	@Override
    public EPMReferenceLink  getEPMReferenceLink(EPMDocumentMaster master){
		
		return null;
    	/*
		EPMReferenceLink link = null;
	    try{
	    	QuerySpec qs = new QuerySpec();
    		int idxA = qs.addClassList(EPMReferenceLink.class, true);
    		int idxB = qs.addClassList(EPMDocument.class, false);
    		
    		//Join
    		qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"roleAObjectRef.key.id",
    											EPMDocument.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{idxA,idxB});
    		qs.appendAnd();
    		qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"roleBObjectRef.key.id",
    											SearchCondition.EQUAL,CommonUtil.getOIDLongValue(master)),new int[]{idxA});
    		
    		//qs.appendAnd();
    		//qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"referenceType",
    		//									SearchCondition.EQUAL,"DRAWING"),new int[]{idxA}); //DRAWING
    		//최신 이터레이션
    		qs.appendAnd();
    		qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idxB });
    		
    		//최신 버전
    		SearchUtil.addLastVersionCondition(qs, EPMDocument.class, idxB);

			QueryResult rt =PersistenceHelper.manager.find(qs);
			while(rt.hasMoreElements()){
				Object[] oo = (Object[]) rt.nextElement();
				link = (EPMReferenceLink)oo[0];
			}

	    }catch(Exception e){
	    		e.printStackTrace();
	    }
	    
	    return link;
	    */
	}
    
    /**
     * 
     * @param master
     * @return
     */
	@Override
    public Vector<EPMReferenceLink>  getEPMReferenceList(EPMDocumentMaster master){
    	Vector<EPMReferenceLink> vec = new Vector<EPMReferenceLink>();
	    try{
	    	QuerySpec qs = new QuerySpec();
    		int idxA = qs.addClassList(EPMReferenceLink.class, true);
    		int idxB = qs.addClassList(EPMDocument.class, false);
    		
    		//Join
    		qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"roleAObjectRef.key.id",
    											EPMDocument.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{idxA,idxB});
    		qs.appendAnd();
    		qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"roleBObjectRef.key.id",
    											SearchCondition.EQUAL,CommonUtil.getOIDLongValue(master)),new int[]{idxA});
    		
    		qs.appendAnd();
    		qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"referenceType",
    											SearchCondition.EQUAL,"DRAWING"),new int[]{idxA}); //DRAWING
    		//최신 이터레이션
    		QuerySpecUtils.toLatest(qs, idxB, EPMDocument.class);

			QueryResult rt =PersistenceHelper.manager.find(qs);
			while(rt.hasMoreElements()){
				Object[] oo = (Object[]) rt.nextElement();
				EPMReferenceLink link = (EPMReferenceLink)oo[0];
				vec.add(link);
				
			}

	    }catch(Exception e){
	    		e.printStackTrace();
	    }
	    
	    return vec;
	}
    
    /**
     * 3D와 2D link  (사용하지 않음)
     * @param epm3D
     * @param epm2D
     * @return
     */
	@Override
    public EPMReferenceLink  getEPMReference(EPMDocument epm3D,EPMDocument epm2D){
    	EPMReferenceLink link = null;
    	try{
    		/*
    		QuerySpec qs = new QuerySpec();
    		int idxA = qs.addClassList(EPMReferenceLink.class, true);
    		int idxB = qs.addClassList(EPMDocument.class, false);
    		
    		//Join
    		qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"roleAObjectRef.key.id",
    											EPMDocument.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{idxA,idxB});
    		qs.appendAnd();
    		qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"roleBObjectRef.key.id",
    											SearchCondition.EQUAL,CommonUtil.getOIDLongValue(master)),new int[]{idxA});
    		
    		//qs.appendAnd();
    		//qs.appendWhere(new SearchCondition(EPMReferenceLink.class,"referenceType",
    		//									SearchCondition.EQUAL,"DRAWING"),new int[]{idxA}); //DRAWING
    		//최신 이터레이션
    		qs.appendAnd();
    		qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idxB });
    		
    		//최신 버전
    		SearchUtil.addLastVersionCondition(qs, EPMDocument.class, idxB);

			QueryResult rt =PersistenceHelper.manager.find(qs);
			while(rt.hasMoreElements()){
				Object[] oo = (Object[]) rt.nextElement();
				EPMReferenceLink link = (EPMReferenceLink)oo[0];
				vec.add(link);
				
			}
			*/
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return link;
    }
    
    /**
     * 최신 버전 Search
     * @param master
     * @return
     */
	@Override
    public EPMDocument getLastEPMDocument(EPMDocumentMaster master){
    	
    	 EPMDocument epm = null;
		 try{
			 long longoid = CommonUtil.getOIDLongValue(master);
			 Class class1 = EPMDocument.class;
			
			 QuerySpec qs = new QuerySpec();
			 int i = qs.appendClassList(class1, true);
			 
			 qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { i });
			 
			 SearchUtil.addLastVersionCondition(qs, EPMDocument.class, i);
			 
			 qs.appendAnd();
			 qs.appendWhere(new SearchCondition(class1, "masterReference.key.id", SearchCondition.EQUAL,longoid), new int[] { i });
			
			 QueryResult qr = PersistenceHelper.manager.find(qs);
			 while(qr.hasMoreElements()){
				 
				 Object obj[] = (Object[])qr.nextElement();
				 epm = (EPMDocument)obj[0];
				 
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
    	 
		 return epm;
    }
    
	@Override
    public EPMDocument getREFDWG(EPMDocument epm){
    	EPMDocument returnEPM = null;
    	try{
    		QueryResult linkQr = PersistenceHelper.manager.navigate((EPMDocumentMaster)epm.getMaster(), "toEPM", REFDWGLink.class);
        	if(linkQr.hasMoreElements()) {
        		EPMDocumentMaster master = (EPMDocumentMaster)linkQr.nextElement();
        		returnEPM = getLastEPMDocument(master);
        	}else {
        		linkQr = PersistenceHelper.manager.navigate((EPMDocumentMaster)epm.getMaster(), "fromEPM", REFDWGLink.class);
            	if(linkQr.hasMoreElements()) {
            		EPMDocumentMaster master = (EPMDocumentMaster)linkQr.nextElement();
            		returnEPM = getLastEPMDocument(master);
            	}
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return returnEPM;
    }
    
    /**
     * 최신 유무 체크 (사용 금지)
     * @param master
     * @return
     */
	@Override
    public boolean  isLastEPMDocument(EPMDocumentMaster master){
    	
    	boolean isLast = false;
    	/*
		 try{
			 long longoid = CommonUtil.getOIDLongValue(master);
			 Class class1 = EPMDocument.class;
			
			 QuerySpec qs = new QuerySpec();
			 int i = qs.appendClassList(class1, true);
			 
			 qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { i });
			 
			 SearchUtil.addLastVersionCondition(qs, EPMDocument.class, i);
			 
			 qs.appendAnd();
			 qs.appendWhere(new SearchCondition(class1, "masterReference.key.id", SearchCondition.EQUAL,longoid), new int[] { i });
			
			 QueryResult qr = PersistenceHelper.manager.find(qs);
			 if(qr.size()>0) return true;
		 }catch(Exception e){
			 e.printStackTrace();
		 }
   	 	*/
		 return isLast;
   }
	
	 /**
     * 참조 품목,참조 도면 (PCB,PDF)
     * @param rc
     * @param isLast
     * @return
     */
	@Override
    public Vector<EPMDescribeLink> getEPMDescribeLink(RevisionControlled rc,boolean isLast){
    	
    	Vector<EPMDescribeLink> vec = new Vector<EPMDescribeLink>();
    	try{
    		
    		long logOid = CommonUtil.getOIDLongValue(rc);
    		Class cls = null;
    		
    		String columnName = "";
    		
    		
    		
    		QuerySpec qs = new QuerySpec();
    		int idxA = qs.addClassList(EPMDescribeLink.class, true);
    		int idxB = qs.addClassList(EPMDocument.class, false);
    		int idxC = qs.addClassList(WTPart.class, false);
    		int idx = 0;
    		if(rc instanceof EPMDocument){
    			cls = WTPart.class;
    			columnName = "roleBObjectRef.key.id";
    			idx = idxC;
    		}else if(rc instanceof WTPart){
    			cls = EPMDocument.class;
    			columnName = "roleAObjectRef.key.id";
    			idx = idxB;
    		}
    		//Join
    		qs.appendWhere(new SearchCondition(EPMDescribeLink.class,"roleBObjectRef.key.id",
    				EPMDocument.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{idxA,idxB});
    		
    		qs.appendAnd();
    		qs.appendWhere(new SearchCondition(EPMDescribeLink.class,"roleAObjectRef.key.id",
    				WTPart.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{idxA,idxC});
    		
    		qs.appendAnd();
    		qs.appendWhere(new SearchCondition(EPMDescribeLink.class,columnName,
    											SearchCondition.EQUAL,logOid),new int[]{idxA});
    		
    		if(isLast){
    			//최신 이터레이션
        		qs.appendAnd();
        		qs.appendWhere(VersionControlHelper.getSearchCondition(cls, true), new int[] { idx });
        		
        		//최신 버전
        		SearchUtil.addLastVersionCondition(qs, cls, idx);
    		}
    		
			QueryResult rt =PersistenceHelper.manager.find(qs);
			while(rt.hasMoreElements()){
				Object[] oo = (Object[]) rt.nextElement();
				EPMDescribeLink link = (EPMDescribeLink)oo[0];
				vec.add(link);
				
			}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return vec;
    }
	
	/**
	 * 2D와 연관된 3D 도면  부품-3D-2D
	 * @param number
	 * @return
	 */
	@Override
	public EPMDocument getDrawingToCad(String number){
		
		EPMDocument epm = null;
		 try{
			
			 Class class1 = EPMDocument.class;
			
			 QuerySpec qs = new QuerySpec();
			 int idx = qs.appendClassList(class1, true);
			 
			 if(qs.getConditionCount() > 0) { qs.appendAnd(); }
			 qs.appendWhere(new SearchCondition(EPMDocument.class,"master>number", SearchCondition.LIKE , "%"+number+"%", false), new int[]{idx});
			 
			 if(qs.getConditionCount() > 0) { qs.appendAnd(); }
			 qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idx });
			 
			 if(qs.getConditionCount() > 0) { qs.appendAnd(); }
			 qs.appendWhere(new SearchCondition(EPMDocument.class,EPMDocument.DOC_TYPE, SearchCondition.EQUAL , "CADASSEMBLY", false), new int[] { idx });
			 
			 SearchUtil.addLastVersionCondition(qs, EPMDocument.class, idx);
			 
			
			 QueryResult qr = PersistenceHelper.manager.find(qs);
			 System.out.println(qs);
			 System.out.println("qr size = "+qr.size());
			 while(qr.hasMoreElements()){
				 
				 Object obj[] = (Object[])qr.nextElement();
				 epm = (EPMDocument)obj[0];
				 
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
   	 
		 return epm;
	}
	
	
	
	/**
	 * GENERIC 에서 INSTANCE 도면
	 * epm.getFamilyTableStatus()  : 0 = Normal
	 * epm.getFamilyTableStatus()  : 1 = INSTANCE
	 * epm.getFamilyTableStatus()  : 2 = GENERIC
	 * epm.getFamilyTableStatus()  : 3 = GENERIC and INSTANCE
	 * @param epm
	 * @param list
	 * @return
	 */
	@Override
	public  List<EPMDocument>  getInstance(EPMDocument epm,List<EPMDocument> list){
		
		try{
			//EPMVariantLink link =
			long longOid = CommonUtil.getOIDLongValue(epm.getMaster());
			QuerySpec qs = new QuerySpec();
			
			Class cls1 = EPMVariantLink.class;
			Class cls2 = EPMDocument.class;
			
			//epm.getFamilyTableStatus();
			int idx1 = qs.addClassList(cls1, false);
			int idx2 = qs.addClassList(cls2, true);
			
			qs.appendWhere(new SearchCondition(EPMVariantLink.class,"roleAObjectRef.key.id",
					EPMDocument.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{idx1,idx2});
			
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EPMVariantLink.class,"roleBObjectRef.key.id",
					SearchCondition.EQUAL,longOid),new int[]{idx1});
			
			if(qs.getConditionCount() > 0) { qs.appendAnd(); }
			 qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idx2 });
			 
			 SearchUtil.addLastVersionCondition(qs, EPMDocument.class, idx2);
			 
			//Working Copy 제외
			if (qs.getConditionCount() > 0) {
				qs.appendAnd();
			}
			qs.appendWhere(new SearchCondition(EPMDocument.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk", false), new int[] { idx2 });
			
			//System.out.println(qs.toString());
			QueryResult rt = PersistenceHelper.manager.find(qs);
			
			
			int idx =1;
			while(rt.hasMoreElements()){
				Object[] obj = (Object[])rt.nextElement();
				
				EPMDocument instenceEPM = (EPMDocument)obj[0];
				list.add(instenceEPM);
				getInstance(instenceEPM,list);
				//System.out.println(idx+" . instenceEPM =" + instenceEPM.getNumber());
				idx++;
			}
			//System.out.println("========== list.size ="+list.size());
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		return list;
	}
	
	@Override
	public void cadPublishScheduler(){
		////CADCOMPONENT,CADASSEMBLY,CADDRAWING
		
		Config conf = ConfigImpl.getInstance();
    	boolean isPublish = conf.getString("scheduler.publish").equals("true") ? true : false;
		System.out.println("scheduler.publish = " + conf.getString("scheduler.publish"));
    	
		if(isPublish){
			getUnPublishList("CADCOMPONENT");
			getUnPublishList("CADDRAWING");
			getUnPublishList("CADASSEMBLY");
		}
		
	}
	
	private void getUnPublishList(String cadType){
		
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
			String location         = "/Default/PART_Drawing";
			/*if (location.length() > 0) {

					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					// Folder Search
					int folder_idx = query.addClassList(EpmLocation.class,
							false);
					query.appendWhere(new SearchCondition(EpmLocation.class,
							EpmLocation.EPM, EPMDocument.class,
							"thePersistInfo.theObjectIdentifier.id"),
							new int[] { folder_idx, idx });
					query.appendAnd();

					query.appendWhere(new SearchCondition(EpmLocation.class,
							"loc", SearchCondition.LIKE, location + "%"),
							new int[] { folder_idx });

			}*/
			
			
			System.out.println(query.toString());
			QueryResult rt  = PersistenceHelper.manager.find(query);
			createLog("=================START =================", cadType);
			int count =1;
			while(rt.hasMoreElements()){
				Object[] obj = (Object[])rt.nextElement();
				
				EPMDocument epm = (EPMDocument)obj[0];
				
				Representation representation = PublishUtils.getRepresentation(epm);
				if(representation == null){
					
					vec.addElement(epm);
					
					
					EpmPublishUtil.publish(epm);
					
					
					String log = count+"."+epm.getNumber() + "," + epm.getCADName();
					//System.out.println(log);
					createLog(log, cadType);
					count++;
				}
				//변환 성공하였지만 썸네일이 나오지 않는 도면 재변환. shjeong 21/10/6
				QueryResult repResult = PublishUtils.getRepresentations(epm);
				String thum_mini = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm), ContentRoleType.THUMBNAIL);	
				if(thum_mini==null){ 
			       if(repResult.size() > 0){
			        	EpmPublishUtil.publish(epm);
			       }
			    }
			}
			
			String msg ="========== Total NoN pulbish = "+ vec.size() +"==========";
			createLog(msg, cadType);
			createLog("=================END =================", cadType);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public ResultData cadRePublish(String oid){
		ResultData returnData = new ResultData();
		oid = StringUtil.checkNull(oid);
		try{
			if(oid.length()==0){
				throw new Exception(Message.get("Oid가 Null 입니다."));
			}
			
			RevisionControlled rc = (RevisionControlled)CommonUtil.getObject(oid);
			EPMDocument epm = null;
			
			if(rc instanceof EPMDocument){
				epm = (EPMDocument)rc;
			}else if(rc instanceof WTPart){
				WTPart part = (WTPart)rc;
				epm = DrawingHelper.service.getEPMDocument(part);
			}
			
			if(epm == null){
				throw new Exception(Message.get("CAD가 존재 하지 않습니다."));
			}
			
			EpmPublishUtil.publish(epm);
			
			returnData.setResult(true);
		}catch(Exception e){
			e.printStackTrace();
			returnData.setMessage(e.getLocalizedMessage());
			returnData.setResult(false);
		}
		
		return returnData;
	}
	
	private  void createLog(String log,String fileName) {
		//System.out.println("========== createLog ===========");
		String filePath = "D:\\e3ps\\ECDocument\\Schedule";
		System.out.println(log);
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
