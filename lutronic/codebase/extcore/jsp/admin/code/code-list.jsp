<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
</head>
<body>
	<form>
		<input type="hidden" name="codeType" id="codeType">
		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>이름</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-200">
				</td>
				<th>코드</th>
				<td class="indent5">
					<input type="text" name="code" id="code" class="width-200">
				</td>
			</tr>
			<tr>
				<th>설명</th>
				<td class="indent5">
					<input type="text" name="description" id="description" class="width-200">
				</td>
				<th>활성화</th>
				<td class="indent5">
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="enabled" value="true" checked="checked">
						<div class="state p-success">
							<label>
								<b>ON</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="enabled" value="false">
						<div class="state p-success">
							<label>
								<b>OFF</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('code-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('code-list');">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="자식 추가" title="자식 추가" class="orange" onclick="addTreeRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="검색" title="검색" class="blue" onclick="loadGridData();">
					<input type="button" value="목록 열기/닫기" title="목록 열기/닫기">
					<input type="button" value="저장" title="저장" class="red" onclick="save();">
				</td>
			</tr>
		</table>
		<table>
			<colgroup>
				<col width="230">
				<col width="10">
				<col width="*">
			</colgroup>
			<tr>
				<td valign="top">
					<jsp:include page="/extcore/jsp/admin/code/code-tree.jsp" />
				</td>
				<td valign="top">&nbsp;</td>
				<td valign="top">
					<div id="grid_wrap" style="height: 700px; border-top: 1px solid #3180c3;"></div>
					<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
					<%@include file="/extcore/jsp/common/aui-context.jsp"%>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "이름",
					dataType : "string",
					width : 300,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "code",
					headerText : "코드",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "sort",
					headerText : "소트",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "description",
					headerText : "설명",
					dataType : "string",
					style : "aui-left",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "enabled",
					headerText : "활성화",
					dataType : "string",
					width : 120,
					renderer : {
						type : "CheckBoxEditRenderer",
						edtiable : false,
					},
					filter : {
						showIcon : false,
						inline : false
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					rowIdField : "oid",
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : false,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					displayTreeOpen : false,
					editable : true,
// 					forceTreeView : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.bind(myGridID, "contextMenu", function(event) {
					const menu = [ {
						label : "행 추가",
						callback : auiContextHandler
					}, {
						label : "자식 추가",
						callback : auiContextHandler
					}, {
						label : "삭제",
						callback : auiContextHandler
					} ];
					return menu;
				});
// 				loadGridData();
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
			}

			function loadGridData() {
				const type = document.getElementById("codeType").value = type;
				const params = {
					type : type
				}
				const url = getCallUrl("/code/list");
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

			function auiReadyHandler() {
			//	AUIGrid.showItemsOnDepth(myGridID, 2);
			}
			
			// 행 추가
			function addRow() {
				const item = {
					enabled : true,
				};
				AUIGrid.addRow(myGridID, item, "first");
			}

			// 행 삭제
			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("삭제할 행을 선택하세요.");
					return false;
				}

				let check = true;
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const oid = checkedItems[i].item.oid;
					const params = {
						oid : oid
					}
					const url = getCallUrl("/code/check");
					call(url, params, function(data) {
						if (!data.result) {
							alet(data.msg);
							check = false;
						}
					}, "POST", false);
				}

				if (check) {
					for (let i = checkedItems.length - 1; i >= 0; i--) {
						const rowIndex = checkedItems[i].rowIndex;
						AUIGrid.removeRow(myGridID, rowIndex);
					}
				}
			}

			// 자식 추가
			function addTreeRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length <= 0) {
					alert("자식행을 추가할 행을 선택하세요.");
					return false;
				}

				if (checkedItems.length > 1) {
					alert("하나의 행을 선택하세요.");
					return false;
				}

				const selItem = checkedItems[0].item;
				const parentRowId = selItem.oid;
				const newItem = new Object();
				newItem.parentRowId = parentRowId;
				newItem.enabled = true;
				AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "selectionDown");
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("code-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				_createAUIGrid(_columns);
				selectbox("_psize");
			});

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					searchData();
				}
			})

			document.addEventListener("click", function(event) {
				hideContextMenu();
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
				AUIGrid.resize(_myGridID);
			});

			function auiContextHandler(event) {
				switch (event.contextIndex) {
				case 0:
					addRow();
					break;
				case 1:
					addTreeRow();
					break;
				case 2:
					deleteRow();
					break;
				}
			};

			function save() {
				const addedRowItems = AUIGrid.getAddedRowItems(myGridID);
				logger(addedRowItems);
				const editedRowItems = AUIGrid.getEditedRowItems(myGridID);
				const removedRowItems = AUIGrid.getRemovedItems(myGridID);
				if (addedRowItems.length == 0 && editedRowItems.length == 0 && removedRowItems.length == 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				if (!confirm("저장하시겠습니까?")) {
					return false;
				}
				const codeType = document.getElementById("codeType").value;
				const params = {
					addRow : addedRowItems,
					editRow : editedRowItems,
					removeRow : removedRowItems,
					codeType : codeType
				}
				const url = getCallUrl("/code/save");
				parent.openLayer();
				call(url, params, function(data) {
					if (data.result) {
						alert(data.msg);
						// 						loadGridData2(codeType);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}
		</script>
	</form>
</body>
</html>