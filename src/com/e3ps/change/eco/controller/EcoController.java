package com.e3ps.change.eco.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.eco.dto.EcoDTO;
import com.e3ps.change.eco.service.EcoHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.service.DocumentHelper;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/eco/**")
public class EcoController extends BaseController {

	@Description(value = "ECO 프로젝트 코드 동기화")
	@ResponseBody
	@GetMapping(value = "/sync")
	public Map<String, Object> sync(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcoHelper.service.sync(oid);
			result.put("msg", "동기화 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	
	@Description(value = "ECO 완제품 연결 삭제 함수")
	@ResponseBody
	@DeleteMapping(value = "/deleteLink")
	public Map<String, Object> deleteLink(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcoHelper.service.deleteLink(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "ECO 산출물")
	@GetMapping(value = "/output")
	public ModelAndView output(@RequestParam String method, @RequestParam String multi,
			@RequestParam(required = false) String state, @RequestParam(required = false) String location, String oid)
			throws Exception {
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
//		ArrayList<Map<String, String>> classTypes1 = DocumentClassHelper.manager.getClassTypes1();

		ModelAndView model = new ModelAndView();
//		model.addObject("classTypes1", classTypes1);
		model.addObject("state", state);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("method", method);
		model.addObject("location", location);
		model.addObject("oid", oid);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/change/eco/include/eco-output");
		return model;
	}

	@Description(value = "설변품목 더미 제외여부")
	@ResponseBody
	@GetMapping(value = "/reloadData")
	public Map<String, Object> reloadData(@RequestParam String oid, @RequestParam String skip) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			ArrayList<Map<String, Object>> list = EcoHelper.manager.reloadData(oid, skip);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 설계변경 통보서")
	@ResponseBody
	@GetMapping(value = "/excel")
	public Map<String, Object> excel(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = EcoHelper.manager.excel(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_ECO");
		ModelAndView model = new ModelAndView();
		model.addObject("lifecycleList", lifecycleList);
		model.setViewName("/extcore/jsp/change/eco/eco-list.jsp");
		return model;
	}

	@Description(value = "관련 ECO 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_ECO");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/change/eco/eco-list-popup");
		return model;
	}

	@Description(value = "ECO 검색 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = EcoHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/eco/eco-create.jsp");
		return model;
	}

	@Description(value = "ECO 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody EcoDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcoHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 상세보기")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		EcoDTO dto = new EcoDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/eco/eco-view");
		return model;
	}

	@Description(value = "ECO 수정")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		EcoDTO dto = new EcoDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/eco/eco-modify");
		return model;
	}

	@Description(value = "ECO 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody EcoDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcoHelper.service.modify(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 삭제 함수")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcoHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 관련 도면 다운로드")
	@ResponseBody
	@PostMapping(value = "/summaryData")
	public Map<String, Object> summaryData(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcoHelper.manager.summaryData(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "SAP 전송전 검증 해보기")
	@ResponseBody
	@PostMapping(value = "/validate")
	public Map<String, Object> validate(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = EcoHelper.manager.validate(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "SAP 검증 결과 팝업페이지")
	@GetMapping(value = "/resultPage")
	public ModelAndView resultPage() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/change/eco/eco-sap-resultPage");
		return model;
	}

	@Description(value = "설변품목 정보 저장")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> editRows = params.get("editRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			HashMap<String, ArrayList<LinkedHashMap<String, Object>>> dataMap = new HashMap<>();
			dataMap.put("editRows", editRows); // 수정행

			EcoHelper.service.save(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설변 관련 문서 저장")
	@PostMapping(value = "/save90")
	@ResponseBody
	public Map<String, Object> save90(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcoHelper.service.save90(params);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "산출물 삭제")
	@PostMapping(value = "/removeLink")
	@ResponseBody
	public Map<String, Object> removeLink(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EcoHelper.service.removeLink(params);
			result.put("result", SUCCESS);
			result.put("msg", DELETE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	
	@Description(value = "일괄 다운로드")
	@ResponseBody
	@GetMapping(value = "/download")
	public Map<String, Object> download(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = EcoHelper.manager.download(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "ECO 리스트 엑셀 다운로드")
	@ResponseBody
	@GetMapping(value = "/excelList")
	public Map<String, Object> excelList() throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = EcoHelper.manager.excelList();
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

}
