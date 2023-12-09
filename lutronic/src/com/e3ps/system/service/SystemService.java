package com.e3ps.system.service;

import javax.servlet.http.HttpServletRequest;

import wt.method.RemoteInterface;

@RemoteInterface
public interface SystemService {

	public abstract void fasooLogger(String name, HttpServletRequest request) throws Exception;
}
