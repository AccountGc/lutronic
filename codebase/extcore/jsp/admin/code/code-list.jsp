<%@page import="wt.session.SessionHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
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
		<input type="hidden" name="codeType" id="codeType">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=user.getFullName()%>">

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						코드체계관리
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
				<th>이름</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-200">
				</td>
				<th>코드</th>
				<td class="indent5">
					<input type="text" name="code" id="code" class="width-200">
				</td>
			</tr>
			<tr>
				<th>설명</th>
				<td class="indent5" colspan="3">
					<input type="text" name="description" id="description" class="width-200">
				</td>
				<!-- 				<th>활성화</th> -->
				<!-- 				<td> -->
				<!-- 					&nbsp; -->
				<!-- 					<div class="pretty p-switch"> -->
				<!-- 						<input type="radio" name="enabled" value="false" checked="checked"> -->
				<!-- 						<div class="state p-success"> -->
				<!-- 							<label> -->
				<!-- 								<b>ON</b> -->
				<!-- 							</label> -->
				<!-- 						</div> -->
				<!-- 					</div> -->
				<!-- 					&nbsp; -->
				<!-- 					<div class="pretty p-switch"> -->
				<!-- 						<input type="radio" name="enabled" value="true"> -->
				<!-- 						<div class="state p-success"> -->
				<!-- 							<label> -->
				<!-- 								<b>OFF</b> -->
				<!-- 							</label> -->
				<!-- 						</div> -->
				<!-- 					</div> -->
				<!-- 				</td> -->
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('code-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('code-list');">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="자식 추가" title="자식 추가" class="orange" onclick="addTreeRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
					<input type="button" value="저장" title="저장" class="gray" onclick="save();">
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="10">10</option>
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
					<input type="button" value="검색" title="검색" onclick="loadGridData();">
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
					<jsp:include page="/extcore/jsp/admin/code/code-tree.jsp" />
				</td>
				<td valign="top">&nbsp;</td>
				<td valign="top">
					<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
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
					width : 300,
				}, {
					dataField : "code",
					headerText : "코드",
					dataType : "string",
					width : 150,
				}, {
					dataField : "sort",
					headerText : "소트",
					dataType : "string",
					width : 100,
				}, {
					dataField : "description",
					headerText : "설명",
					dataType : "string",
					style : "aui-left",
				}, {
					dataField : "enabled",
					headerText : "활성화",
					dataType : "boolean",
					width : 100,
					renderer : {
						type : "CheckBoxEditRenderer",
						// 						showLabel: true,
						editable : true,
					// 						checkValue: "true",
					// 						unCheckValue: "false",
					},
					filter : {
						inline : false
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					rowIdField : "oid",
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					hoverMode : "singleRow",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					displayTreeOpen : false,
					enableRowCheckShiftKey : true,
					editable : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
			}

			function loadGridData() {
				const name = document.getElementById("name").value;
				const code = document.getElementById("code").value;
				const description = document.getElementById("description").value;
				// 				const enabled = document.querySelector("input[name=enabled]:checked").value;
				const codeType = document.getElementById("codeType").value;
				const params = {
					name : name,
					code : code,
					description : description,
					// 					enabled: enabled,
					codeType : codeType
				}
				const url = getCallUrl("/code/list");
				parent.openLayer();
				call(url, params, function(data) {
					logger(data);
					if (data.result) {
						AUIGrid.setGridData(myGridID, data.list);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				})
			}

			// 행 추가
			function addRow() {
				const codeType = document.getElementById("codeType").value;

				if (codeType === "") {
					alert("코드타입을 선택해주세요.");
				} else {
					const item = {
						enabled : true,
					};
					AUIGrid.addRow(myGridID, item, "first");
				}
			}

			// 행 삭제
			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				let check = true;
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const oid = checkedItems[i].item.oid;
					const params = {
						oid : oid
					}
					const url = getCallUrl("/code/check");
					logger(params);
					call(url, params, function(data) {
						if (!data.result) {
							alert(data.msg);
							check = false;
						}
					}, "POST", false);
				}

				if (check) {
					for (let i = checkedItems.length - 1; i >= 0; i--) {
						const rowIndex = checkedItems[i].rowIndex;
						AUIGrid.removeRow(myGridID, rowIndex);
					}
				}
			}

			// 자식 추가
			function addTreeRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length <= 0) {
					alert("자식행을 추가할 행을 선택하세요.");
					return false;
				}

				if (checkedItems.length > 1) {
					alert("하나의 행을 선택하세요.");
					return false;
				}

				const selItem = checkedItems[0].item;
				const parentRowId = selItem.oid;

				if (parentRowId.indexOf("NumberCode") == -1) {
					alert("새로 추가한 행을 먼저 저장해주세요.");
					return false;
				}

				const newItem = new Object();
				newItem.parentRowId = parentRowId;
				newItem.enabled = true;
				AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "selectionDown");
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("code-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				_createAUIGrid(_columns);
				selectbox("_psize");
			});

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
				AUIGrid.resize(_myGridID);
			});

			function save() {
				// 추가된 행
				const addedRowItems = AUIGrid.getAddedRowItems(myGridID);
				// 수정된 행
				const editedRowItems = AUIGrid.getEditedRowItems(myGridID);
				// 삭제된 행
				const removedRowItems = AUIGrid.getRemovedItems(myGridID);

				const codeType = document.getElementById("codeType").value;

				if (addedRowItems.length == 0 && editedRowItems.length == 0 && removedRowItems.length == 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				if (!confirm("저장하시겠습니까?")) {
					return false;
				}
				const params = {
					addRows : addedRowItems,
					editRows : editedRowItems,
					removeRows : removedRowItems,
					codeType : codeType,
				}
				const url = getCallUrl("/code/save");
				parent.openLayer();
				logger(params);
				call(url, params, function(data) {
					if (data.result) {
						alert(data.msg);
						loadGridData(codeType);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			function exportExcel() {
				const exceptColumnFields = [ "enabled" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("코드체계관리 리스트", "코드체계관리", "코드체계관리 리스트", exceptColumnFields, sessionName);
			}
		</script>
	</form>
</body>
</html>