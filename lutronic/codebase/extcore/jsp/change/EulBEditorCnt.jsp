<%-- 
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
/**
 * 
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @version 1.00
 * @author E3PS Developer Team, sbae@e3ps.com
 * @modify
 * @desc
 */
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/util" prefix="util" %>
<%
String oid = request.getParameter("oid");
String eulOid = request.getParameter("eul");
if(eulOid==null)eulOid="";
if(oid==null)oid="";

//System.out.println("--------------------------------------   " + oid);
%>
<html>
<head>
<title>BOM Editor</TITLE>
<LINK rel="stylesheet" type="text/css" href="/Windchill/jsp/css/e3ps.css">
</head>
<body>

<script language=JavaScript  src="/Windchill/jsp/js/common.js"></script>
<br>
<table border="0" cellpadding="0" cellspacing="0" align=center>
<tr align=center valign=top>
	<td align=center valign=top width="250" height=300>
        <util:plugin
      code = "com/e3ps/change/editor/EOEulBApplet.class"
      codebase="../../"
      archive="wt/security/security.jar"
      width="100%" height="100%"
      align="absmiddle">
      <util:params>
         <util:param name="cabinets" value="wt/security/security.cab" />
         <util:param name="cache_option" value="Plugin" />
		 <util:param name="cache_archive" value="lib/changeEditor.jar,wtWork.jar,lib/skinlf.jar,lib/lgc-wspack.jar,lib/lgc-esbclient.jar,lib/httpcore-4.0.1.jar" />
		 <util:param name="oid" value="<%=oid%>" />
		 <util:param name="eulOid" value="<%=eulOid%>" />
         <util:param name="MAYSCRIPT" value="true" />
      </util:params>
   </util:plugin>
	</td>
</tr>
<tr>
<td align=center>
                        <table border="0" cellpadding="0" cellspacing="2" align="center">
                            <tr>
                                <td> <script>setButtonTag("${f:getMessage('닫기')}","60","self.close()","");</script></td>
                            </tr>
                        </table>

</td>
</tr>
</table>
</body>
</html>
