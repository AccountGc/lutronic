package com.e3ps.org.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;
import com.e3ps.org.dto.PeopleDTO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
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

	public Map<String, Object> specify(String oid) throws Exception {
		ArrayList<PeopleDTO> list = new ArrayList<>();
		Map<String, Object> result = new HashMap<>();

		Department departmen = (Department) CommonUtil.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, People.class, "departmentReference.key.id", departmen);
		QuerySpecUtils.toOrderBy(query, idx, People.class, People.NAME, false);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			PeopleDTO dto = new PeopleDTO(obj);
			list.add(dto);
		}

		result.put("list", list);
		return result;
	}

	/**
	 * 사용자 부서 가져온느 함수
	 */
	public Department getDepartment(WTUser user) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(user, "people", WTUserPeopleLink.class);
		Department dept = null;
		if (qr.hasMoreElements()) {
			People p = (People) qr.nextElement();
			dept = p.getDepartment();
		}
		return dept;
	}

	/**
	 * RA 문서 관리 권한 여부
	 */
	public boolean isRa(People people, String[] authList) throws Exception {
		return isAuth(people, authList);
	}

	/**
	 * 생산본수 문서 권한
	 */
	public boolean isProduction(People people, String[] authList) throws Exception {
		return isAuth(people, authList);
	}

	/**
	 * 화장품 문서 권한
	 */
	public boolean isCosmetic(People people, String[] authList) throws Exception {
		return isAuth(people, authList);
	}

	/**
	 * 병리연구 문서 권한
	 */
	public boolean isPathological(People people, String[] authList) throws Exception {
		return isAuth(people, authList);
	}

	/**
	 * 임삼개발 문서 권한
	 */
	public boolean isClinical(People people, String[] authList) throws Exception {
		return isAuth(people, authList);
	}

	/**
	 * 문서 권한 여부 체크
	 */
	private boolean isAuth(People people, String[] authList) throws Exception {
		Department dept = people.getDepartment();
		if (dept == null) {
			return false;
		} else {
			String deptName = dept.getName();
			for (String auth : authList) {
				if (auth.trim().equals(deptName)) {
					return true;
				}
			}
		}
		return false;
	}

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
		rootNode.put("isNew", false);
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("name", root.getName());
		rootNode.put("location", "루트로닉");

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
			node.put("isNew", false);
			node.put("poid", root.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", child.getName());
			node.put("location", "루트로닉/" + child.getName());
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
		String pLoc = (String) parentNode.getString("location");
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department child = (Department) obj[0];
			JSONObject node = new JSONObject();
			node.put("isNew", false);
			node.put("poid", parent.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", child.getName());
			node.put("location", pLoc + "/" + child.getName());
			tree(child, node);
			children.add(node);
		}
		parentNode.put("children", children);
	}

	/**
	 * 부서 명으로 부서 찾기
	 */
	public Department getDepartment(String deptName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEquals(query, idx, Department.class, Department.NAME, deptName);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			Department dept = (Department) obj[0];
			return dept;
		}
		return null;
	}

	/**
	 * 부서명 체크
	 */
	public boolean exist(String name) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEquals(query, idx, Department.class, Department.NAME, name);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.size() > 0) {
			return true;
		}
		return false;
	}
}
