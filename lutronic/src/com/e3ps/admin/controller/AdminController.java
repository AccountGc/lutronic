package com.e3ps.admin.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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

import wt.fc.EnumeratedType;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;

import com.e3ps.admin.service.AdminHelper;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.change.beans.DEFData;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.beans.ROOTData;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.change.service.ECRHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.code.dto.NumberCodeData;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.GenNumberHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ControllerUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.groupware.notice.service.NoticeHelper;
import com.e3ps.org.Department;
import com.e3ps.org.dto.CompanyState;
import com.e3ps.org.service.MailUserHelper;
import com.ptc.core.adapter.server.impl.CollectObjectsWebjectDelegate.OutputType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/admin/**")
public class AdminController extends BaseController {

	/*
	 * 
	 * 부서 관리
	 * 
	 */

	@RequestMapping("/admin_mainCompany")
	public ModelAndView admin_mainCompany(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("admin:/admin/admin_mainCompany");
		model.addObject("module", "part");
		return model;
	}

	@RequestMapping("/admin_menuCompany")
	public ModelAndView admin_menuCompany(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView();
		model.setViewName("include:/admin/admin_menuCompany");
		return model;
	}

	@RequestMapping("/admin_listCompany")
	public ModelAndView admin_listCompany(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView();
		model.setViewName("include:/admin/admin_listCompany");
		return model;
	}

	@RequestMapping("/admin_actionDepartment")
	public ModelAndView admin_actionDepartment(HttpServletRequest request, HttpServletResponse response) {
		try {
			AdminHelper.service.admin_actionDepartment(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/admin/admin_mainCompany.do");
	}

	@ResponseBody
	@RequestMapping("/admin_actionChief")
	public boolean admin_actionChief(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("userOid") String userOid) {
		boolean result = false;
		try {
			result = AdminHelper.service.admin_actionChief(userOid);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	@RequestMapping("/admin_setDuty")
	public ModelAndView admin_setDuty(HttpServletRequest request, HttpServletResponse response) {

		Vector dutyNameList = CompanyState.dutyNameList;
		Vector dutyCodeList = CompanyState.dutyCodeList;

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < dutyCodeList.size(); i++) {
			String dutyCode = (String) dutyCodeList.get(i);
			String dutyName = (String) dutyNameList.get(i);

			Map<String, String> map = new HashMap<String, String>();
			map.put("code", dutyCode);
			map.put("name", dutyName);

			list.add(map);
		}

		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/admin/admin_setDuty");
		model.addObject("list", list);
		return model;
	}

	@ResponseBody
	@RequestMapping("/admin_getDutyListAction")
	public Map<String, List<Map<String, String>>> admin_getDutyListAction(HttpServletRequest request,
			HttpServletResponse response) {
		String duty = StringUtil.checkReplaceStr(request.getParameter("duty"), "");

		Map<String, List<Map<String, String>>> map = null;
		try {
			map = AdminHelper.service.admin_getDutyListAction(duty);
		} catch (Exception e) {
			map = new HashMap<String, List<Map<String, String>>>();
			e.printStackTrace();
		}
		return map;
	}

	@ResponseBody
	@RequestMapping("/admin_setDutyAction")
	public boolean admin_setDutyAction(HttpServletRequest request, HttpServletResponse response) {
		boolean result = false;
		try {
			AdminHelper.service.admin_setDutyAction(request, response);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@RequestMapping("/admin_setDepartment")
	public ModelAndView admin_setDepartment(HttpServletRequest request, HttpServletResponse response) {
		String departmentOid = request.getParameter("departmentOid");

		ModelAndView model = new ModelAndView();
		model.addObject("departmentOid", departmentOid);
		model.addObject("departmentName", ((Department) CommonUtil.getObject(departmentOid)).getName());
		model.setViewName("popup:/admin/admin_setDepartment");
		return model;
	}

	@ResponseBody
	@RequestMapping("/admin_getDepartmentListAction")
	public Map<String, List<Map<String, String>>> admin_getDepartmentListAction(HttpServletRequest request,
			HttpServletResponse response) {
		String oid = StringUtil.checkReplaceStr(request.getParameter("oid"), "");

		Map<String, List<Map<String, String>>> map = null;
		try {
			map = AdminHelper.service.admin_getDepartmentListAction(oid);
		} catch (Exception e) {
			map = new HashMap<String, List<Map<String, String>>>();
			e.printStackTrace();
		}
		return map;
	}

	@ResponseBody
	@RequestMapping("/admin_setDeptAction")
	public boolean admin_setDeptAction(HttpServletRequest request, HttpServletResponse response) {
		boolean result = false;
		try {
			AdminHelper.service.admin_setDeptAction(request, response);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Description(value = "다운로드 이력관리 페이지")
	@GetMapping(value = "/downLoadHistory")
	public ModelAndView downLoadHistory() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/admin/adminDownLoadHistory.jsp");
		return model;
	}

	/*
	 * 
	 * 다운로드 이력관리
	 * 
	 */
//	@RequestMapping("/admin_downLoadHistory")
//	public ModelAndView admin_downLoadHistory(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("admin:/admin/admin_downLoadHistory");
//		model.addObject("module", "download");
//		return model;
//	}

	@Description(value = "다운로드 이력관리 실행")
	@ResponseBody
	@PostMapping(value = "/downLoadHistory")
	public Map<String, Object> downLoadHistory(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = AdminHelper.manager.downLoadHistory(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

//	@ResponseBody
//	@RequestMapping("/admin_downLoadHistoryAction")
//	public Map<String,Object> admin_downLoadHistoryAction(HttpServletRequest request, HttpServletResponse response) {
//		
//		Map<String,Object> map = null;
//		
//		try {
//			map = AdminHelper.service.admin_downLoadHistoryAction(request, response);
//		} catch(Exception e) {
//			map = new HashMap<String,Object>();
//			e.printStackTrace();
//		}
//		
//		return map;
//	}

	@Description(value = "외부 메일 페이지")
	@GetMapping(value = "/adminMail")
	public ModelAndView adminMail() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/admin/adminMail.jsp");
		return model;
	}

	/*
	 * 
	 * 외부 메일
	 * 
	 */
//	@RequestMapping("/admin_mail")
//	public ModelAndView admin_mail(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("admin:/admin/admin_mail");
//		model.addObject("module", "mail");
//		return model;
//	}

	@Description(value = "외부 메일 실행")
	@ResponseBody
	@PostMapping(value = "/adminMail")
	public Map<String, Object> adminMail(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = AdminHelper.manager.adminMail(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

//	@ResponseBody
//	@RequestMapping("/admin_mailAction")
//	public Map<String,Object> admin_mailAction(HttpServletRequest request, HttpServletResponse response) {
//		Map<String,Object> map = null;
//		
//		try {
//			map = AdminHelper.service.admin_mailAction(request, response);
//		} catch(Exception e) {
//			e.printStackTrace();
//			map = new HashMap<String,Object>();
//		}
//		
//		return map;
//	}

	@Description(value = "외부 메일 저장")
	@ResponseBody
	@PostMapping(value = "/adminMailSave")
	public Map<String, Object> adminMailSave(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MailUserHelper.service.adminMailSave(params);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/actionMailUser")
	public Map<String, String> actionMailUser(HttpServletRequest request, HttpServletResponse response) {

		String command = StringUtil.checkNull(request.getParameter("command"));
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String name = StringUtil.checkNull(request.getParameter("name"));
		String email = StringUtil.checkNull(request.getParameter("email"));
		String enable = StringUtil.checkNull(request.getParameter("enable"));

		HashMap map = new HashMap();
		map.put("name", name);
		map.put("email", email);
		map.put("enable", enable);
		map.put("oid", oid);
		map.put("command", command);

		// String msg = "";
		Map<String, String> msg = null;
		if ("create".equals(command)) {
			msg = MailUserHelper.service.createMailUser(map);
		} else if ("update".equals(command)) {
			msg = MailUserHelper.service.modifyMailUser(map);
		} else if ("delete".equals(command)) {
			msg = MailUserHelper.service.deleteMailUser(oid);
		}
		return msg;
	}

	@Description(value = "접속 이력관리 페이지")
	@GetMapping(value = "/loginHistory")
	public ModelAndView loginHistory() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/admin/adminLoginHistory.jsp");
		return model;
	}

	/*
	 * 
	 * 접속 이력관리
	 * 
	 */

//	@RequestMapping("/admin_loginhistory")
//	public ModelAndView admin_loginhistory(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("admin:/admin/admin_loginhistory");
//		model.addObject("module", "loginhistory");
//		return model;
//	}

	@Description(value = "접속 이력관리 실행")
	@ResponseBody
	@PostMapping(value = "/loginHistory")
	public Map<String, Object> loginHistory(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = AdminHelper.manager.loginHistory(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "RootDefinition 수정 페이지")
	@GetMapping(value = "/updateRootDefinition")
	public ModelAndView updateRootDefinition(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EChangeActivityDefinitionRoot root = (EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
		ROOTData rootdata = new ROOTData(root);

		model.addObject("rootdata", rootdata);
		model.setViewName("popup:/admin/rootDefinition-update");
		return model;
	}

	@Description(value = "RootDefinition 수정 실행")
	@ResponseBody
	@PostMapping(value = "/updateRootDefinition")
	public Map<String, Object> updateRootDefinition(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			AdminHelper.service.updateRootDefinition(params);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "RootDefinition 삭제")
	@ResponseBody
	@PostMapping(value = "/deleteRootDefinition")
	public Map<String, Object> deleteRootDefinition(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			AdminHelper.service.deleteRootDefinition(params);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

//	@Description(value = "ActivityDefinition 등록 페이지")
//	@GetMapping(value = "/createActivityDefinition")
//	public ModelAndView createActivityDefinition(@RequestParam String oid) throws Exception {
//		ModelAndView model = new ModelAndView();
//		// eostep 리스트
//		List<NumberCodeData> stepList = NumberCodeHelper.manager.getArrayPartTypeList("EOSTEP", "");
//		// 활동구분 리스트
//		List<Map<String, String>> typeList = ChangeUtil.getActivityTypeList();
//		model.addObject("stepList", stepList);
//		model.addObject("typeList", typeList);
//		model.setViewName("popup:/admin/activityDefinition-create");
//		return model;
//	}


	/**
	 * ActivityDefinition 등록 Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
//	@ResponseBody
//	@RequestMapping("/createActivityDefinitionAction")
//	public ResultData createActivityDefinitionAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		
//		return ECAHelper.service.createActivityDefinitionAction(request);
//	}

	/**
	 * ActivityDefinition 수정 페이지
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updateActivityDefinition")
	public ModelAndView updateActivityDefinition(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		EChangeActivityDefinition def = (EChangeActivityDefinition) CommonUtil.getObject(oid);
		EADData eadData = new EADData(def);
		model.addObject("eadData", eadData);
		model.setViewName("popup:/admin/updateActivityDefinition");
		return model;
	}

	/**
	 * ActivityDefinition 수정 Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/updateActivityDefinitionAction")
	public ResultData updateActivityDefinitionAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return ECAHelper.service.updateActivityDefinitionAction(request);
	}

	/**
	 * ActivityDefinition 삭제 Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
//	@ResponseBody
//	@RequestMapping("/deleteActivityDefinition")
//	public Map<String,Object> deleteActivityDefinition(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		
//		Map<String,Object> map = new HashMap<String,Object>();
//		String deleteOid = request.getParameter("deleteOid");
////		System.out.println("admin deleteRootDefinition oid =" +deleteOid);
//		boolean result = false;
//		String msg = Message.get("삭제 실패하였습니다.");
//		try {
//			msg = ECAHelper.service.deleteActivityDefinition(deleteOid);
//			result = true;
//		}catch(Exception e) {
//			result = false;
//			msg = Message.get("삭제 실패하였습니다.");
//			e.printStackTrace();
//		}
//		map.put("result", result);
//		map.put("msg", msg);
//		
//		return map;
//	}

	@Description(value = "등록 양식 페이지")
	@GetMapping(value = "/adminPackage")
	public ModelAndView adminPackage() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/admin/adminPackage.jsp");
		return model;
	}

//	@RequestMapping("/admin_package")
//	public ModelAndView admin_package(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("admin:/admin/admin_package");
//		model.addObject("module", "package");
//		return model;
//	}

	@RequestMapping("/admin_packageAction")
	public ModelAndView admin_packageAction(HttpServletRequest request, HttpServletResponse response) {
		String message = "";
		try {
			AdminHelper.service.admin_packageAction(request, response);
			message = Message.get("업로드 되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			message = Message.get("업로드 실패하였습니다.") + "\n" + e.getLocalizedMessage();
		}
		return ControllerUtil.messageAlert(message);
	}
}
