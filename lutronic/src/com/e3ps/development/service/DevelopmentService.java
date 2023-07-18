package com.e3ps.development.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.doc.WTDocument;
import wt.method.RemoteInterface;

import com.e3ps.common.beans.ResultData;
import com.e3ps.development.beans.DevActiveData;
import com.e3ps.development.beans.DevTaskData;

@RemoteInterface
public interface DevelopmentService {
	
	Map<String,String> requestDevelopmentMapping(HttpServletRequest request, HttpServletResponse response);
	
	ResultData create(Map<String, Object> params);
	
	Map<String,Object> listDevelopmentAction(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	ResultData deleteDevelopmentAction(String oid);
	
	ResultData updateDevelopmentAction(HttpServletRequest request, HttpServletResponse response);
	
	ResultData editTaskAction(HttpServletRequest request, HttpServletResponse response);

	List<DevTaskData> getTaskDataList(String oid);
	
	List<DevActiveData> getActiveDataList(String oid);
	
	ResultData editActiveAction(HttpServletRequest request, HttpServletResponse response);
	
	ResultData deleteTaskAction(String oid);
	
	ResultData updateTaskAction(HttpServletRequest request, HttpServletResponse response);
	
	ResultData updateActiveAction(HttpServletRequest request, HttpServletResponse response);
	
	ResultData changeStateAction(String oid, String state);
	
	List<Map<String,String>> viewUserList(String oid);
	
	ResultData deleteActiveAction(String oid);
	
	Map<String, Object> listMyDevelopmentAction(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	boolean buttonControll(String oid);

	/**  Activity 상세보기 문서 직접등록
	 * @param document
	 * @param parentOid
	 * @throws Exception 
	 */
	void createDocumentToDevelopmentLink(WTDocument document, String parentOid) throws Exception;

	List<DevActiveData> include_DevelopmentView(String moduleType, String oid)
			throws Exception;

	ResultData requestCompleteAction(String oid);

	ResultData updatCommentAction(HttpServletRequest request, HttpServletResponse response);

	ResultData updatAttachAction(HttpServletRequest request, HttpServletResponse response);
}
