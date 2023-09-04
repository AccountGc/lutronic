package com.e3ps.part.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.part.beans.PartTreeData;

import wt.lifecycle.State;
import wt.method.RemoteInterface;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;

@RemoteInterface
public interface BomSearchService {

	List<PartTreeData> getBOM(WTPart part, boolean desc, Baseline baseline)
			throws Exception;

	List<PartTreeData> getBOM(WTPart part, boolean desc, Baseline baseline,
			boolean isTop) throws Exception;

	List<PartTreeData> getBOM(WTPart part, boolean desc, Baseline baseline,
			boolean isTop, String parentId) throws Exception;

	List<PartTreeData> getBOM(WTPart part, boolean desc, Baseline baseline,
			boolean isALL, List<PartTreeData> dataList, boolean isTop,
			String parentId) throws Exception;

	List<Object[]> descentLastPart(WTPart part, View view, State state)
			throws WTException;

	List<Object[]> descentLastPart(WTPart part, Baseline baseline, State state)
			throws WTException;

	List<Object[]> ancestorPart(WTPart part, View view, State state)
			throws WTException;

	List<Object[]> ancestorPart(WTPart part, Baseline baseline, State state)
			throws WTException;
	
	List<Map<String, Object>> getAUIBOMRootChildAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;
	///
	List<Map<String, Object>> getAUIBOMPartChildAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	List<Map<String, Object>> updateAUIBomListGrid(String oid,
			boolean isCheckDummy) throws Exception;

	boolean isChildren(WTPart part, boolean desc, Baseline baseline)
			throws Exception;

	List<Map<String, Object>> updateAUIPartChangeListGrid(String oid,
			boolean isCheckDummy) throws Exception;

	List<Map<String,Object>> partExpandAUIBomListGrid(String oid,String moduleTypes, String desc) throws Exception;
}
