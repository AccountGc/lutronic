package com.e3ps.doc.etc.service;

import com.e3ps.doc.etc.dto.EtcDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EtcService {

	/**
	 * 기타 문서 등록
	 */
	public abstract void create(EtcDTO dto) throws Exception;

}
