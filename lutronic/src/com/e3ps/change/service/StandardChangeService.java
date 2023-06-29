package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcrPartLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.beans.ECAData;
import com.e3ps.change.beans.ECOData;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.development.devActive;
import com.e3ps.development.devOutPutLink;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.doc.service.DocumentQueryHelper;
import com.e3ps.groupware.workprocess.AsmApproval;
import com.e3ps.groupware.workprocess.service.AsmSearchHelper;
import com.e3ps.org.People;
import com.e3ps.org.beans.PeopleData;
import com.e3ps.org.service.UserHelper;
import com.e3ps.part.beans.PartData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.e3ps.part.service.VersionHelper;

import net.sf.saxon.exslt.Common;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.ManagedBaseline;


@SuppressWarnings("serial")
public class StandardChangeService extends StandardManager implements ChangeService{
	
	public static StandardChangeService newStandardChangeService() throws Exception {
		final StandardChangeService instance = new StandardChangeService();
		instance.initialize();
		return instance;
	}
	
	public static ChangeHelper manager = new ChangeHelper();
	
	@Override
	public Vector<EChangeActivityDefinition> getEOActivityDefinition(String eoType){
		return getEOActivityDefinition(eoType,"","");
	}
	
	@Override
	public Vector<EChangeActivityDefinition> getEOActivityDefinition(String eoType, String step){
		return getEOActivityDefinition(eoType,step,"");
	}
	/**
	 * ECA eoType(ECR,ECO) 별로 get
	 * @param eoType
	 */
	@Override
	public Vector<EChangeActivityDefinition> getEOActivityDefinition(String eoType,String step, String group){
		
		Vector<EChangeActivityDefinition> vec = new Vector<EChangeActivityDefinition>();
		/*
		try{
			
			QuerySpec qs = new QuerySpec();
			Class cls = EChangeActivityDefinition.class;
			int idx = qs.appendClassList(cls, true);
			
			qs.appendWhere(new SearchCondition(cls,EChangeActivityDefinition.EO_TYPE,SearchCondition.EQUAL,eoType),new int[]{0});
			if(step.length()>0){
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(cls,EChangeActivityDefinition.STEP,SearchCondition.EQUAL,step),new int[]{0});
			}
			if(group.length()>0){
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(cls,EChangeActivityDefinition.ACTIVE_GROUP,SearchCondition.EQUAL,group),new int[]{0});
			}
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls,EChangeActivityDefinition.IS_DISABLED,SearchCondition.IS_TRUE),new int[]{0});
			
			qs.appendOrderBy(new OrderBy(new ClassAttribute(cls,EChangeActivityDefinition.SORT_NUMBER), false), new int[] { 0 });  
			QueryResult rt = PersistenceHelper.manager.find(qs);
			while(rt.hasMoreElements()){
				Object[] obj = (Object[])rt.nextElement();
				EChangeActivityDefinition ead = (EChangeActivityDefinition)obj[0];
				vec.add(ead);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		return vec;
		
	}
	
	
	/**
	 * ECO 승인시 
	 * @param eco
	 */
	@Override
	public void approveEO(ECOChange eo) throws Exception{
		/*
		System.out.println("============ approveEO Start =============");
		try{
			Vector<EChangeActivity> nextAct=ECAHelper.service.getNextECA(eo,"");
			if(nextAct.size()>0){
				ECAHelper.service.assignECA(nextAct);
			}else{ 
				ECOHelper.service.completeECO((EChangeOrder)eo);
			}
		}catch(Exception e){
			System.out.println("[ChangeHelper]{approveEO} ERROR = " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		*/
	}
	
	
	/**
	 * ECA 완료
	 * @param eca
	 */
	@Override
	public void completeECA(EChangeActivity eca){
		/*
		System.out.println("============ completeECA Start ===============");
		try{
			String activeGroup = eca.getDefinition().getActiveGroup();
			boolean isNext = ECAHelper.service.isNextECA(eca,activeGroup); //true 이면 동일  step 없음
			if(isNext){
				//Dictionary Activity : ECO 사전 활동 ,Approve Activity:ECO 승인 이후
				Vector<EChangeActivity> nextAct = null;
				System.out.println("============ completeECA activeGroup : " + activeGroup);
				if(activeGroup.equals("Dictionary Activity")){
					nextAct = ECAHelper.service.getNextECA(eca,"Dictionary Activity");
				}else if(activeGroup.equals("Approve Activity")){
					nextAct = ECAHelper.service.getNextECA(eca);
				}else{
					nextAct = ECAHelper.service.getNextECA(eca);
				}
				
				System.out.println("============ completeECA activeGroup nextAct.size() = " + nextAct.size());
				if(nextAct.size()>0){
					ECAHelper.service.assignECA(nextAct);
				}else{
					
					if(eca.getEo() instanceof EChangeOrder){
						SessionHelper.manager.setPrincipal(eca.getEo().getCreatorName());
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eca.getEo(), State.toState("SUBMISSION"));
						SessionHelper.manager.setAdministrator();
						//ChangeECOHelper.manager.completeECO((EChangeOrder)eca.getEo());
					}else{
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eca.getEo(), State.toState("COMPLETED"));
						
					}
					//com.e3ps.groupware.workprocess.beans.WFItemHelper.manager.setWFItemState(eca.getEo(), "COMPLETED");
					
				}
			}
		}catch(Exception e){
			System.out.println("[ChangeHelper]{completeECA} ERROR = " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		*/
	}
	
	
	/**
	 * 관려 ECR,참조 ECR 
	 * @param eco
	 * @return
	 */
	@Override
	public Vector<RequestOrderLink> getRelationECR(EChangeOrder eco){
		Vector<RequestOrderLink> vec = new Vector();
		try{
			QueryResult qr = PersistenceHelper.manager.navigate(eco,"ecr",RequestOrderLink.class,false);
			while(qr.hasMoreElements()){	
				RequestOrderLink link = (RequestOrderLink) qr.nextElement();
				vec.add(link);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vec;
	}
	
	/**
	 * 관련 ECO 참조  ECO
	 * @param ecr
	 * @return
	 */
	@Override
	public Vector<RequestOrderLink> getRelationECO(EChangeRequest ecr){
		Vector<RequestOrderLink> vec = new Vector();
		try{
			QueryResult qr = PersistenceHelper.manager.navigate(ecr,"eco",RequestOrderLink.class,false);
			while(qr.hasMoreElements()){	
				RequestOrderLink link = (RequestOrderLink) qr.nextElement();
				vec.add(link);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return vec;
	}
	
	@Override
	public List<EADData> getEOActivityList(String eoType, String ecaOid) {
		Vector<EChangeActivityDefinition> ecaVec = ChangeHelper.service.getEOActivityDefinition(eoType);
		List<ECAData> ecaList = getEChangeActivtyList(eoType);
		List<EADData> list = new ArrayList<EADData>();
		/*
		
		EADData eadData= null;

		if(StringUtil.checkString(ecaOid)) {
			EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(ecaOid);
			ECOChange eo = eca.getEo();
			
			Vector<EChangeActivity> activeVec = ECAHelper.service.getECAList(eo);
			
			String ecaCheck = "false";
			
			
			for(EChangeActivityDefinition ead : ecaVec) {
				
				if(ead.getStep().equals("STEP1")){
         			continue;
         		}
				
				eadData = new EADData(ead);
				
				String ppOid ="";
         		String finishDate="";
        		String activeUserName ="";
        		String activeDepart ="";
        		String checked =ead.isIsNeed() == true ? "checked" : ""  ;
				
    			for(ECAData ecaData : ecaList) {
					if(ead.getActiveCode().equals(ecaData.activeCode)) {
						eadData.setActiveDepart(ecaData.departmentName);
						eadData.setActiveUserName(ecaData.userName);
						eadData.setPeopleOid(ecaData.peopleOid);
					}
				}
        		
				for(EChangeActivity ea : activeVec) {
					
					if(ea.getDefinition().equals(ead)){
						//System.out.println("Active Name ;::::::   " + ead.getName());
						WTUser activeUser = ea.getActiveUser();
						People pp =UserHelper.service.getPeople(activeUser);
						
						if(pp != null){
							activeUserName = pp.getName();
							activeDepart = pp.getDepartment().getName();
							ppOid = CommonUtil.getOIDString(pp);
						
							finishDate = DateUtil.getDateString(ea.getFinishDate(), "d") ;
						}
						checked ="checked";
						ecaCheck = "true";
						
						eadData.setActiveUserName(activeUserName);
						eadData.setActiveDepart(activeDepart);
						eadData.setPeopleOid(ppOid);
						eadData.setFinishDate(finishDate);
						break;
						
					}
					
				}
				
				eadData.setEcaCheck(ecaCheck);
				eadData.setChecked(checked);
				list.add(eadData);
			}
			
		}else {
			
			for(EChangeActivityDefinition ead : ecaVec) {
				
				eadData = new EADData(ead);
				for(ECAData ecaData : ecaList) {
					
					if(ead.getActiveCode().equals(ecaData.activeCode)) {
						
						eadData.setActiveDepart(ecaData.departmentName);
						eadData.setActiveUserName(ecaData.userName);
						eadData.setPeopleOid(ecaData.peopleOid);
					}
				}
				if(eadData.isNeed) {
					eadData.setChecked("checked");
				}
				
				list.add(eadData);
			}
		}
		*/
		//System.out.println(list);
		
		return list;
	}
	
	
	
	@Override
	public List<Map<String,String>> listEulB_IncludeAction(String partOid, String allBaseline, String baseline) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart)rf.getReference(partOid).getObject();
		
		QuerySpec qs = new QuerySpec();
		int ii = qs.appendClassList(ManagedBaseline.class, true);
		int jj = qs.appendClassList(BaselineMember.class,false);
		int kk = qs.appendClassList(WTPart.class,true);
		//int ll = qs.appendClassList(EOEul.class,false);
		//int mm =qs.appendClassList(EChangeOrder.class,true);


		qs.appendWhere(new SearchCondition(ManagedBaseline.class,"thePersistInfo.theObjectIdentifier.id",BaselineMember.class,"roleAObjectRef.key.id"),new int[]{ii,jj});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(BaselineMember.class,"roleBObjectRef.key.id",WTPart.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{jj,kk});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class,"masterReference.key.id","=",part.getMaster().getPersistInfo().getObjectIdentifier().getId()),new int[]{kk});
		//qs.appendAnd();
		//qs.appendWhere(new SearchCondition(ManagedBaseline.class,"thePersistInfo.theObjectIdentifier.id",EOEul.class,"baselineReference.key.id"),new int[]{ii,ll});
		//qs.appendAnd();
		//qs.appendWhere(new SearchCondition(EOEul.class,"ecoReference.key.id",EChangeOrder.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ll,mm});

		if(!"false".equals(allBaseline)){
			//int nn = qs.appendClassList(EulPartLink.class,false);
			//int oo = qs.appendClassList(WTPart.class,false);
			//qs.appendAnd();
			//qs.appendWhere(new SearchCondition(EulPartLink.class,"roleAObjectRef.key.id",WTPart.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{nn,oo});
			//qs.appendAnd();
			//qs.appendWhere(new SearchCondition(EulPartLink.class,"disabled",SearchCondition.IS_FALSE),new int[]{nn});
			//qs.appendAnd();
			//qs.appendWhere(new SearchCondition(WTPart.class,"masterReference.key.id","=",part.getMaster().getPersistInfo().getObjectIdentifier().getId()),new int[]{oo});
			//qs.appendAnd();
			//qs.appendWhere(new SearchCondition(EulPartLink.class,"roleBObjectRef.key.id",EOEul.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{nn,ll});
		}

		qs.appendOrderBy(new OrderBy(new ClassAttribute(ManagedBaseline.class,"thePersistInfo.createStamp"),true),new int[]{ii});
		//System.out.println(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		while(qr.hasMoreElements()){
			Object[] o = (Object[])qr.nextElement();
			ManagedBaseline bl = (ManagedBaseline)o[0];
			WTPart pp = (WTPart)o[1];

			PartData data = new PartData(pp);
             //EChangeOrder eo = (EChangeOrder)o[2];
            
			String bgcolor = "white";
			if(bl.getPersistInfo().getObjectIdentifier().toString().equals(baseline)){
				bgcolor = "khaki";
			}
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("color", bgcolor);
			map.put("partOid", pp.getPersistInfo().getObjectIdentifier().toString());
			map.put("baseOid", bl.getPersistInfo().getObjectIdentifier().toString());
			map.put("baseName", bl.getName());
			map.put("partVersion", data.getVersion());
			map.put("baseDate", DateUtil.getDateString(bl.getPersistInfo().getCreateStamp(), "d"));
			list.add(map);
		 }
		return list;
	}
	
	@Override
	public List<String[]> addECRPartCheck(String ecrOid) throws Exception {
		
		List<String[]> list = new ArrayList<String[]>();
		
		EChangeRequest ecr = (EChangeRequest)CommonUtil.getObject(ecrOid);
		QueryResult qr = PersistenceHelper.manager.navigate(ecr,"part",EcrPartLink.class,false);
		while(qr.hasMoreElements()){
			EcrPartLink link = (EcrPartLink)qr.nextElement();
			String version = link.getVersion();
			WTPartMaster master = (WTPartMaster)link.getPart();
			WTPart part = PartHelper.service.getPart(master.getNumber(),version);
			boolean isLast = PartSearchHelper.service.isLastPart(part);
			boolean isWorking = PartSearchHelper.service.isSelectEO(part);
			String isEo = "true";
			if(!isLast || !isWorking){
				isEo = "false";
			}
			
			String partOid = part.getPersistInfo().getObjectIdentifier().toString();
			String partNumber = part.getNumber();
			String partName = part.getName();
			String partVer = part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getSeries().getValue();
			
			String[] ecrPart = new String[]{partOid,partNumber,partName,partVer,isEo};
			
			list.add(ecrPart);
		}
		return list;
	}
	
	public List<ECAData> getEChangeActivtyList(String eoType){
		
		List<ECAData> list = new ArrayList<ECAData>();
		
		try{
			
			WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
			Class eoClass = ECOChange.class;
			
			if(eoType.equals("ECO") || eoType.equals("MCO") ||eoType.equals("IPO")){
				eoClass = EChangeOrder.class;
			}else if(eoType.equals("ECR")){
				eoClass = EChangeRequest.class;
			}else if(eoType.equals("ECN") || eoType.equals("MCN")){
				eoClass = EChangeNotice.class;
			}
			
			long longOid = CommonUtil.getOIDLongValue(user);
			QuerySpec qs = new QuerySpec(); 
			
			int eoIdx = qs.appendClassList(eoClass, true);
			
			qs.appendWhere(new SearchCondition(eoClass, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {eoIdx});
			
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(eoClass, ECOChange.EO_TYPE, SearchCondition.EQUAL , eoType), new int[] {eoIdx});
			
			qs.appendOrderBy(new OrderBy(new ClassAttribute(eoClass, "thePersistInfo.createStamp"), true), new int[] { eoIdx }); 
			
			QueryResult rt = PersistenceHelper.manager.find(qs);
			
			if(rt.size()>0){
				ECOChange eo =  null;
				while(rt.hasMoreElements()){
					
					Object[] o = (Object[])rt.nextElement();
					
					eo = (ECOChange)o[0];
					
					break;
				}
				
				String eoOid =CommonUtil.getOIDString(eo);
				list = ECAHelper.service.include_ecaList(eoOid);
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	@Override
	public List<EADData> getEOActivityUpdateList(ECOChange eo, String eoType) {
		List<ECAData> ecaList = getEChangeActivtyList(eoType);
		List<EADData> list = new ArrayList<EADData>();
		EADData eadData= null;
		/*
			//설변활동
			Vector<EChangeActivity> activeVec = ECAHelper.service.getECAList(eo);
			String ecaCheck = "false";
			String state = "";
			List<String> groupList =  new ArrayList<String>();
			HashMap<String, String> hash = new HashMap<String, String>();
			for(EChangeActivity eca : activeVec) {
				state = eca.getState().toString();
				if("COMPLETED".equals(eca.getState().toString())){
					hash.put(eca.getDefinition().getActiveGroup(), eca.getState().toString());
				}else{
					if(hash.containsKey(eca.getDefinition().getActiveGroup())) {
						continue;
					}else {
						hash.put(eca.getDefinition().getActiveGroup(), eca.getState().toString());
					}
				}
			}
			java.util.Iterator it = hash.keySet().iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				String value = hash.get(key);
				if(!"COMPLETED".equals(value)) {
					groupList.add(key);
				}
			}
			
			for(String groupCode : groupList) {
				if(!"".equals(groupCode) && groupCode.length() > 0){
					Vector<EChangeActivityDefinition> ecaVec = ChangeHelper.service.getEOActivityDefinition(eoType, "", groupCode);
					for(EChangeActivityDefinition ead : ecaVec) {
						
						eadData = new EADData(ead);
						
						String ppOid ="";
		         		String finishDate="";
		        		String activeUserName ="";
		        		String activeDepart ="";
		        		String checked =ead.isIsNeed() == true ? "checked" : ""  ;
						
		    			for(ECAData ecaData : ecaList) {
							if(ead.getActiveCode().equals(ecaData.activeCode)) {
								eadData.setActiveDepart(ecaData.departmentName);
								eadData.setActiveUserName(ecaData.userName);
								eadData.setPeopleOid(ecaData.peopleOid);
							}
						}
		        		
						for(EChangeActivity ea : activeVec) {
							
							if(ea.getDefinition().equals(ead)){
								WTUser activeUser = ea.getActiveUser();
								People pp =UserHelper.service.getPeople(activeUser);
								if(pp != null){
									activeUserName = pp.getName();
									activeDepart = pp.getDepartment().getName();
									ppOid = CommonUtil.getOIDString(pp);
								
									finishDate = DateUtil.getDateString(ea.getFinishDate(), "d") ;
								}
								checked ="checked";
								ecaCheck = "true";
								
								eadData.setActiveUserName(activeUserName);
								eadData.setActiveDepart(activeDepart);
								eadData.setPeopleOid(ppOid);
								eadData.setFinishDate(finishDate);
								break;
							}
						}
						eadData.setEcaCheck(ecaCheck);
						eadData.setChecked(checked);
						list.add(eadData);
					}
				}
			}
		*/
		return list;
	}
	
	/**
	 * EO 와 Part 의 Name으로 Baseline 검색
	 * @param eoNumber
	 * @param partNumber
	 * @return
	 * @throws Exception
	 */
	@Override
	public ManagedBaseline getEOToPartBaseline(String eoNumber,String partNumber) throws Exception{
		ManagedBaseline baseline = null;
		String name=eoNumber+":"+partNumber;
		QuerySpec qs = new QuerySpec();
		int ii = qs.appendClassList(ManagedBaseline.class, true);
		
		qs.appendWhere(new SearchCondition(ManagedBaseline.class,ManagedBaseline.NAME,SearchCondition.EQUAL,name,true),new int[]{ii});
		//System.out.println(qs.toString());
		QueryResult rt = PersistenceHelper.manager.find(qs);
		
		while(rt.hasMoreElements()){
			Object[] obj = (Object[])rt.nextElement();
			baseline = (ManagedBaseline)obj[0];
		}
		
		return baseline;
		
	}
	
	/**
	 * Baseline 그룹핑
	 */
	@Override
	public List<Map<String, String>> getGroupingBaseline(String partOid, String allBaseline, String baseline) throws Exception{
		
		List<Map<String, String>> list = listEulB_IncludeAction(partOid, allBaseline, baseline);
		List<Map<String, String>> list2 = new ArrayList<Map<String,String>>();//listEulB_IncludeAction(partOid, allBaseline, baseline);
		Map<String, String> tempMap = new HashMap<String, String>();
		for(Map<String, String> data : list){
			
			Map<String, String> groupMap = new HashMap<String, String>();
			String baseName = data.get("baseName");
			String tempName = baseName.substring(0, baseName.indexOf(":"));
			String baseOid = data.get("baseOid");
			String partOidBaseline = data.get("partOid");
//			System.out.println(baseName+" , " +tempName+ tempMap.containsKey(tempName));
			if(tempMap.containsKey(tempName)){
				continue;
			}else{
				tempMap.put(tempName, tempName);
				groupMap.put("baseName", tempName);
				groupMap.put("baseOid", baseOid);
				groupMap.put("partOid", partOidBaseline);
			}
			
			list2.add(groupMap);
		}
		
		return list2;
	}
	
	@Override
	public ManagedBaseline getLastBaseline(String partOid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart)rf.getReference(partOid).getObject();
		
		QuerySpec qs = new QuerySpec();
		int ii = qs.appendClassList(ManagedBaseline.class, true);
		int jj = qs.appendClassList(BaselineMember.class,false);
		int kk = qs.appendClassList(WTPart.class,true);
		//int ll = qs.appendClassList(EOEul.class,false);
		//int mm =qs.appendClassList(EChangeOrder.class,true);


		qs.appendWhere(new SearchCondition(ManagedBaseline.class,"thePersistInfo.theObjectIdentifier.id",BaselineMember.class,"roleAObjectRef.key.id"),new int[]{ii,jj});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(BaselineMember.class,"roleBObjectRef.key.id",WTPart.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{jj,kk});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class,"masterReference.key.id","=",part.getMaster().getPersistInfo().getObjectIdentifier().getId()),new int[]{kk});
		

		

		qs.appendOrderBy(new OrderBy(new ClassAttribute(ManagedBaseline.class,"thePersistInfo.createStamp"),true),new int[]{ii});
		//System.out.println(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		while(qr.hasMoreElements()){
			Object[] o = (Object[])qr.nextElement();
			ManagedBaseline bl = (ManagedBaseline)o[0];
			return bl;
		 }
		return null;
	}
	
	@Override
    public List<ECOData> include_ECOList(String oid, String moduleType) throws Exception {
    	List<ECOData> list = new ArrayList<ECOData>();
    	if(StringUtil.checkString(oid)){
    		if("doc".equals(moduleType)) {
        		List<ECOData> dataList = ECOSearchHelper.service.getECOListToLinkRoleName(oid, "used");
        		for(ECOData data : dataList) {
        			list.add(data);
        		}
        	}
    	}
    	return list;
    }
}

