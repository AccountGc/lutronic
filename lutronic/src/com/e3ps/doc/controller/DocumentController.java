package com.e3ps.doc.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.e3ps.admin.form.FormTemplate;
import com.e3ps.admin.form.service.FormTemplateHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.DocumentCRLink;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.DocumentECPRLink;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.doc.service.DocumentHelper;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.folder.Folder;
import wt.part.WTPartDescribeLink;
import wt.util.WTProperties;

@Controller
@RequestMapping(value = "/doc")
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
		JSONArray docTypeList = DocumentHelper.manager.toJson();
		model.addObject("docTypeList", docTypeList);
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
		JSONArray docTypeList = DocumentHelper.manager.toJson();
		ModelAndView model = new ModelAndView();
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("docTypeList", docTypeList);
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
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
		DocumentType[] docTypeList = DocumentType.getDocumentTypeSet();
		ModelAndView model = new ModelAndView();
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("docTypeList", docTypeList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("method", method);
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

	@Description(value = "문서 일괄결재")
	@GetMapping(value = "/register")
	public ModelAndView register() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/document/document-register");
		return model;
	}

	@Description(value = "문서 일괄결재 등록 실행")
	@ResponseBody
	@PostMapping(value = "/register")
	public Map<String, Object> register(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.register(params);
			result.put("msg", "일괄결재가 등록 되었습니다.");
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
	
}
