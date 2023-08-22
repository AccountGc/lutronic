<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>
<form name="partChange">
<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
<input type="hidden" name="checkDummy" id="checkDummy" value="false">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">&nbsp;부품 진채번
			</div>
		</td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr height="5" align=center>
		<td>
			<table class="button-table">
				<tr>
					<td class="left">
						<input type="button" value="펼치기" name="depthAllSelect" id="depthAllSelect" >
					</td>
					<td class="right">
						<input type="button" value="필터 초기화" name="filterInit" id="filterInit" >
						<input type="button" value="속성 일괄 적용" name="batchAttribute" id="batchAttribute" >
						<input type="button" value="저장" name="setNumber" id="setNumber"  class="btnCRUD">
						<input type="button" value="닫기" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>
<script>
</script>
</form>
</body>
</html>