package com.e3ps.change.ecn.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.change.ecn.dto.EcnDTO;
import com.e3ps.change.ecn.service.EcnHelper;
import com.e3ps.change.eco.dto.EcoDTO;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.groupware.workprocess.service.WFItemHelper;

@Controller
@RequestMapping(value = "/ecn/**")
public class EcnController extends BaseController {

	@Description(value = "ECN 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		List<Map<String, String>> lifecycleList = WFItemHelper.manager.lifecycleList("LC_Default", "");
		ModelAndView model = new ModelAndView();
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
		boolean isAdmin = CommonUtil.isAdmin();
		EcnDTO dto = new EcnDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/ecn/ecn-view");
		return model;
	}
	
}
