package com.e3ps.org.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.org.Department;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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

	/**
	 * 갤재선 등록페이지에서 부서 가져오는 함수
	 */
	public JSONArray load900() throws Exception {
		Department root = getRoot();

		JSONArray list = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("id", root.getPersistInfo().getObjectIdentifier().getId());
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("name", root.getName());

		JSONArray children = new JSONArray();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Department.class, "parentReference.key.id",
				root.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, Department.class, Department.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department child = (Department) obj[0];
			JSONObject node = new JSONObject();
			node.put("id", child.getPersistInfo().getObjectIdentifier().getId());
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", child.getName());
			load900(child, node);
			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;

	}

	/**
	 * 갤재선 등록페이지에서 부서 가져오는 재귀 함수
	 */
	private void load900(Department parent, JSONObject parentNode) throws Exception {
		JSONArray children = new JSONArray();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Department.class, "parentReference.key.id",
				parent.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, Department.class, Department.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department child = (Department) obj[0];
			JSONObject node = new JSONObject();
			node.put("id", child.getPersistInfo().getObjectIdentifier().getId());
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", child.getName());
			load900(child, node);
			children.add(node);
		}
		parentNode.put("children", children);
	}
}
