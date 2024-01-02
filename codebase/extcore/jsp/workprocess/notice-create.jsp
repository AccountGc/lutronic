<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				공지사항 등록
			</div>
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="174">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb req">제목</th>
		<td class="indent5">
			<input type="text" name="title" id="title" class="width-800">
		</td>
	</tr>
	<tr>
		<th class="lb">팝업 유무</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="isPopup" value="true" checked="checked">
				<div class="state p-success">
					<label for="T">
						<b>팝업 O</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="isPopup" value="false">
				<div class="state p-success">
					<label for="F">
						<b>팝업 X</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td class="indent5">
			<div class="textarea-auto">
				<textarea name="contents" id="contents" rows="5"></textarea>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5">
			<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
				<jsp:param value="" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="등록" title="등록" class="blue" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<script type="text/javascript">
	function create() {
		const title = document.getElementById("title");
		const contents = document.getElementById("contents");
		const secondarys = toArray("secondarys");

		if (title.value === "") {
			alert("제목을 입력하세요.");
			title.focus();
			return false;
		}

		const params = {
			title : title.value,
			contents : contents.value,
			secondarys : secondarys
		}
		const isPopup = document.querySelector("input[name=isPopup]:checked").value;
		params.isPopup = JSON.parse(isPopup);

		if (!confirm("등록하시겠습니까?")) {
			return false;
		}

		const url = getCallUrl("/notice/create");
		logger(params);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			}
			closeLayer();
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("title");
		autoTextarea();
	});
</script>