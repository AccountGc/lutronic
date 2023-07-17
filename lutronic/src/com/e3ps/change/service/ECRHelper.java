package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeRequest;
import com.e3ps.change.beans.ECRData;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.People;

import wt.fc.PagingQueryResult;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class ECRHelper {
	public static final ECRService service = ServiceFactory.getService(ECRService.class);
	public static final ECRHelper manager = new ECRHelper();
	
	public Map<String, Object> list(Map<String, Object> params) throws Exception{
		Map<String, Object> map = new HashMap<>();
		ArrayList<ECRData> list = new ArrayList<>();
		
		QuerySpec qs = null; 
		
		try{
			String name = StringUtil.checkNull((String)params.get("name"));
			String number = StringUtil.checkNull((String)params.get("number"));
			
			String predate = StringUtil.checkNull((String)params.get("predate"));
			String postdate = StringUtil.checkNull((String)params.get("postdate"));
			String creator = StringUtil.checkNull((String)params.get("creator"));
			String state = StringUtil.checkNull((String)params.get("state"));
			
			//ECR 속성
			String preCreateDate = StringUtil.checkNull((String)params.get("preCreateDate"));
			String postCreateDate = StringUtil.checkNull((String)params.get("postCreateDate"));
			String preApproveDate = StringUtil.checkNull((String)params.get("preApproveDate"));
			String postApproveDate = StringUtil.checkNull((String)params.get("postApproveDate"));
			
			String createDepart = StringUtil.checkNull((String)params.get("createDepart"));
			String writer = StringUtil.checkNull((String)params.get("writer"));
			
			String proposer = StringUtil.checkNull((String)params.get("proposer"));
			
			String[] models = (String[])params.get("model");
			String[] changeSections = (String[])params.get("changeSection");
			
			//정렬
			String sortValue = StringUtil.checkNull((String)params.get("sortValue"));
			String sortCheck = StringUtil.checkNull((String)params.get("sortCheck"));
			
			/*
			if("1".equals(sortValue) ) {
				sortValue = "master>number";
			} else if("2".equals(sortValue)) {
				sortValue = "master>number";
			}
			*/
			qs = new QuerySpec(); 
			Class ecrClass = EChangeRequest.class;
			int ecoIdx = qs.appendClassList(ecrClass, true);
			//제목
			if(name.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.EO_NAME, SearchCondition.LIKE, "%"+name+"%", false), new int[] {ecoIdx});
			}
			//번호
			if(number.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.EO_NUMBER, SearchCondition.LIKE, "%"+number+"%", false), new int[] {ecoIdx});
			}
			
			//등록일
			if(predate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecrClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(predate)), new int[] {ecoIdx});
			}
			
			//등록일
			if(postdate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecrClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postdate)), new int[] {ecoIdx});
			}
			
			//상태
			if(state.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.LIFE_CYCLE_STATE, SearchCondition.EQUAL , state), new int[] {ecoIdx});
			}
			
			//등록자//creator.key.id
			if(creator.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				People pp = (People)CommonUtil.getObject(creator);
				long longOid = CommonUtil.getOIDLongValue(pp.getUser());
				qs.appendWhere(new SearchCondition(ecrClass, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {ecoIdx});
			}
			
			//작성일
			if(preCreateDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.CREATE_DATE, SearchCondition.GREATER_THAN_OR_EQUAL , preCreateDate), new int[] {ecoIdx});
			}
			
		
			if(postCreateDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.CREATE_DATE, SearchCondition.LESS_THAN_OR_EQUAL , postCreateDate), new int[] {ecoIdx});
			}
			
			//승인일
			if(preApproveDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.APPROVE_DATE, SearchCondition.GREATER_THAN_OR_EQUAL , preApproveDate), new int[] {ecoIdx});
			}
			
		
			if(postApproveDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.APPROVE_DATE, SearchCondition.LESS_THAN_OR_EQUAL , postApproveDate), new int[] {ecoIdx});
			}
			
			//작성부서
			if(createDepart.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.CREATE_DEPART, SearchCondition.LIKE , "%"+createDepart+"%", false), new int[] {ecoIdx});
			}
			
			//작성자
			if(writer.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.WRITER, SearchCondition.LIKE , "%"+writer+"%", false), new int[] {ecoIdx});
			}
			
			//제안자
			if(proposer.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.PROPOSER, SearchCondition.LIKE , "%"+proposer+"%", false), new int[] {ecoIdx});
			}
			
			
			//제품명
			if(models != null){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendOpenParen();
					for(int i = 0 ;i < models.length ;i++){
						qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.MODEL, SearchCondition.LIKE, "%"+models[i]+"%", false), new int[] {ecoIdx});
						if(i== models.length-1) break;
						qs.appendOr();
						
					}
				qs.appendCloseParen();
			}
			
			//변경구분
			if(changeSections != null){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendOpenParen();
					for(int i = 0 ;i < changeSections.length ;i++){
						qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.CHANGE_SECTION, SearchCondition.LIKE, "%"+changeSections[i]+"%", false), new int[] {ecoIdx});
						if(i== changeSections.length-1) break;
						qs.appendOr();
						
					}
				qs.appendCloseParen();
			}
			
			
			
			
			
			if(sortValue != null && sortValue.length() > 0) {
				if("true".equals(sortCheck)){
					if( !"creator".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeRequest.class,sortValue), true), new int[] { ecoIdx });
					}else{
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EChangeRequest.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , true);
					}
					
				}else{
					if( !"creator".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeRequest.class,sortValue), false), new int[] { ecoIdx });
					}else{
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EChangeRequest.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , false);
					}
				}
			}else{
				qs.appendOrderBy(new OrderBy(new ClassAttribute(ecrClass, "thePersistInfo.modifyStamp"), true), new int[] { ecoIdx }); 
			}
			PageQueryUtils pager = new PageQueryUtils(params, qs);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ECRData data = new ECRData((EChangeRequest) obj[0]);
				list.add(data);
			}

			map.put("list", list);
			//System.out.println(qs.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return map;
	}
}
