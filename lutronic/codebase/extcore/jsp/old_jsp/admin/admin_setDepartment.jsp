<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
	getDepartmentList();
})

$(function() {
	$("#leftMove").click(function() {
		moveAction("noreglist","reglist");
	})
	$("#rightMove").click(function() {
		moveAction("reglist","noreglist");
	})
	$("#setDept").click(function() {
		$("#reglist option").prop("selected", true);
		
		var form = $("form[name=setDepartmentForm]").serialize();
		var url	= getURLString("admin", "admin_setDeptAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: false,
			cache: false,

			error:function(data){
				var msg = "${f:getMessage('직위 설정 오류')}";
				alert(msg);
			},

			success:function(data){
				getDepartmentList();
			}
		});
	})
})

function getDepartmentList() {
	var oid = "<c:out value='${departmentOid }' />";
	var url	= getURLString("admin", "admin_getDepartmentListAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{oid:oid},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			var deptUser = data.deptUser;
			var noDeptUser = data.noDeptUser;

			lfn_addUser(deptUser,"reglist");
			lfn_addUser(noDeptUser,"noreglist");
		}
	});
}

<%----------------------------------------------------------
*                     검색된 사용자 리스트 추가
----------------------------------------------------------%>
function lfn_addUser(data,ulId) {
	$("#" + ulId + " > option ").remove();
	for(var i=0; i<data.length; i++) {
		var peopleOid = data[i].peopleOid;
		var peopleName = data[i].peopleName;
		var peopleDuty = data[i].peopleDuty;
		var peopleId = data[i].peopleId;
		
		$("#"+ulId).append("<option value='" + peopleOid + "'>" + peopleName + ":" + peopleId + "(" + peopleDuty + ")</option>");
		
	}
}

function moveAction(fromId, toId) {
	$("#"+fromId+" option").each(function() {
		if(this.selected) {
			$(this).remove();
			$("#"+toId).append("<option value='" + this.value + "'>" + this.text + "</option>");
		}
	})
}

</script>

<body leftmargin="0" topmargin="0">

<form name="setDepartmentForm" id="setDepartmentForm" method="post">

<input type="hidden" name="oid" id="oid" value="<c:out value='${departmentOid }' />" />

<table width=95% height=40 align=center border=0>
	<tr>
		<td>
			<table border=0 cellpadding=0 cellspacing=0 >
				<tr>
					<td><img src=/Windchill/jsp/portal/images/icon/title2_left.gif></td>
					<td background=/Windchill/jsp/portal/images/icon/title_back.gif>${f:getMessage('부서 설정')} <b>[<c:out value="${departmentName }" />]</b></td>
					<td><img src=/Windchill/jsp/portal/images/icon/title2_right.gif></td>
				</tr>
			</table>
		</td>
		<td align="right">
			<button type="button" id="setDept" >
				<span></span>
				${f:getMessage('확인')}
			</button>
			<button type="button" onclick="javascript:closeWindow()">
				<span></span>
				${f:getMessage('닫기')}
			</button>
		</td>
	</tr>
</table>

<!-- 파란 라인 -->
<table width="95%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	<tr><td height=1 width=100%></td></tr>
</table>

<table width="95%" border="0" cellpadding="2" cellspacing="1" bgcolor=AABCC7 align=center>

	<tr height=23 bgcolor="#D9E2E7" align=center>
		<td width="45%" >${f:getMessage('부서 등록 인원')}</td>
		<td width="10%" ></td>
		<td width="45%" >${f:getMessage('부서 미등록 인원')}</td>
	</tr>
	
	<tr bgcolor="#FFFFFF" align="center">
		
		<!-- 부서 등록 인원 -->
		
		<td width="45%" id=textblue>
			<select name="reglist" id="reglist" size="20" style="width:100%;" multiple>
			</select>
			
		</td>

		<td width="10%" id=textblue valign="middle">
			<img src="/Windchill/jsp/portal/images/icon/btn_preview.gif" border="0" id="leftMove" style="cursor: pointer;">
			<br><br>
			<img src="/Windchill/jsp/portal/images/icon/btn_next.gif" border="0" id="rightMove" style="cursor: pointer;">
		</td>

		<!-- 부서 미등록 인원 -->
		<td width="45%" id=textblue valign="top">
			<select name="noreglist" id="noreglist" size="20" style="width:100%;" multiple>
			</select>
		</td>
	</tr>
	
</table>

</form>

</body>
</html>