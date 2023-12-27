package com.e3ps.mold.controller;

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
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.mold.dto.MoldDTO;
import com.e3ps.mold.service.MoldHelper;

import wt.doc.WTDocument;
import wt.org.WTUser;
import wt.part.WTPartDescribeLink;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/mold/**")
public class MoldController extends BaseController {

	@Description(value = "금형 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> moldTypeList = NumberCodeHelper.manager.getArrayCodeList("MOLDTYPE");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("moldTypeList", moldTypeList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/mold/mold-list.jsp");
		return model;
	}

	@Description(value = "금형 검색")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = MoldHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "금형 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> moldtypeList = NumberCodeHelper.manager.getArrayCodeList("MOLDTYPE");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/mold/mold-create.jsp");
		model.addObject("manufactureList", manufactureList);
		model.addObject("moldtypeList", moldtypeList);
		model.addObject("deptcodeList", deptcodeList);
		return model;
	}

	@Description(value = "금형 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody MoldDTO dto) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MoldHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "금형 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		MoldDTO dto = new MoldDTO(doc);

		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/mold/mold-view");
		return model;
	}

	@Description(value = "금형 개정 페이지")
	@GetMapping(value = "/revise")
	public ModelAndView revise(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/mold/mold-revise.jsp");
		return model;
	}

	@Description(value = "금형 개정 함수")
	@ResponseBody
	@PostMapping(value = "/revise")
	public Map<String, Object> revise(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MoldHelper.service.revise(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "금형 최신버전 이동")
	@GetMapping(value = "/latest")
	public ModelAndView latest(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTDocument latest = DocumentHelper.manager.latest(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		MoldDTO dto = new MoldDTO(latest);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/mold/mold-view");
		return model;
	}

	@Description(value = "금형 수정 페이지")
	@GetMapping(value = "/update")
	public ModelAndView update(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		MoldDTO dto = new MoldDTO(doc);
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> moldtypeList = NumberCodeHelper.manager.getArrayCodeList("MOLDTYPE");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		model.addObject("manufactureList", manufactureList);
		model.addObject("moldtypeList", moldtypeList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("dto", dto);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/mold/mold-update.jsp");
		return model;
	}

	@Description(value = "금형 수정 함수")
	@ResponseBody
	@PostMapping(value = "/update")
	public Map<String, Object> update(@RequestBody MoldDTO dto) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MoldHelper.service.update(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "금형 삭제 함수")
	@ResponseBody
	@PostMapping(value = "/delete")
	public Map<String, Object> delete(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String oid = (String) params.get("oid");
			// true 연결 있음
			if (DocumentHelper.manager.isConnect(oid, WTPartDescribeLink.class)) {
				result.put("result", false);
				result.put("msg", "금형과 연결된 품목이 있습니다.");
				return result;
			}

			if (MoldHelper.manager.isConnect(oid)) {
				result.put("result", false);
				result.put("msg", "금형과 연결된 문서가 있습니다.");
				return result;
			}

			result = DocumentHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "금형 일괄결재 페이지")
	@GetMapping(value = "/all")
	public ModelAndView all() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/mold/mold-all.jsp");
		return model;
	}

	@Description(value = "승인원 검색 페이지")
	@GetMapping(value = "/ap-list")
	public ModelAndView apList() throws Exception {
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> moldTypeList = NumberCodeHelper.manager.getArrayCodeList("MOLDTYPE");
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("moldTypeList", moldTypeList);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/ap/ap-list.jsp");
		return model;
	}

	@Description(value = "승인원 등록 페이지")
	@GetMapping(value = "/ap-create")
	public ModelAndView apCreate() throws Exception {
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> moldtypeList = NumberCodeHelper.manager.getArrayCodeList("MOLDTYPE");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/ap/ap-create.jsp");
		model.addObject("manufactureList", manufactureList);
		model.addObject("moldtypeList", moldtypeList);
		model.addObject("deptcodeList", deptcodeList);
		return model;
	}

	@Description(value = "승인원 결재 페이지")
	@GetMapping(value = "/ap-all")
	public ModelAndView apAll() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/ap/ap-all.jsp");
		return model;
	}

	/**
	 * 일괄 등록 메뉴 이동
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createPackageMold")
	public ModelAndView createPackageMold(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module", "mold");
		model.setViewName("default:/mold/createPackageMold");
		return model;
	}

	/**
	 * 일괄 등록 수행
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createPackageMoldAction")
	public ModelAndView createPackageMoldAction(HttpServletRequest request, HttpServletResponse response) {
		String xmlString = DocumentHelper.service.createPackageDocumentAction(request, response);

		ModelAndView model = new ModelAndView();
		model.addObject("xmlString", xmlString);
		model.setViewName("empty:/mold/createPackageMoldAction");
		return model;
	}

	/**
	 * 일괄 결제 메뉴 이동
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/approvalPackageMold")
	public ModelAndView approvalPackageMold(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu4");
		model.addObject("module", "mold");
		model.setViewName("default:/mold/approvalPackageMold");
		return model;
	}

	/**
	 * 일괄 결제 실행
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/approvalPackageMoldAction")
	public ResultData approvalPackageMoldAction(HttpServletRequest request, HttpServletResponse response) {
		return DocumentHelper.service.approvalPackageDocumentAction(request, response);
	}
}
