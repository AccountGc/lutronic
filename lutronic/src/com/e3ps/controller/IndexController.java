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
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;

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
		PeopleDTO dto = new PeopleDTO(people);
		String department_name = dto.getDepartment_name();
		String auths = dto.getAuth();
		boolean isWork = CommonUtil.isAdmin();
		boolean isDoc = CommonUtil.isAdmin();
		boolean isPart = CommonUtil.isAdmin();
		boolean isEpm = CommonUtil.isAdmin();
		boolean isRohs = CommonUtil.isAdmin();
		boolean isMold = CommonUtil.isAdmin();
		boolean isChange = CommonUtil.isAdmin();
		boolean isEtc = CommonUtil.isAdmin();
		if (StringUtil.checkString(auths)) {
			isWork = auths.contains("나의업무");
			isDoc = auths.contains("문서관리");
			isPart = auths.contains("품목관리");
			isEpm = auths.contains("도면관리");
			isRohs = auths.contains("RoHS");
			isMold = auths.contains("금형관리");
			isChange = auths.contains("설계변경");
			isEtc = auths.contains("기타문서관리");
		}

		// 기타 문서 권한처리
		boolean isRa = false;
		boolean isProduction = false;
		boolean isCosmetic = false;
		boolean isPathological = false;
		boolean isClinical = false;
		if ("".equals(department_name)) {

		}

		model.addObject("isRa", isRa);
		model.addObject("isProduction", isProduction);
		model.addObject("isPathological", isPathological);
		model.addObject("isCosmetic", isCosmetic);
		model.addObject("isClinical", isClinical);

		model.addObject("isWork", isWork);
		model.addObject("isDoc", isDoc);
		model.addObject("isPart", isPart);
		model.addObject("isEpm", isEpm);
		model.addObject("isRohs", isRohs);
		model.addObject("isMold", isMold);
		model.addObject("isChange", isChange);
		model.addObject("isEtc", isEtc);
		model.addObject("dto", dto);
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
}