<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.development.service.DevelopmentHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
// String moduleType = request.getParameter("moduleType");
// JSONArray json = DevelopmentHelper.manager.mockupVersionInfo();
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 버전 정보
			</div>
		</td>
	</tr>
</table>
<div id="grid_ver" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let verGridID;
	const columnVer = [ {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		width : 180,
// 		renderer : {
// 			type : "LinkRenderer",
// 			baseUrl : "javascript",
// 			jsCallback : function(rowIndex, columnIndex, value, item) {
// 				const oid = item.oid;
// 				const url = getCallUrl("/project/info?oid=" + oid);
// 				popup(url);
// 			}
// 		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 180,
	}, {
		dataField : "createDate",
		headerText : "등록일",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGridVer(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			rowCheckToRadio : true,
			fillColumnSizeMode: true,
		}
		
		verGridID = AUIGrid.create("#grid_ver", columnLayout, props);
<%-- 		AUIGrid.setGridData(verGridID, <%=json%>); --%>
		
		var json = [{
		    "number" : 3010357700,
		    "name" : "MOUNT_A-MOTOR",
		    "creator" : "관리자",
		    "createDate" : "2023-08-09"
		}, {
			"number" : 3010357701,
		    "name" : "MOUNT_A-MOTOR1",
		    "creator" : "관리자",
		    "createDate" : "2023-08-09"
		}, {
			"number" : 3010357702,
		    "name" : "MOUNT_A-MOTOR2",
		    "creator" : "관리자",
		    "createDate" : "2023-08-09"
		}, {
			"number" : 3010357703,
		    "name" : "MOUNT_A-MOTOR3",
		    "creator" : "관리자",
		    "createDate" : "2023-08-09"
		}]
		AUIGrid.setGridData(verGridID, json);
	}
	
</script>