package com.e3ps.doc.service;

import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.dto.DocumentDTO;

import wt.doc.WTDocument;
import wt.query.QuerySpec;

public interface DocumentQueryService {
	QuerySpec getListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception;

	QuerySpec devActiveLinkDocument(String activeOid, String docOid);

	QuerySpec listCreateDocumentLinkAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	/**
	 *    DocumentToDocumentLink 리스트를 리턴( WTDocument OID )
	 * @param documentOid
	 * @param roleName     : used, useBy
	 * @return
	 * @throws Exception
	 */
	List<DocumentToDocumentLink> getDocumentToDocumentLinks(String documentOid, String roleName) throws Exception;

	/**
	 *    DocumentToDocumentLink 리스트를 리턴( WTDocument )
	 * @param document
	 * @param roleName     : used, useBy
	 * @return
	 * @throws Exception
	 */
	List<DocumentToDocumentLink> getDocumentToDocumentLinks(WTDocument document, String roleName) throws Exception;

	/**	 DocumentData 리스트를 리턴 ( WTDocument OID )
	 * @param documentOid
	 * @param roleName     : used, useBy
	 * @return
	 * @throws Exception
	 */
	List<DocumentDTO> getDocumentListToLinkRoleName(String documentOid, String roleName) throws Exception;

	/**	 DocumentData 리스트를 리턴 ( WTDocument )
	 * @param document
	 * @param roleName     : used, useBy
	 * @return
	 * @throws Exception
	 */
	List<DocumentDTO> getDocumentListToLinkRoleName(WTDocument document, String roleName) throws Exception;
}
