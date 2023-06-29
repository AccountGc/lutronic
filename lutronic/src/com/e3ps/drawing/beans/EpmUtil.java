package com.e3ps.drawing.beans;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.enterprise.RevisionControlled;
import wt.epm.E3PSRENameObject;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMReferenceType;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.value.IBAHolder;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.struct.StructHelper;

import com.e3ps.change.EcoPartLink;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.drawing.util.EpmPublishUtil;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.e3ps.rohs.service.RohsHelper;



public class EpmUtil implements RemoteAccess{
	
	
	/**
	 * 일괄개정 ,부품,도면(3D,2D)
	 * @param linkArr
	 * @param isExpand
	 * @return
	 */
	public static String batchRevisePartAndDoc(String[] linkArr, boolean isExpand) {
		
		Transaction trx = new Transaction();
		String msg =Message.get("개정 되었습니다.");
		try {
			trx.start();

			ReferenceFactory rf = new ReferenceFactory();
			for (int i = 0; i < linkArr.length; i++) {
				String revisableOid = linkArr[i];
				EcoPartLink link = (EcoPartLink) CommonUtil.getObject(revisableOid);
				String eoNumber =link.getEco().getEoNumber();
				String reviseNote = Message.get("설변") + " ["+eoNumber+"] " +  Message.get("를 통해 개정 되었습니다.");
				WTPart part = PartHelper.service.getPart(link.getPart().getNumber(),link.getVersion());
				
				
				EPMDocument oldEpm = null;
				EPMDocument newEpm = null;
				
				WTPart newPart = null;

				EPMBuildRule ebr=PartSearchHelper.service.getBuildRule(part);
				//System.out.println("============ ebr =" + ebr);
				newPart = (WTPart) ObjectUtil.reviseNote(part,reviseNote);
				
				
				//System.out.println("============ newPart =" + newPart);
				
				/*개정시 물질과 연결*/
				RohsHelper.service.revisePartToROHSLink(part, newPart);
				/*개정 부품  속성 Setting*/
				initAttribute(newPart);
				if (ebr != null) {
					oldEpm = (EPMDocument) ebr.getBuildSource();
					if(oldEpm.getLifeCycleState().toString().equals("APPROVED")){
						newEpm = (EPMDocument) ObjectUtil.reviseNote(oldEpm,reviseNote);
						initAttribute(newEpm);
						
						EPMBuildRule newEbr = PartSearchHelper.service.getBuildRule(newPart);
						newEbr.setBuildSource(newEpm);
						PersistenceServerHelper.manager.update(newEbr);
	
						EPMBuildHistory ebh = getBuildHistory(newPart, oldEpm);
						if (ebh != null) {
							ebh.setBuiltBy(newEpm);
							PersistenceServerHelper.manager.update(ebh);
						}
	
						QueryResult qr = StructHelper.service.navigateDescribes(oldEpm, false);
						if (qr.size() > 0) {
							while (qr.hasMoreElements()) {
								EPMDescribeLink epmdescribelink = (EPMDescribeLink) qr.nextElement();
								WTPart tempPart = (WTPart) epmdescribelink.getDescribes();
								//System.out.println("OldEpm = "+tempPart.getNumber()+","+tempPart.getVersionIdentifier().getValue() +"=="+epmdescribelink.getDescribedBy().getNumber()+","+epmdescribelink.getDescribedBy().getVersionIdentifier().getValue());
								createLink(tempPart, newEpm, "passive");
							}
						}
					}

				}

				if (newEpm != null) {
					//EPMDocument epm2d = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster)newEpm.getMaster());
					Vector<EPMReferenceLink> vec2D = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)newEpm.getMaster());
					for(int h = 0 ; h < vec2D.size() ; h++){
						EPMReferenceLink epmlink = vec2D.get(h);
						EPMDocument epm2d = epmlink.getReferencedBy();
						if(epm2d.getDocType().toString().equals("CADDRAWING")){
							EPMDocument newepm2d = (EPMDocument)ObjectUtil.reviseNote(epm2d,reviseNote);
							EpmPublishUtil.publish(newepm2d);
						}
						
					}
					
					
					
				}else{
					if(oldEpm !=null){
						Vector<EPMReferenceLink> vec2D = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)oldEpm.getMaster());
						for(int h = 0 ; h < vec2D.size() ; h++){
							EPMReferenceLink epmlink = vec2D.get(h);
							EPMDocument epm2d = epmlink.getReferencedBy();
							if(epm2d.getDocType().toString().equals("CADDRAWING")){
								
								EPMDocument newepm2d = (EPMDocument)ObjectUtil.reviseNote(epm2d,reviseNote);
								EpmPublishUtil.publish(newepm2d);
							}
							
						}
					}
					
					
				}
				
				//관련 도면 EPMDescribleLink
				if(newPart != null){
					Vector<EPMDescribeLink> vecDec=EpmSearchHelper.service.getEPMDescribeLink(newPart, true);
					for(EPMDescribeLink linkDec :vecDec){
						EPMDocument epmDec = (EPMDocument)linkDec.getRoleBObject();
						String cadName = epmDec.getCADName();
						String extension = EpmUtil.getExtension(cadName).toUpperCase();
						//if(extension.equals("PDF") || extension.equals("BRD") || extension.equals("DSN")){
							EPMDocument newEPMDesc =(EPMDocument)ObjectUtil.reviseNote(epmDec,reviseNote);
							////System.out.println("관련 도면 createLink: "+ newPart.getNumber()+","+newPart.getVersionIdentifier().getValue() +"=="+newEPMDesc.getNumber()+","+newEPMDesc.getVersionIdentifier().getValue());
							createLink(newPart, newEPMDesc, "passive");
							////System.out.println("관련 도면 deletelink: "+ newPart.getNumber()+","+newPart.getVersionIdentifier().getValue() +"=="+epmDec.getNumber()+","+epmDec.getVersionIdentifier().getValue());
							EpmUtil.deleteDescribeLink(newPart, epmDec);
							
							IBAUtil.deleteIBA(newEPMDesc, AttributeKey.EPMKey.IBA_APPROVAL, "string");
							IBAUtil.deleteIBA(newEPMDesc, AttributeKey.EPMKey.IBA_CHECK, "string");
						//}
					}
				}
				
				link.setRevise(true);
				PersistenceHelper.manager.modify(link);
			}

			trx.commit();
		} catch (Exception e) {
			msg =  Message.get("개정시 오류가 발생 하였습니다.\n 관리자에게 문의 하세요.");
			e.printStackTrace();
			trx.rollback();
		} finally {
			if (trx != null)
				trx.rollback();
			trx = null;
		}
		return msg;
	}
	
	private static EPMBuildHistory getBuildHistory(WTPart wtpart, EPMDocument epmdocument) throws WTException {
		QueryResult qr = PersistenceHelper.manager.find(EPMBuildHistory.class, wtpart, "built", epmdocument);
		return qr.hasMoreElements() ? (EPMBuildHistory) qr.nextElement() : null;
	}
	
	public static boolean createLink(WTPart _part, EPMDocument _epm, String _linkType) {
		if (!RemoteMethodServer.ServerFlag) {
			try {
				Class[] classType = { WTPart.class, EPMDocument.class, String.class };
				Object[] value = { _part, _epm, _linkType };
				return ((Boolean) RemoteMethodServer.getDefault().invoke("createLink", "com.e3ps.drawing.beans.EpmUtil",
						null, classType, value)).booleanValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			if ("active".equalsIgnoreCase(_linkType)) {
				EPMBuildRule link = EPMBuildRule.newEPMBuildRule(_epm, _part, 0);
				PersistenceServerHelper.manager.insert(link);
			} else if ("ecad".equalsIgnoreCase(_linkType)) {
				EPMBuildRule link = EPMBuildRule.newEPMBuildRule(_epm, _part);
				PersistenceServerHelper.manager.insert(link);
			} else {
				EPMDescribeLink link = EPMDescribeLink.newEPMDescribeLink(_part, _epm);
				PersistenceServerHelper.manager.insert(link);
			}
		} catch (Exception e) {
			//System.out.println("Error !! : " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static Vector getReferencedBy(EPMDocument doc, boolean flag) {
		Vector vec = new Vector();
		try {
			HashMap epm2ds = new HashMap();

			Vector refedByVec = getReferenceDependency(doc, "referencedBy");
			for (int k = 0; k < refedByVec.size(); k++) {
				EPMReferenceLink referenceLink = (EPMReferenceLink) refedByVec.get(k);
				EPMDocument epm2d = referenceLink.getReferencedBy();

				if (epm2ds.containsKey(epm2d.getNumber())) {
					EPMReferenceLink preLink = (EPMReferenceLink) epm2ds.get(epm2d.getNumber());
					EPMDocument pre = preLink.getReferencedBy();

					int jEpm2D = new Integer(epm2d.getVersionIdentifier().getValue()).intValue();
					int jPre = new Integer(pre.getVersionIdentifier().getValue()).intValue();

					if (jEpm2D - jPre == 0) {
						int iEpm2D = new Integer(epm2d.getIterationIdentifier().getValue()).intValue();
						int iPre = new Integer(pre.getIterationIdentifier().getValue()).intValue();
						if (iEpm2D - iPre <= 0)
							continue;
						epm2ds.put(epm2d.getNumber(), referenceLink);
					} else if (jEpm2D - jPre > 0) {
						epm2ds.put(epm2d.getNumber(), referenceLink);
					}
				} else {
					epm2ds.put(epm2d.getNumber(), referenceLink);
				}
			}

			for (Iterator epm2dItr = epm2ds.values().iterator(); epm2dItr.hasNext();) {
				EPMReferenceLink referenceLink = (EPMReferenceLink) epm2dItr.next();
				if (flag)
					vec.add(referenceLink.getReferencedBy());
				else
					vec.add(referenceLink);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vec;
	}
	
	public static Vector getReferenceDependency(EPMDocument doc, String role) {
		Vector references = new Vector();
		Object dwg = null;
		EPMReferenceLink referenceLink = null;
		try {
			QueryResult queryReferences = null;
			if (role.equals("referencedBy")) {
				queryReferences = getReferenceByWithQuerySpec(doc);

				while (queryReferences.hasMoreElements()) {
					Object[] obj = (Object[]) queryReferences.nextElement();
					referenceLink = (EPMReferenceLink) obj[0];

					if (showDocument(referenceLink)) {
						references.add(referenceLink);
					}
				}
			} else {
				queryReferences = EPMStructureHelper.service.navigateReferences(doc, null, false);
				while (queryReferences.hasMoreElements()) {
					referenceLink = (EPMReferenceLink) queryReferences.nextElement();

					if (showDocument(referenceLink)) {
						references.add(referenceLink);
					}
				}
			}
		} catch (Exception e) {
			//System.out.println("[EpmHelper][getReferenceDependency]Error getting the Instance Type");
			e.printStackTrace();
			return new Vector();
		}
		return references;
	}
	
	private static QueryResult getReferenceByWithQuerySpec(EPMDocument epm) throws Exception {
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		ClassAttribute classattribute = null;
		OrderBy orderby = null;

		Class A0 = EPMReferenceLink.class;
		Class A1 = EPMDocument.class;
		Class A3 = EPMDocumentMaster.class;
		Class A2 = EPMDocumentMaster.class;

		int idx_A0 = qs.appendClassList(A0, true);
		int idx_A1 = qs.appendClassList(A1, false);
		int idx_A2 = qs.appendClassList(A2, false);
		int idx_A3 = qs.appendClassList(A3, false);

		qs.appendWhere(new SearchCondition(A0, "roleAObjectRef.key.id", A1, "thePersistInfo.theObjectIdentifier.id"),
				new int[] { idx_A0, idx_A1 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(A0, "roleBObjectRef.key.id", A2, "thePersistInfo.theObjectIdentifier.id"),
				new int[] { idx_A0, idx_A2 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(A1, "masterReference.key.id", A3, "thePersistInfo.theObjectIdentifier.id"),
				new int[] { idx_A1, idx_A3 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(A2, "number", "=", epm.getNumber()), new int[] { idx_A2 });
		qs.appendAnd();
		qs.appendWhere(VersionControlHelper.getSearchCondition(A1, true), new int[] { idx_A1 });

		classattribute = new ClassAttribute(A3, "number");
		orderby = new OrderBy(classattribute, false, null);
		qs.appendOrderBy(orderby, idx_A3);

		classattribute = new ClassAttribute(A1, "versionInfo.identifier.versionId");
		orderby = new OrderBy(classattribute, true, null);
		qs.appendOrderBy(orderby, idx_A1);

		classattribute = new ClassAttribute(A1, "iterationInfo.identifier.iterationId");

		SQLFunction toDate = SQLFunction.newSQLFunction("TO_NUMBER", classattribute);

		orderby = new OrderBy(toDate, true, null);
		qs.appendOrderBy(orderby, idx_A1);

		//System.out.println("[EpmHelper][getReferenceByWithQuerySpec]" + qs);

		return PersistenceHelper.manager.find(qs);
	}
	
	public static boolean deleteDescribeLink(WTPart wtpart, EPMDocument _epm) {
		if (!RemoteMethodServer.ServerFlag) {
			try {
				Class[] classType = { WTPart.class, EPMDocument.class };
				Object[] value = { wtpart, _epm };
				return ((Boolean) RemoteMethodServer.getDefault().invoke("deleteDescribeLink",
						"com.e3ps.drawing.beans.EpmUtil", null, classType, value)).booleanValue();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		try {
			QueryResult qr = PersistenceHelper.manager.navigate(wtpart, "describedBy", EPMDescribeLink.class, false);
			while (qr.hasMoreElements()) {
				EPMDescribeLink link = (EPMDescribeLink) qr.nextElement();
				EPMDocument epmDoc = (EPMDocument) link.getDescribedBy();

				if (CommonUtil.getOIDString(epmDoc).equals(CommonUtil.getOIDString(_epm)))
					PersistenceServerHelper.manager.remove(link);
			}
		} catch (WTException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean deleteAllDescribeLink(EPMDocument _epm) {
		if (!RemoteMethodServer.ServerFlag) {
			try {
				Class[] classType = { EPMDocument.class };
				Object[] value = { _epm };
				return ((Boolean) RemoteMethodServer.getDefault().invoke("deleteAllDescribeLink",
						"com.e3ps.drawing.beans.EpmUtil", null, classType, value)).booleanValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			QueryResult qr = PersistenceHelper.manager.navigate(_epm, "describes", EPMDescribeLink.class, false);
			while (qr.hasMoreElements()) {
				EPMDescribeLink link = (EPMDescribeLink) qr.nextElement();
				PersistenceServerHelper.manager.remove(link);
			}
		} catch (WTException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public QueryResult getEPMRefDoc(EPMDocument epm) {
		try {
			QuerySpec query = new QuerySpec(EPMDocument.class, EPMReferenceLink.class);
			query.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[1]);
			return PersistenceHelper.manager.navigate(epm, "referencedBy", query, true);
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (VersionControlException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean showDocument(EPMReferenceLink link) {
		TreeSet depTypesExcluded = null;
		try {
			depTypesExcluded = new TreeSet();
			String str1 = WTProperties.getServerProperties().getProperty(
					"com.ptc.windchill.cadx.caddoc.excludeDependencyTypes");
			if ((str1 != null) && (!str1.equals(""))) {
				StringTokenizer localStringTokenizer = new StringTokenizer(str1, ",");
				int i = localStringTokenizer.countTokens();
				String str2 = null;
				for (int j = 0; j < i; j++) {
					str2 = localStringTokenizer.nextToken();
					depTypesExcluded.add(new Integer(str2.trim()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return !depTypesExcluded.contains(new Integer(link.getDepType()));
	}
	
	public static boolean isWG(EPMDocument epm) {
	    boolean flag = true;
	    if (epm.getOwnerApplication().toString().equals("MANUAL")) {
	      flag = false;
	    }
	    return flag;
	}
	
	/**
	 * 부품 도면 연관 삭제
	 * @param oid
	 * @return
	 */
	public static boolean deleteBuildeRule(String oid){
		boolean isDelete = false;
		//System.out.println(" deleteBuildeRule ");
		try{
			
			WTPart part = (WTPart)CommonUtil.getObject(oid); 
			//System.out.println(" part = " + part);
			EPMBuildRule oBuildRule =PartSearchHelper.service.getBuildRule(part);//QueryResult btQr = PersistenceHelper.manager.navigate(part, "buildSource", EPMBuildRule.class, false);
			//System.out.println(" oBuildRule = " + oBuildRule);
			if(oBuildRule !=null){
				PersistenceServerHelper.manager.remove(oBuildRule);
			}
			
			return true;
			
				//PersistenceHelper.manager.delete(oBuildRule);
			
		}catch(Exception e){
			//System.out.println(" deleteBuildeRule isDelete = " + isDelete);
			e.printStackTrace();
		}
		
		return isDelete;
		
	}
	
	/**
	 * 3D 2D 참조항목 연결
	 * @param oid (3D oid)
	 * @return
	 */
	public static boolean createRelation(String oid){
		boolean isRelation = false;
		try{
			EPMDocument epm3D = (EPMDocument)CommonUtil.getObject(oid);
			
			EPMDocument epm2D = DrawingHelper.service.getLastEPMDocument(epm3D.getNumber()+"_2D");
			if(epm2D != null){
				//EPMReferenceLink link = EPMReferenceLink.newEPMReferenceLink(epm2D, (EPMDocumentMaster)epm3D.getMaster());
				EPMReferenceLink link = EPMReferenceLink.newEPMReferenceLink(epm2D, (EPMDocumentMaster)epm3D.getMaster(), epm3D.getCADName(), 1, EPMReferenceType.toEPMReferenceType("GENERAL"));
				PersistenceServerHelper.manager.insert(link);
			}
			
			isRelation = true;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return isRelation;
	}
	
	/**
	 * 3D 2D 참조항목 연결
	 * @param epm3D
	 * @param epm2D
	 * @return
	 */
	public static boolean createRelation(EPMDocument epm3D, EPMDocument epm2D){
		
		boolean isRelation = false;
		try{
			EPMReferenceLink link = EPMReferenceLink.newEPMReferenceLink(epm2D, (EPMDocumentMaster)epm3D.getMaster(), epm3D.getCADName(), 1, EPMReferenceType.toEPMReferenceType("GENERAL"));
			PersistenceServerHelper.manager.insert(link);
			
			isRelation = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return isRelation;
		
	}
	
	/**
	 * 3D와 2D EPMReferenceLink 삭제 
	 * @param epm3D
	 * @param epm2D
	 * @return
	 */
	public static boolean deleteRelation(EPMDocument epm3D, EPMDocument epm2D){
		
		boolean isDelete = false;
		try{
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return isDelete;
		
	}
	
	/**
	 * 부품과 2D도면 BuildeRule 연결
	 * @return
	 */
	public static boolean createBuildeRule(WTPart part){
		
		try{
			String number = part.getNumber();
			QuerySpec query = PartHelper.service.getEPMNumber(number);
            QueryResult qrs = PersistenceHelper.manager.find(query);
            if(qrs.size() > 0 ){
            	EPMDocument epm = null;
            	Object[] o = (Object[]) qrs.nextElement();
            	epm = (EPMDocument) o[0];
            	EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
            	PersistenceServerHelper.manager.insert(link);
            	
            	return true;
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * oid : Part WTPart 의 속성을  EPMDocument 로 Copy 
	 * @param oid
	 */
	public static void copyIBA(String oid){
		
		/*
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		
		try{
			EPMDocument epm=DrawingHelper.manager.getEPMDocument(part);
			
			if(epm != null){
				String approvedBy = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_APPROVEDBY);
				String designBy = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_DESIGNBY);
				String drawnDate = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_DRAWNDATE);
				String surface = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SURFACE);
				String heat = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_HEAT);
				String maker = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_MAKER);
				
				if(approvedBy.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_APPROVEDBY, approvedBy, "string");
				}
				if(designBy.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_DESIGNBY, designBy, "string");
				}
				if(drawnDate.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_DRAWNDATE, drawnDate, "string");				
				}
				if(surface.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SURFACE, surface, "string");
				}
				if(heat.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_HEAT, heat, "string");
				}
				if(maker.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_MAKER, maker, "string");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		*/
	}
	
	/**
	 * 3D와 2D의 이름 변경
	 * 3D의 속성값에 변경된 이름을 저장한다. (iba : Description)
	 * @param epm
	 * @param changeName
	 */
	public static void changeEPMName(EPMDocument epm, String changeName) {
		try{
			
			E3PSRENameObject.manager.EPMReName(epm, "", changeName, "", false); 
			
			Vector<EPMReferenceLink> vec = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm.getMaster());
			for(int i=0; i<vec.size(); i++) {
				EPMReferenceLink link = (EPMReferenceLink)vec.get(i);
				EPMDocument epm2D = link.getReferencedBy();
				
				E3PSRENameObject.manager.EPMReName(epm2D, "", changeName, "", false);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getExtension(String fileName){
		
		return fileName.substring(fileName.lastIndexOf(".")+1);
	}
	
	public static String getFileNameNonExtension(String fileName){
		
		String extension = getExtension(fileName);
		fileName = fileName.substring(0,fileName.lastIndexOf(extension)-1);
		return fileName;
	}
	
	public static String getNumberNonExtension(String number){
		String extension = getExtension(number);
		if(number.lastIndexOf(extension)>1)
		number = number.substring(0,number.lastIndexOf(extension)-1);
		return number;
	}
	
	public static String getPublishFile(EPMDocument epm,String publishName){
		String cadName = epm.getCADName();
		String name = epm.getName().replaceAll("[*]", "_");
		String fileName = EpmUtil.getFileNameNonExtension(publishName);
	    String extension1 = EpmUtil.getExtension(cadName);
	    fileName = fileName+"_"+name;
	   
	    String extension2 = EpmUtil.getExtension(publishName);
	    fileName = fileName+"."+extension2;
	    fileName  = fileName.replace("_drw", "");
	    fileName = fileName.replace("/", "-");
	    return fileName;
	}
	
	public static void deleteFile(String newFileName){
		
		File file = new File(newFileName);
		
		if(file.isFile()){
			file.delete();
		}
	}
	
	/**
	 * 일괄수정 ,부품,도면(3D,2D)
	 * @param linkArr
	 * @param isExpand
	 * @return
	 */
	public static String batchCheckInOutPartAndDoc(String[] linkArr, boolean isExpand) {
		
		Transaction trx = new Transaction();
		String msg =Message.get("수정 되었습니다.");
		try {
			trx.start();

			ReferenceFactory rf = new ReferenceFactory();
			for (int i = 0; i < linkArr.length; i++) {
				String revisableOid = linkArr[i];
				EcoPartLink link = (EcoPartLink) CommonUtil.getObject(revisableOid);
				String eoNumber =link.getEco().getEoNumber();
				String checkinNote = " ["+eoNumber+"]" + Message.get("를 통해 수정 되었습니다.");
				WTPart part = PartHelper.service.getPart(link.getPart().getNumber(),link.getVersion());
				
				EPMDocument oldEpm = null;
				EPMDocument newEpm = null;
				
				WTPart newPart = null;

				EPMBuildRule ebr=PartSearchHelper.service.getBuildRule(part);
				//System.out.println("============ ebr =" + ebr);
				
				E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) part, "INWORK");
				newPart = (WTPart)ObjectUtil.checkout(part);
				newPart = (WTPart)ObjectUtil.checkin(newPart ,checkinNote);
				E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) part, "APPROVED");
				
				//System.out.println("============ newPart =" + newPart);
				IBAUtil.deleteIBA(newPart, AttributeKey.EPMKey.IBA_APPROVAL, "string");
				IBAUtil.deleteIBA(newPart, AttributeKey.EPMKey.IBA_CHECK, "string");
				if (ebr != null) {
					oldEpm = (EPMDocument)ebr.getBuildSource();
					E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) oldEpm, "INWORK");
					newEpm = (EPMDocument)ObjectUtil.checkout(oldEpm);
					newEpm = (EPMDocument)ObjectUtil.checkin(newEpm ,checkinNote);
					E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) oldEpm, "APPROVED");
					IBAUtil.deleteIBA(newEpm, AttributeKey.EPMKey.IBA_APPROVAL, "string");
					IBAUtil.deleteIBA(newEpm, AttributeKey.EPMKey.IBA_CHECK, "string");
				}

				if (newEpm != null) {
					//EPMDocument epm2d = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster)newEpm.getMaster());
					Vector<EPMReferenceLink> vec2D = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)newEpm.getMaster());
					for(int h = 0 ; h < vec2D.size() ; h++){
						EPMReferenceLink epmlink = vec2D.get(h);
						EPMDocument epm2d = epmlink.getReferencedBy();
						if(epm2d.getDocType().toString().equals("CADDRAWING")){
							E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) epm2d, "INWORK");
							EPMDocument newEpm2d = (EPMDocument)ObjectUtil.checkout(epm2d);
							newEpm2d = (EPMDocument)ObjectUtil.checkin(newEpm2d ,checkinNote);
							E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) epm2d, "APPROVED");
							IBAUtil.deleteIBA(newEpm2d, AttributeKey.EPMKey.IBA_APPROVAL, "string");
							IBAUtil.deleteIBA(newEpm2d, AttributeKey.EPMKey.IBA_CHECK, "string");
						}
					}
					
				}else{
					if(oldEpm !=null){
						Vector<EPMReferenceLink> vec2D = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)oldEpm.getMaster());
						for(int h = 0 ; h < vec2D.size() ; h++){
							EPMReferenceLink epmlink = vec2D.get(h);
							EPMDocument epm2d = epmlink.getReferencedBy();
							if(epm2d.getDocType().toString().equals("CADDRAWING")){
								E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) epm2d, "INWORK");
								EPMDocument newEpm2d = (EPMDocument)ObjectUtil.checkout(epm2d);
								newEpm2d = (EPMDocument)ObjectUtil.checkin(newEpm2d ,checkinNote);
								E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) epm2d, "APPROVED");
								IBAUtil.deleteIBA(newEpm2d, AttributeKey.EPMKey.IBA_APPROVAL, "string");
								IBAUtil.deleteIBA(newEpm2d, AttributeKey.EPMKey.IBA_CHECK, "string");
							}
						}
					}
				}
				
				//관련 도면 EPMDescribleLink
				if(newPart != null){
					Vector<EPMDescribeLink> vecDec=EpmSearchHelper.service.getEPMDescribeLink(newPart, true);
					for(EPMDescribeLink linkDec :vecDec){
						EPMDocument epmDec = (EPMDocument)linkDec.getRoleBObject();
						String cadName = epmDec.getCADName();
						String extension = EpmUtil.getExtension(cadName).toUpperCase();
						//if(extension.equals("PDF") || extension.equals("BRD") || extension.equals("DSN") || extension.equals("ZIP")){
							EPMDocument newEPMDesc = null;
							E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) epmDec, "INWORK");
							newEPMDesc = (EPMDocument)ObjectUtil.checkout(epmDec);
							newEPMDesc = (EPMDocument)ObjectUtil.checkin(newEPMDesc ,checkinNote);
							E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) epmDec, "APPROVED");
							////System.out.println("관련 도면 createLink: "+ newPart.getNumber()+","+newPart.getVersionIdentifier().getValue() +"=="+newEPMDesc.getNumber()+","+newEPMDesc.getVersionIdentifier().getValue());
							IBAUtil.deleteIBA(newEPMDesc, AttributeKey.EPMKey.IBA_APPROVAL, "string");
							IBAUtil.deleteIBA(newEPMDesc, AttributeKey.EPMKey.IBA_CHECK, "string");
						//}
					}
				}
				
				link.setRevise(false);
				PersistenceHelper.manager.modify(link);
			}

			trx.commit();
		} catch (Exception e) {
			msg = Message.get("수정시 오류가 발생 하였습니다.\n 관리자에게 문의 하세요.");
			e.printStackTrace();
			trx.rollback();
		} finally {
			if (trx != null)
				trx.rollback();
			trx = null;
		}
		return msg;
	}
	
	/**
	 * AuthoringType 설정
	 * @param fileName
	 * @return
	 */
	public static String getAuthoringType(String fileName){
		
		String extension = getExtension(fileName);
		extension = extension.toUpperCase();
		String authoringType ="OTHER";
		//orCad(BRD,DSN),SolidWorks(SLDASM,SLDPRT,SLDPRT)AutoCad,기타 -PDF,일러스트
		
		if(extension.equals("PDF")) {
			authoringType = "OTHER";
		}else if (extension.equals("DSN") || extension.equals("BRD") || extension.equals("ZIP")){
			authoringType = "ORCAD";
		}else if (extension.equals("DWG")){
			authoringType = "ACAD";
		}else if (extension.equals("SLDASM") || extension.equals("SLDPRT") || extension.equals("SLDPRT")){
			authoringType = "SOLIDWORKS";
		}else if(extension.equals("PRT")||extension.equals("ASM")){
			authoringType = "PROE";
		}else{
			authoringType = "OTHER";
		}
		
		return authoringType;
	}
	
	/**
	 * 개정시 속성 초기화
	 * 검도자,승인자 삭제, REV 입력
	 * @param iba
	 */
	public static void initAttribute(RevisionControlled rc){
		
		IBAUtil.deleteIBA((IBAHolder)rc, AttributeKey.IBAKey.IBA_APR, "string");
		IBAUtil.deleteIBA((IBAHolder)rc, AttributeKey.IBAKey.IBA_CHK, "string");
		//IBAUtil.changeIBAValue(ibaHolder, varName, value, ibaType);
		IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_REV, rc.getVersionIdentifier().getValue(), "string");
		
	}
	
	/**
	 * 2D의 Dxf 파일 유무 체크
	 * @param drawAppData
	 * @return
	 */
	public static boolean isDwrainPublishFile(ApplicationData drawAppData){
		
		boolean isPublishFile = false;
		
		if(drawAppData.getRole().toString().equals("SECONDARY") && drawAppData.getFileName().lastIndexOf("dxf")>0){
			isPublishFile = true;
    	}
		
		return isPublishFile;
	}
	
	/**
	 * Creo 의 DWG 유무 체크
	 * @param epm
	 * @return
	 */
	public static boolean isCreoDrawing(EPMDocument epm){
		
		String application =epm.getAuthoringApplication().toString();
		String cadType = epm.getDocType().toString();
		boolean isCreoDrawing = application.equals("PROE") && cadType.equals("CADDRAWING");
		
		return isCreoDrawing;
		
	}
}
