package com.e3ps.development.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.introspection.WTIntrospectionException;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTPropertyVetoException;

import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.development.devActive;
import com.e3ps.development.devMaster;
import com.e3ps.development.devOutPutLink;
import com.e3ps.development.devTask;
import com.e3ps.development.dto.DevActiveData;
import com.e3ps.org.People;

public class StandardDevelopmentQueryService extends StandardManager implements DevelopmentQueryService {

	public static StandardDevelopmentQueryService newStandardDevelopmentQueryService() throws Exception {
		final StandardDevelopmentQueryService instance = new StandardDevelopmentQueryService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public QuerySpec listDevelopmentSearchQuery(HttpServletRequest request, HttpServletResponse response) {
		QuerySpec query = null;
		
		try {
			query = new QuerySpec();
			
			int idx = query.addClassList(devMaster.class, true);
			
			// 프로젝트 코드
			String model = StringUtil.checkNull(request.getParameter("model"));
			if(model.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.MODEL, SearchCondition.EQUAL, model), new int[] {idx});
			}
			
			// 프로젝트 명
			String name = StringUtil.checkNull(request.getParameter("name"));
			if(name.length() > 0) {
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.NAME, SearchCondition.LIKE, "%"+name+"%"), new int[] {idx});
			}
			
			// 개발 예상 START
			String developmentStart_Start = StringUtil.checkNull(request.getParameter("developmentStart_Start"));
			if(developmentStart_Start.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.START_DAY, SearchCondition.GREATER_THAN_OR_EQUAL, developmentStart_Start), new int[] {idx});
			}
			
			String developmentStart_End = StringUtil.checkNull(request.getParameter("developmentStart_End"));
			if(developmentStart_End.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.START_DAY, SearchCondition.LESS_THAN_OR_EQUAL, developmentStart_End), new int[] {idx});
			}
			
			// 개발 예상 END
			String developmentEnd_Start = StringUtil.checkNull(request.getParameter("developmentEnd_Start"));
			if(developmentEnd_Start.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.END_DAY, SearchCondition.GREATER_THAN_OR_EQUAL, developmentEnd_Start), new int[] {idx});
			}
			
			String developmentEnd_End = StringUtil.checkNull(request.getParameter("developmentEnd_End"));
			if(developmentEnd_End.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.END_DAY, SearchCondition.LESS_THAN_OR_EQUAL, developmentEnd_End), new int[] {idx});
			}
			
			// DM
			String dm = StringUtil.checkNull(request.getParameter("dm"));
			if(dm.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.DM_REFERENCE + ".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(dm)), new int[] {idx});
			}
			
			// 상태
			String state = StringUtil.checkNull(request.getParameter("state"));
			if(state.length() > 0) {
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(devMaster.class, devMaster.LIFE_CYCLE_STATE, SearchCondition.EQUAL, state),new int[] {idx});
			}
			
			// SORT
			String sortValue = StringUtil.checkNull(request.getParameter("sortValue"));
			if(sortValue.length() > 0) {
				String sortCheck = StringUtil.checkNull(request.getParameter("sortCheck"));
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
			
		} catch(QueryException e){
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTIntrospectionException e) {
			e.printStackTrace();
		}
		return query;
	}
	
	@Override
	public QuerySpec getTaskListFormMasterOid(String oid) {
		
		QuerySpec query = null;
		
		try {
			query = new QuerySpec();
			
			int idx = query.addClassList(devTask.class, true);
			
			query.appendWhere(new SearchCondition(devTask.class, devTask.MASTER_REFERENCE + ".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(oid)), new int[] {idx});
			query.appendOrderBy(new OrderBy(new ClassAttribute(devTask.class, devTask.CREATE_TIMESTAMP), false), new int[] { idx }); 
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		return query;
	}
	
	@Override
	public QuerySpec getActiveListFromTaskOid(String oid){
		QuerySpec query = null;
		
		try {
			query = new QuerySpec();
			
			int idx = query.addClassList(devActive.class, true);
			
			query.appendWhere(new SearchCondition(devActive.class, devActive.TASK_REFERENCE + ".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(oid)), new int[] {idx});
			query.appendOrderBy(new OrderBy(new ClassAttribute(devActive.class, devActive.CREATE_TIMESTAMP), false), new int[] { idx }); 
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		return query;
	}
	
	@Override
	public QuerySpec listMyDevelopmentSearchQuery(Map<String, Object> params) {
		QuerySpec query = null;
		
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
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return query;
	}
	
	@Override
	public QuerySpec getDevelopmentUsers(String oid) {
		QuerySpec spec = null;
		
		try {
			spec = new QuerySpec();
			
			int idx = spec.addClassList(devActive.class, false);
			spec.setAdvancedQueryEnabled(true);
			
			spec.appendWhere(new SearchCondition(devActive.class, devActive.MASTER_REFERENCE + ".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(oid)), new int[] {idx});

			spec.appendGroupBy(new ClassAttribute(devActive.class, devActive.WORKER_REFERENCE + ".key.id"), idx, true);
			
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return spec;
	}
	
	@Override
	public boolean isTaskDelete(devTask task) {
		return isTaskDelete(CommonUtil.getOIDString(task));
	}
	
	@Override
	public boolean isTaskDelete(String oid) {
		
		QuerySpec spec = null;
		
		try {
			spec = new QuerySpec();
			
			int idx = spec.addClassList(devActive.class, false);
			spec.setAdvancedQueryEnabled(true);
			
			spec.appendWhere(new SearchCondition(devActive.class, devActive.TASK_REFERENCE + ".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(oid)), new int[] {idx});
			
			spec.appendGroupBy(new ClassAttribute(devActive.class, devActive.TASK_REFERENCE + ".key.id"), idx, true);
			
			QueryResult result = PersistenceServerHelper.manager.query(spec);
			
			return result.hasMoreElements();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean isActiveDelete(devActive active) {
		return isActiveDelete(CommonUtil.getOIDString(active));
	}
	
	@Override
	public boolean isActiveDelete(String oid) {
		
		QuerySpec spec = null;
		
		try {
			spec = new QuerySpec();
			
			int idx = spec.addClassList(devOutPutLink.class, false);
			spec.setAdvancedQueryEnabled(true);
			
			spec.appendWhere(new SearchCondition(devOutPutLink.class, devOutPutLink.ROLE_AOBJECT_REF + ".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(oid)), new int[] {idx});
			
			spec.appendGroupBy(new ClassAttribute(devOutPutLink.class, devOutPutLink.ROLE_AOBJECT_REF + ".key.id"), idx, true);
			
			QueryResult result = PersistenceServerHelper.manager.query(spec);
			
			return result.hasMoreElements();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public List<DevActiveData> getActiveDataFromRevisionControlled(RevisionControlled rev) throws Exception {
		
		List<DevActiveData> list = new ArrayList<DevActiveData>();
		
		QueryResult qr = PersistenceHelper.manager.navigate(rev, "act", devOutPutLink.class, false);
		
		while(qr.hasMoreElements()) {
			devOutPutLink link = (devOutPutLink)qr.nextElement();
			devActive active = link.getAct();
			DevActiveData data = new DevActiveData(active);
			
			list.add(data);
		}
		
		return list;
	}
	
	@Override
	public List<devOutPutLink> getDevOutPutLinkFromRevisionControlled(RevisionControlled rev) throws Exception {
		
		List<devOutPutLink> list = new ArrayList<devOutPutLink>();
		
		QueryResult qr = PersistenceHelper.manager.navigate(rev, "act", devOutPutLink.class, false);
		
		while(qr.hasMoreElements()) {
			devOutPutLink link = (devOutPutLink)qr.nextElement();
			list.add(link);
		}
		
		return list;
	}
	
	
	
	
	
	
	
	
	
	
}
