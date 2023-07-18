package com.e3ps.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.beans.ResultData;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.development.devActive;
import com.e3ps.development.devMaster;
import com.e3ps.development.devTask;
import com.e3ps.development.beans.DevActiveData;
import com.e3ps.development.beans.DevTaskData;
import com.e3ps.development.beans.MasterData;
import com.e3ps.development.service.DevelopmentHelper;

@Controller
@RequestMapping("/development")
public class DevelopmentController extends BaseController{
	
	@Description(value = "개발업무 등록")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception{
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/development/development-create.jsp");
		return model;
	}
	
	@Description(value = "나의 개발업무")
	@GetMapping(value = "/my")
	public ModelAndView my() throws Exception{
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/development/my-development.jsp");
		return model;
	}
	
	@Description(value = "나의 개발업무 조회")
	@ResponseBody
	@PostMapping(value = "/my")
	public Map<String,Object> my(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DevelopmentHelper.manager.my(params);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value="개발업무 관리 등록 수행")
	@ResponseBody
	@PostMapping(value = "/create")
	public ResultData create(@RequestBody Map<String, Object> params) {
		//Map<String,String> map = DevelopmentHelper.service.requestDevelopmentMapping(request, response);
		return DevelopmentHelper.service.create(params);
	}
	
	@Description(value = "개발업무 검색")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception{
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/development/development-list.jsp");
		return model;
	}
	
	@Description(value = " 개발업무 관리 검색 수행")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String,Object> list(@RequestBody Map<String, Object> params) {
		Map<String,Object> result = null;
		try {
			result = DevelopmentHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
//	/**  개발업무 관리 검색 수행
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping("/listDevelopmentAction")
//	public Map<String,Object> listDevelopmentAction(HttpServletRequest request, HttpServletResponse response) {
//		Map<String,Object> result = null;
//		try {
//			result = DevelopmentHelper.service.listDevelopmentAction(request, response);
//		} catch(Exception e) {
//			e.printStackTrace();
//			result = new HashMap<String,Object>();
//		}
//		return result;
//	}
//	
	
	/**
	 * 				개발업무 관리 관련 Controll
	 */
	
	
	
	/**  개발업무 관리 상세보기 Body
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/bodyDevelopment")
	public ModelAndView bodyDevelopment(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		ModelAndView model = new ModelAndView();
		model.addObject("module","development");
		model.setViewName("default:/development/bodyDevelopment");
		model.addObject("oid", oid);
		return model;
	}
	
	/**  개발업무 관리 상세 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/viewDevelopment")
	public ModelAndView viewDevelopment(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		ModelAndView model = new ModelAndView();
		devMaster master = (devMaster)CommonUtil.getObject(oid);
		MasterData data = new MasterData(master);
		
		model.setViewName("empty:/development/viewDevelopment");
		model.addObject("masterData", data);
		return model;
	}
	
	/**  개발업무 관리 상세 팝업 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/bodyDevelopmentPopup")
	public ModelAndView viewDevelopmentPopup(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/development/bodyDevelopmentPopup");
		model.addObject("oid", oid);
		return model;
	}
	
	/**  개발업무 관리 삭제 수행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteDevelopmentAction")
	public ResultData deleteDevelopmentAction(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		return DevelopmentHelper.service.deleteDevelopmentAction(oid);
	}
	
	/**  개발업무 관리 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/updateDevelopment")
	public ModelAndView updateDevelopment(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid){
		ModelAndView model = new ModelAndView();
		devMaster master = (devMaster)CommonUtil.getObject(oid);
		MasterData data = new MasterData(master);
		
		model.setViewName("empty:/development/updateDevelopment");
		model.addObject("masterData", data);
		return model;
	}
	
	/**  개발업무 관리 수정 수행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateDevelopmentAction")
	public ResultData updateDevelopmentAction(HttpServletRequest request, HttpServletResponse response) {
		//Map<String,String> map = DevelopmentHelper.service.requestDevelopmentMapping(request, response);
		return DevelopmentHelper.service.updateDevelopmentAction(request, response);
	}
	
	
	/**
	 * 					개발업무 관리 Task 관련 Controll
	 */
	

	/**  개발업무 관리 상세보기 Task 리스트 보기
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/viewTaskList")
	public ModelAndView viewTaskList(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		List<DevTaskData> list = DevelopmentHelper.service.getTaskDataList(oid);
		ModelAndView model = new ModelAndView();
		model.addObject("enAbled", DevelopmentHelper.service.buttonControll(oid));
		model.addObject("list", list);
		model.setViewName("empty:/development/viewTaskList");
		return model;
	}
	
	/**  개발업무 관리 상세보기 Task 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/editTaskList")
	public ModelAndView editTaskList(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		List<DevTaskData> list = DevelopmentHelper.service.getTaskDataList(oid);
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("list", list);
		model.setViewName("empty:/development/editTaskList");
		return model;
	}
	
	/**  개발업무 관리 상세보기 Task 수정 수행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/editTaskAction")
	public ResultData editTaskAction(HttpServletRequest request, HttpServletResponse response) {
		return DevelopmentHelper.service.editTaskAction(request, response);
	}
	
	/**  개발업무 관리 Task 상세보기 Body
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/bodyTask")
	public ModelAndView bodyTask(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid){
		ModelAndView model = new ModelAndView();
		
		model.setViewName("empty:/development/bodyTask");
		model.addObject("oid", oid);
		return model;
	}
	
	/**  개발업무 관리 Task 상세 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/viewTask")
	public ModelAndView viewTask(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		ModelAndView model = new ModelAndView();
		devTask task = (devTask)CommonUtil.getObject(oid);
		DevTaskData data = new DevTaskData(task);
		
		model.setViewName("empty:/development/viewTask");
		model.addObject("devTaskData", data);
		return model;
	}
	
	/**  개발업무 관리 Task 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/updateTask")
	public ModelAndView updateTask(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		ModelAndView model = new ModelAndView();
		devTask task = (devTask)CommonUtil.getObject(oid);
		DevTaskData data = new DevTaskData(task);
		
		model.setViewName("empty:/development/updateTask");
		model.addObject("devTaskData", data);
		return model;
	}
	
	/**  개발업무 관리 Task 삭제 수행
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteTaskAction")
	public ResultData deleteTaskAction(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		return DevelopmentHelper.service.deleteTaskAction(oid);
	}
	
	/**  개발업무 관리 Task 수정 수행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateTaskAction")
	public ResultData updateTaskAction(HttpServletRequest request, HttpServletResponse response) {
		return DevelopmentHelper.service.updateTaskAction(request, response);
	}
	
	
	/**
	 * 						개발업무 관리 Active 관련 Controll
	 */
	
	

	/**  개발업무 관리 Task Active 리스트
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/viewActiveList")
	public ModelAndView viewActiveList(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) {
		List<DevActiveData> list = DevelopmentHelper.service.getActiveDataList(oid);
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("list", list);
		model.addObject("enAbled", DevelopmentHelper.service.buttonControll(oid));
		model.setViewName("empty:/development/viewActiveList");
		return model;
	}
	
	/**  개발업무 관리 Task Active 수정
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/editActiveList")
	public ModelAndView editActiveList(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) {
		List<DevActiveData> list = DevelopmentHelper.service.getActiveDataList(oid);
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("list", list);
		model.setViewName("empty:/development/editActiveList");
		return model;
	}
	
	/**  개발업무 관리 Task Active 수정 수행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/editActiveAction")
	public ResultData editActiveAction(HttpServletRequest request, HttpServletResponse response) {
		return DevelopmentHelper.service.editActiveAction(request, response);
	}
	
	/**  개발업무 관리 Active 상세보기
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/viewActive")
	public ModelAndView viewActive(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		ModelAndView model = new ModelAndView();
		devActive active = (devActive)CommonUtil.getObject(oid);
		DevActiveData data = new DevActiveData(active);
		
		model.setViewName("popup:/development/viewActive");
		model.addObject("devActiveData", data);
		return model;
	}
	
	@RequestMapping("/updateActive")
	public ModelAndView updateActive(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid){
		ModelAndView model = new ModelAndView();
		devActive active = (devActive)CommonUtil.getObject(oid);
		DevActiveData data = new DevActiveData(active);
		
		model.setViewName("popup:/development/updateActive");
		model.addObject("devActiveData", data);
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/updateActiveAction")
	public ResultData updateActiveAction(HttpServletRequest request, HttpServletResponse response) {
		return DevelopmentHelper.service.updateActiveAction(request, response);
	}
	
	@ResponseBody
	@RequestMapping("/changeStateAction")
	public ResultData changeStateAction(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value="oid") String oid,
			@RequestParam(value="state") String state) {
		return DevelopmentHelper.service.changeStateAction(oid, state);
	}
	
	/**  구성원 보기 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/viewUserList")
	public ModelAndView viewUserList(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid){
		
		List<Map<String,String>> list = DevelopmentHelper.service.viewUserList(oid);
		
		ModelAndView model = new ModelAndView();
		model.setViewName("empty:/development/viewUserList");
		model.addObject("list", list);
		return model;
	}
	
	/**  Active 상세보기 Active 삭제
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteActiveAction")
	public ResultData deleteActiveAction(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		return DevelopmentHelper.service.deleteActiveAction(oid);
	}
	
	@RequestMapping("/listMyDevelopment")
	public ModelAndView myDevelopment(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module","development");
		model.setViewName("default:/development/listMyDevelopment");
		return model;
	}
	
//	@ResponseBody
//	@RequestMapping("/listMyDevelopmentAction")
//	public Map<String,Object> myDevelopmentAction(HttpServletRequest request, HttpServletResponse response) {
//		Map<String,Object> result = null;
//		try {
//			result = DevelopmentHelper.service.listMyDevelopmentAction(request, response);
//		} catch(Exception e) {
//			e.printStackTrace();
//			result = new HashMap<String,Object>();
//		}
//		return result;
//	}
	
	@RequestMapping("/include_DevelopmentView")
	public ModelAndView include_DevelopmentView(HttpServletRequest request, HttpServletResponse response) {
		
		String moduleType = StringUtil.checkNull(request.getParameter("moduleType"));
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("관련 개발업무"));
		
		List<DevActiveData> list = null;
		
		try {
			list = DevelopmentHelper.service.include_DevelopmentView(moduleType, oid);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<DevActiveData>();
		}
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/development/include_DevelopmentView");
		model.addObject("list", list);
		model.addObject("title", title);
		return model;
	}
	
	@RequestMapping("/include_DevelopmentSelect")
	public ModelAndView include_DevelopmentSelect(HttpServletRequest request, HttpServletResponse response) {
		String moduleType = StringUtil.checkNull(request.getParameter("moduleType"));
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("관련 개발업무"));
		String paramName = StringUtil.checkReplaceStr(request.getParameter("paramName"), "activeOid");
		List<DevActiveData> list = null;
		
		try {
			list = DevelopmentHelper.service.include_DevelopmentView(moduleType, oid);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<DevActiveData>();
		}
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/development/include_DevelopmentSelect");
		model.addObject("list", list);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		return model;
	}
	
	@RequestMapping("/selectDevelopmentPopup")
	public ModelAndView selectDevelopmentPopup(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/development/selectDevelopmentPopup");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/requestCompleteAction")
	public ResultData requestCompleteAction(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) {
		return DevelopmentHelper.service.requestCompleteAction(oid);
	}
	
	@RequestMapping("/include_viewComment")
	public ModelAndView include_viewComment(HttpServletRequest request, HttpServletResponse response) {
		
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("의견"));
		String enabled = StringUtil.checkReplaceStr(request.getParameter("enabled"), "false");
		
		devActive active = (devActive)CommonUtil.getObject(oid);
		String comment = WebUtil.getHtml(active.getWorker_comment());
		
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("comment", comment);
		model.addObject("enabled", enabled);
		model.setViewName("empty:/development/include_viewComment");
		return model;
	}
	
	@RequestMapping("/include_updateComment")
	public ModelAndView include_updateComment(HttpServletRequest request, HttpServletResponse response) {

		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("의견"));
		
		devActive active = (devActive)CommonUtil.getObject(oid);
		String comment = active.getWorker_comment();
		
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("comment", comment);
		model.setViewName("empty:/development/include_updateComment");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/updatCommentAction")
	public ResultData updatCommentAction(HttpServletRequest request, HttpServletResponse response) {
		return DevelopmentHelper.service.updatCommentAction(request, response);
	}
	
	@RequestMapping("/include_viewWorkerAttach")
	public ModelAndView include_viewWorkerAttach(HttpServletRequest request, HttpServletResponse response) {
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("첨부파일"));
		String enabled = StringUtil.checkReplaceStr(request.getParameter("enabled"), "false");
		
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("enabled", enabled);
		model.setViewName("empty:/development/include_viewWorkerAttach");
		return model;
	}
	
	@RequestMapping("/include_updateWorkerAttach")
	public ModelAndView include_updateWorkerAttach(HttpServletRequest request, HttpServletResponse response) {
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("첨부파일"));
		
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.setViewName("empty:/development/include_updateWorkerAttach");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/updatAttachAction")
	public ResultData updatAttachAction(HttpServletRequest request, HttpServletResponse response) {
		return DevelopmentHelper.service.updatAttachAction(request, response);
	}
}
