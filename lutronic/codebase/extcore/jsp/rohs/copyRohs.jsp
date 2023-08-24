<%@page import="com.e3ps.rohs.beans.RohsData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
String oid = (String) request.getAttribute("oid");
RohsData data = (RohsData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>

<form name="copyRohsForm" id="copyRohsForm" method="post" >

<input type="hidden" name="oid" id="oid" value="<%= oid %>" />

<input type="hidden" name="docType"			id="docType"				value="$$ROHS"/>
<input type="hidden" name="location"		id="location"				value="/Default/ROHS" />
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> RoHS 복사
			</div>
		</td>
		<td class="right">
			<input type="button" value="복사" title="복사" class="" id="copyRohs">
			<input type="button" value="닫기" title="닫기" class="gray" id="closeBtn" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="search-table">
	<colgroup>
		<col width="150">
		<col width="*">
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th>물질 번호<button id="NumberCheck" class="btnSearch" type="button">번호 중복</button></th>
		<td class="indent5"><input type="text" name="rohsNumber" id="rohsNumber" class="width-200"></td>
		<th class="req lb">결재방식</th>
		<td>
			<div class="pretty p-switch">
				<input type="radio"name="lifecycle" value="LC_Default" checked="checked">
				<div class="state p-success">
					<label> <b>기본결재</b>
					</label>
				</div>
			</div> &nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="lifecycle" value="LC_Default_NonWF">
				<div class="state p-success">
					<label> <b>일괄결재</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<th class="req lb">물질명<button id="NumberCheck" class="btnSearch" type="button">물질명 중복</button></th>
		<td class="indent5">
			<input type="text" name="rohsName" id="rohsName" class="width-200" value="<%= data.getName() %>">
		</td>
		<th class="req lb">협력업체</th>
		<td class="indent5">
			<select name="manufacture" id="manufacture" class="width-200">
					<option value="">선택</option>
					<option value="MF003">MF003</option>
			</select>
		</td>
	</tr>
</table>
<script type="text/javascript">

$(document).ready(function() {
	numberCodeList('manufacture', '');
})

$(function() {
	<%----------------------------------------------------------
	*                      등록 버튼
	----------------------------------------------------------%>
	$('#copyRohs').click(function() {
		if($.trim($("#rohsName").val()) == '') {
			alert("물질명을(를) 입력하세요.");
			$("#rohsName").focus();
			return;
		}
		
		if($("input[name='lifecycle']:checked").length == 0 ){
			alert("결재방식을(를) 선택하세요.");
			return;
		}
		
		if($("#manufacture").val() == '') {
			alert("협력업체을(를) 선택하세요.");
			$("#manufacture").focus();
			return;
		}
		if(confirm("복사하시겠습니까?")){
			var form = $("form[name=copyRohsForm]").serialize();
			var url	= getURLString("rohs", "copyRohsAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data: form,
				dataType:"json",
				async: true,
				cache: false,

				success:function(data){
					if(data.result) {
						alert('복사완료되었습니다.');
					}else {
						alert(data.message);
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
	})
	
	$('#dupName').click(function() {
		var rohsName = $('#rohsName').val();
		var url	= getURLString("rohs", "duplicateName", "do");
		$.ajax({
			type:"POST",
			url: url,
			data: {
				rohsName : rohsName
			},
			dataType:"json",
			async: true,
			cache: false,

			success:function(data){
				alert(data.message);
			}
		});
	})
	
	<%----------------------------------------------------------
	*                      번호 중복
	----------------------------------------------------------%>
	$("#NumberCheck").click(function() {
		var url = getURLString("rohs", "checkList", "do") + "?rohsNumber="+$("#rohsNumber").val();
		openOtherName(url,"numberCheck","900","450","status=no,scrollbars=yes,resizable=yes");
	})
	
	<%----------------------------------------------------------
	*                      물질명 중복
	----------------------------------------------------------%>
	$("#NameCheck").click(function() {
		var url = getURLString("rohs", "checkList", "do") + "?rohsName="+$("#rohsName").val();
		openOtherName(url,"checkList","900","450","status=no,scrollbars=yes,resizable=yes");
	})
})

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode1) {
	var type = id.toUpperCase();
	var data = common_numberCodeList(type, parentCode1, false);
	
	addSelectList(id, eval(data.responseText));
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id,data){
	
	$("#" + id + " option").remove();
	$("#" + id).append("<option value='' title='' >선택</option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "'>";
			html += " [" + data[i].code + "] ";
			html += data[i].name + "</option>";
			$("#" + id).append(html);
		}
	}
}

</script>
</form>
</body>
</html>