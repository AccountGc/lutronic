package com.e3ps.common.content.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.download.service.DownloadHistoryHelper;

import net.sf.json.JSONObject;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.content.URLData;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.util.FileUtil;

@Controller
@RequestMapping(value = "/content/**")
public class ContentController extends BaseController {

	@Description(value = "파일 다운로드")
	@GetMapping(value = "/download")
	public ResponseEntity<byte[]> download(@RequestParam String oid, @RequestParam(required = false) String holder) {
		HttpHeaders headers = new HttpHeaders();
		byte[] bytes = null;

		try {
			ApplicationData data = (ApplicationData) CommonUtil.getObject(oid);
			String name = null;

			ContentHolder h = null;
			System.out.println("h=" + holder);
			if (StringUtil.checkString(holder)) {
				h = (ContentHolder) CommonUtil.getObject(holder);
				if (h instanceof EPMDocument) {
					EPMDocument e = (EPMDocument) h;

					// 변환 파일 이름 변경
					String ss = data.getFileName();
					String ext = FileUtil.getExtension(ss);

					System.out.println("ss=" + ss);
					System.out.println("ext=" + ext);

					if ("stp".equalsIgnoreCase(ext) || "pdf".equalsIgnoreCase(ext) || "step".equalsIgnoreCase(ext)
							|| "dxf".equalsIgnoreCase(ext)) {
						name = ss.replace("." + ext, "").replace("step_", "").replace("_prt", "").replace("_asm", "")
								.replace("pdf_", "").replace("_drw", "") + "_" + e.getName() + "." + ext;
					} else {
						if (e.getAuthoringApplication().toString().equals("OTHER")) {
							name = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");
						} else {
							name = e.getCADName();
						}
					}
				} else {
					name = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");
				}
			} else {
				name = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");
			}

			// 이름 치환

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

			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentLength(bytes.length);
			headers.setContentDispositionFormData("attachment", name);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}

	@Description(value = "zip파일 다운로드")
	@GetMapping(value = "/downloadZIP")
	public ResponseEntity<byte[]> downloadZIP(@RequestParam List<String> oids) {
		HttpHeaders headers = new HttpHeaders();
		byte[] zipBytes = null;
		String name = null;

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

			for (String oid : oids) {
				WTDocument document = (WTDocument) CommonUtil.getObject(oid);
				QueryResult result = ContentHelper.service.getContentsByRole(document, ContentRoleType.PRIMARY);

				while (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();
					InputStream is = ContentServerHelper.service.findLocalContentStream(data);

					byte[] buffer = new byte[1024];
					int length;

					ZipEntry zipEntry = new ZipEntry(data.getFileName());
					zipOutputStream.putNextEntry(zipEntry);

					ByteArrayOutputStream entryByteArrayOutputStream = new ByteArrayOutputStream();

					while ((length = is.read(buffer)) != -1) {
						entryByteArrayOutputStream.write(buffer, 0, length);
					}

					entryByteArrayOutputStream.close();

					byte[] entryBytes = entryByteArrayOutputStream.toByteArray();
					zipOutputStream.write(entryBytes);

					zipOutputStream.closeEntry();
				}

				result.reset();
				result = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
				while (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();
					InputStream is = ContentServerHelper.service.findLocalContentStream(data);

					byte[] buffer = new byte[1024];
					int length;

					ZipEntry zipEntry = new ZipEntry(data.getFileName());
					zipOutputStream.putNextEntry(zipEntry);

					ByteArrayOutputStream entryByteArrayOutputStream = new ByteArrayOutputStream();

					while ((length = is.read(buffer)) != -1) {
						entryByteArrayOutputStream.write(buffer, 0, length);
					}

					entryByteArrayOutputStream.close();

					byte[] entryBytes = entryByteArrayOutputStream.toByteArray();
					zipOutputStream.write(entryBytes);

					zipOutputStream.closeEntry();
				}
			}

			zipOutputStream.close();

			zipBytes = byteArrayOutputStream.toByteArray();

			name = URLEncoder.encode("downloaded_files.zip", "UTF-8").replaceAll("\\+", "%20");

			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentLength(zipBytes.length);
			headers.setContentDispositionFormData("attachment", name);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
	}

	@Description(value = "첨부 파일 리스트 가져오기")
	@ResponseBody
	@PostMapping(value = "/list")
	public JSONObject list(HttpServletRequest request) throws Exception {
		String oid = (String) request.getParameter("oid");
		String roleType = (String) request.getParameter("roleType");
		JSONObject list = null;
		if (oid.contains("ROHSMaterial")) {
			list = CommonContentHelper.manager.rohsList(oid);
		} else {
			list = CommonContentHelper.manager.list(oid, roleType);
		}
		return list;
	}

	@Description(value = "첨부 파일 업로드")
	@ResponseBody
	@PostMapping(value = "/upload")
	public JSONObject upload(HttpServletRequest request) throws Exception {
		return CommonContentHelper.manager.upload(request);
	}

	@Description(value = "첨부 파일 삭제(화면에서의 제거)")
	@ResponseBody
	@PostMapping(value = "/delete")
	public JSONObject delete(HttpServletRequest param) throws Exception {
		JSONObject result = new JSONObject();
		result.put("status", 0);
		result.put("result", "ok");
		return result;
	}

	/**
	 * 첨부파일 insert / update
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/includeAttachFile")
	public ModelAndView includeAttachFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView();

		// command : 첨부파일 Action type
		// insert : 신규 등록
		// update : 수정

		String form = "";
		String command = "";
		String oid = "";
		String type = "";
		String ac = "";

		form = request.getParameter("form");
		command = request.getParameter("command");
		oid = request.getParameter("oid");
		type = request.getParameter("type");
		ac = request.getParameter("count");

		model.addObject("form", form);
		model.addObject("command", command);

		String isWG = request.getParameter("isWG");
		model.addObject("isWG", isWG);

		// command 가 update시에 update 할 ContentHolder OID

		// type : 첨부파일 타입
		// p or primary : (Primary)주요문서 파일
		// s or secondary : (Secondary)참조문서 파일

		if ("p".equalsIgnoreCase(type) || "primary".equalsIgnoreCase(type))
			type = "primary";
		else if ("document".equalsIgnoreCase(type)) {
			type = "document";
		} else
			type = "secondary";

		model.addObject("type", type);

		// (Secondary)참조문서 파일일 경우 첨부할수 있는 갯수
		int attacheCount = 0;

		if (ac != null && ac.length() > 0) {
			attacheCount = Integer.parseInt(ac);
		}

		model.addObject("attacheCount", attacheCount);

		// (Secondary)참조문서 파일일 경우 설명 사용여부
		String desc = request.getParameter("desc");
		boolean canDesc = false;
		if ("t".equalsIgnoreCase(desc) || "true".equalsIgnoreCase(desc))
			canDesc = true;

		model.addObject("canDesc", canDesc);

		ContentItem primaryFile = null;
		ContentHolder holder = null;
		if ("primary".equals(type) && "update".equalsIgnoreCase(command)) {
			ReferenceFactory rf = new ReferenceFactory();
			holder = (ContentHolder) rf.getReference(oid).getObject();
			if (holder instanceof FormatContentHolder) {
				QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
				if (result.hasMoreElements()) {
					primaryFile = (ContentItem) result.nextElement();
				}
			} else if (holder instanceof ContentHolder) { // 경쟁 제품 용(ContentHolder 일 경우)
				QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
				if (result.hasMoreElements()) {
					primaryFile = (ContentItem) result.nextElement();
				}
			}
			model.addObject("primaryFile", primaryFile);
		}

		if (primaryFile != null) {
			// String downURL = "";
			String nUrl = "";
			if (primaryFile instanceof ApplicationData) {
				// downURL = ContentHelper.getDownloadURL ( holder ,
				// (ApplicationData)primaryFile ).toString();
				nUrl = WebUtil.getHost() + "servlet/DownloadGW?holderOid=" + CommonUtil.getOIDString(holder)
						+ "&appOid=" + CommonUtil.getOIDString(primaryFile);
				nUrl = "<a href=" + nUrl + ">&nbsp;";
				if ("true".equals(isWG)) {
					nUrl = nUrl + ((EPMDocument) holder).getCADName() + "</a>";
				} else {
					nUrl = nUrl + ((ApplicationData) primaryFile).getFileName() + "</a>";
				}
			} else {
				nUrl = ((URLData) primaryFile).getUrlLocation();
			}
			model.addObject("nUrl", nUrl);
		}

		List<Map<String, String>> secondaryList = new ArrayList<Map<String, String>>();
		QueryResult secondaryFiles = null;
		int deleteFileCnt = 0;
		if ("secondary".equals(type) && "update".equalsIgnoreCase(command)) {
			ReferenceFactory rf = new ReferenceFactory();
			holder = (ContentHolder) rf.getReference(oid).getObject();
			secondaryFiles = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		}

		while (secondaryFiles != null && secondaryFiles.hasMoreElements()) {
			ContentItem item = (ContentItem) secondaryFiles.nextElement();
			String name = "";
			if (item instanceof URLData) {
				URLData url = (URLData) item;
				name = url.getUrlLocation();
			} else if (item instanceof ApplicationData) {
				ApplicationData file = (ApplicationData) item;
				name = file.getFileName();
			}

			Map<String, String> secondaryMap = new HashMap<String, String>();

			secondaryMap.put("name", name);
			secondaryMap.put("oid", item.getPersistInfo().getObjectIdentifier().toString());
			if (canDesc) {
				secondaryMap.put("description", item.getDescription());
			}

			secondaryList.add(secondaryMap);

		}
		model.addObject("secondaryList", secondaryList);

		model.setViewName("include:/portal/include_attacheFile");

		return model;
	}

	@RequestMapping("/includeAttachFileView")
	public ModelAndView includeAttachFileView(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String oid = request.getParameter("oid");
		String type = request.getParameter("type");
		String description = request.getParameter("description");

		boolean primary = false;
		boolean isAddRole = false;
		if (StringUtil.checkString(type) && "P".equals(type.toUpperCase())) {
			primary = true;
		}
		// System.out.println("type =" + type);
		if (StringUtil.checkString(type) && (type.equals("ECR") || type.equals("ECO"))) {
			isAddRole = true;
		}

		Object obj = CommonUtil.getObject(oid);
		ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);
		ContentRoleType roleType = primary ? ContentRoleType.PRIMARY : ContentRoleType.SECONDARY;
		if (isAddRole) {

			roleType = ContentRoleType.toContentRoleType(type);
		}
		QueryResult qr = ContentHelper.service.getContentsByRole(holder, roleType);

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		while (qr.hasMoreElements()) {
			ContentItem item = (ContentItem) qr.nextElement();

			if (item != null) {

				ApplicationData data = (ApplicationData) item;

				String url = "/Windchill/jsp/common/DownloadGW.jsp?holderOid=" + CommonUtil.getOIDString(holder)
						+ "&appOid=" + CommonUtil.getOIDString(data);

				Map<String, String> map = new HashMap<String, String>();

				map.put("name", data.getFileName());
				map.put("oid", data.getPersistInfo().getObjectIdentifier().toString());
				map.put("size", String.valueOf(data.getFileSize()));
				map.put("url", url);

				if (StringUtil.checkString(description)) {
					if (description.equals(item.getDescription())) {
						list.add(map);
					}
				} else {
					if (item.getDescription() == null) {
						list.add(map);
					}
				}
			}
		}

		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.setViewName("empty:/portal/include_attachFileView");
		return model;
	}

	@Description(value = "물질 첨부 파일 리스트 가져오기")
	@ResponseBody
	@PostMapping(value = "/rohsList")
	public JSONObject rohsList(HttpServletRequest param) throws Exception {
		String oid = (String) param.getParameter("oid");
		JSONObject list = CommonContentHelper.manager.rohsList(oid);
		return list;
	}
}
