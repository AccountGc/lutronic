package com.e3ps.change.service;

import java.util.List;
import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface ChangeWfService {

	boolean isApprovedTask(Object pbo);

	Map<String, Object> wf_Document(String ecaOid) throws Exception;

	List<Map<String, Object>> wf_OrderNumber(String ecaOid) throws Exception;

	List<Map<String, Object>> wf_CheckPart(String oid) throws Exception;

	List<Map<String, String>> wf_Part_Include(String ecoOid) throws Exception;

	List<Map> wf_CheckDrawing(String ecaOid) throws Exception;

	List<Map<String, Object>> wf_CheckPart(String oid, boolean checkDummy,
			String distribute) throws Exception;

}
