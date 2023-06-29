package com.e3ps.groupware.workprocess.service;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.beans.ResultData;







import wt.method.RemoteInterface;

@RemoteInterface
public interface AsmApprovalService {

	void setAsmApprovalState(Object obj, String state);
	/*
	void setAsmApprovalState(Object obj, String state);

	ResultData createAsmAction(HttpServletRequest request,
			HttpServletResponse response);
	*/

	ResultData createAsmAction(HttpServletRequest request,
			HttpServletResponse response);

	String deleteAsmAction(String oid) throws Exception;

	ResultData updateAsmAction(HttpServletRequest request) throws Exception;
}
