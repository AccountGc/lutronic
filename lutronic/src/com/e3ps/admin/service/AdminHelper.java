package com.e3ps.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;

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
	
	public List<Map<String, Object>> numberCodeTree(String codeType) throws Exception {
		NumberCodeType ctype = NumberCodeType.toNumberCodeType(codeType);
//		System.out.println("admin_numberCodeTree codeType = "+codeType);
		//1Level
		QueryResult rt = null;
		QuerySpec query = new QuerySpec(NumberCode.class);
		query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
		//query.appendAnd();
		//query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE), new int[] { 0 });
		//query.appendAnd();
		//query.appendWhere(new SearchCondition(NumberCode.class,"parentReference.key.id",SearchCondition.EQUAL,(long)0),new int[] { 0 });
		query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class,NumberCode.CODE),false),new int[]{0});
		//System.out.println(query);
		rt = PersistenceHelper.manager.find(query);
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(rt.size()>0){
    		while(rt.hasMoreElements()){
    			NumberCode code = (NumberCode)rt.nextElement();
    			
    			map = new HashMap<String,Object>();
    			map.put("id", code.getPersistInfo().getObjectIdentifier().toString());
    			map.put("codeType", codeType);
    			map.put("treeName", code.getName());
    			map.put("treeCode", code.getCode());
    			if(code.getParent() == null) {
    				map.put("isParent", "false");
    			}else {
    				map.put("isParent", "true");
    				map.put("parent", code.getParent().getPersistInfo().getObjectIdentifier().toString());
    			}
    			list.add(map);
    		}
		}
		
		return list;
	}
}
