package com.e3ps.change.service;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.beans.ECOData;
import com.e3ps.doc.beans.DocumentData;

import wt.method.RemoteInterface;
import wt.vc.baseline.ManagedBaseline;

@RemoteInterface
public interface ChangeService {

	Vector<EChangeActivityDefinition> getEOActivityDefinition(String eoType);
	
	Vector<EChangeActivityDefinition> getEOActivityDefinition(String eoType, String group);
	
	Vector<EChangeActivityDefinition> getEOActivityDefinition(String eoType, String step, String group);
	
	Vector<RequestOrderLink> getRelationECO(EChangeRequest ecr);

	Vector<RequestOrderLink> getRelationECR(EChangeOrder eco);

	void completeECA(EChangeActivity eca);

	void approveEO(ECOChange eo) throws Exception;

	
	List<Map<String, String>> listEulB_IncludeAction(String partOid, String allBaseline, String baseline) throws Exception;

	List<String[]> addECRPartCheck(String ecrOid) throws Exception;

	List<EADData> getEOActivityList(String eoType, String ecaOid);

	List<EADData> getEOActivityUpdateList(ECOChange eo, String eoType);

	ManagedBaseline getEOToPartBaseline(String eoNumber, String partNumber)
			throws Exception;

	List<Map<String, String>> getGroupingBaseline(String partOid,
			String allBaseline, String baseline) throws Exception;

	ManagedBaseline getLastBaseline(String partOid) throws Exception;

	List<ECOData> include_ECOList(String oid, String moduleType)  throws Exception;

	

}
