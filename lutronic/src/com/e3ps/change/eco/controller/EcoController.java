package com.e3ps.change.eco.controller;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.change.eco.service.EcoHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/eco/**")
public class EcoController extends BaseController {

	@Description(value = "ECO 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.setViewName("/extcore/jsp/change/eco/eco-list.jsp");
		return model;
	}

	@Description(value = "관련 ECO 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup() throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.setViewName("popup:/change/eco/eco-list-popup");
		return model;
	}

	@Description(value = "ECO 검색 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = null;
		try {
			result = EcoHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
