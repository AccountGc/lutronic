package com.e3ps.common.util;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.eco.service.EcoHelper;
import com.e3ps.change.eo.service.EoHelper;
import com.e3ps.doc.service.DocumentHelper;
import com.ptc.wvs.server.util.PublishUtils;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.FileUtil;

public class AUIGridUtil {

	private AUIGridUtil() {

	}

	/**
	 * 그리드에서 주 첨부파일 표시
	 */
	public static String primary(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			String icon = getFileIcon(ext);
			String url = "/Windchill/plm/content/download?oid="
					+ data.getPersistInfo().getObjectIdentifier().getStringValue();
			template += "<a href=" + url + "><img src=" + icon + "></a>";
		}
		return template;
	}

	/**
	 * 그리드에서 첨부파일 표시
	 */
	public static String secondary(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			String icon = getFileIcon(ext);
			String url = "/Windchill/plm/content/download?oid="
					+ data.getPersistInfo().getObjectIdentifier().getStringValue();
			template += "<a href=" + url + "><img src=" + icon + "></a>&nbsp;";
		}
		return template;
	}

	/**
	 * 그리드에서 STEP 표시
	 */
	public static String step(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			if (!"stp".equalsIgnoreCase(ext)) {
				continue;
			}
			String icon = getFileIcon(ext);
			String url = "/Windchill/plm/content/download?oid="
					+ data.getPersistInfo().getObjectIdentifier().getStringValue();
			template += "<a href=" + url + "><img src=" + icon + "></a>&nbsp;";
		}
		return template;
	}

	/**
	 * 그리드에서 PDF 표시
	 */
	public static String pdf(ContentHolder holder) throws Exception {
		String template = "";
		Representable representable = PublishUtils.findRepresentable(holder);
		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);
		if (representation != null) {
			QueryResult result = ContentHelper.service.getContentsByRole(representation,
					ContentRoleType.ADDITIONAL_FILES);
			while (result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				String ext = FileUtil.getExtension(data.getFileName());
				String icon = getFileIcon(ext);
				if (!"pdf".equalsIgnoreCase(ext)) {
					continue;
				}
				String url = "/Windchill/plm/content/download?oid="
						+ data.getPersistInfo().getObjectIdentifier().getStringValue();
				template += "<a href=" + url + "><img src=" + icon + "></a>&nbsp;";
			}
		}
		return template;
	}

	/**
	 * 그리드에서 DFX 표시
	 */
	public static String dxf(ContentHolder holder) throws Exception {
		String template = "";
		Representable representable = PublishUtils.findRepresentable(holder);
		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);
		if (representation != null) {
			QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
			while (result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				String ext = FileUtil.getExtension(data.getFileName());
				if (!"dxf".equalsIgnoreCase(ext)) {
					continue;
				}
				String icon = getFileIcon(ext);
				String url = "/Windchill/plm/content/download?oid="
						+ data.getPersistInfo().getObjectIdentifier().getStringValue();
				template += "<a href=" + url + "><img src=" + icon + "></a>&nbsp;";
			}
		}
		return template;
	}

	/**
	 * 첨부파일 다운로드 표기할 아이콘
	 */
	private static String getFileIcon(String ext) throws Exception {
		String icon = "/Windchill/extcore/images/fileicon/file_generic.gif";
		if (ext.equalsIgnoreCase("pdf")) {
			icon = "/Windchill/extcore/images/fileicon/file_pdf.gif";
		} else if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx") || ext.equalsIgnoreCase("csv")) {
			icon = "/Windchill/extcore/images/fileicon/file_excel.gif";
		} else if (ext.equalsIgnoreCase("ppt") || ext.equalsIgnoreCase("pptx")) {
			icon = "/Windchill/extcore/images/fileicon/file_ppoint.gif";
		} else if (ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("docs")) {
			icon = "/Windchill/extcore/images/fileicon/file_msword.gif";
		} else if (ext.equalsIgnoreCase("html") || ext.equalsIgnoreCase("htm")) {
			icon = "/Windchill/extcore/images/fileicon/file_html.gif";
		} else if (ext.equalsIgnoreCase("gif")) {
			icon = "/Windchill/extcore/images/fileicon/file_gif.gif";
		} else if (ext.equalsIgnoreCase("png")) {
			icon = "/Windchill/extcore/images/fileicon/file_png.gif";
		} else if (ext.equalsIgnoreCase("bmp")) {
			icon = "/Windchill/extcore/images/fileicon/file_bmp.gif";
		} else if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
			icon = "/Windchill/extcore/images/fileicon/file_jpg.jpg";
		} else if (ext.equalsIgnoreCase("zip") || ext.equalsIgnoreCase("rar") || ext.equalsIgnoreCase("jar")) {
			icon = "/Windchill/extcore/images/fileicon/file_zip.gif";
		} else if (ext.equalsIgnoreCase("tar") || ext.equalsIgnoreCase("gz")) {
			icon = "/Windchill/extcore/images/fileicon/file_zip.gif";
		} else if (ext.equalsIgnoreCase("exe")) {
			icon = "/Windchill/extcore/images/fileicon/file_exe.gif";
		} else if (ext.equalsIgnoreCase("dwg")) {
			icon = "/Windchill/extcore/images/fileicon/file_dwg.gif";
		} else if (ext.equalsIgnoreCase("xml")) {
			icon = "/Windchill/extcore/images/fileicon/file_xml.png";
		}
		return icon;
	}

	/**
	 * INCLUDE 페이지 처리용 -> 객체 유형에 따라 분기
	 */
	public static JSONArray include(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		Persistable per = CommonUtil.getObject(oid);
		if (per instanceof WTDocument) {
			// 문서 연관 객체
			return DocumentHelper.manager.reference(oid, type);
		} else if (per instanceof EChangeOrder) {
			// EO, ECO 연관 객체
			EChangeOrder eco = (EChangeOrder) per;
			// ECO
			if (eco.getEoType().equals("CHANGE")) {
				return EcoHelper.manager.reference(oid, type);
				// EO
			} else {
				return EoHelper.manager.reference(oid, type);
			}

		} else if (per instanceof EChangeRequest) {
			// CR 연관 겍체
		} else if (per instanceof EChangeNotice) {
			// ECN 연관 객체
		}

		return JSONArray.fromObject(list);
	}

	/**
	 * DTO 클래스 Map 모든 필드값 변경시키는 함수
	 */
	public static Map<String, Object> dtoToMap(Object obj) throws Exception {
		Class<?> clazz = obj.getClass();
		Map<String, Object> map = new HashMap<>();
		for (Field field : clazz.getDeclaredFields()) {
			// 그리드에서 표현 되는내용 차이로 인해 에러 발생하는듯
			if (field.getType().equals(Timestamp.class)) {
				continue;
			}
			field.setAccessible(true);
			String fieldName = field.getName();
			Object fieldValue = field.get(obj);
			map.put(fieldName, fieldValue);
		}
		return map;
	}
}
