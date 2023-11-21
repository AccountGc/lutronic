package com.e3ps.part.bom.controller;

import java.io.File;
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

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.part.bom.service.BomHelper;

import net.sf.json.JSONArray;
import wt.part.WTPart;

@Controller
@RequestMapping(value = "/bom/**")
public class BomController extends BaseController {

	@Description(value = "BOM 보기 화면")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		ArrayList<Map<String, String>> baseline = EChangeUtils.manager.getBaseline(oid);
		model.addObject("baseline", baseline);
		model.addObject("part", part);
		model.setViewName("popup:/part/bom/bom-view");
		return model;
	}

	@Description(value = "BOM 에디터 LAZY 로드")
	@PostMapping(value = "/editorLazyLoad")
	@ResponseBody
	public Map<String, Object> editorLazyLoad(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, Object>> list = BomHelper.manager.editorLazyLoad(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 품목 제거")
	@PostMapping(value = "/removeLink")
	@ResponseBody
	public Map<String, Object> removeLink(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			BomHelper.service.removeLink(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 체크아웃 취소 제거")
	@GetMapping(value = "/undoCheckOut")
	@ResponseBody
	public Map<String, Object> undoCheckOut(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, Object> item = BomHelper.service.undoCheckOut(oid);
			result.put("item", item);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 리로드")
	@GetMapping(value = "/reload")
	@ResponseBody
	public Map<String, Object> reload(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray tree = BomHelper.manager.loadEditor(oid);
			result.put("tree", tree);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 뷰 LAZY LOAD")
	@PostMapping(value = "/lazyLoad")
	@ResponseBody
	public Map<String, Object> lazyLoad(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, Object>> list = BomHelper.manager.lazyLoad(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 뷰")
	@PostMapping(value = "/loadStructure")
	@ResponseBody
	public Map<String, Object> loadStructure(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = BomHelper.manager.loadStructure(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 뷰서 도면 및 첨부파일 일괄 다운로드")
	@GetMapping(value = "/batch")
	public ModelAndView batch(@RequestParam String oid, @RequestParam String target) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		String title = "";
		if ("attach".equals(target)) {
			model.addObject("title", "첨부파일");
		} else if ("epm".equals(target)) {
			model.addObject("title", "도면");
		}
		model.addObject("oid", oid);
		model.addObject("target", target);
		model.addObject("part", part);
		model.setViewName("popup:/part/bom/bom-batch");
		return model;
	}

	@Description(value = "BOM 첨부파일, 도면 일괄 다운로드")
	@PostMapping(value = "/batch")
	@ResponseBody
	public Map<String, Object> batch(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			File file = BomHelper.manager.batch(params);
			result.put("path", file.getPath());
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 에디터")
	@GetMapping(value = "/editor")
	public ModelAndView editor(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart root = (WTPart) CommonUtil.getObject(oid);
		model.addObject("oid", oid);
		model.addObject("root", root);
		model.setViewName("popup:/part/bom/bom-editor");
		return model;
	}

	@Description(value = "BOM 에디터 로드")
	@PostMapping(value = "/loadEditor")
	@ResponseBody
	public Map<String, Object> loadEditor(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = BomHelper.manager.loadEditor(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
