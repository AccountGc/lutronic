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
<body class="in">
	<form method="post" action="j_security_check">
		
        <div class="in_container">
        	<header>
                <h1><span class="hide">LUTRONIC PLM</span></h1>
            </header>
            <section>
            <form name="loginForm" action="/loginProcess.do" method="POST" autocomplete="off">
                <i class="login mb30"></i>
                <div class="text_form">
                    <p>
                        <label for="principal" ></label>
                        <input id="j_username" type="text" name="j_username"  placeholder="ID">
                    </p>
                    <p>
                        <label for="credential"></label>
                        <input id="j_password" type="password" name="j_password"  placeholder="Password" />
                    </p>
                </div>
                <a value="Login" onclick="_login();" class="btn">LOGIN</a>
                <div class="checks check_box">
                    <input type="checkbox" id="checkId" name="checkId">
                    <label for="checkId">아이디저장</label>
                </div>
                
            </form>
            </section>
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
		$(document).ready(function(){
			var idChk = getCookie("idChk");
			if(idChk!=""){
				$("#j_username").val(idChk); 
				$("#j_password").val(getCookie("pw")); 
			}
			 
			if($("#j_username").val() != ""){ 
				$("#idSaveCheck").attr("checked", true); 
			}
			 
			$("#checkId").change(function(){ 
				if($("#checkId").is(":checked")){ 
					setCookie("idChk", $("#j_username").val(), 7); 
					setCookie("pw", $("#j_password").val(), 7); 
				}else{ 
					deleteCookie("idChk");
				}
			});
			 
			$("#j_username").keyup(function(){ 
				if($("#checkId").is(":checked")){
					setCookie("idChk", $("#j_username").val(), 7); 
				}
			});
			
			$("#j_password").keyup(function(){ 
				if($("#checkId").is(":checked")){
					setCookie("pw", $("#j_password").val(), 7); 
				}
			});
		});
		
		function setCookie(cookieName, value, exdays){
		    var exdate = new Date();
		    exdate.setDate(exdate.getDate() + exdays);
		    var cookieValue = escape(value) + ((exdays==null) ? "" : "; expires=" + exdate.toGMTString());
		    document.cookie = cookieName + "=" + cookieValue;
		}
		 
		function deleteCookie(cookieName){
			var expireDate = new Date();
			expireDate.setDate(expireDate.getDate() - 1);
			document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString();
		}
			 
		function getCookie(cookieName) {
			cookieName = cookieName + '=';
			var cookieData = document.cookie;
			var start = cookieData.indexOf(cookieName);
			var cookieValue = '';
			if(start != -1){
				start += cookieName.length;
				var end = cookieData.indexOf(';', start);
				if(end == -1)end = cookieData.length;
				cookieValue = cookieData.substring(start, end);
			}
			return unescape(cookieValue);
		}
	</script>
</html>
