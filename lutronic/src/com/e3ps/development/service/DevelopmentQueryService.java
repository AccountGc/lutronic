package com.e3ps.development.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.enterprise.RevisionControlled;
import wt.method.RemoteInterface;
import wt.query.QuerySpec;

import com.e3ps.development.devActive;
import com.e3ps.development.devOutPutLink;
import com.e3ps.development.devTask;
import com.e3ps.development.beans.DevActiveData;

@RemoteInterface
public interface DevelopmentQueryService {

	QuerySpec listDevelopmentSearchQuery(HttpServletRequest request, HttpServletResponse response);

	QuerySpec getTaskListFormMasterOid(String oid);

	QuerySpec getActiveListFromTaskOid(String oid);
	
	QuerySpec listMyDevelopmentSearchQuery(HttpServletRequest request, HttpServletResponse response);
	
	QuerySpec getDevelopmentUsers(String oid);

	boolean isTaskDelete(devTask task);
	
	boolean isTaskDelete(String oid);
	
	boolean isActiveDelete(devActive active);
	
	boolean isActiveDelete(String oid);

	List<DevActiveData> getActiveDataFromRevisionControlled(RevisionControlled rev) throws Exception;

	List<devOutPutLink> getDevOutPutLinkFromRevisionControlled(RevisionControlled rev) throws Exception;
}
