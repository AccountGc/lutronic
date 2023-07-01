<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
	getDutyList();
})

$(function() {
	$("#duty").change(function() {
		getDutyList();
	})
	$("#leftMove").click(function() {
		if($("#duty").val() != "") {
			moveAction("noreglist","reglist");
		}else {
			alert("${f:getMessage('직위를 선택하세요.')}");
		}
	})
	$("#rightMove").click(function() {
		moveAction("reglist","noreglist");
	})
	$("#setDuty").click(function() {
		$("#reglist option").prop("selected", true);
		
		var form = $("form[name=setDutyForm]").serialize();
		var url	= getURLString("admin", "admin_setDutyAction", "do");
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
				getDutyList();
			}
		});
	})
})

function getDutyList() {
	var duty = $("#duty").val();
	var url	= getURLString("admin", "admin_getDutyListAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{duty:duty},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			var dutyUser = data.dutyUser;
			var noDutyUser = data.noDutyUser;
			
			lfn_addUser(dutyUser,"reglist");
			lfn_addUser(noDutyUser,"noreglist");
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

<form name="setDutyForm" id="setDutyForm" method="post">

<table width=95% height=40 align=center border=0>
	<tr>
		<td>
			<table border=0 cellpadding=0 cellspacing=0 >
				<tr>
					<td><img src=/Windchill/jsp/portal/images/icon/title2_left.gif></td>
					<td background=/Windchill/jsp/portal/images/icon/title_back.gif>${f:getMessage('직위 설정')}</td>
					<td><img src=/Windchill/jsp/portal/images/icon/title2_right.gif></td>
				</tr>
			</table>
		</td>
		<td align="right">
			<button type="button" id="setDuty" >
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
		<td width="45%" >${f:getMessage('직위별 등록 인원')}</td>
		<td width="10%" ></td>
		<td width="45%" >${f:getMessage('직위별 미등록 인원')}</td>
	</tr>
	
	<tr bgcolor="#FFFFFF" align="center">
		<td width="45%" id=textblue>
			<select name="duty" id="duty" style="width:50%;">
				<option value="">-- ${f:getMessage('직위 선택')} --</option>
				<c:forEach items="${list }" var="list">
					<option value="<c:out value='${list.code}'/>">
						<c:out value='${list.name}'/>
					</option>
				</c:forEach>
			
			</select>
		</td>
		
		<td width="10%" id=textblue valign="middle" rowspan=2>
			<img src="/Windchill/jsp/portal/images/icon/btn_preview.gif" border="0" id="leftMove" style="cursor: pointer;">
			<br><br>
			<img src="/Windchill/jsp/portal/images/icon/btn_next.gif" border="0" id="rightMove" style="cursor: pointer;">
		</td>
		
		<!-- 직위별 미등록 인원 -->
		
		<td width="45%" id=textblue rowspan=2>
			<select name="noreglist" id="noreglist" size="20" style="width:100%;" multiple>
			</select>
			
		</td>
	</tr>
	
	<tr height=23 bgcolor="#FFFFFF">
		<!-- 직위별 등록 인원 -->
		<td width="45%" id=textblue valign="top">
			<select name="reglist" id="reglist" size="18" style="width:100%;" multiple>
			</select>
		</td>
	</tr>
</table>

</form>

</body>
</html>