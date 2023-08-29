package com.e3ps.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.beans.NumberCodeData;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.history.LoginHistory;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.org.MailUser;
import com.e3ps.org.People;
import com.e3ps.org.service.UserHelper;

import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
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
		ArrayList<EADData> list = new ArrayList<>();
		
		String rootOid = StringUtil.checkNull((String) params.get("rootOid"));
		if(rootOid.length()==0){
			result.put("list", list);
			return result;
		}
		
		long logRootOid = CommonUtil.getOIDLongValue(rootOid);
		List<EChangeActivityDefinition> ecadList = getActiveDefinition(logRootOid);
		for(EChangeActivityDefinition def : ecadList){
			EADData data = new EADData(def);
			list.add(data);
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
	
	/** 
	 * 외부 메일 리스트
	 */
	public Map<String,Object> adminMail(Map<String, Object> params) throws Exception {
		int page = StringUtil.getIntParameter((String) params.get("page"), 1);
		int rows = StringUtil.getIntParameter((String) params.get("rows"), 10);
		int formPage = StringUtil.getIntParameter((String) params.get("formPage"), 15);
		
		String sessionId = (String) params.get("sessionId");
		Hashtable<String,String> hash = null;
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			String command = StringUtil.checkNull((String) params.get("command"));
		    String oid = StringUtil.checkNull((String) params.get("oid"));    
		    String name = StringUtil.checkNull((String) params.get("name"));
		    String email = StringUtil.checkNull((String) params.get("email"));
		    String enable = StringUtil.checkNull((String) params.get("enable"));
		    
			QuerySpec query = new QuerySpec(MailUser.class);
	        if(command.equals("search")){
				if(email.length() > 0){
		        	 if(query.getConditionCount()>0) query.appendAnd();
		        	 query.appendWhere(new SearchCondition(MailUser.class, MailUser.EMAIL, SearchCondition.LIKE, "%" + email+ "%",true), new int[] { 0 });
		        }
		        
		        if(name.length() > 0){
		        	if(query.getConditionCount()>0) query.appendAnd();
		        	query.appendWhere(new SearchCondition(MailUser.class, MailUser.NAME, SearchCondition.EQUAL, "%" + name + "%"), new int[] { 0 });
		        }
	        }
	        
	        if(enable.equals("true")){
	        	if(query.getConditionCount()>0) query.appendAnd();
	        	query.appendWhere(new SearchCondition(MailUser.class, MailUser.IS_DISABLE, SearchCondition.IS_TRUE), new int[] { 0 });
	        }
		    query.appendOrderBy(new OrderBy(new ClassAttribute(MailUser.class,MailUser.NAME),false),new int[]{0});
		    
		    qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
		}
		
		PageControl control = new PageControl(qr, page, formPage, rows);
	    int totalPage   = control.getTotalPage();
	    int startPage   = control.getStartPage();
	    int endPage     = control.getEndPage();
	    int listCount   = control.getTopListCount();
	    int totalCount  = control.getTotalCount();
	    int currentPage = control.getCurrentPage();
	    String param    = control.getParam();
	    int rowCount    = control.getTopListCount();
		
	    Map<String,Object> map = new HashMap<String,Object>();
	    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
	    	MailUser user = (MailUser) o[0];
			
	    	map.put("oid", user.getPersistInfo().getObjectIdentifier().toString());
	    	map.put("name", user.getName());
	    	map.put("email", user.getEmail());
	    	map.put("enable", user.isIsDisable());
	    	list.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		result.put("formPage"       , formPage);
		result.put("rows"           , rows);
		result.put("totalPage"      , totalPage);
		result.put("startPage"      , startPage);
		result.put("endPage"        , endPage);
		result.put("listCount"      , listCount);
		result.put("totalCount"     , totalCount);
		result.put("currentPage"    , currentPage);
		result.put("param"          , param);
		result.put("sessionId"      , qr.getSessionId()==0 ? "" : qr.getSessionId());
		result.put("list"      , list);
		
		return result;
	}
	
	/** 
	 * 접속 이력 리스트
	 */
	public Map<String,Object> loginHistory(Map<String, Object> params) throws Exception {
		long start = System.currentTimeMillis();
		int page = StringUtil.getIntParameter((String) params.get("page"), 1);
		int rows = StringUtil.getIntParameter((String) params.get("rows"), 10);
		int formPage = StringUtil.getIntParameter((String) params.get("formPage"), 15);
		
		String sessionId = (String) params.get("sessionId");
		Hashtable<String,String> hash = null;
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
		    String userName = (String) params.get("userName");
			String userId = (String) params.get("userId");
				
			QuerySpec qs = new QuerySpec();
			
			int idx = qs.appendClassList(LoginHistory.class, true);
		    	
	        if(userName != null && userName.trim().length() > 0) {
	        	if(qs.getConditionCount() > 0)
	        		qs.appendAnd();
	        	qs.appendWhere(new SearchCondition(LoginHistory.class, "name", SearchCondition.LIKE, "%" + userName + "%"), new int[] { idx });
	        }
	        
	        if(userId != null && userId.trim().length() > 0) {
	        	if(qs.getConditionCount()>0)qs.appendAnd();
	        	qs.appendWhere(new SearchCondition(LoginHistory.class, "id", SearchCondition.LIKE, "%" + userId + "%"), new int[] { idx });
	        }
		    
		    qs.appendOrderBy(new OrderBy(new ClassAttribute(LoginHistory.class,"thePersistInfo.createStamp"), true), new int[] { idx }); 
		    long querystart = System.currentTimeMillis();
		    qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, qs, true);
		    long queryend = System.currentTimeMillis();
		    System.out.println("queryResult " + (queryend-querystart));
		}
		
		PageControl control = new PageControl(qr, page, formPage, rows);
	    int totalPage   = control.getTotalPage();
	    int startPage   = control.getStartPage();
	    int endPage     = control.getEndPage();
	    int listCount   = control.getTopListCount();
	    int totalCount  = control.getTotalCount();
	    int currentPage = control.getCurrentPage();
	    String param    = control.getParam();
	    int rowCount    = control.getTopListCount();
	    long pageend = System.currentTimeMillis();
		
	    Map<String,Object> map = new HashMap<String,Object>();
	    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
	    	LoginHistory history = (LoginHistory) o[0];
	    	map.put("oid", history.getPersistInfo().getObjectIdentifier().toString());
			map.put("name", history.getName());
			map.put("id", history.getId());
			map.put("createDate", history.getPersistInfo().getCreateStamp());
	    	list.add(map);
		}
	    
		Map<String,Object> result = new HashMap<String,Object>();
		
		result.put("formPage"       , formPage);
		result.put("rows"           , rows);
		result.put("totalPage"      , totalPage);
		result.put("startPage"      , startPage);
		result.put("endPage"        , endPage);
		result.put("listCount"      , listCount);
		result.put("totalCount"     , totalCount);
		result.put("currentPage"    , currentPage);
		result.put("param"          , param);
		result.put("sessionId"      , qr.getSessionId()==0 ? "" : qr.getSessionId());
		result.put("list"      , list);
		
		return result;
	}
}
