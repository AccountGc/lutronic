package com.e3ps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import com.e3ps.org.Department;
import com.e3ps.org.People;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.load.LoadUser;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test {

	public static void main(String[] args) throws Exception {

		Department department = null;
		System.out.println("@ syncJuser 시작 @");

		String url = "jdbc:sqlserver://20.20.100.161:14233;databaseName=ZEUS";
		String username = "plm";
		String password = "Pi#szeuS11!";

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		Transaction trs = new Transaction();

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		connection = DriverManager.getConnection(url, username, password);

		statement = connection.createStatement();
		String query = "SELECT * FROM ZEUS.DBO.vw_userinfo_pdm";
		resultSet = statement.executeQuery(query);

		while (resultSet.next()) {
			String userId = resultSet.getString("UserId");
			String userName = resultSet.getString("UserName");
			String groupCode = resultSet.getString("GroupCode");
			String groupName = resultSet.getString("GroupName");
			String email = resultSet.getString("Email");
			String pw = resultSet.getString("Password");

			boolean exist = isUser(userId);
			// 유저 있으면 업데이트
			if (exist) {

				// 없으면 생성
			} else {
				Hashtable hash = new Hashtable<>();
				hash.put("newUser", userId);
				hash.put("webServerID", userId);
				hash.put("fullName", userName);
//                hash.put("Last", last);
				hash.put("Email", email);
				hash.put("Locale", "ko");
				hash.put("Organization", "semi");
				hash.put("password", password);
				hash.put("ignore", "x");

				boolean success = LoadUser.createUser(hash, new Hashtable<>(), new Vector<>());
				// 등록 성공하면 USER 생성
				if (success) {
					People p = getUser(userId);
					WTUser wtuser = getWTUser(userId);

					// USER 테이블 생성
					if (p == null) {
						p = People.newPeople();
//						p.setGroupCode(); // EH
//						p.setGroupName(); // 장비기술팀
						p.setId(userId);
						p.setUser(wtuser);
						p.setDepartment(null);
						p.setName(username);
						PersistenceHelper.manager.save(p);
						// 업데이트
					} else {
						p.setId(userId);
//						p.setGroupCode(); // EH21
//						p.setGroupName(); // 장비기술팀2
						p.setName(username);
						p.setUser(wtuser);
						p.setDepartment(null);
						PersistenceHelper.manager.modify(p);
					}
				}
			}
		}
	}

	public static Department getDepartment(String deptCode) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		SearchCondition sc = new SearchCondition(Department.class, Department.CODE, "=", deptCode);
		query.appendWhere(sc, new int[] { idx });
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			return (Department) obj[0];
		}
		return null;
	}

	public static WTUser getWTUser(String userId) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTUser.class, true);
		SearchCondition sc = new SearchCondition(WTUser.class, WTUser.NAME, "=", userId);
		query.appendWhere(sc, new int[] { idx });
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			return (WTUser) obj[0];
		}
		return null;
	}

	public static People getUser(String userId) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		SearchCondition sc = new SearchCondition(People.class, People.ID, "=", userId);
		query.appendWhere(sc, new int[] { idx });
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			return (People) obj[0];
		}

		return null;
	}

	public static boolean isUser(String userId) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTUser.class, true);
		SearchCondition sc = new SearchCondition(WTUser.class, WTUser.NAME, "=", userId);
		query.appendWhere(sc, new int[] { idx });
		return PersistenceHelper.manager.find(query).size() > 0;
	}
}