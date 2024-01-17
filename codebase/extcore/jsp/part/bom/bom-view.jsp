<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTPart root = (WTPart) request.getAttribute("part");
String oid = root.getPersistInfo().getObjectIdentifier().getStringValue();
ArrayList<Map<String, String>> baseline = (ArrayList<Map<String, String>>) request.getAttribute("baseline");
WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
%>
<style type="text/css">
.ap {
	font-weight: bold !important;
	color: blue !important;
}

.nd {
	font-weight: bold !important;
	color: red !important;
}

.exist {
	font-weight: bold !important;
}

.aui-grid-tree-branch-icon {
	display: inline-block;
	width: 16px;
	height: 16px;
	background: url(/Windchill/wtcore/images/part.gif) 50% 50% no-repeat;
	background-size: 16px;
	vertical-align: bottom;
}

.aui-grid-tree-branch-open-icon {
	display: inline-block;
	width: 16px;
	height: 16px;
	background: url(/Windchill/wtcore/images/part.gif) 50% 50% no-repeat;
	background-size: 16px;
	vertical-align: bottom;
}

.aui-grid-tree-leaf-icon {
	display: inline-block;
	width: 16px;
	height: 16px;
	background: url(/Windchill/wtcore/images/part.gif) no-repeat;
	background-size: 16px;
	vertical-align: bottom;
	margin: 0 2px 0 4px;
}
</style>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<input type="hidden" name="number" id="number" value="<%=root.getNumber()%>">
<input type="hidden" name="sessionName" id="sessionName" value="<%=user.getFullName()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				BOM (
				<font color="red">
					<b><%=root.getNumber()%></b>
				</font>
				) 보기
			</div>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<select name="depth" id="depth" onchange="loadDepth(this.value);" class="AXSelect width-120">
				<option value="0">전체확장</option>
				<option value="1">1레벨</option>
				<option value="2" selected="selected">2레벨</option>
				<option value="3">3레벨</option>
				<option value="4">4레벨</option>
				<option value="5">5레벨</option>
			</select>
			&nbsp;
			<select name="sort" id="sort" onchange="reloadTree();" class="AXSelect width-100">
				<option value="true">정전개</option>
				<option value="false">역전개</option>
			</select>
			&nbsp;
			<div class="pretty p-switch">
				<input type="checkbox" name="skip" id="skip" value="true" checked="checked" onchange="reloadTree();">
				<div class="state p-success">
					<label>
						<b>더미제외</b>
					</label>
				</div>
			</div>
			<font color="red">
				<b>썸네일(X)</b>
			</font>
			<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
			&nbsp;&nbsp;&nbsp;
			<font color="blue">
				<b>썸네일(O)</b>
			</font>
			<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="excel();">
		</td>
		<td class="right">
			<select name="baseline" id="baseline" class="AXSelect width-150" onchange="reloadTree();">
				<option value="" selected="selected" disabled="disabled">베이스라인 보기</option>
				<%
				for (Map<String, String> m : baseline) {
				%>
				<option value="<%=m.get("baseLine_oid")%>"><%=m.get("baseLine_name")%></option>
				<%
				}
				%>
			</select>
			<input type="button" value="도면일괄다운" title="도면일괄다운" class="red" onclick="batch();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>

<script type="text/javascript">
	let myGridID;
	const columnLayout = [ {
		dataField : "thumb_3d",
		headerText : "3D",
		dataType : "string",
		width : 40,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 16,
			onClick : function(event) {
			}
		},
	}, {
		dataField : "thumb_2d",
		headerText : "2D",
		dataType : "string",
		width : 40,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 16,
			onClick : function(event) {
			}
		},
	}, {
		dataField : "level",
		headerText : "레벨",
		dataType : "string",
		width : 45
	}, {
		dataField : "number",
		headerText : "부품번호",
		dataType : "string",
		width : 300
	}, {
		dataField : "dwg_no",
		headerText : "도면번호",
		dataType : "string",
		width : 140,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			if (value == "AP") {
				return "ap";
			} else if (value === "ND") {
				return "nd";
			} else {
				return "exist";
			}
			return null;
		}
	}, {
		dataField : "name",
		headerText : "부품명",
		dataType : "string",
		style : "aui-left",
		width : 300
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80
	}, {
		dataField : "remarks",
		headerText : "OEM Info",
		dataType : "string",
		width : 150
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 80,
	}, {
		dataField : "spec",
		headerText : "사양",
		dataType : "string",
		width : 150,
	}, {
		dataField : "qty",
		headerText : "수량",
		dataType : "string",
		width : 80,
		postfix : "개"
	}, {
		dataField : "ecoNo",
		headerText : "ECO NO",
		dataType : "string",
		width : 150,
	}, {
		dataField : "model",
		headerText : "프로젝트코드",
		dataType : "string",
		width : 150,
	}, {
		dataField : "deptcode",
		headerText : "부서",
		dataType : "string",
		width : 100,
	}, {
		dataField : "manufacture",
		headerText : "MANUFACTURE",
		dataType : "string",
		width : 150,
	}, {
		dataField : "productmethod",
		headerText : "제작방법",
		dataType : "string",
		width : 100,
	}, ];

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			displayTreeOpen : true,
			editable : false,
			treeColumnIndex : 3,
			enableFilter : true,
			flat2tree : true,
			enableSorting : false,
			fixedColumnCount : 4,
			treeLazyMode : true,
			treeLevelIndent : 28,
			useContextMenu : true,
			enableRightDownFocus : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		loadGridData();
		AUIGrid.bind(myGridID, "treeLazyRequest", auiLazyLoadHandler);
		AUIGrid.bind(myGridID, "contextMenu", function(event) {
			const menu = [ {
				label : "품목정보보기",
				callback : auiContextHandler
			}, {
				label : "상위품목",
				callback : auiContextHandler
			}, {
				label : "하위품목",
				callback : auiContextHandler
			}, {
				label : "END ITEM",
				callback : auiContextHandler
			}, {
				label : "속성보기",
				callback : auiContextHandler
			}, {
				label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
			}, {
				label : "CREO VIEW 오픈",
				callback : auiContextHandler
			}, {
				label : "썸네일 보기(3D)",
				callback : auiContextHandler
			}, ];
			return menu;
		});
	}

	function auiContextHandler(event) {
		const item = event.item;
		const oid = item.oid;
		const root = document.getElementById("oid").value;
		const baseline = document.getElementById("baseline").value;
		let url;
		switch (event.contextIndex) {
		case 0:
			url = getCallUrl("/part/view?oid=" + oid);
			_popup(url, 1600, 800, "n");
			break;
		case 1:
			url = getCallUrl("/part/upper?oid=" + oid + "&baseline=" + baseline);
			_popup(url, 600, 430, "n");
			break;
		case 2:
			url = getCallUrl("/part/lower?oid=" + oid + "&baseline=" + baseline);
			_popup(url, 600, 430, "n");
			break;
		case 3:
			url = getCallUrl("/part/end?oid=" + oid + "&baseline=" + baseline);
			_popup(url, 600, 430, "n");
			break;
		case 4:
			url = getCallUrl("/part/attr?oid=" + oid);
			_popup(url, 1000, 500, "n");
			break;
		case 6:
			break;
		case 7:
			url = getCallUrl("/part/thumbnail?oid=" + oid);
			_popup(url, 800, 600, "n");
			break;
		}
	}

	function auiLazyLoadHandler(event) {
		const item = event.item;
		const oid = item.oid;
		const level = item.level;
		const skip = document.querySelector("input[name=skip]").checked;
		const baseline = document.getElementById("baseline").value;
		const sort = document.getElementById("sort").value;
		const params = {
			oid : oid,
			skip : JSON.parse(skip),
			level : level,
			desc : JSON.parse(sort),
			baseline : baseline
		}
		const url = getCallUrl("/bom/lazyLoad")
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				closeLayer();
				event.response(data.list);
			}
		})
	}

	function loadGridData() {
		const oid = document.getElementById("oid").value;
		const skip = document.querySelector("input[name=skip]").checked;
		const sort = document.getElementById("sort").value;
		const baseline = document.getElementById("baseline").value;
		const url = getCallUrl("/bom/loadStructure");
		const params = {
			oid : oid,
			skip : JSON.parse(skip),
			desc : JSON.parse(sort),
			baseline : baseline
		};
		openLayer();
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			logger(data);
			AUIGrid.removeAjaxLoader(myGridID);
			if (data.result) {
				AUIGrid.setGridData(myGridID, data.list);
				AUIGrid.showItemsOnDepth(myGridID, 2);
			}
			closeLayer();
		})
	}

	function reloadTree() {
		AUIGrid.clearGridData(myGridID);
		const oid = document.getElementById("oid").value;
		const skip = document.querySelector("input[name=skip]").checked;
		const sort = document.getElementById("sort").value;
		const baseline = document.getElementById("baseline").value;
		const url = getCallUrl("/bom/loadStructure");
		const params = {
			oid : oid,
			skip : JSON.parse(skip),
			desc : JSON.parse(sort),
			baseline : baseline
		};
		openLayer();
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			if (data.result) {
				AUIGrid.setGridData(myGridID, data.list);
				AUIGrid.showItemsOnDepth(myGridID, 2);
			}
			closeLayer();
		})
	}

	function loadDepth(depth) {
		openLayer();
		setTimeout(function() {

			const sort = document.getElementById("sort").value;
			const skip = document.querySelector("input[name=skip]").checked;
			const baseline = document.getElementById("baseline").value;
			// 모든 레벨 열기
			if (Number(depth) === 0) {
				const grid = AUIGrid.getItemsByValue(myGridID, "_$depth", 1);
				for (let j = 0; j < grid.length; j++) {
					const item = grid[j];
					const isLazy = item.isLazy;
					// 				if (isLazy) {
					const oid = item.oid;
					const level = item.level;
					const params = {
						oid : oid,
						skip : JSON.parse(skip),
						level : level,
						desc : JSON.parse(sort),
						baseline : baseline
					};

					const url = getCallUrl("/bom/lazyLoad")
					call(url, params, function(data) {
						grid[j].children = data.list;
						grid[j] = _recursion(grid[j], skip, sort, baseline);
						const rowIndex = AUIGrid.rowIdToIndex(myGridID, grid[j]._$uid);
						AUIGrid.updateRow(myGridID, grid[j], rowIndex);
					}, "POST", false);
				}
				closeLayer();
				const tree = AUIGrid.getTreeGridData(myGridID);
				AUIGrid.setGridData(myGridID, tree);
				AUIGrid.expandAll(myGridID);
			} else {
				for (let i = 0; i < depth; i++) {
					const grid = AUIGrid.getItemsByValue(myGridID, "_$depth", i + 1);
					for (let j = 0; j < grid.length; j++) {
						const item = grid[j];
						const isLazy = item.isLazy;
						const _$lazyRequested = item._$lazyRequested;
						if (isLazy && !_$lazyRequested) {
							const oid = item.oid;
							const level = item.level;
							const params = {
								oid : oid,
								skip : JSON.parse(skip),
								level : level,
								desc : JSON.parse(sort),
								baseline : baseline
							};

							const url = getCallUrl("/bom/lazyLoad")
							call(url, params, function(data) {
								grid[j].children = data.list;
								if (depth - 1 != grid[j]._$depth) {
									grid[j] = recursion(grid[j], depth, skip, sort, baseline);
								}

								const rowIndex = AUIGrid.rowIdToIndex(myGridID, grid[j]._$uid);
								AUIGrid.updateRow(myGridID, grid[j], rowIndex);
							}, "POST", false);
						}
					}
				}
				end(depth);
			}
		}, 1000);
	}

	function _recursion(parent, skip, sort, baseline) {
		const grid = parent.children;
		for (let i = 0; i < grid.length; i++) {
			const item = grid[i];
			const isLazy = item.isLazy;
			if (isLazy) {
				const oid = item.oid;
				const level = item.level;
				const params = {
					oid : oid,
					skip : JSON.parse(skip),
					level : level,
					desc : JSON.parse(sort),
					baseline : baseline
				};

				const url = getCallUrl("/bom/lazyLoad")
				call(url, params, function(data) {
					grid[i].children = data.list;
					grid[i] = _recursion(grid[i], skip, sort, baseline);
					parent.children[i] = grid[i];
				}, "POSt", false);
			}
		}
		return parent;
	}

	function recursion(parent, depth, skip, sort, baseline) {
		const grid = parent.children;
		for (let i = 0; i < grid.length; i++) {
			const item = grid[i];
			const isLazy = item.isLazy;
			const _$lazyRequested = item._$lazyRequested;
			if (isLazy && !_$lazyRequested) {
				const oid = item.oid;
				const level = item.level;
				const params = {
					oid : oid,
					skip : JSON.parse(skip),
					level : level,
					desc : JSON.parse(sort),
					baseline : baseline
				};

				const url = getCallUrl("/bom/lazyLoad")
				call(url, params, function(data) {
					grid[i].children = data.list;
					if (depth - 1 != grid[i]._$depth) {
						grid[i] = recursion(grid[i], depth, skip, sort, baseline);
					}
					parent.children[i] = grid[i];
				}, "POSt", false);
			}
		}
		return parent;
	}

	function end(depth) {
		const data = AUIGrid.getTreeGridData(myGridID);
		AUIGrid.setGridData(myGridID, data);
		AUIGrid.showItemsOnDepth(myGridID, depth);
		closeLayer();
	}

	function expandAll() {
		AUIGrid.expandAll(myGridID);
	}

	window.gridData = function(mode) {
		if (mode === "ALL") {
			const skip = document.getElementById("skip").value;
			const sort = document.getElementById("sort").value;
			const baseline = document.getElementById("baseline").value;
			const grid = AUIGrid.getItemsByValue(myGridID, "_$depth", 1);
			for (let j = 0; j < grid.length; j++) {
				const item = grid[j];
				const oid = item.oid;
				const level = item.level;
				const params = {
					oid : oid,
					skip : JSON.parse(skip),
					level : level,
					desc : JSON.parse(sort),
					baseline : baseline
				};

				const url = getCallUrl("/bom/lazyLoad")
				call(url, params, function(data) {
					grid[j].children = data.list;
					grid[j] = _recursion(grid[j], skip, sort, baseline);
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, grid[j]._$uid);
					AUIGrid.updateRow(myGridID, grid[j], rowIndex);
				}, "POST", false);
			}
			const tree = AUIGrid.getTreeGridData(myGridID);
			AUIGrid.setGridData(myGridID, tree);
			AUIGrid.expandAll(myGridID);
		}

		const gridData = AUIGrid.getGridData(myGridID);
		return gridData
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columnLayout);
		AUIGrid.resize(myGridID);
		selectbox("depth");
		selectbox("sort");
		selectbox("baseline");
		selectbox("compare");
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});

	function batch() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/bom/batch?oid=" + oid + "&target=epm");
		_popup(url, 500, 350, "n");
	}

	function excel() {
		alert("개발 진행중");
	}

	function exportExcel() {
		const sort = document.getElementById("sort").value;
		const skip = document.querySelector("input[name=skip]").checked;
		const baseline = document.getElementById("baseline").value;
		openLayer();
		setTimeout(function() {
			const grid = AUIGrid.getItemsByValue(myGridID, "_$depth", 1);
			for (let j = 0; j < grid.length; j++) {
				const item = grid[j];
				const isLazy = item.isLazy;
				// 				if (isLazy) {
				const oid = item.oid;
				const level = item.level;
				const params = {
					oid : oid,
					skip : JSON.parse(skip),
					level : level,
					desc : JSON.parse(sort),
					baseline : baseline
				};

				const url = getCallUrl("/bom/lazyLoad")
				call(url, params, function(data) {
					grid[j].children = data.list;
					grid[j] = _recursion(grid[j], skip, sort, baseline);
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, grid[j]._$uid);
					AUIGrid.updateRow(myGridID, grid[j], rowIndex);
				}, "POST", false);
			}
			closeLayer();
			const tree = AUIGrid.getTreeGridData(myGridID);
			AUIGrid.setGridData(myGridID, tree);
			AUIGrid.expandAll(myGridID);
		}, 1000);
		
		setTimeout(function() {
			const sessionName = document.getElementById("sessionName").value;
			const number = document.getElementById("number").value;
			exportToExcel(number + "_BOM 리스트", number + "_BOM 리스트", number + "_BOM 리스트", [], sessionName);
		}, 1000);
	}
</script>