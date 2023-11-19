<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTPart root = (WTPart) request.getAttribute("part");
String oid = root.getPersistInfo().getObjectIdentifier().getStringValue();
ArrayList<Map<String, String>> baseline = (ArrayList<Map<String, String>>) request.getAttribute("baseline");
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
</style>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
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
			<select name="depth" id="depth" onchange="loadDepth();" class="AXSelect width-120">
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
			&nbsp;
			<select name="compare" id="compare" class="AXSelect width-150">
				<option value="" selected="selected" disabled="disabled">베이스라인 비교</option>
				<%
				for (Map<String, String> m : baseline) {
				%>
				<option value="<%=m.get("baseLine_oid")%>"><%=m.get("baseLine_name")%></option>
				<%
				}
				%>
			</select>
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 700px; border-top: 1px solid #3180c3;"></div>

<script type="text/javascript">
	let myGridID;
	const columnLayout = [ {
		dataField : "thumb_3d",
		headerText : "3D",
		dataType : "string",
		width : 40,
		renderer : {
			type : "ImageRenderer"
		}
	}, {
		dataField : "thumb_2d",
		headerText : "2D",
		dataType : "string",
		width : 40,
		renderer : {
			type : "ImageRenderer"
		}
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
		dataField : "dwgNo",
		headerText : "도면번호",
		dataType : "string",
		width : 140,
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
		headerText : "OEM Info.",
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
		width : 100,
	}, {
		dataField : "spec",
		headerText : "사양",
		dataType : "string",
		width : 150,
	}, {
		dataField : "qty",
		headerText : "수량",
		dataType : "string",
		width : 100,
		postfix : "개"
	}, {
		dataField : "ecoNo",
		headerText : "ECO NO.",
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
				label : "완제품",
				callback : auiContextHandler
			}, {
				label : "속성보기",
				callback : auiContextHandler
			}, {
				label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
			}, {
				label : "엑셀다운",
				callback : auiContextHandler
			}, {
				label : "첨부",
				callback : auiContextHandler
			}, {
				label : "도면",
				callback : auiContextHandler
			} ];
			return menu;
		});
	}

	function auiContextHandler(event) {
		const item = event.item;
		const oid = item.oid;
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
			closeLayer();
			event.response(data.list);
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
		logger(params);
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
		logger(params);
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

	function loadDepth() {
		const sort = document.getElementById("sort").value;
		const depth = document.getElementById("depth").value;
		const skip = document.querySelector("input[name=skip]").checked;
		const baseline = document.getElementById("baseline").value;
		// 모든 레벨 열기
		if (depth === 0) {

			// 특정레벨
		} else {
			for (let i = 0; i < depth; i++) {
				const grid = AUIGrid.getItemsByValue(myGridID, "_$depth", i + 1);

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
					openLayer();
					call(url, params, function(data) {
						grid[j].children = data.list;
						logger(grid[j]);
						const rowIndex = AUIGrid.rowIdToIndex(myGridID, grid[j]._$uid);
						console.log(rowIndex);
						AUIGrid.updateRow(myGridID, grid[j]);
						// 						AUIGrid.setGridData(myGridID, AUIGrid.getTreeGridData(myGridID));
						// 						AUIGrid.expandAll(myGridID);
						closeLayer();
						logger(grid[j]);
					})
				}
			}

		}
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
</script>