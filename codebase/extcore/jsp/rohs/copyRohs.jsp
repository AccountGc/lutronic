<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.rohs.dto.RohsData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
String oid = (String) request.getParameter("oid");
RohsData data = (RohsData) request.getAttribute("data");
ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
%>

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
			<input type="button" value="복사" title="복사" class="blue" onclick="copyRohs();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="search-table">
	<colgroup>
		<col width="174">
		<col width="*">
		<col width="174">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">물질번호</th>
		<td class="indent5">
			<input type="text" name="rohsNumber" id="rohsNumber" class="width-200">
			&nbsp;<input type="button" value="번호 중복" title="번호 중복" onclick="NumberCheck();">
		</td>
		<th class="req lb">결재방식</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="lifecycle" value="LC_Default">
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
		<th class="req lb">물질명</th>
		<td class="indent5">
			<input type="text" name="rohsName" id="rohsName" class="width-200" value="<%=data.getName()%>">
			&nbsp;<input type="button" value="물질명 중복" title="물질명 중복" onclick="NameCheck();">
		</td>
		<th class="req lb">협력업체</th>
		<td class="indent5">
			<select name="manufacture" id="manufacture" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode manufacture : manufactureList) {
				%>
				<option value="<%=manufacture.getCode() %>"><%=manufacture.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function copyRohs(){
		if(isEmpty($("#rohsName").val())) {
			alert("물질명을 입력하세요.");
			return;
		}
		
		if($("input[name='lifecycle']:checked").length == 0 ){
			alert("결재방식을(를) 선택하세요.");
			return;
		}
		
		if(isEmpty($("#manufacture").val())) {
			alert("협력업체를 선택하세요.");
			return;
		}
		
		if (!confirm("복사 하시겠습니까?")) {
			return;
		}
		
		let params = new Object();
		params.rohsNumber = $("#rohsNumber").val();
		params.lifecycle = $('input[name=lifecycle]:checked').val();
		params.rohsName = $("#rohsName").val();
		params.manufacture = $("#manufacture").val();
		params.oid = $("#oid").val();
		params.docType = $("#docType").val();
		params.location = $("#location").val();
		var url = getCallUrl("/rohs/copyRohs");
		openLayer();
		call(url, params, function(data) {
			if(data.result){
				alert("복사가 완료되었습니다.");
				self.close();
			}else{
				alert(data.msg);
				closeLayer();
			}
		});
	}
	
	// 번호 중복체크
	function NumberCheck(){
		var params = new Object();
		if(isEmpty($("#rohsNumber").val())){
			alert("입력된 물질번호가 없습니다.");
			return;
		}
		params.rohsName = $("#rohsNumber").val();
		var url = getCallUrl("/rohs/rohsCheck");
		call(url, params, function(data) {
			if(data.result){
				if(data.count==0){
					alert("등록 가능한 물질번호 입니다.");
				}else{
					alert("이미 등록된 물질번호 입니다.");
					$("#rohsNumber").val("");
				}
			}else{
				alert(data.msg);
			}
		});
	}
	
	// 물질명 중복체크
	function NameCheck(){
		var params = new Object();
		if(isEmpty($("#rohsName").val())){
			alert("입력된 물질명이 없습니다.");
			return;
		}
		params.rohsName = $("#rohsName").val();
		var url = getCallUrl("/rohs/rohsCheck");
		call(url, params, function(data) {
			if(data.result){
				if(data.count==0){
					alert("등록 가능한 물질명입니다.");
				}else{
					alert("이미 등록된 물질명입니다.");
					$("#rohsName").val("");
				}
			}else{
				alert(data.msg);
			}
		});
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		selectbox("manufacture");
	});

</script>
