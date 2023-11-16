

<%@page import="com.e3ps.download.service.DownloadHistoryHelper"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.common.content.FileDown"%>
<%@page import="java.util.*,
                java.io.*,
                wt.content.*,
                java.net.*,
                wt.content.*,
                com.e3ps.common.content.remote.*,
                com.e3ps.common.util.*,wt.fc.*,wt.query.*" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>                
<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="request" />
<jsp:setProperty name="wtcontext" property="request" value="<%=request%>" />
                
                
<%
	String addOid = request.getParameter("appOid");
	String empOid = request.getParameter("empOid");

	ApplicationData ad = (ApplicationData)CommonUtil.getObject(addOid);
	HashMap map = new HashMap();
	map.put("oid",addOid);
	map.put("empOid",empOid);
	HashMap mapRe = FileDown.pdfDown(map);
	String tempPath = (String)mapRe.get("tempPath");
	//System.out.println(">>>>>>>>>>>>>>> tempPath : "+ tempPath );
	
	String strFilename = tempPath.substring(tempPath.lastIndexOf(File.separator)+1);
	//System.out.println(">>>>>>>>>>>>>>> strFilename : "+ strFilename );
	//System.out.println("strFilename : "+ strFilename);
	
	String strFilenameOutput=new String(strFilename.getBytes("euc-kr"),"8859_1");
	File file=new File(tempPath);
	byte b[]=new byte[(int)file.length()];
	response.setHeader("Content-Disposition","attachment;filename="+strFilenameOutput.replaceAll(",", " "));
	response.setHeader("Content-Length",String.valueOf(file.length()));
	if(file.isFile()){
	 	BufferedInputStream fin=new BufferedInputStream(new FileInputStream(file));
	 	BufferedOutputStream outs=new BufferedOutputStream(response.getOutputStream());
	 	int read=0;
	 	while((read=fin.read(b))!=-1){outs.write(b,0,read);}
	 	outs.close();
		fin.close();
		
		
		Hashtable hash = new Hashtable();
		WTUser user = (WTUser)SessionHelper .getPrincipal();
		hash.put("dOid", empOid);
		
		hash.put("userId", user.getFullName());
		
		DownloadHistoryHelper.service.createDownloadHistory(hash);
		
		
		
	}
	
	
%>
