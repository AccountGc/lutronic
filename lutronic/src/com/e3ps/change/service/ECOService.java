package com.e3ps.change.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.WTObject;
import wt.iba.value.IBAHolder;
import wt.method.RemoteInterface;
import wt.part.WTPart;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.EOData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.FileRequest;
import com.e3ps.groupware.notice.beans.NoticeData;

@RemoteInterface
public interface ECOService {

	ResultData createEOAction(HttpServletRequest req);

	ResultData updateEOAction(HttpServletRequest req) throws Exception;

	String deleteECOAction(String oid) throws Exception;

	ResultData createECOAction(HttpServletRequest req);

	void completeECO(EChangeOrder eco);
	
	void completeECOTEST(EChangeOrder eco);

	void completePart(EChangeOrder eco) throws Exception;

	void completePartStateChange(WTPart part, Map<String, String> map)
			throws Exception;

	void approvedIBAChange(EChangeOrder eco) throws Exception;

	void approvedIBAChange(RevisionControlled rc, Map<String, String> map)
			throws Exception;

	void createECOBaseline(EChangeOrder eco) throws Exception;

	ManagedBaseline createBaseline(WTPart wtpart, EChangeOrder eco)
			throws Exception;

	List<WTPart> getBomList(WTPart part, View view, List<WTPart> BomList)
			throws Exception;

	List<WTPart> productPartList(EChangeOrder eco) throws Exception;

	void completeProduct(List<WTPart> list, EChangeOrder eco) throws Exception;

	String addPart(HttpServletRequest request) throws Exception;

	ResultData deleteCompletePartAction(String linkOid);

	boolean setReworkEOCheck(WTObject ps);

	ResultData excelDown(String oid, String eoType);

	ResultData batchEOAttachDownAction(String oid, String describe);

	ResultData batchEODrawingDownAction(String oid, String describe);
	
	public void create(ECOData data) throws Exception;
	
	public void createEO(ECOData data) throws Exception;

}
