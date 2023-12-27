package com.e3ps.part.service;

import java.util.List;
import java.util.Vector;

import com.e3ps.change.EChangeOrder;

import wt.epm.build.EPMBuildRule;
import wt.method.RemoteInterface;
import wt.part.WTPart;
import wt.util.WTException;

@RemoteInterface
public interface PartSearchService {

	EPMBuildRule getBuildRule(Object obj) throws WTException;

	Vector<EChangeOrder> getPartEoWorking(WTPart part);

	boolean isLastPart(WTPart part);

	boolean isSelectEO(WTPart part);
	
	boolean isHasDocumentType(WTPart part, String docType) throws Exception;
	
	String getHasDocumentOid(WTPart part, String docType) throws Exception;

	List<WTPart> getPartEndItem(WTPart part, List<WTPart> partList)
			throws Exception;

	boolean isSelectEO(WTPart part, String moduleType);

	Vector<EChangeOrder> getPartEoWorking(WTPart part, String moduleType);

	List<WTPart> getPartInstance(WTPart part, List<WTPart> list)
			throws Exception;

	String isLastPart(String oid);

}
