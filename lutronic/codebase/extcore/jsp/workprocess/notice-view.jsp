<%@page import="com.e3ps.groupware.notice.beans.NoticeData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
NoticeData dto = (NoticeData) request.getAttribute("dto");
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
		<td class="right">
			<%
			if(dto.isAuth()){
			%>
				<input type="button" value="수정" title="수정" class="blue" id="update">
				<input type="button" value="삭제" title="삭제" class="red" id="delete">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="gray" id="closeBtn" onclick="self.close();">
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
		<th>제목</th>
		<td colspan="3"><%=dto.getTitle()%></td>
	</tr>
	<tr>
		<th>등록자</th>
		<td><%=dto.getCreator()%></td>
		<th>등록일</th>
		<td><%=dto.getCreateDate()%></td>
	</tr>
	<tr>
		<th>조회수</th>
		<td><%=dto.getCount()%></td>
		<th>팝업</th>
		<td><%=dto.isPopup()%></td>
	</tr>
	<tr>
		<th>내용</th>
		<td colspan="3"><%=dto.getContents()%></td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td colspan="3"></td>
	</tr>
</table>

<script type="text/javascript">
	//수정
	$("#update").click(function () {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/groupware/updateNotice?oid=" + oid);
		document.location.href = url;
	})
	
	//삭제
	$("#delete").click(function () {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		let params = new Object();
		const oid = document.getElementById("oid").value;
		params.oid = oid;
		const url = getCallUrl("/groupware/deleteNotice");
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
		 				window.opener.loadGridData();
						self.close();
			}
		});
	})
			
	document.addEventListener("DOMContentLoaded", function() {
	});
			

	window.addEventListener("resize", function() {
	});
</script>
