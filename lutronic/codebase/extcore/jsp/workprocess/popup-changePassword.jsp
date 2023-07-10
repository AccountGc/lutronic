<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>
	<form>
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="lastNum" id="lastNum">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="oid" id="oid">

		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
			</colgroup>
			<tr bgcolor="ffffff">
				<td class="indent5" colspan="2">
					<span style="color: red;">
						비밀번호 변경 시 로그인을 다시 해야 합니다.
					</span>
				</td>
			</tr>
			<tr bgcolor="ffffff">
				<th>변경 비밀번호 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="password" name="field_view" id="field_view" class="width-300">
				</td>
			</tr>
			<tr bgcolor="ffffff">
				<th>변경 비밀번호 확인 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="password" name="_field_view" id="_field_view" class="width-300">
				</td>
			</tr>
		</table>
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="수정" title="수정" id="update">
					<input type="button" value="뒤로" title="뒤로" onclick="javascript:history.back();">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "아이디",
					dataType : "string",
					width : 60,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "number",
					headerText : "이름",
					dataType : "string",
					width : 180,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "description",
					headerText : "부서",
					dataType : "string",
					width : 380,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "location",
					headerText : "직위",
					dataType : "string",
					width : 180,
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
					showInlineFilter : true,
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
					vScrollChangeHandler(event);
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
			}

			function loadGridData() {
				// 				let params = new Object();
				// 				const url = getCallUrl("/doc/list");
				// 				const field = ["_psize","oid","name","number","description","state","creatorOid","createdFrom","createdTo"];
				// 				const latest = !!document.querySelector("input[name=latest]:checked").value;
				// 				params = toField(params, field);
				// 				params.latest = latest;
				// 				AUIGrid.showAjaxLoader(myGridID);
				// 				parent.openLayer();
				// 				call(url, params, function(data) {
				// 					AUIGrid.removeAjaxLoader(myGridID);
				// 					AUIGrid.setGridData(myGridID, data.list);
				// 					document.getElementById("sessionid").value = data.sessionid;
				// 					document.getElementById("curPage").value = data.curPage;document.getElementById("lastNum").value = data.list.length;
				// 					parent.closeLayer();
				// 				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("document-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("state");
				selectbox("cadDivision");
				selectbox("cadType");
				selectbox("model");
				selectbox("productmethod");
				selectbox("deptcode");
				selectbox("manufacture");
				selectbox("unit");
				selectbox("mat");
				selectbox("finish");
				finderUser("creator");
				twindate("created");
				twindate("modified");
				selectbox("_psize");
			});

			function exportExcel() {
// 				const exceptColumnFields = [ "primary" ];
// 				const sessionName = document.getElementById("sessionName").value;
// 				exportToExcel("문서 리스트", "문서", "문서 리스트", exceptColumnFields, sessionName);
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