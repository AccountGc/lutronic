package com.e3ps.change.cr.controller;

import java.util.ArrayList; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.admin.form.FormTemplate;
import com.e3ps.admin.form.service.FormTemplateHelper;
import com.e3ps.change.cr.dto.CrDTO;
import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/cr/**")
public class CrController extends BaseController {

	@Description(value = "CR 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("sectionList", sectionList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("deptcodeList", deptcodeList);
		model.setViewName("/extcore/jsp/change/cr/cr-list.jsp");
		return model;
	}

	@Description(value = "CR 검색 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();
		try {
			result = CrHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "관련 CR 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("sectionList", sectionList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/change/cr/cr-list-popup");
		return model;
	}

	@Description(value = "CR 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		FormTemplate form = FormTemplateHelper.manager.getTemplate("변경관리요청서(CR)");
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("sectionList", sectionList);
		model.addObject("html", form==null?"":form.getDescription());
		model.setViewName("/extcore/jsp/change/cr/cr-create.jsp");
		return model;
	}

	@Description(value = "CR 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody CrDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			CrHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "CR 삭제 함수")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			CrHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "CR 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		CrDTO dto = new CrDTO(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/cr/cr-view");
		return model;
	}

	@Description(value = "CR 수정 페이지")
	@GetMapping(value = "/update")
	public ModelAndView update(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		CrDTO dto = new CrDTO(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("sectionList", sectionList);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/cr/cr-update");
		return model;
	}

	@Description(value = "CR 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody CrDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			CrHelper.service.modify(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
