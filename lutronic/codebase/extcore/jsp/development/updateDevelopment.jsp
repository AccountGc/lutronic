<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.e3ps.development.dto.MasterData"%>
<%
String oid = (String) request.getAttribute("oid");
MasterData data = (MasterData) request.getAttribute("data");
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
<form name="updateDevelopmentForm" id="updateDevelopmentForm" method="post" >
	<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
	<table width="100%" border="0" cellpadding="0" cellspacing="0" > 
		<tr height="5">
			<td>
				<table class="button-table">
					<tr>
						<td class="left">
							<div class="header">
								<img src="/Windchill/extcore/images/header.png">&nbsp;개발업무 수정
							</div>
						</td>
						<td class="right">
							<input type="button" value="수정" name="updateDevBtn" id="updateDevBtn" >
							<input type="button" value="이전페이지" name="backBtn" id="backBtn" onclick="javascript:history.back();">
						</td>
					</tr>
				</table>
			</td>
		</tr>
		
		<tr align="center">
			<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
				<table class="create-table">
					<colgroup>
						<col width="150">
						<col width="*">
						<col width="150">
						<col width="*">
					</colgroup>
					<tr>
						<th class="req lb">프로젝트 코드</th>
						<td class="indent5"><select name="model" id="model" class="width-200">
								<option value="">선택</option>
								<option value="INWORK">작업 중</option>
								<option value="UNDERAPPROVAL">승인 중</option>
								<option value="APPROVED">승인됨</option>
								<option value="RETURN">반려됨</option>
						</select></td>
						<th class="req lb">프로젝트명</th>
						<td class="indent5"><input type="text" name="name" id="name" class="width-200" value="<%= data.getName() %>"></td>
					</tr>
					<tr>
						<th class="req lb">예상 시작일</th>
						<td class="indent5"><input type="text" name="developmentStart" id="developmentStart" class="width-100" value="<%= data.getDevelopmentStart() %>"><img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"></td>
						<th class="req lb">예상 종료일</th>
						<td class="indent5"><input type="text" name="developmentEnd" id="developmentEnd" class="width-100" value="<%= data.getDevelopmentEnd() %>"><img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"></td>
					</tr>
					<tr>
						<th class="req lb">관리자</th>
						<td class="indent5" colspan="3"><input type="text" name="dm" id="dm" data-multi="false" class="width-200"> <input type="hidden" name="creatorOid" id="creatorOid" value="<%= data.getDmOid() %>"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')"></td>
					</tr>
					<tr>
						<th class="lb">설명</th>
						<td colspan="3" class="indent5"><textarea name="description" id="description" rows="6"  value="<%= data.getDescription() %>"></textarea></td>
					</tr>
				</table>
		</td>
	</tr>
</table>
<script>
</script>
</form>
</body>
</html>