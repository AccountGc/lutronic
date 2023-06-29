package com.e3ps.groupware.workprocess.service;



import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.beans.ResultData;





import com.e3ps.groupware.workprocess.AppPerLink;
import com.e3ps.groupware.workprocess.AsmApproval;

import wt.doc.WTDocument;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteInterface;
import wt.query.QuerySpec;
import wt.util.WTException;

@RemoteInterface
public interface AsmSearchService {

	Map<String, Object> listAsmAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	QuerySpec getAsmQuery(HttpServletRequest req) throws Exception;

	List<WTDocument> getObjectForAsmApproval(AsmApproval asm);

	List<AppPerLink> getLinkForAsmApproval(AsmApproval asm) throws WTException;

	AsmApproval getAsmApproval(WTDocument doc);
	
	

}
