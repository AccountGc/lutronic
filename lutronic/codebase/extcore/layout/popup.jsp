<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><tiles:insertAttribute name="title" ignore="true" /></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
</head>
<body id="popup">
	<form id="form">
		<!-- body -->
		<table id="popup-table">
			<tr>
				<td valign="top">
					<tiles:insertAttribute name="body" />
				</td>
			</tr>
		</table>

		<!-- footer -->
		<footer>
			<tiles:insertAttribute name="footer" />
		</footer>
	</form>
</body>
</html>

