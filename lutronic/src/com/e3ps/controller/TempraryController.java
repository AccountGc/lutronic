package com.e3ps.controller;

import java.util.ArrayList;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.doc.service.DocumentHelper;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "temprary")
public class TempraryController extends BaseController {

	@Description(value = "임시저장함 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/workprocess/temprary/temprary-list.jsp");
		return model;
	}
}
