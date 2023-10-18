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
	<form id="form">
		<input type="hidden" name="lifecycle"	id="lifecycle" 	value="LC_PART" />
		<input type="hidden" name="fid" 		id="fid"		value="" />
		<input type="hidden" name="location" 	id="location"	value="/Default/PART_Drawing" />

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 도면 정보
					</div>
				</td>
				<td class="right">
						<input type="button" value="기안" title="기안" class="red" onclick="create('false');">
						<input type="button" value="임시저장" title="임시저장" class="" onclick="create('true');">
					</td>
				</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="180">
				<col width="*">
				<col width="180">
				<col width="*">
			</colgroup>
			<tr>
				<th>도면분류 <span style="color:red;">*</span></th>
				<td class="indent5" colspan="3">
					<span id="locationName">
               			/Default/PART_Drawing
               		</span>
				</td>
			</tr>
			<tr>
				<th>도번 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="number" id="number" class="width-500">
				</td>
				<th>도면명 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-500">
				</td>
			</tr>
			<tr>
				<th>도면설명</th>
				<td class="indent5"  colspan="3">
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
			<tr>
				<th class="lb">결재</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/workspace/include/approval-register.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		
		<br>
		<jsp:include page="/extcore/jsp/change/include_selectPart.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
		</jsp:include>
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="기안" title="기안" class="red" onclick="create('false');">
					<input type="button" value="임시저장" title="임시저장" class="" onclick="create('true');">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			function create(temp) {
				
				const temprary = JSON.parse(temp);
				const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
				
				if ($("#location").val() == "") {
					alert("도면분류를 선택하세요.");
					return;
				}
				if($("#number").val() == "") {
					alert("도번을 입력하세요.");
					return;
				}
				
				if($("#name").val() == "") {
					alert("도면명 입력하세요.");
					return;
				}
				
				if (!confirm("등록 하시겠습니까?")) {
					return;
				}
				
				if (temprary) {
					if (!confirm("임시저장하시겠습니까??")) {
						return false;
					}
					
					if(addRows8){
						alert("결재선 지정을 해지해주세요.")
						return false;
					}
					
				} else {
					if (!confirm("등록하시겠습니까?")) {
						return false;
					}
				}
				
				var params = _data($("#form"));
				toRegister(params, addRows8); // 결재선 세팅
				parent.openLayer();
				var url = getCallUrl("/drawing/create");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
					}else{
						alert(data.msg);
					}
				});
			}
			
			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid2(columnsPart);
				createAUIGrid8(columns8);
				AUIGrid.resize(partGridID);
				AUIGrid.resize(myGridID8);
				selectbox("state");
				selectbox("type");
				selectbox("depart");
			});

			function exportExcel() {
// 				const exceptColumnFields = [ "primary" ];
// 				const sessionName = document.getElementById("sessionName").value;
// 				exportToExcel("문서 리스트", "문서", "문서 리스트", exceptColumnFields, sessionName);
			}

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
				AUIGrid.resize(partGridID);
				AUIGrid.resize(myGridID8);
			});
		</script>
	</form>
</body>
</html>