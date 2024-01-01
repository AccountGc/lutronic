package com.e3ps.change.eo.service;

import java.util.ArrayList;
import java.util.Hashtable;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.eo.dto.EoDTO;

import wt.method.RemoteInterface;
import wt.part.WTPart;

@RemoteInterface
public interface EoService {

	/**
	 * EO 등록 함수
	 */
	public abstract void create(EoDTO dto) throws Exception;

	/**
	 * EO 삭제 함수
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * EO 수정
	 */
	public abstract void modify(EoDTO dto) throws Exception;

	/**
	 * EO 베이스 라인 저장
	 */
	public abstract void saveBaseLine(WTPart part, EChangeOrder eo) throws Exception;

	/**
	 * 베이스 라인 저장 함수
	 */
	public abstract void saveBaseline(EChangeOrder eo, ArrayList<EOCompletePartLink> completeParts) throws Exception;

	/**
	 * SAP 전송완료시 모든 품목 상태값 변경 처리
	 */
	public abstract void eoPartApproved(EChangeOrder eo, ArrayList<WTPart> list) throws Exception;

}
