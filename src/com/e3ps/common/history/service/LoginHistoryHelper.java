package com.e3ps.common.history.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.common.history.LoginHistory;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.org.dto.PeopleDTO;

import wt.fc.PagingQueryResult;
import wt.org.OrganizationServicesMgr;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class LoginHistoryHelper {

	public static final LoginHistoryService service = ServiceFactory.getService(LoginHistoryService.class);
	public static final LoginHistoryHelper manager = new LoginHistoryHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String userName = (String) params.get("userName");
		String userId = (String) params.get("userId");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(LoginHistory.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, LoginHistory.class, LoginHistory.NAME, userName);
		QuerySpecUtils.toLikeAnd(query, idx, LoginHistory.class, LoginHistory.ID, userId);
		QuerySpecUtils.toOrderBy(query, idx, LoginHistory.class, "thePersistInfo.createStamp", true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			LoginHistory history = (LoginHistory) o[0];
			Map<String, Object> data = new HashMap<String, Object>();
			WTUser user = OrganizationServicesMgr.getUser(history.getId());
			if (user != null) {
				PeopleDTO dto = new PeopleDTO(user);
				data.put("oid", history.getPersistInfo().getObjectIdentifier().toString());
				data.put("ip", history.getIp());
				data.put("name", history.getName());
				data.put("id", history.getId());
				data.put("duty", dto.getDuty());
				data.put("department_name", dto.getDepartment_name());
				data.put("createDate", history.getPersistInfo().getCreateStamp().toString());
				list.add(data);
			}
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
