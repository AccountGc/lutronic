<%@page import="wt.epm.EPMDocument"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.*,wt.content.*,wt.fc.*,wt.query.*" %>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.common.content.FileRequest"%>
<link rel="stylesheet" href="/Windchill/jsp/css/e3ps.css">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<script language=JavaScript  src="/Windchill/jsp/js/common.js"></script>

<script>
$(document).ready(function() {
})
</script>

<!-- // 파일 첨부 시작 //-->
<c:choose>
<c:when test="${type eq 'primary'}">
<SCRIPT LANGUAGE="JavaScript">
<!--
// ----- 주 첨부파일 체크 ------ //
function checkPrimary() {
	if(document.<c:out value="${form}" />.<c:out value="${type}" />.value == "") {
		alert("");
		return false;
	} else {
		return true;
	}
}

// --- 주요파일 변경하기 -----//
function fcFileShowHide() {
	
	if ($("#ImgFileChange").attr("value") == 'Show') {
		FileShow.style.display = '';
		FileHide.style.display = 'none';
		//document.<c:out value="${form}" />.ImgFileChange.value = 'Hide';
		$("#ImgFileChange").attr("value","Hide");
		document.<c:out value="${form}" />.ImgFileChange.src = '/Windchill/jsp/portal/images/img_default/button/board_btn_file_edit_no.gif';
		document.<c:out value="${form}" />.fileChangeYn.value = 'false';
	} else {
		FileShow.style.display = 'none';
		FileHide.style.display = '';
		//document.<c:out value="${form}" />.ImgFileChange.value = 'Show';
		$("#ImgFileChange").attr("value","Show");
		document.<c:out value="${form}" />.ImgFileChange.src = '/Windchill/jsp/portal/images/img_default/button/board_btn_file_edit.gif';
		document.<c:out value="${form}" />.fileChangeYn.value = 'true';
	}
}
//-->
//-- 파일 첨부 끝
//-->
</SCRIPT>
<input type="hidden" name="fileChangeYn" value="true">
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>	
		<c:choose>
			<c:when test="${ command eq 'insert' }">
				<td align="center" >
					<input type="file" name="<c:out value="${type}" />" id="<c:out value="${type}" />" style="width:100%">
				</td>
			</c:when>
			
			<c:otherwise>
			
				<c:choose>
					<c:when test="${primaryFile != null }" > 
						<td valign="top">
							<span id="FileShow" style="display:none;">
								&nbsp;
								<input type="file" name="<c:out value="${type}" />" id="<c:out value="${type}" />" style="width:100%">
								<input type="hidden" name="<c:out value="${type}" />Description" value="PRIMARY FILE">
							</span>
							<span id="FileHide" style="display:'';">
								<c:out value="${nUrl }" escapeXml="false" /> 
							</span>
						</td>
		
						<c:choose>
							<c:when test="${!isWG }">
							
								<td width="100" align="right" valign="top">
									<img name="ImgFileChange" id="ImgFileChange" value="Show" src="/Windchill/jsp/portal/images/img_default/button/board_btn_file_edit.gif" align="absmiddle" onMouseOver="this.style.cursor='hand'" onCLick="fcFileShowHide();">
								</td>
							</c:when>
						
						</c:choose>
					</c:when>
				<c:otherwise>
					<td valign="top">
						<input type="file" name="<c:out value="${type}" />" id="<c:out value="${type}" />" style="width:100%" >
						<input type="hidden" name="<c:out value="${type}" />Description" value="PRIMARY FILE">
					</td>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	</tr>
</table>
</c:when>

<c:otherwise>

<SCRIPT LANGUAGE="JavaScript">
	
function insertFile<c:out value="${type}" />() {
	index = fileTable<c:out value="${type}" />.rows.length;

	
	<c:if test="${type eq secondary && attacheCount > 0  }">
		if(index >= ("${attacheCount}" + 2)) {
			alert(".('${attacheCount}' ${f:getMessage('개')})");
			return;
		}
	
	</c:if>
	
	if(index >= 2) {
		if(fileTableRow<c:out value="${type}" />.style != null)
			fileTableRow<c:out value="${type}" />.style.display = '';
		else
			fileTableRow<c:out value="${type}" />[0].style.display = '';
	}
	
	trObj = fileTable<c:out value="${type}" />.insertRow(index);
	trObj.replaceNode(fileTable<c:out value="${type}" />.rows[1].cloneNode(true));
	

	fileTableRow<c:out value="${type}" />[0].style.display = 'none';
	
}

function deleteFile<c:out value="${type}" />() {
	index = document.<c:out value="${form}" />.fileDelete<c:out value="${type}" />.length-1;
	
	for(i=index; i>=1; i--) {
		if(document.<c:out value="${form}" />.fileDelete<c:out value="${type}" />[i].checked == true) fileTable<c:out value="${type}" />.deleteRow(i+1);
	}
}
</SCRIPT>
<table width="100%" align="center">
	<tr> 
		<td height="25">

			<table border=0 cellpadding="0" cellspacing=2 align="left">
                <tr>
                	<!--  
                    <td> <script>setButtonTag("","50","insertFile<c:out value="${type}" />()","");</script></td>
                    <td> <script>setButtonTag("","50","deleteFile<c:out value="${type}" />()","");</script></td>
                    -->
                    <td>
                    	<button type="button" class="btnCustom" title="${f:getMessage('추가')}" onclick='javascript:insertFile<c:out value="${type}" />()'>
		                  	<span></span>
		                  	${f:getMessage('추가')}
	                  	</button>
	                  	
	                  	<button type="button" class="btnCustom" title="${f:getMessage('삭제')}" onclick='javascript:deleteFile<c:out value="${type}" />()'>
		                  	<span></span>
		                  	${f:getMessage('삭제')}
	                  	</button>
	                  	
                    </td>
                </tr>
            </table>		
		</td>									
		<c:if test="${type eq secondary && attacheCount > 0  }">		
			<td align="left">
				<B>
					<font color="red">.(<c:out value="${attacheCount }" />${f:getMessage('개')})</font>
				</B>
			</td>
		</c:if>
	</tr>
</table>
<table width="100%" cellspacing="0" cellpadding="1" border="0" id="fileTable<c:out value="${type}" />" align="center">
	<tr bgcolor="#f1f1f1"  align=center>
		<c:choose>
			<c:when test="${canDesc }">
				<td width="60%" height="22" colspan="2" id=tb_inner></td>
				<td width="40%" height="22" id=tb_inner></td>
			</c:when>
			
			<c:otherwise>
				<td width="100%" height="22" colspan="2" id=tb_inner></td>
			</c:otherwise>
		</c:choose>
	</tr>
	<tr align="center" bgcolor="#FFFFFF" id="fileTableRow<c:out value="${type}" />" style="display:NONE"> 
		<c:choose>
			<c:when test="${canDesc }">
				<td id=tb_gray width="3%" height="22">
					<input type="checkbox" name="fileDelete<c:out value="${type}" />">
				</td>
				<td id=tb_gray width="57%">
					<input type="file" name="<c:out value="${type}" />" id=input style="width:99%">
				</td>
				<td id=tb_gray width="40%">
					<input type="text" name="<c:out value="${type}" />Desc" id=input style="width:99%">
				</td>
			</c:when>
			
			<c:otherwise>
				<td id=tb_gray width="3%" height="22">
					<input type="checkbox" name="fileDelete<c:out value="${type}" />">
				</td>
				<td id=tb_gray width="97%">
					<input type="file" name="<c:out value="${type}" />" id=input style="width:99%">
				</td>
			</c:otherwise>
		</c:choose>
	</tr>

	<c:forEach items="${secondaryList }" var="secondaryList" >

	<tr align="center" bgcolor="#ffffff" align=center>
		<c:choose>
			<c:when test="${canDesc }">
				<td id=tb_gray width="3%" height="25">
					<input type="checkbox" name="fileDelete<c:out value="${type}" />">
				</td>
				<td id=tb_gray width="57%" align=left>
					<input type="hidden" name="secondaryDelFile" value='<c:out value="${secondaryList.oid}" />' >&nbsp;<c:out value="${secondaryList.name}" />
				</td>
																												
				<td id=tb_gray width="40%">
					<input type="text" name="secondaryDelFileDesc" id=input style="width:99%" readonly value='<c:out value="${secondaryList.description}" />'>
				</td>
			</c:when>
			<c:otherwise>
				<td id=tb_gray width="3%" height="25">
					<input type="checkbox" name="fileDelete<c:out value="${type}" />">
				</td>
				<td id=tb_gray width="97%" align=left>
					<input type="hidden" name="secondaryDelFile" value='<c:out value="${secondaryList.oid}" />'>&nbsp;<c:out value="${secondaryList.name}" />
				</td>
			</c:otherwise>
		</c:choose>
	</tr>
	
	</c:forEach>
	
</table>
<!-- // 파일 첨부 끝 //-->	
</c:otherwise>
</c:choose>