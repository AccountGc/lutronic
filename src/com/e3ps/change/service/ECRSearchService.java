package com.e3ps.change.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.beans.ECRData;

import wt.method.RemoteInterface;
import wt.query.QuerySpec;

@RemoteInterface
public interface ECRSearchService {

	Map<String, Object> listECRAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	QuerySpec getECRQuery(HttpServletRequest req) throws Exception;
	
	/**
	 * ECR 객체
	 * @param eco
	 * @return
	 * @throws Exception
	 */
	List<EChangeRequest> getRequestOrderLinkECR(EChangeOrder eco)
			throws Exception;
	
	/**
	 * ECRData
	 * @param eco
	 * @return
	 * @throws Exception
	 */
	List<ECRData> getRequestOrderLinkECRData(EChangeOrder eco) throws Exception;
	
}
