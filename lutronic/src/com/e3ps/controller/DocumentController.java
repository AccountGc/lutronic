package com.e3ps.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.beans.NumberCodeData;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.comments.CommentsData;
import com.e3ps.common.message.Message;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.FolderUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.doc.template.SmarteditorVO;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.rohs.service.RohsHelper;
import com.infoengine.util.UrlEncoder;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.folder.Folder;

@Controller
@RequestMapping(value = "/doc")
public class DocumentController extends BaseController {

	@Description(value = "문서 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		model.setViewName("/extcore/jsp/document/document-create.jsp");
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		return model;
	}

	@Description(value = "문서 등록")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.create(params);
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
		DocumentType[] docTypeList = DocumentType.getDocumentTypeSet();
		ModelAndView model = new ModelAndView();
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("docTypeList", docTypeList);
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
		Map<String, String> map = CommonHelper.manager.getAttributes(oid, "view");
		List<CommentsData> cList = DocumentHelper.manager.commentsList(oid);
		String pnum = DocumentHelper.manager.getCnum(cList);

		model.setViewName("popup:/document/document-view");
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("docData", docData);
		model.addObject("cList", cList);
		model.addObject("pnum", pnum);
		model.addAllObjects(map);
		return model;
	}

	@Description(value = "문세 템플릿 검색 페이지 이동")
	@GetMapping(value = "/template-list")
	public ModelAndView templateList() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/document/template-list.jsp");
		return model;
	}

	@Description(value = "문서 템플릿 리스트 불러오기")
	@ResponseBody
	@PostMapping(value = "/template-list")
	public Map<String, Object> templateList(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentHelper.manager.docTemplateList(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문세 템플릿 등록 페이지 이동")
	@GetMapping(value = "/template-create")
	public ModelAndView templateCreate() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<NumberCode> documentTemplateTypeList = NumberCodeHelper.manager.getArrayCodeList("DOCFORMTYPE");
		model.setViewName("/extcore/jsp/document/template-create.jsp");
		model.addObject("documentTemplateTypeList", documentTemplateTypeList);
		return model;
	}

	@Description(value = "문서 템플릿 등록 함수")
	@ResponseBody
	@PostMapping(value = "/template-create")
	public Map<String, Object> templateCreate(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.createTemplate(params);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "댓글 등록 함수")
	@ResponseBody
	@PostMapping(value = "/createComments")
	public Map<String, Object> createComments(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.createComments(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "댓글 수정 함수")
	@ResponseBody
	@PostMapping(value = "/updateComments")
	public Map<String, Object> updateComments(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.updateComments(params);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "댓글 삭제 함수")
	@ResponseBody
	@GetMapping(value = "/deleteComments")
	public Map<String, Object> deleteComments(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.deleteComments(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 개정 페이지")
	@GetMapping(value = "/reviseDocument")
	public ModelAndView reviseDocument(@RequestParam("oid") String oid, HttpServletRequest request) {
		String module = StringUtil.checkReplaceStr(request.getParameter("module"), "doc");
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("module", module);
		model.setViewName("/extcore/jsp/document/document-revise.jsp");
		return model;
	}

	@Description(value = "문서 개정")
	@ResponseBody
	@PostMapping(value = "/reviseDocument")
	public ResultData reviseDocument(@RequestBody Map<String, Object> params) throws Exception {
		ResultData data = null;
		String module = StringUtil.checkReplaceStr((String) params.get("module"), "doc");
		if ("doc".equals(module)) {
			data = DocumentHelper.service.reviseUpdate(params);
		} else if ("rohs".equals(module)) {
			data = RohsHelper.service.reviseUpdate(params);
		}
		return data;
	}

	@Description(value = "문서 삭제")
	@ResponseBody
	@RequestMapping("/delete")
	public Map<String, Object> delete(@RequestBody Map<String, Object> params) {

		Map<String, Object> result = DocumentHelper.service.deleteDocumentAction(params);
		if ((boolean) result.get("result")) {
			result.put("oid", result.get("oid"));
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} else {
			result.put("result", FAIL);
			result.put("msg", (String) result.get("msg"));
		}
		return result;
	}

	@Description(value = "문서 수정 페이지")
	@GetMapping(value = "/update")
	public ModelAndView updateDocument(HttpServletRequest request, @RequestParam(value = "oid") String oid)
			throws Exception {
		ModelAndView model = new ModelAndView();
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		DocumentData docData = new DocumentData(doc);
		String module = StringUtil.checkReplaceStr(request.getParameter("module"), "doc");
		model.addObject("oid", oid);
		model.addObject("module", module);
		model.setViewName("/extcore/jsp/document/document-update.jsp");
		model.addObject("docData", docData);
		return model;
	}

	@Description(value = "문서 수정")
	@ResponseBody
	@PostMapping(value = "/update")
	public Map<String, Object> update(@RequestBody Map<String, Object> params) {

		Map<String, Object> result = DocumentHelper.service.updateDocumentAction(params);
		if ((boolean) result.get("result")) {
			result.put("oid", result.get("oid"));
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} else {
			result.put("result", FAIL);
			result.put("msg", (String) result.get("msg"));
		}
		return result;
	}

	@Description(value = "문서 일괄등록")
	@GetMapping(value = "/batch")
	public ModelAndView batch() throws Exception {

		String container = "document";
		String location = DocumentHelper.DOCUMENT_ROOT;

		ArrayList<Folder> folderList = FolderUtils.loadAllFolder(location, container);
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> documentNameList = NumberCodeHelper.manager.getArrayCodeList("DOCUMENTNAME");
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/document/document-batch.jsp");
		model.addObject("folderList", folderList);
		model.addObject("modelList", modelList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("documentNameList", documentNameList);
		return model;
	}

	@Description(value = "문서 등록")
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

	@Description(value = "문서 일괄결재")
	@GetMapping(value = "/register")
	public ModelAndView register() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/document/document-register.jsp");
		return model;
	}

	@Description(value = "문서 일괄결재 등록 실행")
	@ResponseBody
	@PostMapping(value = "/register")
	public Map<String, Object> register(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.register(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
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

//	/**
//	 * 문서 개정
//	 * 
//	 * @param request
//	 * @param response
//	 * @param oid
//	 * @return
//	 * @throws Exception
//	 */
//	@ResponseBody
//	@RequestMapping("/reviseDocument")
//	public ResultData reviseDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ResultData data = null;
//		String module = StringUtil.checkReplaceStr(request.getParameter("module"), "doc");
//		if ("doc".equals(module)) {
//			data = DocumentHelper.service.reviseUpdate(request, response);
//		} else if ("rohs".equals(module)) {
//			data = RohsHelper.service.reviseUpdate(request, response);
//		}
//		return data;
//	}

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

//	@RequestMapping("/reviseDocumentPopup")
//	public ModelAndView reviseDocumentPopup(HttpServletRequest request, HttpServletResponse response,
//			@RequestParam("oid") String oid) {
//		String module = StringUtil.checkReplaceStr(request.getParameter("module"), "doc");
//		ModelAndView model = new ModelAndView();
//		model.addObject("oid", oid);
//		model.addObject("module", module);
//		model.setViewName("popup:/document/reviseDocumentPopup");
//		return model;
//	}

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

	@Description(value = "문서 양식 사진 업로드 창에 첨부 메서드")
	@PostMapping(value = "/smarteditorMultiImageUpload")
	public void smarteditorMultiImageUpload(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 파일정보
			String sFileInfo = "";
			// 파일명을 받는다 - 일반 원본파일명
			String sFilename = request.getHeader("file-name");
			// 파일 확장자
			String sFilenameExt = sFilename.substring(sFilename.lastIndexOf(".") + 1);
			// 확장자를소문자로 변경
			sFilenameExt = sFilenameExt.toLowerCase();

			// 이미지 검증 배열변수
			String[] allowFileArr = { "jpg", "png", "bmp", "gif" };

			// 확장자 체크
			int nCnt = 0;
			for (int i = 0; i < allowFileArr.length; i++) {
				if (sFilenameExt.equals(allowFileArr[i])) {
					nCnt++;
				}
			}

			// 이미지가 아니라면
			if (nCnt == 0) {
				PrintWriter print = response.getWriter();
				print.print("NOTALLOW_" + sFilename);
				print.flush();
				print.close();
			} else {
				// 디렉토리 설정 및 업로드

				// 파일경로
				String defaultPath = request.getSession().getServletContext().getRealPath("/");
				String filePath = defaultPath + "img" + File.separator + "smarteditor2" + File.separator;
				System.out.println("=======================>" + filePath);
				File file = new File(filePath);

				if (!file.exists()) {
					file.mkdirs();
				}

				String sRealFileNm = "";
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				String today = formatter.format(new java.util.Date());
				sRealFileNm = today + UUID.randomUUID().toString() + sFilename.substring(sFilename.lastIndexOf("."));
				String rlFileNm = filePath + sRealFileNm;

				///////////////// 서버에 파일쓰기 /////////////////
				InputStream inputStream = request.getInputStream();
				OutputStream outputStream = new FileOutputStream(rlFileNm);
				int numRead;
				byte bytes[] = new byte[Integer.parseInt(request.getHeader("file-size"))];
				while ((numRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
					outputStream.write(bytes, 0, numRead);
				}
				if (inputStream != null) {
					inputStream.close();
				}
				outputStream.flush();
				outputStream.close();

				///////////////// 이미지 /////////////////
				// 정보 출력
				sFileInfo += "&bNewLine=true";
				// img 태그의 title 속성을 원본파일명으로 적용시켜주기 위함
				sFileInfo += "&sFileName=" + sFilename;
				sFileInfo += "&sFileURL=" + filePath + sRealFileNm;
				PrintWriter printWriter = response.getWriter();
				printWriter.print(sFileInfo);
				printWriter.flush();
				printWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
