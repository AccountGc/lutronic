package com.e3ps.change.eo.controller;

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

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.change.eo.service.EoHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.part.column.PartColumn;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.service.SAPHelper;

import net.sf.json.JSONArray;
import wt.part.WTPart;

@Controller
@RequestMapping(value = "/eo/**")
public class EoController extends BaseController {

	@Description(value = "EO 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_ECO");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("lifecycleList", lifecycleList);
		model.setViewName("/extcore/jsp/change/eo/eo-list.jsp");
		return model;
	}

	@Description(value = "관련 EO 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_ECO");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/change/eo/eo-list-popup");
		return model;
	}

	@Description(value = "EO 검색 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = EoHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "EO 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/eo/eo-create.jsp");
		return model;
	}

	@Description(value = "EO 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody EoDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EoHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "EO 상세보기")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		EoDTO dto = new EoDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/eo/eo-view");
		return model;
	}

	@Description(value = "EO 수정")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		EoDTO dto = new EoDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/eo/eo-modify");
		return model;
	}

	@Description(value = "EO 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody EoDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EoHelper.service.modify(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "EO 삭제 함수")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			// 삭제 불가 추가 예정

			EoHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "EO SAP 재전송")
	@GetMapping(value = "/sendEoSap")
	public ModelAndView sendEoSap(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		EChangeOrder eo = (EChangeOrder) CommonUtil.getObject(oid);
		EoDTO dto = new EoDTO(eo);
		ArrayList<EOCompletePartLink> completeParts = EoHelper.manager.completeParts(eo);
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for (EOCompletePartLink link : completeParts) {
			String version = link.getVersion();
			WTPart root = PartHelper.manager.getPart(link.getCompletePart().getNumber(), version);
			// 중복 품목 제외를 한다.
			list = SAPHelper.manager.getterSkip(root);
		}

		ArrayList<PartColumn> data = new ArrayList<PartColumn>();
		for (WTPart p : list) {
			PartColumn column = new PartColumn(p);
			data.add(column);
		}
		model.addObject("dto", dto);
		model.addObject("data", JSONArray.fromObject(data));
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/change/eo/eo-resend-sap");
		return model;
	}

	@Description(value = "EO SAP 재전송전 검증")
	@GetMapping(value = "/sendValidate")
	@ResponseBody
	public Map<String, Object> sendValidate(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {

			boolean success = EoHelper.manager.sendPartValidate(oid);
			if(success) {
			} else {
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}
	
	
	@Description(value = "일괄 다운로드")
	@ResponseBody
	@GetMapping(value = "/download")
	public Map<String, Object> download(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = EoHelper.manager.download(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
