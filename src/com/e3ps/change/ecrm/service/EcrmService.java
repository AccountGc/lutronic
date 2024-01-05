package com.e3ps.change.ecrm.service;

import com.e3ps.change.ecrm.dto.EcrmDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EcrmService {

	/**
	 * ECRM 등록
	 */
	public abstract void create(EcrmDTO dto) throws Exception;

	/**
	 * ECRM 수정
	 */
	public abstract void modify(EcrmDTO dto) throws Exception;

	/**
	 * ECRM 삭제
	 */
	public abstract void delete(String oid) throws Exception;
}
