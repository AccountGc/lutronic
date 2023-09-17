<%@page import="java.util.Map"%>
<%@page import="com.e3ps.common.code.dto.NumberCodeDTO"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
String oid = request.getParameter("oid");
List<NumberCodeDTO> stepList = (List<NumberCodeDTO>) request.getAttribute("stepList");
List<Map<String, String>> typeList = (List<Map<String, String>>) request.getAttribute("typeList");
%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
<form>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="lastNum" id="lastNum">
	<input type="hidden" name="curPage" id="curPage">
	<input type="hidden" name="oid" id="oid" value="<%=oid%>">
	
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> 설계변경 활동 등록
				</div>
			</td>
		</tr>
	</table>
	<table class="search-table">
		<colgroup>
			<col width="174">
			<col width="*">
		</colgroup>
		<tr>
			<th class="lb">Name <span class="red">*</span></th>
			<td class="indent5">
				<input type="text" name="name" id="name" class="width-200">
			</td>
		</tr>
		<tr>
			<th class="lb">영문명</th>
			<td class="indent5">
				<input type="text" name="name_eng" id="name_eng" class="width-200">
			</td>
		</tr>
		<tr>
			<th class="lb">Step <span class="red">*</span></th>
			<td class="indent5">
				<select name="eoStep" id="eoStep" class="width-200">
					<option value="">선택</option>
					<%
					if(stepList.size()>0){
									for (NumberCodeDTO data : stepList) {
					%>
						<option value="<%=data.getCode()%>"><%=data.getName()%></option>
						<%
						}
					}
					%>
				</select>
			</td>
		</tr>
		<tr>
			<th class="lb">활동구분 <span class="red">*</span></th>
			<td class="indent5">
				<select name="activeType" id="activeType" class="width-200">
					<option value="">선택</option>
					<%
					if(typeList.size()>0){
						for (Map<String, String> map : typeList) {
						%>
						<option value="<%=map.get("code")%>"><%=map.get("name")%></option>
						<%
						}
					}
					%>
				</select>
			</td>
		</tr>
		<tr>
			<th class="lb">담당자 <span class="red">*</span></th>
			<td class="indent5">
				<input type="text" name="activeUserName" id="activeUserName" data-multi="false" class="width-200">
				<input type="hidden" name="activeUser" id="activeUser">
				<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
			</td>
		</tr>
		<tr>
			<th class="lb">
				sort <span class="red">*</span>
				<br><strong><span class="red">숫자만 입력가능</span></strong>
			</th>
			<td class="indent5">
				<input type="text" name="sortNumber" id="sortNumber" class="width-200" onKeyup="this.value=this.value.replace(/[^0-9]/g,'');">
			</td>
		</tr>
		<tr>
			<th class="lb">설명</th>
			<td class="indent5">
				<textarea name="description" id="description" cols="80" rows="10" style="width:90%" onKeyUp="common_CheckStrLength(this, 4000)" onChange="common_CheckStrLength(this, 4000)"></textarea>
			</td>
		</tr>
	</table>
			
	<table class="button-table">
		<tr>
			<td class="center">
				<input type="button" value="등록" title="등록" id="createBtn" class="blue">
				<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();">
			</td>
		</tr>
	</table>

	<script type="text/javascript">
		$("#createBtn").click(function() {
			if(isEmpty($("#name").val())) {
				alert("Name을 입력하세요.");
				return;
			}
			
			if(isEmpty($("#eoStep").val())) {
				alert("eoStep을 선택하세요.");
				return;
			}
			
			if(isEmpty($("#activeType").val())) {
				alert("activeType을 선택하세요.");
				return;
			}
			
			if(isEmpty($("#sortNumber").val())) {
				alert("sort를 입력하세요.");
				return;
			}
			
			if (!confirm("등록 하시겠습니까?")) {
				return;
			}
			
			let params = new Object();
			const field = ["oid","name","name_eng","eoStep","activeType","sortNumber","activeUser","description"];
			params = toField(params, field);
			
			const url = getCallUrl("/admin/createActivityDefinition");
			call(url, params, function(data) {
				if(data.result){
					alert("등록 되었습니다.");
					opener.loadGridData();
					self.close();
				}else{
					alert(data.msg);
				}
			});
		})

		document.addEventListener("DOMContentLoaded", function() {
			selectbox("eoStep");
			selectbox("activeType");
			finderUser("activeUserName");
		});

		document.addEventListener("click", function(event) {
			hideContextMenu();
		})

	</script>
</form>
