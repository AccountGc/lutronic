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
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class NoticeHelper {
	public static final NoticeService service = ServiceFactory.getService(NoticeService.class);
	public static final NoticeHelper manager = new NoticeHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		long start = System.currentTimeMillis() / 1000;
		System.out.println("공지사항 쿼리 시작 = " + start);
		Map<String, Object> map = new HashMap<>();
		ArrayList<NoticeDTO> list = new ArrayList<>();

		String name = (String) params.get("name");
		String creatorOid = (String) params.get("creatorOid");

		// 정렬
		String sortKey = (String) params.get("sortKey");
		String sortType = (String) params.get("sortType");

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

		boolean sort = QuerySpecUtils.toSort(sortType);
		QuerySpecUtils.toOrderBy(query, idx, Notice.class, toSortKey(sortKey), sort);

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
		long end = System.currentTimeMillis() / 1000;
		System.out.println("공지사항 쿼리 종료 = " + end + ", 걸린 시간 = " + (end - start));
		return map;
	}

	private String toSortKey(String sortKey) throws Exception {
		if ("title".equals(sortKey)) {
			return Notice.TITLE;
		}
		return Notice.CREATE_TIMESTAMP;
	}

	/**
	 * 팝업 형태의 공지사항
	 */
	public ArrayList<NoticeDTO> cookie() throws Exception {
		ArrayList<NoticeDTO> list = new ArrayList<NoticeDTO>();
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(Notice.class, true);
		query.appendWhere(new SearchCondition(Notice.class, Notice.IS_POPUP, SearchCondition.IS_TRUE),
				new int[] { idx });
		QueryResult rt = PersistenceHelper.manager.find(query);
		while (rt.hasMoreElements()) {
			Object[] o = (Object[]) rt.nextElement();
			Notice notice = (Notice) o[0];
			NoticeDTO data = new NoticeDTO(notice);
			list.add(data);
		}
		return list;
	}
}
