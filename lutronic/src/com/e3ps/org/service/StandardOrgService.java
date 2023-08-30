package com.e3ps.org.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.MethodContext;
import wt.org.OrganizationServicesMgr;
import wt.org.WTUser;
import wt.pom.DBProperties;
import wt.pom.Transaction;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTProperties;

public class StandardOrgService extends StandardManager implements OrgService {

	public static StandardOrgService newStandardOrgService() throws Exception {
		final StandardOrgService instance = new StandardOrgService();
		instance.initialize();
		return instance;
	}

	static String dataStore = "Oracle"; // SQLServer ....
	static {
		try {
			dataStore = WTProperties.getLocalProperties().getProperty("wt.db.dataStore");
		} catch (Exception ex) {
			dataStore = "Oracle";
		}
	}

	protected synchronized void performStartupProcess() throws ManagerException {
		super.performStartupProcess();
		try {

			System.out.println("루트 부서 생성 시작!");
			Department department = makeRoot();
			inspectUser(department);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void inspectUser(Department department) throws Exception {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec qs = new QuerySpec();
			int _idx = qs.appendClassList(People.class, true);
			QuerySpecUtils.toOrderBy(qs, _idx, People.class, People.NAME, true);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				People u = (People) obj[0];
				String id = u.getId();
				WTUser user = OrganizationServicesMgr.getUser(id);
				if (user == null) {
					PersistenceHelper.manager.delete(u);
				}
			}

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(WTUser.class, true);
			QuerySpecUtils.toBooleanAnd(query, idx, WTUser.class, WTUser.DISABLED, false);
			QuerySpecUtils.toBooleanAnd(query, idx, WTUser.class, WTUser.REPAIR_NEEDED, false);
			QuerySpecUtils.toOrderBy(query, idx, WTUser.class, WTUser.FULL_NAME, false);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTUser wtuser = (WTUser) obj[0];

				QueryResult _qr = PersistenceHelper.manager.navigate(wtuser, "people", WTUserPeopleLink.class);

				System.out.println("_qr=" + _qr.size() + ", name = " + wtuser.getName());

				People user = null;
				if (!_qr.hasMoreElements()) {
					user = People.newPeople();
					user.setDepartment(department);
					user.setUser(wtuser);
					user.setName(wtuser.getFullName());
					user.setId(wtuser.getName());
					user.setEmail(wtuser.getEMail() != null ? wtuser.getEMail() : "");
					user = (People) PersistenceHelper.manager.save(user);
				}
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
	}

	@Override
	public Department makeRoot() throws Exception {
		Department department = null;
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Department.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, Department.class, Department.CODE, "ROOT");
			QueryResult result = PersistenceHelper.manager.find(query);
			// 루트 부서가 있을 경우
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				department = (Department) obj[0];
			} else {
				// 루트 부서가 없을 경우
				department = Department.newDepartment();
				department.setName("루트로닉");
				department.setCode("ROOT");
				department.setSort(0);
				department = (Department) PersistenceHelper.manager.save(department);
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
		return department;
	}

	@Override
	public JSONObject getDepartmentTree(Department root) throws Exception {

		MethodContext methodcontext = null;
		WTConnection wtconnection = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		JSONObject deptList = new JSONObject();

		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

			StringBuffer sb = null;

			sb = new StringBuffer();

			sb.append("			SELECT LEVEL, 																		");
			sb.append("				NAME, 																			");
			sb.append("				classnameA2A2 ||':' ||idA2A2 oid, 												");
			sb.append("				CODE, 																			");
			sb.append("				SORT, 																			");
			sb.append("				ida2a2 id,																		");
			sb.append("				idA3parentReference pid, 														");
			sb.append("  			CONNECT_BY_ISLEAF AS isleaf 													");
			sb.append("			FROM Department 																	");
			sb.append("  			START WITH idA3parentReference = ? 												");
			sb.append("  			CONNECT BY prior idA2A2        = idA3parentReference 							");
			sb.append("			ORDER SIBLINGS BY SORT	 															");

			st = con.prepareStatement(sb.toString());
			st.setLong(1, root.getPersistInfo().getObjectIdentifier().getId());

			rs = st.executeQuery();

			String rootName = root.getName();
			String mainID = "1";
			int mainLevel = 0;

			JSONObject topList = new JSONObject();
			// String rootCode = "1"; // 대성 (1) : Tree구성을 위해 임의로 code 지정함
			topList.put("id", mainID);
			topList.put("text", rootName);
			topList.put("oid", root.getPersistInfo().getObjectIdentifier().toString());
			topList.put("isLeaf", "0");
			topList.put("parentCode", "0");
			topList.put("levelCnt", mainLevel);
			topList.put("code", root.getCode());
			topList.put("pcode", "0");
			topList.put("sort", root.getSort());
			topList.put("isLeaf", 0);
			topList.put("item", new JSONArray());

			// deptList.put("text" , rootName );

			// Tree에 등록될 순서별로 데이터를 담을 Map
			Map treeMap = new HashMap();

			JSONObject jsonParentData = null;
			JSONArray jsonListData = new JSONArray();
			jsonListData.put(topList);

			// Tree구성을 위해 추가
			deptList.put("id", "0");
			deptList.put("item", jsonListData);

			treeMap.put("0", deptList);
			treeMap.put(mainID, topList);

			while (rs.next()) {
				String id = rs.getString("ID");
				String name = rs.getString("NAME");
				String oid = rs.getString("OID");
				String parentOid = rs.getString("PID");
				String code = rs.getString("CODE");
				String sort = rs.getString("SORT");
				int level = rs.getInt("LEVEL");
				int isLeaf = rs.getInt("ISLEAF");

				JSONObject jsonData = new JSONObject();
				jsonData.put("id", id);
				jsonData.put("text", name);
				jsonData.put("oid", oid);
				jsonData.put("level", level);
				jsonData.put("parentOid", parentOid);
				jsonData.put("code", code);
				jsonData.put("pcode",
						((Department) CommonUtil.getObject("com.e3ps.org.Department:" + parentOid)).getCode());
				jsonData.put("sort", sort);
				jsonData.put("isLeaf", isLeaf);

				// 하위를 가지는 정보를 Map에 넣어준다.
				if (isLeaf == 0) {
					JSONArray jsonSubListData = new JSONArray();
					jsonData.put("item", jsonSubListData);
					treeMap.put(id, jsonData);
				}

				if (level == 1) {
					parentOid = mainID;
				}

				// 해당 데이터의 Parent 정보를 가져와 데이터를 추가한다.
				jsonParentData = (JSONObject) treeMap.get(parentOid);
				jsonListData = jsonParentData.getJSONArray("item");
				if (jsonListData == null) {
					jsonListData = new JSONArray();
				}

				// 해당 데이터를 추가한다.
				jsonListData.put(jsonData);
				jsonParentData.put("item", jsonListData);

				// 수정된 정보를 Map에 다시 넣어준다.
				treeMap.put(parentOid, jsonParentData);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}

		return deptList;
	}

}
