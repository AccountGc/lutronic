<%@page import="com.e3ps.change.activity.dto.DefDTO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.change.beans.ROOTData"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<DefDTO> list = (ArrayList<DefDTO>) request.getAttribute("list");
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

		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>루트</th>
				<td class="indent5">
					<select name="root" id="root" class="width-200">
						<option value="">선택</option>
						<%
						for (DefDTO dto : list) {
						%>
						<option value="<%=dto.getOid()%>"><%=dto.getName()%></option>
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
					<div id="rootLayer">
						<input type="button" value="루트 추가" title="루트 추가" class="blue" onclick="create('root');">
						<input type="button" value="루트 수정" title="루트 수정" class="">
						<input type="button" value="루트 삭제" title="루트 삭제" class="red" onclick="">
					</div>
					<div id="actLayer">
						<input type="button" value="활동추가" title="활동추가" class="blue" onclick="create('act');">
						<input type="button" value="활동삭제" title="활동삭제" class="red" onclick="_delete();">
					</div>
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
		<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "stepName",
				headerText : "단계",
				dataType : "string",
				width : 120,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "name",
				headerText : "활동명",
				dataType : "string",
				width : 400,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "activityName",
				headerText : "활동구분",
				dataType : "string",
				width : 120,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "departName",
				headerText : "담당부서",
				dataType : "string",
				width : 120,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "activeUserName",
				headerText : "담당자",
				dataType : "string",
				width : 120,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "enabled",
				headerText : "활성화",
				dataType : "string",
				width : 120,
				filter : {
					showIcon : true,
					inline : true
				},
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					fillColumnSizeMode : true,
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : false,
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
				const url = getCallUrl("/activity/list");
				const field = [ "root" ];
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
						buttonControl();
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				selectbox("_psize");
				selectbox("root");
				buttonControl();
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

			// 버튼 제어
			function buttonControl() {
				const root = document.getElementById("root").value;
				const rootLayer = document.getElementById("rootLayer");
				const actLayer = document.getElementById("actLayer");

				if (root === "") {
					rootLayer.style.display = "";
					actLayer.style.display = "none";
				} else {
					rootLayer.style.display = "none";
					actLayer.style.display = "";
				}
			}

			function create(type) {
				const oid = document.getElementById("root").value;
				const url = getCallUrl("/activity/create?type=" + type + "&oid=" + oid);
				_popup(url, 600, 500, "n");
			}

			// Root 수정
			$("#updateRootDefinition").click(function() {
				const url = getCallUrl("/admin/updateRootDefinition") + "?oid=" + $("#rootOid").val();
				_popup(url, 600, 500, "n");
			})

			// Root 삭제
			$("#deleteRootDefinition").click(function() {
				var gridList = AUIGrid.getGridData(myGridID);
				if (gridList.length > 0) {
					alert("활동이 있을경우 삭제할 수 없습니다.");
					return;
				}

				if (!confirm("삭제 하시겠습니까?")) {
					return false;
				}

				let params = new Object();
				const oid = $("#rootOid").val();
				params.oid = oid;
				const url = getCallUrl("/admin/deleteRootDefinition");
				call(url, params, function(data) {
					if (data.result) {
						alert(data.msg);
						location.reload();
					} else {
						alert(data.msg);
					}
				});
			})

			// 활동 추가 
			$("#createActivity").click(function() {
				const url = getCallUrl("/admin/createActivityDefinition") + "?oid=" + $("#rootOid").val();
				_popup(url, 600, 550, "n");
			})

			// 활동 삭제

			function _delete() {
				const list = AUIGrid.getCheckedRowItemsAll(myGridID);
				if (items.length == 0) {
					alert("선택된 활동이 없습니다.");
					return;
				}

				if (!confirm("삭제하시겠습니까?")) {
					return;
				}

				const params = new Object();
				params.list = list;
				const url = getCallUrl("/activity/list");
				call(url, params, function(data) {
				}, "DELETE");
			}

			// AXISJ SELECT BOX 바인딩이... 퓨어 스크립트가 안먹히네
			$("#root").change(function() {
				loadGridData();
			})
		</script>
	</form>
</body>
</html>