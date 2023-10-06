<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				설계변경 활동 루트 등록
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
			<input type="text" name="name" id="name" class="width-200">
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
			<input type="text" name="sort" id="sort" class="width-200" maxlength="2">
		</td>
	</tr>
	<tr>
		<th class="lb">설명</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="15"></textarea>
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
		const name = document.getElementById("name");
		const sort = document.getElementById("sort");

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
			name : name.value,
			sort : sort.value,
			description : description,
			type : "root"
		}

		openLayer();
		const url = getCallUrl("/activity/create");
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