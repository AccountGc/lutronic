<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%
	Map<String, Object> params = new HashMap<String, Object>();
	String oid = (String) request.getParameter("oid");
	String moduleType = request.getParameter("moduleType");
	String epmType = request.getParameter("epmType");
	String title = request.getParameter("title");
	String paramName = request.getParameter("paramName");
	params.put("oid", oid);
	params.put("moduleType", moduleType);
	params.put("epmType", epmType);
	params.put("title", title);
	params.put("paramName", paramName);
	JSONArray includeDrawingList = DrawingHelper.manager.include_DrawingList(params);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<%= title %>
			</div>
		</td>
	</tr>
</table>
<div id="grid_drawing_Wrap" style="height: 350px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let drawingGridID;
	const columnsDrawing = [ {
		dataField : "number",
		headerText : "도면번호",
		dataType : "string",
		width : 120,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
	},
	}, {
		dataField : "name",
		headerText : "도면명",
		dataType : "string",
		style : "aui-left",
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 100,
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "description",
		headerText : "수정일",
		dataType : "string",
		width : 100,
	}]

	function createAUIGridDrawing(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : false,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			softRemoveRowMode : false,
			autoGridHeight : true,
		}
		myGridID1 = AUIGrid.create("#grid_drawing_Wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID1,<%= includeDrawingList %>);
	}
</script>