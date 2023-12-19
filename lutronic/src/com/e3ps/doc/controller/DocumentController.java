package com.e3ps.doc.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.admin.form.FormTemplate;
import com.e3ps.admin.form.service.FormTemplateHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.DocumentCRLink;
import com.e3ps.doc.DocumentClassType;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.DocumentECPRLink;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.doc.service.DocumentClassHelper;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.download.service.DownloadHistoryHelper;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.folder.Folder;
import wt.part.WTPartDescribeLink;

@Controller
@RequestMapping(value = "/doc/**")
public class DocumentController extends BaseController {

	@Description(value = "문서 등록 페이지 - 설변활동 링크등록")
	@GetMapping(value = "/link")
	public ModelAndView link(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<FormTemplate> form = FormTemplateHelper.manager.array();
		JSONArray docTypeList = DocumentHelper.manager.toJson();
		model.addObject("docTypeList", docTypeList);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("form", form);
		model.addObject("oid", oid);
		model.setViewName("popup:/document/document-link");
		return model;
	}

	@Description(value = "문서 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<FormTemplate> form = FormTemplateHelper.manager.array();
//		JSONArray docTypeList = DocumentHelper.manager.toJson();

		// 문서 대분류
		ArrayList<Map<String, String>> classTypes1 = DocumentClassHelper.manager.getClassTypes1();

		model.addObject("classTypes1", classTypes1);
//		model.addObject("docTypeList", docTypeList);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("form", form);
		model.setViewName("/extcore/jsp/document/document-create.jsp");
		return model;
	}

	@Description(value = "문서 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody DocumentDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
		// 문서 대분류
		ArrayList<Map<String, String>> classTypes1 = DocumentClassHelper.manager.getClassTypes1();

		ModelAndView model = new ModelAndView();
		model.addObject("classTypes1", classTypes1);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("lifecycleList", lifecycleList);
		model.setViewName("/extcore/jsp/document/document-list.jsp");
		return model;
	}

	@Description(value = "문서 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "관련 문서 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi,
			@RequestParam(required = false) String state, @RequestParam(required = false) String location)
			throws Exception {
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
		DocumentType[] docTypeList = DocumentType.getDocumentTypeSet();
		ModelAndView model = new ModelAndView();
		model.addObject("state", state);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("docTypeList", docTypeList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("method", method);
		model.addObject("location", location);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/document/document-list-popup");
		return model;
	}

	@Description(value = "문서 상세보기")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		DocumentDTO dto = new DocumentDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/document/document-view");
		return model;
	}

	@Description(value = "문서 수정 및 개정 페이지")
	@GetMapping(value = "/update")
	public ModelAndView update(@RequestParam String oid, @RequestParam String mode) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		DocumentDTO dto = new DocumentDTO(oid);
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<FormTemplate> form = FormTemplateHelper.manager.array();
		JSONArray docTypeList = DocumentHelper.manager.toJson();
		model.addObject("docTypeList", docTypeList);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("form", form);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.addObject("mode", mode);
		model.setViewName("popup:/document/document-update");
		return model;
	}

	@Description(value = "문서 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody DocumentDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.modify(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 개정 함수")
	@ResponseBody
	@PostMapping(value = "/revise")
	public Map<String, Object> revise(@RequestBody DocumentDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.revise(dto);
			result.put("msg", REVISE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 삭제 함수")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			// true 연결 있음
			if (DocumentHelper.manager.isConnect(oid, DocumentECOLink.class)) {
				result.put("result", false);
				result.put("msg", "문서와 연결된 ECO가 있습니다.");
				return result;
			}

			if (DocumentHelper.manager.isConnect(oid, DocumentEOLink.class)) {
				result.put("result", false);
				result.put("msg", "문서와 연결된 EO가 있습니다.");
				return result;
			}

			if (DocumentHelper.manager.isConnect(oid, DocumentCRLink.class)) {
				result.put("result", false);
				result.put("msg", "문서와 연결된 CR이 있습니다.");
				return result;
			}

			if (DocumentHelper.manager.isConnect(oid, DocumentECPRLink.class)) {
				result.put("result", false);
				result.put("msg", "문서와 연결된 ECPR이 있습니다.");
				return result;
			}

			if (DocumentHelper.manager.isConnect(oid, WTPartDescribeLink.class)) {
				result.put("result", false);
				result.put("msg", "문서와 연결된 품목이 있습니다.");
				return result;
			}

			result = DocumentHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 최신버전 이동")
	@GetMapping(value = "/latest")
	public ModelAndView latest(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTDocument latest = DocumentHelper.manager.latest(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		DocumentDTO dto = new DocumentDTO(latest);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/document/document-view");
		return model;
	}

	@Description(value = "문서 일괄등록")
	@GetMapping(value = "/batch")
	public ModelAndView batch() throws Exception {
		JSONArray flist = DocumentHelper.manager.recurcive();
		JSONArray mlist = NumberCodeHelper.manager.toJson("MODEL");
		JSONArray dlist = NumberCodeHelper.manager.toJson("DEPTCODE");
		JSONArray nlist = NumberCodeHelper.manager.toJson("DOCUMENTNAME");
		JSONArray plist = NumberCodeHelper.manager.toJson("PRESERATION");
		JSONArray tlist = DocumentHelper.manager.toJson();
		ModelAndView model = new ModelAndView();
		model.addObject("flist", flist);
		model.addObject("mlist", mlist);
		model.addObject("dlist", dlist);
		model.addObject("nlist", nlist);
		model.addObject("plist", plist);
		model.addObject("tlist", tlist);
		model.setViewName("/extcore/jsp/document/document-batch.jsp");
		return model;
	}

	@Description(value = "문서 일괄등록")
	@ResponseBody
	@PostMapping(value = "/batch")
	public Map<String, Object> batch(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.batch(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 종료 바인더")
	@ResponseBody
	@PostMapping(value = "/finder")
	public Map<String, Object> finder(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, String>> list = DocumentHelper.manager.finder(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 관리자 강제 수정 페이지")
	@GetMapping(value = "/force")
	public ModelAndView force(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		DocumentDTO dto = new DocumentDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/document/document-force");
		return model;
	}

	@Description(value = "관리자 권한 수정 함수")
	@ResponseBody
	@PostMapping(value = "/force")
	public Map<String, Object> force(@RequestBody DocumentDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.force(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "버전 객체 이터레이션 정보 페이지")
	@GetMapping(value = "/iteration")
	public ModelAndView iteration(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("oid", oid);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/document/include/document-iteration-include");
		return model;
	}

	@Description(value = "문서 마지막 시퀀시")
	@ResponseBody
	@GetMapping(value = "/lastNumber")
	public Map<String, Object> lastNumber(@RequestParam String number, @RequestParam String classType1)
			throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String lastNumber = DocumentHelper.manager.lastNumber(number, classType1);
			result.put("lastNumber", lastNumber);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 이동 페이지")
	@GetMapping(value = "/move")
	public ModelAndView move(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Folder f = (Folder) CommonUtil.getObject(oid);
		JSONArray moveList = DocumentHelper.manager.getMoveTarget(f);
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("moveList", moveList);
		model.addObject("f", f);
		model.addObject("oid", oid);
		model.setViewName("popup:/document/document-move");
		return model;
	}

	@Description(value = "문서 이동 함수")
	@ResponseBody
	@PostMapping(value = "/move")
	public Map<String, Object> move(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.move(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
