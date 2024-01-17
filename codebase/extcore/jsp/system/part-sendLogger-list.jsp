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

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						품목 전송 현황
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
					<select name="_psize" id="_psize">
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
				},{
					dataField : "number",
					headerText : "품목번호",
					dataType : "string",
					width : 180,
				}, {
					dataField : "eoNumber",
					headerText : "EO/ECO번호",
					dataType : "string",
					width : 120,
				}, {
					dataField : "name",
					headerText : "품목명",
					dataType : "string",
					width : 250,
					style : "aui-left"
				}, {
					dataField : "meins",
					headerText : "단위",
					dataType : "string",
					width : 80,
				}, {
					dataField : "zpec",
					headerText : "사양",
					dataType : "string",
					width : 200,
				}, {
					dataField : "zmodel",
					headerText : "프로젝트 코드",
					dataType : "string",
					width : 150,
				}, {
					dataField : "zprodm",
					headerText : "제작방법",
					dataType : "string",
					width : 120,
				}, {
					dataField : "zdept",
					headerText : "부서",
					dataType : "string",
					width : 80,
				}, {
					dataField : "zdwgno",
					headerText : "도면번호",
					dataType : "string",
					width : 180,
				}, {
					dataField : "version",
					headerText : "REV",
					dataType : "string",
					width : 80,
				}, {
					dataField : "zprepo",
					headerText : "선구매여부",
					dataType : "string",
					width : 100,
				}, {
					dataField : "brgew",
					headerText : "중량",
					dataType : "string",
					width : 80,
				}, {
					dataField : "gewei",
					headerText : "중량단위",
					dataType : "string",
					width : 100,
				}, {
					dataField : "zmatlt",
					headerText : "재질",
					dataType : "string",
					width : 120,
				}, {
					dataField : "zpostp",
					headerText : "후처리",
					dataType : "string",
					width : 200,
				}, {
					dataField : "zdevnd",
					headerText : "개발공급업체",
					dataType : "string",
					width : 200,
				}, {
					dataField : "result",
					headerText : "전송결과",
					dataType : "string",
					width : 100
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
			}

			function loadGridData(movePage) {
				if (movePage === undefined) {
					document.getElementById("sessionid").value = 0;
					document.getElementById("curPage").value = 1;
				}
				let params = new Object();
				const url = getCallUrl("/system/part");
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
				exportToExcel("품목 전송 현황 리스트", "품목", "품목 전송 현황 리스트", [], sessionName);
			}
		</script>
	</form>
</body>
</html>