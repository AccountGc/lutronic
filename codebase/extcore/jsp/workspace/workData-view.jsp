<%@page import="wt.fc.Persistable"%>
<%@page import="com.e3ps.workspace.dto.WorkDataDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WorkDataDTO dto = (WorkDataDTO) request.getAttribute("dto");
boolean validate = dto.isValidate();
%>
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
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		<input type="hidden" name="poid" id="poid" value="<%=dto.getPoid()%>">
		<%
			if(dto.isValidate()) {
				// 검증..
		%>
		<input type="hidden" name="validate" id="validate"> 
		<%
			}
		%>

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						결재선 지정
					</div>
				</td>
				<td class="right">
					<%
						if(validate) {
					%>
					<input type="button" value="검증" title="검증" class="blue" onclick="validate();">
					<%
						}
					%>
					<input type="button" value="기안" title="기안" class="red" onclick="_submit();">
					<input type="button" value="뒤로" title="뒤로" class="gray" onclick="history.go(-1);">
				</td>
			</tr>
		</table>
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">번호</th>
				<td class="indent5">
					<a href="javascript:view('<%=dto.getViewUrl()%>');">
						<%=dto.getNumber()%></a>
				</td>
			</tr>
			<tr>
				<th class="lb">제목</th>
				<td class="indent5">
					<a href="javascript:view('<%=dto.getViewUrl()%>');">
						<%=dto.getName()%></a>
				</td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
			</tr>
			<tr>
				<th class="lb">작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">결재의견</th>
				<td class="indent5">
					<div class="textarea-auto">
						<textarea name="description" id="description" rows="6"></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재선 지정</th>
				<td>
					<jsp:include page="/extcore/jsp/workspace/include/approval-register.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">외부 메일 지정</th>
				<td>
					<jsp:include page="/extcore/jsp/workspace/include/mail-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="update" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="기안" title="기안" class="red" onclick="_submit();">
					<input type="button" value="뒤로" title="뒤로" class="gray" onclick="history.go(-1);">
				</td>
			</tr>
		</table>
	</form>
	<script type="text/javascript">
	
		// SAP 전송전 검증 단계
		function validate() {
			const url = getCallUrl("/sap/validate");
			const params = new Object();
			parent.openLayer();
			call(url, params, function(data) {
				alert(data.msg);
				if(data.result) {
					
				}
				parent.closeLayer();
			});
		}
	
		function view(url) {
			_popup(url, "", "", "f");
		}
	
		function _submit() {
			const oid = document.getElementById("oid").value;
			const description = document.getElementById("description").value;
			// 			const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
			const addRows8 = AUIGrid.getGridData(myGridID8);
			if (addRows8.length === 0) {
				alert("결재선을 지정하세요.");
				addRows8();
				return false;
			}

			// 			const external = AUIGrid.getGridDataWithState(myGridID9, "gridState");
			const external = AUIGrid.getGridData(myGridID9);
			const url = getCallUrl("/workData/_submit");
			const params = {
				oid : oid,
				external : external,
				description : description
			};
			toRegister(params, addRows8); // 결재선 세팅
			if (!confirm("기안 하시겠습니까?")) {
				return false;
			}

			parent.openLayer();
			logger(params);
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					parent.updateWorkData();
					parent.updateWorkspace();
					document.location.href = getCallUrl("/workData/list");
				}
				parent.closeLayer();
			})

		}

		function read() {
			const oid = document.getElementById("oid").value;
			const url = getCallUrl("/workData/read?oid=" + oid);
			parent.openLayer();
			call(url, null, function(data) {
				if (data.result) {
// 					opener.loadGridData();
				} else {
					alert(data.msg);
				}
				parent.closeLayer();
			}, "GET");
		}

		document.addEventListener("DOMContentLoaded", function() {
			toFocus("description");
			createAUIGrid8(columns8);
			createAUIGrid9(columns9);
			AUIGrid.resize(myGridID8)
			AUIGrid.resize(myGridID9);
			read();
			autoTextarea();
		})

		window.addEventListener("resize", function() {
			AUIGrid.resize(myGridID8);
			AUIGrid.resize(myGridID9);
		});
	</script>
</body>
</html>