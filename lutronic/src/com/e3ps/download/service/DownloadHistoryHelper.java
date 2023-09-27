package com.e3ps.download.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.download.DownloadHistory;
import com.e3ps.org.dto.PeopleDTO;

import net.sf.json.JSONArray;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class DownloadHistoryHelper {
	public static final DownloadHistoryService service = ServiceFactory.getService(DownloadHistoryService.class);
	public static final DownloadHistoryHelpe manager = new DownloadHistoryHelper();

	/**
	 * 다운로드 이력
	 */
	public JSONArray dLogger(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(DownloadHistory.class, true);
		QuerySpecUtils.toEquals(query, idx, DownloadHistory.class, DownloadHistory.D_OID, oid);
		QuerySpecUtils.toOrderBy(query, idx, DownloadHistory.class, "thePersistInfo.createStamp", false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			DownloadHistory history = (DownloadHistory) obj[0];
			Map<String, Object> map = new HashMap<>();
			WTUser user = history.getUser();
			PeopleDTO dto = new PeopleDTO(user);
			map.put("oid", history.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("count", history.getDCount());
			map.put("name", dto.getName());
			map.put("duty", dto.getDuty());
			map.put("time", history.getPersistInfo().getCreateStamp());
			map.put("departmentName", dto.getDepartment_name());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}
