package com.e3ps.change.ecn.service;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EcnService {

	/**
	 * ECN 삭제
	 */
	public abstract void delete(String oid) throws Exception;


	/**
	 * ECN 담당자 저장 RA팀 업무
	 */
	public abstract void save(Map<String, Object> params) throws Exception;

	/**
	 * ECO 결재 후 ECN 자동 생성
	 */
	public abstract void create(EChangeOrder eco, ArrayList<EcoPartLink> ecoParts,
			ArrayList<EOCompletePartLink> completeParts) throws Exception;

}
