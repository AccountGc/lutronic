<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
JSONArray list = (JSONArray) request.getAttribute("list");
JSONArray partName1 = (JSONArray) request.getAttribute("partName1");
JSONArray partName2 = (JSONArray) request.getAttribute("partName2");
JSONArray partName3 = (JSONArray) request.getAttribute("partName3");
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
<input type="hidden" name="oid" id="oid" value="<%=oid%>" />
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				부품 진채번
			</div>
		</td>
		<td class="right">
			<input type="button" value="저장" class="red" onclick="setNumber();">
			<input type="button" value="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 500px; border-top: 1px solid #3180c3;"></div>
<script>
	let myGridID;
	const list =
<%=list%>
	const partName1 =
<%=partName1%>
	const partName2 =
<%=partName2%>
	const partName3 =
<%=partName3%>
	const columnLayout = [ {
		dataField : "number",
		headerText : "가도번",
		dataType : "string",
		width : 150,
		editable : false
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		width : 300,
		editable : false
	}, {
		dataField : "real",
		headerText : "진도번",
		dataType : "string",
		width : 150,
		editable : false
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
		editable : false
	}, {
		dataField : "partType1",
		headerText : "부품구분",
		dataType : "string",
		width : 150,
		editable : false,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
			},
			onClick : function(event) {
				AUIGrid.openInputer(event.pid);
			}
		},
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			autoEasyMode : true,
			matchFromFirst : false,
			showEditorBtnOver : false,
			list : list,
			keyField : "oid",
			valueField : "name",
			// 			descendants : [ "detail_code" ],
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = list.length; i < len; i++) {
					if (list[i]["name"] == newValue) {
						isValid = true;
						break;
					}
				}
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			}
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
			let retStr = "";
			for (let i = 0, len = list.length; i < len; i++) {
				if (list[i]["oid"] == value) {
					retStr = list[i]["name"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "partType2",
		headerText : "대분류",
		dataType : "string",
		width : 150,
		editable : false,
	}, {
		dataField : "partType3",
		headerText : "중분류",
		dataType : "string",
		width : 150,
		editable : false,
	}, {
		dataField : "seq",
		headerText : "SEQ",
		dataType : "numeric",
		width : 80,
		editable : false,
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true,
			maxlength : 3,
		},
	}, {
		dataField : "etc",
		headerText : "기타",
		dataType : "numeric",
		width : 80,
		editable : false,
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true,
			maxlength : 2,
		},
	}, {
		dataField : "partName1",
		headerText : "부품명1",
		dataType : "string",
		width : 250,
		editable : false,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
			},
			onClick : function(event) {
				AUIGrid.openInputer(event.pid);
			}
		},
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			autoEasyMode : true,
			matchFromFirst : false,
			showEditorBtnOver : false,
			list : partName1,
			keyField : "oid",
			valueField : "name",
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = partName1.length; i < len; i++) {
					if (partName1[i]["name"] == newValue) {
						isValid = true;
						break;
					}
				}
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			}
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
			let retStr = "";
			for (let i = 0, len = partName1.length; i < len; i++) {
				if (partName1[i]["oid"] == value) {
					retStr = partName1[i]["name"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "partName2",
		headerText : "부품명2",
		dataType : "string",
		width : 250,
		editable : false,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
			},
			onClick : function(event) {
				AUIGrid.openInputer(event.pid);
			}
		},
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			autoEasyMode : true,
			matchFromFirst : false,
			showEditorBtnOver : false,
			list : partName2,
			keyField : "oid",
			valueField : "name",
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = partName2.length; i < len; i++) {
					if (partName2[i]["name"] == newValue) {
						isValid = true;
						break;
					}
				}
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			}
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
			let retStr = "";
			for (let i = 0, len = partName2.length; i < len; i++) {
				if (partName2[i]["oid"] == value) {
					retStr = partName2[i]["name"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "partName3",
		headerText : "부품명3",
		dataType : "string",
		width : 250,
		editable : false,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
			},
			onClick : function(event) {
				AUIGrid.openInputer(event.pid);
			}
		},
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			autoEasyMode : true,
			matchFromFirst : false,
			showEditorBtnOver : false,
			list : partName3,
			keyField : "oid",
			valueField : "name",
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = partName3.length; i < len; i++) {
					if (partName3[i]["name"] == newValue) {
						isValid = true;
						break;
					}
				}
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			}
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
			let retStr = "";
			for (let i = 0, len = partName3.length; i < len; i++) {
				if (partName3[i]["oid"] == value) {
					retStr = partName3[i]["name"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "count",
		headerText : "부품명4",
		dataType : "string",
		width : 250,
		editable : false,
	} ];

	function createAUIGrid() {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderTeext : "번호",
			showRowCheckColumn : true,
			showAutoNoDataMessage : false,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
			displayTreeOpen : true,
			editable : true,
			treeColumnIndex : 1,
			flat2tree : true,
			enableSorting : false,
			fixedColumnCount : 2,
			treeLazyMode : true,
			treeLevelIndent : 17,

// 			rowCheckDisabledFunction : function(rowIndex, isChecked, item) {
// 				if (item.disabled) {
// 					return false; // false 반환하면 disabled 처리됨
// 				}
// 				return true;
// 			}
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		loadGridData();
		AUIGrid.bind(myGridID, "treeLazyRequest", auiLazyLoadHandler);
		AUIGrid.bind(myGridID, "ready", auiReadyHandler);
	}

	function auiReadyHandler(event) {
		const data = AUIGrid.getGridData(event.pid);
		for (let i = 0; i < data.length; i++) {
			const item = data[i];
			const checked = item.checked;
			if (checked) {
				AUIGrid.addCheckedRowsByIds(myGridID, item._$uid);
			}
		}
	}

	function loadGridData() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/part/load");
		const params = {
			oid : oid,
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

	function auiLazyLoadHandler(event) {
		const item = event.item;
		const oid = item.oid;
		const level = item.level;
		const params = {
			oid : oid,
			level : level,
		}
		const url = getCallUrl("/part/lazyLoad")
		openLayer();
		call(url, params, function(data) {
			logger(data);
			if (data.result) {
				closeLayer();
				event.response(data.list);
			}
		})
	}

	function setNumber() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length === 0) {
			alert("채번할 품목을 선택하세요.");
			return false;
		}

		const url = getCallUrl("/part/setNumber");
		const params = {
			data : checkedItems
		}

	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid();
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>