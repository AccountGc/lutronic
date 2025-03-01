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
		<input type="hidden" name="sortKey" id="sortKey">
		<input type="hidden" name="sortType" id="sortType">

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						ECA 활동함
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
				<th>EO/ECO 번호</th>
				<td colspan="3" class="indent5">
					<input type="text" name="number" id="number" class="width-300">
				</td>
			</tr>
			<tr>
				<th>기안자</th>
				<td class="indent5">
					<input type="text" name="submiter" id="submiter" data-multi="false">
					<input type="hidden" name="submiterOid" id="submiterOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('submiter')">
				</td>
				<th>도착일</th>
				<td class="indent5">
					<input type="text" name="receiveFrom" id="receiveFrom" class="width-100">
					~
					<input type="text" name="receiveTo" id="receiveTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('receiveFrom', 'receiveTo')">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('eca-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('eca-list');">
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


		<div id="grid_wrap" style="height: 610px; border-top: 1px solid #3180c3;"></div>
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
				},{
					dataField : "activityName",
					headerText : "활동구분",
					dataType : "string",
					width : 150,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const activityType = item.activityType;
							const oid = item.oid;
							const url = getCallUrl("/activity/info?activityType=" + activityType + "&oid=" + oid);
							document.location.href = url;
						}
					},
				}, {
					dataField : "type",
					headerText : "구분",
					dataType : "string",
					width : 80,
					sortable : false
				}, {
					dataField : "number",
					headerText : "EO/ECO 번호",
					dataType : "string",
					width : 120,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const t = item.type;
							const oid = item.eoid;
							let url;
							if (t === "ECO") {
								url = getCallUrl("/eco/view?oid=" + oid);
							} else {
								url = getCallUrl("/eo/view?oid=" + oid);
							}
							_popup(url, 1600, 800, "n");
						}
					},
				}, {
					dataField : "name",
					headerText : "EO/ECO 제목",
					dataType : "string",
					style : "aui-left",
				}, {
					dataField : "activityUser",
					headerText : "담당자",
					dataType : "string",
					width : 100,
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 80,
				}, {
					dataField : "finishDate",
					headerText : "완료 요청일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
				}, {
					dataField : "createdDate",
					headerText : "도착일",
					dataType : "date",
					formatString : "yyyy-mm-dd HH:MM:ss",
					width : 170,
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
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
// 				loadGridData();
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
			function auiSortingHandler(event) {
				const sortingFields = event.sortingFields;
				const key = sortingFields[0].dataField;
				const sortType = sortingFields[0].sortType; // 오름차순 1 내림 -1
				sortCache[0] = {
					dataField : key,
					sortType : sortType
				};
				document.getElementById("sortKey").value = key;
				document.getElementById("sortType").value = sortType;
			}


			function loadGridData(movePage) {
				if (movePage === undefined) {
					document.getElementById("sessionid").value = 0;
					document.getElementById("curPage").value = 1;
				}
				let params = new Object();
				const url = getCallUrl("/activity/eca");
				const field = [ "sortType", "sortKey", "number", "submiterOid", "receiveFrom", "receiveTo" ];
				params = toField(params, field);
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					logger(data);
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
						totalPage = Math.ceil(data.total / data.pageSize);
						createPagingNavigator(data.total, data.curPage, data.sessionid);
						AUIGrid.setGridData(myGridID, data.list);
						if (sortCache.length > 0) {
							AUIGrid.setSorting(myGridID, sortCache);
						}
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("number");
				const columns = loadColumnLayout("eca-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				finderUser("submiter");
				twindate("receive");
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
				exportToExcel("ECA활동함 리스트", "ECA활동함", "ECA활동함 리스트", [], sessionName);
			}
		</script>
	</form>
</body>
</html>