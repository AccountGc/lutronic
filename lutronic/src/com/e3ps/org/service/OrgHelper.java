package com.e3ps.org.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.org.Department;
import com.e3ps.org.People;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class OrgHelper {

	public static final OrgService service = ServiceFactory.getService(OrgService.class);

	public static final OrgHelper manager = new OrgHelper();

	/**
	 * 부서 트리 가져오기
	 */
	public JSONArray tree() throws Exception {
		Department root = getRoot();
		JSONArray list = new JSONArray();
		if (root == null) {
			return list;
		}

		JSONObject rootNode = new JSONObject();
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
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", child.getName());
			tree(child, node);
			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	/**
	 * 부서 트리 가져오기 재귀 함수
	 */
	private void tree(Department parent, JSONObject parentNode) throws Exception {
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
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", child.getName());
			tree(child, node);
			children.add(node);
		}
		parentNode.put("children", children);
	}

	public Department getRoot() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEquals(query, idx, Department.class, Department.CODE, "ROOT");
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department root = (Department) obj[0];
			return root;
		}
		return null;
	}

	/**
	 * AXISJ 파인더
	 */
	public ArrayList<Map<String, String>> finder(Map<String, String> params) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		String value = params.get("value");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		query.appendOpenParen();
		QuerySpecUtils.toBooleanAnd(query, idx, People.class, People.IS_DISABLE, false);
		QuerySpecUtils.toLikeAnd(query, idx, People.class, People.NAME, value);
		QuerySpecUtils.toLikeOr(query, idx, People.class, People.ID, value);
		query.appendCloseParen();
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People people = (People) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("oid", people.getUser().getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", people.getName());
			list.add(map);
		}
		return list;
	}

	/**
	 * 모든 하위 부서 가져오기
	 */
	public ArrayList<Department> getSubDepartment(Department parent, ArrayList<Department> list) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Department.class, "parentReference.key.id", parent);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department child = (Department) obj[0];
			list.add(child);
			getSubDepartment(child, list);
		}
		return list;
	}

	/**
	 * AUI 그리드에서 사용하기 위한함수 JSON 형태로 리턴
	 */
	public JSONArray toJson() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		QuerySpecUtils.toBooleanAnd(query, idx, People.class, People.IS_DISABLE, false);
		QuerySpecUtils.toOrderBy(query, idx, People.class, People.NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People p = (People) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("key", p.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("value", p.getName());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}
}
