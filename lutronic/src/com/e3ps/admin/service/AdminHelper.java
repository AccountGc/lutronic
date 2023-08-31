package com.e3ps.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.beans.EADData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.beans.NumberCodeData;
import com.e3ps.common.history.LoginHistory;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.download.DownloadHistory;
import com.e3ps.download.beans.DownloadData;
import com.e3ps.org.MailUser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
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
	public JSONArray numberCodeTree(Map<String, Object> params) throws Exception {
		JSONArray list = new JSONArray();
		String codeType = (String) params.get("codeType");
		ArrayList<NumberCode> codeList = numberCodeList(codeType);
		for(NumberCode code : codeList) {
			NumberCodeData data = new NumberCodeData(code);
			JSONObject rootNode = new JSONObject();
			rootNode.put("oid", data.getOid());
			rootNode.put("name", data.getName());
			rootNode.put("engName", data.getEngName());
			rootNode.put("code", data.getCode());
			rootNode.put("sort", data.getSort());
			
			QuerySpec query = new QuerySpec(NumberCode.class);
			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class,"parentReference.key.id",SearchCondition.EQUAL, code.getPersistInfo().getObjectIdentifier().getId()),new int[] {0});
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class,NumberCode.CODE),false),new int[]{0});
			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			JSONArray children = new JSONArray();
			while(result.hasMoreElements()){
				Object[] obj = (Object[]) result.nextElement();
				NumberCode childrenCode = (NumberCode)obj[0];
				NumberCodeData childrenData = new NumberCodeData(childrenCode);
				JSONObject node = new JSONObject();
				node.put("oid", childrenData.getOid());
				node.put("name", childrenData.getName());
				node.put("engName", childrenData.getEngName());
				node.put("code", childrenData.getCode());
				node.put("sort", childrenData.getSort());
				loadTree(childrenCode, node, codeType);
				children.add(node);
			}
			rootNode.put("children", children);
			rootNode.put("list", list);
			rootNode.put("topListCount", pager.getTotal());
			rootNode.put("pageSize", pager.getPsize());
			rootNode.put("total", pager.getTotalSize());
			rootNode.put("sessionid", pager.getSessionId());
			rootNode.put("curPage", pager.getCpage());
			list.add(rootNode);
		}
			
		return list;
	}
	
	/** 
	 * 코드 소메뉴 리스트
	 */
	public ArrayList<NumberCode> numberCodeList(String codeType) throws Exception {
		ArrayList<NumberCode> list = new ArrayList<NumberCode>();
		QuerySpec query = new QuerySpec(NumberCode.class);
		query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
		long longOid =0;
		query.appendAnd();
		query.appendWhere(new SearchCondition(NumberCode.class,"parentReference.key.id",SearchCondition.EQUAL, longOid),new int[] {0});
		query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class,NumberCode.CODE),false),new int[]{0});
		QueryResult rt = PersistenceHelper.manager.find(query);
		while(rt.hasMoreElements()){
			NumberCode code = (NumberCode)rt.nextElement();
			list.add(code);
		}
		return list;
	}
	
	/** 
	 * 코드체계관리 리스트 재귀함수
	 */
	public void loadTree(NumberCode parent, JSONObject parentNode, String codeType) throws Exception {
		JSONArray children = new JSONArray();
		QuerySpec query = new QuerySpec(NumberCode.class);
		query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
		query.appendAnd();
		query.appendWhere(new SearchCondition(NumberCode.class,"parentReference.key.id",SearchCondition.EQUAL, parent.getPersistInfo().getObjectIdentifier().getId()),new int[] {0});
		query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class,NumberCode.CODE),false),new int[]{0});
		QueryResult rt = PersistenceHelper.manager.find(query);
		while(rt.hasMoreElements()){
			NumberCode childcode = (NumberCode)rt.nextElement();
			NumberCodeData childdata = new NumberCodeData(childcode);
			JSONObject node = new JSONObject();
			node.put("oid", childdata.getOid());
			node.put("name", childdata.getName());
			node.put("engName", childdata.getEngName());
			node.put("code", childdata.getCode());
			node.put("sort", childdata.getSort());
			loadTree(childcode, node, codeType);
			children.add(node);
		}
		parentNode.put("children", children);
	}
	
	/** 
	 * 설계 변경 관리 리스트
	 */
	public Map<String,Object> changeActivityList(Map<String, Object> params) throws Exception {
		ArrayList<EADData> list = new ArrayList<>();
		
		String rootOid = StringUtil.checkNull((String) params.get("rootOid"));
		if(rootOid.length()==0){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("list", list);
			map.put("pageSize", 30);
			map.put("total", 0);
			map.put("curPage", 1);
			map.put("topListCount", 0);
			return map;
		}
		long logRootOid = CommonUtil.getOIDLongValue(rootOid);
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
		qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class,"rootReference.key.id","=",logRootOid),new int[]{idx1});
		
		qs.appendOrderBy(new OrderBy(new ClassAttribute(cls2, "sort"), false),new int[] { idx2 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeActivityDefinition.class, "sortNumber"), false),new int[] { idx1 });
		//System.out.println(qs.toString());
		PageQueryUtils pager = new PageQueryUtils(params, qs);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EADData data = new EADData((EChangeActivityDefinition) o[0]);
			list.add(data);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
	
	/** 
	 * 외부 메일 리스트
	 */
	public Map<String,Object> adminMail(Map<String, Object> params) throws Exception {
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
	    
	    PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		
	    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		while(result.hasMoreElements()){	
			Object[] o = (Object[]) result.nextElement();
	    	MailUser user = (MailUser) o[0];
			
	    	Map<String,Object> map = new HashMap<String,Object>();
	    	map.put("oid", user.getPersistInfo().getObjectIdentifier().toString());
	    	map.put("name", user.getName());
	    	map.put("email", user.getEmail());
	    	map.put("enable", user.isIsDisable());
	    	list.add(map);
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
	
	/** 
	 * 접속 이력 리스트
	 */
	public Map<String,Object> loginHistory(Map<String, Object> params) throws Exception {
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
	    
	    PageQueryUtils pager = new PageQueryUtils(params, qs);
		PagingQueryResult result = pager.find();
	    
//	    long querystart = System.currentTimeMillis();
//	    qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, qs, true);
//	    long queryend = System.currentTimeMillis();
//	    System.out.println("queryResult " + (queryend-querystart));
		
	    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		while(result.hasMoreElements()){	
			Object[] o = (Object[]) result.nextElement();
	    	LoginHistory history = (LoginHistory) o[0];
	    	Map<String,Object> map = new HashMap<String,Object>();
	    	map.put("oid", history.getPersistInfo().getObjectIdentifier().toString());
			map.put("name", history.getName());
			map.put("id", history.getId());
			map.put("createDate", DateUtil.getDateString(history.getPersistInfo().getCreateStamp(),"a"));
	    	list.add(map);
		}
	    
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
	
	/** 
	 * 다운로드 이력 리스트
	 */
	public Map<String,Object> downLoadHistory(Map<String,Object> params) throws Exception {
		String type = StringUtil.checkNull((String) params.get("type"));
		String userId = StringUtil.checkNull((String) params.get("manager"));
		String predate = StringUtil.checkNull((String) params.get("createdFrom"));
		String postdate = StringUtil.checkNull((String) params.get("createdTo"));
		
		QuerySpec qs = new QuerySpec();
		
		int idx = qs.appendClassList(DownloadHistory.class, true);
		
		if(type != null && type.trim().length() > 0 ) {
			if (qs.getConditionCount() > 0) qs.appendAnd();
			qs.appendWhere(new SearchCondition(DownloadHistory.class, DownloadHistory.D_OID, SearchCondition.LIKE , "%"+type+"%"), new int[] { idx });
		}
		
		if( userId.length() > 0 ){
			WTUser user = (WTUser)CommonUtil.getObject(userId);
			if (qs.getConditionCount() > 0) qs.appendAnd();
			qs.appendWhere(new SearchCondition(DownloadHistory.class, "userReference.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(user)), new int[] { idx });
		}
		
		//등록일
    	if(predate.length() > 0){
    		if(qs.getConditionCount() > 0) { qs.appendAnd(); }
    		qs.appendWhere(new SearchCondition(DownloadHistory.class, "thePersistInfo.createStamp" ,SearchCondition.GREATER_THAN,DateUtil.convertStartDate(predate)), new int[]{idx});
    	}
    	
    	if(postdate.length() > 0){
    		if(qs.getConditionCount() > 0)qs.appendAnd();
    		qs.appendWhere(new SearchCondition(DownloadHistory.class, "thePersistInfo.createStamp",SearchCondition.LESS_THAN,DateUtil.convertEndDate(postdate)), new int[]{idx});
    	}
		qs.appendOrderBy(new OrderBy(new ClassAttribute(DownloadHistory.class, "thePersistInfo.createStamp"), true), new int[] { idx });  
		
		PageQueryUtils pager = new PageQueryUtils(params, qs);
		PagingQueryResult result = pager.find();
		
	    ArrayList<DownloadData> list = new ArrayList<DownloadData>();
		while(result.hasMoreElements()){	
			Object obj[] = (Object[])result.nextElement();
			DownloadHistory history = (DownloadHistory)obj[0];
			DownloadData data = new DownloadData(history);
			list.add(data);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
