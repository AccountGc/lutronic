package com.e3ps.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.beans.NumberCodeData;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.People;
import com.e3ps.org.service.UserHelper;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class AdminHelper {
	public static final AdminService service = ServiceFactory.getService(AdminService.class);
	public static final AdminHelper manager = new AdminHelper();
	
	/** 
	 * 코드체계관리 리스트
	 */
	public Map<String, Object> numberCodeTree(String codeType) throws Exception {
		ArrayList<NumberCodeData> list = new ArrayList<>();
		Map<String,Object> map = new HashMap<String,Object>();
		QueryResult rt = null;
		QuerySpec query = new QuerySpec(NumberCode.class);
		query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
		query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class,NumberCode.CODE),false),new int[]{0});
		rt = PersistenceHelper.manager.find(query);
		
		while(rt.hasMoreElements()){
			NumberCode code = (NumberCode)rt.nextElement();
			NumberCodeData data = new NumberCodeData(code);
			list.add(data);
		}
		map.put("treeList", list);
			
		return map;
	}
	
	/** 
	 * 설계 변경 관리 리스트
	 */
	public Map<String,Object> changeActivityList(Map<String, Object> params) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		String rootOid = StringUtil.checkNull((String) params.get("rootOid"));
		if(rootOid.length()==0){
			return result;
		}
		
		long logRootOid = CommonUtil.getOIDLongValue(rootOid);
		List<EChangeActivityDefinition> ecadList = getActiveDefinition(logRootOid);
		for(EChangeActivityDefinition def : ecadList){
			Map<String,Object> map = new HashMap<String,Object>();
			String oid = CommonUtil.getOIDString(def);
			String activeUser = "";
			String activeUserDepart = "";
			if(def.getActiveUser() != null){
				People pp =UserHelper.service.getPeople(def.getActiveUser());
				activeUser = def.getActiveUser().getFullName();
				if(pp != null && pp.getDepartment() != null){
					activeUserDepart = pp.getDepartment().getName();
				}
			}
			NumberCode code = NumberCodeHelper.service.getNumberCode("EOSTEP", def.getStep());
			String codeName ="";
			if(code != null){
				codeName = code.getName();
			}
			map.put("oid", oid);
			map.put("codeName", codeName);
			map.put("name", def.getName());
			map.put("type", ChangeUtil.getActivityName(def.getActiveType()));
			map.put("userDepart", activeUserDepart);
			map.put("user", activeUser);
			map.put("sort", def.getSortNumber());
			
			list.add(map);
		}
		
		result.put("list",list);
		
		return result;
	}
	
	/** 
	 * Root별 활동 리스트
	 */
	public List<EChangeActivityDefinition> getActiveDefinition(long rootOid) throws Exception{
		List<EChangeActivityDefinition> list= new ArrayList<EChangeActivityDefinition>();
		
        ClassAttribute classattribute1 = null;
        ClassAttribute classattribute2 = null;
        SearchCondition sc = null;
		QuerySpec qs = new QuerySpec();
		Class cls1 = EChangeActivityDefinition.class;
		Class cls2 = NumberCode.class;
		
		int idx1 = qs.addClassList(EChangeActivityDefinition.class, true);
		int idx2 = qs.addClassList(NumberCode.class, false);
		
		//Join 
		classattribute1 = new ClassAttribute(cls1,"step" );
	    classattribute2= new ClassAttribute(cls2, "code");
		sc = new SearchCondition(classattribute1, "=", classattribute2);
		sc.setFromIndicies(new int[] {idx1, idx2}, 0);
        sc.setOuterJoin(0);
        qs.appendWhere(sc, new int[] {idx1, idx2});
        
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class,"rootReference.key.id","=",rootOid),new int[]{idx1});
		
		
		qs.appendOrderBy(new OrderBy(new ClassAttribute(
				cls2, "sort"), false),
				new int[] { idx2 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(
				EChangeActivityDefinition.class, "sortNumber"), false),
				new int[] { idx1 });
		//System.out.println(qs.toString());
		QueryResult result = PersistenceHelper.manager.find(qs);

		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			list.add((EChangeActivityDefinition) o[0]);
		}

		return list;
	}
}
