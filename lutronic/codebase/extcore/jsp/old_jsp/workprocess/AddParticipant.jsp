<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	loadApprovalLine("app1");
	loadApprovalLine("app2");
	loadApprovalLine("app3");
})

<%----------------------------------------------------------
*                      결재라인 데이터 가져오기
----------------------------------------------------------%>
function loadApprovalLine(type) {
	var form = $("form[name=addParticipant]").serialize();
	var url	= getURLString("groupware", "loadApprovalLine", "do") + "?workoid="+ $("#workOid").val() + "&type="+type;
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
			var app = "${f:getMessage('수신')}";
			if("app1" == type) {
				app = "${f:getMessage('합의')}";
			}else if("app2" == type) {
				app = "${f:getMessage('결재')}";
			}else if("app3" == type) {
				app = "${f:getMessage('수신')}";
			}
			
			for(var i=0; i<data.length; i++) {
				addLineBody(app,data[i].deptName,data[i].name,data[i].id,data[i].duty);
			}
			
		}
	});
}

$(function() {
	<%----------------------------------------------------------
	*                      결재 버튼
	----------------------------------------------------------%>
	$("#approvalBtn").click(function () {
		
		if (!confirm("${f:getMessage('결재하시겠습니까?')}")){
			return;
		}
		$("#addParticipant").attr("action", getURLString("groupware", "approveAction", "do")).submit();
	}),
	<%----------------------------------------------------------
	*                      결재선 지정 버튼
	----------------------------------------------------------%>
	$("#appLineBtn").click(function () {
		var url = getURLString("groupware", "approvalLine", "do") + "?workOid="+$("#workOid").val();
		openOtherName(url,"appLineBtn","1200","650","status=no,scrollbars=yes,resizable=no");
	})
})

<%----------------------------------------------------------
*                      결재선 데이터 설정
----------------------------------------------------------%>
function addLineUser(a1Arry,a2Arry,a3Arry) {
	for(var i=$("#lineBody > tr").length; i>0; i--) {
		$("#lineBody > tr").eq(i).remove();
	}
	
	for(var i=0; i<a1Arry.length; i++) {
		addLineBody("${f:getMessage('합의')}",a1Arry[i][3],a1Arry[i][4],a1Arry[i][5],a1Arry[i][6]);
	}
	
	for(var i=0; i<a2Arry.length; i++) {
		addLineBody("${f:getMessage('결재')}",a2Arry[i][3],a2Arry[i][4],a2Arry[i][5],a2Arry[i][6]);
	}
	
	for(var i=0; i<a3Arry.length; i++) {
		addLineBody("${f:getMessage('수신')}",a3Arry[i][3],a3Arry[i][4],a3Arry[i][5],a3Arry[i][6]);
	}
}

<%----------------------------------------------------------
*                      결재선 데이터 추가
----------------------------------------------------------%>
function addLineBody(type,dept,name,id,duty) {
	var html = "";
	html += "<tr>";

	if(type =="합의"){
		html += "	<td class=tdwhiteM0 style=background-color:#79ABFF >";
		
	}else if(type =="결재"){
		html += "	<td class=tdwhiteM0 style=background-color:#C3ED60 >";
	}else if(type =="수신"){
		html += "	<td class=tdwhiteM0 style=background-color:#FFCBCB >";
	}else{
		html += "	<td class=tdwhiteM>";
	}
	html += (($("#lineBody > tr").length));
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += type;
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += dept;
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += name;
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += id;
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += duty;
	html += "	</td>";
	html += "</tr>";
	$("#lineBody").append(html);
}

</script>

<body>

<form name="addParticipant" id="addParticipant"  method="post">

<input type="hidden" name="cmd" value="approveLine">
<input type="hidden" name="WfUserEvent" value="">
<input type="hidden" name="workOid" id="workOid" value="<c:out value="${workitemoid }" />">
<input type="hidden" name="oid" id="oid" value="<c:out value="${oid }" />">

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('나의업무')} > ${f:getMessage('작업함')} 
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" > <!--//여백 테이블-->
	<tr align="center">
		<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="right" colspan=2>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			               <tr>
			               		<td>
			               			<button class="btnCustom" id="appLineBtn" name="appLineBtn" type="button" >
			               				<span></span>
			               				${f:getMessage('결재선 지정')} 
			               			</button>
			               		</td>
			               		
			               		<td>
			               			<button class="btnCustom" id="approvalBtn" name="approvalBtn" type="button" >
			               				<span></span>
			               				${f:getMessage('결재')} 
			               			</button>
			               		</td>
						   </tr>
			           </table>
					</td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height=1 width=100%></td></tr>
			</table>
			
			<jsp:include page="/eSolution/groupware/processInfo.do">
				<jsp:param value="${workitemoid }" name="workitemoid"/>
			</jsp:include>
			
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td class="space5"> </td>
				</tr>
			</table>
			
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td class="tab_btm2"></td>
				</tr>
			</table>
			
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td  class="tab_btm1"></td>
				</tr>
			</table>
			
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td width=15% class="tdblueM">${f:getMessage('결재의견')} </td>
						<td class="tdwhiteL">
							<TEXTAREA NAME="comment" ROWS="3" COLS="100%" class=fm_area style='width:100%'></TEXTAREA>
						</td>
					</tr>
			</table>
			
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td class="space20"> </td>
				</tr>
			</table>
			
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="*" align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" > <b>${f:getMessage('결재선 지정')}</b></td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr>
					<td height=1 width=100%></td>
				</tr>
			</table>
			
			<table name='approverInfo' id='approverInfo' border="0" cellpadding="0" cellspacing="0" width="100%"> <!--//여백 테이블-->
				<tr>
					<td height="23"  class="tdblueM">${f:getMessage('순서')}</TD>
				    <td  class="tdblueM">${f:getMessage('구분')}</TD>
				    <td height="23"  class="tdblueM">${f:getMessage('부서')}</TD>
				    <td  class="tdblueM">${f:getMessage('이름')}</TD>
				    <td height="23"  class="tdblueM">ID</TD>
				    <td height="23"  class="tdblueM">${f:getMessage('직위')}</TD>
			   </tr>
			   
			   <tbody id="lineBody">
			   		<tr>
			   			<td class="tdwhiteM0" style="background-color:#FFFFA1" >0</TD>
			   			
			   			<td class="tdwhiteM">
			   				${f:getMessage('기안')}
			   			</TD>
			   			
			   			<td height="23"  class="tdwhiteM">
			   				<c:out value="${deptName }" />
			   			</TD>
			   			
			   			<td class="tdwhiteM">
			   				<c:out value="${name }" />
			   			</TD>
			   			
					    <td height="23"  class="tdwhiteM">
					   		<c:out value="${id }" />
					    </TD>
					    
					    <td height="23"  class="tdwhiteM">
					    	<c:out value="${duty }" />
					    </TD>
			   		</tr>
			   </tbody>
			</table>
			
			<br>
			
			<jsp:include page="/eSolution/groupware/include_mailUser">
				<jsp:param value="${workitemoid }" name="workOid"/>
			</jsp:include>
			
		</td>
	</tr>
</table>

</form>

</body>
</html>