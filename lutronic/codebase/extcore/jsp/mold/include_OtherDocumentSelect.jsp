<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">

var docOid = "<c:out value='${paramName}' />";

$(function() {
	<%----------------------------------------------------------
	*                      관련 문서 추가
	----------------------------------------------------------%>
	$("#addDoc").click(function() {
		var state = "<c:out value='${state}' />";
		var url = getURLString("doc", "selectOtherPopup", "do") + "?state="+state +"&searchType="+$("#searchType").val() + "&lifecycle="+$("#lifecycle").val();
		openOtherName(url,"selectOtherPopup","1180","600","status=no,scrollbars=yes,resizable=yes");
	}),
	<%----------------------------------------------------------
	*                      관련 문서 삭제
	----------------------------------------------------------%>
	$("#delDoc").click(function() {
		var obj = $("input[name='docDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
	})
	
	$('#allCheck').click(function() {
		if(this.checked) {
			$("input[name='docDelete']").prop("checked", "checked");
		}else {
			$("input[name='docDelete']").prop("checked", "");
		}
	})
})

<%----------------------------------------------------------
*                      검색 문서 추가
----------------------------------------------------------%>
function addDoc(obj) {
	for (var i = 0; i < obj.length; i++) {
		
		if(DocDuplicateCheck(obj[i][1])) {
			var html = ""
			html += "<tr id='" + obj[i][0] + "'>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='docDelete' id='docDelete'>";
			html += "		<input type='hidden' name='" + docOid +"' id='docOid' value='" + obj[i][0] + "' />";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][1];
			html += "		<input type='hidden' name='docNumber' value='" + obj[i][1] + "' />";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][2];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][3];
			html += "	</td>";
			html += "</tr>";
	
			$("#docTable").append(html);
		}
	}
}

<%----------------------------------------------------------
*                      검색 문서 중복 검색
----------------------------------------------------------%>
function DocDuplicateCheck(docNumber) {
	var obj = $("input[name='docNumber']");
	var checkCount = obj.size();
	for (var i = 0; i < checkCount; i++) {
		if(obj.eq(i).val() == docNumber) {
			return false;
		}
	}
	return true;
}
</script>

<body>

<input type="hidden" name="lifecycle" 		id="lifecycle" 				value="<c:out value='${lifecycle }'/>" />
<input type="hidden" name="searchType" id="searchType" value="<c:out value='${searchType }'/>" />

<table width="100%" border="0" cellpadding="0" cellspacing="3" >	
	<tr bgcolor="#ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>        
	        
    <tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b><c:out value="${title }" /></b>
		</td>
	</tr>
	
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
			
				<tr>
					<td width="150"></td>
					<td width="350"></td>
					<td width="150"></td>
					<td width="350"></td>
				</tr>
			
				<tr>
					<td class="tdblueM">
						<c:out value="${title }" /> <span class="style1">*</span>
					</td>
					<td class="tdwhiteL" colspan="3">
			 			<table id="innerTempTable" style="display:none">
						    <tr>
						        <td class="tdwhiteM"></td>
						        <td class="tdwhiteM"></td>
						        <td class="tdwhiteM"></td>
						        <td class="tdwhiteM"></td>
						    </tr>
						</table>
						
						<table width="100%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="left">
									<table border="0" cellpadding="0" cellspacing="2">
										<tr>
											<td>
												<button type="button" name="addDoc" id="addDoc" class="btnCustom">
													<span></span>
													${f:getMessage('추가')}
												</button>
											</td>
											
											<td>
												<button type="button" name="delDoc" id="delDoc" class="btnCustom">
													<span></span>
													${f:getMessage('삭제')}
												</button>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
								<div style="width:99%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;">
									<table width="100%" cellspacing="0" cellpadding="1" border="0" id="docTable" align="center">
										<tbody>
											<tr>
												<td class="tdblueM"  width="5%"><input type="checkbox" name="allCheck" id="allCheck" ></td>
												<td class="tdblueM"  width="35%">${f:getMessage('번호')}</td>
												<td class="tdblueM"  width="50%">${f:getMessage('문서명')}</td>
												<td class="tdblueM0" width="10%">${f:getMessage('Rev.')}</td>
											</tr>
										
										<c:choose>
											<c:when test="${fn:length(list) != 0 }">
												<c:forEach items="${list }" var="docData">
													<tr align="center" bgcolor="#FFFFFF"> 
														<td class="tdwhiteM"  width="5%">
															<input type="checkbox" name="docDelete">
															<input type="hidden" name="<c:out value="${paramName}" />" id="docOid" value="<c:out value="${docData.oid }" />">
														</td>
														
														<td class="tdwhiteM"  width="35%">
															<c:out value="${docData.number }" />
														</td>
														
														<td class="tdwhiteM"  width="50%">
															<a href="javascript:openView('<c:out value="${docData.oid }" />')">
																<c:out value="${docData.name }" />
															</a>
														</td>
														
														<td class="tdwhiteM" width="10%">
															<c:out value="${docData.version }" />.<c:out value="${docData.iteration }" />
														</td>
													</tr>
												</c:forEach>
											</c:when>
										</c:choose>
										</tbody>
									</table>
								</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>