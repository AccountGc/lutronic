<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
String eoid = (String) request.getAttribute("eoid");
WTPart root = (WTPart) request.getAttribute("root");
EChangeOrder eco = (EChangeOrder) request.getAttribute("eco");
JSONArray data = (JSONArray) request.getAttribute("tree");
%>
<style type="text/css">
/*평쳤을때 이미지*/
/*데이터 앞 이미지*/
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
<input type="hidden" name="eoid" id="eoid" value="<%=eoid%>">
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
				) 편집
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
			<div id="grid_wrap" style="height: 675px; border-top: 1px solid #3180c3;"></div>
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
						<input type="button" value="추가" title="추가" class="red" onclick="insert();">
						<input type="button" value="교체" title="교체" class="blue" onclick="replace();">
					</td>
					<td class="right">
						<input type="button" value="검색" title="검색" onclick="loadGridData();">
					</td>
				</tr>
			</table>
			<div id="grid_wrap2" style="height: 565px; border-top: 1px solid #3180c3;"></div>
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
		width : 60
	}, {
		dataField : "number",
		headerText : "부품번호",
		dataType : "string",
	}, {
		dataField : "name",
		headerText : "부품명",
		dataType : "string",
		width : 250,
		style : "aui-left"
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80,
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
		width : 80
	} ];
	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			displayTreeOpen : true,
			editable : false,
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
		AUIGrid.setGridData(myGridID,
<%=data%>
	);
		AUIGrid.showItemsOnDepth(myGridID, 2);
		AUIGrid.bind(myGridID, "contextMenu", function(event) {
			const item = event.item;
			const isWorkCopy = item.isWorkCopy;
			const menu = [ {
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
				label : "기존삽입",
				callback : auiContextHandler
			}, {
				label : "제거",
				callback : auiContextHandler
			}, {
				label : "교체",
				callback : auiContextHandler
			}, {
				label : "개정",
				callback : auiContextHandler
			} ];
			return menu;
		});

		AUIGrid.bind(myGridID, "treeLazyRequest", auiLazyHandler)
	};

	function auiLazyHandler(event) {
		console.log(event);
		const item = event.item;
		const oid = item.oid;
		const url = getCallUrl("/bom/lazyLoad?oid=" + oid);

		call(url, null, function(data) {
			const list = data.list;
			for (let i = 0; i < list.length; i++) {
				list[i].level = event.item._$depth + 1;
			}
			event.response(list);
		}, "GET");
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
		const eoid = document.getElementById("eoid").value;
		let url;
		let params;
		switch (event.contextIndex) {
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
				eoid : eoid
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

// 부품 검색 부분 그리드
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
			enableMovingColumn : true,
			rowCheckToRadio : true,
		};
		myGridID2 = AUIGrid.create("#grid_wrap2", columnLayout2, props);
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
		logger(params);
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
	})
</script>