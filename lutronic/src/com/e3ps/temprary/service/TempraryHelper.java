package com.e3ps.temprary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.WCUtil;
import com.e3ps.temprary.column.TempraryColumn;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.IteratedFolderMemberLink;
import wt.lifecycle.LifeCycleManaged;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class TempraryHelper {

	public static final TempraryService service = ServiceFactory.getService(TempraryService.class);
	public static final TempraryHelper manager = new TempraryHelper();

	/**
	 * 임시저장함 검색 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<TempraryColumn> list = new ArrayList<TempraryColumn>();
		ReferenceFactory rf = new ReferenceFactory();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(LifeCycleManaged.class, true);

		
		
		// 단순 상태값으로만??/
		QuerySpecUtils.toState(query, idx, LifeCycleManaged.class, "TEMPRARY");
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TempraryColumn data = new TempraryColumn(obj);
			if(CommonUtil.isLatestVersion(rf.getReference(data.getOid()).getObject())) {
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
