package com.e3ps.change.eco.service;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.eco.dto.EcoDTO;
import com.e3ps.change.eo.dto.EoDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EcoService {

	/**
	 * ECO 등록 함수
	 */
	public abstract void create(EcoDTO dto) throws Exception;

	/**
	 * ECO 수정
	 */
	public abstract void modify(EcoDTO dto) throws Exception;

	/**
	 * ECO 상태변경 후 발생 하는 이벤트
	 */
	public abstract void complete(EChangeOrder eco) throws Exception;
}
