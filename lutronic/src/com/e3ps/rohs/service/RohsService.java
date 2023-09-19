package com.e3ps.rohs.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.enterprise.RevisionControlled;
import wt.method.RemoteInterface;
import wt.part.WTPart;
import wt.util.WTException;

import com.e3ps.change.beans.ECRData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.dto.RohsData;

@RemoteInterface
public interface RohsService {
	
	List<Map<String,String>> rohsFileType();
	
	Map<String, Object> listRohsAction(HttpServletRequest request,HttpServletResponse response) throws Exception;

	List<RohsData> include_RohsList(String oid) throws Exception;

	ResultData createRohsAction(HttpServletRequest request,HttpServletResponse response);

	List<Map<String, Object>> getRohsContent(String oid) throws Exception;

	ResultData updateRohsAction(HttpServletRequest request, HttpServletResponse response);
	
	ResultData reviseUpdate(Map<String, Object> params) throws Exception;
	
	Map<String,Object> listRoHSDataAction(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	Map<String, Object> listRoHSPartAction(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	Map<String, Object> listRoHSProductAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	Map<String, Object> delete(String oid) throws Exception;

	void createROHSToPartLink(RevisionControlled rv, String[] oids) throws WTException;

	List<RohsData> include_RohsView(String oid, String module, String roleType) throws Exception;

	void revisePartToROHSLink(WTPart oldPart, WTPart newPart) throws Exception;

	ResultData duplicateName(String rohsName);
	
	ResultData copyRohsAction(HttpServletRequest request, HttpServletResponse response);

	String createPackageRoHSAction(HttpServletRequest request, HttpServletResponse response);

	String createPackageRoHSLinkAction(HttpServletRequest request,
			HttpServletResponse response);

	Map<String, Object> listAUIRoHSPartAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	ResultData batchROHSDown(HttpServletRequest request, HttpServletResponse response);
			
	public void create(Map<String, Object> params) throws Exception;
	
	public void batch(Map<String, Object> params) throws Exception;
	
	public void update(Map<String, Object> params) throws Exception;
}
