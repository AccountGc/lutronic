<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String partNumber = request.getParameter("partNumber");
%>
<input type="hidden" name="partNumber" id="partNumber"  value="<%= partNumber %>"/>
<input type="hidden" name="_psize"  id="_psize"  value="10"/>
<table border="0"  height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
<!-- 							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' /> -->
							&nbsp; 품목 관리 > 품목 검색
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
    <tr>
		<td>
			<div id="grid_wrap" style="border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="2" cellspacing="1" align="center" valign=top>
				<tr height="35">
					<td>
						<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
let myGridID;
function _layout() {
	return [{
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
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
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		style : "aui-left",
		width : 380,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.part_oid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "location",
		headerText : "품목분류",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "version",
		headerText : "Rev.",
		dataType : "string",
		width : 90,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 140,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "createDate",
		headerText : "등록일",
		dataType : "string",
		width : 140,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "modifiedDate",
		headerText : "수정일",
		dataType : "string",
		width : 140,
		filter : {
			showIcon : true,
			inline : true
		}
	}]
}
	
function createAUIGrid(columnLayout) {
	const props = {
		headerHeight : 30,
		showRowNumColumn : true,
		showRowCheckColumn : false,
		rowNumHeaderText : "번호",
		showAutoNoDataMessage : false,
		selectionMode : "multipleCells",
		enableMovingColumn : true,
		enableFilter : true,
		showInlineFilter : false,
		useContextMenu : true,
		enableRowCheckShiftKey : true,
		enableRightDownFocus : true,
		filterLayerWidth : 320,
		filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
	};
	myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
	loadGridData();
	AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
	AUIGrid.bind(myGridID, "vScrollChange", function(event) {
		hideContextMenu();
	});
	AUIGrid.bind(myGridID, "hScrollChange", function(event) {
		hideContextMenu();
	});
}

function loadGridData() {
	let params = new Object();
	const url = getCallUrl("/part/searchSeqAction");
	const partNumber = document.querySelector("#partNumber").value;
	const _psize = document.querySelector("#_psize").value;
	params.partNumber = partNumber;
	params._psize = _psize;
	debugger;
	AUIGrid.showAjaxLoader(myGridID);
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(myGridID);
		if (data.result) {
			totalPage = Math.ceil(data.total / data.pageSize);
// 			document.getElementById("sessionid").value = data.sessionid;
			createPagingNavigator(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
		} else {
			alert(data.msg);
		}
	});
}

document.addEventListener("DOMContentLoaded", function() {
	const columns = loadColumnLayout("seq-list");
	createAUIGrid(columns);
});
</script>