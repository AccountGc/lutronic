package com.e3ps.drawing.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.download.service.DownloadHistoryHelper;
import com.e3ps.drawing.beans.EpmData;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.part.service.PartHelper;
import com.ptc.wvs.server.ui.UIHelper;
import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.session.SessionHelper;
import wt.util.FileUtil;
import wt.util.WTException;

@Controller
@RequestMapping(value = "/drawing/**")
public class DrawingController extends BaseController {

	@Description(value = "도면 썸네일 팝업")
	@GetMapping(value = "/viewThumb")
	public ModelAndView viewThumb(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EPMDocument doc = (EPMDocument) CommonUtil.getObject(oid);
		String docType = doc.getDocType().toString();
		if (docType.equals("CADDRAWING")) {
			model.setViewName("popup:/common/thumbnail-view-2d");
		} else {
			model.setViewName("popup:/common/thumbnail-view-3d");
		}
		model.addObject("oid", oid);
		return model;
	}

	@Description(value = "썸네일 여부 체크")
	@ResponseBody
	@GetMapping(value = "/checkThumb")
	public Map<String, Object> checkThumb(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
			Representation representation = PublishUtils.getRepresentation(epm);
			String docType = epm.getDocType().toString();
			boolean isDrawing = false;
			if ("CADDRAWING".equals(docType)) {
				isDrawing = true;
			}
			if (representation == null) {
				result.put("exist", false);
			} else {
				result.put("exist", true);
				result.put("isDrawing", isDrawing);
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			result.put("result", FAIL);
			result.put("msg", e.toString());
			e.printStackTrace();
		}
		return result;
	}

	@Description(value = "크레오 뷰 URL 얻기")
	@ResponseBody
	@GetMapping(value = "/getCreoViewUrl")
	public Map<String, Object> getCreoViewUrl(HttpServletRequest request, @RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			String url = DrawingHelper.manager.getCreoViewUrl(request, oid);
			result.put("result", SUCCESS);
			result.put("url", url);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "도면 재변환")
	@ResponseBody
	@GetMapping(value = "/publish")
	public Map<String, Object> publish(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			DrawingHelper.service.publish(oid);
			result.put("msg", "재변환이 요청 되었습니다.\n잠시 후 데이터 확인을 해주세요.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "도면 일괄 등록")
	@ResponseBody
	@PostMapping(value = "/progress")
	public Map<String, Object> progress(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DrawingHelper.manager.progress(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "도면 일괄 다운로드 페이지")
	@GetMapping(value = "/download")
	public ModelAndView download() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/drawing/drawing-batch-download");
		return model;
	}

	@Description(value = "도면 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		List<Map<String, String>> cadTypeList = DrawingHelper.manager.cadTypeList();
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_PART");
		QuantityUnit[] unitList = QuantityUnit.getQuantityUnitSet();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("finishList", finishList);
		model.addObject("sessionUser", sessionUser);
		model.addObject("cadTypeList", cadTypeList);
		model.addObject("unitList", unitList);
		model.addObject("lifecycleList", lifecycleList);
		model.setViewName("/extcore/jsp/drawing/drawing-list.jsp");
		return model;
	}

	@Description(value = "도면 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/drawing/drawing-create.jsp");
		return model;
	}

	@Description(value = "도면 일괄 등록 페이지")
	@GetMapping(value = "/batch")
	public ModelAndView batch() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/drawing/drawing-batch.jsp");
		return model;
	}

	@Description(value = "도면 일괄 등록")
	@ResponseBody
	@PostMapping(value = "/batch")
	public Map<String, Object> batch(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, Object>> gridData = (ArrayList<Map<String, Object>>) params.get("gridData");
			for (Map<String, Object> data : gridData) {
				ArrayList<Map<String, Object>> rows91 = (ArrayList<Map<String, Object>>) data.get("rows91");
				if (DrawingHelper.manager.isExist(rows91)) {
					result.put("result", FAIL);
					result.put("msg", "주 도면이 존재합니다.");
					return result;
				}
			}
			DrawingHelper.service.batch(gridData);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "도면 검색 리스트 리턴")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DrawingHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "도면 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DrawingHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "도면 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EPMDocument doc = (EPMDocument) CommonUtil.getObject(oid);
		EpmData dto = new EpmData(doc);

		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/drawing/drawing-view");
		return model;
	}

	@Description(value = "도면 최신버전 이동")
	@GetMapping(value = "/latest")
	public ModelAndView latest(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EPMDocument latest = DrawingHelper.manager.latest(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		EpmData dto = new EpmData(latest);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/drawing/drawing-view");
		return model;
	}

	/**
	 * CAD 구분 리스트 리턴
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cadDivisionList")
	public List<Map<String, String>> cadDivisionList(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, String>> list = DrawingHelper.service.cadDivisionList();
		return list;
	}

	/**
	 * CAD 타입 리스트 리턴
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cadTypeList")
	public List<Map<String, String>> cadTypeList(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, String>> list = DrawingHelper.service.cadTypeList();
		return list;
	}

	@RequestMapping("/include_Reference")
	public ModelAndView include_Reference(HttpServletRequest request, HttpServletResponse response) {
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), "참조");
		String moduleType = request.getParameter("moduleType");
		String oid = request.getParameter("oid");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		List<EpmData> list = null;
		try {
			list = DrawingHelper.service.include_Reference(oid, moduleType);
			// HashSet 데이터 형태로 생성되면서 중복 제거됨
			HashSet<EpmData> hs = new HashSet<EpmData>(list);

			// ArrayList 형태로 다시 생성
			list = new ArrayList<EpmData>(hs);
		} catch (Exception e) {
			list = new ArrayList<EpmData>();
			e.printStackTrace();
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/drawing/include_Reference");
		model.addObject("title", title);
		model.addObject("moduleType", moduleType);
		model.addObject("list", list);
		model.addObject("distribute", distribute);
		return model;
	}

	@Description(value = "참조 항목")
	@PostMapping("/include_ReferenceBy")
	@ResponseBody
	public Map<String, Object> include_ReferenceBy(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		String title = StringUtil.checkReplaceStr((String) params.get("title"), "참조 항목");
		String distribute = StringUtil.checkNull((String) params.get("distribute"));

		List<EpmData> list = new ArrayList<EpmData>();
		String oid = (String) params.get("oid");
		if (StringUtil.checkString(oid)) {
			EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
			List<EPMReferenceLink> refList = EpmSearchHelper.service
					.getEPMReferenceList((EPMDocumentMaster) epm.getMaster());
			for (EPMReferenceLink link : refList) {
				EPMDocument epmdoc = link.getReferencedBy();
				EpmData data = new EpmData(epmdoc);

				data.setLinkRefernceType(link.getReferenceType().getDisplay(Message.getLocale()));

				list.add(data);
			}
		}
		result.put("title", title);
		result.put("list", list);
		result.put("distribute", distribute);
		return result;
	}

	@Description(value = "도면 삭제")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> deleteDrwaingAction(@RequestParam String oid) {
		Map<String, Object> result = DrawingHelper.service.delete(oid);
		if ((boolean) result.get("result")) {
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} else {
			result.put("result", FAIL);
			result.put("msg", (String) result.get("msg"));
		}
		return result;
	}

	/**
	 * 도면 선택 페이지
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/selectDrawingPopup")
	public ModelAndView selectDrawingPopup(HttpServletRequest request, HttpServletResponse response) {
		String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil");
		ModelAndView model = new ModelAndView();
		model.addObject("mode", mode);
		model.setViewName("popup:/drawing/selectDrawingPopup");
		return model;
	}

	@Description(value = "도면 수정 페이지")
	@GetMapping(value = "/update")
	public ModelAndView update(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
		EpmData dto = new EpmData(epm);
		model.addObject("dto", dto);
		model.setViewName("popup:/drawing/drawing-update");
		return model;
	}

	@Description(value = "도면 수정 함수")
	@ResponseBody
	@PostMapping(value = "/update")
	public Map<String, Object> update(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DrawingHelper.service.update(params);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	/**
	 * 관련 도면 추가
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_DrawingSelect")
	public ModelAndView include_DrawingSelect(HttpServletRequest request, HttpServletResponse response) {
		String moduleType = request.getParameter("moduleType");
		String oid = request.getParameter("oid");
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String epmType = StringUtil.checkReplaceStr(request.getParameter("epmType"), "");

		List<EpmData> list = null;
		try {
			list = DrawingHelper.service.include_DrawingList(oid, moduleType, epmType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ModelAndView model = new ModelAndView();
		model.addObject("moduleType", moduleType);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("list", list);
		model.setViewName("include:/drawing/include_DrawingSelect");
		return model;
	}

	/**
	 * 관련 도면 보기
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_DrawingView")
	public ModelAndView include_DrawingView(HttpServletRequest request, HttpServletResponse response) {
		String moduleType = request.getParameter("moduleType");
		String oid = request.getParameter("oid");
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String epmType = StringUtil.checkReplaceStr(request.getParameter("epmType"), "");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		// System.out.println("include_DrawingView distribute =" + distribute);
		List<EpmData> list = null;
		try {
			list = DrawingHelper.service.include_DrawingList(oid, moduleType, epmType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ModelAndView model = new ModelAndView();
		model.addObject("moduleType", moduleType);
		model.addObject("epmType", epmType);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("list", list);
		model.addObject("distribute", distribute);
		model.setViewName("include:/drawing/include_DrawingView");
		return model;
	}

	/**
	 * 도면 미리보기
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/thumbview")
	public ModelAndView thumbview(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();

		Map<String, Object> thumbInfoMap;
		try {
			thumbInfoMap = DrawingHelper.service.thumbView(request);
			model.addAllObjects(thumbInfoMap);
		} catch (WTException e) {
			e.printStackTrace();
		}
		model.setViewName("include:/drawing/thumbview");
		return model;
	}

	@RequestMapping("/include_drawingLink")
	public ModelAndView include_drawingLink(HttpServletRequest request, HttpServletResponse response) {
		String module = request.getParameter("module");
		String oid = request.getParameter("oid");
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("관련 도면"));
		String enabled = StringUtil.checkReplaceStr(request.getParameter("enabled"), "false");

		List<EpmData> list = DrawingHelper.service.include_drawingLink(module, oid);

		ModelAndView model = new ModelAndView();
		model.setViewName("empty:/drawing/include_drawingLink");
		model.addObject("module", module);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("list", list);
		model.addObject("enabled", Boolean.valueOf(enabled));
		return model;
	}

	/**
	 * 도면 삭제
	 * 
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateNameAction")
	public ResultData updateNameAction(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("oid") String oid) {

		return DrawingHelper.service.updateNameAction(request, response);
	}

	@RequestMapping("/createPackageDrawing")
	public ModelAndView createPackageDrawing(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module", "drawing");
		model.setViewName("default:/drawing/createPackageDrawing");
		return model;
	}

	@Description(value = "SAP 에서 호출할 주소")
	@GetMapping(value = "/viewToSap")
	public ModelAndView viewToSap(@RequestParam String number, @RequestParam String version) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = PartHelper.manager.getPart(number, version);
		if (part != null) {
			EPMDocument epm = PartHelper.manager.getEPMDocument(part);
			if (epm != null) {
//				model.setViewName("/extcore/jsp/part/part-thumbnail.jsp");
				EpmData dto = new EpmData(epm);
				boolean isAdmin = CommonUtil.isAdmin();
				model.addObject("isAdmin", isAdmin);
				model.addObject("dto", dto);
				model.setViewName("/extcore/jsp/drawing/drawing-view.jsp");
			}
		}
		return model;
	}

	@Description(value = "PDF 다운로드")
	@GetMapping(value = "/pdf")
	public ResponseEntity<byte[]> pdf(@RequestParam String oid) {
		HttpHeaders headers = new HttpHeaders();
		byte[] bytes = null;
		try {

			Persistable per = CommonUtil.getObject(oid);
			EPMDocument epm = null;
			if (per instanceof WTPart) {
				WTPart part = (WTPart) per;
				epm = PartHelper.manager.getEPMDocument(part);
			} else if (per instanceof EPMDocument) {
				epm = (EPMDocument) per;
			}
			if (epm != null) {
				EPMDocument epm_d = PartHelper.manager.getEPMDocument2D(epm);
				if (epm_d != null) {
					Representable representable = PublishUtils.findRepresentable(epm_d);
					Representation representation = PublishUtils.getRepresentation(representable, true, null, false);
					if (representation != null) {
						QueryResult result = ContentHelper.service.getContentsByRole(representation,
								ContentRoleType.ADDITIONAL_FILES);
						while (result.hasMoreElements()) {
							ApplicationData data = (ApplicationData) result.nextElement();
							String ext = FileUtil.getExtension(data.getFileName());
							if (!"dxf".equalsIgnoreCase(ext)) {
								continue;
							}
							// 다운로드 이력 생성..
							DownloadHistoryHelper.service.create(oid);

							InputStream is = ContentServerHelper.service.findLocalContentStream(data);

							ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
							byte[] buffer = new byte[1024];
							int length;
							while ((length = is.read(buffer)) != -1) {
								byteArrayOutputStream.write(buffer, 0, length);
							}

							bytes = byteArrayOutputStream.toByteArray();
							String name = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");

							headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
							headers.setContentLength(bytes.length);
							headers.setContentDispositionFormData("attachment", name);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}

	@Description(value = "STEP 다운로드")
	@GetMapping(value = "/step")
	public ResponseEntity<byte[]> step(@RequestParam String oid) {
		HttpHeaders headers = new HttpHeaders();
		byte[] bytes = null;
		try {

			Persistable per = CommonUtil.getObject(oid);
			EPMDocument epm = null;
			if (per instanceof WTPart) {
				WTPart part = (WTPart) per;
				epm = PartHelper.manager.getEPMDocument(part);
			} else if (per instanceof EPMDocument) {
				epm = (EPMDocument) per;
			}
			if (epm != null) {
				QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
				while (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();
					String ext = FileUtil.getExtension(data.getFileName());
					if (!"stp".equalsIgnoreCase(ext)) {
						continue;
					}
					// 다운로드 이력 생성..
					DownloadHistoryHelper.service.create(oid);

					InputStream is = ContentServerHelper.service.findLocalContentStream(data);

					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int length;
					while ((length = is.read(buffer)) != -1) {
						byteArrayOutputStream.write(buffer, 0, length);
					}

					bytes = byteArrayOutputStream.toByteArray();
					String name = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");

					headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
					headers.setContentLength(bytes.length);
					headers.setContentDispositionFormData("attachment", name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}

	@Description(value = "DXF 다운로드")
	@GetMapping(value = "/dxf")
	public ResponseEntity<byte[]> dxf(@RequestParam String oid) {
		HttpHeaders headers = new HttpHeaders();
		byte[] bytes = null;
		try {

			Persistable per = CommonUtil.getObject(oid);
			EPMDocument epm = null;
			if (per instanceof WTPart) {
				WTPart part = (WTPart) per;
				epm = PartHelper.manager.getEPMDocument(part);
			} else if (per instanceof EPMDocument) {
				epm = (EPMDocument) per;
			}
			if (epm != null) {
				EPMDocument epm_d = PartHelper.manager.getEPMDocument2D(epm);
				if (epm_d != null) {
					Representable representable = PublishUtils.findRepresentable(epm_d);
					Representation representation = PublishUtils.getRepresentation(representable, true, null, false);
					if (representation != null) {
						QueryResult result = ContentHelper.service.getContentsByRole(representation,
								ContentRoleType.SECONDARY);
						while (result.hasMoreElements()) {
							ApplicationData data = (ApplicationData) result.nextElement();
							String ext = FileUtil.getExtension(data.getFileName());
							if (!"dxf".equalsIgnoreCase(ext)) {
								continue;
							}
							// 다운로드 이력 생성..
							DownloadHistoryHelper.service.create(oid);

							InputStream is = ContentServerHelper.service.findLocalContentStream(data);

							ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
							byte[] buffer = new byte[1024];
							int length;
							while ((length = is.read(buffer)) != -1) {
								byteArrayOutputStream.write(buffer, 0, length);
							}

							bytes = byteArrayOutputStream.toByteArray();
							String name = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");

							headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
							headers.setContentLength(bytes.length);
							headers.setContentDispositionFormData("attachment", name);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}
}
