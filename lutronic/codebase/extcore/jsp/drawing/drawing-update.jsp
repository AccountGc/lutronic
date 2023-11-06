<%@page import="com.e3ps.drawing.beans.EpmData"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EpmData dto = (EpmData) request.getAttribute("dto");
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
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		<input type="hidden" name="location" id="location" value="/Default/PART_Drawing">
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 도면 수정
					</div>
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
					<input type="hidden" name="location" id="location" value="<%=dto.getLocation()%>">
					<span id="locationText"><%=dto.getLocation()%></span>
					<input type="button" value="폴더선택" title="폴더선택" onclick="folder();" class="blue">
				</td>
			</tr>
			<tr>
				<th class="req lb">도번</th>
				<td class="indent5">
					<input type="text" name="number" id="number" class="width-500" value="<%=dto.getNumber()%>" readonly>
				</td>
				<th class="req lb">도면명</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-500" value="<%=dto.getName()%>" readonly>
				</td>
			</tr>
			<tr>
				<th class="lb">도면설명</th>
				<td class="indent5"  colspan="3">
					<textarea name="description" id="description" rows="5"><%=dto.getDescription()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">주 첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="modify" name="mode"/>
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="modify" name="mode"/>
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">결재</th>
				<td colspan="5">
					<jsp:include page="/extcore/jsp/workspace/include/approval-register.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="update" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/change/include_selectPart.jsp">
			<jsp:param value="<%=dto.getOid() %>" name="oid" />
			<jsp:param value="update" name="mode" />
			<jsp:param value="drawing" name="moduleType" />
			<jsp:param value="true" name="multi" />
		</jsp:include>
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button"  value="수정"  title="수정"  class="blue"  id="updateBtn">
					<input type="button" value="이전" title="이전" onclick="javascript:history.back();">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			function folder() {
				const location = decodeURIComponent("/Default/PART_Drawing");
				const url = getCallUrl("/folder/popup?location=" + location);
				_popup(url, 500, 600, "n");
			}
			
			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid8(columns8);
				AUIGrid.resize(myGridID8);
				createAUIGrid2(columnsPart);
				AUIGrid.resize(partGridID);
			});
		
			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID8);
				AUIGrid.resize(partGridID);
			});
			
			$("#updateBtn").click(function(){
				const primary = document.querySelector("input[name=primary]");
				if(primary == null){
					alert("주 첨부파일을 첨부해주세요.");
					return;
				}
				
				if (!confirm("수정 하시겠습니까?")) {
					return;
				}
				
				const oid = toId("oid");
				const location = toId("location");
				const description = toId("description");
				const secondarys = toArray("secondarys");
				const partList = AUIGrid.getGridData(partGridID);
				
				const params = {
					oid : oid,
					location : location,
					description : description,
					primary : primary.value,
					secondarys : secondarys,
					partList : partList
				}
				
				var url = getCallUrl("/drawing/update");
				call(url, params, function(data) {
					alert(data.msg);
					if(data.result){
						opener.loadGridData();
						self.close();
					}
				});
			});
		</script>
	</form>	
</body>
</html>	