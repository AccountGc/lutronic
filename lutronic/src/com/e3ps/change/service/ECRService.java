package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcrToEcrLink;
import com.e3ps.change.beans.ECRData;
import com.e3ps.change.beans.EOData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.FileRequest;

import wt.method.RemoteInterface;
import wt.query.QuerySpec;

@RemoteInterface
public interface ECRService {

	ArrayList getWorkingECAUser(EChangeOrder eco);

	EChangeActivity getLastStepECA(ECOChange eo);

	ResultData updateECRAction(HttpServletRequest req) throws Exception;

	String deleteEcr(String oid) throws Exception;

	List<EOData> include_ecrList(String oid) throws Exception;

	ResultData createECRAction(HttpServletRequest req);

	List<EcrToEcrLink> getEcrToEcrLinks(EChangeRequest ecr, String string) throws Exception;
	
}
