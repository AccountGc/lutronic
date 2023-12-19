<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
%>
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
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=user.getFullName()%>">

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						제품 현황
					</div>
				</td>
			</tr>
		</table>
		<!-- 제품 -->
		<jsp:include page="/extcore/jsp/change/include_selectPart.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="false" name="header" />
		</jsp:include>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('rohs-product-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('rohs-product-list');">
				</td>
				<td class="right">
					<input type="button" value="검색" title="검색" onclick="loadGridData();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 30px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>

		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "number",
					headerText : "제품코드",
					dataType : "string",
					width : 200,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name",
					headerText : "제품명",
					dataType : "string",
					style : "aui-left",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "creator",
					headerText : "등록자",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createDate",
					headerText : "등록일",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "rohsState",
					headerText : "RoHs 상태",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true,
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					hoverMode : "singleRow",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					enableRowCheckShiftKey : true,
					autoGridHeight : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
					vScrollChangeHandler(event);
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
			}

			function loadGridData() {
				let params = new Object();
				params.partList = AUIGrid.getGridData(partGridID);
				const url = getCallUrl("/rohs/product");
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
						AUIGrid.setGridData(myGridID, data.list);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("rohs-product-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				createAUIGrid2(columnsPart);
				AUIGrid.resize(partGridID);
			});

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			document.addEventListener("click", function(event) {
				hideContextMenu();
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
				AUIGrid.resize(partGridID);
			});

			function deleteBtn() {
				const checked = AUIGrid.getCheckedRowItems(partGridID);
				if (checked.length === 0) {
					alert("삭제할 행을 선택하세요.");
					return false;
				}

				for (let i = checked.length - 1; i >= 0; i--) {
					const rowIndex = checked[i].rowIndex;
					AUIGrid.removeRow(partGridID, rowIndex);
				}
			}

			function exportExcel() {
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("제품 현황 리스트", "제품 현황", "제품 현황 리스트", [], sessionName);
			}
		</script>
	</form>
</body>
</html>