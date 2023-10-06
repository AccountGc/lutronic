package com.e3ps.change.activity.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import com.e3ps.change.activity.dto.DefDTO;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.controller.BaseController;
import com.e3ps.org.service.OrgHelper;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/activity/**")
public class ActivityController extends BaseController {

	@Description(value = "설계변경 활동 리스트")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray slist = NumberCodeHelper.manager.toJson("EOSTEP");
		JSONArray ulist = OrgHelper.manager.toJsonWTUser();
		JSONArray alist = ActivityHelper.manager.toJsonActMap();
		ArrayList<DefDTO> list = ActivityHelper.manager.root();
		model.addObject("slist", slist);
		model.addObject("ulist", ulist);
		model.addObject("list", list);
		model.addObject("alist", alist);
		model.setViewName("/extcore/jsp/change/activity/activity-list.jsp");
		return model;
	}

	@Description(value = "설계변경 활동 실행")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ActivityHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설변활동 삭제")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> delete(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ActivityHelper.service.delete(params);
			if ((boolean) result.get("success")) {
				result.put("msg", DELETE_MSG);
				result.put("result", SUCCESS);
			} else {
				result.put("result", FAIL);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설변루트 및 활동 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create(@RequestParam String type, @RequestParam(required = false) String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		if ("act".equals(type)) {
			ArrayList<NumberCode> list = NumberCodeHelper.manager.getArrayCodeList("EOSTEP");
			Map<String, String> actMap = ActivityHelper.manager.getActMap();
			model.addObject("oid", oid);
			model.addObject("list", list);
			model.addObject("actMap", actMap);
			model.setViewName("popup:/change/activity/activity-create");
		} else if ("root".equals(type)) {
			model.setViewName("popup:/change/activity/root-create");
		}
		return model;
	}

	@Description(value = "설계변경 루트 및 활동 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설변활동 저장")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> editRows = params.get("editRows");
		ArrayList<LinkedHashMap<String, Object>> removeRows = params.get("removeRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			HashMap<String, ArrayList<LinkedHashMap<String, Object>>> dataMap = new HashMap<>();
			dataMap.put("editRows", editRows); // 수정행
			dataMap.put("removeRows", removeRows); // 삭제행

			ActivityHelper.service.save(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
