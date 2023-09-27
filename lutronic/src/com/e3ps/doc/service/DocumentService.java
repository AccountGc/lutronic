package com.e3ps.doc.service;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.beans.ResultData;
import com.e3ps.doc.dto.DocumentDTO;

import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.method.RemoteInterface;
import wt.part.WTPartDescribeLink;

@RemoteInterface
public interface DocumentService {

	Map<String, Object> requestDocumentMapping(Map<String, Object> params);

//	ResultData createDocumentAction(Map<String, Object> map);

	Map<String, Object> updateDocumentAction(Map<String, Object> map);

//	String createPackageDocumentAction(HttpServletRequest request, HttpServletResponse response);

	ResultData approvalPackageDocumentAction(HttpServletRequest request, HttpServletResponse response);

	List<DocumentDTO> include_documentLink(String module, String oid);

	ResultData reviseUpdate(Map<String, Object> params) throws Exception;

	void createOutPutLink(WTDocument doc, Hashtable hash) throws Exception;

	WTDocument getLastDocument(String number) throws Exception;

	Map<String, Object> createDocumentLinkAction(HttpServletRequest request, HttpServletResponse respose)
			throws Exception;

	Vector<WTPartDescribeLink> getWTPartDescribeLink(RevisionControlled rc, boolean isLast);

	ResultData linkDocumentAction(HttpServletRequest request, HttpServletResponse response);

	ResultData deleteDocumentLinkAction(HttpServletRequest request, HttpServletResponse response);

	public void batch(Map<String, Object> params) throws Exception;

	public void createComments(Map<String, Object> params) throws Exception;

	public void updateComments(Map<String, Object> params) throws Exception;

	public void deleteComments(String oid) throws Exception;

	/**
	 * 문서 일괄 결재 등록
	 * 
	 * @param params
	 */
	public abstract void register(Map<String, Object> params) throws Exception;

	/**
	 * 문서 등록
	 */
	public abstract void create(DocumentDTO dto) throws Exception;

	/**
	 * 문서 삭제
	 */
	public abstract Map<String, Object> delete(String oid) throws Exception;

	/**
	 * 문서 개정
	 */
	public abstract void revise(DocumentDTO dto) throws Exception;

	/**
	 * 문서 수정
	 */
	public abstract void modify(DocumentDTO dto) throws Exception;

}
