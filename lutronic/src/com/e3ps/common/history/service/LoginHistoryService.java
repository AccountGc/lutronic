package com.e3ps.common.history.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.method.RemoteInterface;

@RemoteInterface
public interface LoginHistoryService {

	public abstract Map<String, Object> create(String j_username, HttpServletRequest request) throws Exception;
}
