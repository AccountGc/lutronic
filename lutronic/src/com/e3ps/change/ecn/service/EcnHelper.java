package com.e3ps.change.ecn.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.cr.column.CrColumn;
import com.e3ps.change.ecn.column.EcnColumn;
import com.e3ps.common.util.PageQueryUtils;

import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class EcnHelper {

	public static final EcnService service = ServiceFactory.getService(EcnService.class);
	public static final EcnHelper manager = new EcnHelper();

	/**
	 * ECN 검색 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<EcnColumn> list = new ArrayList<>();

		String name = (String) params.get("name");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeNotice.class, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EcnColumn data = new EcnColumn(obj);
			list.add(data);
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
