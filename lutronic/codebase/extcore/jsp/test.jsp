<%@page import="com.e3ps.drawing.beans.EpmUtil"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentItem"%>
<%@page import="java.util.Vector"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="wt.representation.Representation"%>
<%@page import="com.e3ps.drawing.beans.EpmData"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.epm.EPMDocument"%>
<%!

%>

<%
String oid = "wt.epm.EPMDocument:189720513";
//String oid = "wt.epm.EPMDocument:189723562";
EPMDocument epm = (EPMDocument)CommonUtil.getObject(oid);
EpmData epmdata = new EpmData(epm);
out.println("get>" + epmdata.getPDFFile() + "<br/>");
String pdfFile = "";

Representation representation = PublishUtils.getRepresentation(epm);
out.println("representation1 >>> " + representation + "<br/>");

representation = (Representation) ContentHelper.service.getContents(representation);
out.println("representation2 >>> " + representation + "<br/>");
Vector contentList = ContentHelper.getContentList(representation);
for (int l = 0; l < contentList.size(); l++) {
    ContentItem contentitem = (ContentItem) contentList.elementAt(l);
    out.println("========== ContentList For Start ============");
    out.println("contentitem >>> " + contentitem + "<br/>");
    if( contentitem instanceof ApplicationData){
    	ApplicationData drawAppData = (ApplicationData) contentitem;
    	out.println("drawAppData >>> " + drawAppData + "<br/>");
    	
    	String fileName= EpmUtil.getPublishFile(epm, drawAppData.getFileName());
    	
    	if(drawAppData.getRole().toString().equals("SECONDARY") && drawAppData.getFileName().lastIndexOf("dxf")>0){
    		String nUrl="/Windchill/jsp/common/DownloadPDF.jsp?&appOid="+CommonUtil.getOIDString(drawAppData)+"&appType=PROE&empOid="+oid;
    		pdfFile = pdfFile + "<a href='"+nUrl+"'>&nbsp;"+fileName+"</a><br>";
    	}
    }
    out.println("========== ContentList For End ============");
}
/* if(pdfFile.length()>0){
	pdfFile = pdfFile.substring(0,pdfFile.length()-1);
} */
out.println("PDF >>> " + pdfFile + "<br/>");
out.println("check end");
%>
