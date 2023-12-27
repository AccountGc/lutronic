<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
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
		headerText : "도면번호",
		dataType : "string",
		width : 150,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "name",
		headerText : "도면명",
		dataType : "string",
		style : "aui-left",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 90,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 110,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "string",
		width : 110,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 110,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "modifiedDate",
		headerText : "수정일",
		dataType : "string",
		width : 110,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "primary",
		headerText : "주 첨부파일",
		dataType : "string",
		width : 90,
		renderer : {
			type : "TemplateRenderer"
		},
		filter : {
			showIcon : false,
			inline : false
		},
	}, {
		dataField : "secondary",
		headerText : "첨부파일",
		dataType : "string",
		width : 110,
		renderer : {
			type : "TemplateRenderer"
		},
		filter : {
			showIcon : false,
			inline : false
		},
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
		AUIGrid.setGridData(myGridID50, <%=DrawingHelper.manager.allIterationsOf(oid)%>);
	}
</script>