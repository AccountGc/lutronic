<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
WTPart root = (WTPart) request.getAttribute("root");
%>
<style type="text/css">
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

.isCheckOut {
	background-color: #FFCBCB;
	font-weight: bold;
}

.approved {
	color: red !important;
	font-weight: bold !important;
}
</style>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				BOM (
				<b>
					<font color="red"><%=root.getNumber()%></font>
				</b>
				) 에디터 &nbsp;
				<div class="pretty p-switch">
					<input type="checkbox" name="skip" id="skip" value="true" checked="checked" onchange="reloadTree();">
					<div class="state p-success">
						<label>
							<b>더미제외</b>
						</label>
					</div>
				</div>
				<select name="depth" id="depth" onchange="loadDepth();" class="AXSelect width-120">
					<option value="0">전체확장</option>
					<option value="1">1레벨</option>
					<option value="2" selected="selected">2레벨</option>
					<option value="3">3레벨</option>
					<option value="4">4레벨</option>
					<option value="5">5레벨</option>
				</select>
			</div>
		</td>
	</tr>
</table>

<!-- 전체 테이블 -->
<table>
	<colgroup>
		<col width="55%">
		<col width="50px">
		<col width="44%">
	</colgroup>
	<tr>
		<td valign="top">
			<div id="grid_wrap" style="height: 645px; border-top: 1px solid #3180c3;"></div>
		</td>
		<td>&nbsp;</td>
		<td valign="top">
			<table class="search-table">
				<colgroup>
					<col width="130">
					<col width="*">
					<col width="130">
					<col width="*">
				</colgroup>
				<tr>
					<th>품목번호</th>
					<td class="indent5">
						<input type="text" name="partNumber" id="partNumber" class="width-300">
					</td>
					<th>품목명</th>
					<td class="indent5">
						<input type="text" name="partName" id="partName" class="width-300">
					</td>
				</tr>
			</table>
			<table class="button-table">
				<tr>
					<td class="left">
						<input type="button" value="교체" title="교체" class="blue" onclick="replace();">
					</td>
					<td class="right">
						<input type="button" value="검색" title="검색" onclick="loadGridData();">
						<input type="button" value="추가" title="추가" class="blue" onclick="insert();">
					</td>
				</tr>
			</table>
			<div id="grid_wrap2" style="height: 535px; border-top: 1px solid #3180c3;"></div>
			<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
		</td>
	</tr>
</table>

<script type="text/javascript">
	let myGridID;
	const columnLayout = [ {
		dataField : "thumb",
		headerText : "",
		dataType : "string",
		width : 40,
		editable : false,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			onClick : function(event) {
			}
		},
	}, {
		dataField : "level",
		headerText : "레벨",
		dataType : "string",
		width : 45,
		editable : false
	}, {
		dataField : "number",
		headerText : "부품번호",
		dataType : "string",
		// 		width : 250,
		editable : false
	}, {
		dataField : "name",
		headerText : "부품명",
		dataType : "string",
		width : 200,
		editable : false,
		style : "aui-left"
	}, {
		dataField : "qty",
		headerText : "수량",
		dataType : "numeric",
		width : 60,
		postfix : "개",
		editable : true
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
		editable : false
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80,
		editable : false,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			if (value == "승인됨") {
				return "approved";
			}
			return null;
		}
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 80,
		editable : false
	} ];
	function createAUIGrid(columnLayout) {
		const props = {
			editable : true,
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			displayTreeOpen : true,
			treeColumnIndex : 2,
			enableFilter : true,
			flat2tree : true,
			enableSorting : false,
			fixedColumnCount : 3,
			treeLazyMode : true,
			treeLevelIndent : 17,
			useContextMenu : true,
			enableRightDownFocus : true,
			rowCheckToRadio : true,
			showRowCheckColumn : true,
			enableDrop : true,
			treeIconFunction : function(rowIndex, isBranch, isOpen, depth, item) {
				let imgSrc = "/Windchill/wtcore/images/part.gif";
				const isCheckOut = item.isCheckOut;
				if (isCheckOut) {
					imgSrc = "/Windchill/wtcore/images/part_checkout.png";
				}
				return imgSrc;
			},
			rowStyleFunction : function(rowIndex, item) {
				const isCheckOut = item.isCheckOut;
				if (isCheckOut) {
					return "isCheckOut";
				}
				return null;
			}
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		loadTree();
		AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEnd);
		AUIGrid.bind(myGridID, "contextMenu", function(event) {
			const item = event.item;
			const isWorkCopy = item.isWorkCopy;
			const isRevise = item.isRevise;
			const menu = [ {
				label : "개정",
				callback : auiContextHandler,
				disable : !isRevise
			}, {
				label : "체크아웃",
				callback : auiContextHandler,
				disable : isWorkCopy
			}, {
				label : "체크아웃 취소",
				callback : auiContextHandler
			}, {
				label : "체크인",
				callback : auiContextHandler
			}, {
				label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
			}, {
				label : "하위추가",
				callback : auiContextHandler
			}, {
				label : "개정",
				callback : auiContextHandler
			}, {
				label : "제거",
				callback : auiContextHandler
			}, {
				label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
			}, {
				label : "신규품목교체",
				callback : auiContextHandler
			}, {
				label : "기존품목교체",
				callback : auiContextHandler
			} ];
			return menu;
		});
		AUIGrid.bind(myGridID, "treeLazyRequest", auiLazyHandler)
	};

	function auiCellEditBegin(event) {
		logger(event);
	}

	function loadTree() {
		const oid = document.getElementById("oid").value;
		const skip = document.querySelector("input[name=skip]").checked;
		const url = getCallUrl("/bom/loadEditor");
		const params = {
			oid : oid,
			skip : JSON.parse(skip),
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

	function auiCellEditEnd(event) {
		const item = event.item;
		const dataField = event.dataField;
		if (dataField === "qty") {
			logger(item);
		}
	}

	function auiLazyHandler(event) {
		const item = event.item;
		const oid = item.oid;
		const skip = document.querySelector("input[name=skip]").checked;
		const url = getCallUrl("/bom/editorLazyLoad");
		const level = item.level;
		const params = {
			oid : oid,
			skip : JSON.parse(skip),
			level : level
		};
		openLayer();
		call(url, params, function(data) {
			closeLayer();
			event.response(data.list);
		});
	}

	function auiContextHandler(event) {
		const root = document.getElementById("oid").value;
		' // 최상위 OID'
		const item = event.item;
		const number = item.number;
		const oid = item.oid; // 부품 OID
		const link = item.link;// USAGE LINK OID
		const poid = item.poid; // 부모 OID
		const isRoot = item.isRoot;
		const rowIndex = event.rowIndex;
		let url;
		let params;
		switch (event.contextIndex) {
		case 0: // 개정
			url = getCallUrl("/bom/revise?oid=" + oid);
			openLayer();
			call(url, null, function(data) {
				if (data.result) {
					reload(root);
				}
				closeLayer();
			}, "GET");
			break;
		case 0:
			break;
		case 1:
			url = getCallUrl("/bom/undoCheckOut?oid=" + oid);
			openLayer();
			call(url, null, function(data) {
				if (data.result) {
					reload(root);
				}
				closeLayer();
			}, "GET");
			break;
		case 2:
			break;

		case 4:
			// 도면
			_popup(getCallUrl("/activity/reference?oid=" + next_oid), 1200, 500, "n");
			break;
		case 5:
			if (isRoot) {
				alert("최상위는 제거가 불가능합니다.");
				return false;
			}

			if (!confirm(number + " 품목을 BOM 에서 제거하시겠습니까?")) {
				return false;
			}

			url = getCallUrl("/bom/removeLink");
			params = {
				oid : oid,
				link : item.link,
				poid : poid,
			};
			openLayer();
			call(url, params, function(data) {
				if (data.result) {
					reload(root);
				} else {
					alert(data.msg);
				}
				closeLayer();
			})
			break;
		case 6:
			if (isRoot) {
				alert("최상위는 교체가 불가능합니다.");
				return false;
			}
			break;
		case 7:
			break;
		}
	};

	function reload(oid) {
		const url = getCallUrl("/bom/reload?oid=" + oid);
		openLayer();
		AUIGrid.showAjaxLoader(myGridID);
		call(url, null, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			if (data.result) {
				AUIGrid.setGridData(myGridID, data.tree);
				AUIGrid.showItemsOnDepth(myGridID, 2);
			}
			closeLayer();
		}, "GET");
	}
</script>

<script type="text/javascript">
	let myGridID2;
	const columnLayout2 = [ {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		style : "aui-left",
	}, {
		dataField : "name",
		headerText : "REV",
		dataType : "string",
		width : 80,
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
	} ];

	function createAUIGrid2(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableMovingColumn : true,
			rowCheckToRadio : true,
			showDragKnobColumn : true,
			enableDrop : false,
			dropToOthers : true,
		};
		myGridID2 = AUIGrid.create("#grid_wrap2", columnLayout2, props);
		AUIGrid.bind(myGridID2, "dropEnd", function(event) {
			logger(event);
		});
		loadGridData();
	}

	function loadGridData() {
		let params = new Object();
		const url = getCallUrl("/part/list");
		const field = [ "partNumber", "partName" ];
		// 		const latest = document.querySelector("input[name=latest]:checked").value;
		params = toField(params, field);
		// 		params.latest = JSON.parse(latest);
		params.latest = true;
		AUIGrid.showAjaxLoader(myGridID2);
		openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID2);
			if (data.result) {
				console.log(data);
				totalPage = Math.ceil(data.total / data.pageSize);
				document.getElementById("sessionid").value = data.sessionid;
				createPagingNavigator(data.curPage);
				AUIGrid.setGridData(myGridID2, data.list);
			} else {
				alert(data.msg);
			}
			closeLayer();
		});
	}

	function insert() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID2);
		if (checkedItems.length === 0) {
			alert("추가 할 품목을 선택하세요.");
			return false;
		}
	}

	function replace() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID2);
		if (checkedItems.length === 0) {
			alert("교체 할 품목을 선택하세요.");
			return false;
		}
	}

	function loadDepth() {
		const depth = document.getElementById("depth").value;
		const skip = document.querySelector("input[name=skip]").checked;
		// 모든 레벨 열기
		if (Number(depth) === 0) {
			const grid = AUIGrid.getItemsByValue(myGridID, "_$depth", 1);
			for (let j = 0; j < grid.length; j++) {
				const item = grid[j];
				const isLazy = item.isLazy;
				if (isLazy) {
					const oid = item.oid;
					const level = item.level;
					const params = {
						oid : oid,
						skip : JSON.parse(skip),
						level : level,
					};

					const url = getCallUrl("/bom/editorLazyLoad")
					call(url, params, function(data) {
						grid[j].children = data.list;
						grid[j] = _recursion(grid[j], skip);
						const rowIndex = AUIGrid.rowIdToIndex(myGridID, grid[j]._$uid);
						AUIGrid.updateRow(myGridID, grid[j], rowIndex);
					}, "POST", false);
				}
			}
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
						};

						const url = getCallUrl("/bom/editorLazyLoad")
						call(url, params, function(data) {
							grid[j].children = data.list;
							if (depth - 1 != grid[j]._$depth) {
								grid[j] = recursion(grid[j], depth, skip);
							}

							const rowIndex = AUIGrid.rowIdToIndex(myGridID, grid[j]._$uid);
							AUIGrid.updateRow(myGridID, grid[j], rowIndex);
						}, "POST", false);
					}
				}
			}
			end(depth);
		}
	}

	function _recursion(parent, skip) {
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
				};

				const url = getCallUrl("/bom/editorLazyLoad")
				call(url, params, function(data) {
					grid[i].children = data.list;
					grid[i] = _recursion(grid[i], skip);
					parent.children[i] = grid[i];
				}, "POSt", false);
			}
		}
		return parent;
	}

	function recursion(parent, depth, skip) {
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
				};

				const url = getCallUrl("/bom/lazyLoad")
				call(url, params, function(data) {
					grid[i].children = data.list;
					if (depth - 1 != grid[i]._$depth) {
						grid[i] = recursion(grid[i], depth, skip);
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
	}

	document.addEventListener("keydown", function(event) {
		const keyCode = event.keyCode || event.which;
		if (keyCode === 13) {
			loadGridData();
		}
	})

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columnLayout);
		AUIGrid.resize(myGridID);
		createAUIGrid2(columnLayout2);
		AUIGrid.resize(myGridID2);
		selectbox("depth");
	})
</script>