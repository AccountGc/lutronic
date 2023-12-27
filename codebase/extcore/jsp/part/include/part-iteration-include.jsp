<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%
String oid = request.getParameter("oid");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				버전 이력
			</div>
		</td>
	</tr>
</table>
<div id="grid50" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID50;
	const columns50 = [ {
		dataField : "number",
		headerText : "부품번호",
		dataType : "string",
		width : 150,
	}, {
		dataField : "name",
		headerText : "부품명",
		dataType : "string",
		style : "aui-left",
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 90,
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 110,
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "string",
		width : 110,
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 110,
	}, {
		dataField : "modifiedDate",
		headerText : "수정일",
		dataType : "string",
		width : 110,
	}, {
		dataField : "primary",
		headerText : "주 첨부파일",
		dataType : "string",
		width : 90,
		renderer : {
			type : "TemplateRenderer"
		},
	}, {
		dataField : "secondary",
		headerText : "첨부파일",
		dataType : "string",
		width : 110,
		renderer : {
			type : "TemplateRenderer"
		},
	}, {
		dataField : "note",
		headerText : "수정사유",
		dataType : "string",
	} ]

	function createAUIGrid50(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableFilter : true,
			autoGridHeight : true
		}
		myGridID50 = AUIGrid.create("#grid50", columnLayout, props);
		AUIGrid.setGridData(myGridID50,
<%=PartHelper.manager.allIterationsOf(oid)%>
	);
	}
</script>