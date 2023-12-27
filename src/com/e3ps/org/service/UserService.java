package com.e3ps.org.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.org.Department;
import com.e3ps.org.People;

import wt.method.RemoteInterface;
import wt.org.WTGroup;
import wt.org.WTUser;

@RemoteInterface
public interface UserService {

	void setAllUserName() throws Exception;

	boolean isAdmin() throws Exception;

	boolean isMember(String group) throws Exception;

	void syncSave(WTUser _user);

	void syncDelete(WTUser _user);

	void syncWTUser();

	People getPeople(WTUser user);

	People getPeople(long userid);

	WTUser getUser(String id);

	String[] getUserInfo(WTUser user) throws Exception;

	Department getDepartment(String code) throws Exception;

	WTGroup getWTGroup(String code) throws Exception;

	WTUser getWTUser(String name);

	String getDepartmentImg(Department dp);

	Map<String, Object> searchUserAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	List<Map<String,Object>> approveUserSearch(String userKey) throws Exception;

	List<Map<String, Object>> approveUserOrg(String deptCode) throws Exception;

	Map<String, Object> companyTreeSearch(HttpServletRequest request, HttpServletResponse response) throws Exception;

	List<Map<String, String>> viewApproverTemplate(String oid, String type) throws Exception;

}
