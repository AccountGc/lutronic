package com.e3ps.sap.service;

import com.e3ps.change.EChangeOrder;

import wt.method.RemoteInterface;
import wt.part.WTPart;

@RemoteInterface
public interface SAPService {

	/**
	 * EO / ECO 전송
	 */
	public abstract void sendERP(EChangeOrder eco) throws Exception;

	/**
	 * BOM 샘플 데이터 업로드(운영서버 BOM 다운받은것으로 진행)
	 */
	public abstract void loaderBom(String path) throws Exception;

	/**
	 * SAP 샘플용 데이터 부품 만들기
	 */
	public abstract WTPart create(String number, String name, String version) throws Exception;
}
