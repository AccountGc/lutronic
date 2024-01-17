package com.e3ps.org.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.xml.utils.res.StringArrayWrapper;
import org.mvel2.ast.Sign;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.Signature;
import com.e3ps.org.WTUserPeopleLink;
import com.e3ps.org.dto.PeopleDTO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTProperties;

public class OrgHelper {

	public static final OrgService service = ServiceFactory.getService(OrgService.class);

	public static final OrgHelper manager = new OrgHelper();

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
	 * AUI 그리드에서 사용하기 위한함수 JSON(People) 형태로 리턴
	 */
	public JSONArray toJsonPeople() throws Exception {
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

	/**
	 * AUI 그리드에서 사용하기 위한함수 JSON(WTUser) 형태로 리턴
	 */
	public JSONArray toJsonWTUser() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTUser.class, true);
		QuerySpecUtils.toBooleanAnd(query, idx, WTUser.class, WTUser.DISABLED, false);
		QuerySpecUtils.toBooleanAnd(query, idx, WTUser.class, WTUser.REPAIR_NEEDED, false);
		QuerySpecUtils.toOrderBy(query, idx, WTUser.class, WTUser.FULL_NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTUser u = (WTUser) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("key", u.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("value", u.getFullName());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * AUI 그리드에서 사용하기 위한함수 JSON(WTUser) 형태로 리턴
	 */
	public JSONArray toJsonWTUser(String deptName) throws Exception {
		Department dept = DepartmentHelper.manager.getDepartment(deptName);
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		QuerySpecUtils.toBooleanAnd(query, idx, People.class, People.IS_DISABLE, false);
		QuerySpecUtils.toEqualsAnd(query, idx, People.class, "departmentReference.key.id", dept);
		QuerySpecUtils.toOrderBy(query, idx, People.class, People.NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People p = (People) obj[0];
			WTUser u = p.getUser();
			Map<String, String> map = new HashMap<>();
			map.put("key", u.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("value", u.getFullName());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 부서명 가져오기
	 */
	public String department(String oid) throws Exception {
		WTUser user = (WTUser) CommonUtil.getObject(oid);
		QueryResult result = PersistenceHelper.manager.navigate(user, "people", WTUserPeopleLink.class);
		String department_name = null;
		if (result.hasMoreElements()) {
			People p = (People) result.nextElement();
			department_name = p.getDepartment() != null ? p.getDepartment().getName() : "지정안됨";
		}
		return department_name;
	}

	/**
	 * 결재선 지정 페이지에서 부서별 사용자 가져오기
	 */
	public ArrayList<PeopleDTO> load1000(String oid) throws Exception {
		ArrayList<PeopleDTO> list = new ArrayList<>();
		Department department = null;

		if (StringUtil.checkString(oid)) {
			department = (Department) CommonUtil.getObject(oid);
		} else {
			department = getRoot();
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
//		int idx_d = query.appendClassList(Department.class, false);
//		int idx_w = query.appendClassList(WTUser.class, false);

//		QuerySpecUtils.toInnerJoin(query, People.class, WTUser.class, "userReference.key.id",
//				WTAttributeNameIfc.ID_NAME, idx, idx_w);
//		QuerySpecUtils.toInnerJoin(query, People.class, Department.class, "departmentReference.key.id",
//				WTAttributeNameIfc.ID_NAME, idx, idx_d);
		QuerySpecUtils.toBooleanAnd(query, idx, People.class, People.IS_DISABLE, false);

		query.appendAnd();
		query.appendOpenParen();
		SearchCondition sc = new SearchCondition(People.class, "departmentReference.key.id", "=",
				department.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		ArrayList<Department> departments = OrgHelper.manager.getSubDepartment(department, new ArrayList<Department>());
		for (int i = 0; i < departments.size(); i++) {
			Department sub = (Department) departments.get(i);
			query.appendOr();
			long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(People.class, "departmentReference.key.id", "=", sfid),
					new int[] { idx });
		}
		query.appendCloseParen();
		QuerySpecUtils.toOrderBy(query, idx, People.class, People.NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People people = (People) obj[0];
			PeopleDTO dto = new PeopleDTO(people);
			list.add(dto);
		}
		return list;
	}

	/**
	 * 조직도 리스트
	 */
	public Map<String, Object> organization(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<PeopleDTO> list = new ArrayList<PeopleDTO>();

		String name = (String) params.get("name");
		String userId = (String) params.get("userId");
		String oid = (String) params.get("oid"); // 부서 OID
		boolean isFire = (boolean) params.get("isFire");

		Department department = null;
		if (StringUtil.checkString(oid)) {
			department = (Department) CommonUtil.getObject(oid);
		} else {
			department = getRoot();
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, People.class, People.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, People.class, People.ID, userId);
//		QuerySpecUtils.toEqualsAnd(query, idx, People.class, "departmentReference.key.id", dOid);

		QuerySpecUtils.toBooleanAnd(query, idx, People.class, People.IS_DISABLE, isFire);

		query.appendAnd();
		query.appendOpenParen();
		SearchCondition sc = new SearchCondition(People.class, "departmentReference.key.id", "=",
				department.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		ArrayList<Department> departments = OrgHelper.manager.getSubDepartment(department, new ArrayList<Department>());
		for (int i = 0; i < departments.size(); i++) {
			Department sub = (Department) departments.get(i);
			query.appendOr();
			long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(People.class, "departmentReference.key.id", "=", sfid),
					new int[] { idx });
		}
		query.appendCloseParen();

		QuerySpecUtils.toOrderBy(query, idx, People.class, People.NAME, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People people = (People) obj[0];
			PeopleDTO data = new PeopleDTO(people);
			data.setRowNum(rowNum++);
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
	 * 서명 경로 가져오기
	 */
	public String getSignPath(String id) throws Exception {
		String signPath = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "sign";
		File f = new File(signPath);
		File file = null;
		if (!f.exists()) {
			f.mkdirs();
		}

		SessionHelper.manager.setAdministrator();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Signature.class, true);
		QuerySpecUtils.toEquals(query, idx, Signature.class, Signature.ID, id);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			Signature sign = (Signature) obj[0];

			QueryResult result = ContentHelper.service.getContentsByRole(sign, ContentRoleType.PRIMARY);
			if (result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(data);
				file = new File(signPath + File.separator + id + ".png");
				FileOutputStream fos = new FileOutputStream(file);
				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
			}
		}

		if (file == null) {
			return null;
		}
		return file.getPath();
	}

	/**
	 * 서명 다운로드 주소
	 */
	public String getSignFileName(String id) throws Exception {
		SessionHelper.manager.getAdministrator();
		String path = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator + "extcore"
				+ File.separator + "jsp" + File.separator + "org" + File.separator + "sign";
		File save = new File(path);
		if (!save.exists()) {
			save.mkdirs();
		}
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Signature.class, true);
		QuerySpecUtils.toEquals(query, idx, Signature.class, Signature.ID, id);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			Signature sign = (Signature) obj[0];

			QueryResult rs = ContentHelper.service.getContentsByRole(sign, ContentRoleType.PRIMARY);
			if (rs.hasMoreElements()) {
				ApplicationData dd = (ApplicationData) rs.nextElement();
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(dd);
				File file = new File(path + File.separator + dd.getFileName());
				FileOutputStream fos = new FileOutputStream(file);
				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
				return dd.getFileName();
			}
		}
		return null;
	}
}
