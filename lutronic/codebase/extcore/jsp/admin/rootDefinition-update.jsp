<%@page import="com.e3ps.change.beans.ROOTData"%>
<%@page import="com.e3ps.groupware.notice.dto.NoticeDTO"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ROOTData data = (ROOTData) request.getAttribute("rootdata");
%>
<form>
	<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
	
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> 설계변경 활동 ROOT 수정
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
				<input type="text" name="name" id="name" class="width-200" value="<%=data.getName()%>">
			</td>
		</tr>
		<tr>
			<th class="lb">영문명 <span class="red">*</span></th>
			<td class="indent5">
				<input type="text" name="name_eng" id="name_eng" class="width-200" value="<%=data.getName_eng()%>">
			</td>
		</tr>
		<tr>
			<th class="lb">sort <span class="red">*</span></th>
			<td class="indent5">
				<input type="text" name="sortNumber" id="sortNumber" class="width-200" value="<%=data.getSortNumber()%>" onKeyup="this.value=this.value.replace(/[^0-9]/g,'');">
			</td>
		</tr>
		<tr>
			<th class="lb">설명</th>
			<td class="indent5">
				<textarea name="description" id="description" cols="80" rows="10" style="width:90%" onKeyUp="common_CheckStrLength(this, 2000)" onChange="common_CheckStrLength(this, 2000)"><%=data.getDescription() %></textarea>
			</td>
		</tr>
	</table>
			
	<table class="button-table">
		<tr>
			<td class="center">
				<input type="button" value="수정" title="수정" class="blue" id="updateBtn">
				<input type="button" value="닫기" name="닫기" class="gray" onclick="javascript:self.close();">
			</td>
		</tr>
	</table>

	<script type="text/javascript">
		$("#updateBtn").click(function() {
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
			
			if (!confirm("수정 하시겠습니까?")) {
				return;
			}
			
			let params = new Object();
			const field = ["name","name_eng","sortNumber","description","oid"];
			params = toField(params, field);
			
			const url = getCallUrl("/admin/updateRootDefinition");
			call(url, params, function(data) {
				if(data.result){
					alert(data.msg);
					opener.location.reload();
					self.close();
				}else{
					alert(data.msg);
				}
			});
		})

		document.addEventListener("keydown", function(event) {
			const keyCode = event.keyCode || event.which;
			if (keyCode === 13) {
				loadGridData();
			}
		})

		document.addEventListener("click", function(event) {
			hideContextMenu();
		})

	</script>
</form>
