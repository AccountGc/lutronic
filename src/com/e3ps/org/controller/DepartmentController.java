package com.e3ps.org.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.util.FolderUtils;
import com.e3ps.controller.BaseController;
import com.e3ps.org.Department;
import com.e3ps.org.service.DepartmentHelper;
import com.e3ps.org.service.OrgHelper;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/department/**")
public class DepartmentController extends BaseController {

	@Description(value = "부서 트리 구조 가져오기")
	@PostMapping(value = "/tree")
	@ResponseBody
	public Map<String, Object> tree() throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = DepartmentHelper.manager.tree();
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "결재선 등록 페이지에서 부서 트리 구조 가져오기")
	@GetMapping(value = "/load900")
	@ResponseBody
	public Map<String, Object> load900() throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = DepartmentHelper.manager.load900();
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "결재선 등록 페이지에서 부서 더블클릭시 해당 부서 사용자들 일괄 지정")
	@GetMapping(value = "/specify")
	@ResponseBody
	public Map<String, Object> specify(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DepartmentHelper.manager.specify(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "부서 트리 저장")
	@PostMapping(value = "/treeSave")
	@ResponseBody
	public Map<String, Object> treeSave(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DepartmentHelper.service.treeSave(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
