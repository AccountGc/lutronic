package com.e3ps.part.bom.controller;

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

import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.part.bom.service.BomHelper;

import wt.part.WTPart;

@Controller
@RequestMapping(value = "/bom/**")
public class BomController extends BaseController {

	@Description(value = "BOM 보기 화면")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		ArrayList<Map<String, String>> baseline = EChangeUtils.manager.getBaseline(oid);
		model.addObject("baseline", baseline);
		model.addObject("part", part);
		model.setViewName("popup:/part/bom/bom-view");
		return model;
	}

	@Description(value="BOM 뷰")
	@PostMapping(value="/loadStructure")
	@ResponseBody
	public Map<String, Object> loadStructure(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
//			BomHelper.manager.loadStructure(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
