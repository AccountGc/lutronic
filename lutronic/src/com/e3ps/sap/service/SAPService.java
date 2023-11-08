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
	 * BOM 샘플 데이터 업로드(운영서버 BOM 다운받은것으로 진행)
	 */
	public abstract void loaderBom(String path) throws Exception;

	/**
	 * SAP 샘플용 데이터 부품 만들기
	 */
	public abstract WTPart create(String number, String name, String version) throws Exception;

	/**
	 * SAP EO 전송
	 */
	public abstract void sendSapToEo(EChangeOrder e, ArrayList<EOCompletePartLink> completeParts) throws Exception;

	/**
	 * SAP ECO 전송
	 */
	public abstract void sendSapToEco(EChangeOrder e) throws Exception;

	/**
	 * SAP ECN 전송
	 */
	public abstract void sendSapToEcn(Map<String, Object> params) throws Exception;

}
