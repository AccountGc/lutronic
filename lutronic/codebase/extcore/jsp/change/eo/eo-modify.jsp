<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.change.eo.dto.EoDTO"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>

<%
	EoDTO dto = (EoDTO) request.getAttribute("dto");
	boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>

</head>
<body>
	<form>
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		<input type="hidden" name="eoType" id="eoType" value="<%=dto.getEoType()%>">
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						EO 수정
					</div>
				</td>
				<td class="right">
					<input type="button" value="수정" title="수정" class="blue" onclick="update('false');">
					<input type="button" value="임시저장" title="임시저장" onclick="update('true');">
					<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
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
				<th class="req lb">EO 제목</th>
				<td class="indent5" colspan="3">
					<input type="text" name="name" id="name" class="width-400" value="<%=dto.getName()%>">
				</td>
			</tr>
			<tr>
				<th class="req lb">제품명</th>
				<td colspan="3" class="indent5 pt5">
					<jsp:include page="/extcore/jsp/admin/code/include/code-include.jsp">
						<jsp:param value="<%= dto.getOid() %>" name="oid" />
						<jsp:param value="update" name="mode" />
						<jsp:param value="insert300" name="method" />
						<jsp:param value="MODEL" name="codeType" />
						<jsp:param value="true" name="multi" />
						<jsp:param value="150" name="height" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">완제품 품목</th>
				<td colspan="3" class="indent5 pt5">
					<jsp:include page="/extcore/jsp/change/include/complete-part-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="update" name="mode" />
						<jsp:param value="true" name="multi" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">제품 설계 개요</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentA" id="eoCommentA" rows="6"><%=dto.getEoCommentA()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">특기사항</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentB" id="eoCommentB" rows="6"><%=dto.getEoCommentB()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">기타사항</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentC" id="eoCommentC" rows="6"><%=dto.getEoCommentC()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="modify" name="mode"/>
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">결재</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/workspace/include/approval-register.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">외부 메일 지정</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/workspace/include/mail-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="update" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>

		<!-- 	관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="update" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<!-- 	설변 활동 -->
		<jsp:include page="/extcore/jsp/change/activity/include/activity-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="update" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
		</jsp:include>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="수정" title="수정" class="blue" onclick="update('false');">
					<input type="button" value="임시저장" title="임시저장" onclick="update('true');">
					<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				toFocus("name");
				createAUIGrid8(columns8);
				createAUIGrid9(columns9);
				createAUIGrid300(columns300);
				createAUIGrid104(columns104);
				createAUIGrid90(columns90);
				createAUIGrid200(columns200);
				AUIGrid.resize(myGridID8);
				AUIGrid.resize(myGridID9);
				AUIGrid.resize(myGridID300);
				AUIGrid.resize(myGridID104);
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID200);
			});
			
			function update(temp) {
				// 임시저장
				const temprary = JSON.parse(temp);
				// 결재선
				const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
				
				if (temprary) {
					if (!confirm("임시저장하시겠습니까??")) {
						return false;
					}
					
					if (addRows8.length > 0) {
						alert("결재선 지정을 해지해주세요.")
						return false;
					}
					
				} else {
					if (!confirm("수정 하시겠습니까?")) {
						return false;
					}
				}

				const name = toId("name");
				const eoCommentA = toId("eoCommentA");
				const eoCommentB = toId("eoCommentB");
				const eoCommentC = toId("eoCommentC");
				const secondarys = toArray("secondarys");
				const eoType = document.querySelector("input[name=eoType]:checked").value;
				// 완제품
				var rows104 = AUIGrid.getGridDataWithState(myGridID104, "gridState");
				rows104 = rows104.filter(function(item){
					return item.gridState!=	"removed";
				});
				
				// 관련문서
				var rows90 = AUIGrid.getGridDataWithState(myGridID90, "gridState");
				rows90 = rows90.filter(function(item){
					return item.gridState!=	"removed";
				});
				// ECA
				var rows200 = AUIGrid.getGridDataWithState(myGridID200, "gridState");
				rows200 = rows200.filter(function(item){
					return item.gridState!=	"removed";
				});
				// 제품
				var  rows300 = AUIGrid.getGridDataWithState(myGridID300, "gridState");
				rows300 = rows300.filter(function(item){
					return item.gridState!=	"removed";
				});
				// 외부메일
				const external = AUIGrid.getGridDataWithState(myGridID9, "gridState");
				
				const url = getCallUrl("/eo/modify");
				const params = {
					name : name,
					eoCommentA : eoCommentA,
					eoCommentB : eoCommentB,
					eoCommentC : eoCommentC,
					eoType : eoType,
					secondarys : secondarys,
					rows104 : rows104,
					rows90 : rows90,
					rows200 : rows200,
					rows300 : rows300,
					external : external,
					temprary : temprary,
					oid : $("#oid").val()
				}
				
				toRegister(params, addRows8); // 결재선 세팅
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						opener.loadGridData();
						self.close();
					}
				});
			}

			function finder(id) {
				axdom("#" + id).bindSelector({
					reserveKeys : {
						options : "list",
						optionValue : "oid",
						optionText : "name"
					},
					optionPrintLength : "all",
					onsearch : function(id, obj, callBack) {
						const value = document.getElementById(id).value;
						const params = new Object();
						const url = getCallUrl("/code/finder");
						params.codeType = id.toUpperCase();
						params.value = value;
						params.obj = obj;
						call(url, params, function(data) {
							callBack({
								options : data.list
							})
						})
					},
					onchange : function() {
						const id = this.targetID;
						const value = this.selectedOption.oid
						document.getElementById(id + "_oid").value = value;
					},
				})
			}

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID8);
				AUIGrid.resize(myGridID9);
				AUIGrid.resize(myGridID300);
				AUIGrid.resize(myGridID104);
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID200);
			});
		</script>
	</form>
</body>
</html>