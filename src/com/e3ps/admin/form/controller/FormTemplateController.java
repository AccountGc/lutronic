package com.e3ps.admin.form.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.admin.form.FormTemplate;
import com.e3ps.admin.form.dto.FormTemplateDTO;
import com.e3ps.admin.form.service.FormTemplateHelper;
import com.e3ps.admin.form.service.FormTemplateService;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.groupware.notice.service.NoticeHelper;

@Controller
@RequestMapping(value = "/form/**")
public class FormTemplateController extends BaseController {

	@Description(value = "문서 템플릿 등록 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/admin/form/form-list.jsp");
		return model;
	}

	@Description(value = "문서 템플릿 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = FormTemplateHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 템플릿 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> formType = NumberCodeHelper.manager.getArrayCodeList("DOCFORMTYPE");
		model.addObject("formType", formType);
		model.setViewName("/extcore/jsp/admin/form/form-create.jsp");
		return model;
	}

	@Description(value = "문서 템플릿 등록")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			FormTemplateHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 템플릿 양식 가져오기")
	@ResponseBody
	@GetMapping(value = "/html")
	public Map<String, Object> html(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			FormTemplate form = (FormTemplate) CommonUtil.getObject(oid);
			result.put("html", form.getDescription());
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 템플릿 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		FormTemplateDTO dto = new FormTemplateDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/admin/form/form-view.jsp");
		return model;
	}

	@Description(value = "문서 템플릿 수정 페이지")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		FormTemplateDTO dto = new FormTemplateDTO(oid);
		ArrayList<NumberCode> formType = NumberCodeHelper.manager.getArrayCodeList("DOCFORMTYPE");
		model.addObject("dto", dto);
		model.addObject("formType", formType);
		model.setViewName("/extcore/jsp/admin/form/form-modify.jsp");
		return model;
	}

	@Description(value = "문서 템플릿 삭제")
	@ResponseBody
	@PostMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			FormTemplateHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 템플릿 수정")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			FormTemplateHelper.service.modify(params);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 템플릿 가져오기 문서 채번에 맞게")
	@ResponseBody
	@GetMapping(value = "/getHtml")
	public Map<String, Object> getHtml(@RequestParam String clazz) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			FormTemplate form = FormTemplateHelper.manager.getHtml(clazz);
			if (form != null) {
				result.put("html", form.getDescription());
			} else {
				result.put("html", "");
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
