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

		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>문서 템플릿 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
				<th>문서 템플릿 타입</th>
				<td class="indent5">
					<select name="formType" id="formType" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode n : formType) {
						%>
						<option value="<%=n.getName()%>"><%=n.getName()%></option>
						<%
						}
						%>
					</select>
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
				<div id="grid_wrap" style="height: 700px; border-top: 1px solid #3180c3;"></div>
				<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
				<%@include file="/extcore/jsp/common/aui-context.jsp"%>
			</td>
		</tr>
	</table>

	<script type="text/javascript">
		let myGridID;
		function _layout() {
			return [ {
				dataField : "formType",
				headerText : "템플릿 타입",
				dataType : "string",
				width : 150,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "number",
				headerText : "문서 템플릿 번호",
				dataType : "string",
				width : 150,
				filter : {
					showIcon : true,
					inline : true
				},
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
				dataField : "name",
				headerText : "문서 템플릿 제목",
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
						const url = getCallUrl("/form/view?oid=" + oid);
						document.location.href = url;
					}
				},
			}, {
				dataField : "version",
				headerText : "버전",
				dataType : "numeric",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
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
				width : 100,
				filter : {
					showIcon : true,
					inline : true
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
		}

		function loadGridData() {
			let params = new Object();
			const url = getCallUrl("/form/list");
			const field = [ "_psize", "name", "creator" ];
			params = toField(params, field);
			AUIGrid.showAjaxLoader(myGridID);
			parent.openLayer();
			call(url, params, function(data) {
				AUIGrid.removeAjaxLoader(myGridID);
				if (data.result) {
					createPagingNavigator(data.curPage);
					document.getElementById("sessionid").value = data.sessionid;
					AUIGrid.setGridData(myGridID, data.list);
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
	</script>
</body>
</html>