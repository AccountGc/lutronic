<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
JSONArray list = (JSONArray) request.getAttribute("list");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
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
						ECN 검색
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
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>ECN 번호</th>
				<td class="indent5">
					<input type="text" name="number" id="number" class="width-260">
				</td>
				<th>ECN 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
				<th>상태</th>
				<td class="indent5">
					<select name="state" id="state" class="width-240">
						<option value="">선택</option>
						<%
						for (Map<String, String> lifecycle : lifecycleList) {
						%>
						<option value="<%=lifecycle.get("code")%>"><%=lifecycle.get("name")%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th>등록자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" data-multi="false" class="width-200">
					<input type="hidden" name="creatorOid" id="creatorOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
				<th>등록일</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
				</td>
				<th>프로젝트 코드</th>
				<td class="indent5">
					<input type="text" name="model" id="model" class="width-200">
					<input type="hidden" name="modelcode" id="modelcode">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearValue('model', 'code')">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('ecn-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('ecn-list');">
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
			const list =
		<%=list%>
			;
			function _layout() {
				return [ 
				<%if (isAdmin) {%>
				{
					dataField : "oid",
					dataType : "string",
					width : 100,
				},
				<%}%>
				{
					dataField : "rowNum",
					headerText : "번호",
					width : 40,
					dataType : "numeric",
					filter : {
						inline : false
					},
					sortable : false
				}, {
					dataField : "ecoNumber",
					headerText : "ECO 번호",
					dataType : "string",
					cellMerge : true,
					editable : false,
					width : 130,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.eoid;
							const url = getCallUrl("/eco/view?oid=" + oid);
							_popup(url, 1600, 800, "n");
						}
					},
				}, {
					dataField : "number",
					headerText : "ECN번호",
					dataType : "string",
					width : 130,
					editable : false,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/ecn/view?oid=" + oid);
							_popup(url, 1600, 800, "n");
						}
					},
				}, {
					dataField : "partNumber",
					headerText : "제품번호",
					dataType : "string",
					width : 150,
					editable : false,
				// 					renderer : {
				// 						type : "LinkRenderer",
				// 						baseUrl : "javascript",
				// 						jsCallback : function(rowIndex, columnIndex, value, item) {
				// 							const oid = item.oid;
				// 							const url = getCallUrl("/part/view?oid=" + oid);
				// 							_popup(url, 1600, 500, "n");
				// 						}
				// 					},
				}, {
					dataField : "partName",
					headerText : "제품명",
					dataType : "string",
					style : "aui-left",
					editable : false,
				}, {
					dataField : "progress",
					headerText : "상태",
					dataType : "string",
					width : 100,
					editable : false,
				}, {
					dataField : "worker_name",
					headerText : "담당자",
					dateType : "string",
					width : 100,
// 					editable : true,
// 				}, {
// 					dataField : "creator",
// 					headerText : "등록자",
// 					dataType : "string",
// 					width : 100,
// 					cellMerge : true,
// 					editable : false,
// 					mergeRef : "number",
// 					mergePolicy : "restrict",
				}, {
					dataField : "createdDate",
					headerText : "등록일",
					dataType : "date",
					width : 100,
					cellMerge : true,
					editable : false,
					mergeRef : "number",
					mergePolicy : "restrict",
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					enableCellMerge : true,
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
// 				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBeginHandler);
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
				const url = getCallUrl("/ecn/work");
				const field = [ "sortKey", "sortType", "name", "number", "state", "creatorOid", "createdFrom", "createdTo", "modelcode" ];
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

			function save() {
				const editRows = AUIGrid.getEditedRowItems(myGridID);
				if (editRows.length === 0) {
					alert("수정사항이 없습니다.");
					return false;
				}

				if (!confirm("저장하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/ecn/save");
				const params = {
					editRows : editRows
				};
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					}
					parent.closeLayer();
				})
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("number");
				const columns = loadColumnLayout("ecn-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("state");
				finderUser("creator");
				twindate("created");
				twindate("modified");
				selectbox("_psize");
				finderCode("model", "MODEL");
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
				exportToExcel("ECN 리스트", "ECN", "ECN 리스트", [], sessionName);
			}
		</script>
	</form>
</body>
</html>