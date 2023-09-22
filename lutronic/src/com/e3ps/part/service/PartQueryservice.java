package com.e3ps.part.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.method.RemoteInterface;
import wt.query.QuerySpec;

@RemoteInterface
public interface PartQueryservice {

	QuerySpec listPartSearchQuery(HttpServletRequest request, HttpServletResponse response) throws Exception;
	QuerySpec listPartApprovedSearchQuery(HttpServletRequest request, HttpServletResponse response) throws Exception;
	QuerySpec searchSeqAction(Map<String, Object> params) throws Exception;
}
