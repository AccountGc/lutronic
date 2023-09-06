<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
<form>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="lastNum" id="lastNum">
	<input type="hidden" name="curPage" id="curPage">
	<input type="hidden" name="oid" id="oid">
	
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> 설계변경 활동 ROOT 등록
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
			<th class="lb">영문명 <span class="red">*</span></th>
			<td class="indent5">
				<input type="text" name="name_eng" id="name_eng" class="width-200">
			</td>
		</tr>
		<tr>
			<th class="lb">sort <span class="red">*</span></th>
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
			
			if(isEmpty($("#name_eng").val())) {
				alert("영문명을 입력하세요.");
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
			const field = ["name","name_eng","sortNumber","description"];
			params = toField(params, field);
			
			const url = getCallUrl("/admin/createRootDefinition");
			call(url, params, function(data) {
				if(data.result){
					alert(data.msg);
					opener.loadGridData();
					self.close();
				}else{
					alert(data.msg);
				}
			});
		})

		document.addEventListener("DOMContentLoaded", function() {
		});

		document.addEventListener("click", function(event) {
			hideContextMenu();
		})

	</script>
</form>
