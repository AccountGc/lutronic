<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	<form>

		<!-- 패스워드 변경 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						비밀번호 변경
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th>변경 비밀번호</th>
				<td class="indent5">
					<input type="password" name="password" id="password" class="width-200">
				</td>
			</tr>
			<tr>
				<th>변경 비밀번호 확인</th>
				<td class="indent5">
					<input type="password" name="repassword" id="repassword" class="width-200">
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="변경" title="변경" class="blue" onclick="update();">
					<input type="button" value="뒤로" title="뒤로" onclick="history.go(-1);">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				document.getElementById("password").focus();
			})

			function update() {
				const password = document.getElementById("password");
				const repassword = document.getElementById("repassword");

				if (password.value === "") {
					alert("변경 비밀번호를 입력하세요.");
					password.focus();
					return false;
				}

				if (repassword.value === "") {
					alert("변경 비밀번호 확인을 입력하세요.");
					repassword.focus();
					return false;
				}

				if (password.value !== repassword.value) {
					alert("변경 비밀번호와 변경 비밀번호 확인의 값이 일치 하지 않습니다.");
					password.value = "";
					repassword.value = "";
					password.focus();
					return false;
				}

				if (!confirm("비밀번호를 변경하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/groupware/password");
				const params = {
					password : password.value
				};
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						alert("로그인 페이지 이동 시키기!!");"
					} else {
						parent.closeLayer();
					}
				})
			}
		</script>
	</form>
</body>
</html>