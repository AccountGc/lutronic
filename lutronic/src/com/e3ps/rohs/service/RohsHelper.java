package com.e3ps.rohs.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.org.People;
import com.e3ps.part.dto.ObjectComarator;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.rohs.PartToRohsLink;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.dto.RoHSHolderData;
import com.e3ps.rohs.dto.RohsData;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.vc.VersionControlHelper;

public class RohsHelper {
	public static final RohsService service = ServiceFactory.getService(RohsService.class);
	public static final RohsHelper manager = new RohsHelper();
	
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<RohsData> list = new ArrayList<>();
    	
    	QuerySpec query = new QuerySpec();
    	int idx = query.addClassList(ROHSMaterial.class, true);
    	ReferenceFactory rf = new ReferenceFactory();
    	
    	try {
    		
    		String foid = StringUtil.checkNull((String) params.get("fid"));
    		if(foid.length() == 0) {
    			foid = StringUtil.checkNull((String) params.get("folder"));
    		}
    		
    		String islastversion 	= StringUtil.checkNull((String)params.get("islastversion"));
    		String rohsNumber 		= StringUtil.checkNull((String) params.get("number"));
    		String rohsName 		= StringUtil.checkNull((String) params.get("name"));
    		String description		= StringUtil.checkNull((String) params.get("description"));
    		String predate 			= StringUtil.checkNull((String) params.get("predate"));
    		String postdate 		= StringUtil.checkNull((String) params.get("postdate"));
    		String predate_modify	= StringUtil.checkNull((String) params.get("predate_modify"));
    		String postdate_modify	= StringUtil.checkNull((String) params.get("postdate_modify"));
    		String creator 			= StringUtil.checkNull((String) params.get("creator"));
    		String state 			= StringUtil.checkNull((String) params.get("state"));
    		String manufacture 		= StringUtil.checkNull((String) params.get("manufacture"));
    		String sortValue 		= StringUtil.checkNull((String) params.get("sortValue"));
    		String sortCheck 		= StringUtil.checkNull((String) params.get("sortCheck"));
			
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
				SearchUtil.setOrderBy(query, ROHSMaterial.class, idx, ROHSMaterial.MODIFY_TIMESTAMP, "sort", true);
			}
			
			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				RohsData data = new RohsData((ROHSMaterial) obj[0]);
				list.add(data);
			}

			map.put("list", list);
			map.put("topListCount", pager.getTotal());
			map.put("pageSize", pager.getPsize());
			map.put("total", pager.getTotalSize());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			
	    }catch (Exception e) {
			e.printStackTrace();
		}
    	return map;
    	
	}
	
	public JSONArray include_RohsView(String oid, String module, String roleType) throws Exception {
		List<RohsData> list = null;
		try {
			if(oid.length() > 0){
				if("rohs".equals(module)){
					ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
					list = RohsQueryHelper.service.getRepresentToLinkList(rohs,roleType);
				}else if("part".equals(module)){
					WTPart part = (WTPart)CommonUtil.getObject(oid);
					list = RohsQueryHelper.service.getPartToROHSList(part);
				}else {
					list = new ArrayList<RohsData>();
				}
			}else {
				list = new ArrayList<RohsData>();
			}
			//System.out.println("include_RohsView ObjectComarator START =" + list.size());
			Collections.sort(list, new ObjectComarator());
			//System.out.println("include_RohsView ObjectComarator end");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONArray.fromObject(list);
	}
	
	public Map<String, Object> listRohsFile(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<RohsData> list = new ArrayList<>();
    	
		String fileType = StringUtil.checkNull((String)params.get("fileType"));
		String publication_Start = StringUtil.checkNull((String)params.get("publication_Start"));
		String publication_End = StringUtil.checkNull((String)params.get("publication_End"));
		String fileName = StringUtil.checkNull((String)params.get("fileName"));
		
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
		
		PageQueryUtils pager = new PageQueryUtils(params, spec);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ROHSContHolder holder = (ROHSContHolder) obj[0];
			
			RoHSHolderData data = new RoHSHolderData(holder);
			RohsData rData = new RohsData(data.getRohs());
			list.add(rData);
		}
		map.put("list", list);
			
    	return map;
	}
	
	public Map<String, Object> listAUIRoHSPart(Map<String, Object> params) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String,Object>> partRohlist = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> partlist  = new ArrayList<Map<String,Object>>();
		
		String partNumber = StringUtil.checkNull((String)params.get("partNumber"));
		WTPart part = PartHelper.service.getPart(partNumber);
		String partOid = CommonUtil.getOIDString(part);
		//WTPart part = (WTPart)CommonUtil.getObject(partOid);
		if(part == null){
			return returnMap;
		}
		
		Map<String,Object>  productStateMap = RohsUtil.getProductRoHsState(partOid);
		returnMap.put("totalState", productStateMap.get("totalState"));
		returnMap.put("passCount", productStateMap.get("passCount"));
		returnMap.put("totalCount", productStateMap.get("totalCount"));
		returnMap.put("greenCount", productStateMap.get("greenCount"));
		returnMap.put("blackCount", productStateMap.get("blackCount"));
		returnMap.put("dumyCount", productStateMap.get("dumyCount"));
		returnMap.put("continueCount", productStateMap.get("continueCount"));
		returnMap.put("redCount", productStateMap.get("redCount"));
		returnMap.put("orangeCount", productStateMap.get("orangeCount"));
		returnMap.put("listCount", productStateMap.get("listCount"));
		returnMap.put("isDumy_SonPartsCount", productStateMap.get("isDumy_SonPartsCount"));
		
		partlist = RohsQueryHelper.service.childPartPutMap(part, partlist,0);
	    HashMap<String, Integer> stateMap = new HashMap<String, Integer>();
	    
	    int totalLevl = 0;
		for(int i=0; i<partlist.size(); i++){
			//Map<String,Object> partRohsMap = new HashMap<String, Object>();
			Map<String,Object> partMap = partlist.get(i);
			
			String oid = (String)partMap.get("partOid");
			int level = (Integer)partMap.get("level");
			
			if(totalLevl < level){
				totalLevl = level;
			}
			//String key = String.valueOf(count) + "_"+oid;
			WTPart supart = (WTPart)CommonUtil.getObject(oid);
			
			//System.out.println("1.supart =" + supart.getNumber() +"," + "Level"+level+"="+partMap.get("Level"+level));
			
			List<RohsData> rohslist = RohsQueryHelper.service.getPartToROHSList(supart);
			
			stateMap = RohsUtil.getRohsState(stateMap,oid);
			int rohsState = stateMap.get(oid);
			String state ="검은색";
			if(rohsState == RohsUtil.STATE_NOT_APPROVED){
				state ="빨강색";
			}else if(rohsState ==RohsUtil.STATE_NONE_ROHS){
				state ="주황색";
			}else if(rohsState ==RohsUtil.STATE_ALL_APPROVED){
				state ="녹색";
			}else if(rohsState ==RohsUtil.STATE_NOT_ROHS){
				state ="검은색";
			}
			
			if(rohslist.size()>0){
				for(RohsData rohsData : rohslist){
					//System.out.println("rohslist.size >0 .supart =" + supart.getNumber());
					List<ROHSContHolder> holderList = RohsQueryHelper.service.getROHSContHolder(rohsData.getOid());
					
					if(holderList.size() > 0){
						
						for(ROHSContHolder rohsHolder : holderList){
							Map<String,Object> partRohsMap = new HashMap<String, Object>();
							//partRohsMap1 = partMap;
							partRohsMap.put("rohsNumber", rohsData.getNumber());
							partRohsMap.put("rohsName", rohsData.getName());
							partRohsMap.put("rohsState", rohsState);
							partRohsMap.put("rohsStateName", state);
							partRohsMap.put("rohslifeState", rohsData.getState());
							partRohsMap.put("fileName", rohsHolder.getFileName());
							partRohsMap.put("docType", RohsUtil.getRohsDocTypeName(rohsHolder.getFileType()));
							
							partRohsMap = setPartROHMap(partRohsMap, partMap);
							//System.out.println("holderList.size >0 .supart =" + partRohsMap.get("partNumber") +", getFileName" +partRohsMap.get("fileName"));
							partRohlist.add(partRohsMap);
						}
					}else{
						//System.out.println("holderList.size =0 .supart =" + supart.getNumber());
						Map<String,Object> partRohsMap = new HashMap<String, Object>();
						partRohsMap.put("rohsNumber", rohsData.getNumber());
						partRohsMap.put("rohsName", rohsData.getName());
						partRohsMap.put("rohsState", rohsState);
						partRohsMap.put("rohslifeState", rohsData.getState());
						partRohsMap.put("rohsStateName", state);
						partRohsMap = setPartROHMap(partRohsMap, partMap);
						partRohlist.add(partRohsMap);
					}
				}
			}else{
				//System.out.println("rohslist.size =0 .supart =" + supart.getNumber());
				Map<String,Object> partRohsMap = new HashMap<String, Object>();
			
				partRohsMap.put("rohsState", rohsState);
				partRohsMap.put("rohsStateName", state);
				partRohsMap = setPartROHMap(partRohsMap, partMap);
				partRohlist.add(partRohsMap);
			}
			
		}
		
		returnMap.put("totalLevel", totalLevl);
		returnMap.put("partRohlist", partRohlist);
		/*
		System.out.println("list =" + partRohlist.size());
		
		for(int i=0; i<partRohlist.size(); i++){
			Map<String,Object> partMap2 = partRohlist.get(i);
			System.out.println("partRohlist.get(i) =" + partRohlist.get(i));
			//System.out.println(partMap2.get("fileName")+"," + partMap2.get("docType"));
		}
		*/
		return returnMap;
	}
	
	private Map<String,Object> setPartROHMap(Map<String,Object> partRohsMap, Map<String,Object> partMap){
		partRohsMap.put("partOid", partMap.get("partOid"));
		partRohsMap.put("partNumber", partMap.get("partNumber"));
		partRohsMap.put("partName", partMap.get("partName"));
		partRohsMap.put("partCreator", partMap.get("partCreateDate"));
		partRohsMap.put("partState", partMap.get("partState"));
		partRohsMap.put("level", partMap.get("level"));
		int level =(Integer)partMap.get("level");
		partRohsMap.put("L"+level, level);
		return partRohsMap;
	}
	
	public Map<String, Object> listRoHSProduct(Map<String, Object> params) throws Exception {
		String[] partOids = (String[]) params.get("partOids");
		Map<String,Object> result = new HashMap<String,Object>();
		List<PartData> list = new ArrayList<>(); 
		
		if(partOids != null) {
			for(String partOid : partOids) {
				WTPart part = (WTPart)CommonUtil.getObject(partOid);
				PartData data = new PartData(part);
				list.add(data);
			}
		}
		result.put("list", list);
		
		return result;
	}
	
	/**
	 * 물질 전체 리스트 가져오기
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public ArrayList<RohsData> totalList() throws Exception {
		ArrayList<RohsData> list = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();
    	
    	QuerySpec query = new QuerySpec();
    	int idx = query.addClassList(ROHSMaterial.class, true);
    	ReferenceFactory rf = new ReferenceFactory();
    	
    	try {
			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				RohsData data = new RohsData((ROHSMaterial) obj[0]);
				list.add(data);
			}
	    }catch (Exception e) {
			e.printStackTrace();
		}
    	return list;
	}
	
	public ROHSMaterial getRohs(String name) throws Exception {
		QuerySpec query = new QuerySpec();
    	int idx = query.addClassList(ROHSMaterial.class, true);
    	QuerySpecUtils.toEquals(query, idx, ROHSMaterial.class, ROHSMaterial.NAME, name);
    	QueryResult result = PersistenceHelper.manager.find(query);
    	ROHSMaterial rohs = null;
    	while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			rohs = (ROHSMaterial) obj[0];
		}
    	return rohs;
	}
	
	public List<Map<String,Object>> getRohsContent(String oid) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		List<ROHSContHolder> holderList = RohsQueryHelper.service.getROHSContHolder(oid);
		for(ROHSContHolder holder : holderList){
			ApplicationData data = holder.getApp();
			String url="/Windchill/plm/content/download?oid="+CommonUtil.getOIDString(data);
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("fileDown", url);
			map.put("fileType", RohsUtil.getRohsDocTypeName(holder.getFileType()));
			map.put("fileDate", holder.getPublicationDate());
			map.put("fileAppOid", CommonUtil.getOIDString(data));
			map.put("fileOid", CommonUtil.getOIDString(holder));
			map.put("fileRole", data.getRole().toString());
			map.put("fileTypeCode", holder.getFileType());
			map.put("fileName", holder.getFileName());
			list.add(map);
		}
		return list;
	}
	
	public int rohsCheck(Map<String, Object> params) throws Exception {
		String rohsName = StringUtil.checkNull((String) params.get("rohsName"));
		QuerySpec query = new QuerySpec();
    	int idx = query.addClassList(ROHSMaterial.class, true);
    	QuerySpecUtils.toEquals(query, idx, ROHSMaterial.class, ROHSMaterial.NAME, rohsName);
    	QueryResult result = PersistenceHelper.manager.find(query);
    	int count = 0;
    	while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ROHSMaterial rohs = (ROHSMaterial) obj[0];
			count++;
		}
    	return count;
	}
	
	public List<Map<String,String>> rohsFileType() {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String[] fileCode = AttributeKey.RohsKey.ROHS_CODE;
		String[] fileName = AttributeKey.RohsKey.ROHS_NAME;
		
		for(int i=0; i < fileCode.length; i++) {
			Map<String,String> map = new HashMap<String,String>();
			
			map.put("code", fileCode[i]);
			map.put("name", fileName[i]);
			
			list.add(map);
		}
		return list;
	}
	
	public String rohsNameCheck(Map<String, Object> params) throws Exception {
		ArrayList<String> list = (ArrayList<String>) params.get("list");
		String duplicate = "";
		for(String rohsName : list) {
			QuerySpec query = new QuerySpec();
	    	int idx = query.addClassList(ROHSMaterial.class, true);
	    	QuerySpecUtils.toEquals(query, idx, ROHSMaterial.class, ROHSMaterial.NAME, rohsName);
	    	QueryResult result = PersistenceHelper.manager.find(query);
	    	while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ROHSMaterial rohs = (ROHSMaterial) obj[0];
				return rohs.getName();
			}
		}
		
    	return duplicate;
	}
	
	public List<PartData> getROHSToPartList(ROHSMaterial rohs) throws Exception{
		return getROHSToPartList(rohs, CommonUtil.isLatestVersion(CommonUtil.getOIDString(rohs)));
	}
	
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
}
