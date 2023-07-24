package com.e3ps.doc.service;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.method.RemoteInterface;
import wt.part.WTPartDescribeLink;

import com.e3ps.common.beans.ResultData;
import com.e3ps.doc.beans.DocumentData;

@RemoteInterface
public interface DocumentService {
	
	Map<String,Object> requestDocumentMapping(Map<String, Object> params);
	
	ResultData createDocumentAction(Map<String, Object> map);
	
	ResultData deleteDocumentAction(HttpServletRequest request, HttpServletResponse response);
	
	ResultData updateDocumentAction(Map<String,Object> map);
	
	String createPackageDocumentAction(HttpServletRequest request, HttpServletResponse response);
	
	ResultData approvalPackageDocumentAction(HttpServletRequest request, HttpServletResponse response);
	
	List<DocumentData> include_documentLink(String module, String oid);
	
	Map<String, Object> listDocumentAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	String delete(String oid) throws Exception;

	ResultData reviseUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void createOutPutLink(WTDocument doc, Hashtable hash) throws Exception;

	List<DocumentData> include_DocumentList(String oid, String moduleType) throws Exception;

	WTDocument getLastDocument(String number) throws Exception;

	Map<String, Object> createDocumentLinkAction(HttpServletRequest request, HttpServletResponse respose) throws Exception;

	Vector<WTPartDescribeLink> getWTPartDescribeLink(RevisionControlled rc, boolean isLast);
	
	ResultData linkDocumentAction(HttpServletRequest request, HttpServletResponse response);

	ResultData deleteDocumentLinkAction(HttpServletRequest request, HttpServletResponse response);

	List<Map<String, Object>> listAUIDocumentAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	ResultData createAUIPackageDocumentAction(HttpServletRequest request,
			HttpServletResponse response);

	Map<String, Object> listPagingAUIDocumentAction(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception;
	
	public void create(Map<String, Object> params) throws Exception;
}
