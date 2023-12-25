package com.e3ps.system.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.method.RemoteInterface;

@RemoteInterface
public interface SystemService {

	/**
	 * 파수 업로드 로그
	 */
	public abstract void fasooLogger(String name, HttpServletRequest request) throws Exception;

	/**
	 * SAP 자재 전송 로그
	 */
	public abstract void saveSendPartLogger(Map<String, Object> params) throws Exception;
}
