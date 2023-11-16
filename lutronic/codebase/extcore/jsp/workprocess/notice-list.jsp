<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.ownership.Ownership"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="com.e3ps.groupware.notice.Notice"%>
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
		<input type="hidden" name="lastNum" id="lastNum">
		<input type="hidden" name="curPage" id="curPage">
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						공지사항 검색
					</div>
				</td>
			</tr>
		</table>

		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
				<th>등록자</th>
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
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('notice-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('notice-list');">
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
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
				dataField : "title",
				headerText : "제목",
				dataType : "string",
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true
				},
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/notice/view?oid=" + oid);
						_popup(url, 1600, 550, "n");
					}
				},
			}, {
				dataField : "creator",
				headerText : "등록자",
				dataType : "string",
				width : 150,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "createdDate",
				headerText : "등록일",
				dataType : "date",
				width : 170,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "count",
				headerText : "조회횟수",
				dataType : "string",
				width : 120,
				postfix : "번",
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "popup",
				headerText : "팝업",
				dataType : "string",
				width : 170,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "secondary",
				headerText : "첨부파일",
				dataType : "string",
				width : 100,
				renderer : {
					type : "TemplateRenderer"
				},
				filter : {
					showIcon : false,
					inline : false
				},
			} ]
		}

		function createAUIGrid(columnLayout) {
			const props = {
				headerHeight : 30,
				showRowNumColumn : true,
				showRowCheckColumn : true,
				rowNumHeaderText : "번호",
				showAutoNoDataMessage : false,
				selectionMode : "multipleCells",
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

		function loadGridData() {
			let params = new Object();
			const url = getCallUrl("/notice/list");
			const field = [ "name", "creatorOid" ];
			params = toField(params, field);
			AUIGrid.showAjaxLoader(myGridID);
			parent.openLayer();
			call(url, params, function(data) {
				AUIGrid.removeAjaxLoader(myGridID);
				if (data.result) {
					totalPage = Math.ceil(data.total / data.pageSize);
					document.getElementById("sessionid").value = data.sessionid;
					createPagingNavigator(data.curPage);
					AUIGrid.setGridData(myGridID, data.list);
				} else {
					alert(data.msg);
				}
				parent.closeLayer();
			});
		}

		document.addEventListener("DOMContentLoaded", function() {
			toFocus("name");
			const columns = loadColumnLayout("notice-list");
			const contenxtHeader = genColumnHtml(columns);
			$("#h_item_ul").append(contenxtHeader);
			$("#headerMenu").menu({
				select : headerMenuSelectHandler
			});
			createAUIGrid(columns);
			AUIGrid.resize(myGridID);
			finderUser("creator");
			selectbox("_psize");
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
			const url = getCallUrl("/notice/create");
			_popup(url, 1500, 650, "n");
		}
		
		function exportExcel() {
			const exceptColumnFields = [ "isPopup", "secondary" ];
			const sessionName = "<%=user.getFullName()%>";
			exportToExcel("공지사항 리스트", "공지사항", "공지사항 리스트", exceptColumnFields, sessionName);
		}
	</script>
</body>
</html>