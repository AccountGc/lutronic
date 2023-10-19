package com.e3ps.change.ecpr.controller;

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

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.beans.ECRData;
import com.e3ps.change.cr.dto.CrDTO;
import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.change.ecpr.dto.EcprDTO;
import com.e3ps.change.ecpr.service.EcprHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.mold.service.MoldHelper;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPartDescribeLink;

@Controller
@RequestMapping(value = "/ecpr/**")
public class EcprController extends BaseController {
	
	@Description(value = "ECPR 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		List<Map<String, String>> lifecycleList = WFItemHelper.manager.lifecycleList("LC_Default", "");
		model.addObject("modelList", modelList);
		model.addObject("sectionList", sectionList);
		model.addObject("lifecycleList", lifecycleList);
		model.setViewName("/extcore/jsp/change/ecpr/ecpr-list.jsp");
		return model;
	}
	
	@Description(value = "ECPR 검색 Action")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String,Object> list(@RequestBody Map<String, Object> params){
		Map<String,Object> result = null;
		try {
			 result = EcprHelper.manager.list(params);
			 result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "관련 ECPR 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		List<Map<String, String>> lifecycleList = WFItemHelper.manager.lifecycleList("LC_Default", "");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("sectionList", sectionList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/change/ecpr/ecpr-list-popup");
		return model;
	}
	
	@Description(value = "ECPR 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("sectionList", sectionList);
		model.setViewName("/extcore/jsp/change/ecpr/ecpr-create.jsp");
		return model;
	}

	@Description(value = "ECPR 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody EcprDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcprHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "ECPR 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception{
		ModelAndView model = new ModelAndView();
		EcprDTO dto = new EcprDTO(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/change/ecpr/ecpr-view.jsp");
		return model;
	}
	
	@Description(value = "ECPR 수정 페이지")
	@GetMapping(value = "/update")
	public ModelAndView update(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EcprDTO dto = new EcprDTO(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> sectionList = NumberCodeHelper.manager.getArrayCodeList("CHANGESECTION");
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("sectionList", sectionList);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/change/ecpr/ecpr-update.jsp");
		return model;
	}
	
	@Description(value = "ECPR 수정 함수")
	@ResponseBody
	@PostMapping(value = "/update")
	public Map<String, Object> update(@RequestBody EcprDTO dto) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcprHelper.service.update(dto);
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
	@PostMapping(value = "/delete")
	public Map<String, Object> delete(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String oid = (String) params.get("oid");
			// true 연결 있음
			if (EcprHelper.manager.isConnect(oid)) {
				result.put("result", false);
				result.put("msg", "ecpr과 연결된 cr이 있습니다.");
				return result;
			}
			
			EcprHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
