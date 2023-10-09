package com.e3ps.change.cr.service;

import com.e3ps.change.cr.dto.CrDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface CrService {

	/**
	 * CR 등록
	 */
	public abstract void create(CrDTO dto) throws Exception;

	/**
	 * CR 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * CR 수정
	 */
	public abstract void modify(CrDTO dto) throws Exception;
}
