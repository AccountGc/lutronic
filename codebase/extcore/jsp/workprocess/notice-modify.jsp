<%@page import="com.e3ps.groupware.notice.dto.NoticeDTO"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
NoticeDTO data = (NoticeDTO) request.getAttribute("data");
%>
<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				공지사항 수정
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
			<input type="text" name="title" id="title" class="width-500" value="<%=data.getTitle()%>">
		</td>
	</tr>
	<tr>
		<th class="lb">팝업 유무</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="isPopup" value="true" <%if (true==data.isPopup()) {%> checked="checked" <%}%>>
				<div class="state p-success">
					<label>
						<b>팝업 O</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="isPopup" value="false" <%if (false==data.isPopup()) {%> checked="checked" <%}%>>
				<div class="state p-success">
					<label>
						<b>팝업 X</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td class="indent5">
			<textarea name="contents" id="contents" rows="10"><%=data.getContents()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
				<jsp:param value="<%=data.getOid()%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<input type="button" value="이전" title="이전" onclick="history.go(-1);">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<script type="text/javascript">
	function modify() {
		const oid = document.getElementById("oid").value;
		const title = document.getElementById("title");
		const contents = document.getElementById("contents").value;
		const secondarys = toArray("secondarys");

		if (title.value === "") {
			alert("제목을 입력하세요.");
			title.focus();
			return false;
		}

		const params = {
			oid : oid,
			title : title.value,
			contents : contents,
			secondarys : secondarys
		}
		
		const isPopup = document.querySelector("input[name=isPopup]:checked").value;
		params.isPopup = JSON.parse(isPopup);
		
		if (!confirm("수정하시겠습니까?")) {
			return false;
		}

		const url = getCallUrl("/notice/modify");
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}
</script>