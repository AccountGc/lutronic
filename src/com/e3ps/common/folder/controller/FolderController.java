package com.e3ps.common.folder.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.util.WTException;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.folder.beans.CommonFolderHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.FolderUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.part.bom.service.BomHelper;
import com.google.gwt.i18n.client.LocalizableResource.Description;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/folder")
public class FolderController extends BaseController {

	@Description(value = "폴더 LAZY LOAD")
	@PostMapping(value = "/lazyTree")
	@ResponseBody
	public Map<String, Object> lazyTree(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, Object>> list = FolderUtils.lazyTree(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "폴더 팝업창")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String location, @RequestParam(required = false) String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("location", location);
		model.addObject("method", method);
		model.setViewName("popup:/common/folder/folder-popup");
		return model;
	}

	@Description(value = "폴더 트리 구조 가져오기")
	@PostMapping(value = "/tree")
	@ResponseBody
	public Map<String, Object> tree(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = FolderUtils.tree(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "폴더 트리 저장")
	@PostMapping(value = "/treeSave")
	@ResponseBody
	public Map<String, Object> treeSave(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			FolderUtils.treeSave(params);
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
