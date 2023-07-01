<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
	$("#submitBody").hide();
	var oid = "<c:out value='${oid}'/>";
	if(oid != "") {
		wfProcessInfoAction(oid, "", "", "", "");
	}
})

$(function() {
	$("#submit").click(function() {
		var oid = $("#oid").val();
		wfProcessInfoAction(oid, "", "", "", "");
	})
	
	$("#delete").click(function() {
		wfProcessInfoAction($("#oid").val(), "delete", "", "", "");
	})
	
	$("#reassign").click(function() {
		wfProcessInfoAction($("#oid").val(), "reassign", $("#lifecycle").val(), "", "");
	})
	
	$("#statechange").click(function() {
		wfProcessInfoAction($("#oid").val(), "stateChange", "", $("#state").val(), $("#terminate:checked").val());
	})
	
	$("#goBack").click(function() {
		document.location = getURLString("groupware", "wfProcessInfo", "do");
	})
	
})

function wfProcessInfoAction(oid, command, lifecycle, state, terminate) {
	var url	= getURLString("groupware", "wfProcessInfoAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{oid: oid, command: command, lifecycle: lifecycle, state: state, terminate: terminate},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			if(data.command == "error") {
				alert(data.message);
			}else if(data.command == "") {
				$("#submitTable").hide();
				$("#submitBody").show();
				setObjectData(data);
			}else if(data.command == "delete") {
				alert(data.message);
				document.location = getURLString("groupware", "wfProcessInfo", "do");
				
				if($("#isPoup").val() == "true") {
					self.close();
				}
				
			}else if(data.command == "reassign") {
				alert(data.message);
				$("#lifecycle").html(data.targetLC);
				wfProcessInfoAction(oid, "", "", "", "");
			}else if(data.command == "stateChange") {
				alert(data.message);
				$("#targetState").html(data.targetState);
				wfProcessInfoAction(oid, "", "", "", "");
			}
		}
	});
}

function setObjectData(data) {
	$("#targetOid").html(data.targetOid);
	$("#targetLC").html(data.targetLC);
	$("#targetState").html(data.targetState);
	lfn_getStateList(data.targetLC)
	
	var wf = data.wf;
	if(wf.length > 0) {
		$("#wf tr").remove();
		for(var i=0; i<wf.length; i++) {
			var html = "";
			html += "<tr>";
			html += "	<td class='td0'>";
			html += "		<a href='javascript:void(0);' onclick=javascript:processHistory('/Windchill/ptc1/process/info?oid=" + wf[i].wfOid + "&action=ProcessManager')>";
			html += wf[i].wfName;
			html += "		</a>";
			html += "	</td>";
			html += "	<td class='td0'>";
			html += wf[i].wfState;
			html += "	</td>";
			html += "	<td class='td0'>";
			html += wf[i].wfCreate;
			html += "	</td>";
			
			$("#wf").append(html);
		}
	}
	
	var content = data.content;
	if(content.length > 0 ) {
		$("#content").html("");
		for(var i=0; i<content.length; i++) {
			var html = "";
			html += "<a target=blank href='" + content[i].url + "'>";
			html += content[i].name;
			html += "</a>";
			
			if(i < (content.length-1)) {
				html += "<br>";	
			}
			
			$("#content").append(html);
		}
	}
}


<%----------------------------------------------------------
*                      Lifecycle 상태 가져오기
----------------------------------------------------------%>
function lfn_getStateList(lifecycle) {
	var url	= getURLString("WFItem", "lifecycleList", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{lifecycle:lifecycle},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('상태 리스트 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			$("#state").find("option").remove();
			if(data.length > 0) {
				for(var i=0; i<data.length; i++) {
					$("#state").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
				}
			}
			
		}
	});
}

</script>

<style type="text/css" media="screen">
<!--

h1#header
{
	color: #036;
	font-size: 120%;
	font-weight: bold;
	text-transform: uppercase;
	text-align: center;
	letter-spacing: .5em;
	padding: .4em 0;
	border-top: 1px solid #069;
	border-bottom: 1px solid #069;
}

.label0 {
	text-align:center;
	background-color:#ADD8E6;
	font: 1em arial, helvetica, sans-serif;
	color:#191970;
	
}

.td0 {
	text-align:left;
	background-color:#FFFFFF;
	font: 1em arial, helvetica, sans-serif;
	color:#191970;

}

a {
	text-decoration: none;
}

// -->
</style>

<body>

<form name="wfProcessInfo" id="wfProcessInfo" method=post >

<input type="hidden" id="isPoup" name="isPoup" value="<c:out value='${isPoup}'/>">

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('나의업무')} > ${f:getMessage('관리자메뉴')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<h1 id="header">
	CAUTION
</h1>

<table id="submitTable">
	<tr>
		<td>
			<input type='text' name='oid' id='oid' value='<c:out value="${oid }"/>'> 
			<button type="button" name="submit" id="submit" class="btnCRUD">
				<span></span>
				submit
			</button>
		</td>
	</tr>
</table>

<div id="submitBody">

<table cellspacing="1" cellpadding="0" border="0" style="background-color:#4682B4;" width="100%">
	<col width='15%'><col width='85%'>
		<tr>
			<td class='label0'>
				OID
			</td>
			<td class='td0'>
				<i id="targetOid" ></i>
				<input type='button' name='delete' id='delete' value='Delete' >
			</td>
		</tr>

		<tr>
			<td class='label0'>
				LifeCycleTemplate
			</td>
			<td class='td0'>
				<i id="targetLC" ></i>
				<br>
				New LifeCycleTemplate&nbsp;:&nbsp;<input type='text' name='lifecycle' id='lifecycle' value=''>
				<input type='button' name='reassign' id='reassign' value='reassign' >
			</td>
		</tr>
		
		<tr>
			<td class='label0'>
				State
			</td>
			<td class='td0'>
				<i id="targetState" ></i>
				<br>
				<select name="state" id="state" style="width:110" >
				</select>
				&nbsp;&nbsp;
				TERMINATE : 
				<input type='radio' name='terminate' id='terminate' value='true' checked>TRUE&nbsp;
				<input type='radio' name='terminate' id='terminate' value='false'>FALSE

				<input type='button' name='statechange' id='statechange' value='상태변경'>
			</td>
		</tr>
</table>

<br>

<table cellspacing="1" cellpadding="0" border="0" style="background-color:#4682B4;" width="100%">
<col width='60%'><col width='20%'><col width='20%'>
	<tr>
		<td class='label0'>
			Name
		</td>
		<td class='label0'>
			State
		</td>
		<td class='label0'>
			Created Time
		</td>
	</tr>
	
	<tbody id="wf">
	</tbody>
	
</table>

<br>

<table cellspacing="1" cellpadding="0" border="0" style="background-color:#4682B4;" width="100%">
	<col width='15%'><col width='85%'>
	<tr>
		<td class='label0'>
			File
		</td>
		<td class='td0' id="content">
		</td>
	</tr>
	
	
</table>

<table>
	<tr>
		<td>
			<input type='button' name='goBack' id='goBack' value="${f:getMessage('뒤로')}">
		</td>
	</tr>
</table>

</div>

</form>

</body>
</html>