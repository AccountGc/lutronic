<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) request.getAttribute("list");
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
		<input type="hidden" name="oid" id="oid">

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						관리자 메뉴
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>OID</th>
				<td class="indent5">
					<input type="button" name="delete" id="delete" value="삭제" class="red">
				</td>
			</tr>
			<tr>
				<th>라이프사이클 템플릿</th>
				<td class="indent5">
					새 라이프 사이클 템플릿
					<input type="text" name="lifecycle" id="lifecycle" class="width-300">
					<input type='button' name='reassign' id='reassign' value='reassign'>
				</td>
			</tr>
			<tr>
				<th>상태</th>
				<td>
					TERMINATE :&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="terminate" value="true" checked="checked">
						<div class="state p-success">
							<label>
								<b>TRUE</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="terminate" value="false">
						<div class="state p-success">
							<label>
								<b>FALSE</b>
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
						<img src="/Windchill/extcore/images/header.png">
						속성값 변경
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
						<%
						for (Map<String, String> map : list) {
						%>
						<option value="<%=map.get("key")%>"><%=map.get("name") %></option>
						<%
						}
						%>
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
					<input type="button" value="변경" title="변경" class="blue" onclick="modify();">
					<input type="button" value="초기화" title="초기화">
				</td>
			</tr>
		</table>
		<br>

		<!-- 도면 재변환 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						도면 재변환
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
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="변환요청" title="변환요청" class="blue" onclick="publish();">
				</td>
			</tr>
		</table>

		<!-- 패스워드 변경 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						비밀번호 변경
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
				<th>변경 비밀번호</th>
				<td class="indent5">
					<input type="password" name="password" class="width-200">
				</td>
			</tr>
			<tr>
				<th>변경 비밀번호 확인</th>
				<td class="indent5">
					<input type="password" name="repassword" class="width-200">
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="수정" title="수정" class="blue" onclick="update();">
					<input type="button" value="뒤로" title="뒤로" onclick="history.go(-1);">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				selectbox("attrName");
				twindate("created");
			})

			function publish() {
				const createdFrom = document.getElementById("createdFrom").value;
				const createdTo = document.getElementById("createdTo").value;

				if (createdFrom === "") {
					alert("등록일(시작0)을 입력(선택) 하세요.");
					return false;
				}

				if (createdTo === "") {
					alert("등록일(끝)을 입력(선택) 하세요.");
					return false;
				}

				const params = {
					predate : createdFrom,
					postdate : createdTo
				};

				if (!confirm(createdFrom + " ~ " + createdTo + " 기간동안의 도면파일을 재변환 하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/groupware/publish");
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.reload();
					}
					parent.closeLayer();
				})
			}
		</script>
	</form>
</body>
</html>