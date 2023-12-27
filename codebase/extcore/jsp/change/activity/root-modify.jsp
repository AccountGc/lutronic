<%@page import="com.e3ps.change.activity.dto.DefDTO"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
DefDTO dto = (DefDTO) request.getAttribute("dto");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				설계변경 활동 루트 수정
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
		<th class="req b">이름</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-200" value="<%=dto.getName()%>">
		</td>
	</tr>
	<!-- 	<tr> -->
	<!-- 		<th class="lb">영문명</th> -->
	<!-- 		<td class="indent5"> -->
	<!-- 			<input type="text" name="name_eng" id="name_eng" class="width-200"> -->
	<!-- 		</td> -->
	<!-- 	</tr> -->
	<tr>
		<th class="lb req">정렬</th>
		<td class="indent5">
			<input type="text" name="sort" id="sort" class="width-200" maxlength="2" value="<%=dto.getSort()%>">
		</td>
	</tr>
	<tr>
		<th class="lb">설명</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="15"><%=dto.getDescription()%></textarea>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();">
		</td>
	</tr>
</table>

<script type="text/javascript">
	function modify() {
		const name = document.getElementById("name");
		const sort = document.getElementById("sort");
		const oid = document.getElementById("oid").value;
		if (name.value === "") {
			alert("이름을 입력하세요.");
			name.focus();
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

		const description = toId("description");
		const params = {
			oid : oid,
			name : name.value,
			sort : sort.value,
			description : description,
		}
		logger(params);
		openLayer();
		const url = getCallUrl("/activity/modify");
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.location.reload();
				self.close();
			} else {
				closeLayer();
			}
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("name");
	})
</script>