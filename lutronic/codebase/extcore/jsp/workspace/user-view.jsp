<%@page import="java.util.HashMap"%>
<%@page import="com.e3ps.org.dto.PeopleDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
PeopleDTO dto = (PeopleDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
HashMap<String, String> hash = dto.getSignature();
%>

<input type="hidden" name="oid" id="oid" value="<%=dto.getPoid()%>">
<input type="hidden" name="woid" id="woid" value="<%=dto.getWoid()%>">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				사용자 정보 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="150">
		<col width="*">
		<col width="300">
	</colgroup>
	<tr>
		<th class="lb">이름</th>
		<td class="indent5"><%=dto.getName()%></td>
		<td rowspan="8">이름</td>
	</tr>
	<tr>
		<th class="lb">아이디</th>
		<td class="indent5"><%=dto.getId()%></td>
	</tr>
	<tr>
		<th class="lb">권한</th>
		<td class="indent5"><%=dto.getAuth()%></td>
	</tr>
	<tr>
		<th class="lb">부서</th>
		<td class="indent5"><%=dto.getDepartment_name()%></td>
	</tr>
	<tr>
		<th class="lb">직위</th>
		<td class="indent5"><%=dto.getDuty()%></td>
	</tr>
	<tr>
		<th class="lb">이메일</th>
		<td class="indent5"><%=dto.getEmail()%></td>
	</tr>
	<tr>
		<th class="lb">퇴사여부</th>
		<td class="indent5"><%=dto.isFire() == false ? "재직" : "퇴사"%></td>
	</tr>
	<tr>
		<th class="lb">서명</th>
		<td class="indent5">
			<jsp:include page="/extcore/jsp/common/primary-view.jsp">
				<jsp:param value="<%=dto.getPoid()%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>

<script type="text/javascript">
	function modify() {
		const oid = document.getElementById("woid").value;
		document.location.href = getCallUrl("/org/modify?oid=" + oid);
	}
</script>
