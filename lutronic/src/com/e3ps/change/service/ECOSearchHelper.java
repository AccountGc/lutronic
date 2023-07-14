package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.EOData;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.People;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class ECOSearchHelper {
	public static final ECOSearchService service = ServiceFactory.getService(ECOSearchService.class);
	public static final ECOSearchHelper manager = new ECOSearchHelper();
	
	public Map<String, Object> listECOAction(HttpServletRequest req) throws Exception{
		Map<String, Object> map = new HashMap<>();
		ArrayList<ECOData> list = new ArrayList<>();
			QuerySpec query = getECOQuery(req);
			
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EChangeOrder eco = (EChangeOrder) obj[0];
				ECOData data = new ECOData(eco);
				list.add(data);
			}

			map.put("list", list);
			 
			return map;
	}
	
	public Map<String, Object> listEOAction(HttpServletRequest req, HttpServletResponse res) throws Exception{
		Map<String, Object> map = new HashMap<>();
		ArrayList<EOData> list = new ArrayList<>();
		QuerySpec query = getECOQuery(req);
		
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EChangeOrder eco = (EChangeOrder) obj[0];
			EOData data = new EOData(eco);
			list.add(data);
		}
		
		map.put("list", list);
		
		return map;
	}
	
	public QuerySpec getECOQuery(HttpServletRequest req) throws Exception{
		QuerySpec qs = null; 
		try{
			String name = StringUtil.checkNull(req.getParameter("name"));
			String number = StringUtil.checkNull(req.getParameter("number"));
			String eoType = StringUtil.checkNull(req.getParameter("eoType"));
			
			String predate = StringUtil.checkNull(req.getParameter("predate"));
			String postdate = StringUtil.checkNull(req.getParameter("postdate"));
			
			String creator = StringUtil.checkNull(req.getParameter("creator"));
			String state = StringUtil.checkNull(req.getParameter("state"));
			
			String licensing = StringUtil.checkNull(req.getParameter("licensing"));
			
			String[] models = req.getParameterValues("model");
			
			String sortCheck =StringUtil.checkNull(req.getParameter("sortCheck"));
			String sortValue =StringUtil.checkNull(req.getParameter("sortValue"));
			
			String riskType = StringUtil.checkNull(req.getParameter("riskType"));
			String preApproveDate = StringUtil.checkNull(req.getParameter("preApproveDate"));
			String postApproveDate = StringUtil.checkNull(req.getParameter("postApproveDate"));
			
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
			if(models != null){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendOpenParen();
					for(int i = 0 ;i < models.length ;i++){
						qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.MODEL, SearchCondition.LIKE, "%"+models[i]+"%", false), new int[] {ecoIdx});
						if(i== models.length-1) break;
						qs.appendOr();
						
					}
				qs.appendCloseParen();
			}
			
			String[] completeParts = req.getParameterValues("completeParts");
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
			 
			//System.out.println(qs);
		}catch(Exception e){
			e.printStackTrace();
		}
	
	return qs;
	}
}
