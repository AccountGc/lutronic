package com.e3ps.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.FileUtil;
import wt.util.WTProperties;

public class ContentUtils {

	public static String TMP_PATH = "";

	public static String FILE_PATH = "/Windchill/extcore/lutronic/";

	static {
		try {
			TMP_PATH = WTProperties.getServerProperties().getProperty("wt.temp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 객체 생성 방지
	 */
	private ContentUtils() {

	}

	/**
	 * OID로 와 ContentRoleType 으로 첨부파일 가져오기
	 */
	public static Map<String, Object> getContentByRole(String oid, String role) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();
		return getContentByRole(holder, role);
	}

	/**
	 * OID로 와 ContentRoleType 으로 첨부파일 가져오기
	 */
	public static Map<String, Object> getContentByRole(ContentHolder holder, String role) throws Exception {
		Map<String, Object> primary = new HashMap<>();
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.toContentRoleType(role));
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String fileIcon = getFileIcon(data.getFileName());
			primary = new HashMap<>();
			primary.put("oid", holder.getPersistInfo().getObjectIdentifier().getStringValue());
			primary.put("aoid", data.getPersistInfo().getObjectIdentifier().getStringValue());
			primary.put("name", data.getFileName());
			primary.put("fileSizeKB", data.getFileSizeKB() + "KB");
			primary.put("fileIcon", fileIcon);
			primary.put("url", "/Windchill/plm/content/download?oid="
					+ data.getPersistInfo().getObjectIdentifier().getStringValue());
			primary.put("fileSize", data.getFileSize());
			primary.put("filePath", FILE_PATH);
		}
		return primary;
	}

	/**
	 * OID로 주 첨부 파일 내용들 가져오는 함수
	 */
	public static Map<String, Object> getPrimary(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();
		return getPrimary(holder);
	}

	/**
	 * ContentHolder 객체로 주 첨부 파일 내용들 가져오는 함수
	 */
	public static Map<String, Object> getPrimary(ContentHolder holder) throws Exception {
		Map<String, Object> primary = null;
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String fileIcon = getFileIcon(data.getFileName());
			String ext = FileUtil.getExtension(data.getFileName());
			primary = new HashMap<>();
			primary.put("oid", holder.getPersistInfo().getObjectIdentifier().getStringValue());
			primary.put("aoid", data.getPersistInfo().getObjectIdentifier().getStringValue());
			if (holder instanceof EPMDocument) {
				EPMDocument d = (EPMDocument) holder;
				if (d.getAuthoringApplication().toString().equals("OTHER")) {
					primary.put("name", data.getFileName());
				} else {
					primary.put("name", d.getCADName());
				}

			} else {
				primary.put("name", data.getFileName());
			}

			primary.put("fileSizeKB", data.getFileSizeKB() + "KB");
			primary.put("fileIcon", fileIcon);
			primary.put("url",
					"/Windchill/plm/content/download?oid="
							+ data.getPersistInfo().getObjectIdentifier().getStringValue() + "&holder="
							+ holder.getPersistInfo().getObjectIdentifier().getStringValue());
			primary.put("fileSize", data.getFileSize());
			primary.put("filePath", FILE_PATH);
		}
		return primary;
	}

	/**
	 * 객체 OID로 첨부파일 가져오기
	 */
	public static Vector<Map<String, Object>> getSecondary(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();
		return getSecondary(holder);
	}

	/**
	 * ContentHolder 객체에 대한 첨부파일 가져오기
	 */
	public static Vector<Map<String, Object>> getSecondary(ContentHolder holder) throws Exception {
		Vector<Map<String, Object>> secondarys = new Vector<>();
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			Map<String, Object> map = new HashMap<>();
			ApplicationData data = (ApplicationData) result.nextElement();
			String fileIcon = getFileIcon(data.getFileName());
			map.put("oid", holder.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("aoid", data.getPersistInfo().getObjectIdentifier().getStringValue());
			if (holder instanceof EPMDocument) {
				EPMDocument d = (EPMDocument) holder;
				map.put("name", d.getCADName());
			} else {
				map.put("name", data.getFileName());
			}
			map.put("fileSizeKB", data.getFileSizeKB() + "KB");
			map.put("fileIcon", fileIcon);
			map.put("url",
					"/Windchill/plm/content/download?oid="
							+ data.getPersistInfo().getObjectIdentifier().getStringValue() + "&holder="
							+ holder.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("fileSize", data.getFileSize());
			secondarys.add(map);
		}
		return secondarys;
	}

	/**
	 * 파일확장자로 파일 아이콘 경로 리턴
	 */
	public static String getFileIcon(String name) {
		String ext = FileUtil.getExtension(name);

		String icon = "/Windchill/extcore/images/fileicon/file_notepad.gif";
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

	public static String[] getRepresentationData(ContentHolder holder) throws Exception {
		String[] representationData = new String[6];
		Representable representable = PublishUtils.findRepresentable(holder);
		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);

		if (representation != null) {
			QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
			while (result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				representationData[0] = data.getPersistInfo().getObjectIdentifier().getStringValue();
				representationData[1] = representation.getPersistInfo().getObjectIdentifier().getStringValue();
				representationData[2] = data.getFileName();
				representationData[3] = data.getFileSizeKB() + " KB";
				// representationData[4] = getFileIcon(representationData[2]);
				representationData[5] = ContentHelper
						.getDownloadURL(representation, data, true, representationData[2], true).toString();
			}
		}
		return representationData;
	}

	/**
	 * Base64 형태로 IMG 소스 가져오기
	 */
	public static String imageToBase64(File image, String ext) throws Exception {
		String base64 = Base64.getEncoder().encodeToString(loadFileAsBytesArray(image));
		return "data:image/" + ext + ";base64," + base64;
	}

	/**
	 * File -> byte[] 로 변경
	 */
	private static byte[] loadFileAsBytesArray(File file) throws Exception {
		int length = (int) file.length();
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[length];
		reader.read(bytes, 0, length);
		reader.close();
		return bytes;
	}

	/**
	 * 썸네일 저장
	 */
	public static void saveThumbnail(ContentHolder holder, String path) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.THUMBNAIL);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}

		ApplicationData data = ApplicationData.newApplicationData(holder);
		data.setRole(ContentRoleType.THUMBNAIL);
		data = (ApplicationData) ContentServerHelper.service.updateContent(holder, data, path);
	}

	/**
	 * 썸네일 보기 위해 base64 형태로 반환
	 */
	public static String getPreViewBase64(String oid) throws Exception {
		ContentHolder holder = (ContentHolder) CommonUtil.getObject(oid);
		return getPreViewBase64(holder);
	}

	/**
	 * 썸네일 보기 위해 base64 형태로 반환
	 */
	public static String getPreViewBase64(ContentHolder holder) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.THUMBNAIL);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ApplicationData data = (ApplicationData) item;
			String ext = FileUtil.getExtension(data.getFileName());
			InputStream is = ContentServerHelper.service.findLocalContentStream(data);
			File tempFile = File.createTempFile(String.valueOf(is.hashCode()), ".tmp");
			tempFile.deleteOnExit();
			FileUtils.copyInputStreamToFile(is, tempFile);
			String base64 = Base64.getEncoder().encodeToString(loadFileAsBytesArray(tempFile));
			return "data:image/" + ext + ";base64," + base64;
		}
		return null;
	}

	/**
	 * TEMP 파일 얻어오기
	 */
	public static File getTempFile() throws Exception {
		File directory = new File(TMP_PATH + File.separator + "tempFile");
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return File.createTempFile("TEMP_", "", directory);
	}

	/**
	 * 파일명을 받아 TEMP 파일 만들기
	 */
	public static File getTempFile(String name) throws Exception {
		File directory = new File(TMP_PATH + File.separator + "tempFile");
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return new File(TMP_PATH + File.separator + "tempFile" + File.separator + name);
	}

	/**
	 * 특정 컨텐트타입의 데이터 가져오기
	 */
	public static Map<String, Object> getContentData(String oid, String roleType) throws Exception {
		ContentHolder holder = (ContentHolder) CommonUtil.getObject(oid);
		Map<String, Object> content = null;
		QueryResult result = ContentHelper.service.getContentsByRole(holder,
				ContentRoleType.toContentRoleType(roleType));
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String fileIcon = getFileIcon(data.getFileName());
			content = new HashMap<>();
			content.put("oid", holder.getPersistInfo().getObjectIdentifier().getStringValue());
			content.put("aoid", data.getPersistInfo().getObjectIdentifier().getStringValue());
			content.put("name", data.getFileName());
			content.put("fileSizeKB", data.getFileSizeKB() + "KB");
			content.put("fileIcon", fileIcon);
			content.put("url", "/Windchill/plm/content/download?oid="
					+ data.getPersistInfo().getObjectIdentifier().getStringValue());
			content.put("fileSize", data.getFileSize());
			content.put("filePath", FILE_PATH);
		}
		return content;

	}
}