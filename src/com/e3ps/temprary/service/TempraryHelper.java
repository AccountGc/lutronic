package com.e3ps.temprary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.temprary.column.TempraryColumn;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTUser;
import wt.part.WTPart;
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

		String number = StringUtil.checkNull((String) params.get("number"));
		String name = StringUtil.checkNull((String) params.get("name"));
		String dataType = StringUtil.checkNull((String) params.get("dataType"));

		if (!CommonUtil.isAdmin()) {
			WTUser sessionUser = CommonUtil.sessionUser();
			SearchCondition sc = new SearchCondition(LifeCycleManaged.class, "ownership.owner.key.id", "=",
					sessionUser.getPersistInfo().getObjectIdentifier().getId());
			query.appendWhere(sc, new int[] { idx });
		}

		QuerySpecUtils.toState(query, idx, LifeCycleManaged.class, "LINE_REGISTER");
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TempraryColumn data = new TempraryColumn(obj);
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

	/**
	 * 객체 마다 주소 가져오기
	 */
	public String getViewUrl(String oid) throws Exception {
		Persistable per = CommonUtil.getObject(oid);
		if (per instanceof WTDocument) {
			WTDocument doc = (WTDocument) per;
			if ("$$MMDocument".equals(doc.getDocType().toString())) {
				// 금형
				return "mold";
			} else if ("$$ROHS".equals(doc.getDocType().toString())) {
				return "rohs";
			} else {
				// 문서
				return "doc";
			}
		} else if (per instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) per;
			if (eco.getEoType().equals("CHANGE")) {
				// ECO
				return "eco";
			} else {
				// EO
				return "eo";
			}
		} else if (per instanceof EChangeRequest) {
			// CR
			return "cr";
		} else if (per instanceof EChangeNotice) {
			// ECN
			return "ecn";
		} else if (per instanceof ECRMRequest) {
			// ECN
			return "ecrm";
		} else if (per instanceof ECPRRequest) {
			// ECPR
			return "ecpr";
		} else if (per instanceof WTPart) {
			// 부품
			return "part";
		} else if (per instanceof EPMDocument) {
			// 부품
			return "drawing";
		}
		return null;
	}
}
