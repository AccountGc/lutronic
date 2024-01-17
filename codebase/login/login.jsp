<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>LUTRONIC PLM</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="/Windchill/login/css/login.css" type="text/css">
<link rel="shortcut icon" href="/Windchill/extcore/images/icon/poongsan_icon.png">
<script type="text/javascript" src="https://code.jquery.com/jquery-1.10.2.min.js" /></script>
</head>
<body>
	<form method="post" action="j_security_check">

		<div id="form-container">
			<div id="form-inner-container">
				<!-- Sign up form -->
				<div id="sign-up-container">
					<h1>
						<font color="red">LUTRONIC</font>
						<font color="red"> PDM</font>
					</h1>
					<label for="j_username">아이디</label>
					<input type="text" name="j_username" id="j_username" placeholder="j_username">

					<label for="j_password">비밀번호</label>
					<input type="password" name="j_password" id="j_password" placeholder="&#9679;&#9679;&#9679;&#9679;&#9679;&#9679;">

					<div id="form-controls">
						<button type="submit">
							<b>로 그 인</b>
						</button>
					</div>

					<input type="checkbox" name="terms" id="terms">
					<label for="terms" style="position: relative; bottom: 12px; left: -5px;">
						<b>아이디 저장</b>
					</label>
				</div>

				<!-- 				<div id="animation-container"> -->
				<!-- 					<lottie-player src="https://assets3.lottiefiles.com/packages/lf20_aesgckiv.json" background="transparent" speed="1" style="width: 520px; height: 520px;" loop autoplay></lottie-player> -->
				<!-- 				</div> -->
			</div>
		</div>


		<!-- 		<script src="https://unpkg.com/@lottiefiles/lottie-player@latest/dist/lottie-player.js"></script> -->

	</form>
</body>


<script type="text/javascript">
	function _login() {
		document.forms[0].submit();
		$checkId = $("#checkId");
	}

	document.addEventListener("keydown", function(event) {
		const keyCode = event.keyCode || event.which;
		if (keyCode === 13) {
			_login();
		}
	})

	document.addEventListener("DOMContentLoaded", function() {
		const j_username = document.getElementById("j_username").focus();
	})

	//아이디저장
	$(document).ready(function() {
		var idChk = getCookie("idChk");
		if (idChk != "") {
			$("#j_username").val(idChk);
			$("#j_password").val(getCookie("pw"));
		}

		if ($("#j_username").val() != "") {
			$("#idSaveCheck").attr("checked", true);
		}

		$("#checkId").change(function() {
			if ($("#checkId").is(":checked")) {
				setCookie("idChk", $("#j_username").val(), 7);
				setCookie("pw", $("#j_password").val(), 7);
			} else {
				deleteCookie("idChk");
			}
		});

		$("#j_username").keyup(function() {
			if ($("#checkId").is(":checked")) {
				setCookie("idChk", $("#j_username").val(), 7);
			}
		});

		$("#j_password").keyup(function() {
			if ($("#checkId").is(":checked")) {
				setCookie("pw", $("#j_password").val(), 7);
			}
		});
	});

	function setCookie(cookieName, value, exdays) {
		var exdate = new Date();
		exdate.setDate(exdate.getDate() + exdays);
		var cookieValue = escape(value) + ((exdays == null) ? "" : "; expires=" + exdate.toGMTString());
		document.cookie = cookieName + "=" + cookieValue;
	}

	function deleteCookie(cookieName) {
		var expireDate = new Date();
		expireDate.setDate(expireDate.getDate() - 1);
		document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString();
	}

	function getCookie(cookieName) {
		cookieName = cookieName + '=';
		var cookieData = document.cookie;
		var start = cookieData.indexOf(cookieName);
		var cookieValue = '';
		if (start != -1) {
			start += cookieName.length;
			var end = cookieData.indexOf(';', start);
			if (end == -1)
				end = cookieData.length;
			cookieValue = cookieData.substring(start, end);
		}
		return unescape(cookieValue);
	}
</script>
</html>
