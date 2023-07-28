<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.development.service.DevelopmentHelper"%>
<%
String oid = (String) request.getParameter("oid");
JSONArray userList = DevelopmentHelper.manager.viewUserList(oid);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				구성원
			</div>
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 350px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "Role",
				headerText : "역할",
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
				dataField : "department",
				headerText : "부서명",
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
				dataField : "name",
				headerText : "이름",
				dataType : "string",
				width : 100,
			}, {
				dataField : "duty",
				headerText : "직위",
				dataType : "string",
				width : 100,
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					softRemoveRowMode : false,
					autoGridHeight : true,
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID, <%= userList %>);
			}
			
	</script>