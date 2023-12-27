<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				문서 채번보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 530px; border-top: 1px solid #3180c3;"></div>

<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "classType1Name",
		headerText : "대분류",
		dataType : "string",
		width : 100,
		cellMerge : true,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "classType2Name",
		headerText : "중분류",
		dataType : "string",
		width : 300,
		cellMerge : true,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "classType3Name",
		headerText : "소분류",
		dataType : "string",
		width : 150,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "model",
		headerText : "프로젝트코드",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "lastNumber",
		headerText : "최종 채번 번호",
		dataType : "string",
		style : "aui-left",
		filter : {
			showIcon : true,
			inline : true
		},
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			enableCellMerge : true,
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
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		logger(<%=list%>);
		AUIGrid.setGridData(myGridID,
<%=list%>
	);
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>