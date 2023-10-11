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
	ArrayList<Map<String, String>> modelInfo =	dto.getModelInfo();
%>

</head>
<body>
	<form>
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						EO 수정
					</div>
				</td>
				<td class="right">
					<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
					<input type="button" value="이전" title="이전" class="blue" onclick="history.back();">
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
				<td class="indent5" >
					<input type="text" name="name" id="name" class="width-400" value="<%=dto.getName()%>">
				</td>
				<th class="req lb">EO 구분</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="eoType" value="DEV" <% if("DEV".equals(dto.getEoType())){%>checked="checked"<%}%>>
						<div class="state p-success">
							<label>
								<b>개발</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="eoType" value="PRODUCT" <% if("PRODUCT".equals(dto.getEoType())){%>checked="checked"<%}%>>
						<div class="state p-success">
							<label>
								<b>양산</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<th class="req lb">제품명</th>
				<td colspan="3" class="indent5 pt5">
					<jsp:include page="/extcore/jsp/admin/code/include/code-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
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
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
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
		</table>

		<!-- 	관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="150" name="height" />
		</jsp:include>

		<!-- 	설변 활동 -->
		<jsp:include page="/extcore/jsp/change/activity/include/activity-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
		</jsp:include>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
					<input type="button" value="이전" title="이전" class="blue" onclick="back();">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			var modelInfo=[];
			<%= dto.getRows104()%>
			document.addEventListener("DOMContentLoaded", function() {
				<% if(modelInfo.size()!=0){
					for(Map<String,String> item :modelInfo ){%>
							var map ={
									  "name" :"<%= item.get("name")%>"
									, "code" : "<%= item.get("code")%>"
									, "sort" : "<%= item.get("sort")%>"
									, "description" :"<%= item.get("description")==null?"":item.get("description")%>"
									, "enabled" : "<%= item.get("enabled")%>"
									, "oid" : "<%= item.get("oid")%>"
							};
							modelInfo.push(map);
					<%}
				}%>
				toFocus("name");
				createAUIGrid104(columns104);
				createAUIGrid90(columns90);
				createAUIGrid300(columns300);
				createAUIGrid200(columns200);
				AUIGrid.resize(myGridID104);
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID300);
				AUIGrid.resize(myGridID200);
				
				
				AUIGrid.setGridData(myGridID300, modelInfo);
			});
			
		
			
			function modify() {

				if (!confirm("수정 하시겠습니까?")) {
					return false;
				}

				const name = toId("name");
				const eoCommentA = toId("eoCommentA");
				const eoCommentB = toId("eoCommentB");
				const eoCommentC = toId("eoCommentC");
				const secondarys = toArray("secondarys");
				const eoType = document.querySelector("input[name=eoType]:checked").value;
				// 완제품
				const rows104 = AUIGrid.getGridDataWithState(myGridID104, "gridState");
				// 관련문서
				const rows90 = AUIGrid.getGridDataWithState(myGridID90, "gridState");
				// ECA
				const rows200 = AUIGrid.getGridDataWithState(myGridID200, "gridState");
				// 제품
				var  rows300 = AUIGrid.getGridDataWithState(myGridID300, "gridState");
				rows300 = rows300.filter(function(item){
					return item.gridState!=	"removed";
				});
				
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
					oid : $("#oid").val()
				}
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
// 					if (data.result) {
						opener.loadGridData();
						self.close();
						
// 						document.location.href = getCallUrl("/eo/list");
// 					} else {
// 						parent.closeLayer();
// 					}
				});
			}

			function back(){
				document.location.href = getCallUrl("/eo/view?oid=" + $("#oid").val());
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
				AUIGrid.resize(myGridID104);
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID300);
				AUIGrid.resize(myGridID200);
			});
		</script>
	</form>
</body>
</html>