package com.e3ps.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import wt.doc.WTDocument;
import wt.org.WTUser;
import wt.session.SessionHelper;

import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.dto.RohsData;
import com.e3ps.rohs.service.RohsHelper;
import com.e3ps.rohs.service.RohsQueryHelper;

@Controller
@RequestMapping(value = "/mold/**")
public class MoldController extends BaseController {
	
	@Description(value = "금형 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> moldTypeList = NumberCodeHelper.manager.getArrayCodeList("MOLDTYPE");
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("moldTypeList", moldTypeList);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/mold/mold-list.jsp");
		return model;
	}
	
	@Description(value = "금형 검색")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentHelper.manager.listMoldAction(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "금형 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception{
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> moldtypeList = NumberCodeHelper.manager.getArrayCodeList("MOLDTYPE");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/mold/mold-create.jsp");
		model.addObject("manufactureList", manufactureList);
		model.addObject("moldtypeList", moldtypeList);
		model.addObject("deptcodeList", deptcodeList);
		return model;
	}
	
	@Description(value = "금형 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String,Object> create(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "금형 상세 페이지")
	@GetMapping(value =  "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception{
		ModelAndView model = new ModelAndView();
		WTDocument doc = (WTDocument)CommonUtil.getObject(oid);
		DocumentDTO dto = new DocumentDTO(doc);
		
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/mold/mold-view.jsp");
		return model;
	}
	
	@Description(value = "금형 일괄결재 페이지")
	@GetMapping(value = "/all")
	public ModelAndView all() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/mold/mold-all.jsp");
		return model;
	}
	
	@Description(value = "승인원 검색 페이지")
	@GetMapping(value = "/ap-list")
	public ModelAndView apList() throws Exception {
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> moldTypeList = NumberCodeHelper.manager.getArrayCodeList("MOLDTYPE");
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("moldTypeList", moldTypeList);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/ap/ap-list.jsp");
		return model;
	}
	
	@Description(value = "승인원 등록 페이지")
	@GetMapping(value = "/ap-create")
	public ModelAndView apCreate() throws Exception{
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> moldtypeList = NumberCodeHelper.manager.getArrayCodeList("MOLDTYPE");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/ap/ap-create.jsp");
		model.addObject("manufactureList", manufactureList);
		model.addObject("moldtypeList", moldtypeList);
		model.addObject("deptcodeList", deptcodeList);
		return model;
	}
	
	@Description(value = "승인원 결재 페이지")
	@GetMapping(value = "/ap-all")
	public ModelAndView apAll() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/ap/ap-all.jsp");
		return model;
	}
	
	/**	문서 상세보기
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
//	@RequestMapping("/viewMold")
//	public ModelAndView viewMold(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
//		ModelAndView model = new ModelAndView();
//		WTDocument doc = (WTDocument)CommonUtil.getObject(oid);
//		DocumentData docData = new DocumentData(doc);
//		
//		model.setViewName("popup:/mold/viewMold");
//		model.addObject("isAdmin", CommonUtil.isAdmin());
//		model.addObject("docData", docData);
//		return model;
//	}
	
	/**  일괄 등록 메뉴 이동
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createPackageMold")
	public ModelAndView createPackageMold(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module","mold");
		model.setViewName("default:/mold/createPackageMold");
		return model;
	}
	
	/**	 일괄 등록 수행
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createPackageMoldAction")
	public ModelAndView createPackageMoldAction(HttpServletRequest request, HttpServletResponse response) {
		String xmlString = DocumentHelper.service.createPackageDocumentAction(request, response);
		
		ModelAndView model = new ModelAndView();
		model.addObject("xmlString", xmlString);
		model.setViewName("empty:/mold/createPackageMoldAction");
		return model;
	}
	
	/**	일괄 결제 메뉴 이동
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/approvalPackageMold")
	public ModelAndView approvalPackageMold(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu4");
		model.addObject("module","mold");
		model.setViewName("default:/mold/approvalPackageMold");
		return model;
	}
	
	/**	일괄 결제 실행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/approvalPackageMoldAction")
	public ResultData approvalPackageMoldAction(HttpServletRequest request, HttpServletResponse response) {
		return DocumentHelper.service.approvalPackageDocumentAction(request, response);
	}
	
	/**	관련 금형, rohs 추가
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_OtherDocumentSelect")
	public ModelAndView include_OtherDocumentSelect(HttpServletRequest request, HttpServletResponse response) {
		String moduleType = request.getParameter("moduleType");
		String oid = request.getParameter("oid");
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String type = request.getParameter("type");
		String state = StringUtil.checkReplaceStr(request.getParameter("state"),"");
		String searchType = StringUtil.checkReplaceStr(request.getParameter("searchType"),"");
		String lifecycle = StringUtil.checkReplaceStr(request.getParameter("lifecycle"), "LC_Default");
		List<DocumentDTO> list = null;
		try {
			list = DocumentHelper.service.include_DocumentList(oid,moduleType);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<DocumentDTO>();
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/mold/include_OtherDocumentSelect");
		model.addObject("list", list);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("type", type);
		model.addObject("state",state);
		model.addObject("searchType",searchType);
		model.addObject("lifecycle",lifecycle);
		return model;
	}
	
	/** 금형, rohs 검색 팝업
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/selectOtherPopup")
	public ModelAndView selectOtherPopup(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String moduleType = request.getParameter("moduleType");
		String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil");
		String type = StringUtil.checkReplaceStr(request.getParameter("type"), "select");
		String state = StringUtil.checkReplaceStr(request.getParameter("state"), "");
		String searchType = StringUtil.checkReplaceStr(request.getParameter("searchType"),"");
		String lifecycle = StringUtil.checkReplaceStr(request.getParameter("lifecycle"), "LC_Default");
		String nameValue = "";
		
		if(searchType.equals("MOLD")){
			nameValue = "${f:getMessage('금형')}";
		}else if(searchType.equals("rohs")){
			nameValue = "${f:getMessage('물질')}";
		}
		
		model.addObject("mode", mode);
		model.addObject("modeulType", moduleType);
		model.addObject("type", type);
		model.addObject("state", state);
		model.addObject("searchType",searchType);
		model.addObject("lifecycle", lifecycle);
		model.addObject("nameValue", nameValue);
		model.setViewName("popup:/mold/selectOtherPopup");
		return model;
	}
}
