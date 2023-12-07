<%@page import="java.util.Base64.Decoder"%>
<%@page import="java.util.Base64.Encoder"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="java.util.Base64"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- 폴더 그리드 리스트 -->
<div id="_grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let _myGridID;
	const _columns = [ {
		dataField : "display",
		headerText : "문서타입",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	} ]

	function _createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			enableFilter : true,
			displayTreeOpen : true,
		}
		_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
		loadTree();
		AUIGrid.bind(_myGridID, "cellClick", auiCellClick);
	}

	function auiCellClick(event) {
		const rowIndex = event.rowIndex;
		if (rowIndex === 0) {
			return false;
		}
		const type = event.item.type;
		const params = {
			codeType : type
		}
		document.getElementById("classType").value = type;
		const url = getCallUrl("/class/list");
		parent.openLayer();
		call(url, params, function(data) {
			if (data.result) {
				AUIGrid.setGridData(myGridID, data.list);
			} else {
				alert(data.msg);
			}
			parent.closeLayer();
		})
	}

	function loadTree() {
		const url = getCallUrl("/class/tree");
		AUIGrid.showAjaxLoader(_myGridID);
		call(url, null, function(data) {
			AUIGrid.removeAjaxLoader(_myGridID);
			if (data.result) {
				AUIGrid.setGridData(_myGridID, data.list);
			} else {
				alert(data.msg);
			}
		}, "GET");
	}
</script>
