<%@page import="com.e3ps.groupware.notice.dto.NoticeDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
NoticeDTO dto = (NoticeDTO) request.getAttribute("dto");
%>

<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				공지사항
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
		<td class="indent5">
			<%=dto.isPopup() == true ? "팝업 O" : "팝업 X"%>
		</td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td colspan="3" class="indent5">
			<div class="textarea-auto">
				<textarea rows="10" readonly="readonly"><%=dto.getContents()%></textarea>
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
		<td align="left">
			<div class="pretty p-switch">
				<input type="checkbox" name="checkId" id="checkId" onclick="setCookie();">
				<div class="state p-success">
					<label>
						<b>일주일 간 열지 않기</b>
					</label>
				</div>
			</div>
		</td>
		<td align="right">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<script>
	function setCookie() {
		const oid = document.getElementById("oid").value;
		const cDay = 7;
		let expire = new Date();

		expire.setDate(expire.getDate() + cDay);
		cookies = oid + '=' + escape(oid) + '; path=/ ';

		if (typeof cDay != 'undefined') {
			cookies += ';expires=' + expire.toGMTString() + ';';
		}
		document.cookie = cookies;
		self.close();
	}
	document.addEventListener("DOMContentLoaded", function() {
		autoTextarea();
	})
</script>