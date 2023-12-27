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
import com.e3ps.change.activity.dto.ActDTO;
import com.e3ps.change.eco.dto.EcoDTO;

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

	List<ActDTO> getEOActivityList(String eoType, String ecaOid);

	List<ActDTO> getEOActivityUpdateList(ECOChange eo, String eoType);

	ManagedBaseline getEOToPartBaseline(String eoNumber, String partNumber)
			throws Exception;

	List<Map<String, String>> getGroupingBaseline(String partOid,
			String allBaseline, String baseline) throws Exception;

	ManagedBaseline getLastBaseline(String partOid) throws Exception;

	List<EcoDTO> include_ECOList(String oid, String moduleType)  throws Exception;

	

}
