<%@page import="wt.content.ContentRoleType"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.rohs.service.RohsUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	
})
</script>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<table border="0" cellpadding="0" cellspacing="2">
			    <tr>
			        <td>
			        	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('추가')}" id="addFile" name="addFile">
			            	<span></span>
			            	${f:getMessage('추가')}
			           	</button>
			        </td>
			        <td>
			        	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('삭제')}" id="delFile" name=""delFile"">
			            	<span></span>
			            	${f:getMessage('삭제')}
			           	</button>
			        </td>
			    </tr>
			</table>
			<table width="100%" cellspacing="0" cellpadding="1" border="0" id="fileTable" align="center">
				<tr>
				    <td class="tdblueM" width="35%">${f:getMessage('첨부파일')}</td>
				   <td class="tdblueM" width="25%">${f:getMessage('파일종류')} </td>
				  	<td class="tdblueM" width="20%">${f:getMessage('발행일')}</td>
				</tr>
				<%
				List<ContentRoleType> list = RohsUtil.getROHSContentRoleType();
				for(ContentRoleType roleType :list){
				%>
					<tr>
						<td class="tdwhiteL">
							<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
								<jsp:param name="formId" value=""/>
								<jsp:param name="type" value="<%=roleType.toString() %>"/>
							</jsp:include>
					</td>
					<td class='tdwhiteM'>
						<select id='manufacture' name='manufacture' style='width: 90%'> 
							</select>
						</td>
						<td class='tdwhiteL'>
							<input name="filedate" id="filedate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
							<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="filedateBtn" id="filedateBtn" ></a>
							<a href="JavaScript:clearText('filedate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
						</td>
					</tr>
				<%} %>
			</table>
		</td>
	</tr>
</table>