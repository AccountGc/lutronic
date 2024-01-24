<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				SAP 검증 결과
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<script type="text/javascript">
	const rtnList = window.rtnList;
	logger(rtnList);
	let myGridID;
	const columns = [ {
		dataField : "ERROR",
		dataType : "string",
		headerText : "성공여부",
		width : 100
	}, {
		dataField : "MATNR_OLD",
		dataType : "string",
		headerText : "이전부모",
		width : 180
	}, {
		dataField : "IDNRK_OLD",
		dataType : "string",
		headerText : "이전자식",
		width : 180
	}, {
		dataField : "MATNR_NEW",
		dataType : "string",
		headerText : "신규부모",
		width : 180
	}, {
		dataField : "IDNRK_NEW",
		dataType : "string",
		headerText : "신규자식",
		width : 180
	}, {
		dataField : "ZIFMSG",
		dataType : "string",
		headerText : "결과 메세지",
		style : "aui-left"
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : false,
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableMovingColumn : true,
			enableFilter : true,
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID, rtnList);
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>