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
		<!-- 		<iframe id="download" name="download" style="display: none;"></iframe> -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						도면 일괄 다운로드
						<font color="red">
							<b>어셈블리의 경우 해당 하위도면 모두 다운받습니다.</b>
						</font>
					</div>
				</td>
				<td class="right">
					<div class="pretty p-switch">
						<input type="checkbox" name="pdf" value="true">
						<div class="state p-success">
							<label>
								<b>PDF</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="checkbox" name="dxf" value="true">
						<div class="state p-success">
							<label>
								<b>DXF</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="checkbox" name="step" value="true">
						<div class="state p-success">
							<label>
								<b>STEP</b>
							</label>
						</div>
					</div>
					&nbsp;
					<input type="button" value="다운로드" title="다운로드" onclick="zip();">
					<input type="button" value="삭제" class="red" title="삭제" onclick="deleteRow();">
					<input type="button" value="추가" class="blue" title="추가" onclick="addRow();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 60px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "thumb",
				headerText : "뷰",
				dataType : "string",
				width : 50,
				renderer : {
					type : "ImageRenderer",
					altField : null,
					imgHeight : 16,
				},
				filter : {
					inline : false
				},
				sortable : false
			}, {
				dataField : "icon",
				headerText : "",
				dataType : "string",
				width : 50,
				renderer : {
					type : "ImageRenderer",
					altField : null,
					imgHeight : 16,
				},
				filter : {
					inline : false
				},
			}, {
				dataField : "cadType",
				headerText : "CAD타입",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "도면번호",
				dataType : "string",
				width : 200,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.epm_oid;
						const url = getCallUrl("/drawing/view?oid=" + oid);
						_popup(url, 1600, 800, "n");
					}
				},
			}, {
				dataField : "name",
				headerText : "도면명",
				dataType : "string",
				style : "aui-left",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.epm_oid;
						const url = getCallUrl("/drawing/view?oid=" + oid);
						_popup(url, 1600, 800, "n");
					}
				},
			}, {
				dataField : "location",
				headerText : "도면분류",
				dataType : "string",
				width : 120,
				sortable : false
			}, {
				dataField : "version",
				headerText : "REV",
				dataType : "string",
				width : 100,
				renderer : {
					type : "TemplateRenderer"
				},
				sortable : false
			}, {
				dataField : "state",
				headerText : "상태",
				dataType : "string",
				width : 100,
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					if (value === "승인됨") {
						return "approved";
					}
					return null;
				}
			}, {
				dataField : "creator",
				headerText : "등록자",
				dataType : "string",
				width : 100,
			}, {
				dataField : "createdDate",
				headerText : "등록일",
				dataType : "date",
				width : 100,
			}, {
				dataField : "modifiedDate",
				headerText : "수정일",
				dataType : "date",
				width : 100,
			}, {
				dataField : "epm_oid",
				dataType : "string",
				visible : false
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					showStateColumn : true,
					headerHeight : 30,
					showRowNumColumn : false,
					showAutoNoDataMessage : false,
					showRowCheckColumn : true,
					selectionMode : "multipleCells",
					hoverMode : "singleRow",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					autoGridHeight : true,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
			}

			// 추가 버튼 클릭 시 팝업창 메서드
			function addRow() {
				const url = getCallUrl("/drawing/popup?method=insertRow");
				_popup(url, 1400, 700, "n");
			}

			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("삭제할 행을 선택하세요.");
					return false;
				}
				AUIGrid.removeCheckedRows(myGridID);
			}

			function insertRow(arr, callBack) {
				let checker = true;
				let number;
				arr.forEach(function(dd) {
					const rowIndex = dd.rowIndex;
					const item = dd.item;
					const unique = AUIGrid.isUniqueValue(myGridID, "epm_oid", item.epm_oid);
					if (!unique) {
						number = item.number;
						checker = false;
						return true;
					}
				})

				if (!checker) {
					callBack(true, false, number + " 도면은 이미 추가 되어있습니다.");
				} else {
					arr.forEach(function(dd) {
						const rowIndex = dd.rowIndex;
						const item = dd.item;
						AUIGrid.addRow(myGridID, item, rowIndex);
					})
				}
			}

			function zip() {
				const dxf = document.querySelector("input[name=dxf]:checked");
				const step = document.querySelector("input[name=step]:checked");
				const pdf = document.querySelector("input[name=pdf]:checked");
				const params = new Object();
				if (dxf != null) {
					params.dxf = JSON.parse(dxf.value);
				} else {
					params.dxf = false;
				}

				if (step != null) {
					params.step = JSON.parse(step.value);
				} else {
					params.step = false;
				}

				if (pdf != null) {
					params.pdf = JSON.parse(pdf.value);
				} else {
					params.pdf = false;
				}

				if (pdf == null && dxf == null && step == null) {
					alert("다운로드 파일 타입을 하나이상 선택하세요.");
					return false;
				}
				const gridData = AUIGrid.getGridData(myGridID);
				params.gridData = gridData;
				const url = getCallUrl("/drawing/zip");
				logger(params);
				parent.openLayer();
				call(url, null, function(data) {
					if (data.result) {
						const n = data.name;
						document.location.href = '/Windchill/extcore/jsp/common/content/FileDownload.jsp?fileName=' + n + '&originFileName=' + n;
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>