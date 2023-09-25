<%@page import="java.util.List"%>
<%@page import="com.e3ps.common.service.CommonHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.common.iba.AttributeKey"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.ownership.Ownership"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="com.e3ps.common.util.SequenceDao"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="wt.doc.DocumentType"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.rohs.ROHSMaterial"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
	List<Map<String,String>> typeList = (List<Map<String,String>>) request.getAttribute("typeList");
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
		<input type="hidden" name="docType"			id="docType"				value="$$ROHS"/>
		<input type="hidden" name="location"		id="location"				value="/Default/ROHS" />
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> RoHS 정보
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="174">
				<col width="*">
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">물질번호</th>
				<td class="indent5">
					<input type="text" name="rohsNumber" id="rohsNumber" class="width-400">
					&nbsp;<input type="button" value="번호 중복" title="번호 중복" id="NumberCheck">
				</td>
				<th class="req lb">물질명</th>
				<td class="indent5">
					<input type="text" name="rohsName" id="rohsName" class="width-400">
					&nbsp;<input type="button" value="물질명 중복" title="물질명 중복" id="NameCheck">
					<input type="hidden" id="duplicationChk" value="F">
				</td>
			</tr>
			<tr>
				<th class="req lb">결재방식</th>
				<td class="indent5">
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
				<th class="req lb">협력업체</th>
				<td class="indent5">
					<select name="manufacture" id="manufacture" class="width-500">
						<option value="">선택</option>
						<%
						for (NumberCode manufacture : manufactureList) {
						%>
						<option value="<%=manufacture.getCode() %>"><%=manufacture.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5"><textarea name="description" id="description" rows="6"></textarea></td>
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
				<th class="lb">파일구분</th>
				<td class="indent5">
					<select name="fileType" id="fileType" class="width-500">
						<option value="">선택</option>
						<%
						for (Map<String,String> type : typeList) {
						%>
						<option value="<%=type.get("code") %>"><%=type.get("name")%></option>
						<%
						}
						%>
					</select>
				</td>
				<th class="lb">발행일</th>
				<td class="indent5">
					<input type="text" name="publicationDate" id="publicationDate" class="width-100">
				</td>
			</tr>
		</table>
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/change/include_selectPart.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
		</jsp:include>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 관련 RoHS
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="600">
				<col width="150">
				<col width="600">
			</colgroup>
			<tr>
				<th class="lb">관련 RoHS</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/rohs/include_selectRohs.jsp">
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
			var nameChk;
		
			$("#createBtn").click(function() {
				if(isEmpty($("#rohsName").val())) {
					alert("물질명을 입력하세요.");
					return;
				}
				
				if($("#duplicationChk").val()=="F" || nameChk!=$("#rohsName").val()){
					alert("물질명 중복체크 해주세요.");
					return;
				}
				
				if(isEmpty($("#manufacture").val())) {
					alert("협력업체를 선택하세요.");
					return;
				}
				
				let params = new Object();
				params.rohsNumber = $("#rohsNumber").val();
				params.lifecycle = $('input[name=lifecycle]:checked').val();
				params.rohsName = $("#rohsName").val();
				params.manufacture = $("#manufacture").val();
				params.description = $("#description").val();
				params.docType = $("#docType").val();
				params.location = $("#location").val();
				const secondarys = toArray("secondarys");
				params.secondary = secondarys;
				params.fileType = $("#fileType").val();
				params.publicationDate = $("#publicationDate").val();
				params.rohsList = AUIGrid.getGridData(rohsGridID);
				params.partList = AUIGrid.getGridData(partGridID);
				
				if (!confirm("등록 하시겠습니까?")) {
					return;
				}
				
				var url = getCallUrl("/rohs/create");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						location.href = getCallUrl("/rohs/list");
					}else{
						alert(data.msg);
					}
				});
			});
			
			$("#listBtn").click(function() {
				location.href = getCallUrl("/rohs/list");
			});
			
			// 물질명 중복체크
			$("#NameCheck").click(function() {
				var params = new Object();
				if(isEmpty($("#rohsName").val())){
					alert("입력된 물질명이 없습니다.");
					return;
				}
				params.rohsName = $("#rohsName").val();
				var url = getCallUrl("/rohs/rohsCheck");
				call(url, params, function(data) {
					if(data.result){
						if(data.count==0){
							alert("등록 가능한 물질명입니다.");
							$("#duplicationChk").val("S");
							nameChk = $("#rohsName").val();
						}else{
							alert("이미 등록된 물질명입니다.");
							$("#rohsName").val("");
						}
					}else{
						alert(data.msg);
					}
				});
			});
			
			// 번호 중복체크
			$("#NumberCheck").click(function() {
				var params = new Object();
				if(isEmpty($("#rohsNumber").val())){
					alert("입력된 물질번호가 없습니다.");
					return;
				}
				params.rohsName = $("#rohsNumber").val();
				var url = getCallUrl("/rohs/rohsCheck");
				call(url, params, function(data) {
					if(data.result){
						if(data.count==0){
							alert("등록 가능한 물질번호 입니다.");
						}else{
							alert("이미 등록된 물질번호 입니다.");
							$("#rohsNumber").val("");
						}
					}else{
						alert(data.msg);
					}
				});
			});
	
			document.addEventListener("DOMContentLoaded", function() {
				selectbox("manufacture");
				selectbox("fileType");
				date("publicationDate");
				createAUIGrid2(columnsPart);
				AUIGrid.resize(partGridID);
				createAUIGrid6(columnsRohs);
				AUIGrid.resize(rohsGridID);
			});
	
			window.addEventListener("resize", function() {
				AUIGrid.resize(partGridID);
				AUIGrid.resize(rohsGridID);
			});
			
		</script>
	</form>	
</body>
</html>