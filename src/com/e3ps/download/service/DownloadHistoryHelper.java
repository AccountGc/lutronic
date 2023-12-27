package com.e3ps.download.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.download.DownloadHistory;
import com.e3ps.download.beans.DownloadDTO;

import net.sf.json.JSONArray;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class DownloadHistoryHelper {

	public static final DownloadHistoryService service = ServiceFactory.getService(DownloadHistoryService.class);
	public static final DownloadHistoryHelper manager = new DownloadHistoryHelper();

	/**
	 * 다운로드 이력
	 */
	public JSONArray dLogger(String oid) throws Exception {
		Persistable per = CommonUtil.getObject(oid);
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(DownloadHistory.class, true);
		QuerySpecUtils.toEquals(query, idx, DownloadHistory.class, "persistReference.key.id", per);
		QuerySpecUtils.toOrderBy(query, idx, DownloadHistory.class, DownloadHistory.CREATE_TIMESTAMP, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			DownloadHistory history = (DownloadHistory) obj[0];
			DownloadDTO dto = new DownloadDTO(history);
			Map<String, Object> map = new HashMap<>();
			map.put("oid", dto.getOid());
			map.put("count", dto.getCnt());
			map.put("userName", dto.getUserName());
			map.put("name", dto.getName());
			map.put("id", dto.getId());
			map.put("duty", dto.getDuty());
			map.put("createdDate", dto.getCreatedDate_txt());
			map.put("department_name", dto.getDepartment_name());
			map.put("info", dto.getInfo());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}
}
