package com.e3ps.change.ecrm.controller;

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
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.ecpr.dto.EcprDTO;
import com.e3ps.change.ecpr.service.EcprHelper;
import com.e3ps.change.ecrm.dto.EcrmDTO;
import com.e3ps.change.ecrm.service.EcrmHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.service.WorkspaceHelper;

@Controller
@RequestMapping(value = "/ecrm/**")
public class EcrmController extends BaseController {

	@Description(value = "ECRM 검색 페이지")
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
		model.setViewName("/extcore/jsp/change/ecrm/ecrm-list.jsp");
		return model;
	}

	@Description(value = "ECRM 검색 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();
		try {
			result = EcrmHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECRM 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		FormTemplate form = FormTemplateHelper.manager.getTemplate("설계변경 위험관리 보고서(ECRM)");
		model.addObject("preserationList", preserationList);
		model.addObject("sectionList", sectionList);
		model.addObject("html", form == null ? "" : form.getDescription());
		model.setViewName("/extcore/jsp/change/ecrm/ecrm-create.jsp");
		return model;
	}

	@Description(value = "ECRM 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody EcrmDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcrmHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECRM 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EcrmDTO dto = new EcrmDTO(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		model.addObject("sectionList", sectionList);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/ecrm/ecrm-view");
		return model;
	}

	@Description(value = "ECRM 수정 페이지")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EcrmDTO dto = new EcrmDTO(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		model.addObject("preserationList", preserationList);
		model.addObject("sectionList", sectionList);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/ecrm/ecrm-modify");
		return model;
	}

	@Description(value = "ECRM 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody EcrmDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcrmHelper.service.modify(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECPR 삭제 함수")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			// true 연결 있음
//			if (EcprHelper.manager.isConnect(oid)) {
//				result.put("result", false);
//				result.put("msg", "ECPR과 연결된 CE이 있습니다.");
//				return result;
//			}

			EcrmHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECRM 인쇄하기")
	@GetMapping(value = "/print")
	public ModelAndView print(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		ECRMRequest ecrm = (ECRMRequest) CommonUtil.getObject(oid);
		EcrmDTO dto = new EcrmDTO(ecrm);
		ApprovalMaster m = WorkspaceHelper.manager.getMaster(ecrm);
		ApprovalLine submitLine = WorkspaceHelper.manager.getSubmitLine(m);
		ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLine(m);
		ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(m);
		model.addObject("submitLine", submitLine);
		model.addObject("approvalLines", approvalLines);
		model.addObject("agreeLines", agreeLines);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/ecrm/ecrm-print");
		return model;
	}
}
