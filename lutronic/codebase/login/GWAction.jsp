<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%
	String redirectUrl = "";
	String userID = (String)request.getSession().getAttribute("loginUserId");
	boolean isLogin = userID == null ? false : true;

	//System.out.println("##############	GWAction	start		###########");
	//System.out.println("############## userID ="+userID+",  isLogin="+isLogin);
	if(isLogin){
		//System.out.println("##############	GWAction	Sucess 		###########");
		redirectUrl = "/Windchill/jsp/portal/loginHistory.jsp";
		response.sendRedirect(redirectUrl);
	}else{
		//System.out.println("##############	GWAction	faile		###########");
		redirectUrl = "/Windchill/login/";
		response.sendRedirect(redirectUrl);
	}
	
%>

