package com.e3ps.controller;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.doc.dto.DocumentDTO;

@Controller
@RequestMapping(value = "/aui/**")
public class AUIGridController extends BaseController {

	@Description(value = "그리드 리스트서 주 첨부파일 추가 페이지")
	@GetMapping(value = "/primary")
	public ModelAndView primary(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/common/aui/aui-primary");
		return model;
	}

	@Description(value = "그리드 리스트서 첨부파일 추가 페이지")
	@GetMapping(value = "/secondary")
	public ModelAndView create(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/common/aui/aui-secondary");
		return model;
	}
	
	@Description(value = "그리드 리스트서 주 첨부파일 추가 페이지")
	@GetMapping(value = "/primaryDrawing")
	public ModelAndView primaryDrawing(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/common/aui/aui-primary-drawing");
		return model;
	}

}
