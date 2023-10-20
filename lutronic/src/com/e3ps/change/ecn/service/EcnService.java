package com.e3ps.change.ecn.service;

import com.e3ps.change.ecn.dto.EcnDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EcnService {

	/**
	 * ECN 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * ECO 완료시 ECN 자동생성
	 */
	public abstract void create(EcnDTO dto) throws Exception;
}
