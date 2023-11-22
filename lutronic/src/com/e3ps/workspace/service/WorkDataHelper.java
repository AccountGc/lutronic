package com.e3ps.workspace.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.WorkData;
import com.e3ps.workspace.column.ApprovalLineColumn;
import com.e3ps.workspace.column.WorkDataColumn;

import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class WorkDataHelper {

	public static final WorkDataService service = ServiceFactory.getService(WorkDataService.class);
	public static final WorkDataHelper manager = new WorkDataHelper();

	/**
	 * 합의함 조회 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<WorkDataColumn> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkData.class, true);

		QuerySpecUtils.toBooleanAnd(query, idx, WorkData.class, WorkData.PROCESS, false);

		QuerySpecUtils.toOrderBy(query, idx, WorkData.class, WorkData.CREATE_TIMESTAMP, false);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkDataColumn column = new WorkDataColumn(obj);
			list.add(column);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
