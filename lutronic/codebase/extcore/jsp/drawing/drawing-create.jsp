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
				<col width="180">
				<col width="*">
				<col width="180">
				<col width="*">
			</colgroup>
			<tr>
				<th>품목분류 <span style="color:red;">*</span></th>
				<td class="indent5" colspan="3">
					<span id="locationName">
               			/Default/PART_Drawing
               		</span>
				</td>
			</tr>
			<tr>
				<th>도번 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="number" id="number" class="width-500">
				</td>
				<th>도면명 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-500">
				</td>
			</tr>
			<tr>
				<th>도면설명</th>
				<td class="indent5">
					<input type="text" name="description" id="description" class="width-800">
				</td>
			</tr>
			<tr>
				<th>주 첨부파일 <span style="color:red;">*</span></th>
				<td class="indent5">
				</td>
			</tr>
			<tr>
				<th>첨부파일</th>
				<td class="indent5">
				</td>
			</tr>
		</table>
		
		<br>
		
		<table class="search-table">
			<colgroup>
				<col width="180">
				<col width="80">
				<col width="180">
				<col width="*">
				<col width="120">
			</colgroup>
			<tr>
				<th>관련 품목</th>
				<th><input type="checkbox" name="moduleType" id="moduleType"></th>
				<th>품목번호</th>
				<th>품목명</th>
				<th>Rev.</th>
		</table>
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" id="createBtn" onclick="loadGridData();">
					<input type="button" value="초기화" title="초기화" id="resetBtn" onclick="loadGridData();">
					<input type="button" value="목록" title="목록" id="listBtn" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "품목번호",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "number",
					headerText : "결과",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "description",
					headerText : "품목구분(*)",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "location",
					headerText : "대분류(*)",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "state",
					headerText : "중분류(*)",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "version",
					headerText : "SEQ",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "creator",
					headerText : "etc",
					dataType : "string",
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
				selectbox("type");
				selectbox("depart");
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