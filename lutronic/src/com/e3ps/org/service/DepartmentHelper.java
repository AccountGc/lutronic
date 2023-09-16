package com.e3ps.org.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.org.Department;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class DepartmentHelper {
	public static final DepartmentService service = ServiceFactory.getService(DepartmentService.class);
	public static final DepartmentHelper manager = new DepartmentHelper();

	/**
	 * 부서 전체 목록 가져오기 그리드에서 사용
	 */
	public ArrayList<Map<String, String>> getAllDepartmentList() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toOrderBy(query, idx, Department.class, Department.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department department = (Department) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("oid", department.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", department.getName());
			list.add(map);
		}

		return list;
	}

	/**
	 * 최상위 부서 가져오기
	 */
	public Department getRoot() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEquals(query, idx, Department.class, Department.CODE, "ROOT");
		QueryResult result = PersistenceHelper.manager.find(query);
		Department department = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			department = (Department) obj[0];
			return department;
		}
		return null;
	}
}
