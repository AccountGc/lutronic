<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
</head>
<body>
	<form>
		<input type="hidden" name="lifecycle" id="lifecycle" value="LC_PART" />
		<input type="hidden" name="fid" id="fid" value="" />
		<input type="hidden" name="location" id="location" value="/Default/PART_Drawing" />

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						도면 정보
					</div>
				</td>
				<td class="right">
					<input type="button" value="등록" title="등록" class="red" onclick="create('false');">
<!-- 					<input type="button" value="임시저장" title="임시저장" onclick="create('true');"> -->
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">도면분류</th>
				<td class="indent5" colspan="3">

					<input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT%>">
					<span id="locationText"> /Default/PART_Drawing </span>
					<input type="button" value="폴더선택" title="폴더선택" onclick="folder();" class="blue">

					<!-- 					<span id="locationName"> -->
					<!--                			/Default/PART_Drawing -->
					<!--                		</span> -->
				</td>
			</tr>
			<tr>
				<th class="req lb">도번</th>
				<td class="indent5">
					<input type="text" name="number" id="number" class="width-500">
				</td>
				<th class="req">도면명</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-500">
				</td>
			</tr>
			<tr>
				<th class="lb">도면설명</th>
				<td class="indent5" colspan="3">
					<input type="text" name="description" id="description" class="width-800">
				</td>
			</tr>
			<tr>
				<th class="req lb">주 첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>

		<br>
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/part/include/part-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" class="red" onclick="create('false');">
					<input type="button" value="임시저장" title="임시저장" onclick="create('true');">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			function create(temp) {
// 				const temprary = JSON.parse(temp); // 임시저장
				const location = toId("location");
				const number = document.getElementById("number");
				const name = document.getElementById("name");
				const primary = document.querySelector("input[name=primary]");

				if (location === "/Default/PART_Drawing") {
					alert("도면분류를 선택하세요.");
					folder();
					return false;
				}

				if (isNull(number.value)) {
					number.focus();
					alert("도번을 입력하세요.");
					return;
				}
				if (isNull(name.value)) {
					name.focus();
					alert("도면명 입력하세요.");
					return;
				}
				if (primary == null) {
					alert("주 첨부파일을 첨부해주세요.");
					return;
				}

				if (!confirm("등록하시겠습니까?")) {
					return false;
				}

				const lifecycle = toId("lifecycle");
				const fid = toId("fid");
				const description = toId("description");
				const secondarys = toArray("secondarys");
				const rows91 = AUIGrid.getGridDataWithState(myGridID91, "gridState");
				const params = {
					lifecycle : lifecycle,
					fid : fid,
					location : location,
					number : number,
					name : name,
					description : description,
					primary : primary == null ? '' : primary.value,
					secondarys : secondarys,
					rows91 : rows91,
// 					temprary : temprary
				}

				const url = getCallUrl("/drawing/create");
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.href = getCallUrl("/drawing/list");
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid91(columns91);
				AUIGrid.resize(partGridID91);
				selectbox("state");
				selectbox("type");
				selectbox("depart");
			});

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			document.addEventListener("click", function(event) {
				hideContextMenu();
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(partGridID91);
			});

			function folder() {
				const location = decodeURIComponent("/Default/PART_Drawing");
				const url = getCallUrl("/folder/popup?location=" + location);
				_popup(url, 500, 600, "n");
			}
		</script>
	</form>
</body>
</html>