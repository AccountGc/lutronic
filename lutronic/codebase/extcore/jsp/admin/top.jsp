<%@page import="java.sql.Timestamp"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="com.e3ps.common.history.LoginHistory"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.method.RemoteMethodServerBean"%>
<%@page import="wt.auth.Authentication"%>
<%@page import="wt.method.RemoteMethodServer"%>
<%@page import="com.e3ps.common.service.CommonHelper"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<style>
#module_menu {background-color:#444444;width:100%;background-color:#444444;height:50px;}
#module_menu ul { list-style:none; margin:0; padding:0;}
#module_menu li{background-color:#444444; color:#FFFFFF; margin:0 0 0 0; cursor:pointer; padding:0 20px 0 20px;border:0; float:left;font-family:맑은 고딕;font-weight:bold;font-size:14px;vertical-align:middle;height:50px;line-height:50px;}

</style>
<%
HttpSession session2 = request.getSession();
long createTimeL = session2.getCreationTime();
try{
	QuerySpec qs = new QuerySpec();
	
	int idx = qs.appendClassList(LoginHistory.class, true);
	WTUser ssuser = (WTUser) SessionHelper.manager.getPrincipal();
	String userName = "";
	if(null!=ssuser){
		userName = ssuser.getName();
		//if(userName.equals("Administrator")) userName = "wcadmin";
	}
	
	if(userName != null && userName.trim().length() > 0) {
		if(qs.getConditionCount() > 0)
			qs.appendAnd();
		qs.appendWhere(new SearchCondition(LoginHistory.class, "id", SearchCondition.LIKE, "%" + userName + "%"), new int[] { idx });
	}
	 qs.appendOrderBy(new OrderBy(new ClassAttribute(LoginHistory.class,"thePersistInfo.createStamp"), true), new int[] { idx }); 
	QueryResult qr = PersistenceHelper.manager.find(qs);
	System.out.println(qs);
	System.out.println(qr.size());
	if(qr.hasMoreElements()){
		Object[] objs = (Object[])qr.nextElement();
		LoginHistory loginHistory= (LoginHistory)objs[0];
		Timestamp data = loginHistory.getPersistInfo().getCreateStamp();
		Date createDate = new Date(createTimeL);
		Timestamp ts=new Timestamp(createDate.getTime());
		long diff = ts.getTime() - data.getTime();
		long sec = diff / 1000;
		long min = sec / 60;
		if(min<0) min=min*-1;
		if(min>60)
			CommonHelper.service.createLoginHistoty();
		System.out.println(data+"\t"+createDate+"\tsec ::: "+sec+"\tmin ::: "+min);
	}
}catch(Exception e){
	
}

%>

<script type="text/javascript">
var module = "<c:out value='${module}'/>";

$(document).ready(function() {
	getUserData();
	setToDay();
	$("#module_menu li[title='" + module + "']").css("background-color","#317AB2");
	$("#module_menu li[title='" + module + "']").css("line-height","55px");
	//$("#"+module).attr("src","/Windchill/jsp/admin/images/img_menu/"+module+"_1.png");
})

$(function() {
	/*
	$("img").click(function() {
		
		if(this.id == "part"){
			document.location = getURLString("admin", "admin_mainCompany", "do");
		}else if(this.id == "code") {
			document.location = getURLString("admin", "admin_numberCode", "do");
		}else if(this.id == "activity") {
			document.location = getURLString("admin", "admin_listChangeActivity", "do");
		}else if(this.id == "mail") {
			document.location = getURLString("admin", "admin_mail", "do");
		}else if(this.id == "loginhistory") {
			document.location = getURLString("admin", "admin_loginhistory", "do");
		}else if(this.id == "download") {
			document.location = getURLString("admin", "admin_downLoadHistory", "do");
		}else if(this.id == "windchill") {
			document.location = getURLString("admin", "admin_Windchill", "do");
		}
	})
	*/
	$("#module_menu li").click(function(){

		var title = $(this).attr("title");
		var url;
		
		if(title == "part"){
			document.location = getURLString("admin", "admin_mainCompany", "do");
		}else if(title == "code") {
			document.location = getURLString("admin", "admin_numberCode", "do");
		}else if(title == "activity") {
			document.location = getURLString("admin", "admin_listChangeActivity", "do");
		}else if(title == "mail") {
			document.location = getURLString("admin", "admin_mail", "do");
		}else if(title == "loginhistory") {
			document.location = getURLString("admin", "admin_loginhistory", "do");
		}else if(title == "download") {
			document.location = getURLString("admin", "admin_downLoadHistory", "do");
		}else if(title == "windchill") {
			document.location = getURLString("admin", "admin_Windchill", "do");
		}else if(title == 'package') {
			document.location = getURLString('admin', 'admin_package', 'do');
		}
		//goMenu(url,title);
	});
	
	$("#module_menu li").mouseover(function(){
		
		var title = $(this).attr("title");
		
		if(module != title){
			$(this).css("background-color","#317AB2");
			$(this).css("line-height","55px");
		}
	});
	$("#module_menu li").mouseout(function(){
		
		var title = $(this).attr("title");
		
		if(module != title){
			$(this).css("background-color","#444444");
			$(this).css("line-height","50px");
		}
	});
})

<%----------------------------------------------------------
*                      접속자 이름 설정
----------------------------------------------------------%>
function getUserData() {
	var form = $("form[name=adminMain]").serialize();
	var url	= getURLString("user", "getUserData", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "유저 정보 오류";
			alert(msg);
		},

		success:function(data){
			$("#userFullName").html(data.name);
		}
	});
}

<%----------------------------------------------------------
*                      오늘 날자 설정
----------------------------------------------------------%>
function setToDay() {
	var week = new Array("일","월","화","수","목","금","토");
	
	var toDay = new Date();
	
	var year = toDay.getFullYear();
	var month = toDay.getMonth();
 	var date = toDay.getDate();
 	var day = toDay.getDay();
 	
 	$("#toDay").html(year + "년 " + (month+1) + "월 " + date + "일 " + week[day] + "요일"  );
 	
}

</script>

<body>

<form id="adminMain" >

<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
        <td height="100" colspan="3">
        	<table width="100%" valign="top" height="150" style="padding-top:0; table-layout: fixed;" background="" border="0" cellspacing="0" cellpadding="0">
        		<tr>
        			<td align="center" valign="top">
        				<table border="0" width=100% cellspacing="0" cellpadding="0" align="center">
        					<tr>
        						<td colspan="6" align="right" width=100% style="padding-right:10;"  height="76">
        							<table border="0" width=100% cellpadding="0" cellspacing="0">
        								<tr>
        									<td  width="5%">
        										<a href="/Windchill/eSolution/admin/admin_mainCompany.do" target="_top">
        											<img src="/Windchill/jsp/admin/images/img_menu/adminmode.png" border="0">
        										</a>
        									</td>
        									
        									<td align="right" width="80%">
        									
	        									<B><i id="userFullName"></i></B> 님께서 접속하셨습니다. 
	        									
	        									<font color="#757575">
	        										<i id="toDay"></i>
	        									</font>
	        									<BR><BR>
	        									
	        								</td>
	        								        									
        								</tr>
        							</table>
        						</td>
        					</tr>
        					
        					<tr>
        						<td>
        							<!-- 버튼 부분 -->
        							<table width="100%" border="0" cellpadding="0" cellspacing="0">
        								<tr>
        									<td align="left">
        										<%-- 
												<img border="0" src="/Windchill/jsp/admin/images/img_menu/part.png" id="part" style="cursor: pointer;"/>
												
												<img border="0" src="/Windchill/jsp/admin/images/img_menu/code.png" id="code" style="cursor: pointer;"/>
												
												
												<img border="0" src="/Windchill/jsp/admin/images/img_menu/admin.png" id="admin" style="cursor: pointer;"/>
												
												
												<img border="0" src="/Windchill/jsp/admin/images/img_menu/activity.png" id="activity" style="cursor: pointer;"/>
												
												<img border="0" src="/Windchill/jsp/admin/images/img_menu/mail.png" id="mail" style="cursor: pointer;"/>
												
												<img border="0" src="/Windchill/jsp/admin/images/img_menu/loginhistory.png" id="loginhistory" style="cursor: pointer;"/>
												
												<img border="0" src="/Windchill/jsp/admin/images/img_menu/download.png" id="download" style="cursor: pointer;"/>
												
												<img border="0" src="/Windchill/jsp/admin/images/img_menu/windchill.png" id="windchill" style="cursor: pointer;"/>
												--%>
												
												<div id="module_menu">
													<ul>
														<li title="part">
															${f:getMessage("부서관리")}
														</li>
														<li title="code">
															${f:getMessage("코드체계관리")}
														</li>
														<li title="activity">
															${f:getMessage("설계변경관리")}
														</li>
														<li title="mail">
															${f:getMessage("외부메일관리")}
														</li>
														<li title="loginhistory">
															${f:getMessage("접속이력관리")}
														</li>
														<li title="download">
															${f:getMessage("다운로드 이력관리")}
														</li>
														<li title="package">
															${f:getMessage("등록양식관리")}
														</li>
														<li title="windchill">
															Windchill
														</li>
													</ul>
												</div>
												
        									</td>
        								</tr>
        							</table>
        						</td>
        					</tr>
        				</table>        				
        			</td>
        		</tr>
        	</table>				
		</td>
	</tr>
</table>

</form>

</body>
</html>