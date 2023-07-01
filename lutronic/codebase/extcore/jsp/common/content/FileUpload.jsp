<%@page import="com.e3ps.common.content.CacheUploadUtil"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="wt.session.SessionServerHelper"%>
<%@page import="java.net.URL"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.File"%>
<%@page import="wt.fv.uploadtocache.CachedContentDescriptor"%>
<%@page import="wt.fv.uploadtocache.UploadToCacheHelper"%>
<%@page import="wt.fv.uploadtocache.CacheDescriptor"%>
<%@page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy"%>
<%@page import="com.oreilly.servlet.MultipartRequest"%>
<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%>

<%

	String savePath = WTProperties.getServerProperties().getProperty("wt.temp");

	//System.out.println("savePath : "+savePath);

	int sizeLimit = (1024*1024*100);

	MultipartRequest multi = new MultipartRequest(request, savePath, sizeLimit, "UTF-8", new DefaultFileRenamePolicy());

	String roleType = multi.getParameter("roleType");
	String originFileName = multi.getOriginalFileName(roleType);
	String fileName = multi.getFilesystemName(roleType);
	String fileExtType = multi.getContentType(roleType);
	String userId = multi.getParameter("userId");
	String masterHost = multi.getParameter("masterHost");
	String formId = multi.getParameter("formId");
	String description = multi.getParameter("description");
	
	//System.out.println("formId ============== " + formId);

	String m_fileFullPath = savePath + "/" + fileName;

	CacheDescriptor cd = CacheUploadUtil.getCacheDescriptor(1, true, userId, masterHost);

	File file = new File(m_fileFullPath);

	
	//System.out.println("unix new file path ::  " + file.getPath());

	InputStream[] streams = new InputStream[1];
	streams[0] = new FileInputStream(file);

	long[] fileSize = new long[1];
	fileSize[0] = file.length();

	String[] paths = new String[1];
	paths[0] = file.getPath();

	//System.out.println("fileSize[0] : " + fileSize[0] +"\n");
	//System.out.println("fileName : " + fileName +"\n");
	//System.out.println("paths[0] : " + paths[0] +"\n");

	ResourceBundle bundle =ResourceBundle.getBundle("wt");

	String reqHost = bundle.getString("wt.rmi.server.hostname");
   	boolean isMain = masterHost.indexOf(reqHost) > 0;

    CachedContentDescriptor descriptor =  CacheUploadUtil.doUploadToCache(cd, file, isMain);
    
    originFileName = originFileName.replaceAll("'", "\\\\'");
    fileName = fileName.replaceAll("'", "\\\\'");
    
    String returnValue = "";
	returnValue += "{";
	returnValue += "name : '"+originFileName+"', ";
	returnValue += "type : '"+fileExtType+"', ";
	returnValue += "saveName : '"+fileName+"', ";
	returnValue += "fileSize : '"+fileSize[0]+"', ";
	returnValue += "uploadedPath : '', ";
	returnValue += "thumbUrl : '', ";
	returnValue += "roleType : '"+roleType+"', ";
	returnValue += "formId : '"+formId+"', ";
	returnValue += "description : '"+ description +"', ";
	returnValue += "cacheId : '"+descriptor.getEncodedCCD()+"'";
	returnValue += "}";
	//System.out.println("returnValue : "+returnValue);
	out.println(returnValue);
%>