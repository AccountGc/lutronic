package com.e3ps.doc.etc.controller;

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
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.DocumentCRLink;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.etc.dto.EtcDTO;
import com.e3ps.doc.etc.service.EtcHelper;
import com.e3ps.doc.service.DocumentHelper;

import net.sf.json.JSONArray;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.part.WTPartDescribeLink;

@Controller
@RequestMapping(value = "/etc/**")
public class EtcController extends BaseController {

	@Description(value = "기타문서 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list(String type) throws Exception {
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		JSONArray docTypeList = DocumentHelper.manager.toJson();
		String location = EtcHelper.manager.toLocation(type);
		String title = "";
		if (location.contains("생산본부")) {
			title = "생산본부";
		} else if (location.contains("병리연구")) {
			title = "병리연구";
		} else if (location.contains("임상개발")) {
			title = "임상개발";
		} else if (location.contains("RA팀")) {
			title = "RA팀";
		} else if (location.contains("화장품")) {
			title = "화장품";
		}
		ModelAndView model = new ModelAndView();
		model.addObject("type", type);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("docTypeList", docTypeList);
		model.addObject("title", title);
		model.setViewName("/extcore/jsp/document/etc/etc-list.jsp");
		return model;
	}

	@Description(value = "기타문서 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = EtcHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "기타문서 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create(String type) throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<FormTemplate> form = FormTemplateHelper.manager.array();
		JSONArray docTypeList = DocumentHelper.manager.toJson();
		String location = EtcHelper.manager.getLocation(type);
		model.addObject("location", location);
		model.addObject("docTypeList", docTypeList);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("form", form);
		model.setViewName("/extcore/jsp/document/etc/etc-create.jsp");
		return model;
	}

	@Description(value = "기타문서 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody EtcDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EtcHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "관련 문서 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi,
			@RequestParam(required = false) String state, @RequestParam(required = false) String location)
			throws Exception {
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
		DocumentType[] docTypeList = DocumentType.getDocumentTypeSet();
		ModelAndView model = new ModelAndView();
		model.addObject("state", state);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("docTypeList", docTypeList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("method", method);
		model.addObject("location", location);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/document/etc/etc-list-popup");
		return model;
	}

	@Description(value = "기타문서 상세보기")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid, @RequestParam String type) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		EtcDTO dto = new EtcDTO(oid);
		model.addObject("type", type);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/document/etc/etc-view");
		return model;
	}

	@Description(value = "기타문서 수정 및 개정 페이지")
	@GetMapping(value = "/update")
	public ModelAndView update(@RequestParam String oid, @RequestParam String mode, @RequestParam String type)
			throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		EtcDTO dto = new EtcDTO(oid);
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<FormTemplate> form = FormTemplateHelper.manager.array();
		DocumentType[] docTypeList = DocumentType.getDocumentTypeSet();
		model.addObject("type", type);
		model.addObject("docTypeList", docTypeList);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("form", form);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.addObject("mode", mode);
		model.setViewName("popup:/document/etc/etc-update");
		return model;
	}

	@Description(value = "기타문서 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody EtcDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EtcHelper.service.modify(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "기타문서 개정 함수")
	@ResponseBody
	@PostMapping(value = "/revise")
	public Map<String, Object> revise(@RequestBody EtcDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EtcHelper.service.revise(dto);
			result.put("msg", REVISE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "기타문서 삭제 함수")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			// true 연결 있음
			if (EtcHelper.manager.isConnect(oid, DocumentECOLink.class)) {
				result.put("result", false);
				result.put("msg", "문서와 연결된 ECO가 있습니다.");
				return result;
			}

			if (EtcHelper.manager.isConnect(oid, DocumentEOLink.class)) {
				result.put("result", false);
				result.put("msg", "문서와 연결된 EO가 있습니다.");
				return result;
			}

			if (EtcHelper.manager.isConnect(oid, DocumentCRLink.class)) {
				result.put("result", false);
				result.put("msg", "문서와 연결된 CR이 있습니다.");
				return result;
			}

//			if (DocumentHelper.manager.connect(doc, DocumentECPRLink.class)) {
//				result.put("result", false);
//				result.put("msg", "문서와 연결된 ECPR이 있습니다.");
//				return result;
//			}

			if (EtcHelper.manager.isConnect(oid, WTPartDescribeLink.class)) {
				result.put("result", false);
				result.put("msg", "문서와 연결된 품목이 있습니다.");
				return result;
			}

			result = EtcHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		System.out.println(result);
		return result;
	}

	@Description(value = "기타문서 최신버전 이동")
	@GetMapping(value = "/latest")
	public ModelAndView latest(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTDocument latest = DocumentHelper.manager.latest(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		EtcDTO dto = new EtcDTO(latest);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/document/etc/etc-view");
		return model;
	}
}
