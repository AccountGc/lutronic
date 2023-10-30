package com.e3ps.drawing.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.beans.ResultData;
import com.e3ps.drawing.beans.EpmData;
import com.e3ps.groupware.notice.dto.NoticeDTO;

import wt.epm.EPMDocument;
import wt.epm.EPMDocumentType;
import wt.method.RemoteInterface;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.util.WTException;

@RemoteInterface
public interface DrawingService {

	EPMDocument getEPMDocument(WTPart _part) throws Exception;

	Map<String, String> create(Map<String, Object> hash, String[] loc);

	String getPrefix(String changeName);

	boolean isFileNameCheck(String fileName);

	EPMDocumentType getEPMDocumentType(String fileName);

	Map<String, Object> listDrawingAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	QuerySpec getListQuery(HttpServletRequest request) throws Exception;

	List<Map<String, String>> cadDivisionList();

	List<Map<String, String>> cadTypeList();

	WTPart getWTPart(EPMDocument _epm) throws Exception;

	EPMDocument createEPM(Map<String,Object> hash) throws Exception;

	Map<String,Object> delete(String oid);

	Hashtable modify(Hashtable hash, String[] loc, String[] deloc, String[] partOid) throws Exception;

	List<EpmData> include_DrawingList(String oid, String moduleType, String epmType) throws Exception;

	EPMDocument getLastEPMDocument(String number);

	String getThumbnailSmallTag(EPMDocument epm) throws Exception;

	List<EpmData> include_Reference(String oid, String moduleType) throws Exception;

	Map<String, Object> thumbView(HttpServletRequest request) throws WTException;

	String updateIBA(EPMDocument epm, WTPart part) throws WTException;

	Map<String, Object> requestDrawingMapping(HttpServletRequest request, HttpServletResponse response);
	
	ResultData createDrawing(Map<String,Object> map);
	
	List<EpmData> include_drawingLink(String module, String oid);

	ResultData linkDrawingAction(HttpServletRequest request, HttpServletResponse response);
	
	ResultData deleteDrawingLinkAction(HttpServletRequest request, HttpServletResponse response);

	ResultData updateNameAction(HttpServletRequest request, HttpServletResponse response);

	String createPackageDrawingAction(HttpServletRequest request, HttpServletResponse response);

	void partTreeDrawingDown(HttpServletRequest request,
			HttpServletResponse response) throws Exception;
	
	public void create(Map<String,Object> map) throws Exception;
	
	public void batch(ArrayList<Map<String, Object>> gridData) throws Exception;
}
