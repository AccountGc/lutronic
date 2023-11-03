<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<link rel="stylesheet" href="../login/css/login.css" type="text/css" />
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>LUTRONIC PDM</title>
</head>
<body>
	<form method="post" action="j_security_check" id="loginForm">
		<div id="container" class="container">
			<div class="row">
				<div class="col align-items-center flex-col sign-up">
					<div class="form-wrapper align-items-center">
						<div class="form sign-up"></div>
					</div>
				</div>
				<div class="col align-items-center flex-col sign-in">
					<div class="form-wrapper align-items-center">
						<div class="form sign-in">
							<div class="input-group">
								<i class='bx bxs-user'></i>
								<input type="text" placeholder="아이디" id="j_username" name="j_username">
							</div>
							<div class="input-group">
								<i class='bx bxs-lock-alt'></i>
								<input type="password" placeholder="비밀번호" id="j_password" name="j_password">
							</div>
							<input type="checkbox" name="checkId" id="checkId" style="margin-bottom: 20px;">
							<label for="checkId">&nbsp;ID저장</label>
							<input type="button" id="login" value="로그인" onclick="_login();">
						</div>
					</div>
					<div class="form-wrapper"></div>
				</div>
			</div>
			<div class="row content-row">
				<!-- SIGN IN CONTENT -->
				<div class="col align-items-center flex-col">
					<div class="text sign-in">
						<h2>LUTRONIC</h2>
						<h2>PDM SYSTEM</h2>
					</div>
					<div class="img sign-in"></div>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			function _login() {
				document.forms[0].submit();
			};

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					_login();
				}
			})

			document.addEventListener("DOMContentLoaded", function() {
				const j_username = document.getElementById("j_username");
				const j_password = document.getElementById("j_password");
				const checkId = document.getElementById("checkId");
				
				//
				const savedUsername = localStorage.getItem("cookie");
				if (savedUsername) {
					j_username.value = savedUsername;
					checkId.checked = true;
					j_password.focus();
				} else {
					j_username.focus();
				}
			})
			
			//아이디 저장하기 위해 체크박스에 이벤트 리스너를 추가합니다.
			checkId.addEventListener("change", function() {
				const j_username = document.getElementById("j_username");
				if (checkId.checked) {
					localStorage.setItem("cookie", j_username.value);
				} else {
					localStorage.removeItem("cookie");
				}
			});
			
			const container = document.getElementById("container")
			toggle = () => {
				container.classList.toggle("sign-in")
			};
			
			setTimeout(() => {
				container.classList.add("sign-in")
			}, 200);
			
		</script>
	</form>
</body>
</html>
