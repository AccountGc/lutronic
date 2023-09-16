package com.e3ps.groupware.notice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.beans.NoticeData;

import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class NoticeHelper {
	public static final NoticeService service = ServiceFactory.getService(NoticeService.class);
	public static final NoticeHelper manager = new NoticeHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<NoticeData> list = new ArrayList<>();

		String name = (String) params.get("name");
		String creatorOid = (String) params.get("creatorOid");

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(Notice.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, Notice.class, Notice.TITLE, name);
		QuerySpecUtils.toCreator(query, idx, Notice.class, creatorOid);

		QuerySpecUtils.toOrderBy(query, idx, Notice.class, Notice.CREATE_TIMESTAMP, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NoticeData data = new NoticeData((Notice) obj[0]);
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
