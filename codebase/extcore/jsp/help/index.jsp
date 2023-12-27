<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<form>

<table width="100%" height="100%" border="0" cellspacing="5" cellpadding="0">
	<tr>
		<td width=100%>
			<table border="0" width="100%" height="100%" cellspacing="0" cellpadding="0">
				<tr>
					<td class="TL"></td>
                    <td class="TM"> </td>
                    <td class="TR"></td>
                </tr>
                <tr>
                    <td class="L"></td>
                    <td valign="top">
                        <table width=100% border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>
                                	<c:forEach items="${list }" var="list">
                                		<a href="/Windchill/jsp/help/<c:out value="${sFilePath }"/>/<c:out value="${list.fileName }" escapeXml="false" />">
	                                		<c:out value="${list.icon }" escapeXml="false" />
	                                		<c:out value="${list.fileName }" escapeXml="false" />(<c:out value="${list.fileSize }" />) <br>
                                		</a>
                                	</c:forEach>
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