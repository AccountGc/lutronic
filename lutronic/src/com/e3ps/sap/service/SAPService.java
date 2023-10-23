package com.e3ps.sap.service;

import java.io.File;

import com.e3ps.change.EChangeOrder;

import wt.method.RemoteInterface;

@RemoteInterface
public interface SAPService {

	/**
	 * EO / ECO 전송
	 */
	public abstract void sendERP(EChangeOrder eco) throws Exception;

	/**
	 * BOM 샘플 데이터 업로드(운영서버 BOM 다운받은것으로 진행)
	 */
	public abstract void upload(File f) throws Exception;
}
