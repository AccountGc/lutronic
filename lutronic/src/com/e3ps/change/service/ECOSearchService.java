package com.e3ps.change.service;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.EOData;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.part.beans.PartData;

import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.method.RemoteInterface;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.util.WTException;

@RemoteInterface
public interface ECOSearchService {

	QuerySpec getECOQuery(HttpServletRequest req) throws Exception;

	Map<String, Object> listECOAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	QueryResult ecoPartLink(EChangeOrder eco);

	Vector<WTPart> ecoPartList(EChangeOrder eco);

	List<WTPart> ecoPartReviseList(EChangeOrder eco);

	Vector<EcoPartLink> ecoPartLinkList(EChangeOrder eco);

	QuerySpec getEOPartList(HttpServletRequest req);

	Map<String, Object> listEOAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	List<EOCompletePartLink> getCompletePartLink(WTObject eco) throws WTException;

	List<WTPart> getCompletePartList(EChangeOrder eco) throws Exception;

	List<PartData> getCompletePartDataList(EChangeOrder eco) throws Exception;

	List<RequestOrderLink> getRequestOrderLink(ECOChange eo) throws WTException;

	List<EChangeOrder> getRequestOrderLinkECO(EChangeRequest eo)
			throws Exception;

	List<ECOData> getRequestOrderLinkECOData(EChangeRequest ecr)
			throws Exception;

	List<Map<String, Object>> batchRevision(String[] revisableArr)
			throws Exception;

	List<EChangeOrder> getPartTOECOList(WTPart part) throws Exception;

	List<ECOData> include_ChangeECOView(String oid, String moduleType)
			throws Exception;

	List<ECOData> include_DistributeEOList(String oid, String moduleType)
			throws Exception;

	EChangeOrder getEChangeOrder(String ecoNumber) throws Exception;

	List<Map<String, Object>> listAUIEOAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	List<Map<String, Object>> listAUIECOAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	/**	 ECOData 리스트를 리턴 ( WTDocument OID )
	 * @param documentOid
	 * @param roleName     : used, useBy
	 * @return
	 * @throws Exception
	 */
	List<ECOData> getECOListToLinkRoleName(String documentOid, String roleName) throws Exception;

	/**	 ECOData 리스트를 리턴 ( WTDocument )
	 * @param document
	 * @param roleName     : used, useBy
	 * @return
	 * @throws Exception
	 */
	List<ECOData> getECOListToLinkRoleName(WTDocument document, String roleName) throws Exception;

	Map<String, Object> listPagingAUIECOAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	Map<String, Object> listPagingAUIEOAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;


}
