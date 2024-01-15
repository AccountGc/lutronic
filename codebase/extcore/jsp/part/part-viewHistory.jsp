<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTPart part = (WTPart) request.getAttribute("part");
JSONArray data = (JSONArray) request.getAttribute("data");
%>
<style type="text/css">
.selected {
	background-color: rgb(200, 255, 203) !important;
}
</style>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				품목(
				<font color="red">
					<b><%=part.getNumber()%></b>
				</font>
				) 변경이력보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 30px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "number",
		dataType : "string",
		headerText : "품목번호",
		width : 180,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.part_oid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "name",
		dataType : "string",
		headerText : "품목명",
		style : "aui-left"
	}, {
		dataField : "eoNumber",
		dataType : "string",
		headerText : "ECO 번호",
		width : 120,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.eco_oid;
				const url = getCallUrl("/eco/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "eo_createdDate_txt",
		dataType : "string",
		headerText : "ECO 발행일",
		width : 100
	}, {
		dataField : "version",
		dataType : "string",
		headerText : "버전",
		width : 80
	}, {
		dataField : "state",
		dataType : "string",
		headerText : "상태",
		width : 100
	}, {
		dataField : "state",
		dataType : "string",
		headerText : "상태",
		width : 100
	}, {
		dataField : "creator",
		dataType : "string",
		headerText : "등록자",
		width : 100
	}, {
		dataField : "createdDate_txt",
		dataType : "string",
		headerText : "등록일",
		width : 100
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableFilter : true,
			showInlineFilter : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			autoGridHeight : true,
			rowStyleFunction : function(rowIndex, item) {
				if (item.selected == "YES") {
					return "selected";
				}
				return "";
			}
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID,
<%=data%>
	);
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
		selectbox("state");
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	})
</script>