package com.e3ps.system.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.service.DocumentClassHelper;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.system.service.SystemHelper;

import wt.org.WTUser;

@Controller
@RequestMapping(value = "/system/**")
public class SystemController extends BaseController {

	@Description(value = "PART 전송 현황 페이지")
	@GetMapping(value = "/part")
	public ModelAndView part() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTUser sessionUser = CommonUtil.sessionUser();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/system/part-sendLogger-list.jsp");
		return model;
	}

	@Description(value = "품목 전송 현황 조회 함수")
	@ResponseBody
	@PostMapping(value = "/part")
	public Map<String, Object> part(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = SystemHelper.manager.part(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 전송 현황 페이지")
	@GetMapping(value = "/bom")
	public ModelAndView bom() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTUser sessionUser = CommonUtil.sessionUser();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/system/bom-sendLogger-list.jsp");
		return model;
	}

	@Description(value = "BOM 전송 현황 조회 함수")
	@ResponseBody
	@PostMapping(value = "/bom")
	public Map<String, Object> bom(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = SystemHelper.manager.bom(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

}
