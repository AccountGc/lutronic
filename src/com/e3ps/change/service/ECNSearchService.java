package com.e3ps.change.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;

import wt.query.QuerySpec;

public interface ECNSearchService {

	Map<String, Object> listECNAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	QuerySpec getECNQuery(HttpServletRequest req) throws Exception;

	EChangeNotice getECN(EChangeOrder eco);


}
