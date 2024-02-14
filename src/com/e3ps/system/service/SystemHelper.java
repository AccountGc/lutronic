package com.e3ps.system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.system.PrintHistory;
import com.e3ps.system.SAPInterfacePartLogger;
import com.e3ps.system.dto.SendPartLoggerDTO;

import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class SystemHelper {

	public static final SystemService service = ServiceFactory.getService(SystemService.class);
	public static final SystemHelper manager = new SystemHelper();

	public Map<String, Object> bom(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 품목 전송 현황
	 */
	public Map<String, Object> part(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<SendPartLoggerDTO> list = new ArrayList<>();

		String number = (String) params.get("number");
		String eoNumber = (String) params.get("eoNumber");
		String creatorOid = (String) params.get("creatorOid");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SAPInterfacePartLogger.class, true);

		QuerySpecUtils.toOrderBy(query, idx, SAPInterfacePartLogger.class, SAPInterfacePartLogger.CREATE_TIMESTAMP,
				true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			SAPInterfacePartLogger logger = (SAPInterfacePartLogger) obj[0];
			SendPartLoggerDTO data = new SendPartLoggerDTO(logger);
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

	public Map<String, Object> print(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<Map<String, String>> list = new ArrayList<>();

		String ip = (String) params.get("ip");
		String name = (String) params.get("name");
		String departmentName = (String) params.get("departmentName");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PrintHistory.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, PrintHistory.class, PrintHistory.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, PrintHistory.class, PrintHistory.IP, ip);
		QuerySpecUtils.toLikeAnd(query, idx, PrintHistory.class, PrintHistory.DEPARTMENT_NAME, departmentName);
		QuerySpecUtils.toOrderBy(query, idx, PrintHistory.class, PrintHistory.CREATE_TIMESTAMP, true);
		
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PrintHistory h = (PrintHistory) obj[0];
			Map<String, String> m = new HashMap<>();
			m.put("name", h.getName());
			m.put("ip", h.getIp());
			m.put("departmentName", h.getDepartmentName());
			m.put("targetName", h.getTargetName());
			m.put("createdDate", h.getCreateTimestamp().toString().substring(0, 16));
			list.add(m);
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
