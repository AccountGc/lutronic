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
		<input type="hidden" name="woid" id="woid" value="<%=dto.getWoid()%>">
		
		<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				사용자 정보 변경
			</div>
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
				<th>변경 비밀번호</th>
				<td class="indent5">
					<input type="text" name="pw" id="pw" class="width-300">
				</td>
				<th>변경 비밀번호 확인</th>
				<td class="indent5">
					<input type="text" name="pw2" id="pw2" class="width-300">
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
					<input type="text" name="cellTel" id="cellTel" class="width-300" value="<%=dto.getCellTel()%>">
				</td>
			</tr>
			<tr>
				<th>이메일</th>
				<td class="indent5" colspan="3">
					<input type="text" name="email" id="email" class="width-300" value="<%=StringUtil.checkNull(dto.getEmail())%>">
				</td>
			</tr>
			<tr>
				<th>서명</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
						<jsp:param value="<%=dto.getOid() %>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="변경" title="변경" class="blue" id="update">
					<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
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
		
			//변경하기
			$("#update").click(function(){
				var telExp = /^(?:(010-\d{4})|(01[1|6|7|8|9]-\d{3,4}))-(\d{4})$/;
				var emailExp = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
				if(isEmpty($("#cellTel").val())) {
					alert("휴대폰번호를 입력하세요.");
					return;
				}
				
				if(!telExp.test($("#cellTel").val())){
				    alert("휴대폰번호가 올바르지 않습니다.");
				    return;
			  	}
				
				if(isEmpty($("#email").val())) {
					alert("이메일을 입력하세요.");
					return;
				}
			  	
			  	if(!emailExp.test($("#email").val())){
				    alert("이메일 형식에 맞지 않습니다.");
				    return;
			  	}
				
				var params = _data($("#form"));
				params.primarys = toArray("primarys");
				
				if(params.primarys.length>1){
					alert("서명은 1개만 등록할 수 있습니다.");
					return;
				}
				debugger;
				
				if (!confirm("변경 하시겠습니까?")) {
					return;
				}
				
				var url = getCallUrl("/groupware/userInfoEdit");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						opener.loadGridData();
						self.close();
					}else{
						alert(data.msg);
					}
				});
// 				var inputFile = $("input[name='sign']");
// 				var file = inputFile[0].files[0];
// 				params.file=file;
				
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

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>