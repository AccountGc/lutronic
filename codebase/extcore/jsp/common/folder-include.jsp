<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = CommonUtil.isAdmin();
String type = request.getParameter("type");
String location = request.getParameter("location");
String container = request.getParameter("container");
String mode = request.getParameter("mode");
String height = request.getParameter("height");
%>
<!-- 폴더 그리드 리스트 -->
<input type="hidden" name="type" id="type" value="<%=type%>">
<div id="_grid_wrap" style="height: <%=height%>px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let _myGridID;
	const _columns = [ {
		dataField : "name",
		headerText : "폴더명",
		dataType : "string",
	} ]

	function _createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
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
			<%
				if(isAdmin) {
			%>
			editable : true
			<%
				}
			%>
		}
		_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
<%if (isAdmin) {%>
	AUIGrid.bind(_myGridID, "contextMenu", auiContextMenuHandler_);
<%}%>
// 	AUIGrid.bind(_myGridID, "cellDoubleClick", auiCellDoubleClick);
		AUIGrid.bind(_myGridID, "cellClick", auiCellClick);
		AUIGrid.bind(_myGridID, "ready", auiReadyHandler);
		tree();
	}

	function auiContextMenuHandler_(event) {
		const menu = [ {
			label : "권한설정",
			callback : auiContextHandler_
		}, {
			label : "문서이동",
			callback : auiContextHandler_
		}, {
			label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
		}, {
			label : "폴더 생성(자식)",
			callback : auiContextHandler_
		}, {
			label : "폴더 생성(동일레벨)",
			callback : auiContextHandler_
		}, {
			label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
		}, {
			label : "저장",
			callback : auiContextHandler_
		} ]
		return menu;
	}

	function auiContextHandler_(event) {
		const t = document.getElementById("type").value;
		const item = event.item;
		const oid = item.oid;
		const poid = item.poid;
		const isNew = item.isNew;
		switch (event.contextIndex) {
		case 0:
			url = getCallUrl("/access/view?oid=" + oid);
			_popup(url, 1400, 600, "n");
			break;
		case 1:
			if ("doc" !== t) {
				alert("문서관리에서만 가능한 기능입니다.");
				return false;
			}
			url = getCallUrl("/doc/move?oid=" + oid);
			_popup(url, 1600, 600, "n");
			break;
		case 3:
			addTreeRow(oid, isNew);
			break;
		case 4:
			addRow(poid, isNew);
			break;
		case 5:
			break;
		case 6:
			treeSave();
			break;
		}
	}

	function treeSave() {
		const editRows = AUIGrid.getEditedRowItems(_myGridID);
		const addRows = AUIGrid.getAddedRowItems(_myGridID);
		
		const url = getCallUrl("/folder/treeSave");
		const params = {
			editRows:editRows,
			addRows:addRows,
		};
		parent.openLayer();
		logger(params);
		call(url, params, function(data) {
			alert(data.msg);
			if(data.result) {
				tree();
			}
			parent.closeLayer();
		})
	}

	function auiReadyHandler() {
		AUIGrid.showItemsOnDepth(_myGridID, 2);
	}

	function auiCellClick(event) {
		const item = event.item;
		const oid = item.oid;
		const location = item.location;
		document.getElementById("oid").value = oid;
		document.getElementById("location").value = location;
		document.getElementById("locationText").innerText = location;
		loadGridData();
	}
	
	let timerId = null;
	function auiCellDoubleClick(event) {
<%if ("list".equals(mode)) {%>
	// 500ms 보다 빠르게 그리드 선택자가 변경된다면 데이터 요청 안함
		if (timerId) {
			clearTimeout(timerId);
		}

		timerId = setTimeout(function() {
			const primeCell = event.item;
			const oid = primeCell.oid;
			const location = primeCell.location;
			document.getElementById("oid").value = oid;
			document.getElementById("location").value = location;
			document.getElementById("locationText").innerText = location;
			loadGridData();
		}, 500);
<%}%>
	}
	
	function addTreeRow(oid) {
		const parentRowId = oid;
		const newItem = new Object();
		newItem.parentRowId = parentRowId;
		newItem.poid = oid;
		newItem.name = "새 폴더";
		newItem.isNew = true;
		AUIGrid.addTreeRow(_myGridID, newItem, parentRowId, "selectionDown");
	}
	
	function addRow(poid) {
		const parentRowId = poid;
		const newItem = new Object();
		newItem.poid = poid;
		newItem.parentRowId = parentRowId;
		newItem.name = "새 폴더";
		newItem.isNew = true;
		AUIGrid.addTreeRow(_myGridID, newItem, parentRowId, "selectionDown");
	}

	function tree() {
		const location = decodeURIComponent("<%=location%>");
		const url = getCallUrl("/folder/tree");
		const params = {
			location : location
		}
		call(url, params, function(data) {
			AUIGrid.setGridData(_myGridID, data.list);
		});
	}
</script>
