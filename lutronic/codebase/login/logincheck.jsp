<%@page import="wt.org.WTUser"%>

<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.common.util.LoginAuthUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%

	String redirectUrl = "/Windchill/login/index.html";
	String authString = null;

	String id=request.getParameter("id");
	String pw=request.getParameter("pw");
	String autoTaskSet = StringUtil.checkNull(request.getParameter("autoTaskSet"));

	//System.out.println("##############		start		###########");
	
	if(LoginAuthUtil.validatePassword(id,pw)){
		authString = com.infoengine.util.Base64.encode(id+":"+pw);
		
		authString = setString(authString);
		
		Cookie authCookie = new Cookie("AuthCookie",authString);
		authCookie.setPath("/Windchill");
		response.addCookie(authCookie);
		redirectUrl = "/Windchill/jsp/portal/loginHistory.jsp";
		
		if(!"".equals(autoTaskSet) ){
			redirectUrl += "?autoTaskSet="+autoTaskSet;
		}
		
		session.removeAttribute("Logout");
		session.setAttribute("loginUserId", id);
		response.setHeader("Location", redirectUrl);
	    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		//System.out.println("##############		end		###########");
	}else{
%>
<script>
alert("아이디 또는 비밀번호가 잘못되었습니다.");		//아이디 또는 비밀번호가 잘못되었습니다.
location.href="index.html";
</script>
<%
	}
%>

<%!
	public String setString(String ss){
		if(ss.endsWith("=")){
			ss= ss.substring(0,ss.length()-1);
			return setString(ss);
		}else
			return ss;
	}
%>