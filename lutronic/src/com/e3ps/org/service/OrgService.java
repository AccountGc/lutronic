package com.e3ps.org.service;

import org.json.JSONObject;
import wt.method.RemoteInterface;

import com.e3ps.org.Department;


@RemoteInterface
public interface OrgService {

	JSONObject getDepartmentTree(Department root) throws Exception;

	public Department makeRoot() throws Exception;

}
