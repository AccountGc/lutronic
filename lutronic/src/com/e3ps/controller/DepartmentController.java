package com.e3ps.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.org.Department;
import com.e3ps.org.service.DepartmentHelper;
import com.e3ps.org.service.OrgHelper;

@Controller
@RequestMapping("/department")
public class DepartmentController {
	
	/** 부서 트리
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/treeDepartment")
	public ModelAndView treeDepartment(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/org/treeDepartment");
		
		String root = request.getParameter("code");
		if(root == null) {
			root="ROOT";
		}
		
		Department dept = DepartmentHelper.service.getDepartment(root);
		
		JSONObject json = OrgHelper.service.getDepartmentTree(dept);
		model.addObject("json", json.toString());
		return model;
	}
}
