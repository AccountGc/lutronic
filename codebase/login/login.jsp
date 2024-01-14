<%@page session="false"
%><%@page contentType="text/html" pageEncoding="UTF-8"

%><%@page import="wt.login.loginResource"

%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/util" prefix="util"

%><%--
 /* The output of this page is read by com.ptc.fba.FormBasedLogin to produce a form-based login GUI.
  * This page can be customized to inform FormBasedLogin of additional fields, whether or not it is
  * used as the login form.
  * In order for FormBasedLogin to understand the output:
  *   1) It MUST be valid XML.
  *   2) All form inputs to be presented to the user (besides OK/Cancel/Yes/No) must be of type "text"
  *      or "password".  Visible (non-hidden) inputs of other types will simply be ignored.
  *   3) Each "text" or "password" input must have a corresponding label element and this element
  *      must contain nothing other than the label text.
  *   4) Form inputs of type "hidden" can also be included and used to provide pre-specified
  *      (non-user-specified) inputs to the login.
  * Note that the element ids "username" and "password" are used by FormBasedLogin to recognize the
  * user name and password fields.
  */
--%><util:locale
/><fmt:setLocale value="${localeBean.locale}"
/><fmt:setBundle basename="<%=loginResource.class.getName()%>"
/><?xml version="1.0" encoding="UTF-8"?>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title><fmt:message key='<%=loginResource.LOGIN_TO_WINDCHILL%>'/></title>
</head>
<body>
<center>
<h2><fmt:message key='<%=loginResource.LOGIN_TO_WINDCHILL%>'/></h2>
<c:choose>
<c:when test='${loginFailed}'>
<font style="color: red; font-weight: bolder"><fmt:message key='<%=loginResource.RE_ENTER_CREDENTIALS%>'/></font>
</c:when>
<c:otherwise><b><fmt:message key='<%=loginResource.ENTER_CREDENTIALS%>'/></b></c:otherwise>
</c:choose>
<form method="POST" action="j_security_check" id="login">
<table>
<tr><td colspan="2" height="6"/></tr>
<tr>
<th scope="row" align="right"><label for="username"><fmt:message key='<%=loginResource.USER_NAME_LABEL%>'/></label></th>
<td><input type="text" name="j_username" id="username" size="32" AUTOCOMPLETE="OFF" /></td>
</tr>
<tr>
<th scope="row" align="right"><label for="password"><fmt:message key='<%=loginResource.PASSWORD_LABEL%>'/></label></th>
<td><input type="password" name="j_password" id="password" size="32" AUTOCOMPLETE="OFF" /></td>
</tr>
<tr><td colspan="2" height="12"/></tr>
<tr>
<td colspan="2" align="center"><input type="submit" id="ok" value="<fmt:message key='<%=loginResource.OK%>'/>"/></td>
</tr>
</table>
</form>
</center>
</body>
</html>
