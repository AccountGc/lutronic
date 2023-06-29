package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.beans.ECAData;
import com.e3ps.change.beans.ROOTData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.FileRequest;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.part.beans.PartData;

import wt.doc.WTDocument;
import wt.method.RemoteInterface;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;

@RemoteInterface
public interface ECAService {

	ResultData createRootDefinitionAction(HttpServletRequest req)
			throws Exception;

	ResultData updateRootDefinitionAction(HttpServletRequest req)
			throws Exception;

	String deleteRootDefinitionAction(HttpServletRequest req) throws Exception;

	List<EChangeActivityDefinition> getActiveDefinition(long rootOid)
			throws Exception;

	List<ROOTData> getRootDefinition() throws Exception;

	Map<String, Object> listActiveDefinitionAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	ResultData createActivityDefinitionAction(HttpServletRequest req)
			throws Exception;

	String deleteRootDefinition(String oid) throws Exception;

	String deleteActivityDefinition(String oids) throws Exception;

	ResultData updateActivityDefinitionAction(HttpServletRequest req)
			throws Exception;

	List<EADData> setActiveDefinition(String oid) throws Exception;

	boolean createActivity(HttpServletRequest req, ECOChange eco) throws Exception;

	
	List<ECAData> include_ecaList(String oid) throws Exception;

	List<EChangeActivity> getECAList(ECOChange eo) throws Exception;

	List<EChangeActivity> getNextActivity(EChangeActivity activity)
			throws Exception;

	List<DocumentActivityLink> getECADocumentLink(EChangeActivity eca);

	List<WTDocument> getECADocument(EChangeActivity eca);

	List<DocumentData> getECADocumentData(EChangeActivity eca);

	boolean isStepComplete(EChangeActivity eca) throws Exception;

	void createDocumentActivityLink(WTDocument doc, String parentOid)
			throws Exception;

	List<Map<String, Object>> viewECA(ECOChange eo) throws Exception;

	boolean ecaAttachFile(Hashtable hash) throws Exception;

	void createAutoActivity(EChangeOrder eco) throws Exception;

	String getECAMaxStep(ECOChange eo) throws Exception;

	int getECAMaxSortNumber(ECOChange eo) throws Exception;

	boolean getEOActivity(ECOChange eo, String activityName) throws Exception;

	List<ECAData> getEOActivity(String oid) throws Exception;

	ResultData modifyECAction(HttpServletRequest req) throws Exception;

	List<Map<String, Object>> viewECA_Doc(ECOChange eo) throws Exception;

	
	
}
