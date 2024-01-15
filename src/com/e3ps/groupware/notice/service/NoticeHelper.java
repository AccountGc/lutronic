package com.e3ps.groupware.notice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.dto.NoticeDTO;
import com.e3ps.rohs.ROHSMaterial;

import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class NoticeHelper {
	public static final NoticeService service = ServiceFactory.getService(NoticeService.class);
	public static final NoticeHelper manager = new NoticeHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<NoticeDTO> list = new ArrayList<>();

		String name = (String) params.get("name");
		String creatorOid = (String) params.get("creatorOid");

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(Notice.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, Notice.class, Notice.TITLE, name);
		// 등록자
		if (creatorOid.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(Notice.class, "owner.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(creatorOid)), new int[] { idx });
		}

		QuerySpecUtils.toOrderBy(query, idx, Notice.class, Notice.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Notice n = (Notice) obj[0];
			NoticeDTO dto = new NoticeDTO(n);
			dto.setRowNum(rowNum++);
			list.add(dto);
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
