package com.e3ps.change.ecn.controller;

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

import com.e3ps.change.ecn.dto.EcnDTO;
import com.e3ps.change.ecn.service.EcnHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.DocumentCRLink;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.DocumentECPRLink;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.org.service.OrgHelper;
import com.e3ps.sap.service.SAPHelper;

import net.sf.json.JSONArray;
import wt.part.WTPartDescribeLink;

@Controller
@RequestMapping(value = "/ecn/**")
public class EcnController extends BaseController {

	@Description(value = "ECN 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
		ModelAndView model = new ModelAndView();
		JSONArray list = OrgHelper.manager.toJsonWTUser();
		model.addObject("list", list);
		model.addObject("modelList", modelList);
		model.addObject("lifecycleList", lifecycleList);
		model.setViewName("/extcore/jsp/change/ecn/ecn-list.jsp");
		return model;
	}

	@Description(value = "ECN 검색 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();
		try {
			result = EcnHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECN 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EcnDTO dto = new EcnDTO(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		JSONArray arr = EcnHelper.manager.viewData(oid);
		ArrayList<Map<String, String>> list = NumberCodeHelper.manager.getCountry();
		model.addObject("list", list);
		model.addObject("isAdmin", isAdmin);
		model.addObject("arr", arr);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/ecn/ecn-view");
		return model;
	}

	@Description(value = "ECN ERP 전송 함수")
	@ResponseBody
	@PostMapping(value = "/send")
	public Map<String, Object> send(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();
		try {
			SAPHelper.service.sendSapToEcn(params);
			result.put("result", SUCCESS);
			result.put("msg", "전송 되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECN 담당자 지정 함수")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcnHelper.service.save(params);
			result.put("msg", "담당자가 지정되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECN 삭제 함수")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcnHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECN 완료 함수")
	@ResponseBody
	@PostMapping(value = "/complete")
	public Map<String, Object> complete(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			EcnHelper.service.complete(params);
			result.put("msg", "완료 처리 되었습니다.");
			result.put("result", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
