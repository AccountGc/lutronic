package com.e3ps.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.common.history.service.LoginHistoryHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.workspace.service.WorkDataHelper;
import com.e3ps.workspace.service.WorkspaceHelper;

@Controller
public class IndexController extends BaseController {

	@Description(value = "로그인")
	@PostMapping(value = "/login")
	@ResponseBody
	public Map<String, Object> login(@RequestBody Map<String, String> params, HttpServletRequest request)
			throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			String j_username = (String) params.get("j_username");
			result = LoginHistoryHelper.service.create(j_username, request);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			result.put("result", FAIL);
			result.put("msg", e.toString());
			e.printStackTrace();
		}
		return result;
	}

	@Description(value = "로그아웃")
	@GetMapping(value = "/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		System.out.println("로그아웃!");
		response.sendRedirect("/Windchill/login/index.html");
	}

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

		int workData = WorkDataHelper.manager.count();
		int eca = ActivityHelper.manager.count();

		Map<String, Integer> count = WorkspaceHelper.manager.count();
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

		// 결재 재수
		model.addObject("count", count);
		model.addObject("workData", workData);
		model.addObject("eca", eca);

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