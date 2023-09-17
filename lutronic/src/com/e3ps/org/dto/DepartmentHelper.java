/**
 * @(#) DepartmentHelper.java Copyright (c) e3ps. All rights reserverd
 * 
 * @version 1.00
 * @since jdk 1.4.2
 * @author Cho Sung Ok, jerred@e3ps.com
 */

package com.e3ps.org.dto;

import java.util.ArrayList;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.introspection.WTIntrospectionException;
import wt.org.WTUser;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.Tree;
import com.e3ps.common.util.TreeHelper;
import com.e3ps.org.Department;
import com.e3ps.org.DepartmentPeopleLink;
import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;

public class DepartmentHelper {
	public static final DepartmentHelper manager = new DepartmentHelper();

	protected DepartmentHelper() {
	}

	public ArrayList getAllList() {
		ArrayList returnData = new ArrayList();
		try {
			QuerySpec spec = new QuerySpec();
			Class mainClass = Department.class;
			int mainClassPos = spec.addClassList(mainClass, true);
//            CommonUtil.setOrderBy(spec, mainClass, mainClassPos, Department.CODE, "code", false);
			SearchUtil.setOrderBy(spec, mainClass, mainClassPos, "thePersistInfo.createStamp", "createStamp", false);
			QueryResult qr = PersistenceHelper.manager.find(spec);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				returnData.add(obj[0]);
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return returnData;
	}

	public ArrayList getTopList() {
		ArrayList returnData = new ArrayList();
		try {
			QuerySpec spec = getTopQuerySpec();
			QueryResult qr = PersistenceHelper.manager.find(spec);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				returnData.add(obj[0]);
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return returnData;
	}

	public QuerySpec getTopQuerySpec() throws WTPropertyVetoException, QueryException, WTIntrospectionException {
		QuerySpec spec = new QuerySpec();
		Class mainClass = Department.class;
		int mainClassPos = spec.addClassList(mainClass, true);
		spec.appendWhere(new SearchCondition(mainClass, "parentReference.key.id", SearchCondition.EQUAL, (long) 0),
				mainClassPos);
		// CommonUtil.setOrderBy(spec, mainClass, mainClassPos, Department.CODE, "code",
		// false);
		SearchUtil.setOrderBy(spec, mainClass, mainClassPos, "sort", "sort", false);
		return spec;
	}

	public ArrayList getChildList(Department department) {
		ArrayList returnData = new ArrayList();
		try {
			QuerySpec spec = getChildQuerySpec(department);
			QueryResult qr = PersistenceHelper.manager.find(spec);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				returnData.add(obj[0]);
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return returnData;
	}

	public QuerySpec getChildQuerySpec(Department tree)
			throws WTPropertyVetoException, QueryException, WTIntrospectionException {
		QuerySpec spec = new QuerySpec();
		Class mainClass = Department.class;
		int mainClassPos = spec.addClassList(mainClass, true);
		spec.appendWhere(new SearchCondition(mainClass, "parentReference.key.id", SearchCondition.EQUAL,
				CommonUtil.getOIDLongValue(tree)), mainClassPos);
//        CommonUtil.setOrderBy(spec, mainClass, mainClassPos, Department.CODE, "code", false);
		SearchUtil.setOrderBy(spec, mainClass, mainClassPos, "thePersistInfo.createStamp", "createStamp", false);
		return spec;
	}

	public ArrayList getAllChildList(Department department, ArrayList returnlist) {
		try {
			QuerySpec spec = getChildQuerySpec(department);
			QueryResult qr = PersistenceHelper.manager.find(spec);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				returnlist.add(obj[0]);
				getAllChildList((Department) obj[0], returnlist);
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return returnlist;
	}

	public ArrayList getParentsList(Department department, ArrayList returnlist) {
		Department parent = (Department) department.getParent();
		if (parent != null) {
			returnlist.add(parent);
			getParentsList(parent, returnlist);
		}
		return returnlist;
	}

	public String getDepartmentName(WTUser user) {
		String returnData = "";
		try {
			QueryResult qr = PersistenceHelper.manager.navigate(user, "people", WTUserPeopleLink.class);
			if (qr.hasMoreElements()) {
				People people = (People) qr.nextElement();
				QueryResult subQr = PersistenceHelper.manager.navigate(people, "department",
						DepartmentPeopleLink.class);
				if (subQr.hasMoreElements()) {
					Department department = (Department) subQr.nextElement();
					returnData = department.getName();
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return returnData;
	}

	public Department getDepartment(String code) {
		try {
			QuerySpec spec = new QuerySpec();
			Class mainClass = Department.class;
			int mainClassPos = spec.addClassList(mainClass, true);
			spec.appendWhere(new SearchCondition(mainClass, Department.CODE, SearchCondition.EQUAL, code),
					mainClassPos);
			QueryResult qr = PersistenceHelper.manager.find(spec);
			if (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				return (Department) obj[0];
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Department getDepartment(WTUser user) throws Exception {
		Department department = null;
		QueryResult qr = PersistenceHelper.manager.navigate(user, "people", WTUserPeopleLink.class);
		if (qr.hasMoreElements()) {
			People people = (People) qr.nextElement();
			QueryResult subQr = PersistenceHelper.manager.navigate(people, "department", DepartmentPeopleLink.class);
			if (subQr.hasMoreElements()) {
				department = (Department) subQr.nextElement();
			}
		}
		return department;

	}

	public QueryResult getDepartmentPeople(Department dept) {

		QuerySpec spec = null;
		QueryResult rs = null;
		try {
			String oid = CommonUtil.getOIDString(dept);
			spec = new QuerySpec();
			Class target = People.class;
			int idx = spec.addClassList(target, true);

			if (spec.getConditionCount() > 0)
				spec.appendAnd();
			// 하위 부서도 모두 출력되게 수정 ( 2006.04.04 Choi Seunghwan )
			ArrayList list = TreeHelper.manager.getAllChildList(Department.class, (Tree) CommonUtil.getObject(oid),
					new ArrayList());
			spec.appendOpenParen();
			for (int i = list.size() - 1; i > -1; i--) {
				Department temp = (Department) list.get(i);
				spec.appendWhere(new SearchCondition(target, "departmentReference.key.id", "=",
						CommonUtil.getOIDLongValue(temp)), new int[] { idx });
				spec.appendOr();
			}
			spec.appendWhere(
					new SearchCondition(target, "departmentReference.key.id", "=", CommonUtil.getOIDLongValue(oid)),
					new int[] { idx });
			spec.appendCloseParen();

			if (spec.getConditionCount() > 0)
				spec.appendAnd();
			spec.appendWhere(new SearchCondition(target, People.IS_DISABLE, SearchCondition.IS_FALSE),
					new int[] { idx });

			rs = PersistenceHelper.manager.find(spec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public QueryResult getDepartmentPeople(Department dept, String userName, boolean isValid) {

		QuerySpec spec = null;
		QueryResult rs = null;
		try {

			spec = new QuerySpec();
			Class target = People.class;
			int idx = spec.addClassList(target, true);

			if (dept != null) {
				String oid = CommonUtil.getOIDString(dept);
				ArrayList list = TreeHelper.manager.getAllChildList(Department.class, (Tree) CommonUtil.getObject(oid),
						new ArrayList());
				spec.appendOpenParen();
				for (int i = list.size() - 1; i > -1; i--) {
					Department temp = (Department) list.get(i);
					spec.appendWhere(new SearchCondition(target, "departmentReference.key.id", "=",
							CommonUtil.getOIDLongValue(temp)), new int[] { idx });
					spec.appendOr();
				}
				spec.appendWhere(
						new SearchCondition(target, "departmentReference.key.id", "=", CommonUtil.getOIDLongValue(oid)),
						new int[] { idx });
				spec.appendCloseParen();
			}

			if (isValid) {
				if (spec.getConditionCount() > 0) {
					spec.appendAnd();
				}
				spec.appendWhere(new SearchCondition(target, People.IS_DISABLE, SearchCondition.IS_FALSE),
						new int[] { idx });
			}

			if (userName != null && userName.trim().length() > 0) {
				if (spec.getConditionCount() > 0) {
					spec.appendAnd();
				}
				spec.appendWhere(new SearchCondition(target, People.NAME, SearchCondition.LIKE, userName.trim() + "%"),
						new int[] { idx });
			}

			rs = PersistenceHelper.manager.find(spec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public QuerySpec getDepartmentPeopleAll(Department dept) {
		QuerySpec spec = null;
		try {
			String oid = CommonUtil.getOIDString(dept);
			spec = new QuerySpec();
			Class target = People.class;
			int idx = spec.addClassList(target, true);

			if (spec.getConditionCount() > 0)
				spec.appendAnd();
			// 하위 부서도 모두 출력되게 수정 ( 2006.04.04 Choi Seunghwan )
			ArrayList list = TreeHelper.manager.getAllChildList(Department.class, (Tree) CommonUtil.getObject(oid),
					new ArrayList());
			spec.appendOpenParen();
			for (int i = list.size() - 1; i > -1; i--) {
				Department temp = (Department) list.get(i);
				spec.appendWhere(new SearchCondition(target, "departmentReference.key.id", "=",
						CommonUtil.getOIDLongValue(temp)), new int[] { idx });
				spec.appendOr();
			}
			spec.appendWhere(
					new SearchCondition(target, "departmentReference.key.id", "=", CommonUtil.getOIDLongValue(oid)),
					new int[] { idx });
			spec.appendCloseParen();

			if (spec.getConditionCount() > 0)
				spec.appendAnd();
			spec.appendWhere(new SearchCondition(target, People.IS_DISABLE, SearchCondition.IS_FALSE),
					new int[] { idx });

		} catch (Exception e) {
			e.printStackTrace();
		}
		return spec;
	}

}