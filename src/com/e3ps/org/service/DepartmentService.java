package com.e3ps.org.service;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.org.Department;

import wt.introspection.WTIntrospectionException;
import wt.method.RemoteInterface;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.util.WTPropertyVetoException;

@RemoteInterface
public interface DepartmentService {

	Department getDepartment(String code);

	ArrayList getAllChildList(Department department, ArrayList returnlist);

	QuerySpec getChildQuerySpec(Department tree)
			throws WTPropertyVetoException, QueryException, WTIntrospectionException;

	QuerySpec getDepartmentPeopleAll(Department dept);

	/**
	 * 부서 트리 저장
	 */
	public void treeSave(Map<String, Object> params) throws Exception;

}
