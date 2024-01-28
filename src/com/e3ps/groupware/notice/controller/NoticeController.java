package com.e3ps.groupware.notice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.dto.NoticeDTO;
import com.e3ps.groupware.notice.service.NoticeHelper;

@Controller
@RequestMapping(value = "/notice/**")
public class NoticeController extends BaseController {

	@Description(value = "공지사항 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/workprocess/notice-list.jsp");
		return model;
	}

	@Description(value = "공지사항 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = NoticeHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "공지사항 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/workprocess/notice-create");
		return model;
	}

	@Description(value = "공지사항 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			NoticeHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "공지사항 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid, @RequestParam(required = false) String main) throws Exception {
		NoticeHelper.service.read(oid);
		Notice notice = (Notice) CommonUtil.getObject(oid);
		NoticeDTO dto = new NoticeDTO(notice);
		boolean isMain = Boolean.parseBoolean(main);
		ModelAndView model = new ModelAndView();
		model.addObject("isMain", isMain);
		model.addObject("dto", dto);
		model.setViewName("popup:/workprocess/notice-view");
		return model;
	}

	@Description(value = "공지사항 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String oid) throws Exception {
		NoticeHelper.service.read(oid);
		NoticeDTO dto = new NoticeDTO(oid);
		ModelAndView model = new ModelAndView();
		model.addObject("dto", dto);
		model.setViewName("popup:/workprocess/notice-view-popup");
		return model;
	}

	@Description(value = "공지사항 수정 페이지")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid, @RequestParam(required = false) String main) throws Exception {
		ModelAndView model = new ModelAndView();
		NoticeDTO dto = new NoticeDTO(oid);
		boolean isMain = Boolean.parseBoolean(main);
		model.addObject("isMain", isMain);
		model.addObject("dto", dto);
		model.setViewName("popup:/workprocess/notice-modify");
		return model;
	}

	@Description(value = "공지사항 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			NoticeHelper.service.modify(params);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "공지사항 삭제")
	@ResponseBody
	@GetMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			NoticeHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "팝업 공지사항")
	@ResponseBody
	@PostMapping(value = "/cookie")
	public Map<String, Object> cookie() throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			ArrayList<NoticeDTO> list = NoticeHelper.manager.cookie();
			result.put("result", SUCCESS);
			result.put("list", list);
		} catch (Exception e) {
			result.put("msg", e.toString());
			result.put("result", FAIL);
			e.printStackTrace();
		}
		return result;
	}
}
