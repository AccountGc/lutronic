<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">

var rohsOid = "<c:out value='${paramName}' />";

$(function() {
	<%----------------------------------------------------------
	*                      관련 문서 추가
	----------------------------------------------------------%>
	$("#addRohs").click(function() {
		var state = "<c:out value='${state}' />";
		var url = getURLString("rohs", "selectRohsPopup", "do") + "?state="+state +"&searchType="+$("#searchType").val() + "&lifecycle="+$("#lifecycle").val();
		openOtherName(url,"selecDocPopup","1180","600","status=no,scrollbars=yes,resizable=yes");
	}),
	<%----------------------------------------------------------
	*                      관련 문서 삭제
	----------------------------------------------------------%>
	$("#delRohs").click(function() {
		var obj = $("input[name='rohsDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
		$("#allCheck").prop("checked", "");
	})
	
	$('#allCheck').click(function() {
		if(this.checked) {
			$("input[name='rohsDelete']").prop("checked", "checked");
		}else {
			$("input[name='rohsDelete']").prop("checked", "");
		}
	})
})

<%----------------------------------------------------------
*                      검색 문서 추가
----------------------------------------------------------%>
function addRohs(obj) {
	for (var i = 0; i < obj.length; i++) {
		
		if(DocDuplicateCheck(obj[i][1])) {
			var html = ""
			
			html += "<tr id='" + obj[i][0] + "'>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='rohsDelete' id='rohsDelete'>";
			html += "		<input type='hidden' name='" + rohsOid +"' id='rohsOid' value='" + obj[i][0] + "' />";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][1];
			html += "		<input type='hidden' name='rohsNumber' value='" + obj[i][1] + "' />";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][3];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][4];
			html += "	</td>";
			html += "</tr>";
	
			$("#rohsTable").append(html);
		}
	}
}

<%----------------------------------------------------------
*                      등록문서 문서 추가
----------------------------------------------------------%>
function addConDoc(obj) {
	
	for (var i = 0; i < obj.length; i++) {
		var html = ""
		html += "<tr id='" + obj[i].oid + "'>";
		html += "	<td class='tdwhiteM'>";
		html += "		<input type='checkbox' name='rohsDelete' id='rohsDelete'>";
		html += "		<input type='hidden' name='" + rohsOid +"' id='rohsOid' value='" + obj[i].oid + "' />";
		html += "	</td>";
		html += "	<td class='tdwhiteM'>";
		html += obj[i].number;
		html += "	</td>";
		html += "	<td class='tdwhiteM'>";
		html += " <a href=javascript:openView('" + obj[i].oid + "')>" + obj[i].name + "</a>"
		html += "	</td>";
		html += "	<td class='tdwhiteM'>";
		html += obj[i].version;
		html += "	</td>";
		html += "</tr>";

		$("#rohsTable").append(html);
	}
}

<%----------------------------------------------------------
*                      검색 문서 중복 검색
----------------------------------------------------------%>
function DocDuplicateCheck(rohsNumber) {
	var obj = $("input[name='rohsNumber']");
	var checkCount = obj.size();
	for (var i = 0; i < checkCount; i++) {
		if(obj.eq(i).val() == rohsNumber) {
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
						<c:out value="${title }" />
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
												<button type="button" name="addRohs" id="addRohs" class="btnCustom">
													<span></span>
													${f:getMessage('추가')}
												</button>
											</td>
											
											<td>
												<button type="button" name="delRohs" id="delRohs" class="btnCustom">
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
									<table width="100%" cellspacing="0" cellpadding="1" border="0" id="rohsTable" align="center">
										<tbody>
											<tr>
												<td class="tdblueM"  width="5%"><input type="checkbox" name="allCheck" id="allCheck" ></td>
												<td class="tdblueM"  width="35%">${f:getMessage('물질번호')}</td>
												<td class="tdblueM"  width="50%">${f:getMessage('물질명')}</td>
												<td class="tdblueM0" width="10%">${f:getMessage('Rev.')}</td>
												
											</tr>
										
										<c:choose>
											<c:when test="${fn:length(list) != 0 }">
												<c:forEach items="${list }" var="rohsData">
													<tr align="center" bgcolor="#FFFFFF"> 
														<td class="tdwhiteM"  width="5%">
															<input type="checkbox" name="rohsDelete">
															<input type="hidden" name="<c:out value="${paramName}" />" id="rohsOid" value="<c:out value="${rohsData.oid }" />">
														</td>
														
														<td class="tdwhiteM"  width="35%">
															<c:out value="${rohsData.number }" />
														</td>
														
														<td class="tdwhiteM"  width="50%">
															<a href="javascript:openView('<c:out value="${rohsData.oid }" />')">
																<c:out value="${rohsData.name }" />
															</a>
														</td>
														
														<td class="tdwhiteM" width="10%">
															<c:out value="${rohsData.version }" />.<c:out value="${rohsData.iteration }" />
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