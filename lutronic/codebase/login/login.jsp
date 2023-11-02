<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>LUTRONIC PDM</title>
</head>
<LINK rel="stylesheet" type="text/css" href="/Windchill/extcore/login/css/e3ps.css">
<%!
public static String getClientIp(HttpServletRequest req) {
    String ip = req.getHeader("X-Forwarded-For");
    if (ip == null) ip = req.getRemoteAddr();
    return ip;
}
%>
<%
//System.out.println("HI @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	String  ip =getClientIp(request);
	
	String ipAddress=request.getRemoteAddr();
	boolean isCheck = false;
	if(ipAddress.equals("192.168.254.113")){
		System.out.println("클라이언트 IP 주소: "+ipAddress);
		wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer.getDefault();
		   System.out.println("<br>세션 확인 ::: "+(methodServer.getUserName() == null));
		   HttpSession session2 = request.getSession(false);
		   int maxTime = session2.getMaxInactiveInterval();
		   long createTimeL = session2.getCreationTime();
			   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			   String creationTimeString = dateFormat.format(new Date(createTimeL));
			   System.out.println("<br>creationTimeString ::: "+creationTimeString);
			   // 서버로 클라이언트가 마지막으로 요청한 시간
			   long accessedTime = session.getLastAccessedTime();
			   
			   String accessedTimeString = dateFormat.format(new Date(accessedTime));
			    System.out.println("<br>accessedTimeString ::: "+accessedTimeString);
		   System.out.println("<br>세션 남은시간 ::: "+(maxTime)+"분");
		  
		if (methodServer.getUserName() == null)
		{
		   System.out.println("<br>세션 셋팅 ::: "+(methodServer.getUserName() == null));
			
		}else{
			isCheck = true;
				
				System.out.println("<br>세션 유저 네임 ::: "+(methodServer.getUserName()));
		}
		

	}
	String  url = request.getRequestURL().toString();
	System.out.println("ip 192.168.254.113 ="+ip+(ip.equals("192.168.254.113")+"\t"+request.getProtocol()));
	//System.out.println("ip 10.200.101.53 ="+ip+(ip.equals("10.200.101.53")+"\t"+request.getProtocol()));
%>
<script type="text/javascript" src="/Windchill/login/js/jquery-1.11.1.min.js" ></script>
<script type="text/javascript">

<%if((ip.equals("192.168.254.113") || ip.equals("worker-HP"))&& !isCheck ){%>
 window.onload = function(){

 	//	enter();
		 autologin();
 	}
	//$("#login").attr("action","j_security_check").submit();
<%}%>
var cookieName = "eCarID=";
document.onkeypress = onKeyPress; 


/**************************************************************
*                      쿠키 생성
****************************************************************/
window.setCookie = function(cName, cValue, cDay) {
	var expire = new Date();
	expire.setDate(expire.getDate() + cDay);
	cookies = cName + '=' + escape(cValue) + '; path=/ '; // 한글 깨짐을 막기위해 escape(cValue)를 합니다.
	if (typeof cDay != 'undefined')
		cookies += ';expires=' + expire.toGMTString() + ';';
	document.cookie = cookies;
}

/**************************************************************
*                      쿠키 가져오기
****************************************************************/
window.getCookie = function(cName) {
	cName = cName + '=';
	var cookieData = document.cookie;
	var start = cookieData.indexOf(cName);
	var cValue = '';
	if (start != -1) {
		start += cName.length;
		var end = cookieData.indexOf(';', start);
		if (end == -1)
			end = cookieData.length;
		cValue = cookieData.substring(start, end);
	}
	return unescape(cValue);
}

$(document).ready(function () {
	checkServerType();
// 	$("#id").focus();
	getId();
	
	<%if((ip.equals("192.168.254.113") || ip.equals("worker-HP"))&& !isCheck ){%>
	
	autologin();
	
	//$("#login").attr("action","j_security_check").submit();
<%}%>
})

window.checkServerType = function() {
	var url = location.host;
	var server = url.split('.')[0];
	if( server.indexOf('dev') > 0) {
		$('#serverType').show();
	}else {
		$('#serverType').hide();
	}
}

function enter() {
  if(IsNullData($("#j_username").val())) {
		alert("아이디를 입력하세요");
		return;
	}
	if(IsNullData($("#j_password").val())) {
		alert("비밀번호를 입력하세요");
		return;
	}

	$checkId = $("#checkId");
	
	if($checkId.prop("checked") == true) {
		setCookie("setID", $id.val(), 9999);
	}else{
		setCookie("setID", $id.val(), -1);
	}
	//createId();
	//$("#login").attr("action","/Windchill/intellian/common/loginCheck.do").submit();
	//$("#login").attr("action","/Windchill/login/logincheck.jsp").submit();
	$("#login").attr("action","/Windchill/j_security_check").submit();
    //login.submit();
    
}

function IsNullData(str) {
	if(str.length == 0) {
		return true;
	}
	for(var i=0;i<str.length;i++) {
		if(str.charCodeAt(i) != 32) {
			return false;
		}
	}
	return true;
}

function onKeyPress() { 
	var keycode; 
	if (window.event) keycode = window.event.keyCode;
	else if ( e ) keycode = e.which;
	else return true;
	if (keycode == 13) {
		enter();
	return false;
	} 
	return true;
}

function getId() {
	var val = "";

	$id = $("#j_username");
	$password = $("#j_password");
	$checkId = $("#checkId");
	
	val = getCookie("setID");
	
	if (val != "") {
		$id.val(val);
		$checkId.prop("checked", true);
		$password.focus();
	} else {
		$checkId.prop("checked", false);
		$id.focus();
	}
}

<%if((ip.equals("192.168.254.113") || ip.equals("worker-HP"))&& !isCheck ){%>
function autologin() {
	document.getElementById("j_username").value = "wcadmin";
	document.getElementById("j_password").value = "lutadmin321!";
	$("#login").attr("action","j_security_check").submit();
} 
// document.getElementById("login").addEventListener("load", autologin);
<%}%>
</Script>
</head>
<style>
.background {
	width:815px;
	height:350px;
	background:url('/Windchill/login/images/bg_login_1.jpg') no-repeat;
	text-align:center;
}
.background img{
	float:left;
}
.background .text{
	width:160px;
	height:18px;
	border:1px solid #CCCCCC;
}
.background .btn{
	float:left;
	width:70px;
	height:45px;
	margin-top:3px;
	color:#FFFFFF;
	font-weight:bold;
	font-familly:arial;
	border:0px;
	background:#395886;
	border-radius: 3px;
	cursor:pointer;
}
.background label{
	cursor:pointer;
}
</style>
<body>
<form name="login" id="login" method="post" style="margin-bottom:0px;">
<table style="width:100%;height:100%;">
	<tr>
		<td style="width:25%;"></td>
		<td>
			<div class="background">
				<img src="/Windchill/login/images/lutronic.gif" />
				
				<div style="padding-left:200px; padding-top:10px;">
					<span id='serverType' style='float:left; font-weight: bold; font-size: 17pt;'>
						DEV
					</span>
				</div>
				
				<div style="padding-left:330px;padding-top:130px;">
					<span style="float:left;">
						<input class="text" type="text" name="j_username" id="j_username" style="margin:5px;"><br>
						<input class="text" type="password" name="j_password" id="j_password">
					</span>
					<input class="btn" type="button" value="Sign In" onclick="javascript:enter();"/>
				</div>
				<div style="margin-top:80px;text-align:left;margin-left:280px;">
					<label><input type="checkbox" name="checkId" id="checkId" value="checkbox">&nbsp;ID저장</label>
				</div>
			</div>
		</td>
		<td style="width:25%;"></td>
	</tr>
</table>
</form>
</body>
</html>
