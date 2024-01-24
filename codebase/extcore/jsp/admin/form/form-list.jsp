<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.ownership.Ownership"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="com.e3ps.groupware.notice.Notice"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> formType = (ArrayList<NumberCode>) request.getAttribute("formType");
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
						문서 템플릿관리
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>문서 템플릿 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('form-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('form-list');">
				</td>
				<td class="right">
					<select name="_psize" id="_psize" onchange="loadGridData();">
						<option value="10">10</option>
						<option value="20" selected="selected">20</option>
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<input type="button" value="검색" title="검색" onclick="loadGridData();">
				</td>
			</tr>
		</table>
	</form>
	<table>
		<tr>
			<td valign="top">
				<div id="grid_wrap" style="height: 640px; border-top: 1px solid #3180c3;"></div>
				<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
				<%@include file="/extcore/jsp/common/aui-context.jsp"%>
			</td>
		</tr>
	</table>

	<script type="text/javascript">
		let myGridID;
		function _layout() {
			return [ {
				dataField : "rowNum",
				headerText : "번호",
				width : 40,
				dataType : "numeric",
				sortable : false
			}, {
				dataField : "name",
				headerText : "문서 템플릿 제목",
				dataType : "string",
				style : "aui-left",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/form/view?oid=" + oid);
						document.location.href = url;
					}
				},
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
				showInlineFilter : false,
				useContextMenu : true,
				enableRightDownFocus : true,
				filterLayerWidth : 320,
				filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
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
			const url = getCallUrl("/form/list");
			const field = [ "name", "formType" ];
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
			toFocus("name");
			const columns = loadColumnLayout("form-list");
			const contenxtHeader = genColumnHtml(columns);
			$("#h_item_ul").append(contenxtHeader);
			$("#headerMenu").menu({
				select : headerMenuSelectHandler
			});
			createAUIGrid(columns);
			AUIGrid.resize(myGridID);
			selectbox("_psize");
			selectbox("formType");
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

		function create() {
			const url = getCallUrl("/form/create");
			document.location.href = url;
		}

		function exportExcel() {
			const sessionName = document.getElementById("sessionName").value;
			exportToExcel("문서 템플릿관리 리스트", "문서 템플릿관리", "문서 템플릿관리 리스트", [], sessionName);
		}
	</script>
</body>
</html>