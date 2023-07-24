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
		<input type="hidden" name="fid" 			id="fid" 					value="">
		<input type="hidden" name="location" 		id="location" 				value="/Default/금형문서">
		<input type="hidden" name="documentType" 	id="documentType" 			value="$$MMDocument">
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 금형 정보
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>결재방식 <span class="red">*</span></th>
				<td class="indent5" colspan="3">
					<div class="pretty p-switch">
						<input type="radio"name="lifecycle" value="LC_Default" checked="checked">
						<div class="state p-success">
							<label> <b>기본결재</b>
							</label>
						</div>
					</div> &nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="lifecycle" value="LC_Default_NonWF">
						<div class="state p-success">
							<label> <b>일괄결재</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<th>문서명 <span style="color:red;">*</span></th>
				<td class="indent5" colspan="3">
					<input type="text" name="docName" id="docName" class="width-500">
				</td>
			</tr>
			<tr>
				<th>Manufacturer</th>
				<td class="indent5">
					<select name="manufacture" id="manufacture" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
					</select>
				</td>
				<th>금형타입 <span class="red">*</span></th>
				<td class="indent5">
					<select name="moldtype" id="moldtype" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>업체자제금형번호</th>
				<td class="indent5">
					<input type="text" name="moldnumber" id="moldnumber" class="width-500">
				</td>
				<th>금형개발비</th>
				<td class="indent5">
					<input type="text" name="moldcost" id="moldcost" class="width-500">
				</td>
			</tr>
			<tr>
				<th>내부 문서번호 <br>(자산등록번호)</th>
				<td class="indent5">
					<input type="text" name="interalnumber" id="interalnumber" class="width-500">
				</td>
				<th>부서</th>
				<td class="indent5">
					<select name="deptcode" id="deptcode" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>문서설명</th>
				<td class="indent5" colspan="3">
					<input type="text" name="description" id="description" class="width-300">
				</td>
			</tr>
			<tr>
				<th class="req lb">주 첨부파일 <span class="red">*</span></th>
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
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 관련 품목
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>관련 품목</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/change/include_selectPart.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<br>
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 관련 문서
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>관련 문서</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/document/include_selectDocument.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button"  value="등록"  title="등록"  class="blue"  id="createBtn">
					<input type="button" value="초기화" title="초기화" id="resetBtn">
					<input type="button" value="목록" title="목록" id="listBtn">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			$("#createBtn").click(function() {
				if($("#lifecycle").val() == "") {
					alert("결재방식을 선택하세요.");
					return;
				}
				
				if($("#docName").val() == "") {
					alert("문서명을 입력하세요.");
					return;
				}
				
// 				if($("#moldtype").val() == "") {
// 					alert("금형타입을 선택하세요.");
// 					return;
// 				}
				
// 				if($("#PRIMARY").val() == "") {
// 					alert("주 첨부파일을 추가해주세요.");
// 					return;
// 				}
				
				if (!confirm("등록 하시겠습니까?")) {
					return;
				}
				
				var params = _data($("#form"));
				var url = getCallUrl("/mold/create");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						location.href = getCallUrl("/mold/list");
					}else{
						alert(data.msg);
					}
				});
			});
			
			$("#listBtn").click(function() {
				location.href = getCallUrl("/mold/list");
			});
		
			document.addEventListener("DOMContentLoaded", function() {
				selectbox("manufacture");
				selectbox("moldtype");
				selectbox("deptcode");
				
				createAUIGrid2(columnsPart);
				AUIGrid.resize(partGridID);
				createAUIGrid4(columnsDoc);
				AUIGrid.resize(docGridID);
				document.getElementById("name").focus();
			});
			
			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
				AUIGrid.resize(partGridID);
				AUIGrid.resize(docGridID);
			});

		</script>
	</form>
</body>
</html>