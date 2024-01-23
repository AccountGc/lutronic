package com.e3ps.org.service;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.Signature;
import com.e3ps.org.WTUserPeopleLink;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.load.LoadUser;
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
					// 연결 안된 사용자 삭제.
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

				People user = null;
				if (!_qr.hasMoreElements()) {

					boolean isUser = OrgHelper.manager.isUser(wtuser.getName());
					if (isUser) {
						System.out.println("기존에 이미 등록된 사용자 = " + wtuser.getName() + " [" + wtuser.getFullName() + "]");
						continue;
					}

					// 기존에 생성된 유저 있는지 생성

					user = People.newPeople();
					user.setDepartment(department);
					user.setUser(wtuser);
					user.setPdfAuth(false);
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

	@Override
	public void save(ArrayList<LinkedHashMap<String, Object>> editRows) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (LinkedHashMap<String, Object> edit : editRows) {
				String oid = (String) edit.get("poid");
				String auth = (String) edit.get("auth");
				String department_oid = (String) edit.get("department_oid");
				String duty = (String) edit.get("duty");
				String email = (String) edit.get("email");
				boolean fire = (boolean) edit.get("fire");
//				boolean pdfAuth = (boolean) edit.get("pdfAuth");
				People people = (People) CommonUtil.getObject(oid);

				if (StringUtil.checkString(department_oid)) {
					Department department = (Department) CommonUtil.getObject(department_oid);
					people.setDepartment(department);
				}
				people.setIsDisable(fire);
				people.setEmail(email);
				people.setDuty(duty);
				people.setAuth(auth);
//				people.setPdfAuth(pdfAuth);
				PersistenceHelper.manager.modify(people);

				WTUser user = people.getUser();
				user.setEMail(email);
				PersistenceHelper.manager.modify(user);
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
		}

	}

	@Override
	public void loaderUser(String path) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			File file = new File(path);

			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheetAt(0);

			int rows = sheet.getPhysicalNumberOfRows(); // 시트의 행 개수 가져오기

			// 모든 행(row)을 순회하면서 데이터 가져오기
			for (int i = 1; i < rows; i++) {
				Row row = sheet.getRow(i);

				String email = row.getCell(7).getStringCellValue();
				String name = row.getCell(12).getStringCellValue();
				String last = row.getCell(15).getStringCellValue();
				String id = row.getCell(17).getStringCellValue();

				Hashtable hash = new Hashtable();
				hash.put("newUser", id);
				hash.put("webServerID", id);
				hash.put("fullName", name);
				hash.put("Last", last);
				hash.put("Email", email);
				hash.put("Locale", "ko");
				hash.put("Organization", "lutronic");
				hash.put("password", "changeme");
				hash.put("ignore", "x");
				boolean success = LoadUser.createUser(hash, new Hashtable(), new Vector());
				if (!success) {
					System.out.println("실패한 유저 = " + id);
				}
			}

			workbook.close();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void modify(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String primary = (String) params.get("primary");
		boolean isFire = (boolean) params.get("isFire");
		Transaction trs = new Transaction();
		try {
			trs.start();

			People people = (People) CommonUtil.getObject(oid);
			people.setIsDisable(isFire);
			people = (People) PersistenceHelper.manager.modify(people);

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Signature.class, true);
			QuerySpecUtils.toEquals(query, idx, Signature.class, Signature.ID, people.getId());
			QueryResult rs = PersistenceHelper.manager.find(query);
			if (rs.hasMoreElements()) {
				Object[] obj = (Object[]) rs.nextElement();
				Signature signature = (Signature) obj[0];
				QueryResult qr = ContentHelper.service.getContentsByRole(signature, ContentRoleType.PRIMARY);
				if (qr.hasMoreElements()) {
					ContentItem item = (ContentItem) qr.nextElement();
					ContentServerHelper.service.deleteContent(people, item);
				}
				PersistenceHelper.manager.delete(signature);
			}

			Signature sign = Signature.newSignature();
			sign.setId(people.getId());
			PersistenceHelper.manager.save(sign);

			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
			ApplicationData applicationData = ApplicationData.newApplicationData(sign);
			applicationData.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(sign, applicationData, vault.getPath());

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void loaderSign() throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String signFolder = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "sign";
			File folder = new File(signFolder);
			File[] list = folder.listFiles();
			for (File f : list) {
				if (f.isDirectory()) {
					continue;
				}

				String fname = f.getName();
				int ff = fname.lastIndexOf(".");
				String id = fname.substring(0, ff);

				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(People.class, true);
				QuerySpecUtils.toEquals(query, idx, People.class, People.ID, id);
				QueryResult result = PersistenceHelper.manager.find(query);
				if (result.hasMoreElements()) {
					Object[] obj = (Object[]) result.nextElement();
					People p = (People) obj[0];
					saveSign(p, f.getPath());
				}
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
		}
	}

	/**
	 * 서명 저장
	 */
	private void saveSign(People people, String path) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Signature.class, true);
		QuerySpecUtils.toEquals(query, idx, Signature.class, Signature.ID, people.getId());
		QueryResult rs = PersistenceHelper.manager.find(query);
		if (rs.hasMoreElements()) {
			Object[] obj = (Object[]) rs.nextElement();
			Signature signature = (Signature) obj[0];
			QueryResult qr = ContentHelper.service.getContentsByRole(signature, ContentRoleType.PRIMARY);
			if (qr.hasMoreElements()) {
				ContentItem item = (ContentItem) qr.nextElement();
				ContentServerHelper.service.deleteContent(people, item);
			}
			PersistenceHelper.manager.delete(signature);
		}

		Signature sign = Signature.newSignature();
		sign.setId(people.getId());
		PersistenceHelper.manager.save(sign);

		ApplicationData applicationData = ApplicationData.newApplicationData(sign);
		applicationData.setRole(ContentRoleType.PRIMARY);
		PersistenceHelper.manager.save(applicationData);
		ContentServerHelper.service.updateContent(sign, applicationData, path);
	}
}
