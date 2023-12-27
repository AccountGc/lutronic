<%@page import="com.e3ps.common.service.CommonHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import ="wt.session.*,wt.org.*,java.util.Calendar,wt.query.*,wt.fc.*"%>




<%
//System.out.println("jsp History		==	start");
CommonHelper.service.createLoginHistoty();
//System.out.println("jsp History		==	end");
%>


<%@page import="java.util.Hashtable"%>
<jsp:forward page="/eSolution/groupware/main.do"/>