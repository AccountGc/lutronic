<%@page import="com.e3ps.groupware.notice.dto.NoticeDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
NoticeDTO data = (NoticeDTO) request.getAttribute("data");
%>
<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
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
		<col width="10%">
		<col width="40%">
		<col width="10%">
		<col width="40%">
	</colgroup>
	<tr>
		<th class="lb">제목</th>
		<td colspan="3" class="indent5"><%=data.getTitle()%></td>
	</tr>
	<tr>
		<th class="lb">등록자</th>
		<td class="indent5"><%=data.getCreator()%></td>
		<th>등록일</th>
		<td class="indent5">
			<%=data.getCreatedDate()%>
		</td>
	</tr>
	<tr>
		<th class="lb">조회수</th>
		<td class="indent5"><%=data.getCount()%></td>
		<th>팝업</th>
		<td class="indent5">
			<%=data.isPopup()%>
		</td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td colspan="3" class="indent5">
			<textarea rows="10" readonly="readonly"><%=data.getContents()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
				<jsp:param value="<%=data.getOid()%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="center">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<script type="text/javascript">
	function modify() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/notice/modify?oid=" + oid);
		document.location.href = url;
	}

	function _delete() {
		if (!confirm("삭제하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
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
	});

	window.addEventListener("resize", function() {
	});
</script>
