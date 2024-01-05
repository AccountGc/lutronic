package com.e3ps.change.ecpr.service;

import com.e3ps.change.ecpr.dto.EcprDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EcprService {
	/**
	 * ECPR 등록
	 */
	public abstract void create(EcprDTO dto) throws Exception;

	/**
	 * ECPR 수정
	 */
	public abstract void modify(EcprDTO dto) throws Exception;

	/**
	 * ECPR 삭제
	 */
	public abstract void delete(String oid) throws Exception;

}
