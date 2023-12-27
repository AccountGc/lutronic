package com.e3ps.doc.access.controller;

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

import com.e3ps.admin.form.FormTemplate;
import com.e3ps.admin.form.service.FormTemplateHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.access.service.AccessHelper;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.doc.service.DocumentClassHelper;
import com.e3ps.doc.service.DocumentHelper;

import net.sf.json.JSONArray;
import wt.folder.Folder;
import wt.folder.SubFolder;

@Controller
@RequestMapping(value = "/access")
public class AccessController extends BaseController {

	@Description(value = "권한 뷰 및 등록")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		SubFolder f = (SubFolder) CommonUtil.getObject(oid);
		JSONArray userList = AccessHelper.manager.getUserList(f);
		JSONArray groupList = AccessHelper.manager.getGroupList(f);
		JSONArray authList = AccessHelper.manager.getAuthList(f);
		model.addObject("authList", authList);
		model.addObject("userList", userList);
		model.addObject("groupList", groupList);
		model.addObject("f", f);
		model.addObject("oid", oid);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/document/access/access-view");
		return model;
	}

	@Description(value = "권한 체크 함수")
	@ResponseBody
	@GetMapping(value = "/isPermission")
	public Map<String, Object> isPermission(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			boolean isPermission = AccessHelper.manager.isPermission(oid);
			result.put("isPermission", isPermission);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "권한 저장 함수")
	@ResponseBody
	@PostMapping(value = "/save")
	public Map<String, Object> save(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			AccessHelper.service.save(params);
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
