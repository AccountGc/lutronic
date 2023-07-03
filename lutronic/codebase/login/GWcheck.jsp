<%@page import="wt.org.WTUser"%>

<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.common.util.LoginAuthUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%

	String redirectUrl = "/Windchill/login/index.html";
	String authString = null;

	String id=request.getParameter("id");
	id = id.toLowerCase();
	String pw=request.getParameter("pw");
	pw = pw.toLowerCase();
	String autoTaskSet = StringUtil.checkNull(request.getParameter("autoTaskSet"));

	//System.out.println("##############		GWcheck start		###########");
	//System.out.println("##############		id ="+ id);
	//System.out.println("##############		pw ="+ pw);
	//if (LoginAuthUtil.validatePassword(id,pw) ){
	if(LoginAuthUtil.validatePassword(id,pw)){
		//	System.out.println("=================>>>>authString " +id+":"+pw );
		authString = com.infoengine.util.Base64.encode(id+":"+pw);
		
		authString = setString(authString);
		
		Cookie authCookie = new Cookie("AuthCookie",authString);
		authCookie.setPath("/Windchill");
		response.addCookie(authCookie);
		redirectUrl = "/Windchill/jsp/portal/loginHistory.jsp";
		//redirectUrl = "/Windchill/intellian/groupware/main.do";
		
		if(!"".equals(autoTaskSet) ){
			redirectUrl += "?autoTaskSet="+autoTaskSet;
		}
		
		session.removeAttribute("Logout");
		session.setAttribute("loginUserId", id);
		//response.setHeader("Location", redirectUrl);
	    //response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		//System.out.println("##############		GWcheck end	Sucess	###########");
	}else{
		//System.out.println("##############		GWcheck end	Faile	###########");
	}
%>
<script>

window.open('about:blank','_self');
opener=window;
window.close();
</script>
<%!
	public String setString(String ss){
		if(ss.endsWith("=")){
			ss= ss.substring(0,ss.length()-1);
			return setString(ss);
		}else
			return ss;
	}
%>