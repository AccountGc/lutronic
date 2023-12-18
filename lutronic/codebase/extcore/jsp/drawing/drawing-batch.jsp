<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
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
						주 도면 일괄등록
					</div>
				</td>
				<td class="right">
					<input type="button" value="등록" title="등록" onclick="batch();">
					<input type="button" value="추가" title="추가" class="blue" onclick="addRow();">
					<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 740px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			let recentGridItem;
			const layout = [ {
				headerText : "관련품목",
				children : [ {
					dataField : "rows91",
					dataType : "string",
					visible : false
				}, {
					dataField : "partNumber",
					headerText : "품목번호",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					headerText : "관련품목",
					dataType : "string",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "부품추가",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item.oid;
							const url = getCallUrl("/part/popup?method=insert91&multi=false&rowId=0&limit=true");
							_popup(url, 1800, 900, "n");
						}
					}
				} ]
			}, {
				headerText : "첨부파일",
				children : [ {
					dataField : "primaryName",
					headerText : "파일명",
					width : 160,
					editable : false,
				}, {
					dataField : "primary",
					headerText : "파일",
					width : 160,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item.oid;
							const url = getCallUrl("/aui/primary?oid=" + oid + "&method=primary");
							_popup(url, 800, 200, "n");
						}
					},
				} ]
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					rowIdField : "oid",
					editable : true,
					headerHeight : 35,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleRows",
					hoverMode : "singleRow",
					wordWrap : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.bind(myGridID, "keyDown", auiKeyDownHandler);
				auiReadyHandler();
			}

			function auiReadyHandler() {
				AUIGrid.addRow(myGridID, {}, "first");
			}
			
			function auiKeyDownHandler(event) {
				if (event.keyCode == 13) {
					var selectedItems = AUIGrid.getSelectedItems(event.pid);
					var rowIndex = selectedItems[0].rowIndex;
					if (rowIndex === AUIGrid.getRowCount(event.pid) - 1) {
						AUIGrid.addRow(event.pid, {});
						return false;
					}
				}
				return true;
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(layout);
				AUIGrid.resize(myGridID);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
			
			// 품목추가
			function insert91(arr, rowId) {
				const rows91 = [];
				var item = arr[0].item;
				rows91.push(item);
				var number = item.number;
				AUIGrid.updateRowsById(myGridID, {
					oid : recentGridItem.oid,
					rows91 : rows91,
					partNumber : number
				});
			}
			
			// 첨부파일
			function primary(data) {
				AUIGrid.updateRowsById(myGridID, {
					oid : recentGridItem.oid,
					primary : data.cacheId,
					primaryName : data.name
				});
				recentGridItem = undefined;
			}
			
			// 추가
			function addRow() {
				AUIGrid.addRow(myGridID, {}, 'last');
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
			
			// 등록
			function batch() {
				const gridData = AUIGrid.getGridData(myGridID);
				for (let i = 0; i < gridData.length; i++) {
					const item = gridData[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item.oid);
					if (isNull(item.partNumber)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "품목을 선택해주세요.");
						return false;
					}
					if (isNull(item.primary)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 3, "첨부파일을 선택하세요.");
						return false;
					}
				}
				
				if (!confirm("등록 하시겠습니까?")) {
					return false;
				}
				
				const url = getCallUrl("/drawing/batch");
				const params = {
					gridData : gridData
				}
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.href = getCallUrl("/drawing/list");
					}
				});
			}
			
		</script>
	</form>
</html>