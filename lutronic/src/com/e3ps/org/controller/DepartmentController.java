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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.controller.BaseController;
import com.e3ps.org.Department;
import com.e3ps.org.service.DepartmentHelper;
import com.e3ps.org.service.OrgHelper;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/department/**")
public class DepartmentController extends BaseController {

	@Description(value = "부서 트리")
	@PostMapping(value = "/loadFolderTree")
	@ResponseBody
	public ModelAndView treeDepartment(@RequestBody Map<String, String> params) throws Exception {
		ModelAndView model = new ModelAndView();

		String root = params.get("code");
		if (root == null) {
			root = "ROOT";
		}

		Department dept = DepartmentHelper.service.getDepartment(root);

		JSONObject json = OrgHelper.service.getDepartmentTree(dept);
		model.addObject("json", json.toString());
		return model;
	}

	@Description(value = "부서 트리 구조 가져오기")
	@PostMapping(value = "/tree")
	@ResponseBody
	public Map<String, Object> tree() throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = OrgHelper.manager.tree();
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

}
