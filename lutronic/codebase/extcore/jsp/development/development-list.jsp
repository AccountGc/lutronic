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
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>프로젝트코드</th>
				<td class="indent5"><select name="model" id="model" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
				</select></td>
				<th>프로젝트명</th>
				<td class="indent5"><input type="text" name="name" id="name" class="width-200"></td>
			</tr>
			<tr>
				<th>예상 시작일</th>
				<td class="indent5"><input type="text" name="developmentStart_Start" id="developmentStart_Start" class="width-100"> ~ <input type="text" name="developmentStart_End" id="developmentStart_End" class="width-100"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"
					onclick="clearFromTo('createdFrom', 'createdTo')"></td>
				<th>예상 종료일</th>
				<td class="indent5"><input type="text" name="developmentEnd_Start" id="developmentEnd_Start" class="width-100"> ~ <input type="text" name="developmentEnd_End" id="developmentEnd_End" class="width-100"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"
					onclick="clearFromTo('createdFrom', 'createdTo')"></td>
			</tr>
			<tr>
				<th>관리자</th>
				<td class="indent5"><input type="text" name="dm" id="dm" data-multi="false" class="width-200"> <input type="hidden" name="creatorOid" id="creatorOid"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')"></td>
				<th>상태</th>
				<td class="indent5"><select name="state" id="state" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
				</select></td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();"> 
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('development-list');"> 
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('development-list');"> 
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select> 
					<input type="button" value="검색" title="검색" onclick="loadGridData();">
					<input type="button" value="초기화" title="초기화" id="reset">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 645px; border-top: 1px solid #3180c3;"></div>
		<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>


		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "model",
					headerText : "프로젝트 코드",
					dataType : "string",
					width : 120,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/development/view?oid=" + oid);
							popup(url, 1600, 800);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name",
					headerText : "프로젝트명",
					dataType : "string",
					width : 350,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/development/view?oid=" + oid);
							popup(url, 1600, 800);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "dm",
					headerText : "관리자",
					dataType : "string",
					width : 250,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "developmentStart",
					headerText : "예상 시작일",
					dataType : "date",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "developmentEnd",
					headerText : "예상 종료일",
					dataType : "date",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "createdAt",
					headerText : "등록일",
					dataType : "date",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "state",
					headerText : "상태",
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
					fillColumnSizeMode : true,
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
				createPagingNavigator(1);
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
					const url = getCallUrl("/development/list");
 				const field = ["_psize","model","name","developmentStart_Start","developmentStart_End","developmentEnd_Start","developmentEnd_End", "dm","state"];
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
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("development-list");
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