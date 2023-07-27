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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.beans.ResultData;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.rohs.service.RohsHelper;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.folder.Folder;

@Controller
@RequestMapping(value = "/doc")
public class DocumentController extends BaseController {

	@Description(value = "문서 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/document/document-create.jsp");
		return model;
	}

	@Description(value = "문서 등록")
	@ResponseBody
	@PostMapping(value = "/create")
	public ResultData createDocumentAction(@RequestBody Map<String, Object> params) {
		Map<String, Object> map = DocumentHelper.service.requestDocumentMapping(params);
		return DocumentHelper.service.createDocumentAction(map);
	}

	@Description(value = "문서 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception{
		ModelAndView model = new ModelAndView();
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
//			ErrorLogHelper.service.create(e.toString(), "/doc/list", "문서 조회 함수");
		}
		return result;
	}

	@Description(value = "문서 상세보기")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam(value = "oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		DocumentData docData = new DocumentData(doc);

		model.setViewName("/extcore/jsp/document/document-view.jsp");
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("docData", docData);
		return model;
	}
//	/**
//	 * 문서 상세보기
//	 * 
//	 * @param request
//	 * @param response
//	 * @param oid
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("/viewDocument")
//	public ModelAndView viewDocument(HttpServletRequest request, HttpServletResponse response,
//			@RequestParam(value = "oid") String oid) throws Exception {
//		ModelAndView model = new ModelAndView();
//		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
//		DocumentData docData = new DocumentData(doc);
//		
//		model.setViewName("popup:/document/viewDocument");
//		model.addObject("isAdmin", CommonUtil.isAdmin());
//		model.addObject("docData", docData);
//		return model;
//	}

	/**
	 * 문서 삭제
	 * 
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/deleteDocumentAction")
	public ResultData deleteDocumentAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return DocumentHelper.service.deleteDocumentAction(request, response);

	}

	/**
	 * 문서 수정 페이지
	 * 
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateDocument")
	public ModelAndView updateDocument(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();

		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		DocumentData docData = new DocumentData(doc);

		model.setViewName("popup:/document/updateDocument");
		model.addObject("docData", docData);
		return model;
	}

	/**
	 * 문서 수정
	 * 
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateDocumentAction")
	public ResultData updateDocumentAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = DocumentHelper.service.requestDocumentMapping(request, response);
		return DocumentHelper.service.updateDocumentAction(map);
	}

	@Description(value = "문서 일괄등록")
	@GetMapping(value = "/batch")
	public ModelAndView batch() throws Exception{
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/document/document-batch.jsp");
		return model;
	}
	
	@Description(value = "문서 일괄결재")
	@GetMapping(value = "/all")
	public ModelAndView all() throws Exception{
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/document/document-all.jsp");
		return model;
	}

	/**
	 * 일괄 등록(AUI) 메뉴 이동
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createAUIPackageDocument")
	public ModelAndView createAUIPackageDocument(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module", "document");
		model.setViewName("default:/document/createAUIPackageDocument");
		return model;
	}

	/**
	 * 일괄 등록 수행
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createPackageDocumentAction")
	public ModelAndView createPackageDocumentAction(HttpServletRequest request, HttpServletResponse response) {
		String xmlString = DocumentHelper.service.createPackageDocumentAction(request, response);

		ModelAndView model = new ModelAndView();
		model.addObject("xmlString", xmlString);
		model.setViewName("empty:/document/createPackageDocumentAction");
		return model;
	}

	/**
	 * 일괄 등록 AUI 수행
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	@ResponseBody
	@RequestMapping(value = "/createAUIPackageDocumentAction", method = RequestMethod.POST)
	public ResultData createAUIPackageDocumentAction(HttpServletRequest request, HttpServletResponse response) {
		return DocumentHelper.service.createAUIPackageDocumentAction(request, response);
	}

	/**
	 * 관련 문서 추가
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_DocumentSelect")
	public ModelAndView include_DocumentSelect(HttpServletRequest request, HttpServletResponse response) {
		String moduleType = request.getParameter("moduleType");
		String oid = request.getParameter("oid");
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String type = request.getParameter("type");
		String state = StringUtil.checkReplaceStr(request.getParameter("state"), "");
		String searchType = StringUtil.checkReplaceStr(request.getParameter("searchType"), "");
		String lifecycle = StringUtil.checkReplaceStr(request.getParameter("lifecycle"), "LC_Default");
		List<DocumentData> list = null;
		try {
			list = DocumentHelper.service.include_DocumentList(oid, moduleType);
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<DocumentData>();
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/document/include_DocumentSelect");
		model.addObject("list", list);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("type", type);
		model.addObject("state", state);
		model.addObject("searchType", searchType);
		model.addObject("lifecycle", lifecycle);
		return model;
	}

	/**
	 * 문서 검색 팝업
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/selectDocPopup")
	public ModelAndView selectDocPopup(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String moduleType = request.getParameter("moduleType");
		String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil");
		String type = StringUtil.checkReplaceStr(request.getParameter("type"), "select");
		String state = StringUtil.checkReplaceStr(request.getParameter("state"), "");
		String searchType = StringUtil.checkReplaceStr(request.getParameter("searchType"), "");
		String lifecycle = StringUtil.checkReplaceStr(request.getParameter("lifecycle"), "LC_Default");

		model.addObject("mode", mode);
		model.addObject("modeulType", moduleType);
		model.addObject("type", type);
		model.addObject("state", state);
		model.addObject("searchType", searchType);
		model.addObject("lifecycle", lifecycle);
		model.setViewName("popup:/document/selectDocPopup");
		return model;
	}

	/**
	 * 문서 개정
	 * 
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/reviseDocument")
	public ResultData reviseDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultData data = null;
		String module = StringUtil.checkReplaceStr(request.getParameter("module"), "doc");
		if ("doc".equals(module)) {
			data = DocumentHelper.service.reviseUpdate(request, response);
		} else if ("rohs".equals(module)) {
			data = RohsHelper.service.reviseUpdate(request, response);
		}
		return data;
	}

	/**
	 * 관련 문서 보기
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_DocumentView")
	public ModelAndView include_DocumentView(HttpServletRequest request, HttpServletResponse response) {
		String moduleType = request.getParameter("moduleType");
		String oid = request.getParameter("oid");
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		List<DocumentData> list = null;
		try {
			list = DocumentHelper.service.include_DocumentList(oid, moduleType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ModelAndView model = new ModelAndView();
		model.addObject("moduleType", moduleType);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("list", list);
		model.addObject("distribute", distribute);
		model.setViewName("include:/document/include_DocumentView");
		return model;
	}

	/**
	 * 프로젝트 - 태스크 산출물 직접등록
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createDocumentPop")
	public ModelAndView createDocumentPop(HttpServletRequest request, HttpServletResponse response) {
		String parentOid = request.getParameter("parentOid");
		String type = request.getParameter("type");
		ModelAndView model = new ModelAndView();

		model.addObject("oLocation", "/Default/Document");
		model.addObject("parentOid", parentOid);
		model.addObject("type", type);
		model.setViewName("popup:/document/createDocumentPop");

		return model;
	}

	/**
	 * 프로젝트 - 태스크 산출물 링크등록
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createDocumentLink")
	public ModelAndView createDocumentLink(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String parentOid = request.getParameter("parentOid");
		String type = request.getParameter("type");
		ModelAndView model = new ModelAndView();
		Folder folder = null;
		folder = FolderTaskLogic.getFolder("/Default/Document", WCUtil.getWTContainerRef());
		model.addObject("folder", folder.getFolderPath());
		model.addObject("parentOid", parentOid);
		model.addObject("type", type);
		model.setViewName("popup:/document/createDocumentLink");
		return model;
	}

	@ResponseBody
	@RequestMapping("/createDocumentLinkAction")
	public Map<String, Object> createDocumentLinkAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = null;

		try {
			map = DocumentHelper.service.createDocumentLinkAction(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			map = new HashMap<String, Object>();
		}

		return map;
	}

	@RequestMapping("/include_documentLink")
	public ModelAndView include_documentLink(HttpServletRequest request, HttpServletResponse response) {
		String module = request.getParameter("module");
		String oid = request.getParameter("oid");
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("관련 문서"));
		String enabled = StringUtil.checkReplaceStr(request.getParameter("enabled"), "false");

		List<DocumentData> list = DocumentHelper.service.include_documentLink(module, oid);

		ModelAndView model = new ModelAndView();
		model.setViewName("empty:/document/include_documentLink");
		model.addObject("module", module);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("list", list);
		model.addObject("enabled", Boolean.valueOf(enabled));
		return model;
	}

	@ResponseBody
	@RequestMapping("/linkDocumentAction")
	public ResultData linkDocumentAction(HttpServletRequest request, HttpServletResponse response) {
		return DocumentHelper.service.linkDocumentAction(request, response);
	}

	@ResponseBody
	@RequestMapping("/deleteDocumentLinkAction")
	public ResultData deleteDocumentLinkAction(HttpServletRequest request, HttpServletResponse response) {
		return DocumentHelper.service.deleteDocumentLinkAction(request, response);
	}

	@RequestMapping("/reviseDocumentPopup")
	public ModelAndView reviseDocumentPopup(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("oid") String oid) {
		String module = StringUtil.checkReplaceStr(request.getParameter("module"), "doc");
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("module", module);
		model.setViewName("popup:/document/reviseDocumentPopup");
		return model;
	}

	/**
	 * 일괄 등록 추가 AUI
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchDocumentCreate")
	public ModelAndView batchDocumentCreate(HttpServletRequest request, HttpServletResponse response) {

		String auiId = StringUtil.checkNull(request.getParameter("auiId"));
		String mode = StringUtil.checkNull(request.getParameter("mode")); // single

		String title = "일괄 추가 ";
		if (mode.equals("single")) {
			title = "수정[" + auiId + "]";
		}
		// System.out.println("batchCreate auiId =" + auiId);

		ModelAndView model = new ModelAndView();
		model.addObject("auiId", auiId);
		model.addObject("mode", mode);
		model.addObject("title", title);
		model.addObject("oLocation", "/Default/Document");

		model.setViewName("popup:/document/batchDocumentCreate");
		return model;
	}
}
