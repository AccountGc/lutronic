package com.e3ps.admin.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTProperties;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.GenNumberHelper;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.history.LoginHistory;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.download.DownloadHistory;
import com.e3ps.download.beans.DownloadData;
import com.e3ps.org.Department;
import com.e3ps.org.MailUser;
import com.e3ps.org.People;
import com.e3ps.org.beans.CompanyState;
import com.e3ps.org.beans.PeopleData;
import com.e3ps.org.service.MailUserHelper;

public class StandardAdminService extends StandardManager implements AdminService {

	public static StandardAdminService newStandardAdminService() throws Exception {
		final StandardAdminService instance = new StandardAdminService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public Map<String,Object> admin_listCompanyAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		Hashtable<String,String> hash = null;
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			
			String name = request.getParameter("name");
			String id = request.getParameter("id");
			
			String departmentOid = request.getParameter("departmentOid");
			ReferenceFactory rf = new ReferenceFactory();
			Department dept = null;

			if(departmentOid!=null && departmentOid.length()>0 && !departmentOid.equals("null") && !departmentOid.equals("root") ){
				dept = (Department)rf.getReference(departmentOid).getObject();
			}
			
			QuerySpec qs = new QuerySpec();
			
			int ii = qs.addClassList(People.class,true);
			int jj = qs.addClassList(WTUser.class,true);
			int kk = qs.addClassList(Department.class,true);

			qs.appendWhere(new SearchCondition(People.class,"departmentReference.key.id",Department.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,kk});
			qs.appendAnd();

		    qs.appendWhere(new SearchCondition(People.class,"userReference.key.id",WTUser.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,jj});	
		    qs.appendAnd();
		    
		    qs.appendWhere(new SearchCondition(WTUser.class,WTUser.DISABLED,SearchCondition.IS_FALSE),new int[] {jj});
		    
			if(dept!=null){
				if (qs.getConditionCount() > 0)
		        	qs.appendAnd();
				qs.appendWhere(new SearchCondition(Department.class,"thePersistInfo.theObjectIdentifier.id","=",dept.getPersistInfo().getObjectIdentifier().getId()),new int[]{kk});
			}

			if (name.length() > 0){
		        if (qs.getConditionCount() > 0)
		        	qs.appendAnd();
		        qs.appendWhere(new SearchCondition(People.class, People.NAME, SearchCondition.LIKE, "%" + name + "%"),
		                         new int[] { ii });
		    }
		    
		    if (id != null && !id.equals("")){
		        if (qs.getConditionCount() > 0)
		        	qs.appendAnd();
		        qs.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.LIKE, "%" + id + "%"),
		                         new int[] { jj });
		    }
		    
		    qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, qs, true);
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
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
		
		while(qr.hasMoreElements()){	
			Object[] o = (Object[])qr.nextElement();
			PeopleData pd = new PeopleData(o);
			
			xmlBuf.append("<row id='" + pd.peopleOID + "'>");
			xmlBuf.append("<cell></cell>");
			xmlBuf.append("<cell>" + (rowCount--) + "</cell>");
			xmlBuf.append("<cell>" + pd.id + "</cell>");
			xmlBuf.append("<cell>" + pd.name + "</cell>");
			xmlBuf.append("<cell>" + pd.departmentName + "</cell>");
			xmlBuf.append("<cell>" + pd.duty + "</cell>");
			xmlBuf.append("<cell>" + pd.email + "</cell>");
			xmlBuf.append("<cell>" + (pd.getChief()== true ? "O" :"X") + "</cell>");
			xmlBuf.append("</row>");
			
		}
		xmlBuf.append("</rows>");
		
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
		result.put("xmlString"      , xmlBuf);
		
		return result;
	}
	
	@Override
	public void admin_actionDepartment(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ReferenceFactory rf = new ReferenceFactory();
		String command = request.getParameter("command");
		
		if("create".equals(command)){
			String cname = request.getParameter("cname");
			String ccode = request.getParameter("ccode");
			String csort = request.getParameter("csort");
			String soid = request.getParameter("soid");
		
			Department dept = Department.newDepartment();
			dept.setName(cname);
			dept.setCode(ccode);
			dept.setSort(Integer.parseInt(csort));
			
		
			if(soid!=null && soid.length()>0 && !"root".equals(soid)){
				
				Department parent = (Department)rf.getReference(soid).getObject();
				dept.setParent(parent);
			}
			
			PersistenceHelper.manager.save(dept);
		}
		
		else if("update".equals(command)){
			String sname = request.getParameter("sname");
			String scode = request.getParameter("scode");
			String ssort = request.getParameter("ssort");
			String soid = request.getParameter("soid");
			String sdept = request.getParameter("sdept");
			
		
			Department dept = (Department)rf.getReference(soid).getObject();
		
			dept.setName(sname);
			dept.setCode(scode);
			dept.setSort(Integer.parseInt(ssort));
		
//			Department parent = (Department)dept.getParent();
		
			if(sdept!=null && !"root".equals(sdept) ){
				QuerySpec qs = new QuerySpec(Department.class);
				qs.appendWhere(new SearchCondition(Department.class,"code","=",sdept),new int[]{0});			

				QueryResult result = PersistenceHelper.manager.find(qs);
				if(result.hasMoreElements()) {
					Department parent = (Department)result.nextElement();
					dept.setParent(parent);
				}
			}
			
			PersistenceHelper.manager.save(dept);
		}
		
		else if("delete".equals(command)){
		
			String soid = request.getParameter("soid");
			Department dept = (Department)rf.getReference(soid).getObject();
			
			QuerySpec qs = new QuerySpec(Department.class);
			int ii = qs.addClassList(Department.class,true);

			qs.appendWhere(new SearchCondition(Department.class,"parentReference.key.id","=",dept.getPersistInfo().getObjectIdentifier().getId()),new int[]{ii});

			QueryResult result = PersistenceHelper.manager.find(qs);
			
			if(result.size() != 0) {
			  	String rtn_msg = "\n <script language=\"javascript\">"
					  + "\n   alert(\"하위 부서가 있는 경우는 삭제할 수 없습니다.\");"
					  + "\n </script>";
			  	//out.println(rtn_msg);
			}else {
				PersistenceHelper.manager.delete(dept);
			}
		}
	}
	
	@Override
	public boolean admin_actionChief(String userOid) throws Exception {
		People pp = (People)CommonUtil.getObject(userOid);
		QueryResult rt = null;
		Department dept = pp.getDepartment();
		boolean chiefRe = true;
		if(dept == null) chiefRe =false;
		
		if(chiefRe){
			QuerySpec qs = new QuerySpec();
			int ii = qs.addClassList(People.class,true);
			int kk = qs.addClassList(Department.class,false);
	
			qs.appendWhere(new SearchCondition(People.class,"departmentReference.key.id",Department.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,kk});
		    
			if(dept!=null){
				if (qs.getConditionCount() > 0)
		        	qs.appendAnd();
				qs.appendWhere(new SearchCondition(Department.class,"thePersistInfo.theObjectIdentifier.id","=",dept.getPersistInfo().getObjectIdentifier().getId()),new int[]{kk});
			}
			
			if (qs.getConditionCount() > 0)
	        	qs.appendAnd();
			qs.appendWhere(new SearchCondition(People.class,"chief",SearchCondition.IS_TRUE));
			//System.out.println(qs);
			rt = PersistenceHelper.manager.find(qs);
			while(rt.hasMoreElements()){
				Object[] oo = (Object[])rt.nextElement();
				People chiefpp = (People)oo[0];
				
				chiefpp.setChief(false);
				PersistenceHelper.manager.save(chiefpp);
				
			}
				
			PersistenceHelper.manager.refresh(pp);
			
			pp.setChief(true);
			PersistenceHelper.manager.save(pp);	
		}
		
		return chiefRe;
	}
	
	@Override
	public Map<String,Object> admin_downLoadHistoryAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long start = System.currentTimeMillis();
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		Hashtable<String,String> hash = null;
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			
			String type = StringUtil.checkNull(request.getParameter("type"));
			String userId = StringUtil.checkNull(request.getParameter("manager"));
			String predate = StringUtil.checkNull(request.getParameter("predate"));
			String postdate = StringUtil.checkNull(request.getParameter("postdate"));
			
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
		
		    qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, qs, true);
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
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
	    long appendstart = System.currentTimeMillis();
		while(qr.hasMoreElements()){	
			Object obj[] = (Object[])qr.nextElement();
			DownloadHistory history = (DownloadHistory)obj[0];
			
			DownloadData data = new DownloadData(history);
			
			xmlBuf.append("<row id='" + history.getPersistInfo().getObjectIdentifier().toString() + "'>");
			xmlBuf.append("<cell><![CDATA[	" + data.getUserName() + " ]]></cell>");
			xmlBuf.append("<cell><![CDATA[	" + data.getUserID() + " ]]></cell>");
			xmlBuf.append("<cell><![CDATA[	" + data.getModuleInfo() + " ]]></cell>");
			xmlBuf.append("<cell><![CDATA[	" + data.getDownCount() + " ]]></cell>");
			xmlBuf.append("<cell><![CDATA[	" + data.getDownTime() + " ]]></cell>");
			xmlBuf.append("<cell><![CDATA[	" + StringUtil.checkReplaceStr(data.getDescribe(), "-") + " ]]></cell>");
			xmlBuf.append("</row>");
			
		}
		xmlBuf.append("</rows>");
//		System.out.println("xmlBuf="+xmlBuf);
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
		result.put("xmlString"      , xmlBuf);
		return result;
	}
	

	@Override
	public Map<String,Object> admin_mailAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		Hashtable<String,String> hash = null;
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			String command = StringUtil.checkNull(request.getParameter("command"));
		    String oid = StringUtil.checkNull(request.getParameter("oid"));    
		    String name = StringUtil.checkNull(request.getParameter("name"));
		    String email = StringUtil.checkNull(request.getParameter("email"));
		    String enable = StringUtil.checkNull(request.getParameter("enable"));
		    
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
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
		
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
	    	MailUser user = (MailUser) o[0];
			
			xmlBuf.append("<row id='" + user.getPersistInfo().getObjectIdentifier().toString() + "'>");
			xmlBuf.append("<cell>" + user.getName() + "</cell>");
			xmlBuf.append("<cell>" + user.getEmail() + "</cell>");
			xmlBuf.append("<cell>" + user.isIsDisable() + "</cell>");
			xmlBuf.append("</row>");
			
		}
		xmlBuf.append("</rows>");
		
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
		result.put("xmlString"      , xmlBuf);
		
		return result;
	}
	
	@Override
	public Map<String,Object> admin_loginHistoryAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long start = System.currentTimeMillis();
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		Hashtable<String,String> hash = null;
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
		    String userName = request.getParameter("userName");
			String userId = request.getParameter("userId");
				
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
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
		
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
	    	LoginHistory history = (LoginHistory) o[0];
			
			xmlBuf.append("<row id='" + history.getPersistInfo().getObjectIdentifier().toString() + "'>");
			xmlBuf.append("<cell>" + history.getName() + "</cell>");
			xmlBuf.append("<cell>" + history.getId() + "</cell>");
			xmlBuf.append("<cell>" + history.getPersistInfo().getCreateStamp() + "</cell>");
			xmlBuf.append("</row>");
			
		}
		xmlBuf.append("</rows>");
	    
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
		result.put("xmlString"      , xmlBuf);
		
		return result;
	}
	
	@Override
	public Map<String,Object> admin_changeActivityAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		Hashtable<String,String> hash = null;
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			
			String type = StringUtil.checkReplaceStr(request.getParameter("type"), "ECO");
				
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(EChangeActivityDefinition.class, true);
		    //query.appendWhere(new SearchCondition(EChangeActivityDefinition.class, EChangeActivityDefinition.EO_TYPE, "=", type), new int[] { idx });
		    query.appendOrderBy(new OrderBy(new ClassAttribute(EChangeActivityDefinition.class,EChangeActivityDefinition.STEP),false),new int[]{idx});
		    query.appendOrderBy(new OrderBy(new ClassAttribute(EChangeActivityDefinition.class,EChangeActivityDefinition.SORT_NUMBER),false),new int[]{idx});
		    
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
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
		
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
	    	EChangeActivityDefinition eca = (EChangeActivityDefinition) o[0];
	    	
	    	String name = eca.getName();
	    	int sort = eca.getSortNumber();
	    	String enname = eca.getName_eng();
	    	String step = eca.getStep();
	        								        
	    	String activeType = eca.getActiveType();
	    	String activeCode = "";//eca.getActiveCode();
	    	String activeGroupName =""; //ca.getActiveGroupName();
	    	String activeGroup =""; //eca.getActiveGroup();
	        boolean isNeed = false; //eca.isIsNeed();
	        boolean isDisabled = false; //eca.isIsDisabled();
	        boolean isApprove = false; //eca.getIsAprove();
	        boolean isDocument = false; //eca.isIsDocument();
	        boolean isDocState = false; //eca.isIsDocState();
	        
	        String description = eca.getDescription();
	    	
			
			xmlBuf.append("<row id='" + eca.getPersistInfo().getObjectIdentifier().toString() + "'>");
			xmlBuf.append("<cell><![CDATA[" + activeGroupName + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + activeGroup + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + name + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + enname + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + description + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + step + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + sort + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + activeType + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + activeCode + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + "<input type=checkbox " +(isNeed ? "checked" : "") + " onclick='return false' >" + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + "<input type=checkbox " +(isDisabled ? "checked" : "") + " onclick='return false' >" + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + "<input type=checkbox " +(isApprove ? "checked" : "") + " onclick='return false' >" + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + "<input type=checkbox " +(isDocument ? "checked" : "") + " onclick='return false' >" + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + "<input type=checkbox " +(isDocState ? "checked" : "") + " onclick='return false' >" + "]]></cell>");
			xmlBuf.append("</row>");
			
		}
		xmlBuf.append("</rows>");
		
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
		result.put("xmlString"      , xmlBuf);
		
		return result;
		
	}
	
	@Override
	public Map<String,Object> admin_numberCodeAction(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			
			String codeType = StringUtil.checkReplaceStr(request.getParameter("codeType"), "ISSUETYPE");
			String parentOid = StringUtil.checkReplaceStr(request.getParameter("parentOid"),"");
			
			QuerySpec query = new QuerySpec(NumberCode.class);
		    query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
		    long longOid =0;
		    if(parentOid!= null && parentOid.length()>0){
		    	longOid = CommonUtil.getOIDLongValue(parentOid);
		    	query.appendAnd();
		    	query.appendWhere(new SearchCondition(NumberCode.class,"parentReference.key.id",SearchCondition.EQUAL,longOid),new int[] {0});
		    }else{
		    	query.appendAnd();
		    	query.appendWhere(new SearchCondition(NumberCode.class,"parentReference.key.id",SearchCondition.EQUAL,longOid),new int[] {0});
		    }
		    
		    query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.SORT),false),new int[]{0});
		    query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE),false),new int[]{0});
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
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
		
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
	    	NumberCode ncode = (NumberCode) o[0];
			
			xmlBuf.append("<row id='" + ncode.getPersistInfo().getObjectIdentifier().toString() + "'>");
			xmlBuf.append("<cell><![CDATA[" + ncode.getName() + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + StringUtil.checkReplaceStr(ncode.getEngName(), "&nbsp;") + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + ncode.getCode() + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + StringUtil.checkReplaceStr(ncode.getSort(), "&nbsp;") + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + StringUtil.checkReplaceStr(ncode.getDescription(), "&nbsp;") + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + !ncode.isDisabled() + "]]></cell>");
			xmlBuf.append("</row>");
			
		}
		xmlBuf.append("</rows>");
		
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
		result.put("xmlString"      , xmlBuf);
		
		return result;
	}
	
	@Override
	public List<Map<String,String>> admin_numberCodeTree(String codeType) throws Exception {
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
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("codeType", codeType);
		map.put("codeTypeName", ctype.getDisplay());
		
		list.add(map);
		
		if(rt.size()>0){
    		while(rt.hasMoreElements()){
    			NumberCode code = (NumberCode)rt.nextElement();
    			
    			map = new HashMap<String,String>();
    			map.put("id", code.getPersistInfo().getObjectIdentifier().toString());
    			map.put("codeType", codeType);
    			map.put("name", code.getName());
    			map.put("code", code.getCode());
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
	
	@Override
	public Map<String,List<Map<String,String>>> admin_getDutyListAction(String duty) throws Exception {
		QuerySpec spec = new QuerySpec();
		int peopleClassPos = spec.addClassList(People.class,true);

	    spec.appendOrderBy(new OrderBy(new ClassAttribute(People.class,People.NAME),false),new int[]{peopleClassPos});
	    
	    QueryResult qr = PersistenceHelper.manager.find(spec);
	    
	    List<Map<String,String>> noDutyUser = new ArrayList<Map<String,String>>();
	    List<Map<String,String>> dutyUser = new ArrayList<Map<String,String>>();
	    
	    while ( qr.hasMoreElements() ) {
	    	Object[] obj = (Object[])qr.nextElement();
	    	People people = (People)obj[0];
	    	
	    	Map<String,String> map = new HashMap<String,String>();
	    	map.put("peopleOid", people.getPersistInfo().getObjectIdentifier().toString());
	    	map.put("peopleName", people.getName());
	    	map.put("peopleId", people.getUser().getName());
	    	map.put("peopleDuty", (people.getDuty()==null)?"지정안됨":people.getDuty());
	    	
	    	if(duty.equals(people.getDutyCode())) {
	    		dutyUser.add(map);
	    	}else {
	    		noDutyUser.add(map);
	    	}
	    }
	    Map<String,List<Map<String,String>>> result = new HashMap<String,List<Map<String,String>>>();
	    result.put("noDutyUser", noDutyUser);
	    result.put("dutyUser", dutyUser);
	    
	    return result;
	}
	
	@Override
	public void admin_setDutyAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String duty = request.getParameter("duty");

		//System.out.println("setDuty..... duty == " + duty);
		
		QuerySpec spec = new QuerySpec();
		Class peopleClass = People.class;
		int peopleClassPos = spec.addClassList(peopleClass,true);
		spec.appendWhere(new SearchCondition(peopleClass,People.DUTY_CODE,SearchCondition.EQUAL,duty), new int[]{peopleClassPos});
		QueryResult result = PersistenceHelper.manager.find(spec);

		Vector dutyUser = new Vector();
		while ( result.hasMoreElements() ) {
			Object[] obj = (Object[])result.nextElement();
			People people = (People)obj[0];
			String peopleOID = PersistenceHelper.getObjectIdentifier ( people ).getStringValue ();
			dutyUser.add(peopleOID);
		}

		ReferenceFactory rf =new ReferenceFactory();
		Vector newdutyUser = new Vector();
		String[] reglist = request.getParameterValues("reglist");

		//Vector dutyInfo = GetDutyInfoFromP2MS.manager.queryDutyInfoFromP2MS();
		Hashtable dutyHash = CompanyState.dutyTable;//(Hashtable)dutyInfo.get(2);
		
		if ( reglist != null && reglist.length > 0 ) {
			for ( int i = 0 ; i < reglist.length ; i++ ) {
				String peopleOID = reglist[i];
				newdutyUser.add(peopleOID);
				People people = (People)rf.getReference(peopleOID).getObject();
				people.setDuty((String)dutyHash.get(duty));
				people.setDutyCode(duty);
				PersistenceHelper.manager.modify(people);
			}
		}
		
		for ( int j = 0 ; j < dutyUser.size() ; j++ ) {
			String peopleOID = (String)dutyUser.get(j);
			if ( !newdutyUser.contains(peopleOID) ) {
				People people = (People)rf.getReference(peopleOID).getObject();
				people.setDuty(null);
				people.setDutyCode(null);
				PersistenceHelper.manager.modify(people);
			}
		}
	}
	
	@Override
	public Map<String,List<Map<String,String>>> admin_getDepartmentListAction(String oid) throws Exception {
		ReferenceFactory rf =new ReferenceFactory();
		//Department dept = (Department)rf.getReference(oid).getObject();

		QuerySpec spec = new QuerySpec();
		Class peopleClass = People.class;
		int peopleClassPos = spec.addClassList(peopleClass,true);
	    spec.appendOrderBy(new OrderBy(new ClassAttribute(People.class,People.NAME),false),new int[]{peopleClassPos});

		QueryResult qr = PersistenceHelper.manager.find(spec);
		
		List<Map<String,String>> noDeptUser = new ArrayList<Map<String,String>>();
	    List<Map<String,String>> deptUser = new ArrayList<Map<String,String>>();
	    
		while ( qr.hasMoreElements() ) {
			Object[] obj = (Object[])qr.nextElement();
	    	People people = (People)obj[0];
	    	
	    	Map<String,String> map = new HashMap<String,String>();
	    	map.put("peopleOid", people.getPersistInfo().getObjectIdentifier().toString());
	    	map.put("peopleName", people.getName());
	    	map.put("peopleId", people.getUser().getName());
	    	map.put("peopleDuty", (people.getDuty()==null)?"지정안됨":people.getDuty());
	    	
	    	String peopleDeptOid = "";
	    	
	    	try{
	    		if(people.getDepartment() != null) {
		    		peopleDeptOid = (people.getDepartment()).getPersistInfo().getObjectIdentifier().toString();
		    	}
	    		
	    	}catch(Exception e){
	    		
	    		people.setDepartment(null);
	    		PersistenceHelper.manager.modify(people);
	    		
	    		
	    		
	    	}
	    	
	    	
	    	
	    	if(oid.equals(peopleDeptOid)) {
	    		deptUser.add(map);
	    	}else {
	    		noDeptUser.add(map);
	    	}
		}
		Map<String,List<Map<String,String>>> result = new HashMap<String,List<Map<String,String>>>();
	    result.put("noDeptUser", noDeptUser);
	    result.put("deptUser", deptUser);
	    
	    return result;

	}
	
	@Override
	public void admin_setDeptAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReferenceFactory rf =new ReferenceFactory();
		String oid = request.getParameter("oid");
		Department dept = (Department)rf.getReference(oid).getObject();

		QuerySpec spec = new QuerySpec();
		Class peopleClass = People.class;
		int peopleClassPos = spec.addClassList(peopleClass,true);
		spec.appendWhere(new SearchCondition(peopleClass,"departmentReference.key",SearchCondition.EQUAL,PersistenceHelper.getObjectIdentifier(dept)),peopleClassPos);
	    spec.appendOrderBy(new OrderBy(new ClassAttribute(People.class,People.DUTY_CODE),false),new int[]{peopleClassPos});
		QueryResult result = PersistenceHelper.manager.find(spec);

		Vector deptUser = new Vector();
		while ( result.hasMoreElements() ) {
			Object[] obj = (Object[])result.nextElement();
			People people = (People)obj[0];
			String peopleOID = PersistenceHelper.getObjectIdentifier ( people ).getStringValue ();
			deptUser.add(peopleOID);
		}

		Vector newDeptUser = new Vector();
		String[] reglist = request.getParameterValues("reglist");
		if ( reglist != null && reglist.length > 0 ) {
			for ( int i = 0 ; i < reglist.length ; i++ ) {
				String peopleOID = reglist[i];
				newDeptUser.add(peopleOID);
				People people = (People)rf.getReference(peopleOID).getObject();
				people.setDepartment(dept);
				PersistenceHelper.manager.modify(people);
			}
		}

		for ( int j = 0 ; j < deptUser.size() ; j++ ) {
			String peopleOID = (String)deptUser.get(j);
			if ( !newDeptUser.contains(peopleOID) ) {
				People people = (People)rf.getReference(peopleOID).getObject();
				people.setDepartment(null);
				PersistenceHelper.manager.modify(people);
			}
		}
	}
	
	@Override
	public void admin_packageAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FileRequest req = new FileRequest(request);
		
		String document = req.getFileLocation("document");
		String part = req.getFileLocation("part");
		String drawing = req.getFileLocation("drawing");
		String rohs = req.getFileLocation("rohs");
		String rohsLink = req.getFileLocation("rohsLink");
		String eco = req.getFileLocation("eco");
		
		String path = WTProperties.getServerProperties().getProperty("wt.home");
		String uploadPath = path + "/loadFiles/" + CommonUtil.getOrgName();
		
		
		if(document != null) {
			File docFile = new File(document);
			CommonUtil.copyFile(docFile, new File(path + "/codebase/jsp/document/" + docFile.getName()));
			docFile.delete();
		}
		
		if(part != null) {
			File partFile = new File(part);
			CommonUtil.copyFile(partFile, new File(path + "/codebase/jsp/part/" + partFile.getName()));
			partFile.delete();
		}
		
		if(rohs != null) {
			File rohsFile = new File(rohs);
			CommonUtil.copyFile(rohsFile, new File(path + "/codebase/jsp/rohs/" + rohsFile.getName()));
			rohsFile.delete();
		}
		
		if(rohsLink != null) {
			File rohsLinkFile = new File(rohsLink);
			CommonUtil.copyFile(rohsLinkFile, new File(path + "/codebase/jsp/rohs/" + rohsLinkFile.getName()));
			rohsLinkFile.delete();
		}
		
		if(eco != null) {
			File ecoFile = new File(eco);
			CommonUtil.copyFile(ecoFile, new File(path + "/codebase/jsp/change/" + ecoFile.getName()));
			ecoFile.delete();
		}
		
		if(drawing != null) {
			File drawingFile = new File(drawing);
			CommonUtil.copyFile(drawingFile, new File(path + "/codebase/jsp/drawing/" + drawingFile.getName()));
			drawingFile.delete();
		}
	}
	
	@Override
	public Map<String, Object> admin_mailNewAction(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
			int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
			int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
			int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
			String sessionId = request.getParameter("sessionId");
			
//			System.out.println("rows="+rows);
//			System.out.println("page="+page);
			Hashtable<String,String> hash = null;
		
			PagingQueryResult qr = null;
			HashMap map = new HashMap();
			String name = StringUtil.checkNull(request.getParameter("named"));
			String command = StringUtil.checkNull(request.getParameter("command"));
			map.put("isDisable","true");
			map.put("name",name);
			map.put("command",command);
			if(StringUtil.checkString(sessionId)) {
				qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
			}else {
				QuerySpec qs = MailUserHelper.service.getQuery(map);
				 qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, qs, true);
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
			
			StringBuffer xmlBuf = new StringBuffer();
		    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		    xmlBuf.append("<rows>");
		    while(qr.hasMoreElements()) {
		    	Object[] objs = (Object[])qr.nextElement();
		    	MailUser user = (MailUser)objs[0];
				
		    	xmlBuf.append("<row id='" + user.getPersistInfo().getObjectIdentifier().toString() + "'>");
		    	xmlBuf.append("<cell><![CDATA[]]></cell>");
		    	xmlBuf.append("<cell><![CDATA[" + user.getName() + "]]></cell>");
		    	xmlBuf.append("<cell><![CDATA[" + user.getEmail() + "]]></cell>");
		    	xmlBuf.append("</row>");
		    }
			xmlBuf.append("</rows>");
			
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
			result.put("xmlString"      , xmlBuf);
			
			return result;
	}

	@Override
	public void numberCodeSave(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addList = (ArrayList<Map<String, Object>>) params.get("addRow");
		ArrayList<Map<String, Object>> editList = (ArrayList<Map<String, Object>>) params.get("editRow");
		ArrayList<Map<String, Object>> removeList = (ArrayList<Map<String, Object>>) params.get("removeRow");
		Transaction trx = new Transaction();
		try{
	    	trx.start();
	    	// 추가
	    	if(addList.size()>0) {
	    		for(Map<String, Object> map : addList) {
    				String name = (String) map.get("name");
    				String engName = (String) map.get("engName");
    				String sort = (String) map.get("sort");
    				String description = (String) map.get("description");
    				boolean enabled = map.get("enabled").equals("true") ? true : false;
    				String codeType = (String) map.get("codeType");
    				String parentOid = StringUtil.checkNull((String) map.get("parentOid"));
    				String code = (String) map.get("code");
    				NumberCodeType ctype = NumberCodeType.toNumberCodeType(codeType);
    				boolean isSeq = ctype.getShortDescription().equals("true") ? true : false;
    				String seqNm = ctype.getLongDescription();
    				
    				String codeNum = "";
    				NumberCode nCode = NumberCode.newNumberCode();
               	 	if(isSeq){
               	 		codeNum=SequenceDao.manager.getNumberCodeSeqNo(codeType, seqNm, "000", "NumberCode", "code");
               	 		codeNum=seqNm+codeNum;
               	 	}else{
               	 		codeNum = code.toUpperCase();
               	 	}
	               	 nCode.setName(name);
	                 nCode.setEngName(engName);
	                 nCode.setCode(codeNum);
	                 nCode.setSort(sort);
	                 nCode.setDescription(description);
	                 nCode.setCodeType(ctype);
//	                 nCode.setDisabled(!"true".equals(enabled));
	                 nCode.setDisabled(enabled);
	                 if(parentOid!= null && parentOid.length()>0){
	                	 NumberCode pCode = (NumberCode)CommonUtil.getObject(parentOid);
	                	 nCode.setParent(pCode);
	                 }
	                 PersistenceHelper.manager.save(nCode);
	    		}
	    	}
    	    
    	    // 수정
	    	if(editList.size()>0) {
    			for(Map<String, Object> map : editList) {
    				String oid = (String) map.get("oid");
    				String name = (String) map.get("name");
    				String engName = (String) map.get("engName");
    				String sort = (String) map.get("sort");
    				String description = (String) map.get("description");
    				boolean enabled = map.get("enabled").equals("true") ? true : false;
    				NumberCode code = (NumberCode) CommonUtil.getObject(oid);
    				code.setName(name);
    				code.setEngName(engName);
    				code.setSort(sort);
    				code.setDescription(description);
    				code.setDisabled(enabled);
//    				code.setDisabled(!"true".equals(enabled));
    				PersistenceHelper.manager.modify(code);
    			}
    		}
	    	
	    	// 삭제
	    	if(removeList.size()>0) {
	    		for(Map<String, Object> map : removeList) {
	    			String oid = (String) map.get("oid");
	    			NumberCode nCode = (NumberCode) CommonUtil.getObject(oid);
    	        	WTPrincipal curUser = SessionHelper.manager.getPrincipal();
    	        	String userName = curUser.getName();
    	        	String log = "user : "+userName+", code : "+nCode.getCode()+", oid : "+oid;
    	        	
    	        	AdminHelper.manager.createLog(log, "CodeDelete");
    	        	
    	        	PersistenceHelper.manager.delete(nCode);
	    		}
	    	}
			
    	    trx.commit();
		    trx = null;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(trx!=null){
				trx.rollback();
			}
		}
	}
	
}
