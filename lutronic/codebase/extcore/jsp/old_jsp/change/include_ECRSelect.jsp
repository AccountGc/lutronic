<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">

$(function() {
	$("#addECR").click(function () {
		selectECR();
	})
	
	$("#delECR").click(function() {
		var obj = $("input[name='ecrDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
		$("#ecrCheck").prop("checked", "");
	})
	
	$("#ecrCheck").click(function() {
		if(this.checked) {
			$("input[name='ecrDelete']").prop("checked", "checked");
		}else {
			$("input[name='ecrDelete']").prop("checked", "");
		}
	})
})


<%----------------------------------------------------------
*                      관련 ECR PopUP
----------------------------------------------------------%>
function selectECR(){
	var url = getURLString("changeECR", "selectECRPopup", "do");
	openOtherName(url,"selectECRPopup","1180","880","status=no,scrollbars=yes,resizable=yes");
}



<%----------------------------------------------------------
*                      검색 ECR 추가
----------------------------------------------------------%>
function addECR(obj) {
	for (var i = 0; i < obj.length; i++) {
		
		if(DocDuplicateCheck(obj[i][1])) {
			var html = ""
			html += "<tr id='" + obj[i][0] + "'>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='ecrDelete' id='ecrDelete'>";
			html += "		<input type='hidden' name='ecrOid' id='ecrOid' value='" + obj[i][0] + "' />";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][1];
			html += "		<input type='hidden' name='ecrNumber' value='" + obj[i][1] + "' />";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][2];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][3];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][4];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][5];
			html += "	</td>";
			html += "</tr>";
	
			$("#ecrTable").append(html);
		}
	}
}


<%----------------------------------------------------------
*                      검색 문서 중복 검색
----------------------------------------------------------%>
function DocDuplicateCheck(docNumber) {
	var obj = $("input[name='ecrNumber']");
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



<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td align="left">
			<table border="0" cellpadding="0" cellspacing="2">
				<tr>
					<td>
						<button type="button" name="addECR" id="addECR" class="btnCustom">
							<span></span>
							${f:getMessage('추가')}
						</button>
					</td>
					
					<td>
						<button type="button" name="delECR" id="delECR" class="btnCustom">
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
		<div style="width:98%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;">
			<table width="100%" cellspacing="0" cellpadding="1" border="0" id="ecrTable" align="center">
				<tbody>
					<tr bgcolor="9acd32" height="25">
						<td class="tdblueM"  width="5%"><input type="checkbox" name="ecrCheck" id="ecrCheck"></td>
						<td class="tdblueM" width="20%">CR/ECPR${f:getMessage('번호')}</td>
						<td class="tdblueM" width="*">CR/ECPR${f:getMessage('제목')}</td>
						<td class="tdblueM" width="10%">${f:getMessage('상태')}</td>
						<td class="tdblueM" width="15%">${f:getMessage('등록자')}</td>
						<td class="tdblueM" width="10%">${f:getMessage('등록일')}</td>
					</tr>
					<c:choose>
							<c:when test="${fn:length(list) != 0 }">
								<c:forEach items="${list}" var="ecrData"  varStatus="status">
									<tr>
										<td class="tdwhiteM">
											<input type='checkbox' name='ecrDelete' id='ecrDelete'>
											<input type='hidden' name='ecrOid' id='ecrOid' value='<c:out value="${ecrData.oid }" />' />
										</td>
										<td class="tdwhiteM">
											<c:out value="${ecrData.number }" />
											<input type='hidden' name='ecrNumber' value='<c:out value="${ecrData.number }" />' />
										</td>
										<td class="tdwhiteM">
											<a href=javascript:openView('<c:out value="${ecrData.oid }" />')>
												<c:out value="${ecrData.name }" />
											</a>
										</td>
										<td class="tdwhiteM">
											<c:out value="${ecrData.lifecycle }" />
										</td>
										<td class="tdwhiteM">
											<c:out value="${ecrData.creator }" />
										</td>
										<td class="tdwhiteM">
											<c:out value="${ecrData.dateSubString(true) }" />
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
					
</body>
</html>