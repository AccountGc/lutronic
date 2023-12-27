package com.e3ps.workspace.controller;

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

import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.workspace.dto.WorkDataDTO;
import com.e3ps.workspace.service.WorkDataHelper;

@Controller
@RequestMapping(value = "/workData/**")
public class WorkDataController extends BaseController {

	@Description(value = "작업함 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/workspace/workData-list.jsp");
		return model;
	}

	@Description(value = "작업함 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkDataHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/approval", "결재함 조회 함수");
		}
		return result;
	}

	@Description(value = "결재선 지정 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WorkDataDTO dto = new WorkDataDTO(oid);
		model.addObject("dto", dto);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/workspace/workData-view.jsp");
		return model;
	}

	@Description(value = "결재 기안")
	@ResponseBody
	@PostMapping(value = "/_submit")
	public Map<String, Object> _submit(@RequestBody WorkDataDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkDataHelper.service._submit(dto);
			result.put("msg", "결재가 기안되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "작업함 읽음 처리")
	@ResponseBody
	@GetMapping(value = "/read")
	public Map<String, Object> read(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkDataHelper.service.read(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
