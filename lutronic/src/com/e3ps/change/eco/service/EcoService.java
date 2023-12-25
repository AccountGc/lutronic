package com.e3ps.change.eco.service;

import java.util.ArrayList;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.eco.dto.EcoDTO;

import wt.method.RemoteInterface;
import wt.part.WTPart;

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
	public abstract void delete(String oid) throws Exception;

	/**
	 * EO 베이스 라인 저장
	 */
	public abstract void saveBaseLine(WTPart part, EChangeOrder eco) throws Exception;

	/**
	 * 베이스 라인 저장 함수
	 */
	public abstract void saveBaseline(EChangeOrder eco, ArrayList<EOCompletePartLink> completeParts) throws Exception;

	/**
	 * SAP 전송후 전송품목에 대한 상태값 변경
	 */
	public abstract void ecoPartApproved(EChangeOrder eco) throws Exception;
}
