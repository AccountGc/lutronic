<%@page import="com.e3ps.common.util.ZipUtil"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.File"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="java.io.InputStream"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
String today = DateUtil.getToDay();
String id = user.getName();

String path = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "pdm" + File.separator + today
		+ File.separator + id;

File ff = new File(path);
if (!ff.exists()) {
	ff.mkdirs();
}

String oid = "wt.doc.WTDocument:1574033";
WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
while (qr.hasMoreElements()) {
	ApplicationData dd = (ApplicationData) qr.nextElement();
	byte[] buffer = new byte[10240];
	InputStream is = ContentServerHelper.service.findLocalContentStream(dd);
	String name = dd.getFileName();
	File file = new File(path + File.separator + name);
	FileOutputStream fos = new FileOutputStream(file);
	int j = 0;
	while ((j = is.read(buffer, 0, 10240)) > 0) {
		fos.write(buffer, 0, j);
	}
	fos.close();
	is.close();
}

qr.reset();
qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
while (qr.hasMoreElements()) {
	ApplicationData dd = (ApplicationData) qr.nextElement();
	byte[] buffer = new byte[10240];
	InputStream is = ContentServerHelper.service.findLocalContentStream(dd);
	String name = dd.getFileName();
	File file = new File(path + File.separator + name);
	FileOutputStream fos = new FileOutputStream(file);
	int j = 0;
	while ((j = is.read(buffer, 0, 10240)) > 0) {
		fos.write(buffer, 0, j);
	}
	fos.close();
	is.close();
}

ZipUtil.compress(today + File.separator + id, "test.zip");

File[] fs = ff.listFiles();
System.out.println("fs="+fs.length);
for (File f : fs) {
	System.out.println(f.getName());
	f.delete();
	System.out.println("파일 삭제!");
}

System.out.println("종료!");
%>