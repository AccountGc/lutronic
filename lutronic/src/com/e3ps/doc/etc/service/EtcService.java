package com.e3ps.doc.etc.service;

import java.util.Map;

import com.e3ps.doc.etc.dto.EtcDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EtcService {

	/**
	 * 기타 문서 등록
	 */
	public abstract void create(EtcDTO dto) throws Exception;
	
	/**
	 * 기타 문서 삭제
	 */
	public abstract Map<String, Object> delete(String oid) throws Exception;
	
	/**
	 * 기타 문서 개정
	 */
	public abstract void revise(EtcDTO dto) throws Exception;

	/**
	 * 기타 문서 수정
	 */
	public abstract void modify(EtcDTO dto) throws Exception;

}
