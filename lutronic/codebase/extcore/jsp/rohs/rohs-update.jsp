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
				<td class="indent5" colspan="3">
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