<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="/Windchill/extcore/jsp/css/e3ps.css">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">
$(document).ready(function() {
	divPageLoad("devBody", "viewDevelopment", $("#oid").val());
	divPageLoad("taskDiv", "viewTaskList", $("#oid").val());
	divPageLoad('userDiv', 'viewUserList', $('#oid').val());
})

$(function() {
	<%----------------------------------------------------------
	*                      구성원 접기 / 펼치기
	----------------------------------------------------------%>
	$("#divToggle").click(function() {
		if ( $( "#divTo" ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#divTo").slideToggle();
	})
})

<%----------------------------------------------------------
*                      페이지 이동 function
----------------------------------------------------------%>
window.divPageLoad = function(divId, url, oid) {
	if(url.length > 0) {
		var url	= getURLString("development", url, "do");
		$.ajax({
			type:"POST",
			url: url,
			data: {
				oid : oid
			},
			success:function(data){
				$('#' + divId).html(data);
			}
		});
	} else {
		$('#' + divId).html('');
	}
}

<%----------------------------------------------------------
*                     Master 삭제
----------------------------------------------------------%>
window.deleteDevlopment = function() {
	if (confirm("${f:getMessage('삭제하시겠습니까?')}")){
		var url	= getURLString("development", "deleteDevelopmentAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data: {
				oid : $("#oid").val()
			},
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				alert("${f:getMessage('삭제 오류 발생')}");
			},
			success:function(data){
				alert(data.message);
				if(data.result) {
					document.location = getURLString("development", "listDevelopment", "do");
				}
			}
			,beforeSend: function() {
				gfn_StartShowProcessing();
	        }
			,complete: function() {
				gfn_EndShowProcessing();
	        }
		});
	}
}

<%----------------------------------------------------------
*                      Task 상세 보기
----------------------------------------------------------%>
window.viewTask = function(oid) {
	var url = getURLString("development", "bodyTask", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			oid : oid
		},
		success:function(data){
			$('#viewTask').html(data);
		}
	});
}

<%----------------------------------------------------------
*                      Master, Task 상태 변경
----------------------------------------------------------%>
window.changeState = function(oid, state, type) {
	var url	= getURLString("development", "changeStateAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			oid : oid,
			state : state
		},
		dataType:"json",
		async: true,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('등록 오류')}";
			alert(msg);
		},

		success:function(data){
			if(data.result) {
				alert("${f:getMessage('작업을 성공하였습니다.')}");
			}else {
				alert("${f:getMessage('작업을 실패하였습니다.')} \n" + data.message);
			}
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
			if(type == '1') {
				divPageLoad("devBody", "viewDevelopment", $("#oid").val());
			}else if(type == '2') {
				divPageLoad("taskDiv", "viewTaskList", $("#oid").val());
				viewTask(oid);
			}
        }
	});
}
</script>

<body>

<form name="viewDevelopmentForm" id="viewDevelopmentForm" method="post" >

<input type="hidden" name="oid" id="oid" value="<c:out value="${oid }"/>" />

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						${f:getMessage('개발업무')}
						${f:getMessage('관리')}
						>
						${f:getMessage('개발업무')}
						${f:getMessage('상세보기')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<div id="devBody">
</div>

</form>

<!-- 구성원 보기 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >	
	<tr bgcolor="#ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>        
	        
    <tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>${f:getMessage('구성원')}</b>
		</td>
		
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="divToggle" >
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<div id="divTo">
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
		   			<tr>
		       			<td height="1" width="100%"></td>
	    			</tr>
				</table>
				
				<div id='userDiv'>
				</div>
				
			</div>
		</td>
	</tr>
</table>

<br>

<!-- Task List 보기 -->
<div id="taskDiv" style="width: 50%; float: left;">
</div>

<!-- Task 보기 -->
<div id="viewTask" style="width: 50%; float: left;">
</div>

</body>
</html>