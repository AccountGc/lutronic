<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
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
						수신함
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
				<th>결재 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
				<th>상태</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="state" value="ALL">
						<div class="state p-success">
							<label>
								<b>전체</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="state" value="COMPLETE">
						<div class="state p-success">
							<label>
								<b>완료</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="state" value="START" checked="checked">
						<div class="state p-success">
							<label>
								<b>수신확인중</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="state" value="REJECT">
						<div class="state p-success">
							<label>
								<b>반려</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<th>기안자</th>
				<td class="indent5">
					<input type="text" name="submiter" id="submiter" data-multi="false">
					<input type="hidden" name="submiterOid" id="submiterOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('submiter')">
				</td>
				<th>수신일</th>
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
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('receive-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('receive-list');">
					<input type="button" value="일괄수신처리" title="일괄수신처리" onclick="receives();">
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

		<div id="grid_wrap" style="height: 605px; border-top: 1px solid #3180c3;"></div>
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
					dataField : "reads",
					headerText : "확인",
					dataType : "boolean",
					width : 60,
					renderer : {
						type : "CheckBoxEditRenderer",
					},
					filter : {
						inline : false
					},
				}, {
					dataField : "type",
					headerText : "구분",
					dataType : "string",
					width : 80,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/workspace/lineView?oid=" + oid + "&columnType=COLUMN_RECEIVE");
							_popup(url, 1500, 800);
						}
					},
				}, {
					dataField : "role",
					headerText : "역할",
					dataType : "string",
					width : 80,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/workspace/lineView?oid=" + oid + "&columnType=COLUMN_RECEIVE");
							_popup(url, 1500, 800);
						}
					},
				}, {
					dataField : "name",
					headerText : "결재 제목",
					dataType : "string",
					style : "aui-left",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/workspace/lineView?oid=" + oid + "&columnType=COLUMN_RECEIVE");
							_popup(url, 1500, 800);
						}
					},
				}, {
					dataField : "point",
					headerText : "진행단계",
					dataType : "string",
					style : "right",
					renderer : {
						type : "TemplateRenderer"
					},
					filter : {
						inline : false
					},
					sortable : false
				}, {
					dataField : "submiter",
					headerText : "기안자",
					dataType : "string",
					width : 100,
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 80,
				}, {
					dataField : "receiveTime",
					headerText : "수신일",
					dataType : "date",
					formatString : "yyyy-mm-dd HH:MM:ss",
					width : 170,
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					showRowCheckColumn : true,
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
					rowCheckDisabledFunction : function(rowIndex, isChecked, item) {
						if (item.complete) {
							return false;
						}
						return true;
					}
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
				}
				let params = new Object();
				const url = getCallUrl("/workspace/receive");
				const state = document.querySelector("input[name=state]:checked").value;
				const field = [ "sortKey", "sortType", "name", "receiveFrom", "receiveTo", "submiterOid" ];
				params = toField(params, field);
				params.state = state;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
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
				toFocus("name");
				const columns = loadColumnLayout("receive-list");
				let contenxtHeader = genColumnHtml(columns);
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

			function exportExcel() {
				const exceptColumnFields = [ "reads", "point" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("수신함 리스트", "수신함", "수신함 리스트", exceptColumnFields, sessionName);
			}

			function receives() {
				const list = AUIGrid.getCheckedRowItemsAll(myGridID);
				if (list.length === 0) {
					alert("일괄수신처리 할 결재를 선택하세요.");
					return false;
				}
				if (!confirm("일괄 수신처리 하시겠습니까?")) {
					return false;
				}
				const url = getCallUrl("/workspace/receives");
				const params = {
					list : list
				};
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					}
				})
			}

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
		</script>
	</form>
</body>
</html>