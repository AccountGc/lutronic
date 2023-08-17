<%@page import="com.e3ps.org.beans.PeopleData"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.org.beans.UserData"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
PeopleData dto = (PeopleData) request.getAttribute("dto");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>
	<form id="form">
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		
		<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				사용자 정보 상세
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" class="blue" id="update">
			<input type="button" value="닫기" title="닫기" class="gray" id="closeBtn" onclick="self.close();">
		</td>
	</tr>
</table>
		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>아이디</th>
				<td class="indent5" colspan="3">
					<%=dto.getId()%>
				</td>
			</tr>
			<tr>
				<th>이름</th>
				<td class="indent5" colspan="3">
					<%=dto.getName() %>
				</td>
			</tr>
			<tr>
				<th>부서</th>
				<td class="indent5">
					<%=dto.getDepartmentName()%>
				</td>
				<th>직책</th>
				<td class="indent5">
					<%=dto.getDuty() %>
				</td>
			</tr>
			<tr>
				<th>휴대폰번호</th>
				<td class="indent5" colspan="3">
					<%=dto.getCellTel()%>
				</td>
			</tr>
			<tr>
				<th>이메일</th>
				<td class="indent5" colspan="3">
					<%=StringUtil.checkNull(dto.getEmail())%>
				</td>
			</tr>
			<tr>
				<th>서명</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/signFile-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
		
		<script type="text/javascript">
			//사인 이미지 미리보기
			function signView(image) {
			  	if (image.files && image.files[0]) {
			    	var reader = new FileReader();
			    	reader.onload = function(e) {
			      		document.getElementById('preview').src = e.target.result;
			    	};
			    	reader.readAsDataURL(image.files[0]);
			  	} else {
			    	document.getElementById('preview').src = "";
			  	}
			}
			
			//변경페이지 이동
			$("#update").click(function(){
				const oid = document.getElementById("oid").value;
				const url = getCallUrl("/groupware/userInfoEdit?oid=" + oid);
				document.location.href = url;
			});
		
			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			document.addEventListener("click", function(event) {
				hideContextMenu();
			})

		</script>
	</form>
</body>
</html>