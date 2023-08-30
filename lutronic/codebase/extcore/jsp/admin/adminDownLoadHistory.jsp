<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// NumberCode c = NumberCode.newNumberCode();
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
	<form id="form">
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">

		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>유저 검색</th>
				<td>
					<input type="text" name="tempmanager" id="tempmanager" data-multi="false" class="width-200"> 
					<input type="hidden" name="manager" id="manager"> 
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
				<th>다운로드일</th>
				<td>
					<input type="text" name="datePre" id="datePre" class="width-100"> ~ <input type="text" name="datePost" id="datePost" class="width-100"> 
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();"> 
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('downloadHistory-list');"> 
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('downloadHistory-list');"> 
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="검색" title="검색" id="searchBtn">
					<input type="button" value="초기화" title="초기화" id="btnReset">
				</td>
			</tr>
		</table>
		<table>
			<colgroup>
				<col width="230">
				<col width="10">
				<col width="*">
			</colgroup>
			<tr>
				<td valign="top">
					<div id="grid_type" style="height: 670px; border-top: 1px solid #3180c3;"></div>
				</td>
				<td valign="top">&nbsp;</td>
				<td valign="top">
					<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
					<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
					<%@include file="/extcore/jsp/common/aui-context.jsp"%>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "이름",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "id",
					headerText : "아이디",
					dataType : "string",
					width : 400,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createDate",
					headerText : "해당모듈",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createDate",
					headerText : "다운횟수",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createDate",
					headerText : "다운시간",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createDate",
					headerText : "다운사유",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					showRowCheckColumn : false,
// 					rowNumHeaderText : "번호",
					fillColumnSizeMode: true,
					showAutoNoDataMessage : true,
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
// 				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
			}

			function loadGridData() {
// 				var params = _data($("#form"));
// 				var url = getCallUrl("/admin/loginHistory");
// 				call(url, params, function(data) {
// 					if (data.result) {
// 						AUIGrid.setGridData(myGridID, data.list);
// 					} else {
// 						alert(data.msg);
// 					}
// 				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("downloadHistory-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				createAUIGridType(typeColumns);
				selectbox("_psize");
				twindate("date");
				finderUser("tempmanager");
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
				AUIGrid.resize(typeGridID);
			});
			
			// 사이드 메뉴 그리드
			let typeGridID;
			const typeColumns = [ {
				dataField : "menuType",
				headerText : "관리종류",
				dataType : "string",
				filter : {
					showIcon : true,
					inline : true
				},
			} ]
			
			function createAUIGridType(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
// 					rowNumHeaderText : "번호",
					selectionMode: "multipleCells",
					enableFilter : true, 
					displayTreeOpen : true,
					useContextMenu : true
				}
				typeGridID = AUIGrid.create("#grid_type", columnLayout, props);
			}
		</script>
	</form>
</body>
</html>