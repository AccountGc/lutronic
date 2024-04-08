package com.e3ps.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.admin.service.AdminHelper;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.org.Department;
import com.e3ps.org.dto.CompanyState;
import com.e3ps.org.service.MailUserHelper;

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
	
	@Description(value = "다운로드 이력관리 엑셀 다운로드")
	@ResponseBody
	@GetMapping(value = "/excelList")
	public Map<String, Object> excelList() throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = AdminHelper.manager.excelList();
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
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

	@Description(value = "외부 메일 페이지")
	@GetMapping(value = "/mail")
	public ModelAndView mail() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/admin/mail/mail-list.jsp");
		return model;
	}

	@Description(value = "외부 메일 리스트 함수")
	@ResponseBody
	@PostMapping(value = "/mail")
	public Map<String, Object> mail(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = AdminHelper.manager.mail(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "외부 메일 저장")
	@ResponseBody
	@PutMapping(value = "/mailSave")
	public Map<String, Object> mailSave(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MailUserHelper.service.mailSave(params);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
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
