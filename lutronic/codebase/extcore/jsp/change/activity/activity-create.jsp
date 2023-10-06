<%@page import="java.util.Iterator"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.common.code.dto.NumberCodeDTO"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
ArrayList<NumberCode> list = (ArrayList<NumberCode>) request.getAttribute("list");
Map<String, String> actMap = (Map<String, String>) request.getAttribute("actMap");
%>
<form>
	<input type="hidden" name="oid" id="oid" value="<%=oid%>">

	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png">
					설계변경 활동 등록
				</div>
			</td>
		</tr>
	</table>
	<table class="create-table">
		<colgroup>
			<col width="150">
			<col width="*">
		</colgroup>
		<tr>
			<th class="lb req">이름</th>
			<td class="indent5">
				<input type="text" name="name" id="name" class="width-200">
			</td>
		</tr>
		<!-- 		<tr> -->
		<!-- 			<th class="lb">영문명</th> -->
		<!-- 			<td class="indent5"> -->
		<!-- 				<input type="text" name="name_eng" id="name_eng" class="width-200"> -->
		<!-- 			</td> -->
		<!-- 		</tr> -->
		<tr>
			<th class="lb req">STEP</th>
			<td class="indent5">
				<select name="step" id="step" class="width-200">
					<option value="">선택</option>
					<%
					for (NumberCode n : list) {
					%>
					<option value="<%=n.getCode()%>"><%=n.getName()%></option>
					<%
					}
					%>
				</select>
			</td>
		</tr>
		<tr>
			<th class="lb req">활동구분</th>
			<td class="indent5">
				<select name="activeType" id="activeType" class="width-200">
					<option value="">선택</option>
					<%
					Iterator it = actMap.keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						String value = actMap.get(key);
					%>
					<option value="<%=key%>"><%=value%></option>
					<%
					}
					%>
				</select>
			</td>
		</tr>
		<!-- 		<tr> -->
		<!-- 			<th class="lb">담당자</th> -->
		<!-- 			<td class="indent5"> -->
		<!-- 				<input type="text" name="activeUserName" id="activeUserName" data-multi="false" class="width-200"> -->
		<!-- 				<input type="hidden" name="activeUser" id="activeUser"> -->
		<!-- 				<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')"> -->
		<!-- 			</td> -->
		<!-- 		</tr> -->
		<tr>
			<th class="lb">정렬</th>
			<td class="indent5">
				<input type="text" name="sort" id="sort" class="width-200" maxlength="3">
			</td>
		</tr>
		<tr>
			<th class="lb">설명</th>
			<td class="indent5">
				<textarea name="description" id="description" rows="13"></textarea>
			</td>
		</tr>
	</table>

	<table class="button-table">
		<tr>
			<td class="center">
				<input type="button" value="등록" title="등록" class="blue" onclick="create();">
				<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();">
			</td>
		</tr>
	</table>

	<script type="text/javascript">
		function create() {
			const oid = document.getElementById("oid").value;
			const name = document.getElementById("name");
			const sort = document.getElementById("sort");
			const step = document.getElementById("step");
			const activeType = document.getElementById("activeType");
			const description = document.getElementById("description").value;

			if (name.value === "") {
				alert("이름을 입력하세요.");
				name.focus();
				return false;
			}
			
			if (step.value === "") {
				alert("STE{을 선택하세요.");
				return false;
			}
				
			if (activeType.value === "") {
				alert("활동구분을 선택하세요.");
				return false;
			}
			
			if (sort.value === "") {
				alert("정렬을 입력하세요.");
				sort.focus();
				return false;
			}

			if (!confirm("등록 하시겠습니까?")) {
				return;
			}

			const params = {
				name : name.value,
				oid : oid,
				step : step.value,
				activeType : activeType.value,
				sort : sort.value,
				description : description,
				type : "act"
			}
			openLayer();
			const url = getCallUrl("/activity/create");
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					opener.loadGridData();
					self.close();
				} else {
					closeLayer();
				}
			});
		}

		document.addEventListener("DOMContentLoaded", function() {
			toFocus("name");
			selectbox("step");
			selectbox("activeType");
		});
	</script>
</form>
