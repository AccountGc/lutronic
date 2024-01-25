package com.e3ps.doc.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.beans.ResultData;
import com.e3ps.doc.dto.DocumentDTO;

import wt.doc.WTDocument;
import wt.method.RemoteInterface;

@RemoteInterface
public interface DocumentService {

	ResultData approvalPackageDocumentAction(HttpServletRequest request, HttpServletResponse response);

	/**
	 * 문서 일괄 등록
	 */
	public void batch(Map<String, Object> params) throws Exception;

	/**
	 * 문서 등록
	 */
	public abstract void create(DocumentDTO dto) throws Exception;

	/**
	 * 문서 삭제
	 */
	public abstract Map<String, Object> delete(String oid) throws Exception;

	/**
	 * 문서 개정
	 */
	public abstract void revise(DocumentDTO dto) throws Exception;

	/**
	 * 문서 수정
	 */
	public abstract void modify(DocumentDTO dto) throws Exception;

	/**
	 * 관리자 권한 문서 수정
	 */
	public abstract void force(DocumentDTO dto) throws Exception;

	/**
	 * 문서 이동
	 */
	public abstract void move(Map<String, Object> params) throws Exception;

	/**
	 * 문서 표지 생성
	 */
	public abstract void createCover(WTDocument doc) throws Exception;

	/**
	 * 개발문서 및 지침서
	 */
	public abstract void publish(String oid) throws Exception;

}
