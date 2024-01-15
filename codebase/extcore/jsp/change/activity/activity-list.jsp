<%@page import="wt.session.SessionHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.change.activity.dto.DefDTO"%>
<%@page import="java.util.ArrayList"%>
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
JSONArray alist = (JSONArray) request.getAttribute("alist");
JSONArray ulist = (JSONArray) request.getAttribute("ulist");
JSONArray slist = (JSONArray) request.getAttribute("slist");
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
						설계변경관리
					</div>
				</td>
			</tr>
		</table>
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
					</div>
					<div id="actLayer">
						<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
						<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('activity-list');">
						<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('activity-list');">
						<input type="button" value="루트 수정" title="루트 수정" onclick="modify();">
						<input type="button" value="루트 삭제" title="루트 삭제" class="red" onclick="_delete()">
						<input type="button" value="활동추가" title="활동추가" class="blue" onclick="create('act');">
						<input type="button" value="활동삭제" title="활동삭제" class="red" onclick="deleteRow();">
						<input type="button" value="저장" title="저장" onclick="save();">
					</div>
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="10">10</option>
						<option value="20" selected="selected">20</option>
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
		<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			const slist =
		<%=slist%>
			const alist =
		<%=alist%>
			const ulist =
		<%=ulist%>
			function _layout() {
				return [ {
					dataField : "rowNum",
					headerText : "번호",
					width : 40,
					dataType : "numeric",
					filter : {
						inline : false
					},
				},{
					dataField : "step",
					headerText : "단계",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
					renderer : {
						type : "IconRenderer",
						iconWidth : 16,
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : {
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
						},
						onClick : function(event) {
							AUIGrid.openInputer(event.pid);
						}
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = slist.length; i < len; i++) {
							if (slist[i]["key"] == value) {
								retStr = slist[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						list : slist,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = slist.length; i < len; i++) {
								if (slist[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						}
					},
				}, {
					dataField : "name",
					headerText : "활동명",
					dataType : "string",
					style : "aui-left",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "activity_type",
					headerText : "활동구분",
					dataType : "string",
					width : 200,
					filter : {
						showIcon : true,
						inline : true
					},
					renderer : {
						type : "IconRenderer",
						iconWidth : 16,
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : {
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
						},
						onClick : function(event) {
							AUIGrid.openInputer(event.pid);
						}
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = alist.length; i < len; i++) {
							if (alist[i]["key"] == value) {
								retStr = alist[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						list : alist,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = alist.length; i < len; i++) {
								if (alist[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						}
					},
				}, {
					dataField : "sort",
					headerText : "정렬",
					dataType : "numeric",
					width : 100,
					formatString : "###0",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true,
						maxlength : 2,
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "department_name",
					headerText : "담당부서",
					dataType : "string",
					width : 150,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "activeUser_oid",
					headerText : "담당자",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
					renderer : {
						type : "IconRenderer",
						iconWidth : 16,
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : {
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
						},
						onClick : function(event) {
							AUIGrid.openInputer(event.pid);
						}
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = ulist.length; i < len; i++) {
							if (ulist[i]["key"] == value) {
								retStr = ulist[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						list : ulist,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = ulist.length; i < len; i++) {
								if (ulist[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						}
					},
				} ]
			}
			
			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showStateColumn : true,
					showRowNumColumn : false,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					fillColumnSizeMode : true,
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					hoverMode : "singleRow",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : false,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					enableRowCheckShiftKey : true,
					editable : true
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
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
			}

			function auiCellEditEndHandler(event) {
				const dataField = event.dataField;
				if (dataField === "activeUser_name") {
					parent.openLayer();
					const value = event.value;
					const url = getCallUrl("/org/department?oid=" + value);
					call(url, null, function(data) {
						if (data.result) {
							const department_name = data.department_name;
							AUIGrid.setCellValue(event.pid, event.rowIndex, "department_name", department_name);
						} else {
							alert(data.msg);
						}
						parent.closeLayer();
					}, "GET");
				}
			}

			function loadGridData() {
				const root = document.getElementById("root").value;
				// 				if (root === "") {
				// 					buttonControl();
				// 					return false;
				// 				}
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
						createPagingNavigator(data.total, data.curPage);
						AUIGrid.setGridData(myGridID, data.list);
						buttonControl();
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("activity-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				selectbox("_psize");
				selectbox("root");
				buttonControl();
				$("#_psize").bindSelectSetValue("20");
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
				_popup(url, 600, 550, "n");
			}

			// Root 수정
			function modify() {
				const oid = document.getElementById("root").value;
				const url = getCallUrl("/activity/modify?oid=" + oid);
				_popup(url, 600, 500, "n");
			}

			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("삭제할 행을 선택하세요.");
					return false;
				}

				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			function save() {
				const editRows = AUIGrid.getEditedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);

				if (editRows.length === 0 && removeRows.length === 0) {
					alert("수정 내용이 없습니다.");
					return false;
				}

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/activity/save");

				const params = {
					editRows : editRows,
					removeRows : removeRows
				}
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					}
					parent.closeLayer();
				})
			}

			// 활동 루트 삭제
			function _delete() {
				const oid = document.getElementById("root").value;
				if (oid === "") {
					alert("삭제할 루트 활동을 선택하세요.");
					return false;
				}

				if (!confirm("삭제하시겠습니까?")) {
					return;
				}

				const params = new Object();
				params.oid = oid;
				const url = getCallUrl("/activity/delete");
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.reload();
					}
					parent.closeLayer();
				}, "DELETE");
			}

			// AXISJ SELECT BOX 바인딩이... 퓨어 스크립트가 안먹히네
			$("#root").change(function() {
				loadGridData();
			})
			
			function exportExcel() {
			    const sessionName = document.getElementById("sessionName").value;
			    exportToExcel("설계변경관리 리스트", "설계변경관리", "설계변경관리 리스트", [], sessionName);
			}
		</script>
	</form>
</body>
</html>