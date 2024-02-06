<%@page import="com.e3ps.org.MailUser"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<input type="hidden" name="sortKey" id="sortKey">
<input type="hidden" name="sortType" id="sortType">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				외부 메일 추가
			</div>
		</td>
	</tr>
</table>

<table class="search-table">
	<colgroup>
		<col width="80">
		<col width="*">
		<col width="80">
		<col width="*">
	</colgroup>
	<tr>
		<th>이름</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-120">
		</td>
		<th>이메일</th>
		<td class="indent5">
			<input type="text" name="email" id="email" class="width-120">
		</td>
	</tr>
</table>
<table class="button-table">
	<tr>
		<td class="right">
			<select name="_psize" id="_psize" onchange="loadGridData();">
				<option value="10">10</option>
				<option value="20" selected="selected">20</option>
				<option value="30">30</option>
				<option value="50">50</option>
				<option value="100">100</option>
			</select>
			<input type="button" value="검색" title="검색" onclick="loaData();">
			<input type="button" value="추가" title="추가" class="blue" onclick="addRow();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 415px; border-top: 1px solid #3180c3;"></div>
<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
<%@include file="/extcore/jsp/common/aui-context.jsp"%>
<script type="text/javascript">
	let myGridID;
	function _layout() {
		return [ {
			dataField : "rowNum",
			headerText : "번호",
			width : 40,
			dataType : "numeric",
			filter : {
				inline : false
			},
		}, {
			dataField : "name",
			headerText : "이름",
			dataType : "string",
			width : 120,
		}, {
			dataField : "email",
			headerText : "이메일",
			dataType : "string",
			style : "aui-left",
		} ]
	}

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : false,
			showRowCheckColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableMovingColumn : true,
			enableFilter : true,
			showInlineFilter : false,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		// 		loadGridData();
		AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
		AUIGrid.bind(myGridID, "vScrollChange", function(event) {
			hideContextMenu();
		});
		AUIGrid.bind(myGridID, "hScrollChange", function(event) {
			hideContextMenu();
		});
		AUIGrid.bind(myGridID, "cellClick", auiCellClick);
		AUIGrid.bind(myGridID, "sorting", auiSortingHandler);
	}

	let sortCache = [];
	let compField;
	function auiSortingHandler(event) {
		const sortingFields = event.sortingFields;
		if (sortingFields.length > 0) {
			const key = sortingFields[0].dataField;
			if (compField !== key) {
				compField = key;
				const sortType = sortingFields[0].sortType; // 오름차순 1 내림 -1
				sortCache[0] = {
					dataField : key,
					sortType : sortType
				};
				document.getElementById("sortKey").value = key;
				document.getElementById("sortType").value = sortType;
				loadGridData();
			}
		}
	}

	function auiCellClick(event) {
		const item = event.item;

		if (AUIGrid.isCheckedRowById(event.pid, item._$uid)) {
			AUIGrid.addUncheckedRowsByIds(event.pid, item._$uid);
		} else {
			AUIGrid.addCheckedRowsByIds(event.pid, item._$uid);
		}
	}

	function loadGridData(movePage) {
		if (movePage === undefined) {
			document.getElementById("sessionid").value = 0;
			document.getElementById("curPage").value = 1;
		}
		let params = new Object();
		const field = [ "name", "email" ];
		const enable = true;
		const url = getCallUrl("/admin/mail");
		params = toField(params, field);
		params.enable = JSON.parse(enable);
		AUIGrid.showAjaxLoader(myGridID);
		openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			if (data.result) {
				totalPage = Math.ceil(data.total / data.pageSize);
				createPagingNavigator(data.total, data.curPage, data.sessionid);
				AUIGrid.setGridData(myGridID, data.list);
				if (movePage === undefined) {
					AUIGrid.setSorting(myGridID, sortCache);
					compField = null;
				}
			} else {
				alert(data.msg);
			}
			closeLayer();
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("name");
		const columns = loadColumnLayout("mail-list");
		const contenxtHeader = genColumnHtml(columns);
		$("#h_item_ul").append(contenxtHeader);
		$("#headerMenu").menu({
			select : headerMenuSelectHandler
		});
		createAUIGrid(columns);
		selectbox("_psize");
		$("#_psize").bindSelectSetValue("20");
	});

	document.addEventListener("keydown", function(event) {
		const keyCode = event.keyCode || event.which;
		if (keyCode === 13) {
			loadGridData();
		}
	})

	document.addEventListener("click", function(event) {
		hideContextMenu();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});

	// 추가
	function addRow() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length === 0) {
			alert("추가할 사용자를 선택하세요.");
			return false;
		}
		opener.insert9(checkedItems, function(res, close, msg) {
			trigger(close, msg);
		})
	}
</script>
