package com.e3ps.workspace.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.workspace.WorkData;
import com.e3ps.workspace.column.WorkDataColumn;

import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
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

		String name = (String) params.get("name");
		String submiterOid = (String) params.get("submiterOid");
		String receiveFrom = (String) params.get("receiveFrom");
		String receiveTo = (String) params.get("receiveTo");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkData.class, true);

		// 관리자가 아니면 자기것만
		if (!CommonUtil.isAdmin()) {
			WTUser user = CommonUtil.sessionUser();
			QuerySpecUtils.toEquals(query, idx, WorkData.class, "ownership.owner.key.id", user);
		}

		QuerySpecUtils.toBooleanAnd(query, idx, WorkData.class, WorkData.PROCESS, false);

		QuerySpecUtils.toOrderBy(query, idx, WorkData.class, WorkData.MODIFY_TIMESTAMP, true);
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

	/**
	 * 결재해야할 개수
	 */
	public int count() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkData.class, true);

		// 관리자가 아니면 자기것만
		if (!CommonUtil.isAdmin()) {
			WTUser user = CommonUtil.sessionUser();
			QuerySpecUtils.toEquals(query, idx, WorkData.class, "ownership.owner.key.id", user);
		}

		QuerySpecUtils.toBooleanAnd(query, idx, WorkData.class, WorkData.PROCESS, false);

		QuerySpecUtils.toOrderBy(query, idx, WorkData.class, WorkData.CREATE_TIMESTAMP, true);
		return PersistenceHelper.manager.find(query).size();
	}

	/**
	 * 결재선지정 객체 가져오기
	 */
	public WorkData getWorkData(Persistable per) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(per, "workData", PerWorkDataLink.class);
		if (qr.hasMoreElements()) {
			WorkData dd = (WorkData) qr.nextElement();
			return dd;
		}
		return null;
	}
}
