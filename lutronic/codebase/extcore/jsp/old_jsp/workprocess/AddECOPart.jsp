<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
var oid = "<c:out value='${oid}' />";
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	$("#addBtn").click(function () {
		var url = getURLString("part", "selectPartPopup", "do") + "?mode=mutil&moduleType=ECO";
		openOtherName(url,"","1180","880","status=no,scrollbars=yes,resizable=yes");
	}),
	<%----------------------------------------------------------
	*                      품목 삭제 버튼
	----------------------------------------------------------%>
	$("#delBtn").click(function () {
		var obj = $("input[name='partDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
	})
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
		if (!confirm("${f:getMessage('수정하시겠습니까?')}")){
			return;
		}
		$("#addPartForm").attr("action", getURLString("change", "AddPartAction", "do") + "?oid=" + $("#oid").val()).submit();
	})
	<%----------------------------------------------------------
	*                      품목 전체 선택
	----------------------------------------------------------%>
	$("#partCheck").click(function(){
		if($("#partDelete").prop("checked")) {
			$("input[name=partDelete]").prop("checked",false);
		}else{
			$("input[name=partDelete]").prop("checked",true);
		}
	})
})


<%----------------------------------------------------------
*                      선택 품목 데이터 중복 검사
----------------------------------------------------------%>
function PartDuplicateCheck(number) {
	var obj = $("input[name='partNumber']");
	var checkCount = obj.size();
	for (var i = 0; i < checkCount; i++) {
		
		if(obj.eq(i).val() == number) {
			
			return false;
		}
	}
	return true;
}

function viewBom(oid) {
	//var url = getURLString("part", "partExpand", "do") + "?partOid=" + oid + "&moduleType="+$("#moduleType").val();
	var url = getURLString("part", "partAUIExpand", "do") + "?partOid=" + oid + "&moduleType="+$("#moduleType").val();
	openOtherName(url,"openBom","1180","880","status=no,scrollbars=yes,resizable=yes");
	//openOtherName(url,"openBom","1180","880","status=no,scrollbars=yes,resizable=yes");
}

<%----------------------------------------------------------
*                      선택 품목 데이터 추가
----------------------------------------------------------%>
function addPart(obj,isHref) {
	var cnt = 0;
	for (var i = 0; i < obj.length; i++) {
		
		if(PartDuplicateCheck(obj[i][1])) {
			var html = ""
			html += "<tr id='" + obj[i][0] + "'>";
			html += "	<td class='tdwhiteM' style='background-color:powderblue;'>";
			html += "		<input type='checkbox' name='partDelete' id='partDelete'>";
			html += "		<input type='hidden' name='partOid' id='partOid' value='" + obj[i][0] + "' />";
			html += "		<input type='hidden' name='partNumber' id='partNumber' value='"+obj[i][1]+"' />"
			html += "	</td>";
			html += "	<td class='tdwhiteM' style='background-color:powderblue;'>";
			html += obj[i][1];
			html += "	</td>";
			html += "	<td class='tdwhiteM' style='background-color:powderblue;'>";
			
			if(isHref) {
				html += "<a href=javascript:openView('" + obj[i][0] + "')>";
				html += obj[i][2];
				html += "</a>";
			}else {
				html += obj[i][2];
			}
			
			html += "	</td>";
			html += "	<td class='tdwhiteM' style='background-color:powderblue;'>";
			html += (obj[i][3]);
			html += "	</td>";
			html += "	<td class='tdwhiteM' style='background-color:powderblue;'>";
			html += "		<input type='button' name='bom' onclick=javascript:viewBom('" + obj[i][0]+ "') value='BOM ${f:getMessage('전개')}'>";
			html += "	</td>";
			/*
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='isSelecctBom' id='" + obj[i][0] + "' value='" + obj[i][1] + "'>";
			html += "	</td>";
			*/
			html += "</tr>";
			console.log($("#data").length);
			if ( $("#data").length !=0 ){
				$("#data").before(html);
			}else{
				$("#partTable").append(html);
			}

			cnt++;
		}
	}
	return cnt;
		
}
<%----------------------------------------------------------
*                      선택 품목 데이터 추가
----------------------------------------------------------%>
function addPart2(obj,isHref) {
	for (var i = 0; i < obj.length; i++) {
		
		if(PartDuplicateCheck(obj[i][1])) {
			var html = ""
			html += "<tr id='" + obj[i][0] + "'>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='partDelete' id='partDelete'>";
			html += "		<input type='hidden' name='partOid' id='partOid' value='" + obj[i][0] + "' />";
			html += "		<input type='hidden' name='partNumber' id='partNumber' value='"+obj[i][1]+"' />"
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][1];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			
			if(isHref) {
				html += "<a href=javascript:openView('" + obj[i][0] + "')>";
				html += obj[i][2];
				html += "</a>";
			}else {
				html += obj[i][2];
			}
			
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += (obj[i][3]+obj[i][4]);
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='button' name='bom' onclick=javascript:viewBom('" + obj[i][0]+ "') value='BOM ${f:getMessage('전개')}'>";
			html += "	</td>";
			/*
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='isSelecctBom' id='" + obj[i][0] + "' value='" + obj[i][1] + "'>";
			html += "	</td>";
			*/
			html += "</tr>";
	
			$("#partTable").append(html);
		}
	}
}
</script>
</head>
<body>

<form name="addPartForm" id="addPartForm" method="post" style="padding:0px;margin:0px" >
<input type="hidden" name="oid" id="oid" value="<c:out value='${oid}' />"/>
<input type="hidden" name="number" id="number" value="<c:out value='${number}' />"/>
<table width="100%" border="0" cellpadding="0" cellspacing="0" >
    <tr>
        <td>
            <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center">
		   				<B><font color=white></font></B>
		   			</td>
		   		</tr>
			</table>
            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
            	<tr>
					<td>
						<table width="100%" border="0" cellpadding="0" cellspacing="4" >
							<tr>
								<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>ECO ${f:getMessage('품목추가')}</b></td>
								<td align="right" colspan="2">
									<button type="button" class="btnCustom" title="추가" id="addBtn" name="addBtn">
					                 	<span></span>
					                 	${f:getMessage('추가')}
				                	</button>
				                	<button type="button" class="btnCustom" title="삭제" id="delBtn" name="delBtn">
					                 	<span></span>
					                 	${f:getMessage('삭제')}
				                	</button>
									<button type="button" class="btnCRUD" title="수정" id="updateBtn" name="updateBtn">
					                 	<span></span>
					                 	${f:getMessage('수정')}
				                	</button>
									<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
				               			<span></span>
				               			${f:getMessage('닫기')}
			               		    </button>
								</td>
							</tr>	
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr  align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">

			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
				<tr><td height=1 width=100%></td></tr>
			</table>
			<table width="100%" cellspacing="0" cellpadding="1" border="0" id="partTable" align="center">
				<tbody>
					<tr>
						<td class="tdblueM" width="5%"><input type="checkbox" name="partCheck" id="partCheck"></td>
		                <td class="tdblueM" width="25%">${f:getMessage('번호')}</td>
		                <td class="tdblueM" width="*">${f:getMessage('품목명')}</td>
		                <td class="tdblueM" width="10%">Rev.</td>
		                <td class="tdblueM" width="20%">BOM ${f:getMessage('전개')}</td>
		                <!--  
		                <td class="tdblueM" width="10%">BOM ${f:getMessage('전송')}</td>
		                -->
					</tr>
					<c:choose>
						<c:when test="${fn:length(list) != 0 }">
							<c:forEach items="${list}" var="partData" varStatus="status" >
								<tr id="data">
									<td class="tdwhiteM" width="5%">
										<input type='checkbox' name='partDelete' id='partDelete'>
										<input type='hidden' name='partOid' id='partOid' value="<c:out value="${partData.partOid }" />" />
									</td>
									
									<td class="tdwhiteM" width="25%">
										<input type='hidden' name='partNumber' id='partNumber' value="<c:out value="${partData.number }" />" />
										<c:out value="${partData.number }" />
									</td>
									
									<td class="tdwhiteM" width="30%">
										<a href="javascript:openView('<c:out value="${partData.partOid }" />')">
											<c:out value="${partData.name }" />
										</a>
									</td>
									
									<td class="tdwhiteM" width="10%">
										<c:out value="${partData.version }" />.<c:out value="${partData.iteration }" />
									</td>
									
									<td class="tdwhiteM" width="10%">
										<c:out value="${partData.bom }" escapeXml="false"/>
									</td>
									<!--  
									<td class="tdwhiteM" width="10%">
										<input type='checkbox' name='isSelecctBom' id='isSelecctBom' value="<c:out value="${partData.number }" />" <c:out value="${partData.BOMValueCheck }" /> />
									</td>
									-->
								</tr>
							</c:forEach>
						</c:when>
					</c:choose>
				</tbody>
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>