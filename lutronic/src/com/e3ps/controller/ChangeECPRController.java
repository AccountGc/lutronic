package com.e3ps.controller;

import java.util.ArrayList;
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

import com.e3ps.change.EChangeRequest;
import com.e3ps.change.beans.ECRData;
import com.e3ps.change.service.ECPRHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
//import com.e3ps.doc.service.DocumentHelper;

@Controller
@RequestMapping(value = "/changeECPR/**")
public class ChangeECPRController extends BaseController {

	@Description(value = "ECPR 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView createECR() throws Exception{
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/ecr-create.jsp");
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("sectionList", sectionList);
		return model;
	}
	
	@Description(value = "ECPR 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		model.addObject("modelList", modelList);
		model.addObject("sectionList", sectionList);
		model.setViewName("/extcore/jsp/change/ecpr-list.jsp");
		return model;
	}
	
	@Description(value = "ECPR 검색  Action")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String,Object> list(@RequestBody Map<String, Object> params){
		Map<String,Object> result = null;
		try {
			 result = ECPRHelper.manager.list(params);
			 result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "관련 ECPR 팝업 페이지")
	@GetMapping(value = "/listPopup")
	public ModelAndView listPopup(@RequestParam(value = "parentRowIndex", required = false) Integer parentRowIndex) throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		model.addObject("modelList", modelList);
		model.addObject("parentRowIndex", parentRowIndex);
		model.addObject("sectionList", sectionList);
		model.setViewName("popup:/change/ecpr-list-popup");
		return model;
	}
	
	@Description(value = "ECPR 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception{
		ModelAndView model = new ModelAndView();
		EChangeRequest ecr = (EChangeRequest)CommonUtil.getObject(oid);
		ECRData dto = new ECRData(ecr);
		
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/change/ecr-view.jsp");
		return model;
	}
}
