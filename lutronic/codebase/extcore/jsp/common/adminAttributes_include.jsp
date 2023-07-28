<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.common.service.CommonHelper"%>
<%
String oid = (String) request.getParameter("oid");
String module = (String) request.getParameter("module");
JSONArray json = CommonHelper.manager.include_adminAttribute(oid, module);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관리자 속성
			</div>
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 350px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
			let adminGridID;
			const columnsAdmin = [ {
				dataField : "badT",
				headerText : "badT",
				dataType : "string",
				width : 120,
				}]

			function createAUIGridAdmin(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					softRemoveRowMode : false,
					autoGridHeight : true,
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID, <%= json %>);
			}
			
	</script>