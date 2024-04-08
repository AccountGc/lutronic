package com.e3ps.common.history.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aspose.cells.Cell;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.history.LoginHistory;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.dto.PeopleDTO;

import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.OrganizationServicesMgr;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTProperties;

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
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			LoginHistory history = (LoginHistory) o[0];
			Map<String, Object> data = new HashMap<String, Object>();
			WTUser user = OrganizationServicesMgr.getUser(history.getId());
			if (user != null) {
				PeopleDTO dto = new PeopleDTO(user);
				data.put("rowNum", rowNum++);
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

	public Map<String, Object> excelList() throws Exception {
		Map<String, Object> result = new HashMap<>();

		String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
		String path = WTProperties.getServerProperties().getProperty("wt.temp");

		File orgFile = new File(wtHome + "/codebase/com/e3ps/common/history/dto/login_list.xlsx");

		File newFile = CommonUtil.copyFile(orgFile, new File(path + "/접속이력 리스트.xlsx"));

		Workbook workbook = new Workbook(newFile.getPath());
		Worksheet worksheet = workbook.getWorksheets().get(0);
		worksheet.setName("접속이력 리스트"); // 시트 이름

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(LoginHistory.class, true);
		QuerySpecUtils.toOrderBy(query, idx, LoginHistory.class, "thePersistInfo.createStamp", true);

		QueryResult qr = PersistenceHelper.manager.find(query);

		int rowIndex = 1;

		Style center = workbook.createStyle();
		center.setHorizontalAlignment(TextAlignmentType.CENTER);

		Style left = workbook.createStyle();
		left.setHorizontalAlignment(TextAlignmentType.LEFT);

		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			LoginHistory history = (LoginHistory) obj[0];

			WTUser user = OrganizationServicesMgr.getUser(history.getId());
			if (user != null) {
				PeopleDTO dto = new PeopleDTO(user);

				Cell rowCell = worksheet.getCells().get(rowIndex, 0);
				rowCell.setStyle(center);
				rowCell.putValue(rowIndex);

				Cell ipCell = worksheet.getCells().get(rowIndex, 1);
				rowCell.setStyle(center);
				rowCell.putValue(history.getIp());

				Cell nameCell = worksheet.getCells().get(rowIndex, 2);
				nameCell.setStyle(center);
				nameCell.putValue(history.getName());

				Cell idCell = worksheet.getCells().get(rowIndex, 3);
				idCell.setStyle(left);
				idCell.putValue(history.getId());

				Cell dutyCell = worksheet.getCells().get(rowIndex, 4);
				dutyCell.setStyle(center);
				dutyCell.putValue(dto.getDuty());

				Cell departmentCell = worksheet.getCells().get(rowIndex, 5);
				departmentCell.setStyle(center);
				departmentCell.putValue(dto.getDepartment_name());

				Cell createdDateCell = worksheet.getCells().get(rowIndex, 6);
				createdDateCell.setStyle(center);
				createdDateCell.putValue(history.getPersistInfo().getCreateStamp().toString());
				rowIndex++;
			}
		}

		String fullPath = path + "/접속이력 리스트.xlsx";
		workbook.save(fullPath);
		result.put("name", newFile.getName());
		return result;
	}
}
