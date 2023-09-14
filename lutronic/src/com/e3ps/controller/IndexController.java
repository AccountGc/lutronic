package com.e3ps.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.FolderUtils;
import com.e3ps.org.People;

import net.sf.json.JSONArray;
import wt.org.WTUser;

@Controller
public class IndexController extends BaseController {

	@Description(value = "메인 페이지")
	@GetMapping(value = "/index")
	public ModelAndView index() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("default:/index");
		return model;
	}

	@Description(value = "메인 페이지")
	@GetMapping(value = "/mainPage")
	public ModelAndView mainPage() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/mainPage.jsp");
		return model;
	}

	@Description(value = "헤더 페이지")
	@GetMapping(value = "/header")
	public ModelAndView header() throws Exception {
		ModelAndView model = new ModelAndView();
		People people = CommonUtil.sessionPeople();
		WTUser sessionUser = CommonUtil.sessionUser();
		ArrayList<String> auth = people.getAuth();
		boolean isWork = false;
		boolean isDoc = false;
		boolean isPart = false;
		boolean isEpm = false;
		boolean isRohs = false;
		boolean isMold = false;
		boolean isChange = false;
		boolean isEtc = false;
		if (auth != null) {
			isWork = auth.contains("나의업무");
			isDoc = auth.contains("문서관리");
			isPart = auth.contains("품목관리");
			isEpm = auth.contains("도면관리");
			isRohs = auth.contains("RoHS");
			isMold = auth.contains("금형관리");
			isChange = auth.contains("설계변경");
			isEtc = auth.contains("기타문서관리");
		}
		model.addObject("isWork", isWork);
		model.addObject("isDoc", isDoc);
		model.addObject("isPart", isPart);
		model.addObject("isEpm", isEpm);
		model.addObject("isRohs", isRohs);
		model.addObject("isMold", isMold);
		model.addObject("isChange", isChange);
		model.addObject("isEtc", isEtc);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/layout/header.jsp");
		return model;
	}

	@Description(value = "푸터 페이지")
	@GetMapping(value = "/footer")
	public ModelAndView footer() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/layout/footer.jsp");
		return model;
	}

	@Description(value = "폴더 트리 구조 가져오기")
	@PostMapping(value = "/loadFolderTree")
	@ResponseBody
	public Map<String, Object> loadFolderTree(@RequestBody Map<String, String> params) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = FolderUtils.loadFolderTree(params);
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