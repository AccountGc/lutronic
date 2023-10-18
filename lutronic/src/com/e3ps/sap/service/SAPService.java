package com.e3ps.sap.service;

import com.e3ps.change.EChangeOrder;

import wt.method.RemoteInterface;

@RemoteInterface
public interface SAPService {

	/**
	 * EO / ECO 전송
	 */
	public abstract void sendERP(EChangeOrder eco) throws Exception;
}
