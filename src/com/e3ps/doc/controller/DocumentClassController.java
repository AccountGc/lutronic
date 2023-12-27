package com.e3ps.doc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.dto.DocumentClassDTO;
import com.e3ps.doc.service.DocumentClassHelper;
import com.e3ps.doc.service.DocumentHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/class/**")
public class DocumentClassController extends BaseController {

	@Description(value = "문서 채번 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/document/class/document-class-list.jsp");
		return model;
	}

	@Description(value = "문서 채번 리스트 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentClassHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 채번 타입 트리")
	@GetMapping(value = "/tree")
	@ResponseBody
	public Map<String, Object> tree() throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = DocumentClassHelper.manager.tree();
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 채번 저장")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, Object> params) throws Exception {
		ArrayList<LinkedHashMap<String, Object>> addRows = (ArrayList<LinkedHashMap<String, Object>>) params
				.get("addRows");
		ArrayList<LinkedHashMap<String, Object>> editRows = (ArrayList<LinkedHashMap<String, Object>>) params
				.get("editRows");
		ArrayList<LinkedHashMap<String, Object>> removeRows = (ArrayList<LinkedHashMap<String, Object>>) params
				.get("removeRows");
		String classType = (String) params.get("classType");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ArrayList<DocumentClassDTO> addRow = new ArrayList<>();
			if (addRows != null && addRows.size() > 0) {
				for (LinkedHashMap<String, Object> add : addRows) {
					DocumentClassDTO dto = mapper.convertValue(add, DocumentClassDTO.class);
					addRow.add(dto);
				}
			}

			ArrayList<DocumentClassDTO> editRow = new ArrayList<>();
			if (editRows != null && editRows.size() > 0) {
				for (LinkedHashMap<String, Object> edit : editRows) {
					DocumentClassDTO dto = mapper.convertValue(edit, DocumentClassDTO.class);
					editRow.add(dto);
				}
			}

			ArrayList<DocumentClassDTO> removeRow = new ArrayList<>();
			if (removeRows != null && removeRows.size() > 0) {
				for (LinkedHashMap<String, Object> remove : removeRows) {
					DocumentClassDTO dto = mapper.convertValue(remove, DocumentClassDTO.class);
					removeRow.add(dto);
				}
			}

			HashMap<String, Object> dataMap = new HashMap<>();
			dataMap.put("classType", classType);
			dataMap.put("addRows", addRow); // 추가행
			dataMap.put("editRows", editRow); // 수정행
			dataMap.put("removeRows", removeRow); // 삭제행

			DocumentClassHelper.service.save(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "중분류 가져오기")
	@ResponseBody
	@GetMapping(value = "/classType2")
	public Map<String, Object> classType2(@RequestParam String classType1) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, String>> classType2 = DocumentClassHelper.manager.classType2(classType1);
			result.put("classType2", classType2);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "소분류 가져오기")
	@ResponseBody
	@GetMapping(value = "/classType3")
	public Map<String, Object> classType3(@RequestParam String classType1, @RequestParam String classType2) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, String>> classType3 = DocumentClassHelper.manager.classType3(classType1, classType2);
			result.put("classType3", classType3);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

}
