<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">

var epmOid = "<c:out value='${paramName}' />";

$(function() {
	<%----------------------------------------------------------
	*                      도면 추가 버튼
	----------------------------------------------------------%>
	$("#addDrw").click(function () {
		var url = getURLString("drawing", "selectDrawingPopup", "do");
		openOtherName(url,"window","1180","880","status=no,scrollbars=yes,resizable=yes");
	}),
	<%----------------------------------------------------------
	*                      도면 삭제 버튼
	----------------------------------------------------------%>
	$("#delDrw").click(function () {
		var obj = $("input[name='epmDelete']");
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
			$("input[name='epmDelete']").prop("checked", "checked");
		}else {
			$("input[name='epmDelete']").prop("checked", "");
		}
	})
})

<%----------------------------------------------------------
*                      선택 도면 데이터 추가
----------------------------------------------------------%>
function addDrawing(obj) {
	for (var i = 0; i < obj.length; i++) {
		if(DuplicateCheck(obj[i][0])) {
			var html = "";
			html += "<tr align=center bgcolor=#FFFFFF>";
			html += "	<td class=tdwhiteM height=22>";
			html += "		<input type=checkbox name=epmDelete id=epmDelete>";
			html += "		<input type=hidden name=" + epmOid + " id=epmOid value=" + obj[i][0] + ">";
			html += "	</td>";
			html += "	<td class=tdwhiteM height=22>";
			html += obj[i][2];
			html += "	</td>";
			html += "	<td class=tdwhiteM width=50%>";
			html += obj[i][3];
			html += "	</td>";
			html += "	<td class=tdwhiteM width=10%>";
			html += obj[i][4];
			html += "	</td>";
			html += "</tr>";
			
			$("#DrwTable").append(html);
		}
	}
}

<%----------------------------------------------------------
*                      검색 문서 중복 검색
----------------------------------------------------------%>
function DuplicateCheck(oid) {
	var obj = $("input[name='" + epmOid + "']");
	var checkCount = obj.size();
	for (var i = 0; i < checkCount; i++) {
		if(obj.eq(i).val() == oid) {
			return false;
		}
	}
	return true;
}

</script>

<body>

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
					<td class="tdblueM" colspan="2" width="20%">
						<c:out value="${title }" />
					</td>
					<td class="tdwhiteL" colspan="4">
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
												<button type="button" name="addDrw" id="addDrw" class="btnCustom">
													<span></span>
													${f:getMessage('추가')}
												</button>
											</td>
											
											<td>
												<button type="button" name="delDrw" id="delDrw" class="btnCustom">
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
									<table width="100%" cellspacing="0" cellpadding="1" border="0" id="DrwTable" align="center">
										<tbody>
											<tr>
							                    <td class="tdblueM" width="4%"><input type="checkbox" name="allCheck" id="allCheck" ></td>
							                    <td class="tdblueM" width="20%">${f:getMessage('도면')}${f:getMessage('번호')}</td>
							                    <td class="tdblueM" width="37%" style="word-break:break-all;">${f:getMessage('도면명')}</td>
							                    <td class="tdblueM" width="6%">Rev.</td>
							                </tr>
										
										<c:choose>
											<c:when test="${fn:length(list) != 0 }">
												<c:forEach items="${list }" var="epmData">
												
													<tr align="center" bgcolor="#FFFFFF"> 
														<td class="tdwhiteM" height="22">
															<input type="checkbox" name="epmDelete" id="epmDelete">
															<input type="hidden" name="<c:out value="${paramName}" />" id="epmOid" value="<c:out value="${epmData.oid }" />">
														</td>
														
														<td class="tdwhiteM" height="22">
															<c:out value="${epmData.number }" />
														</td>
														
														<td class="tdwhiteM" width="50%">
															<a href="javascript:openView('<c:out value="${epmData.oid }" />')">
																<c:out value="${epmData.name }" />
															</a>
														</td>
														
														<td class="tdwhiteM" width="10%">
															<c:out value="${epmData.version }" />
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