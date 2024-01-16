package com.e3ps.part.bom.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface BomService {

	/**
	 * BOM 연결 제거
	 */
	public abstract Map<String, Object> removeLink(Map<String, String> params) throws Exception;

	/**
	 * 체크아웃 취소
	 */
	public abstract Map<String, Object> undocheckout(String oid) throws Exception;

	/**
	 * 체크아웃후 변경된 노드 리턴
	 */
	public abstract Map<String, Object> checkout(String oid) throws Exception;

	/**
	 * 체크인 후 변경된 노드 리턴
	 */
	public abstract Map<String, Object> checkin(String oid) throws Exception;

	/**
	 * BOM 에디터서 복사붙여넣기
	 */
	public abstract Map<String, Object> paste(Map<String, Object> params) throws Exception;

	/**
	 * BOM 기존 품목 교체
	 */
	public abstract Map<String, Object> exist(Map<String, Object> params) throws Exception;

	/**
	 * BOM 드랍
	 */
	public abstract Map<String, Object> drop(Map<String, Object> params) throws Exception;

	/**
	 * BOM 신규 품목 추가
	 */
	public abstract Map<String, Object> append(Map<String, String> params) throws Exception;

	/**
	 * BOM 기존 항목 교체
	 */
	public abstract Map<String, Object> replace(Map<String, String> params) throws Exception;

	/**
	 * BOM 품목 멀티 제거
	 */
	public abstract Map<String, Object> removeMultiLink(Map<String, Object> params) throws Exception;

	/**
	 * 수량 업데이트
	 */
	public abstract Map<String, Object> update(Map<String, Object> params) throws Exception;

	/**
	 * BOM 새품번 저장
	 */
	public abstract Map<String, Object> saveAs(Map<String, Object> params) throws Exception;
}
