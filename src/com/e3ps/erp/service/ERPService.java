package com.e3ps.erp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.beans.ResultData;
import com.e3ps.erp.beans.BomData;

import wt.lifecycle.State;
import wt.method.RemoteInterface;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.vc.views.View;

@RemoteInterface
public interface ERPService {

	void sendERP(EChangeOrder eco) throws Exception;

	ArrayList descentLastPart(WTPart part, View view, State state) throws WTException;

	void sendERPToPart(List<WTPart> sendPartList, String ecoNumber)
			throws Exception;

	void sendEOERP(EChangeOrder eco) throws Exception;

	void sendECOERP(EChangeOrder eco) throws Exception;

	Vector<BomData> getBom(WTPart part, WTPartUsageLink link, View view,
			Vector tempBom, boolean isALL) throws Exception;

	void sendERPToECO(EChangeOrder eco, Vector<BomData> tempBOM)
			throws Exception;

	void sendERPToBOM(Vector<BomData> bomList, String ecoNumber)
			throws Exception;

	Vector<BomData> compareBom(WTPart part, Vector<BomData> bomList) throws Exception;

	ResultData erpCheckAction(HttpServletRequest request) throws Exception;

	ResultData returnERP(Class cls, String startZifno, String endZifno)
			throws Exception;

	

}
