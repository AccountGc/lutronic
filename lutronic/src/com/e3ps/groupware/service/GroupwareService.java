package com.e3ps.groupware.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import wt.method.RemoteInterface;

import com.e3ps.common.beans.ResultData;
import com.e3ps.org.beans.PeopleData;

@RemoteInterface
public interface GroupwareService {

	Map<String, Object> listWorkItemAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	ModelAndView processInfo(HttpServletRequest request, HttpServletResponse response) throws Exception;

	ModelAndView approval(HttpServletRequest request, HttpServletResponse response) throws Exception;

	List<Map<String, Object>> getApprovalList(String oid) throws Exception;

	List<Map<String, Object>> getMailList(String oid);

	void approveAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	Map<String, Object> listItemAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	List<Map<String, String>> loadApprovalLine(HttpServletRequest request, HttpServletResponse response) throws Exception;

	Map<String, String> includeReassing(String workItemOid) throws Exception;

	boolean changePasswordAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	Map<String, Object> listNoticeAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	Map<String, Object> wfProcessInfoAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	ResultData changeIBAAction(HttpServletRequest request, HttpServletResponse response);

	Map<String, String> multiPublishingAction(HttpServletRequest request,
			HttpServletResponse response);

	List<Map<String, String>> include_mailUser(String workOid);

	String emailUserListAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	void batchReceiveAction(Map<String, Object> reqMap) throws Exception;

	void userInfoEdit(PeopleData data) throws Exception;
}
