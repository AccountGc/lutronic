<%@page import="com.e3ps.groupware.notice.dto.NoticeDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
NoticeDTO dto = (NoticeDTO) request.getAttribute("dto");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				공지사항 상세보기
			</div>
		</td>
	</tr>
</table>
<table class="view-table">
	<colgroup>
		<col width="130">
		<col width="500">
		<col width="130">
		<col width="500">
	</colgroup>
	<tr>
		<th class="lb">제목</th>
		<td colspan="3" class="indent5"><%=dto.getTitle()%></td>
	</tr>
	<tr>
		<th class="lb">등록자</th>
		<td class="indent5"><%=dto.getCreator()%></td>
		<th>등록일</th>
		<td class="indent5">
			<%=dto.getCreatedDate()%>
		</td>
	</tr>
	<tr>
		<th class="lb">조회수</th>
		<td class="indent5"><%=dto.getCount()%></td>
		<th>팝업</th>
		<td class="indent5"><%=dto.isPopup() == true ? "팝업 O" : "팝업 X"%></td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td colspan="3" class="indent5">
			<div class="textarea-auto">
				<textarea rows="5" readonly="readonly"><%=dto.getContents() != null ? dto.getContents() : ""%></textarea>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="center">
			<%
			if (dto.isModify()) {
			%>
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<%
			}
			if (dto.isDelete()) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<script type="text/javascript">
	const oid = document.getElementById("oid").value;
	function modify() {
		const url = getCallUrl("/notice/modify?oid=" + oid);
		document.location.href = url;
	}

	function _delete() {
		if (!confirm("삭제하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/notice/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		}, "GET");
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		autoTextarea();
	});
</script>