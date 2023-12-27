<%@page import="wt.org.WTUser"%>
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
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						물질 일괄링크
					</div>
				</td>
				<td class="right">
					<input type="button" value="등록" title="등록" onclick="link();">
					<input type="button" value="추가" title="추가" class="blue" onclick="addRow();">
					<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 785px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "partNumber",
				headerText : "모 부품코드",
				dataType : "string",
			}, {
				dataField : "rohsNumber",
				headerText : "자 물질코드",
				dataType : "string",
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					hoverMode : "singleRow",
					enableFilter : true,
					showInlineFilter : false,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					showRowCheckColumn : true,
					fillColumnSizeMode : true,
					editable : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				auiReadyHandler();
				AUIGrid.bind(myGridID, "keyDown", auiKeyDownHandler);
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
// 				AUIGrid.bind(myGridID, "pasteEnd", auiPasteEndHandler);
			}

// 			function auiPasteEndHandler(event) {
// 				logger(event);
// 			}

			function auiCellEditEndHandler(event) {
				const dataField = event.dataField;
				const item = event.item;
				logger(item);
				if (dataField === "rohsNumber") {
					const url = getCallUrl("/rohs/validateRohsNumber?number=" + item.rohsNumber);
					parent.openLayer();
					call(url, null, function(data) {
						if (!data.exist) {
							alert("존재하지 않는 RoHS번호입니다.");
							item.rohsNumber = "";
							AUIGrid.updateRow(myGridID, item, event.rowIndex);
						}
						parent.closeLayer();
					}, "GET");
				} else if (dataField === "partNumber") {
					logger(item.partNumber);
					const url = getCallUrl("/rohs/validatePartNumber?number=" + item.partNumber);
					parent.openLayer();
					call(url, null, function(data) {
						if (!data.exist) {
							alert("존재하지 않는 품목번호입니다.");
							item.partNumber = "";
							AUIGrid.updateRow(myGridID, item, event.rowIndex);
						}
						parent.closeLayer();
					}, "GET");
				}
			}

			function auiKeyDownHandler(event) {
				if (event.keyCode == 13) {
					const selectedItems = AUIGrid.getSelectedItems(event.pid);
					const rowIndex = selectedItems[0].rowIndex;
					if (rowIndex === AUIGrid.getRowCount(event.pid) - 1) {
						AUIGrid.addRow(event.pid, {});
						return false;
					}
				}
				return true;
			}

			function auiReadyHandler() {
				const item = new Object();
				AUIGrid.addRow(myGridID, item, 'last');
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});

			// 추가
			function addRow() {
				const item = new Object();
				AUIGrid.addRow(myGridID, item, 'last');
			}

			// 삭제
			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("삭제할 행을 선택하세요.");
					return false;
				}

				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			// 저장
			function link() {
				const data = AUIGrid.getGridData(myGridID);
				for (let i = 0; i < data.length; i++) {
					const item = data[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);

					if (isNull(item.partNumber)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "모 품목번호를 입력되지 않았습니다.");
						return false;
					}

					if (isNull(item.rohsNumber)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "자 물질코드가 입력되지 않았습니다.");
						return false;
					}
				}

				if (!confirm("등록 하시겠습니까?")) {
					return;
				}

				const params = {
					data : data
				}
				const url = getCallUrl("/rohs/link");
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.reload();
					}
					parent.closeLayer();
				});
			}
		</script>
	</form>
</body>
</html>