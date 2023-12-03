package com.e3ps.part.bom.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface BomService {

	/**
	 * BOM 연결 제거
	 */
	public abstract void removeLink(Map<String, String> params) throws Exception;

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
}
