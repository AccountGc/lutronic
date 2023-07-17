package com.e3ps.development.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.development.devActive;
import com.e3ps.development.devMaster;
import com.e3ps.development.beans.DevActiveData;
import com.e3ps.development.beans.MasterData;
import com.e3ps.org.People;

import wt.fc.PagingQueryResult;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;

public class DevelopmentHelper {
	public static final DevelopmentService service = ServiceFactory.getService(DevelopmentService.class);
	public static final DevelopmentHelper manager = new DevelopmentHelper();
	
	
	public Map<String, Object> list(Map<String, Object> params) {
		QuerySpec query = null;
		ArrayList<MasterData> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		
		try {
			query = new QuerySpec();
			
			int idx = query.addClassList(devMaster.class, true);
			
			// 프로젝트 코드
			String model = StringUtil.checkNull((String)params.get("model"));
			if(model.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.MODEL, SearchCondition.EQUAL, model), new int[] {idx});
			}
			
			// 프로젝트 명
			String name = StringUtil.checkNull((String)params.get("name"));
			if(name.length() > 0) {
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.NAME, SearchCondition.LIKE, "%"+name+"%"), new int[] {idx});
			}
			
			// 개발 예상 START
			String developmentStart_Start = StringUtil.checkNull((String)params.get("developmentStart_Start"));
			if(developmentStart_Start.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.START_DAY, SearchCondition.GREATER_THAN_OR_EQUAL, developmentStart_Start), new int[] {idx});
			}
			
			String developmentStart_End = StringUtil.checkNull((String)params.get("developmentStart_End"));
			if(developmentStart_End.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.START_DAY, SearchCondition.LESS_THAN_OR_EQUAL, developmentStart_End), new int[] {idx});
			}
			
			// 개발 예상 END
			String developmentEnd_Start = StringUtil.checkNull((String)params.get("developmentEnd_Start"));
			if(developmentEnd_Start.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.END_DAY, SearchCondition.GREATER_THAN_OR_EQUAL, developmentEnd_Start), new int[] {idx});
			}
			
			String developmentEnd_End = StringUtil.checkNull((String)params.get("developmentEnd_End"));
			if(developmentEnd_End.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.END_DAY, SearchCondition.LESS_THAN_OR_EQUAL, developmentEnd_End), new int[] {idx});
			}
			
			// DM
			String dm = StringUtil.checkNull((String)params.get("dm"));
			if(dm.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.DM_REFERENCE + ".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(dm)), new int[] {idx});
			}
			
			// 상태
			String state = StringUtil.checkNull((String)params.get("state"));
			if(state.length() > 0) {
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.LIFE_CYCLE_STATE, SearchCondition.EQUAL, state),new int[] {idx});
			}
			
			// SORT
			String sortValue = StringUtil.checkNull((String)params.get("sortValue"));
			if(sortValue.length() > 0) {
				String sortCheck = StringUtil.checkNull((String)params.get("sortCheck"));
				boolean sort = "true".equals(sortCheck);
				
				if (!"dm".equals(sortValue)) {
					SearchUtil.setOrderBy(query, devMaster.class, idx, sortValue, "sort", sort);
				}else {
					if (query.getConditionCount() > 0)
						query.appendAnd();
					int idx_user = query.appendClassList(WTUser.class, false);
					int idx_people = query.appendClassList(People.class, false);
	
					ClassAttribute ca = null;
					ClassAttribute ca2 = null;
	
					ca = new ClassAttribute(devMaster.class, devMaster.DM_REFERENCE + ".key.id");
					ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
	
					SearchCondition sc2 = new SearchCondition(ca, "=", ca2);
	
					query.appendWhere(sc2, new int[] { idx, idx_user });
	
					ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
	
					query.appendAnd();
					query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
					SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort", sort);
				}
			}else {
				query.appendOrderBy(new OrderBy(new ClassAttribute(devMaster.class, devMaster.MODIFY_TIMESTAMP), true), new int[] { idx }); 
			}
			
			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				devMaster master = (devMaster) obj[0];
				MasterData data = new MasterData(master);
				list.add(data);
			}

			map.put("list", list);
//			map.put("sessionid", pager.getSessionId());
//			map.put("curPage", pager.getCpage());
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	
	public Map<String, Object> my(Map<String, Object> params) {
		QuerySpec query = null;
		Map<String, Object> map = new HashMap<>();
		ArrayList<DevActiveData> list = new ArrayList<>();
		
		try {
			
			WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
			long userOid = CommonUtil.getOIDLongValue(user);
			
			query = new QuerySpec();
			
			int idx_a = query.addClassList(devActive.class, true);
			int idx_m = query.addClassList(devMaster.class, false);
			
			ClassAttribute ca_a = new ClassAttribute(devActive.class, devActive.MASTER_REFERENCE + ".key.id");
			ClassAttribute ca_m = new ClassAttribute(devMaster.class, "thePersistInfo.theObjectIdentifier.id");
			
			query.appendWhere(new SearchCondition(ca_a, SearchCondition.EQUAL, ca_m), new int[] {idx_a, idx_m});

			// 프로젝트 코드
			String model = StringUtil.checkNull((String)params.get("model"));
			if(model.length() > 0) {
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.MODEL, SearchCondition.EQUAL, model), new int[] { idx_m });
			}
			
			// 프로젝트 명
			String name = StringUtil.checkNull((String)params.get("name"));
			if(name.length() > 0) {
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.NAME, SearchCondition.LIKE, "%"+name+"%"), new int[] { idx_m });
			}
			
			// 프로젝트 상태
			String masterState = StringUtil.checkNull((String)params.get("masterState"));
			if(masterState.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.LIFE_CYCLE_STATE, SearchCondition.EQUAL, masterState), new int[] { idx_m });
			}
			
			// Active 명
			String activeName = StringUtil.checkNull((String)params.get("activeName"));
			if(activeName.length() > 0) {
				if(query.getConditionCount() > 0){
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devActive.class, devActive.NAME, SearchCondition.LIKE, "%" + activeName + "%", false), new int[] {idx_a});
			}
			
			// 요청일자
			String activeDateStart = StringUtil.checkNull((String)params.get("activeDateStart"));
			if(activeDateStart.length() > 0){
				if(query.getConditionCount() > 0){
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devActive.class, devActive.ACTIVE_DATE, SearchCondition.GREATER_THAN_OR_EQUAL, activeDateStart), new int[] {idx_a});
			}
			String activeDateEnd = StringUtil.checkNull((String)params.get("activeDateEnd"));
			if(activeDateEnd.length() > 0){
				if(query.getConditionCount() > 0){
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devActive.class, devActive.ACTIVE_DATE, SearchCondition.LESS_THAN_OR_EQUAL, activeDateEnd), new int[] {idx_a});
			}
			
			// 완료일자
			String finishDateStart = StringUtil.checkNull((String)params.get("finishDateStart"));
			if(finishDateStart.length() > 0){
				if(query.getConditionCount() > 0){
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devActive.class, devActive.FINISH_DATE, SearchCondition.GREATER_THAN_OR_EQUAL, DateUtil.convertStartDate(finishDateStart)), new int[] {idx_a});
			}
			String finishDateEnd = StringUtil.checkNull((String)params.get("finishDateEnd"));
			if(finishDateEnd.length() > 0){
				if(query.getConditionCount() > 0){
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devActive.class, devActive.FINISH_DATE, SearchCondition.LESS_THAN_OR_EQUAL, DateUtil.convertStartDate(finishDateEnd)), new int[] {idx_a});
			}
			
			// Active 상태
			String activeState = StringUtil.checkNull((String)params.get("activeState"));
			if(activeState.length() > 0) {
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devActive.class, devActive.LIFE_CYCLE_STATE, SearchCondition.EQUAL, activeState), new int[] {idx_m});
			}
			
			// 접속자
			if(!CommonUtil.isAdmin()) {
				if(query.getConditionCount() > 0){
					query.appendAnd();
				}
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(devActive.class, devActive.DM_REFERENCE + ".key.id", SearchCondition.EQUAL, userOid), new int[]{ idx_a });
				query.appendOr();
				query.appendWhere(new SearchCondition(devActive.class, devActive.WORKER_REFERENCE + ".key.id", SearchCondition.EQUAL, userOid), new int[] { idx_a });
				query.appendCloseParen();
			}
			
			// Sort
			/*
			String sortValue = StringUtil.checkNull(request.getParameter("sortValue"));
			if(sortValue.length() > 0) {
				String sortCheck = StringUtil.checkNull(request.getParameter("sortCheck"));
				boolean sort = "true".equals(sortCheck);
			}else {
				query.appendOrderBy(new OrderBy(new ClassAttribute(devMaster.class, devMaster.MODEL), true), new int[] { idx_m });
				query.appendOrderBy(new OrderBy(new ClassAttribute(devActive.class, devActive.CREATE_TIMESTAMP), true), new int[] { idx_a });
			}
			*/
			query.appendOrderBy(new OrderBy(new ClassAttribute(devActive.class, devActive.MODIFY_TIMESTAMP), true), new int[] { idx_a });
			query.appendOrderBy(new OrderBy(new ClassAttribute(devMaster.class, devMaster.MODEL), true), new int[] { idx_m });
			
			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				devActive dev = (devActive) obj[0];
				DevActiveData data = new DevActiveData(dev);
				list.add(data);
			}

			map.put("list", list);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
}
