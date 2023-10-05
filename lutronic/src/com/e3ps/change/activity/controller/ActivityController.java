package com.e3ps.change.activity.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.change.activity.dto.DefDTO;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/activity/**")
public class ActivityController extends BaseController {

	@Description(value = "설계변경 활동 리스트")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<DefDTO> list = ActivityHelper.manager.root();
		model.addObject("list", list);
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
	public Map<String, Object> delete(@RequestBody Map<String, ArrayList<Map<String, String>>> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service.delete(params);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
