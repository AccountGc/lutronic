<%@page import="java.util.*,
                java.io.*,
                wt.content.*,
                java.net.*,
                wt.content.*,
                com.e3ps.common.util.*,wt.fc.*,wt.query.*" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>                
<%@page import="com.e3ps.download.service.DownloadHistoryHelper"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="request" />
<jsp:setProperty name="wtcontext" property="request" value="<%=request%>" />
<%

String HOLDEROID = "holderOid";
String ADOID = "appOid";
String type = request.getParameter("type");
String seOid = request.getParameter("seOid");
String contentOid = request.getParameter(HOLDEROID);
String addOid = request.getParameter(ADOID);

ReferenceFactory rf = new ReferenceFactory();
    
ContentHolder contentHolder = (ContentHolder)rf.getReference(contentOid).getObject();
ApplicationData ad = (ApplicationData)rf.getReference(addOid).getObject();

try {
	if("{$CAD_NAME}".equals(ad.getFileName())){
		
		EPMDocument epm = (EPMDocument)contentHolder;
		
		ad.setFileName(epm.getCADName());
	}
	System.out.println("ad.getFileName()="+ad.getFileName());
	URL durl = ContentHelper.service.getDownloadURL(contentHolder, ad);
	
	System.out.println("durl =" + durl);
	System.out.println("durl.toString() =" + durl.toString());
	response.sendRedirect(durl.toString()); 
	
	WTUser user = (WTUser)SessionHelper.getPrincipal();
	
	Hashtable hash = new Hashtable();
	
	hash.put("dOid", contentOid);
	if( "disReq".equals(type)){
		hash.put("dOid", seOid);
	}
	
	hash.put("userId", user.getFullName());
	
	DownloadHistoryHelper.service.createDownloadHistory(hash);
} catch(Exception e) {
%>

<script>
alert("<%= e.getLocalizedMessage() %>");
history.back();
</script>

<%
}
%>

