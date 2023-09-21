package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.EOData;
import com.e3ps.change.beans.EoComparator;
import com.e3ps.change.service.StandardChangeWfService.NumberAscCompare;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.People;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.dto.RohsData;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.baseline.ManagedBaseline;

public class ECOHelper {
	public static final ECOService service = ServiceFactory.getService(ECOService.class);
	public static final ECOHelper manager = new ECOHelper();
	
	public Map<String, Object> list(Map<String, Object> params) throws Exception{
		Map<String, Object> map = new HashMap<>();
		ArrayList<ECOData> list = new ArrayList<>();
		
		QuerySpec qs = null; 
		try{
			String name = StringUtil.checkNull((String) params.get("name"));
			String number = StringUtil.checkNull((String) params.get("number"));
			String eoType = StringUtil.checkNull((String) params.get("eoType"));
			
			String predate = StringUtil.checkNull((String) params.get("predate"));
			String postdate = StringUtil.checkNull((String) params.get("postdate"));
			
			String creator = StringUtil.checkNull((String) params.get("creator"));
			String state = StringUtil.checkNull((String) params.get("state"));
			
			String licensing = StringUtil.checkNull((String) params.get("licensing"));
			
			String model = StringUtil.checkNull((String)params.get("model"));
			
			String sortCheck =StringUtil.checkNull((String) params.get("sortCheck"));
			String sortValue =StringUtil.checkNull((String) params.get("sortValue"));
			
			String riskType = StringUtil.checkNull((String) params.get("riskType"));
			String preApproveDate = StringUtil.checkNull((String) params.get("preApproveDate"));
			String postApproveDate = StringUtil.checkNull((String) params.get("postApproveDate"));
			
			qs = new QuerySpec(); 
			Class ecoClass = EChangeOrder.class;
			int ecoIdx = qs.appendClassList(ecoClass, true);
			//제목
			if(name.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NAME, SearchCondition.LIKE, "%"+name+"%", false), new int[] {ecoIdx});
			}
			//번호
			if(number.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NUMBER, SearchCondition.LIKE, "%"+number+"%", false), new int[] {ecoIdx});
			}
			
			
			//등록일
			if(predate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(predate)), new int[] {ecoIdx});
			}
			//등록일
			if(postdate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postdate)), new int[] {ecoIdx});
			}
			//상태
			if(state.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LIFE_CYCLE_STATE, SearchCondition.EQUAL , state), new int[] {ecoIdx});
			}//creator.key.id
			
			//등록자
			if(creator.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				People pp = (People)CommonUtil.getObject(creator);
				long longOid = CommonUtil.getOIDLongValue(pp.getUser());
				qs.appendWhere(new SearchCondition(ecoClass, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {ecoIdx});
			}
			
			
			//ECO 구분
			if(eoType.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, eoType, false), new int[] {ecoIdx});
			}else{
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
//				qs.appendOpenParen();
//				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, ECOKey.ECO_DEV, false), new int[] {ecoIdx});
//				
//				qs.appendOr();
//				
//				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, ECOKey.ECO_PRODUCT, false), new int[] {ecoIdx});
//				qs.appendCloseParen();
				
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, ECOKey.ECO_CHANGE, false), new int[] {ecoIdx});
			}
			
			//인허가 구분
			if(licensing.length()>0){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				if(licensing.equals("NONE")){
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LICENSING_CHANGE, SearchCondition.IS_NULL,true), new int[] {ecoIdx});
				}else{
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LICENSING_CHANGE, SearchCondition.EQUAL, licensing, false), new int[] {ecoIdx});
				}
				
			}
			
			//인허가 구분
			if(riskType.length()>0){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				if(riskType.equals("NONE")){
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.RISK_TYPE, SearchCondition.IS_NULL,true), new int[] {ecoIdx});
				}else{
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.RISK_TYPE, SearchCondition.EQUAL, riskType, false), new int[] {ecoIdx});
				}
				
			}
			
			//승인일
			if(preApproveDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecoClass, "eoApproveDate", SearchCondition.GREATER_THAN_OR_EQUAL ,preApproveDate), new int[] {ecoIdx});
			}
			//승인일
			if(postApproveDate .length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecoClass, "eoApproveDate", SearchCondition.LESS_THAN_OR_EQUAL , postApproveDate), new int[] {ecoIdx});
			}
			
			//제품명
			if(model.length() > 0){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.MODEL, SearchCondition.EQUAL, model, false), new int[] {ecoIdx});
			}
			
			String[] completeParts = (String[]) params.get("completeParts");
			if(completeParts != null && completeParts.length > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				
				int idx_partLink = qs.appendClassList(EOCompletePartLink.class, false);
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.theObjectIdentifier.id", EOCompletePartLink.class, EOCompletePartLink.ROLE_BOBJECT_REF + ".key.id"), new int[] {ecoIdx, idx_partLink});

				qs.appendAnd();
				qs.appendOpenParen();
				for(int i=0; i < completeParts.length; i++) {
					
					if(i != 0) {
						qs.appendOr();
					}
					
					String completePart = completeParts[i];
					WTPart part = (WTPart)CommonUtil.getObject(completePart);
					qs.appendWhere(new SearchCondition(EOCompletePartLink.class, EOCompletePartLink.ROLE_AOBJECT_REF+".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(part.getMaster())),new int[] {idx_partLink});
					
				}
				
				qs.appendCloseParen();
			}
			
			if(sortValue != null && sortValue.length() > 0) {
				if("true".equals(sortCheck)){
					if( !"creator".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeOrder.class,sortValue), true), new int[] { ecoIdx });
					}else{
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EChangeOrder.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , true);
					}
					
				}else{
					if( !"creator".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeOrder.class,sortValue), false), new int[] { ecoIdx });
					}else{
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EChangeOrder.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , false);
					}
				}
			}else{
				qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass, "thePersistInfo.modifyStamp"), true), new int[] { ecoIdx }); 
			}
			
			PageQueryUtils pager = new PageQueryUtils(params, qs);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ECOData data = new ECOData((EChangeOrder) obj[0]);
				list.add(data);
			}

			map.put("list", list);
			map.put("topListCount", pager.getTotal());
			map.put("pageSize", pager.getPsize());
			map.put("total", pager.getTotalSize());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			 
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	public Map<String, Object> listEO(Map<String, Object> params) throws Exception{
		Map<String, Object> map = new HashMap<>();
		ArrayList<EOData> list = new ArrayList<>();
		
		QuerySpec qs = null; 
		try{
			String name = StringUtil.checkNull((String)params.get("name"));
			String number = StringUtil.checkNull((String)params.get("number"));
			String eoType = StringUtil.checkNull((String)params.get("eoType"));
			
			String predate = StringUtil.checkNull((String)params.get("predate"));
			String postdate = StringUtil.checkNull((String)params.get("postdate"));
			
			String creator = StringUtil.checkNull((String)params.get("creator"));
			String state = StringUtil.checkNull((String)params.get("state"));
			
			String licensing = StringUtil.checkNull((String)params.get("licensing"));
			
			String model = StringUtil.checkNull((String)params.get("model"));  
			
			String sortCheck =StringUtil.checkNull((String)params.get("sortCheck"));
			String sortValue =StringUtil.checkNull((String)params.get("sortValue"));
			
			String riskType = StringUtil.checkNull((String)params.get("riskType"));
			String preApproveDate = StringUtil.checkNull((String)params.get("preApproveDate"));
			String postApproveDate = StringUtil.checkNull((String)params.get("postApproveDate"));
			
			//System.out.println("Query licensing ====="+licensing);
			//System.out.println("Query riskType ====="+riskType);
			
			qs = new QuerySpec(); 
			Class ecoClass = EChangeOrder.class;
			int ecoIdx = qs.appendClassList(ecoClass, true);
			//제목
			if(name.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NAME, SearchCondition.LIKE, "%"+name+"%", false), new int[] {ecoIdx});
			}
			//번호
			if(number.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NUMBER, SearchCondition.LIKE, "%"+number+"%", false), new int[] {ecoIdx});
			}
			
			
			//등록일
			if(predate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(predate)), new int[] {ecoIdx});
			}
			//등록일
			if(postdate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postdate)), new int[] {ecoIdx});
			}
			//상태
			if(state.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LIFE_CYCLE_STATE, SearchCondition.EQUAL , state), new int[] {ecoIdx});
			}//creator.key.id
			
			//등록자
			if(creator.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				People pp = (People)CommonUtil.getObject(creator);
				long longOid = CommonUtil.getOIDLongValue(pp.getUser());
				qs.appendWhere(new SearchCondition(ecoClass, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {ecoIdx});
			}
			
			
			//ECO 구분
			if(eoType.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, eoType, false), new int[] {ecoIdx});
			}else{
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendOpenParen();
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, ECOKey.ECO_DEV, false), new int[] {ecoIdx});
				
				qs.appendOr();
				
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, ECOKey.ECO_PRODUCT, false), new int[] {ecoIdx});
				qs.appendCloseParen();
			}
			
			//인허가 구분
			if(licensing.length()>0){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				if(licensing.equals("NONE")){
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LICENSING_CHANGE, SearchCondition.IS_NULL,true), new int[] {ecoIdx});
				}else{
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LICENSING_CHANGE, SearchCondition.EQUAL, licensing, false), new int[] {ecoIdx});
				}
				
			}
			
			//인허가 구분
			if(riskType.length()>0){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				if(riskType.equals("NONE")){
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.RISK_TYPE, SearchCondition.IS_NULL,true), new int[] {ecoIdx});
				}else{
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.RISK_TYPE, SearchCondition.EQUAL, riskType, false), new int[] {ecoIdx});
				}
				
			}
			
			//승인일
			if(preApproveDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecoClass, "eoApproveDate", SearchCondition.GREATER_THAN_OR_EQUAL ,preApproveDate), new int[] {ecoIdx});
			}
			//승인일
			if(postApproveDate .length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecoClass, "eoApproveDate", SearchCondition.LESS_THAN_OR_EQUAL , postApproveDate), new int[] {ecoIdx});
			}
			
			
			
			//제품명
			if (model.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.MODEL, SearchCondition.EQUAL, model, false), new int[] {ecoIdx});
			}
			
			String[] completeParts = (String[])params.get("completeParts");
			if(completeParts != null && completeParts.length > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				
				int idx_partLink = qs.appendClassList(EOCompletePartLink.class, false);
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.theObjectIdentifier.id", EOCompletePartLink.class, EOCompletePartLink.ROLE_BOBJECT_REF + ".key.id"), new int[] {ecoIdx, idx_partLink});

				qs.appendAnd();
				qs.appendOpenParen();
				for(int i=0; i < completeParts.length; i++) {
					
					if(i != 0) {
						qs.appendOr();
					}
					
					String completePart = completeParts[i];
					WTPart part = (WTPart)CommonUtil.getObject(completePart);
					qs.appendWhere(new SearchCondition(EOCompletePartLink.class, EOCompletePartLink.ROLE_AOBJECT_REF+".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(part.getMaster())),new int[] {idx_partLink});
					
				}
				
				qs.appendCloseParen();
			}
			
			if(sortValue != null && sortValue.length() > 0) {
				//System.out.println("sortCheck="+sortCheck+"\tsortValue="+sortValue);
				if("true".equals(sortCheck)){
					
					if( !"creator.key.id".equals(sortValue)){
						if(!"PROCESSDATE".equals(sortValue)){
							qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeOrder.class,sortValue), true), new int[] { ecoIdx });
						}
					}else{
						
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EChangeOrder.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , true);
					}
					
				}else{
					
					if( !"creator.key.id".equals(sortValue)){
						if(!"PROCESSDATE".equals(sortValue)){
							qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeOrder.class,sortValue), false), new int[] { ecoIdx });
						}
					}else{
						
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EChangeOrder.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , false);
					}
				}
			}else{
				qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass, EChangeOrder.EO_APPROVE_DATE), true), new int[] { ecoIdx }); 
				qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass, EChangeOrder.CREATE_TIMESTAMP), true), new int[] { ecoIdx });
			}
			 
			PageQueryUtils pager = new PageQueryUtils(params, qs);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EOData data = new EOData((EChangeOrder) obj[0]);
				list.add(data);
			}

			map.put("list", list);
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return map;
	}
	
	public JSONArray getCompletePartList(String oid) throws Exception{
		List<PartData> partList = new ArrayList<PartData>();
		try {
			if (oid.length() > 0) {
				EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
				List<EOCompletePartLink> list= ECOSearchHelper.service.getCompletePartLink(eco);
				//( (이 CHANGE (ECO) 자동으로 생성,승인됨이 아닌것 , 작성자  ) || 관리자 ) && ECO
				String eoType = eco.getEoType();
				String state = eco.getLifeCycleState().toString();
				String userName = eco.getCreator().getName();
				String sessionName = SessionHelper.getPrincipal().getName();
				
				boolean isECO = eoType.equals(ECOKey.ECO_CHANGE);
				boolean isCreate = userName.equals(sessionName);
				boolean isstate = !state.equals("APPROVED") ;
//				boolean isDelete = ((isCreate && isstate) || CommonUtil.isAdmin()) && isECO;
				
				for(EOCompletePartLink link : list){
					
					String version = link.getVersion();
					WTPartMaster master = (WTPartMaster)link.getCompletePart();
					WTPart part = PartHelper.service.getPart(master.getNumber(),version);
					ManagedBaseline baseline = ChangeHelper.service.getEOToPartBaseline(eco.getEoNumber(), part.getNumber());
					boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED") ? true : false; 
					
					PartData data = new PartData(part);
//					data.setDelete(isDelete);
					data.setApproved(isApproved);
					data.setBaselineOid(CommonUtil.getOIDString(baseline));
					partList.add(data);
				}
				//PJT EDIT START START 20161116
//				Collections.sort(partList,new NumberAscCompare());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//PJT EDIT START END
		return JSONArray.fromObject(partList);
	}
	
	public JSONArray getRequestOrderLinkECOData(String oid) throws Exception {
		List<ECOData> dataList = new ArrayList<ECOData>();
		try {
			if(oid.length()>0){
				EChangeRequest ecr = (EChangeRequest)CommonUtil.getObject(oid);
				List<EChangeOrder> ecoList = getRequestOrderLinkECO(ecr);
				
				for(EChangeOrder eco : ecoList){
					ECOData data = new ECOData(eco);
					dataList.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONArray.fromObject(dataList);
	}
	
	public List<EChangeOrder> getRequestOrderLinkECO(EChangeRequest ecr) throws Exception {
		
		List<RequestOrderLink> list = getRequestOrderLink(ecr);
		List<EChangeOrder> ecoList = new ArrayList<EChangeOrder>();
		for(RequestOrderLink link : list){
			EChangeOrder eco = (EChangeOrder)link.getRoleAObject();
			ecoList.add(eco);
		}
		
		return ecoList;
	}
	
	public List<RequestOrderLink> getRequestOrderLink(ECOChange eo) throws WTException {
		List<RequestOrderLink> list = new ArrayList<RequestOrderLink>();
		QueryResult rt = null;
		if(eo instanceof EChangeOrder){
			rt=PersistenceHelper.manager.navigate(eo, "ecr", RequestOrderLink.class,false);
		}else if (eo instanceof EChangeRequest){
			rt=PersistenceHelper.manager.navigate(eo, "eco", RequestOrderLink.class,false);
		}else {
			return list;
		}
		
		while(rt.hasMoreElements()){
			RequestOrderLink link = (RequestOrderLink)rt.nextElement();
			list.add(link);
		}
		return list;
	}
	
	public JSONArray include_ChangeECOView(String oid, String moduleType) throws Exception {
		List<ECOData> list = new ArrayList<ECOData>();
		if (StringUtil.checkString(oid)) {
			if ("part".equals(moduleType)) {
				WTPart part = (WTPart) CommonUtil.getObject(oid);
				List<EChangeOrder> eolist = getPartTOECOList(part);
				for (EChangeOrder eco : eolist) {
					ECOData data = new ECOData(eco);
					list.add(data);
				}
			}
		}
		return JSONArray.fromObject(list);
	}
	
	public List<EChangeOrder> getPartTOECOList(WTPart part) throws Exception {
		List<EChangeOrder> list = new ArrayList<EChangeOrder>();
		QueryResult eolinkQr = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class,true);
		
		while(eolinkQr.hasMoreElements()){
			EChangeOrder eo = (EChangeOrder)eolinkQr.nextElement();
			list.add(eo);
		}
		
		//ECO Type이 CHANGE 제외
		eolinkQr = PersistenceHelper.manager.navigate(part.getMaster(), "eco",EOCompletePartLink.class,true);
		while(eolinkQr.hasMoreElements()){
			EChangeOrder eo = (EChangeOrder)eolinkQr.nextElement();
			if(eo.getEoType().equals(ECOKey.ECO_CHANGE)) continue;
			list.add(eo);
		}
		Collections.sort(list, new EoComparator());
		return list;
	}
}
