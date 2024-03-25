<%@page import="java.util.List"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="com.e3ps.common.util.PageQueryUtils"%>
<%@page import="wt.util.WTAttributeNameIfc"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="wt.doc.WTDocumentMaster"%>
<%@page import="com.e3ps.rohs.ROHSMaterial"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="com.e3ps.rohs.dto.RohsData"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser user = (WTUser) request.getAttribute("sessionUser");
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
		<input type="hidden" name="sortKey" id="sortKey">
		<input type="hidden" name="sortType" id="sortType">

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						BOM 전송 현황
					</div>
				</td>
			</tr>
		</table>

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
					<input type="text" name="number" id="number" class="width-300">
				</td>
				<th>EO/ECO 번호</th>
				<td class="indent5">
					<input type="text" name="eoNumber" id="eoNumber" class="width-300">
				</td>
			</tr>
			<tr>
				<th>전송일</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
				</td>
				<th>전송자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" data-multi="false" class="width-200">
					<input type="hidden" name="creatorOid" id="creatorOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('part-send-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('part-send-list');">
				</td>
				<td class="right">
					<select name="_psize" id="_psize" onchange="loadGridData();">
						<option value="10">10</option>
						<option value="20" selected="selected">20</option>
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
					<input type="button" value="검색" title="검색" onclick="loadGridData();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 640px; border-top: 1px solid #3180c3;"></div>
		<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>

		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "rowNum",
					headerText : "번호",
					width : 40,
					dataType : "numeric",
					filter : {
						inline : false
					},
					sortable : false
				}, {
					dataField : "oldParentNumber",
					headerText : "이전 모품번",
					dataType : "string",
					width : 200,
				}, {
					dataField : "oldParentVersion",
					headerText : "이전 모품번버전",
					dataType : "string",
					width : 100,
				}, {
					dataField : "oldChildNumber",
					headerText : "이전 자품번",
					dataType : "string",
					width : 200,
				}, {
					dataField : "oldChildVersion",
					headerText : "이전 자품번버전",
					dataType : "string",
					width : 100,
				}, {
					dataField : "newParentNumber",
					headerText : "신규 모품번",
					dataType : "string",
					width : 200,
				}, {
					dataField : "newParentVersion",
					headerText : "신규 모품번버전",
					dataType : "string",
					width : 100,
				}, {
					dataField : "newChildNumber",
					headerText : "신규 자품번",
					dataType : "string",
					width : 200,
				}, {
					dataField : "newChildVersion",
					headerText : "신규 자품번버전",
					dataType : "string",
					width : 100,
				}, {
					dataField : "qty",
					headerText : "수량",
					dataType : "string",
					width : 100,
				}, {
					dataField : "sendType",
					headerText : "전송타입",
					dataType : "string",
					width : 80,
				}, {
					dataField : "creator",
					headerText : "전송자",
					dataType : "string",
					width : 100,
				}, {
					dataField : "createdDate_txt",
					headerText : "전송일",
					dataType : "date",
					width : 130,
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
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
					enableRowCheckShiftKey : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "sorting", auiSortingHandler);
			}

			let sortCache = [];
			let compField;
			function auiSortingHandler(event) {
				const sortingFields = event.sortingFields;
				if (sortingFields.length > 0) {
					const key = sortingFields[0].dataField;
					if (compField !== key) {
						compField = key;
						const sortType = sortingFields[0].sortType; // 오름차순 1 내림 -1
						sortCache[0] = {
							dataField : key,
							sortType : sortType
						};
						document.getElementById("sortKey").value = key;
						document.getElementById("sortType").value = sortType;
						loadGridData();
					}
				}
			}

			function loadGridData(movePage) {
				if (movePage === undefined) {
					document.getElementById("sessionid").value = 0;
					document.getElementById("curPage").value = 1;
				}
				let params = new Object();
				const url = getCallUrl("/system/bom");
				const field = [ "number", "eoNumber", "creatorOid", "createdFrom", "createdTo" ];
				params = toField(params, field);
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
						totalPage = Math.ceil(data.total / data.pageSize);
						createPagingNavigator(data.total, data.curPage, data.sessionid);
						AUIGrid.setGridData(myGridID, data.list);
						if (movePage === undefined) {
							AUIGrid.setSorting(myGridID, sortCache);
							compField = null;
						}
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("number");
				const columns = loadColumnLayout("part-send-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				finderUser("creator");
				twindate("created");
				selectbox("_psize");
				$("#_psize").bindSelectSetValue("20");
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
			});

			function exportExcel() {
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("BOM 전송 현황 리스트", "BOM", "BOM 전송 현황 리스트", [], sessionName);
			}
		</script>
	</form>
</body>
</html>