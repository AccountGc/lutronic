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
					<input type="button" value="추가" title="추가" class="blue" onclick="addBtn();">
					<input type="button" value="삭제" title="삭제" class="red" onclick="javascript:self.close();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 570px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const layout = [ {
				dataField : "number",
				headerText : "품목구분<br>(1자리)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "대분류<br>(2자리)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "중분류<br>(2자리)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "SEQ<br>(3자리)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "CUSTOM<br>(2자리)",
				dataType : "string",
				width : 120,
			}, {
				headerText : "MAT",
				children : [ {
					dataField : "avg08",
					headerText : "재질코드",
					width : 120,
				}, {
					dataField : "sdev08",
					headerText : "재질명",
					width : 120,
				} ]
			}, {
				dataField : "number",
				headerText : "품목명<br>(대제목)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "품목명<br>(소제목)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "품목명<br>(KEY-IN)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "단위",
				dataType : "string",
				width : 120,
			}, {
				headerText : "부서",
				children : [ {
					dataField : "avg08",
					headerText : "부서코드",
					width : 120,
				}, {
					dataField : "sdev08",
					headerText : "부서명",
					width : 120,
				} ]
			}, {
				headerText : "프로젝트 코드",
				children : [ {
					dataField : "avg08",
					headerText : "프로젝트 코드",
					width : 120,
				}, {
					dataField : "sdev08",
					headerText : "프로젝트 명",
					width : 120,
				} ]
			}, {
				headerText : "제작방법",
				children : [ {
					dataField : "avg08",
					headerText : "제작방법 코드",
					width : 120,
				}, {
					dataField : "sdev08",
					headerText : "제작방법",
					width : 120,
				} ]
			}, {
				dataField : "number",
				headerText : "사양",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "주도면",
				dataType : "string",
				width : 120,
			}, ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 35,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					fillColumnSizeMode : false,
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : false,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
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
		</script>
	</form>
</html>