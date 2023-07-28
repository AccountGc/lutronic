<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.development.service.DevelopmentHelper"%>
<%
String oid = (String) request.getParameter("oid");
boolean enAbled = (boolean) request.getAttribute("enAbled");
JSONArray taskList = DevelopmentHelper.manager.viewTaskList(oid);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				TASK
			</div>
		</td>
		<% if(enAbled){ %>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="update('modify');">
		</td>
		<% } %>
	</tr>
</table>
<div id="grid_taskWrap" style="height: 350px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
			let myGridID2;
			const columns2 = [ {
				dataField : "name",
				headerText : "TASK명",
				dataType : "string",
				width : 120,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
// 					jsCallback : function(rowIndex, columnIndex, value, item) {
// 						const oid = item.oid;
// 						const url = getCallUrl("/doc/view?oid=" + oid);
// 						popup(url, 1600, 800);
// 					}
				},
			}, {
				dataField : "state",
				headerText : "상태",
				dataType : "string",
				style : "aui-left",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
// 					jsCallback : function(rowIndex, columnIndex, value, item) {
// 						const oid = item.oid;
// 						const url = getCallUrl("/doc/view?oid=" + oid);
// 						popup(url, 1600, 800);
// 					}
				},
			}, {
				dataField : "description",
				headerText : "설명",
				dataType : "string",
				width : 100,
			} ]

			function createAUIGrid2(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					softRemoveRowMode : false,
					autoGridHeight : true,
				}
				myGridID2 = AUIGrid.create("#grid_taskWrap", columnLayout, props);
				AUIGrid.setGridData(myGridID2, <%= taskList %>);
			}
	</script>