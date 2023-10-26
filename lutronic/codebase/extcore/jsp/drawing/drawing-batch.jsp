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
				<td>
					<input type="button" value="등록" title="등록" onclick="batch();">
					<input type="button" value="추가" title="추가" class="blue" onclick="addRow();">
					<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 570px; border-top: 1px solid #3180c3;"></div>
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
							const url = getCallUrl("/part/popup?method=insert91&multi=true");
							_popup(url, 1800, 900, "n");
						}
					}
				} ]
			}, {
				dataField : "epm",
				headerText : "주도면",
				dataType : "string",
				width : 500,
			}, {
				headerText : "첨부파일",
				children : [ {
					dataField : "secondaryName",
					headerText : "파일명",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					dataField : "secondary",
					headerText : "파일",
					width : 160,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item.oid;
							const url = getCallUrl("/aui/secondary?oid=" + oid + "&method=secondary");
							_popup(url, 800, 400, "n");
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
					selectionMode : "multipleCells",
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
			function insert91(arr, callBack) {
				const rows91 = [];
				let number = "";
				arr.forEach(function(dd) {
					const item = dd.item;
					rows91.push(item);
					number += item.number + "\n";
					if(item.state!='작업 중'){
						alert("부품이 작업중 상태가 아닙니다.");
						return;
					}
				})
				AUIGrid.updateRowsById(myGridID, {
					oid : recentGridItem.oid,
					rows91 : rows91,
					partNumber : toRowsExp(number)
				});
				callBack(true);
			}
			
			// 첨부파일
			function secondary(data) {
				const cacheId = [];
				let name = "";
				for (let i = 0; i < data.length; i++) {
					cacheId.push(data[i].cacheId);
					name += data[i].name + "\n";
				}
				// 개행 처리
				AUIGrid.updateRowsById(myGridID, {
					oid : recentGridItem.oid,
					secondary : cacheId,
					secondaryName : toRowsExp(name)
				});
				// 초기화
				recentGridItem = undefined;
			}
			
			// 개행 처리 
			function toRowsExp(value) {
				return value.replace(/\r|\n|\r\n/g, "<br/>");
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
					if (isNull(item.secondaryName)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 4, "첨부파일을 선택하세요.");
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
				debugger;
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