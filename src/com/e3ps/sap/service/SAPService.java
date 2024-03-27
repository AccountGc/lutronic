package com.e3ps.sap.service;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;

import wt.method.RemoteInterface;
import wt.part.WTPart;

@RemoteInterface
public interface SAPService {

	/**
	 * SAP EO 전송
	 */
	public abstract void sendSapToEo(EChangeOrder e, ArrayList<EOCompletePartLink> completeParts,
			ArrayList<WTPart> list) throws Exception;

	/**
	 * SAP ECO 전송
	 */
	public abstract void sendSapToEco(EChangeOrder e) throws Exception;

	/**
	 * SAP ECO 재전송
	 */
	public abstract void resendSapToEco(EChangeOrder e) throws Exception;

	/**
	 * SAP ECN 전송
	 */
	public abstract void sendSapToEcn(Map<String, Object> params) throws Exception;

	
	/**
	 * SAP ECO 재전송
	 */
	public abstract void qaSapToEco(EChangeOrder e) throws Exception;
}
