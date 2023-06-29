package com.e3ps.drawing.service;

import java.util.List;
import java.util.Vector;

import com.e3ps.common.beans.ResultData;

import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.method.RemoteInterface;

@RemoteInterface
public interface EpmSearchService {

	Vector getReferenceDependency(EPMDocument doc, String role);

	boolean isLastEPMDocument(EPMDocumentMaster master);

	EPMDocument getREFDWG(EPMDocument epm);

	EPMDocument getLastEPMDocument(EPMDocumentMaster master);

	EPMReferenceLink getEPMReference(EPMDocument epm3d, EPMDocument epm2d);

	Vector<EPMReferenceLink> getEPMReferenceList(EPMDocumentMaster master);

	EPMReferenceLink getEPMReferenceLink(EPMDocumentMaster master);

	EPMDocument getEPM2D(EPMDocumentMaster master);

	Vector getWTDocumentLink(String oid);

	Vector getRefBy(String oid);

	Vector getRef(String oid);

	Vector<EPMDescribeLink> getEPMDescribeLink(RevisionControlled rc, boolean isLast);

	EPMDocument getDrawingToCad(String number);

	List<EPMDocument> getInstance(EPMDocument epm, List<EPMDocument> list);

	void cadPublishScheduler();

	ResultData cadRePublish(String oid);

}
