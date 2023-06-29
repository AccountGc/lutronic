package com.e3ps.org.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.groupware.workprocess.ApprovalLineTemplate;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;
import com.e3ps.org.beans.PeopleData;

@SuppressWarnings("serial")
public class StandardUserService extends StandardManager implements UserService {

	public static boolean nameChange = false;

	public static StandardUserService newStandardUserService() throws WTException {
		final StandardUserService instance = new StandardUserService();
		instance.initialize();
		return instance;
	}

	@Override
	public void setAllUserName() throws Exception {

		if (!nameChange) {
			QuerySpec query = new QuerySpec();
			QueryResult qr = null;
			int idx = query.addClassList(WTUser.class, true);
			SearchCondition sc = new SearchCondition(WTUser.class, "first", "<>", "");
			query.appendWhere(sc, new int[] { idx });
			qr = PersistenceHelper.manager.find(query);
			String fullName = "";

			while (qr.hasMoreElements()) {
				Object[] o = (Object[]) qr.nextElement();
				WTUser wtuser = (WTUser) o[0];
				fullName = wtuser.getFullName();
				int i = fullName.indexOf(", ");
				if (i > 0) {
					fullName = fullName.substring(0, i) + fullName.substring(i + 2);
					wtuser.setFullName(fullName);
					PersistenceServerHelper.manager.update(wtuser);
				}
			}
			nameChange = true;
			//System.out.println("�쑀�� 蹂�寃쎌셿猷�!!!");
		}
	}

	@Override
	public boolean isAdmin() throws Exception {
		return isMember("Administrators");
	}

	@Override
	public boolean isMember(String group) throws Exception {
		WTUser user = (wt.org.WTUser) SessionHelper.manager.getPrincipal();
		Enumeration en = user.parentGroupNames();
		while (en.hasMoreElements()) {
			String st = (String) en.nextElement();
			if (st.equals(group))
				return true;
		}
		return false;
	}

	@Override
	public void syncSave(WTUser _user) {
		//System.out.println("syncSave");
		try {
			if (_user.isDisabled()) {
				syncDelete(_user);
				return;
			}

			// Windchill 10 �씠�썑 �씠由� 蹂�寃�
			String fullName = _user.getFullName();
			int i = fullName.indexOf(", ");
			if (i > 0) {
				fullName = fullName.substring(0, i) + fullName.substring(i + 2);
				_user.setFullName(fullName);
				PersistenceServerHelper.manager.update(_user);
			}
			//

			People people = getPeople(_user);

			if (people == null) {
				people = People.newPeople();
				people.setNameEn(_user.getFullName());
			}

			people.setUser(_user);
			people.setName(_user.getFullName());

			PersistenceHelper.manager.save(people);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void syncDelete(WTUser _user) {
		//System.out.println("syncDelete");
		try {
			QueryResult qr = PersistenceHelper.manager.navigate(_user, "people", WTUserPeopleLink.class);
			if (qr.hasMoreElements()) {
				People people = (People) qr.nextElement();
				people.setIsDisable(true);
				PersistenceHelper.manager.modify(people);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void syncWTUser() {
		try {
			QuerySpec query = new QuerySpec(WTUser.class);
			QueryResult result = PersistenceHelper.manager.find(query);
			WTUser wtuser = null;
			while (result.hasMoreElements()) {
				wtuser = (WTUser) result.nextElement();
				if (!wtuser.isDisabled())
					syncSave(wtuser);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public People getPeople(WTUser user) {
		return getPeople(user.getPersistInfo().getObjectIdentifier().getId());
	}

	@Override
	public People getPeople(long userid) {
		try {
			QuerySpec spec = new QuerySpec();
			Class mainClass = People.class;
			int mainClassPos = spec.addClassList(mainClass, true);
			spec.appendWhere(new SearchCondition(mainClass, "userReference.key.id", "=", userid), new int[] { mainClassPos });
			QueryResult qr = PersistenceHelper.manager.find(spec);
			if (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				return (People) obj[0];
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public WTUser getUser(String id) {
		try {
			QuerySpec spec = new QuerySpec();
			Class mainClass = WTUser.class;
			int mainClassPos = spec.addClassList(mainClass, true);
			spec.appendWhere(new SearchCondition(mainClass, "name", "=", id), new int[] { mainClassPos });
			QueryResult qr = PersistenceHelper.manager.find(spec);
			if (qr.hasMoreElements()) {
				Object obj[] = (Object[]) qr.nextElement();
				return (WTUser) obj[0];
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String[] getUserInfo(WTUser user) throws Exception {

		People people = getPeople(user);
		Department dept1 = people.getDepartment();

		String[] infos = new String[4];

		String useroid = StringUtil.checkNull(PersistenceHelper.getObjectIdentifier(user).getStringValue());
		String peopleoid = StringUtil.checkNull(PersistenceHelper.getObjectIdentifier(people).getStringValue());

		String deptoid = "";
		if (dept1 != null) {
			deptoid = StringUtil.checkNull(PersistenceHelper.getObjectIdentifier(dept1).getStringValue());
		}

		String id = StringUtil.checkNull(user.getName());
		String name = StringUtil.checkNull(user.getFullName());

		String departmentname = dept1 == null ? "" : StringUtil.checkNull(dept1.getName());
		String duty = StringUtil.checkNull(people.getDuty());
		String dutycode = StringUtil.checkNull(people.getDutyCode());
		String email = StringUtil.checkNull(user.getEMail());
		String temp = StringUtil.checkNull(people.getName());

		String values = useroid + "," + peopleoid + "," + deptoid + "," + id
				+ "," + name + "," + departmentname + "," + duty + ","
				+ dutycode + "," + email + "," + temp;

		infos[0] = departmentname;
		infos[1] = id;
		infos[2] = duty;
		infos[3] = values;

		return infos;
	}

	@Override
	public Department getDepartment(String code) throws Exception {
		QuerySpec qs = new QuerySpec(Department.class);
		qs.appendWhere(new SearchCondition(Department.class, Department.CODE, "=", code), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			Department dept = (Department) qr.nextElement();
			return dept;
		}
		return null;
	}

	@Override
	public WTGroup getWTGroup(String code) throws Exception {
		QuerySpec qs = new QuerySpec(WTGroup.class);
		qs.appendWhere(new SearchCondition(WTGroup.class, WTGroup.NAME, "=", code), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			WTGroup dept = (WTGroup) qr.nextElement();
			return dept;
		}
		return null;
	}

	@Override
	public WTUser getWTUser(String name) {
		try {
			QuerySpec spec = new QuerySpec();
			int userPos = spec.addClassList(WTUser.class, true);
			spec.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME, "=", name), new int[] { userPos });
			QueryResult qr = PersistenceHelper.manager.find(spec);
			if (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				return (WTUser) obj[0];
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 吏꾩쁺�궛�뾽 = JY 吏꾩쁺�궛�뾽(�슱�궛) = UJ 留덉꽦�궛�뾽 = MS 遺곴꼍�궛�뾽 = BJ 湲덉＜吏꾩쁺 = CJ
	 * 遺��꽌蹂� 濡쒓렇
	 * 
	 * @return
	 * @throws WTException
	 */
	/*
	 * public String getDepartmentImg() throws WTException{
	 * 
	 * WTUser user = (WTUser)SessionHelper.getPrincipal(); return
	 * getDepartmentImg(user); }
	 * 
	 * public String getDepartmentImg(WTUser user){ String imgURL =
	 * "\\Windchill\\jsp\\portal\\images\\img_menu\\"; try{ //WTUser user =
	 * (WTUser)SessionHelper.getPrincipal(); Department dp=
	 * DepartmentHelper.manager.getDepartment(user);
	 * 
	 * String img = "JY_LOGIN.gif"; if(dp != null){ String departCode =
	 * dp.getCode(); if(departCode.toUpperCase().startsWith("JY") ||
	 * departCode.toUpperCase().startsWith("UJ")){ img = "JY_LOGIN.gif"; }else
	 * if(departCode.toUpperCase().startsWith("MS")){ img = "MS_LOGIN.gif";
	 * }else if(departCode.toUpperCase().startsWith("BJ")){ img =
	 * "BJ_LOGIN.gif"; }else if(departCode.toUpperCase().startsWith("CJ")){ img
	 * = "CJ_LOGIN.gif"; } } imgURL =imgURL+img; }catch(Exception e){
	 * e.printStackTrace(); }
	 * 
	 * return imgURL; }
	 */
	@Override
	public String getDepartmentImg(Department dp) {

		String imgURL = "\\Windchill\\jsp\\portal\\images\\img_menu\\";
		try {

			String img = "JY_LOGIN.gif";
			if (dp != null) {
				String departCode = dp.getCode();
				if (departCode.toUpperCase().startsWith("JY")
						|| departCode.toUpperCase().startsWith("UJ")) {
					img = "topMenu_left2.png";
				} else if (departCode.toUpperCase().startsWith("MS")) {
					img = "MS_LOGIN.gif";
				} else if (departCode.toUpperCase().startsWith("BJ")) {
					img = "BJ_LOGIN.gif";
				} else if (departCode.toUpperCase().startsWith("CJ")) {
					img = "CJ_LOGIN.gif";
				}
			}
			imgURL = imgURL + img;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return imgURL;
	}
	
	@Override
	public Map<String,Object> searchUserAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			
			String deptCode = request.getParameter("deptCode");
			Department dept = DepartmentHelper.service.getDepartment(deptCode);
			
			String keyvalue = request.getParameter("keyvalue");
			//String chief = null;
			
			QuerySpec qs = new QuerySpec(People.class);
			int ii = qs.addClassList(People.class, true);
			int jj = qs.addClassList(WTUser.class, true);
			int kk = qs.addClassList(Department.class, true);

			qs.appendWhere(new SearchCondition(People.class, "isDisable", SearchCondition.IS_FALSE), new int[] { ii });

			qs.appendAnd();
			qs.appendWhere(new SearchCondition(People.class, "departmentReference.key.id", Department.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { ii, kk });

			qs.appendAnd();
			qs.appendWhere(new SearchCondition(People.class, "userReference.key.id", WTUser.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { ii, jj });

			if (dept != null) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();
				qs.appendWhere( new SearchCondition(Department.class, "thePersistInfo.theObjectIdentifier.id", "=", dept.getPersistInfo().getObjectIdentifier().getId()), new int[] { kk });
			}
			
			if (keyvalue != null && !keyvalue.equals("")) {
				if (qs.getConditionCount() > 0) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(People.class, "name", SearchCondition.LIKE, "%" + keyvalue + "%"), new int[] { ii });
			}

			/*
			if ("true".equals(chief)) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				qs.appendWhere(new SearchCondition(People.class, "chief", SearchCondition.IS_TRUE));

			}
			*/
			
			SearchUtil.setOrderBy(qs, People.class, ii, People.NAME, false);
			
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
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
		
	    while(qr.hasMoreElements()){
			Object[] o = (Object[])qr.nextElement();
			PeopleData pd = new PeopleData(o);
			
			xmlBuf.append("<row id='"+ pd.peopleOID +"'>");
			xmlBuf.append("<cell><![CDATA[]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + pd.name + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + pd.duty + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + pd.departmentName + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + pd.email + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + (pd.getChief()==true? "O":" ") + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + pd.wtuserOID + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + pd.gradeName + "]]></cell>");
			
			String taskView = "";
			taskView += "<a href=javascript:viewTaskList('" + pd.wtuserOID + "')>";
			taskView += "<img name='taskView' src='/Windchill/jsp/portal/images/s_search.gif' border=0>";
			taskView += "</a>";
			
			xmlBuf.append("<cell><![CDATA[" + taskView + "]]></cell>");
			xmlBuf.append("</row>" );
			
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
	public List<Map<String,Object>> approveUserSearch(String userKey) throws Exception {
		if(userKey==null)
			userKey = "";

			QuerySpec qs = new QuerySpec(People.class);
			int ii = qs.addClassList(People.class,true);
			int jj = qs.addClassList(WTUser.class,true);
			int kk = qs.addClassList(Department.class,true);
						
			qs.appendWhere(new SearchCondition(People.class,"departmentReference.key.id",Department.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,kk});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(People.class,"userReference.key.id",WTUser.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,jj});
			
			if (userKey != null && !userKey.equals("")) {
			    if (qs.getConditionCount() > 0)
			    	qs.appendAnd();

				qs.appendOpenParen();
			    qs.appendWhere(new SearchCondition(People.class, "name", SearchCondition.LIKE, "%" + userKey + "%", false), new int[] { ii });
			    qs.appendOr();
			    qs.appendWhere(new SearchCondition(WTUser.class, "name", SearchCondition.LIKE, "%" + userKey + "%", false), new int[] { jj });
			    qs.appendCloseParen();
			}
			qs.appendOrderBy(new OrderBy(new ClassAttribute(People.class, "name"),false),new int[]{ii});
			//qs.appendOrderBy(new OrderBy(new ClassAttribute(People.class,"dutyCode"),false),new int[]{ii});

			//System.out.println("@@@ qs = " + qs.toString());
			
			QueryResult qr = PersistenceHelper.manager.find(qs);

			Object[] o = null;
			People people = null;
			Department dept1 = null;
			WTUser user = null;

			//request.setCharacterEncoding("utf-8");

			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			
			if( qr != null ){ 
				//System.out.println("@@@ people size = " + qr.size());
				while(qr.hasMoreElements()){
					o = (Object[])qr.nextElement();
					people = (People)o[0];
					user = (WTUser)o[1]; 
					dept1 =(Department) o[2];
					//PeopleData data = new PeopleData(people);
					
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("userOid", user.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("peopleOid", people.getPersistInfo().getObjectIdentifier().getStringValue());
					//map.put("deptOid", data.department);
					map.put("id", user.getName());
					map.put("name", people.getName());
					map.put("deptName", dept1.getName());
					map.put("duty", people.getDuty());
					map.put("dutyCode", people.getDutyCode());
					map.put("email", user.getEMail());
					map.put("temp", people.getName());
					
					list.add(map);
				}
			}
			return list;
	}
	
	@Override
	public List<Map<String,Object>> approveUserOrg(String deptCode) throws Exception {
		Department dept = DepartmentHelper.service.getDepartment(deptCode);
		
		QuerySpec qs = new QuerySpec(People.class);
		int ii = qs.addClassList(People.class, true);
		int jj = qs.addClassList(WTUser.class, true);
		int kk = qs.addClassList(Department.class, true);

		qs.appendWhere(new SearchCondition(People.class, "isDisable", SearchCondition.IS_FALSE), new int[] { ii });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(People.class, "departmentReference.key.id", Department.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { ii, kk });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(People.class, "userReference.key.id", WTUser.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { ii, jj });

		if (dept != null) {
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere( new SearchCondition(Department.class, "thePersistInfo.theObjectIdentifier.id", "=", dept.getPersistInfo().getObjectIdentifier().getId()), new int[] { kk });
		}
		
		
		qs.appendOrderBy(new OrderBy(new ClassAttribute(People.class, People.NAME), false), new int[] { ii });
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		if( qr != null ){ 
			Object[] o = null;
			People people = null;
			WTUser user = null;
			Department dept1 = null;
			while(qr.hasMoreElements()){
				o = (Object[])qr.nextElement();
				people = (People)o[0];
				user = (WTUser)o[1]; 
				dept1 =(Department) o[2];
				//PeopleData data = new PeopleData(people);
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("userOid", user.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("peopleOid", people.getPersistInfo().getObjectIdentifier().getStringValue());
				//map.put("deptOid", data.department);
				map.put("id", user.getName());
				map.put("name", people.getName());
				map.put("deptName", dept1.getName());
				map.put("duty", people.getDuty());
				map.put("dutyCode", people.getDutyCode());
				map.put("email", user.getEMail());
				map.put("temp", people.getName());
				
				list.add(map);
			}
		}
		return list;
	}
	
	@Override
	public Map<String,Object> companyTreeSearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 10);
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			String oid = StringUtil.checkNull(request.getParameter("oid"));
			String sortType = StringUtil.checkReplaceStr(request.getParameter("sortType"), "false");
			String name = StringUtil.checkNull(request.getParameter("name"));
			String command = StringUtil.checkNull(request.getParameter("command"));
			
			Department dept = null;
			
			if(oid!=null && oid.length()>0 && !oid.equals("null") && !oid.equals("root") ){
				dept = (Department)CommonUtil.getObject(oid);
			}
			
			QuerySpec qs = new QuerySpec();
			
			int ii = qs.addClassList(People.class,true);
			int jj = qs.addClassList(WTUser.class,false);
			int kk = qs.addClassList(Department.class,false);

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
		    
			/*int idx = 0;   2022.01.19 shjeong modify
			if ( oid.equals("root") || oid.trim().equals("") ){
				spec = new QuerySpec();
				idx = spec.appendClassList(People.class, true);
			}else {
				dept = (Department)CommonUtil.getObject(oid);
				spec = DepartmentHelper.service.getDepartmentPeopleAll(dept);
			}
			
			if(name.trim().length() > 0) {
				if(spec.getConditionCount() > 0)
					spec.appendAnd();
				spec.appendWhere(new SearchCondition(People.class, "name", SearchCondition.LIKE, "%" + name + "%" ), new int[] { idx });
			}
			
			spec.appendOrderBy(new OrderBy(new ClassAttribute(People.class, People.NAME), false), new int[] { idx });*/
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
	    	Object[] obj = (Object[])qr.nextElement();
			PeopleData data = new PeopleData(obj[0]);
			
			String name = "";
			String deptName = "";
			String duty = "";
			
			if(data.isDiable) {
				name = "<font color=gray>" + data.name + "</font>";
				deptName = "<font color=gray>" + data.departmentName + "</font>";
				duty = "<font color=gray>" + data.duty + "</font>";
			}else {
				name = data.name;
				deptName = data.departmentName;
				duty = data.duty;
			}
			
			xmlBuf.append("<row id='" + data.id + "'>");
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			if(CommonUtil.isAdmin()) {
				xmlBuf.append("<cell><![CDATA[<a href=javascript:changePassword('" + data.id + "')>" + data.id + "</a>]]></cell>");
			}else {
				xmlBuf.append("<cell><![CDATA[" + data.id + "]]></cell>");
			}
			xmlBuf.append("<cell><![CDATA[" + name + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + deptName + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + duty + "]]></cell>");
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
	public List<Map<String,String>> viewApproverTemplate(String oid, String type) throws Exception {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		if( !oid.equals("") ) {
			ApprovalLineTemplate template = null;
			try {
				template = (ApprovalLineTemplate)WCUtil.getObject(oid);
			} catch (Exception err) {
				err.printStackTrace();
			}

			//System.out.println("template = " + template);
			if(template!=null){
				
				ArrayList alist = null;
				
				if("app1".equals(type)) {
					alist = template.getPreDiscussList();
				}else if("app2".equals(type)) {
					alist = template.getDiscussList();
				}else if("app3".equals(type)) {
					alist = template.getPostDiscussList();
				}
				
				ReferenceFactory rf = new ReferenceFactory();
				for(int i=0; alist!=null && alist.size()>i; i++){
					String uid = (String)alist.get(i);
					WTUser auser = (WTUser)rf.getReference(uid).getObject();
					String[] userInfo = UserHelper.service.getUserInfo(auser);
					
					Map<String,String> map = new HashMap<String,String>();
					map.put("oid", uid);
					map.put("name", auser.getFullName());
					map.put("deptName", userInfo[0]);
					map.put("id", userInfo[1]);
					map.put("duty", userInfo[2]);
					
					list.add(map);
				}
				/*
				//합의
				ArrayList alist2 = template.getPreDiscussList();
				//결재
				ArrayList alist3 = template.getDiscussList();
				//수신
				ArrayList alist4 = template.getPostDiscussList();			
				ArrayList alist5 = template.getTempList();
				
				
				//ArrayList tlist = template.getTempList();
				ReferenceFactory rf = new ReferenceFactory();
				for(int i=0; alist2!=null && alist2.size()>i; i++){
					String uid = (String)alist2.get(i);
					WTUser auser = (WTUser)rf.getReference(uid).getObject();
					String[] userInfo = UserHelper.service.getUserInfo(auser);
					%>
						addApprovalLine2('<%=uid%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>');
					<%
				}		
				for(int i=0; alist3!=null && alist3.size()>i; i++){
					String uid = (String)alist3.get(i);
					WTUser auser = (WTUser)rf.getReference(uid).getObject();
					String[] userInfo = UserHelper.service.getUserInfo(auser);
					%>
						addApprovalLine3('<%=uid%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>');
					<%
				}		
				for(int i=0; alist4!=null && alist4.size()>i; i++){
					String uid = (String)alist4.get(i);
					WTUser auser = (WTUser)rf.getReference(uid).getObject();
					String[] userInfo = UserHelper.service.getUserInfo(auser);
					%>
						addApprovalLine4('<%=uid%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>');
					<%
				}		
				for(int i=0; alist5!=null && alist5.size()>i; i++){
					String uid = (String)alist5.get(i);
					WTUser auser = (WTUser)rf.getReference(uid).getObject();
					String[] userInfo = UserHelper.service.getUserInfo(auser);
					%>
					addTempLine('<%=uid%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>');
					<%
				}
				*/
			}
		}
		
		return list;
	}
}
