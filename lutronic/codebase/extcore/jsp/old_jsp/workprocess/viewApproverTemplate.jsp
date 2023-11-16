<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<html>
<head>
<title>${f:getMessage('결재선 보기')}</title>
<script language="JavaScript" type="text/JavaScript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
	loadApprovalLine("app1");
	loadApprovalLine("app2");
	loadApprovalLine("app3");
})

<%----------------------------------------------------------
*                      결재라인 데이터 가져오기
----------------------------------------------------------%>
function loadApprovalLine(type) {
	var form = $("form[name=viewApproverTemplate]").serialize();
	var url	= getURLString("user", "loadApproverTemplate", "do") + "?oid="+ $("#oid").val() + "&type="+type;
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('검색오류')}";
			alert(msg);
		},

		success:function(data){
			var app = "${f:getMessage('통보')}";
			var className = "border_text_03-1";
			if(type == "app1") {
				app = "${f:getMessage('합의')}";
				className = "border_text_03-1";
			}else if(type == "app2") {
				app = "${f:getMessage('결재')}";
				className = "a_con_05";
			}else if(type == "app3") {
				app = "${f:getMessage('통보')}";
				className = "border_text_03-1";
			}
			
			for(var i=0; i<data.length; i++) {
				addAppLine(app, data[i].oid, data[i].deptName, data[i].name, data[i].id, data[i].duty, className);
			}
			
		}
	});
}

<%----------------------------------------------------------
*                      결재라인 정보 설정
----------------------------------------------------------%>
function addAppLine(app, uid, deptName, name, id, duty, className) {
	var count = $("#approverLink > tr").length;
	
	var html = "";
	html += "<tr>";
	html += "	<td class=" + className + ">";
	html += (count+1);
	html += "	</td>";
	html += "	<td class=" + className + ">";
	html += app;
	html += "	</td>";
	html += "	<td class=" + className + ">";
	html += deptName;
	html += "	</td>";
	html += "	<td class=" + className + ">";
	html += name;
	html += "	</td>";
	html += "	<td class=" + className + ">";
	html += id;
	html += "	</td>";
	html += "	<td class=" + className + ">";
	html += duty;
	html += "	</td>";
	html += "</tr>";
	
	$("#approverLink").append(html);
}
</script>

<body>

<form name="viewApproverTemplate" id="viewApproverTemplate">

<input type="hidden" name="oid" id="oid" value="<c:out value="${oid}"/>" />

<table width="650" border="0" align="center" cellpadding="0" cellspacing="0"class=9pt>
	<tr>
		<td colspan="2" valign="top"  class="border_text_03-1"><br> 
			<div id=list style="height:100%;" width="100%"> 
				<table width="95%" border="0" cellpadding="0" cellspacing="1" bgcolor=#752e41 align=center>
					<tr> 
						<td height=1 width=95%></td>
					</tr>
				</table>
				<table name='approverInfo' id='approverInfo'width="95%" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
					<tr> 
						<td height="23"  class="a_con_01">${f:getMessage('순서')}</TD>
						<td  class="a_con_01">${f:getMessage('구분')}</TD>
						<td height="23"  class="a_con_01">${f:getMessage('부서')}</TD>
						<td  class="a_con_01">${f:getMessage('이름')}</TD>
						<td height="23"  class="a_con_01">ID</TD>
						<td height="23"  class="a_con_01">${f:getMessage('직급')}</TD>
					</TR>
					<tbody id="approverLink">
					</tbody>
				</table>
			<br>
			</div>
		</TD>
	</TR>
	
	<tr>
		<td class="border_text_03-1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
</table>

</form>

</body>