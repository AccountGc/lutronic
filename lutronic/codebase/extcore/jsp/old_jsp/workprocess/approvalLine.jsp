<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">

// 현재 결재선 추가 상태 설정위한 변수
var appTypeUL = "";						 

<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
	lfn_SearchUser();
	appTypeUL = "searchUL";
	loadApprovalLine("app1");
	loadApprovalLine("app2");
	loadApprovalLine("app3");
})

<%----------------------------------------------------------
*                      결재선 데이터 가져오기
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
			var appType = 3;
			var app = "Report";
			
			if(type == "app1") {
				app = "Agree";
				appType = 1;
			}else if(type == "app2") {
				app = "Approver";
				appType = 2;
			}else if(type == "app3") {
				app = "Report";
				appType = 3;
			}
			
			for(var i=0; i<data.length; i++) {
				addAppLine(appType, app, data[i].userOid, data[i].deptName, data[i].name, data[i].id, data[i].duty);
			}
			
		}
	});
}

$(function() {
	$('#appType1').click(function() {
		$('input:radio[name=appType]').eq(0).prop('checked','checked');
	})
	$('#appType2').click(function() {
		$('input:radio[name=appType]').eq(1).prop('checked','checked');
	})
	$('#appType3').click(function() {
		$('input:radio[name=appType]').eq(2).prop('checked','checked');
	})
	<%----------------------------------------------------------
	*                      UL 에 MouseOver 시 Class 추가
	----------------------------------------------------------%>
	$("ul[name='uls']").on("mouseover", ".lineInc" ,function(){
        $(this).addClass("hover") ;
	})
	<%----------------------------------------------------------
	*                      UL 에 MouseOut 시 Class 삭제
	----------------------------------------------------------%>
	$("ul[name='uls']").on("mouseout", ".lineInc",function(){
        $(this).removeClass("hover");
	})
	<%----------------------------------------------------------
	*                      UL 에 MouseClick 시 Class 추가 및 삭제
	----------------------------------------------------------%>
	$("ul[name='uls']").on("click", ".lineInc",function(){
		$("li").removeClass("select");
		$("li").removeClass("hover");
        $(this).addClass("select");
	})
	<%----------------------------------------------------------
	*                      UL 에 MouseDubleClick 시  Event
	----------------------------------------------------------%>
	$("ul[name='uls']").on("dblclick", ".lineInc",function(){
		lfn_onDBClick();
	})
	<%----------------------------------------------------------
	*                     Search 메뉴 선택시
	----------------------------------------------------------%>
	$("#Search").click(function () {
		$('#searchTD').css('backgroundImage','url(/Windchill/jsp/portal/images/k_bt_01_ov.gif)');
		$('#orgTD').css('backgroundImage','url(/Windchill/jsp/portal/images/k_bt_02.gif)');
		$('#lineTD').css('backgroundImage','url(/Windchill/jsp/portal/images/k_bt_03.gif)');
		$("#searchDIV").show();
		$("#OrgDIV").hide();
		$("#lineDIV").hide();
		$("#addBtn").show();
		appTypeUL = "searchUL";
		lfn_SearchUser();
	})
	<%----------------------------------------------------------
	*                     Search 메뉴 - 검색 버튼
	----------------------------------------------------------%>
	$("#userSearch").click(function () {
		lfn_SearchUser();
	})
	<%----------------------------------------------------------
	*                     Enter 키 입력
	----------------------------------------------------------%>
	$("#userKey").keypress(function(event) {
		if( event.which == 13) {
			lfn_SearchUser();
		}
	})
	<%----------------------------------------------------------
	*                     Org 메뉴 선택시
	----------------------------------------------------------%>
	$("#Org").click(function () {
		$('#searchTD').css('backgroundImage','url(/Windchill/jsp/portal/images/k_bt_01.gif)');
		$('#orgTD').css('backgroundImage','url(/Windchill/jsp/portal/images/k_bt_02_ov.gif)');
		$('#lineTD').css('backgroundImage','url(/Windchill/jsp/portal/images/k_bt_03.gif)');
		$("#OrgDIV").show();
		$("#searchDIV").hide();
		$("#lineDIV").hide();
		$("#addBtn").show();
		appTypeUL = "orgUL";
		lfn_SearchOrg();
	})
	<%----------------------------------------------------------
	*                     App.Line 메뉴 선택시
	----------------------------------------------------------%>
	$("#Line").click(function () {
		$('#searchTD').css('backgroundImage','url(/Windchill/jsp/portal/images/k_bt_01.gif)');
		$('#orgTD').css('backgroundImage','url(/Windchill/jsp/portal/images/k_bt_02.gif)');
		$('#lineTD').css('backgroundImage','url(/Windchill/jsp/portal/images/k_bt_03_ov.gif)');
		$("#OrgDIV").hide();
		$("#searchDIV").hide();
		$("#lineDIV").show();
		//$("#addBtn").hide();
		appTypeUL = "lineUL";
		lfn_SearchLine();
	})
	<%----------------------------------------------------------
	*                     App.Line 메뉴 - Save 버튼
	----------------------------------------------------------%>
	$("#lineSaveBtn").click(function () {
		windEdit_Enter(3);
	})
	<%----------------------------------------------------------
	*                     App.Line 메뉴 - Delete 버튼
	----------------------------------------------------------%>
	$("#lineDelBtn").click(function () {
		windEdit_Enter(4);
	})
	<%----------------------------------------------------------
	*                     App.Line 메뉴 - Add 버튼
	----------------------------------------------------------%>
	$("#lineAddBtn").click(function () {
		windEdit_Enter(5);
	})
	<%----------------------------------------------------------
	*                     App.Line 메뉴 - Information 버튼
	----------------------------------------------------------%>
	$("#lineInfoBtn").click(function () {
		windEdit_Enter(6);
	})
	<%----------------------------------------------------------
	*                     추가 버튼
	----------------------------------------------------------%>
	$("#addBtn").click(function() {
		
		if(appTypeUL == "searchUL" || appTypeUL == "orgUL") {
			if($("input:radio[name=appType]:checked").size() == 0 ) {
				alert("${f:getMessage('결재 방법')}${f:getMessage('을(를) 선택하세요.')}");
				return;
			}
			var vArr  = lfn_getSeclcedUser();
			
			if(vArr.length == 0) {
				alert("${f:getMessage('결재자')}${f:getMessage('을(를) 선택하세요.')}");
				return;
			}
			
			var appType = $("input:radio[name=appType]:checked").val();
			lfn_addAppType(vArr,appType);
		
		}else if(appTypeUL == "lineUL") {
			var selectName = $('ul#lineUL > li.select').attr('title');
			
			if (!confirm("[" + selectName + "]${f:getMessage('을(를) 설정하시겠습니까?')}\n${f:getMessage('기존의 결재선이 지워집니다.')}")) {
				return; 
			}
			
			var loadOid = $('ul#lineUL > li.select').attr('oid');
			
			delAllAppLine();
			loadSavedApprovalLine(loadOid, "app1");
			loadSavedApprovalLine(loadOid, "app2");
			loadSavedApprovalLine(loadOid, "app3");
		}
		
	})
	<%----------------------------------------------------------
	*                     삭제 버튼
	----------------------------------------------------------%>
	$("#delBtn").click(function() {
		var obj = $("input[name='deleteChk']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
		$('.AllCheck').prop("checked", "");
	})
	<%----------------------------------------------------------
	*                     모두 삭제 버튼
	----------------------------------------------------------%>
	$("#delAllBtn").click(function() {
		if(confirm("${f:getMessage('결재선을')} ${f:getMessage('모두 삭제하시겠습니까?')}")){
			delAllAppLine();
		}
		$('.AllCheck').prop("checked", "");
	})
	<%----------------------------------------------------------
	*                     등록 버튼
	----------------------------------------------------------%>
	$("#createLine").click(function() {
		if($("#app2 tr").length == 0) {
			//alert("${f:getMessage('결재자는 1명이상 선택 해야 합니다.')}");
			//return;
		}
		
		var agrLine="";
		var a1Arry = new Array();
		for(var i=0; i<$("#app1 tr").length; i++) {
			
			a1Arry[i] = new Array();
			a1Arry[i][0] = $("input[name='Agree']").eq(i).val();
			agrLine += $("input[name='Agree']").eq(i).val();
			
			if((i+1) < $("#app1 tr").length) {
				agrLine += ",";
			}
			
			for(var j=1; j<$("#app1 tr").eq(i).children("td").length; j++) {
				a1Arry[i][j] = $.trim($("#app1 tr").eq(i).children("td").eq(j).text());
			}
		}
		
		var appLine="";
		var a2Arry = new Array();
		for(var i=0; i<$("#app2 tr").length; i++) {
			a2Arry[i] = new Array();
			a2Arry[i][0] = $("input[name='Approver']").eq(i).val();
			appLine += $("input[name='Approver']").eq(i).val();
			
			if((i+1) < $("#app2 tr").length) {
				appLine += ",";
			}
			
			for(var j=1; j<$("#app2 tr").eq(i).children("td").length; j++) {
				a2Arry[i][j] = $.trim($("#app2 tr").eq(i).children("td").eq(j).text());
			}
		}
		
		var tempLine="";
		var a3Arry = new Array();
		for(var i=0; i<$("#app3 tr").length; i++) {
			a3Arry[i] = new Array();
			a3Arry[i][0] = $("input[name='Report']").eq(i).val();
			tempLine += $("input[name='Report']").eq(i).val();
			
			if((i+1) < $("#app3 tr").length) {
				tempLine += ",";
			}
			
			for(var j=1; j<$("#app3 tr").eq(i).children("td").length; j++) {
				a3Arry[i][j] = $.trim($("#app3 tr").eq(i).children("td").eq(j).text());
			}
		}
		
		var result = saveApprovalLine(appLine,agrLine,tempLine);
		
		if(result.responseText == "true") {
			alert("${f:getMessage('결재선이 지정 되었습니다.')}");
			opener.parent.addLineUser(a1Arry,a2Arry,a3Arry);
			self.close();
		}else {
			alert("${f:getMessage('결재선 지정시 오류가 발생 하였습니다.')}");
		}
		
	})
	
	$('.AllCheck').click(function() {
		if(this.checked) {
			$('.'+ this.value + "AllCheck").prop("checked", "checked");
		}else {
			$('.'+ this.value + "AllCheck").prop("checked", "");
		}
	})
})

//////////////////////////////////////////////////////////////////////////
//공통 이벤트
<%----------------------------------------------------------
*                     모든 결재 선 삭제
----------------------------------------------------------%>
function delAllAppLine() {
	$("#app1 > tr").remove();
	$("#app2 > tr").remove();
	$("#app3 > tr").remove();
}

<%----------------------------------------------------------
*                     결재선 등록시 결재선 저장
----------------------------------------------------------%>
function saveApprovalLine(appLine,agrLine,tempLine) {
	var form = $("form[name=approvalLineForm]").serialize();
	var url	= getURLString("groupware", "saveApprovalLineAction", "do") + "?workItemOid=" + $("#workOid").val() + "&appLine="+appLine+"&agrLine="+agrLine+"&tempLine="+tempLine;
	return $.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		}

	});
}

<%----------------------------------------------------------
*                     선택된 사용자 확인
----------------------------------------------------------%>
function lfn_getSeclcedUser() {
   var vArr = new Array();
   if($('ul#' + appTypeUL + ' > li.select').length != 0) {
	   var id = $('ul#' + appTypeUL + ' > li.select').attr('id');
	   var uid = $('ul#' + appTypeUL + ' > li.select').attr('uid');
	   
	   var pid = $('ul#' + appTypeUL + ' > li.select').attr('pid');
	   var name = $('ul#' + appTypeUL + ' > li.select').attr('uname');
	   var deptName = $('ul#' + appTypeUL + ' > li.select').attr('dptname');
	   var duty = $('ul#' + appTypeUL + ' > li.select').attr('duty');
	
	   vArr[0] = id;
	   vArr[1] = uid;
	   vArr[2] = pid;
	   vArr[3] = name;
	   vArr[4] = deptName;
	   vArr[5] = duty;
   }
   
   return vArr;
}

<%----------------------------------------------------------
*                     검색된 사용자 리스트 추가
----------------------------------------------------------%>
function lfn_addUser(data,ulId) {
	$("#" + ulId + " li").remove();
	for(var i=0; i<data.length; i++) {
		var userOid = data[i].userOid;
		var peopleOid = data[i].peopleOid;
		var id = data[i].id;
		var name = data[i].name;
		var departmentName = data[i].deptName;
		var duty = data[i].duty;
		var dutyCode = data[i].dutyCode;
		var email = data[i].email;
		var temp = data[i].temp;
		
		//var oNewNode=document.createElement('LI');
		var oNewNode = document.createElement('LI');
		
		oNewNode.className = "lineInc";
        oNewNode.id        = id;
        oNewNode.setAttribute("name"    , "userList");
        oNewNode.setAttribute("uid"     , userOid);
        oNewNode.setAttribute("pid"     , peopleOid);
        oNewNode.setAttribute("uname"   , name	);
        oNewNode.setAttribute("dptname" , departmentName);
        oNewNode.setAttribute("duty" 	, duty);
        oNewNode.innerText = temp + "/" + duty + "/" + departmentName;
        $("#"+ulId).append(oNewNode);
	}
}

<%----------------------------------------------------------
*                     선택된 사용자 결재선에 추가시 타입, 중복 검사
----------------------------------------------------------%>
function lfn_addAppType(vArr,appType){
	
	var app = "Report";
	if(appType == 1) {
		app = "Agree";
	}else if(appType == 2) {
		app = "Approver";
	}else if(appType == 3) {
		app = "Report";
	}
	
	var id = vArr[0];
	var uid = vArr[1];
	var pid = vArr[2];
    var name = vArr[3];
    var deptName = vArr[4];
    var duty = vArr[5];
	
    if(lfn_DuplicationCheck(uid,app,name)) {
    	addAppLine(appType, app, uid, deptName, name, id, duty);
    }
}

<%----------------------------------------------------------
*                     선택된 사용자 결재선에 추가시 중복 검사
----------------------------------------------------------%>
function lfn_DuplicationCheck(uid, app, name) {
	if(app == "Agree" || app == "Approver") {
		
		if(uid == $("#ownUser").val()) {
			alert("${f:getMessage('등록자는 추가할 수 없습니다.')}");
			return;
		}
		
		var obj = $("input[name='Agree']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if(obj.eq(i).val() == uid) {
				alert("[${f:getMessage('합의')}] " + name + "${f:getMessage('이(가) 중복됩니다.')}");
				return false;
			}
		}
		var obj = $("input[name='Approver']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if(obj.eq(i).val() == uid) {
				alert("[${f:getMessage('합의')}] " + name + "${f:getMessage('이(가) 중복됩니다.')}");
				return false;
			}
		}
	}else if(app == "Report") {
		var obj = $("input[name='Report']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if(obj.eq(i).val() == uid) {
				alert("[${f:getMessage('수신')}] " + name + "${f:getMessage('이(가) 중복됩니다.')}");
				return false;
			}
		}
	}
	return true;
}

<%----------------------------------------------------------
*                     선택 사용자 결재 선에 추가
----------------------------------------------------------%>
function addAppLine(appType, app, uid, deptName, name, id, duty) {
	
	var count = $("#app"+appType + " > tr").length;
	
	var html = "";
	html += "<tr>";
	html += "	<td class=tdwhiteM>";
	html += "		<input type=checkbox name=deleteChk id=deleteChk class="+appType+"AllCheck value=app"+appType+">";
	html += "		<input type=hidden name=" + app + " value=" + uid + " >";
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += (count+1);
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += app;
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += deptName;
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += name;
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += id
	html += "	</td>";
	html += "	<td class=tdwhiteM>";
	html += duty;
	html += "	</td>";
	html += "</tr>";
	
	$("#app"+appType).append(html);
}

<%----------------------------------------------------------
*                     사용자, 저장된 결재선 더블클릭시 이벤트
----------------------------------------------------------%>
function lfn_onDBClick(){
	if(appTypeUL == "searchUL" || appTypeUL == "orgUL") {
		if($("input:radio[name=appType]:checked").size() == 0 ) {
			alert("${f:getMessage('결재 방법')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		var vArr  = lfn_getSeclcedUser();
		
		var appType = $("input:radio[name=appType]:checked").val();
		lfn_addAppType(vArr,appType);
	}else if(appTypeUL == "lineUL") {
		var selectName = $('ul#lineUL > li.select').attr('title');
		
		if (!confirm("[" + selectName + "]${f:getMessage('을(를) 설정하시겠습니까?')}\n${f:getMessage('기존의 결재선이 지워집니다.')}")) {
			return; 
		}
		
		var loadOid = $('ul#lineUL > li.select').attr('oid');
		
		delAllAppLine();
		loadSavedApprovalLine(loadOid, "app1");
		loadSavedApprovalLine(loadOid, "app2");
		loadSavedApprovalLine(loadOid, "app3");
	}
}

//////////////////////////////////////////////////////////////////////////
//Search
<%----------------------------------------------------------
*                     Search 메뉴 - 사용자 검색
----------------------------------------------------------%>
function lfn_SearchUser() {
	var form = $("form[name=approvalLineForm]").serialize();
	var url	= getURLString("user", "approveUserSearch", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			lfn_addUser(data,"searchUL");
		}
	});
}

//////////////////////////////////////////////////////////////////////////
//Org
<%----------------------------------------------------------
*                     부서 선택시
----------------------------------------------------------%>
function lfn_DeptTreeOnClick(id){
	var name   = deptTree.getAttribute(id, "text");
	var oid    = deptTree.getAttribute(id, "oid");
	var code   = deptTree.getAttribute(id, "code");
	
	if("ROOT" == code ) {
		code = "";
	}
	$("#deptCode").val(code);
	lfn_SearchOrg();
}

<%----------------------------------------------------------
*                     부서 선택시 사용자 검색
----------------------------------------------------------%>
function lfn_SearchOrg() {
	var form = $("form[name=approvalLineForm]").serialize();
	var url	= getURLString("user", "approveUserOrg", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			lfn_addUser(data,"orgUL");
		}
	});
}

//////////////////////////////////////////////////////////////////////////
//App.Line
<%----------------------------------------------------------
*                     저장된 결재선 검색
----------------------------------------------------------%>
function lfn_SearchLine() {
	var form = $("form[name=approvalLineForm]").serialize();
	var url	= getURLString("user", "loadLineAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			lfn_addAppLine(data,"lineUL");
		}
	});
}

<%----------------------------------------------------------
*                     Save, Delete, Add, Information 버튼 
----------------------------------------------------------%>
function windEdit_Enter(arg) {
	if(arg == 1) {
		return false;
	}
	if(arg == 3) {
		createLine();
	}else if(arg == 4) {
		deleteLine();
	}else if(arg == 5) {
		getLine();
	}else if(arg == 6) {
		viewLine();
	}
}

<%----------------------------------------------------------
*                     저장된 결재선 리스트 추가
----------------------------------------------------------%>
function lfn_addAppLine(data,ulId) {
	$("#" + ulId + " li").remove();
	for(var i=0; i<data.length; i++) {
		var title = data[i].title;
		var oid = data[i].oid;
		
		//var oNewNode=document.createElement('LI');
		var oNewNode = document.createElement('LI');
		
		oNewNode.className = "lineInc";
        oNewNode.id        = oid;
        oNewNode.setAttribute("title"     , title);
        oNewNode.setAttribute("oid"     , oid);
        oNewNode.innerText = title;
        $("#"+ulId).append(oNewNode);
	}
}

<%----------------------------------------------------------
*                     결재 선 저장
----------------------------------------------------------%>
function createLine() {
	var lineTitle = inputLine();
	if(lineTitle == null || lineTitle == "" || lineTitle == "cancel") 
		return;
	$("#title").val(lineTitle);
	
	var form = $("form[name=approvalLineForm]").serialize();
	var url	= getURLString("user", "createLineAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			alert(data);
			lfn_SearchLine();
		}
	});
}

<%----------------------------------------------------------
*                     결재 선 이름 추출
----------------------------------------------------------%>
function inputLine() {
	var url = "/Windchill/jsp/workprocess/InputApprovalLine.jsp";
	var attache = window.showModalDialog(url,"InputApprovalLine","help=no; scroll=no; resizable=no; dialogWidth=360px; dialogHeight:200px;");
	return attache;
}

<%----------------------------------------------------------
*                     선택 결재선 삭제
----------------------------------------------------------%>
function deleteLine(){
	if($('ul#lineUL > li.select').length == 0) {
		alert("${f:getMessage('삭제할 결재선을 선택하세요.')}");
		return;
	}
	
	var delOid = $('ul#lineUL > li.select').attr('oid');
	var form = $("form[name=approvalLineForm]").serialize();
	var url	= getURLString("user", "deleteLineAction", "do") + "?delOid=" + delOid;
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 삭제오류')}";
			alert(msg);
		},

		success:function(data){
			alert(data);
			lfn_SearchLine();
		}
	});
}
<%----------------------------------------------------------
*                     선택 결재선 정보 설정
----------------------------------------------------------%>
function getLine() {
	if($('ul#lineUL > li.select').length == 0) {
		alert("${f:getMessage('가져올 결재선을 선택하세요.')}");
		return;
	}
	
	var selectName = $('ul#lineUL > li.select').attr('title');
	
	if (!confirm("[" + selectName + "]${f:getMessage('을(를) 설정하시겠습니까?')}\n${f:getMessage('기존의 결재선이 지워집니다.')}")) {
		return; 
	}
	
	var loadOid = $('ul#lineUL > li.select').attr('oid');
	
	delAllAppLine();
	loadSavedApprovalLine(loadOid, "app1");
	loadSavedApprovalLine(loadOid, "app2");
	loadSavedApprovalLine(loadOid, "app3");
}

<%----------------------------------------------------------
*                     선택 결재 선정보 결재선에 설정
----------------------------------------------------------%>
function loadSavedApprovalLine(oid, type) {
	var form = $("form[name=approvalLineForm]").serialize();
	var url	= getURLString("user", "loadApproverTemplate", "do") + "?oid="+ oid + "&type="+type;
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			var appType = 3;
			var app = "Report";
			
			if(type == "app1") {
				app = "Agree";
				appType = 1;
			}else if(type == "app2") {
				app = "Approver";
				appType = 2;
			}else if(type == "app3") {
				app = "Report";
				appType = 3;
			}
			
			for(var i=0; i<data.length; i++) {
				addAppLine(appType, app, data[i].oid, data[i].deptName, data[i].name, data[i].id, data[i].duty);
			}
			
		}
	});
}
<%----------------------------------------------------------
*                     결재선 상세보기
----------------------------------------------------------%>
function viewLine(){
	if($('ul#lineUL > li.select').length == 0) {
		alert("${f:getMessage('결재선')}${f:getMessage('을(를) 선택하세요.')}");
		return;
	}
	var selectedLine = $('ul#lineUL > li.select').attr('oid');
	var url = getURLString("user", "viewApproverTemplate", "do") + "?oid=" + selectedLine;
	var popWidth = "670";
	var popHeight = "300";
	var userScreenWidth = screen.availWidth / 2 - popWidth / 2;
	var userScreenHeight = screen.availHeight / 2 - popHeight / 2;
	
	var styles = "titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=no,width=670,height=300,left=" + userScreenWidth + ", top=" + userScreenHeight;
	userSearchPop = window.open(url,"",styles);
	userSearchPop.focus();
}

</script>

<style>
ul#searchUL .lineInc.hover{ 
      cursor:pointer;
      background:#dedede;
}
ul#searchUL li.hover{ 
      cursor:pointer;
      background:#dedede;
}
ul#searchUL li.select{ 
      cursor:pointer;
      background:#80ffff;
}
ul#orgUL .lineInc.hover{ 
      cursor:pointer;
      background:#dedede;
}
ul#orgUL li.hover{ 
      cursor:pointer;
      background:#dedede;
}
ul#orgUL li.select{ 
      cursor:pointer;
      background:#80ffff;
}
ul#lineUL .lineInc.hover{ 
      cursor:pointer;
      background:#dedede;
}
ul#lineUL li.hover{ 
      cursor:pointer;
      background:#dedede;
}
ul#lineUL li.select{ 
      cursor:pointer;
      background:#80ffff;
}
</style>

<body>

<form name="approvalLineForm" id="approvalLineForm" method=post >

<input type="hidden"	name="ownUser"		id="ownUser"	value="<c:out value="${ownUser }"/>" >
<input type="hidden"	name="workOid"		id="workOid"	value="<c:out value="${workOid }"/>" >
<input type="hidden"	name="deptCode"		id="deptCode"	value="">

<!-- form에 input type="text"가 하나만 있으면 Enter입력시 submit이 되는걸 방지 -->
<input type="text"	name="AAA"		id="AAA"	value="" style="display: none;">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td align="center">
		<table width="100%" border="0" align="center" cellpadding="0" cellspacing="1" class="tablehead" class=9pt>
			<tr> 
				<td height=30 width=93% align=center><B><font color=white>${f:getMessage('결재선 지정')}</font></B></td>
			</tr>
		</table>
	</td>
</tr>
</table>

<div style="position:relative; padding:10px 20px 20px 20px; margin-bottom:0;">

	<div>
		<table width="100%" border="0" style="padding-right: 5px">
			<colgroup>
				<col width="120">
				<col width="76">
				<col width="62">
				<col width="76">
				<col width="120">
				<col width="*">
			</colgroup>
			<tbody>
				<tr>
					<td></td>
					<td id="searchTD" height="23" valign="middle" background="/Windchill/jsp/portal/images/k_bt_01_ov.gif">
						<div align="center">
							<a id="Search" href="javascript:void(0);">
								<strong><font color="#FFFFFF">${f:getMessage('검색')}</font></strong>
							</a>
						</div>
					</td>
					
					<td id="orgTD" height="23" valign="middle" background="/Windchill/jsp/portal/images/k_bt_02.gif">
						<div align="center">
							<a id="Org" href="javascript:void(0);">
								<strong><font color="#FFFFFF">Org</font></strong>
							</a>
						</div>
					</td>
					
					<td id="lineTD" height="23" valign="middle" background="/Windchill/jsp/portal/images/k_bt_03.gif" >
						<div align="center">
							<a id="Line" href="javascript:void(0);">
								<strong><font color="#FFFFFF">App.Line</font></strong>
							</a>
						</div>
					</td>
					
					<td></td>
					
					<td align="right">
						<button type="button" class="btnCustom" id="createLine" >${f:getMessage('등록')}</button>
						<button type="button" class="btnCustom" id="cancel" onclick="self.close()">${f:getMessage('취소')}</button>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<div id="body" style="padding-top: 10px">
	
		<div id="searchDIV" style="width: 40%; overflow:auto; float:left; height:500px; border:1px solid #a0a0a0; margin-right:10px;">
			<div style="width: 100%; overflow:auto; height: 10%; border-bottom: 1px solid #a0a0a0;"  >
				<div style="padding: 5px 10px 10px 5px ; height: 10% ">
					${f:getMessage('이름 또는 ID 검색')} <br>
					<input type="text" name="userKey" id="userKey" value="" /> 
					<button type="button" name="userSearch" id="userSearch" class="btnSearch" >${f:getMessage('검색')}</button>
				</div>
			</div>
			
			<div style="width: 100%; overflow:auto; height: 89%;"  >
				<ul id="searchUL" name="uls" style="padding-left: 5px; list-style-type: none;">
				</ul>
			</div>
		</div>
	
		<div id="OrgDIV" style="width: 40%; float:left; height:500px; border:1px solid #a0a0a0; margin-right:10px; display: none">
	
			<div style="width: 50%; overflow:auto; float:left; height: 100%; border-right: 1px solid #a0a0a0; ">
				<jsp:include page="/eSolution/department/treeDepartment.do">
					<jsp:param value="ROOT" name="code"/>
				</jsp:include>
			</div>
			
			<div style="width: 49%; overflow:auto; float:left; height: 100%; ">
				<ul id="orgUL" name="uls" style="padding-left: 5px; list-style-type: none;">
				</ul>
			</div>
		
		</div>
		
		<div id="lineDIV" style="width: 40%; overflow:auto; float:left; height:500px; border:1px solid #a0a0a0; margin-right:10px; display: none">
			<div style="width: 100%; overflow:auto; height: 24px; border-bottom: 1px solid #a0a0a0;"  >
				
				<table width="100%" height="100%" border="0" >
					<colgroup>
						<col width="*">
						<col width="100px">
						<col width="100px">
						<col width="100px">
						<col width="100px">
						<col width="*">
					</colgroup>
					
					<tbody>
						<tr>
							<td></td>
							<td align="center" background="/Windchill/jsp/portal/images/k_btg_04_ov.gif">
								<a href="javascript:void(0);" id="lineSaveBtn">
									<font color="#FFFFFF"><strong>Save</strong></font>
								</a>
							</td>
							
							<td align="center" background="/Windchill/jsp/portal/images/k_btg_04_ov.gif">
								<a href="javascript:void(0);" id="lineDelBtn">
									<font color="#FFFFFF"><strong>Delete</strong></font>
								</a>
							</td>
							<td align="center" background="/Windchill/jsp/portal/images/k_btg_04_ov.gif">
								<a href="javascript:void(0);" id="lineAddBtn">
									<font color="#FFFFFF"><strong>Add</strong></font>
								</a>
							</td>
							<td align="center" background="/Windchill/jsp/portal/images/k_btg_04_ov.gif">
								<a href="javascript:void(0);" id="lineInfoBtn">
									<font color="#FFFFFF"><strong>Infomation</strong></font>
								</a>
							</td>
							<td></td>
						</tr>
					</tbody>
				</table>
				
			</div>
				<div>
					<input type="hidden" name="title" id="title" value="">
					<ul id="lineUL" name="uls" style="padding-left: 5px; list-style-type: none;">
					</ul>
				</div>
		</div>
		
		
		<div style="width: 10%; overflow:auto; float:left; height: 500px; border:1px solid #a0a0a0; margin-right:10px; background-color: #B5D1D7">
		
			<div style="width: 90%; height: 220px; margin-left:5px; margin-right:10px; border:1px solid #a0a0a0; margin-top:100px; background-color: white; ">
			
				<table width="100%" border="0" cellpadding="0" cellspacing="1" class=9pt>
					<tr>
						<td>
							<table width="100%" cellspacing="1" cellpadding="0" >
								<tr>
									<td height="23"  class="a_con_01"  bgcolor=#cfcfcf>
										${f:getMessage('결재방법')}
									</td>
								</tr>
								<tr>
									<td height="23"  class="border_text_03-00" id='appType1' >
										<input type="radio" name="appType" value="1">
										<span style='cursor: default;'>
											${f:getMessage('합의')}
										</span>
									</td>
								</tr>
							
								<tr>
									<td height="23" class="border_text_03-00" id='appType2' >
										<input type="radio" name="appType" value="2">
										<span style='cursor: default;'>
											${f:getMessage('결재')}
										</span>
									</td>
								</tr>
							
								<tr>
									<td height="23"class="border_text_03-00" id='appType3' >
										<input type="radio" name="appType" value="3">
										<span style='cursor: default;'>
											${f:getMessage('수신')}
										</span>
									</td>
								</tr>
							</table>
						
						<br> 
						<br>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
			                 	<tr>
			                 		<td height="25" align="center">
										<button type="button" class="btnCustom" id="addBtn" style="width: 80%" >
											${f:getMessage('추가')}
										</button>
									</td>
								</tr>
							
								<tr>
									<td height="25" align="center">
										<button type="button" class="btnCustom" id="delBtn" style="width: 80%" >
											${f:getMessage('삭제')}
										</button>
									</td>
								</tr>
							
								<tr>
									<td height="25" align="center">
										<button type="button" class="btnCustom" id="delAllBtn" style="width: 80%" >
											${f:getMessage('모두삭제')}
										</button>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
			
		</div>
		
		<div style="width: 47%; float:left; height:500px; border:1px solid #a0a0a0;">
		
			<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
				<tr>
 					<td>
 						<table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
   							<tr>
     							<td width="100%" height="20" bgcolor="#efefef" ><div align="center"><strong>${f:getMessage('합의')}</strong></div></TD>
     						</tr>
   						</table>
   							
   						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
			           		<tr>
			             		<td height=1 width=100%></td>
			             	</tr>
     					</table>
     				</td>
				</tr>
			</table>
			
			<div style="overflow: auto; height:140px">
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
              		<tr>
               			<td class="tdblueM">
               				<input type=checkbox class=AllCheck value=1>
               			</TD>
               			<td class="tdblueM">${f:getMessage('순서')}</td>
               			<td class="tdblueM">${f:getMessage('구분')}</td>
               			<td class="tdblueM">${f:getMessage('부서')} </td>
               			<td class="tdblueM">${f:getMessage('이름')}</td>
               			<td class="tdblueM">ID</td>
               			<td class="tdblueM">${f:getMessage('직급')}</td>
              		</tr>
                  	<tbody id="app1"></tbody>
           		</table>
			</div>
			
			<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
				<tr>
 					<td>
 						<table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
   							<tr>
     							<td width="100%" height="20" bgcolor="#efefef" ><div align="center"><strong>${f:getMessage('결재')}</strong></div></TD>
     						</tr>
   						</table>
   							
   						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
			           		<tr>
			             		<td height=1 width=100%></td>
			             	</tr>
     					</table>
     				</td>
				</tr>
			</table>
			
			<div style="overflow: auto;height:140px">
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
              		<tr>
               			<td height="20"  class="tdblueM">
               				<input type=checkbox class=AllCheck value=2>
               			</TD>
               			<td height="25"  class="tdblueM">${f:getMessage('순서')}</td>
               			<td height="25"  class="tdblueM">${f:getMessage('구분')}</td>
               			<td height="25"  class="tdblueM">${f:getMessage('부서')} </td>
               			<td height="25"  class="tdblueM">${f:getMessage('이름')}</td>
               			<td height="25"  class="tdblueM">ID</td>
               			<td height="25"  class="tdblueM">${f:getMessage('직급')}</td>
              		</tr>
                  	<tbody id="app2"></tbody>
           		</table>
			</div>
			
			<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
				<tr>
 					<td>
 						<table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
   							<tr>
     							<td width="100%" height="20" bgcolor="#efefef" ><div align="center"><strong>${f:getMessage('수신')}</strong></div></TD>
     						</tr>
   						</table>
   							
   						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
			           		<tr>
			             		<td height=1 width=100%></td>
			             	</tr>
     					</table>
     				</td>
				</tr>
			</table>
			
			<div style="overflow: auto;height:140px">
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
              		<tr>
               			<td height="20"  class="tdblueM">
               				<input type=checkbox class=AllCheck value=3>
               			</TD>
               			<td height="25"  class="tdblueM">${f:getMessage('순서')}</td>
               			<td height="25"  class="tdblueM">${f:getMessage('구분')}</td>
               			<td height="25"  class="tdblueM">${f:getMessage('부서')} </td>
               			<td height="25"  class="tdblueM">${f:getMessage('이름')}</td>
               			<td height="25"  class="tdblueM">ID</td>
               			<td height="25"  class="tdblueM">${f:getMessage('직급')}</td>
              		</tr>
                  	<tbody id="app3"></tbody>
           		</table>
			</div>
			
		</div>
	
	</div>

</div>

</form>

</body>
