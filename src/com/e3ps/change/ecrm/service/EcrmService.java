package com.e3ps.change.ecrm.service;

import com.e3ps.change.ecrm.dto.EcrmDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EcrmService {

	/**
	 * ECRM 등록
	 */
	public abstract void create(EcrmDTO dto) throws Exception;
}
