<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>LUTRONIC PLM</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="/Windchill/login/css/login.css" type="text/css">
<link href="//netdna.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
<script type="text/javascript" src="https://code.jquery.com/jquery-1.10.2.min.js" /></script>
</head>
<body>
	<form method="post" action="j_security_check">
		<div class="myform">
			<div class="logo">
				PLEASE LOG IN!
				<div>
					<i class="fa fa-cloud-upload" aria-hidden="true"></i>
				</div>
			</div>
				<input type="email" placeholder="&#xf003;   Email" />
				<input type="password" placeholder=" &#xf023;  Password" />
				<button type="submit">LOG IN</button>
				<div>
					<a href="#">Forgot Password?</a>
				</div>
		</div>
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
