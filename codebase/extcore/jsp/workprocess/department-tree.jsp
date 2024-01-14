<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = CommonUtil.isAdmin();
String height = request.getParameter("height");
%>
<!-- 폴더 그리드 리스트 -->
<div id="_grid_wrap" style="height: <%=height%>px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let _myGridID;
	const layout = [ {
		dataField : "name",
		headerText : "부서명",
		dataType : "string",
	} ]

	function _createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			showAutoNoDataMessage : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableFilter : true,
			showInlineFilter : true,
			displayTreeOpen : true,
			forceTreeView : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			<%if (isAdmin) {%>
			editable : true
			<%}%>			
		}
		_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
		loadTree();
		AUIGrid.bind(_myGridID, "cellClick", auiCellClick);
		AUIGrid.bind(_myGridID, "ready", auiReadyHandler);
		<%if (isAdmin) {%>
		AUIGrid.bind(_myGridID, "contextMenu", auiContextMenuHandler_);
		<%}%>
	}

	function auiContextMenuHandler_(event) {
		const menu = [ { 
			label : "부서 생성(자식)",
			callback : auiContextHandler_
		}, {
			label : "부서 생성(동일레벨)",
			callback : auiContextHandler_
		}, {
			label : "삭제",
			callback : auiContextHandler_
		}, {
			label : "_$line"
		}, {
			label : "저장",
			callback : auiContextHandler_
		} ]
		return menu;
	}

	function auiContextHandler_(event) {
		const item = event.item;
		const oid = item.oid;
		const poid = item.poid;
		const isNew = item.isNew;
		switch (event.contextIndex) {
		case 0:
			addTreeRow(poid);
			break;
		case 1:
			addRow(poid);
			break;
		case 2:
			break;
		case 4:
			treeSave();
			break;
		}
	}
	
	function treeSave() {
		const editRows = AUIGrid.getEditedRowItems(_myGridID);
		const addRows = AUIGrid.getAddedRowItems(_myGridID);
		
		const url = getCallUrl("/department/treeSave");
		const params = {
			editRows:editRows,
			addRows:addRows,
		};
		parent.openLayer();
		logger(params);
		call(url, params, function(data) {
			alert(data.msg);
			if(data.result) {
				loadTree();
			}
			parent.closeLayer();
		})
	}
	
	function addTreeRow(oid) {
		const parentRowId = oid;
		const newItem = new Object();
		newItem.parentRowId = parentRowId;
		newItem.poid = oid;
		newItem.name = "새 부서";
		newItem.isNew = true;
		AUIGrid.addTreeRow(_myGridID, newItem, parentRowId, "selectionDown");
	}
	
	function addRow(poid) {
		const parentRowId = poid;
		const newItem = new Object();
		newItem.poid = poid;
		newItem.parentRowId = parentRowId;
		newItem.name = "새 부서";
		newItem.isNew = true;
		AUIGrid.addTreeRow(_myGridID, newItem, parentRowId, "selectionDown");
	}
	
	function auiReadyHandler() {
		AUIGrid.showItemsOnDepth(_myGridID, 2);
	}

	let timerId;
	function auiCellClick(event) {
		if (timerId) {
			clearTimeout(timerId);
		}

		timerId = setTimeout(function() {
			const primeCell = event.item;
			const oid = primeCell.oid;
			const location = primeCell.location;
			document.getElementById("oid").value = oid;
			document.getElementById("locationName").innerText = location;
			loadGridData();
		}, 500);
	}

	function loadTree() {
		const url = getCallUrl("/department/tree");
		const params = new Object();
		AUIGrid.showAjaxLoader(_myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(_myGridID);
			AUIGrid.setGridData(_myGridID, data.list);
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		_createAUIGrid(layout);
		AUIGrid.resize(_myGridID);
	});
</script>
