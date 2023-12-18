<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
				<th>이름(국문)</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-500">
				</td>
				<th>코드</th>
				<td class="indent5">
					<input type="text" name="code" id="code" class="width-500">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="선택" title="선택" class="green">
					<input type="button" value="검색" title="검색"  class="blue">
					<input type="button" value="초기화" title="초기화" class="gray">
					<input type="button" value="닫기" title="닫기" class="gray">
				</td>
			</tr>
		</table>

		<table>
			<tr>
				<td valign="top">
					<div id="grid_wrap" style="height: 645px; border-top: 1px solid #3180c3;"></div>
					<%@include file="/extcore/jsp/common/aui-context.jsp"%>
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "number",
					headerText : "이름(국문)",
					dataType : "string",
					width : 180,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name",
					headerText : "코드",
					dataType : "string",
					width : 380,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "location",
					headerText : "설명",
					dataType : "string",
					width : 180,
					filter : {
						showIcon : true,
						inline : true
					}
				}]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleRows",
					hoverMode : "singleRow",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : false,
					useContextMenu : true,
					enableRowCheckShiftKey : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					rowCheckToRadio : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
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
				 				let params = new Object();
				 				const url = getCallUrl("/part/list");
				 				const field = ["_psize","oid","partNumber","partName","predate","predate_modify","creator","state","model", "productmethod", "deptcode", "unit", "weight", "manufacture", "mat", "finish", "remarks", "specification", "ecoNo", "eoNo"];
				 				/* const latest = !!document.querySelector("input[name=latest]:checked").value; */
				 				/* params = toField(params, field); */
				 				/* params.latest = latest; */
				 				AUIGrid.showAjaxLoader(myGridID);
// 				 				parent.openLayer();
				 				call(url, params, function(data) {
									AUIGrid.removeAjaxLoader(myGridID);
									if (data.result) {
										document.getElementById("sessionid").value = data.sessionid;
										document.getElementById("curPage").value = data.curPage;
// 										document.getElementById("lastNum").value = data.list.length;
										AUIGrid.setGridData(myGridID, data.list);
									} else {
										alert(data.msg);
									}
// 									parent.closeLayer();
								});
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
				selectbox("model");
				selectbox("productmethod");
				selectbox("deptcode");
				selectbox("unit");
				selectbox("mat");
				selectbox("finish");
				selectbox("manufacture");
				finderUser("creator");
				twindate("created");
				twindate("modified");
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
			
			function addBtn(){
				const items = AUIGrid.getCheckedRowItemsAll(myGridID);
				if (items.length == 0) {
					alert("추가할 부품을 선택하세요.");
					return false;
				}
				opener.append(items);
				self.close();
			}
			
		</script>
	</form>
</body>
</html>