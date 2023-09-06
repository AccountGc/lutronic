<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
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
	<form>
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="lastNum" id="lastNum">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="oid" id="oid">

		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>OID</th>
				<td class="indent5">
					<input type="button" name="delete" id="delete" value="Delete" >
				</td>
			</tr>
			<tr>
				<th>LifeCycleTemplate</th>
				<td class="indent5">
					New LifeCycleTemplate<input type="text" name="lifecycle" id="lifecycle" class="width-300">
					<input type='button' name='reassign' id='reassign' value='reassign' >
				</td>
			</tr>
			<tr>
				<th>State</th>
				<td>TERMINATE :&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="terminate" value="true" checked="checked">
						<div class="state p-success">
							<label> <b>TRUE</b>
							</label>
						</div>
					</div> &nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="terminate" value="false">
						<div class="state p-success">
							<label> <b>FALSE</b>
							</label>
						</div>
					</div>
					<input type='button' name='statechange' id='statechange' value='상태변경'>
				</td>
			</tr>	
		</table>
		
		<br>
		
		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>이름</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
				<th>상태</th>
				<td class="indent5">
					<input type="text" name="state" id="state" class="width-300">
				</td>
				<th>등록일</th>
				<td class="indent5">
					<input type="text" name="createdTime" id="createdTime" class="width-300">
				</td>
			</tr>
		</table>
		<br>
		
		<!-- 속성값 변경 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 속성값 변경
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>OID</th>
				<td class="indent5">
					<input type="text" name="oid" id="oid" class="width-300">
				</td>
			</tr>
			<tr>
				<th>속성명</th>
				<td class="indent5">
					<select name="attrName" id="attrName" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>속성값</th>
				<td class="indent5">
					<input type="text" name="attrValue" id="attrValue" class="width-300">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="변경" title="변경" id="doSubmit" class="blue">
					<input type="button" value="초기화" title="초기화" id="btnReset">
					<input type="button" value="메인화면으로" title="메인화면으로" id="goMain">
				</td>
			</tr>
		</table>
		<br>
		
		<!-- 도면 재변환 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 도면 재변환
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th>등록일자</th>
				<td class="indent5"><input type="text" name="createdFrom" id="createdFrom" class="width-100"> ~ <input type="text" name="createdTo" id="createdTo" class="width-100"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"
					onclick="clearFromTo('createdFrom', 'createdTo')"></td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button"  value="변환요청"  title="변환요청"  class="btnCRUD"  id="createBtn" name="createBtn">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			//

		</script>
	</form>
</body>
</html>