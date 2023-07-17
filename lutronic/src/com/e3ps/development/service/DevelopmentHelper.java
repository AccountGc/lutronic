package com.e3ps.development.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.development.devMaster;
import com.e3ps.development.beans.MasterData;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.org.People;

import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.introspection.WTIntrospectionException;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTPropertyVetoException;

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
}
