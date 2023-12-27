package com.e3ps.common.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.beans.ResultData;

import wt.iba.value.IBAHolder;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface CommonService {

	Map<String, Object> downloadHistory(Map<String, Object> params) throws Exception;

	Map<String, String> getAttributes(String oid) throws Exception;
	
	Map<String, String> getAttributes(String oid, String mode) throws Exception;

	Map<String, Object> versionHistory(String oid) throws Exception;

	void createLoginHistoty();

	List<Map<String, String>> include_Notice() throws Exception;

	List<Map<String, String>> include_Approve() throws Exception;

	List<Map<String, String>> include_Document() throws Exception;

	List<Map<String, String>> include_Drawing(HttpServletRequest request) throws Exception;
	
	void changeIBAValues(IBAHolder ibaHolder, Map<String,Object> map) throws Exception;

	List<Map<String,String>> documentTypeList(HttpServletRequest request, HttpServletResponse response);
	
	Map<String,String> setRequestParamToMap(HttpServletRequest request);

	void setManagedDefaultSetting(LifeCycleManaged lm, String location, String lifecycle) throws Exception;

	List<Map<String, String>> include_MyDevelopment(HttpServletRequest request, HttpServletResponse response) throws Exception;

	List<Map<String,String>> autoSearchUserName(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void copyInstanceIBA(IBAHolder ibaHolder, Map<String, Object> map)
			throws Exception;

	void doTempDelete(boolean uploadFolder) throws WTException;

	void copyIBAAttributes(IBAHolder fromIba, IBAHolder toIba) throws Exception;

	ResultData withDrawAction(String oid, boolean isInit);

	ResultData batchSecondaryDown(HttpServletRequest request,HttpServletResponse response);
	
	public Map<String, Object> withDraw(Map<String, Object> params) throws Exception;
}
