package com.e3ps.admin.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.method.RemoteInterface;

@RemoteInterface
public interface AdminService {

	void admin_actionDepartment(HttpServletRequest request, HttpServletResponse response) throws Exception;

	boolean admin_actionChief(String userOid) throws Exception;

	Map<String, Object> admin_downLoadHistoryAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	Map<String, Object> admin_mailAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	Map<String, Object> admin_loginHistoryAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	Map<String, Object> admin_changeActivityAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	Map<String, List<Map<String, String>>> admin_getDutyListAction(String duty) throws Exception;

	void admin_setDutyAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	Map<String, List<Map<String, String>>> admin_getDepartmentListAction(String oid) throws Exception;

	void admin_setDeptAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void admin_packageAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	Map<String, Object> admin_mailNewAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	public void createRootDefinition(Map<String, Object> params) throws Exception;

	public void updateRootDefinition(Map<String, Object> params) throws Exception;

	public void deleteRootDefinition(Map<String, Object> params) throws Exception;

	public void createActivityDefinition(Map<String, Object> params) throws Exception;

}
