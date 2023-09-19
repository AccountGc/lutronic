<%@page import="com.e3ps.rohs.dto.RohsData"%>
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
	List<Map<String,Object>> contentList = (List<Map<String,Object>>) request.getAttribute("contentList");
	RohsData data = (RohsData) request.getAttribute("data");
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
		<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
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
				<th class="req lb">물질명</th>
				<td class="indent5">
					<input type="text" name="rohsName" id="rohsName" class="width-400" value="<%=data.getName()%>">
					&nbsp;<input type="button" value="물질명 중복" title="물질명 중복" id="NameCheck">
					<input type="hidden" id="duplicationChk" value="F">
				</td>
				<th class="req lb">협력업체</th>
				<td class="indent5">
					<select name="manufacture" id="manufacture" class="width-500">
							<option value="">선택</option>
							<%
							for (NumberCode manufacture : manufactureList) {
							%>
								<option value="<%=manufacture.getCode() %>" <%if (manufacture.getCode().equals(data.getManufacture())) {%> selected="selected" <%}%>><%=manufacture.getName()%></option>
							<%
							}
							%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5"><textarea name="description" id="description" rows="6"><%=data.getDescription()%></textarea></td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="<%=data.getOid() %>" name="oid" />
						<jsp:param value="rohs" name="moduleType"/>
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
						<option value="<%=type.get("code") %>" <%if (type.get("code").equals(data.getFileType())) {%> selected="selected" <%}%>><%=type.get("name")%></option>
						<%
						}
						%>
					</select>
				</td>
				<th class="lb">발행일</th>
				<td class="indent5">
					<input type="text" name="publicationDate" id="publicationDate" class="width-100" value="<%=data.getPublicationDate()%>">
				</td>
			</tr>
		</table>
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/change/include_selectPart.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
			<jsp:param value="update" name="mode" />
			<jsp:param value="rohs" name="moduleType"/>
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
						<jsp:param value="<%=data.getOid() %>" name="oid" />
						<jsp:param value="composition" name="roleType"/>
						<jsp:param value="update" name="mode" />
						<jsp:param value="rohs" name="module" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button"  value="수정"  title="수정"  class="blue"  id="updateBtn">
					<input type="button" value="이전" title="이전" onclick="javascript:history.back();">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
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
			
			var nameChk = "<%=data.getName()%>";
			$("#updateBtn").click(function(){
				if(isEmpty($("#rohsName").val())) {
					alert("물질명을 입력하세요.");
					return;
				}
				if(nameChk==$("#rohsName").val()){
					$("#duplicationChk").val("S");
				}
				
				if($("#duplicationChk").val()=="F" || nameChk!=$("#rohsName").val()){
					alert("물질명 중복체크 해주세요.");
					return;
				}
				
				if(isEmpty($("#manufacture").val())) {
					alert("협력업체를 선택하세요.");
					return;
				}
				
				const secondarys = toArray("secondarys");
				if(secondarys.length>0){
					if(isEmpty($("#fileType").val())) {
						alert("파일구분을 선택하세요.");
						return;
					}
					if(isEmpty($("#publicationDate").val())) {
						alert("발행일을 입력하세요.");
						return;
					}
				}
				
				if (!confirm("수정 하시겠습니까?")) {
					return;
				}
				
				let params = new Object();
				params.oid = $("#oid").val();
				params.description = $("#description").val();
				params.manufacture = $("#manufacture").val();
				params.secondary = secondarys;
				params.fileType = $("#fileType").val();
				params.publicationDate = $("#publicationDate").val();
				params.rohsList = AUIGrid.getGridData(rohsGridID);
				params.partList = AUIGrid.getGridData(partGridID);
				
				var url = getCallUrl("/rohs/update");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						opener.loadGridData();
						self.close();
					}else{
						alert(data.msg);
					}
				});
			});
			
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
			
		</script>
	</form>	
</body>
</html>