package com.e3ps.rohs.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.structure.EPMVariantLink;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.People;
import com.e3ps.part.dto.ObjectComarator;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.VersionHelper;
import com.e3ps.part.util.PartUtil;
import com.e3ps.rohs.PartToRohsLink;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.RepresentToLink;
import com.e3ps.rohs.dto.RohsData;

@SuppressWarnings("serial")
public class StandardRohsQueryService extends StandardManager implements RohsQueryService {

	public static final String ROOTLOCATION = "/Default/ROHS";
	
	public static StandardRohsQueryService newStandardRohsQueryService() throws WTException {
		final StandardRohsQueryService instance = new StandardRohsQueryService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public QuerySpec getListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	QuerySpec query = new QuerySpec();
    	int idx = query.addClassList(ROHSMaterial.class, true);
    	ReferenceFactory rf = new ReferenceFactory();
    	
    	try {
    		
    		//String location 	= StringUtil.checkNull(request.getParameter("location"));
			String foid 		= StringUtil.checkNull(request.getParameter("fid"));
			if(foid.length() == 0) {
				foid = StringUtil.checkNull(request.getParameter("folder"));
			}
			
			String islastversion 	= StringUtil.checkNull(request.getParameter("islastversion"));
			String rohsNumber 		= StringUtil.checkNull(request.getParameter("rohsNumber"));
			String rohsName 		= StringUtil.checkNull(request.getParameter("rohsName"));
			String description		= StringUtil.checkNull(request.getParameter("description"));
			String predate 			= StringUtil.checkNull(request.getParameter("predate"));
			String postdate 		= StringUtil.checkNull(request.getParameter("postdate"));
			String predate_modify	= StringUtil.checkNull(request.getParameter("predate_modify"));
			String postdate_modify	= StringUtil.checkNull(request.getParameter("postdate_modify"));
			String creator 			= StringUtil.checkNull(request.getParameter("creator"));
			String state 			= StringUtil.checkNull(request.getParameter("state"));
			
			String manufacture 		= StringUtil.checkNull(request.getParameter("manufacture"));
						
			String sortValue 		= StringUtil.checkNull(request.getParameter("sortValue"));
			String sortCheck 		= StringUtil.checkNull(request.getParameter("sortCheck"));
			
			//System.out.println("ROHS getListQuery description =" + description);
			
			if(!StringUtil.checkString(islastversion)) {
	    		islastversion = "true";
	    	}

	    	if(query.getConditionCount() > 0) { query.appendAnd(); }
	    	query.appendWhere(VersionControlHelper.getSearchCondition(ROHSMaterial.class, true), new int[]{idx});
	    	
	    	if("true".equals(islastversion)) {
				 SearchUtil.addLastVersionCondition(query, ROHSMaterial.class, idx);
			}
			
	    	//문서번호
	    	if(rohsNumber.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.NUMBER, SearchCondition.LIKE, "%" + rohsNumber + "%", false), new int[]{idx});
	    	}
	    	
	    	//문서명
	    	if(rohsName.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.NAME, SearchCondition.LIKE, "%" + rohsName + "%", false), new int[]{idx});
	    	}
	    	
	    	// 설명
			if(description.length() > 0) {
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.DESCRIPTION, SearchCondition.LIKE, "%"+description+"%", false), new int[] {idx} );
			}
	    	
	    	//등록일
	    	if(predate.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.CREATE_TIMESTAMP ,SearchCondition.GREATER_THAN,DateUtil.convertStartDate(predate)), new int[]{idx});
	    	}
	    	
	    	if(postdate.length() > 0){
	    		if(query.getConditionCount() > 0)query.appendAnd();
	    		query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.CREATE_TIMESTAMP,SearchCondition.LESS_THAN,DateUtil.convertEndDate(postdate)), new int[]{idx});
	    	}
	    	
	    	//수정일
	    	if(predate_modify.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.MODIFY_TIMESTAMP, SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate_modify)), new int[]{idx});
	    	}
	    	
	    	if(postdate_modify.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.MODIFY_TIMESTAMP, SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate_modify)), new int[]{idx});
	    	}
	    	
	    	//등록자
	    	if(creator.length() > 0){
	    		People people = (People)rf.getReference(creator).getObject();
	    		WTUser user = people.getUser();
	    		if(query.getConditionCount() > 0) { query.appendAnd(); } 
	    		query.appendWhere(new SearchCondition(ROHSMaterial.class,"iterationInfo.creator.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(user)), new int[]{idx});
	    	}
	    	
	    	//상태
	    	if(state.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.LIFE_CYCLE_STATE, SearchCondition.EQUAL, state), new int[]{idx});
	    	}
	    	
	    	
	    	// 협력 업체
			if (manufacture.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", ROHSMaterial.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + manufacture + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				manufacture = "";
			}
			
	    	//소팅
	    	if (sortCheck == null) sortCheck = "true";
			if(sortValue != null && sortValue.length() > 0) {
				if("true".equals(sortCheck)){
					if( "creator".equals(sortValue)){
						if(query.getConditionCount() > 0) query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);
						
						ClassAttribute ca = new ClassAttribute(ROHSMaterial.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
			            query.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{idx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort" , true);
					}else if("manufacture".equals(sortValue)){
						
						
						
						AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
						if (aview != null) {
							if (query.getConditionCount() > 0) {
								query.appendAnd();
							}
							int _idx = query.appendClassList(StringValue.class, false);
							query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", ROHSMaterial.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
							query.appendAnd();
							query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
							
							query.appendOrderBy(new OrderBy(new ClassAttribute(StringValue.class,"value"), true), new int[] { _idx });
						
						}
					}else{
						query.appendOrderBy(new OrderBy(new ClassAttribute(ROHSMaterial.class,sortValue), true), new int[] { idx });
						
					}
				}else{
					if( "creator".equals(sortValue)){
						if(query.getConditionCount() > 0) query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);
						
						ClassAttribute ca = new ClassAttribute(ROHSMaterial.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
			            query.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{idx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort" , false);
					
						
					}else if("manufacture".equals(sortValue)){
						AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
						if (aview != null) {
							if (query.getConditionCount() > 0) {
								query.appendAnd();
							}
							int _idx = query.appendClassList(StringValue.class, false);
							query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", ROHSMaterial.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
							query.appendAnd();
							query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
							
							query.appendOrderBy(new OrderBy(new ClassAttribute(StringValue.class,"value"), false), new int[] { _idx });
						
						}
					}else{
						query.appendOrderBy(new OrderBy(new ClassAttribute(ROHSMaterial.class,sortValue), false), new int[] { idx });
					}
				}
			}else{
				//query.appendOrderBy(new OrderBy(new ClassAttribute(ROHSMaterial.class,"thePersistInfo.createStamp"), true), new int[] { idx }); 
				SearchUtil.setOrderBy(query, WTDocument.class, idx, WTDocument.MODIFY_TIMESTAMP, "sort", true);
			}
			
	    }catch (Exception e) {
			e.printStackTrace();
		}
    	//System.out.println(query.toString());
		return query;
    }
	
	@Override
	public List<ROHSContHolder> getROHSContHolder(String oid) throws Exception {
		
		long longOid = CommonUtil.getOIDLongValue(oid);
		List<ROHSContHolder> list = new ArrayList<ROHSContHolder>();
		
		QuerySpec qs = new QuerySpec();
		int idx = qs.addClassList(ROHSContHolder.class, true);
		
		qs.appendWhere(new SearchCondition(ROHSContHolder.class,"rohsReference.key.id", SearchCondition.EQUAL, longOid), new int[]{idx});
		
		QueryResult rt = PersistenceHelper.manager.find(qs);
		
		while(rt.hasMoreElements()){
			
			Object[] oo = (Object[]) rt.nextElement();
			ROHSContHolder rholder = (ROHSContHolder)oo[0];
			list.add(rholder);
		}
		
		return list;
		
	}
	
	@Override
	public List<RepresentToLink> getRepresentLink(ROHSMaterial rohs) throws Exception{
		//composition,represent
		List<RepresentToLink> list = new ArrayList<RepresentToLink>();
		String vr = CommonUtil.getVROID(rohs);
		rohs = (ROHSMaterial)CommonUtil.getObject(vr);
		QueryResult rt = PersistenceHelper.manager.navigate(rohs, "composition", RepresentToLink.class,false);
		
	
		while(rt.hasMoreElements()){
			RepresentToLink link = (RepresentToLink)rt.nextElement();
			list.add(link);
		}
		
		return list;
	}
	
	@Override
	public List<RohsData> getRepresentToLinkList(ROHSMaterial rohs,String roleType) throws Exception{
		
		//composition 구성,represent 대표
		List<RohsData> list = new ArrayList<RohsData>();
		String vr = CommonUtil.getVROID(rohs);
		rohs = (ROHSMaterial)CommonUtil.getObject(vr);
		
		QueryResult rt =PersistenceHelper.manager.navigate(rohs,roleType, RepresentToLink.class,true);
		while(rt.hasMoreElements()){
			ROHSMaterial rohsMaterial = (ROHSMaterial)rt.nextElement();
			//System.out.println("getRepresentToLinkList rohsMaterial =" + rohsMaterial.getNumber());
			RohsData data = new RohsData(rohsMaterial);
			list.add(data);
		}
		return list;
	}
	
	@Override
	public List<PartToRohsLink> getPartToRohsLinkList(RevisionControlled rev) throws Exception{
		//composition,represent
		List<PartToRohsLink> list = new ArrayList<PartToRohsLink>();
		String vr = CommonUtil.getVROID(rev);
		rev = (RevisionControlled)CommonUtil.getObject(vr);
		String roleType ="rohs";
		if(rev instanceof ROHSMaterial){
			roleType = "part";
		}
		
		QueryResult rt = PersistenceHelper.manager.navigate(rev, roleType, PartToRohsLink.class,false);
	
		while(rt.hasMoreElements()){
			PartToRohsLink link = (PartToRohsLink)rt.nextElement();
			list.add(link);
		}
		
		return list;
	}
	
	@Override
	public List<PartData> getROHSToPartList(ROHSMaterial rohs) throws Exception{
		return getROHSToPartList(rohs, VersionHelper.service.isLastVersion(rohs));
		/*
		List<PartData> list = new ArrayList<PartData>();
		String vr = CommonUtil.getVROID(rohs);
		rohs = (ROHSMaterial)CommonUtil.getObject(vr);
		
		QueryResult rt =PersistenceHelper.manager.navigate(rohs,"part", PartToRohsLink.class,true);
		while(rt.hasMoreElements()){
			WTPart part = (WTPart)rt.nextElement();
			PartData data = new PartData(part);
			list.add(data);
		}
		return list;
		*/
	}
	
	@Override
	public List<PartData> getROHSToPartList(ROHSMaterial rohs,boolean islastversion) throws Exception{
		
		
		List<PartData> list = new ArrayList<PartData>();
		String vr = CommonUtil.getVROID(rohs);
		rohs = (ROHSMaterial)CommonUtil.getObject(vr);
		
		QuerySpec qs = new QuerySpec();
		int idx1= qs.addClassList(WTPart.class, true);
        int idx2 = qs.addClassList(PartToRohsLink.class, true);
        
        if(qs.getConditionCount() > 0) { qs.appendAnd(); }
    	qs.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[]{idx1});
       
    	if(islastversion) {
        	SearchUtil.addLastVersionCondition(qs, WTPart.class, idx1);
		}
    	SearchUtil.setOrderBy(qs, WTPart.class, idx1, WTPart.NUMBER, false);
		
		QueryResult rt =PersistenceHelper.manager.navigate(rohs,"part", qs,true);
		while(rt.hasMoreElements()){
			WTPart part = (WTPart)rt.nextElement();
			PartData data = new PartData(part);
			list.add(data);
		}
		return list;
	}
	
	@Override
	public List<RohsData> getPartToROHSList(WTPart part) throws Exception{
		
		
		return getPartROHSList(part,  CommonUtil.isLatestVersion(part));
		
		/*
		List<RohsData> list = new ArrayList<RohsData>();
		String vr = CommonUtil.getVROID(part);
		part = (WTPart)CommonUtil.getObject(vr);
		
		QueryResult rt =PersistenceHelper.manager.navigate(part,"rohs", PartToRohsLink.class,true);
		while(rt.hasMoreElements()){
			ROHSMaterial rohs = (ROHSMaterial)rt.nextElement();
			RohsData data = new RohsData(rohs);
			list.add(data);
		}
		return list;
		*/
	}
	
	@Override
	public List<RohsData>  getPartROHSList(WTPart part,boolean islastversion) throws Exception {
		
		QuerySpec qs = new QuerySpec();
		int idx1= qs.addClassList(ROHSMaterial.class, true);
        int idx2 = qs.addClassList(PartToRohsLink.class, true);
        
        if(qs.getConditionCount() > 0) { qs.appendAnd(); }
    	qs.appendWhere(VersionControlHelper.getSearchCondition(ROHSMaterial.class, true), new int[]{idx1});
        
        if(islastversion) {
         	SearchUtil.addLastVersionCondition(qs, ROHSMaterial.class, idx1);
		}
    	SearchUtil.setOrderBy(qs, ROHSMaterial.class, idx1, ROHSMaterial.NUMBER, false);
	
		QueryResult rt = PersistenceHelper.manager.navigate(part, "rohs",qs,true);
		List<RohsData> list = new ArrayList<RohsData>();
		while(rt.hasMoreElements()){
			ROHSMaterial rohs = (ROHSMaterial)rt.nextElement();
			RohsData data = new RohsData(rohs);
		
			list.add(data);
		}
		
		return list;
	}
	
	
	@Override
	public QuerySpec listRoHSDataAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String fileType = StringUtil.checkNull(request.getParameter("fileType"));
		String publication_Start = StringUtil.checkNull(request.getParameter("publication_Start"));
		String publication_End = StringUtil.checkNull(request.getParameter("publication_End"));
		String fileName = StringUtil.checkNull(request.getParameter("fileName"));
		
		QuerySpec spec = new QuerySpec();
		int idx = spec.addClassList(ROHSContHolder.class, true);
		int idx2 = spec.addClassList(ROHSMaterial.class, false);
		
		spec.appendWhere(new SearchCondition(ROHSContHolder.class,"rohsReference.key.id",
				ROHSMaterial.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{idx,idx2});
		
		if(spec.getConditionCount() > 0) { spec.appendAnd(); }
		spec.appendWhere(VersionControlHelper.getSearchCondition(ROHSMaterial.class, true), new int[]{idx2});
       
    	SearchUtil.addLastVersionCondition(spec, ROHSMaterial.class, idx2);
		
		
		
		if(fileType.length() > 0) {
			if(spec.getConditionCount() > 0) {
				spec.appendAnd();
			}
			spec.appendWhere(new SearchCondition(ROHSContHolder.class, ROHSContHolder.FILE_TYPE, SearchCondition.EQUAL, fileType), new int[] {idx});
		}
		
		if(fileName.length() > 0) {
			if(spec.getConditionCount() > 0) {
				spec.appendAnd();
			}
			spec.appendWhere(new SearchCondition(ROHSContHolder.class, ROHSContHolder.FILE_NAME, SearchCondition.LIKE, "%"+fileName.toUpperCase()+"%",false), new int[] {idx});
		}
		
		
		
		if(publication_Start.length() > 0) {
			if(spec.getConditionCount() > 0){
				spec.appendAnd();
			}
			spec.appendWhere(new SearchCondition(ROHSContHolder.class, ROHSContHolder.PUBLICATION_DATE, SearchCondition.GREATER_THAN_OR_EQUAL, publication_Start), new int[] {idx});
		}
		
		
		if(publication_End.length() > 0) {
			if(spec.getConditionCount() > 0){
				spec.appendAnd();
			}
			spec.appendWhere(new SearchCondition(ROHSContHolder.class, ROHSContHolder.PUBLICATION_DATE, SearchCondition.LESS_THAN_OR_EQUAL, publication_End), new int[] {idx});
		}
		
		SearchUtil.setOrderBy(spec, ROHSContHolder.class, idx, ROHSContHolder.ROHS_REFERENCE + ".key.id", false);
		
		//System.out.println(spec.toString());
		return spec;
	}
	
	@Override
	public List<Map<String,Object>> childPartPutMap(WTPart part, List<Map<String,Object>> list,int level) throws Exception {
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		//boolean isChildCheck = isChildPartCheck(part);
		if(PartUtil.isChange(part.getNumber())){
			return list;
		}
		
		map.put("partOid", part.getPersistInfo().getObjectIdentifier().toString());
		map.put("partNumber", part.getNumber());
		map.put("partName", part.getName());
		map.put("partCreator", part.getCreatorFullName());
		map.put("partCreateDate", DateUtil.subString(DateUtil.getDateString(part.getPersistInfo().getCreateStamp(), "a"),0,10));
		map.put("partState", part.getLifeCycleState().getDisplay(Message.getLocale()));
		map.put("level", level);
		map.put("Level"+level, level);
		
		list.add(map);
		
		QueryResult result = isChildPart(part, true);
		List<WTPart> tempList = new ArrayList<WTPart>();
		while(result.hasMoreElements()) {
			Object[] o = (Object[])result.nextElement();
			WTPartMaster childPartMaster = (WTPartMaster)o[0];
			WTPart childPart = PartHelper.service.getPart(childPartMaster.getNumber());//getWTPartFormWTPartMaster(childPartMaster);
			if(PartUtil.isChange(childPart.getNumber())){
				continue;
			}
			tempList.add(childPart);
			
			
			/*
			if(childPart != null){
				childPartPutMap(childPart, list,level+1);
			}
			*/
		}
		
		Collections.sort(tempList, new ObjectComarator());
		for(WTPart childPart : tempList){
			
			if(childPart != null){
				childPartPutMap(childPart, list,level+1);
			}
			
		}
		
		return list;
	}
	
	public WTPart getWTPartFormWTPartMaster(WTPartMaster master) throws Exception {
		WTPart part = null;
		QuerySpec spec = new QuerySpec();
		
		int idx_part = spec.addClassList(WTPart.class, true);
		int idx_master = spec.addClassList(WTPartMaster.class, false);
		
		ClassAttribute idx_p = new ClassAttribute(WTPart.class, WTPart.MASTER_REFERENCE + ".key.id");
		ClassAttribute idx_m = new ClassAttribute(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id");
		
		spec.appendWhere(new SearchCondition(idx_p, SearchCondition.EQUAL, idx_m), new int[] {idx_part, idx_master});
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(master)),new int[] {idx_master});
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true), new int[] { idx_part });
		
		QueryResult result = PersistenceHelper.manager.find(spec);
		
		if(result.hasMoreElements()) {
			Object[] o = (Object[])result.nextElement();
			part = (WTPart)o[0];
		}
		return part;
	}
	
	
	private boolean isChildPartCheck(WTPart part) throws Exception {
		QueryResult result = isChildPart(part, false);
		return result.hasMoreElements();
	}
	
	private QueryResult isChildPart(WTPart part, boolean isData) throws Exception {
		QuerySpec spec = new QuerySpec();
		
		int idx_master = spec.addClassList(WTPartMaster.class, isData);
		int idx = spec.addClassList(WTPartUsageLink.class, false);
		int idx_part = spec.addClassList(WTPart.class, false);
		spec.setAdvancedQueryEnabled(true);
		
		ClassAttribute idx_l = new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id");
		ClassAttribute idx_p = new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id");
		
		spec.appendWhere(new SearchCondition(idx_l, SearchCondition.EQUAL, idx_p), new int[] {idx, idx_part});
		
		spec.appendAnd();
		ClassAttribute idx_l2 = new ClassAttribute(WTPartUsageLink.class, "roleBObjectRef.key.id");
		ClassAttribute idx_m = new ClassAttribute(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id");
		
		spec.appendWhere(new SearchCondition(idx_l2, SearchCondition.EQUAL, idx_m), new int[] {idx, idx_master});
		
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true), new int[] { idx_part });
		
		spec.appendJoin(idx, "roleA", part);
		
		if(!isData) {
			spec.appendGroupBy(new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"), idx_part, true);
		}
		
		spec.appendOrderBy(new OrderBy(new ClassAttribute(WTPartMaster.class, WTPartMaster.NUMBER), true), new int[] { idx_master });
		
		//System.out.println(spec.toString());
		QueryResult result = PersistenceServerHelper.manager.query(spec);
		
		return result;
	}
	
	
	@Override
	public int duplicateName(String rohsName) throws Exception {
		QuerySpec spec = new QuerySpec(ROHSMaterial.class);
		
		spec.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.NAME, SearchCondition.EQUAL, rohsName), new int[0]);
		
		if(spec.getConditionCount() > 0) { spec.appendAnd(); }
		spec.appendWhere(VersionControlHelper.getSearchCondition(ROHSMaterial.class, true), new int[]{0});
    	
		SearchUtil.addLastVersionCondition(spec, ROHSMaterial.class, 0);
		
		QueryResult result = PersistenceHelper.manager.find(spec);
		
		return result.size();
		
	}
	
	@Override
	public boolean duplicateLink(String partOid, String rohsOid) throws Exception {
		QuerySpec qs = new QuerySpec();
		
		int idx = qs.addClassList(PartToRohsLink.class, true);
		
		qs.appendWhere(new SearchCondition(PartToRohsLink.class, "roleAObjectRef.key.branchId", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(partOid)));
		
		qs.appendAnd();
		
		qs.appendWhere(new SearchCondition(PartToRohsLink.class, "roleBObjectRef.key.branchId", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(rohsOid)));
		
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		return qr.hasMoreElements();
		
	}
	
	@Override
	public boolean duplicateNumber(String partOid, String rohsNumber) throws Exception {
		
		boolean isDuble = false;
		QuerySpec qs = new QuerySpec();
		
		int idx = qs.addClassList(PartToRohsLink.class, true);
		
		qs.appendWhere(new SearchCondition(PartToRohsLink.class, "roleAObjectRef.key.branchId", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(partOid)));
		
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		//System.out.println(qs.toString());
		while(qr.hasMoreElements()){
			Object[] obj = (Object[])qr.nextElement();
			PartToRohsLink link = (PartToRohsLink)obj[0];
			
			ROHSMaterial rohs=(ROHSMaterial)link.getRoleBObject();
			//System.out.println("rohs.getNumber() =" + rohs.getNumber() +",rohsNumber="+rohsNumber + "===="+(rohs.getNumber().equals(rohsNumber)));
			if(rohs.getNumber().equals(rohsNumber)){
				isDuble = true;
			}
		}
		
		return isDuble;
		
	}
	
	/**
	 * 대표의 대표 구조 찾기
	 * @param rohs
	 * @param roleType
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<RohsData> getRepresentStructure(ROHSMaterial rohs,List<RohsData> representlist) throws Exception{
		
		//composition 구성,represent 대표
		
		String vr = CommonUtil.getVROID(rohs);
		rohs = (ROHSMaterial)CommonUtil.getObject(vr);
		
		List<RohsData> tempList = getRepresentToLinkList(rohs, "represent");
		
		for(RohsData data : tempList){
			//System.out.println("getRepresentStructure ="+ data.number);
			if(data.isLatest()){
				representlist.add(data);
				representlist = getRepresentStructure(data.rohs, representlist);
			}
			
		}
		
		return representlist;
	}
	
	/*
	private  List<RohsData> getRepresentStructurea(ROHSMaterial rohs,List<RohsData> representlist) throws Exception{
		
		String vr = CommonUtil.getVROID(rohs);
		rohs = (ROHSMaterial)CommonUtil.getObject(vr);
		
		List<RohsData> temprepresentlist = getRepresentToLinkList(rohs, "represent");
		
		for(RohsData data : representlist){
			System.out.println("getRepresentStructure ="+ data.number);
			representlist.add(data);
			representlist = getRepresentStructure(data.rohs, representlist);
		}
		
		return representlist;
		
	}
	*/
	
	
}
