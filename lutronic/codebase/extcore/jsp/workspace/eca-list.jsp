<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
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
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						ECA 활동함
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
				<th>EO/ECO 제목</th>
				<td colspan="3" class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
			</tr>
			<tr>
				<th>기안자</th>
				<td class="indent5">
					<input type="text" name="submiter" id="submiter" data-multi="false">
					<input type="hidden" name="submiterOid" id="submiterOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('submiter')">
				</td>
				<th>도착일</th>
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
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('eca-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('eca-list');">
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
				</td>
			</tr>
		</table>


		<div id="grid_wrap" style="height: 640px; border-top: 1px solid #3180c3;"></div>
		<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "step",
					headerText : "STEP",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "activityName",
					headerText : "활동구분",
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
							const activityType = item.activityType;
							const oid = item.oid;
							const url = getCallUrl("/activity/info?activityType=" + activityType + "&oid=" + oid);
							document.location.href = url;
						}
					},
				}, {
					dataField : "number",
					headerText : "EO/ECO 번호",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name",
					headerText : "EO/ECO 제목",
					dataType : "string",
					style : "aui-left",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "activityUser",
					headerText : "담당자",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "finishDate",
					headerText : "완료 요청일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "createdDate",
					headerText : "도착일",
					dataType : "date",
					formatString : "yyyy-mm-dd HH:MM:ss",
					width : 170,
					filter : {
						showIcon : true,
						inline : true,
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
				const url = getCallUrl("/activity/eca");
				const field = [ "name","submiterOid","receiveFrom","receiveTo" ];
				params = toField(params, field);
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					logger(data);
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
				const columns = loadColumnLayout("eca-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				finderUser("submiter");
				twindate("receive");
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
			
			function exportExcel() {
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("ECA활동함 리스트", "ECA활동함", "ECA활동함 리스트", [], sessionName);
			}
		</script>
	</form>
</body>
</html>