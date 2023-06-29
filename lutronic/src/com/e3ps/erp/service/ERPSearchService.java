package com.e3ps.erp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.erp.beans.BOMERPData;
import com.e3ps.erp.beans.PARTERPData;
import com.e3ps.part.beans.PartTreeData;

import wt.fc.WTObject;
import wt.method.RemoteInterface;
import wt.query.QuerySpec;
import wt.vc.baseline.ManagedBaseline;


@RemoteInterface
public interface ERPSearchService {

	QuerySpec getPARTERPQuery(HttpServletRequest req) throws Exception;

	Map<String, Object> listPARTERPAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	QuerySpec getECOERPQuery(HttpServletRequest req) throws Exception;

	Map<String, Object> listECOERPAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	QuerySpec getBOMERPQuery(HttpServletRequest req) throws Exception;

	Map<String, Object> listBOMERPAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	ManagedBaseline getLastManagedBaseline(String partOid) throws Exception;

	ArrayList<PartTreeData[]> getBaseLineCompare(String oid)
			throws Exception;

	HashMap<String, Object> getZINFOData(Class cls, String startZinfo, String endZinfo)
			throws Exception;

	List<PARTERPData> listPARTERPAction(String ecoNumber) throws Exception;

	List<BOMERPData> listBOMERPAction(String ecoNumber) throws Exception;

	boolean checkSendEO(String eoNumber);

}
