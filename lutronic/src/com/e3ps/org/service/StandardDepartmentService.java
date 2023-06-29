package com.e3ps.org.service;

import java.util.ArrayList;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.introspection.WTIntrospectionException;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.Tree;
import com.e3ps.common.util.service.TreeHelper;
import com.e3ps.org.Department;
import com.e3ps.org.People;

public class StandardDepartmentService extends StandardManager implements
		DepartmentService {

	public static StandardDepartmentService newStandardDepartmentService()
			throws Exception {
		final StandardDepartmentService instance = new StandardDepartmentService();
		instance.initialize();
		return instance;
	}

	@Override
	public Department getDepartment(String code) {
		try {
			QuerySpec spec = new QuerySpec();
			Class mainClass = Department.class;
			int mainClassPos = spec.addClassList(mainClass, true);
			spec.appendWhere(new SearchCondition(mainClass, Department.CODE, SearchCondition.EQUAL, code), mainClassPos);
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

	@Override
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
	
	@Override
	public QuerySpec getChildQuerySpec(Department tree) throws WTPropertyVetoException, QueryException, WTIntrospectionException {
		QuerySpec spec = new QuerySpec();
		Class mainClass = Department.class;
		int mainClassPos = spec.addClassList(mainClass, true);
		spec.appendWhere(new SearchCondition(mainClass, "parentReference.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(tree)), mainClassPos);
		SearchUtil.setOrderBy(spec, mainClass, mainClassPos, "thePersistInfo.createStamp", "createStamp", false);
		return spec;
	}
	
	@Override
	public QuerySpec getDepartmentPeopleAll(Department dept) {
    	QuerySpec spec = null;
        try {
            String oid = CommonUtil.getOIDString(dept);
            spec = new QuerySpec();
            Class target = People.class;
            int idx = spec.addClassList(target, true);

            if (spec.getConditionCount() > 0) spec.appendAnd();
            // 하위 부서도 모두 출력되게 수정 ( 2006.04.04 Choi Seunghwan )
            ArrayList list = TreeHelper.service.getAllChildList(Department.class, (Tree) CommonUtil.getObject(oid), new ArrayList());
            spec.appendOpenParen();
            for (int i = list.size() - 1; i > -1; i--)
            {
                Department temp = (Department) list.get(i);
                spec.appendWhere(new SearchCondition(target, "departmentReference.key.id", "=", CommonUtil.getOIDLongValue(temp)), new int[] { idx });
                spec.appendOr();
            }
            spec.appendWhere(new SearchCondition(target, "departmentReference.key.id", "=", CommonUtil.getOIDLongValue(oid)), new int[] { idx });
            spec.appendCloseParen();

            if (spec.getConditionCount() > 0) spec.appendAnd();
            spec.appendWhere(new SearchCondition(target, People.IS_DISABLE, SearchCondition.IS_FALSE), new int[] { idx });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return spec;
    }
}
