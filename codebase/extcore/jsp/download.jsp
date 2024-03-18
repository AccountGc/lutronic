<%@page import="com.e3ps.common.util.ZipUtil"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.File"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.util.FileUtil"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.representation.Representation"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.part.bom.service.BomHelper"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "wt.part.WTPart:239201538";
WTPart part = (WTPart) CommonUtil.getObject(oid);

WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
String today = DateUtil.getToDay();
String id = user.getName();

String path = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "pdm" + File.separator
		+ today + File.separator + id;

File dir = new File(path);
if (!dir.exists()) {
	dir.mkdirs();
}

ArrayList<WTPart> list = PartHelper.manager.descendants(part);
for (WTPart node : list) {
	// 	out.println("번호 = " + node.getNumber() + "<br>");

	EPMDocument epm = PartHelper.manager.getEPMDocument(node);
	if (epm != null) {
		EPMDocument d = PartHelper.manager.getEPMDocument2D(epm);
		if (d != null) {
	Representation representation = PublishUtils.getRepresentation(d);
	if (representation != null) {
		QueryResult qr = ContentHelper.service.getContentsByRole(representation,
				ContentRoleType.ADDITIONAL_FILES);
		while (qr.hasMoreElements()) {
			ApplicationData data = (ApplicationData) qr.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			if ("pdf".equalsIgnoreCase(ext)) {
				String name = data.getFileName();
				name = name.replace("." + ext, "").replace("step_", "").replace("_prt", "").replace("_asm", "")
						.replace("pdf_", "").replace("_drw", "") + "_" + d.getName() + "." + ext;
				out.println("PDF = " + data.getFileName() + " 드로잉 = " + d.getNumber() + " 부품 = "
						+ node.getNumber() + "<br>");
				
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(data);
				File file = new File(dir.getPath() + File.separator + name);
				FileOutputStream fos = new FileOutputStream(file);
				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
			}
		}

		qr.reset();
		qr = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
		while (qr.hasMoreElements()) {
			ApplicationData data = (ApplicationData) qr.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			if ("dxf".equalsIgnoreCase(ext)) {
				String name = data.getFileName();
				name = name.replace("." + ext, "").replace("step_", "").replace("_prt", "").replace("_asm", "")
						.replace("pdf_", "").replace("_drw", "") + "_" + d.getName() + "." + ext;
				out.println("DXF = " + data.getFileName() + " 드로잉 = " + d.getNumber() + " 부품 = "
						+ node.getNumber() + "<br>");
				
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(data);
				File file = new File(dir.getPath() + File.separator + name);
				FileOutputStream fos = new FileOutputStream(file);
				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
			}
		}
	}

		}
	}
	String nn = "BOM-" + id + ".zip";

	ZipUtil.compress(today + File.separator + id, nn);

	File[] fs = dir.listFiles();
	for (File f : fs) {
		f.delete();
		System.out.println("파일 삭제!");
	}
	
}
%>