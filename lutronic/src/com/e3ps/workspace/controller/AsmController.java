package com.e3ps.workspace.controller;

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

import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.workspace.dto.AsmDTO;
import com.e3ps.workspace.service.AsmHelper;
import com.ptc.windchill.uwgm.proesrv.createesrdoc.AsmDocUtility;

@Controller
@RequestMapping(value = "/asm/**")
public class AsmController extends BaseController {

	@Description(value = "일괄결재 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list(String number) throws Exception {
		ModelAndView model = new ModelAndView();
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
		boolean isAdmin = CommonUtil.isAdmin();
		String title = "";
		if ("NDBT".equals(number)) {
			title = "문서";
		} else if ("ROHSBT".equals(number)) {
			title = "RoHS";
		} else if ("MMBT".equals(number)) {
			title = "금형";
		}
		model.addObject("title", title);
		model.addObject("number", number);
		model.addObject("isAdmin", isAdmin);
		model.addObject("lifecycleList", lifecycleList);
		model.setViewName("/extcore/jsp/workspace/asm-list.jsp");
		return model;
	}

	@Description(value = "일괄결재 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = AsmHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 일괄결재 등록 실행")
	@ResponseBody
	@PostMapping(value = "/register")
	public Map<String, Object> register(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			String msg = "";
			String type = (String) params.get("type");
			if ("DOC".equals(type)) {
				msg = "문서 일괄 결재가 등록 되었습니다.";
			} else if ("MOLD".equals(type)) {
				msg = "금형문서 일괄 결재가 등록 되었습니다.";
			} else if ("ROHS".equals(type)) {
				msg = "RoHS 일괄 결재가 등록 되었습니다.";
			}

			AsmHelper.service.register(params);
			result.put("msg", msg);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "일괄결재 상세보기")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		AsmDTO dto = new AsmDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/workspace/asm-view");
		return model;
	}

	@Description(value = "일괄결재 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create(@RequestParam String type) throws Exception {
		ModelAndView model = new ModelAndView();

		if ("DOC".equals(type)) {
			model.setViewName("/extcore/jsp/document/document-register.jsp");
		} else if ("MOLD".equals(type)) {
			model.setViewName("/extcore/jsp/mold/mold-register.jsp");
		} else if ("ROHS".equals(type)) {
			model.setViewName("/extcore/jsp/rohs/rohs-register.jsp");
		}

		return model;
	}
}
