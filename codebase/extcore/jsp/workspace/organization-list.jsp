<%@page import="wt.session.SessionHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%
String oid = (String) request.getAttribute("oid");
JSONArray list = (JSONArray) request.getAttribute("list");
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
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=user.getFullName()%>">

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						조직도
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
				<th>부서 및 사원 관리</th>
				<td class="indent5">
					<input type="hidden" name="oid" id="oid" value="<%=oid%>">
					<span id="locationName">루트로닉</span>
				</td>
				<th>퇴사여부</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="isFire" value="false" checked="checked">
						<div class="state p-success">
							<label>
								<b>재직</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="isFire" value="true">
						<div class="state p-success">
							<label>
								<b>퇴사</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<th>이름</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
				<th>아이디</th>
				<td class="indent5">
					<input type="text" name="userId" id="userId" class="width-300">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('organization-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('organization-list');">
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="10">10</option>
						<option value="20" selected="selected">20</option>
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
					<%
					if (isAdmin) {
					%>
					<input type="button" value="저장" title="저장" class="red" onclick="save();">
					<%
					}
					%>
					<input type="button" value="검색" title="검색" onclick="loadGridData();">
				</td>
			</tr>
		</table>
		<table>
			<colgroup>
				<col width="300">
				<col width="10">
				<col width="*">
			</colgroup>
			<tr>
				<td valign="top">
					<jsp:include page="/extcore/jsp/workprocess/department-tree.jsp">
						<jsp:param value="670" name="height" />
					</jsp:include>
				</td>
				<td valign="top">&nbsp;</td>
				<td valign="top">
					<div id="grid_wrap" style="height: 605px; border-top: 1px solid #3180c3;"></div>
					<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
					<%@include file="/extcore/jsp/common/aui-context.jsp"%>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			let myGridID;
			const list =
		<%=list%>
			;
			const duty = [ "본부장/마스터", "마스터", "엑스퍼트/마스터", "팀장", "팀장/마스터", "코치", "프로" ];
			const auths = [ "나의업무", "문서관리", "품목관리", "도면관리", "설계변경", "RoHS", "금형관리" ];
			function _layout() {
				return [ {
					dataField : "rowNum",
					headerText : "번호",
					width : 40,
					dataType : "numeric",
					filter : {
						inline : false
					},
				}, {
					dataField : "id",
					headerText : "아이디",
					dataType : "string",
					width : 150,
					editable : false,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.woid;
							const url = getCallUrl("/org/view?oid=" + oid);
							_popup(url, 1000, 400, "n");
						}
					},
				}, {
					dataField : "name",
					headerText : "이름",
					dataType : "string",
					width : 120,
					editable : false,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.woid;
							const url = getCallUrl("/org/view?oid=" + oid);
							_popup(url, 1000, 400, "n");
						}
					},
				}, {
					dataField : "auth",
					headerText : "메뉴권한",
					dataType : "string",
					style : "aui-left",
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
					editRenderer : {
						type : "DropDownListRenderer",
						showEditorBtn : false,
						showEditorBtnOver : false,
						multipleMode : true,
						showCheckAll : true,
						list : auths,
					},
				},
				// 				{
				// 					dataField : "pdfAuth",
				// 					dataType : "boolean",
				// 					headerText : "PDF 권한",
				// 					width : 100,
				// 					renderer : {
				// 						type : "CheckBoxEditRenderer",
				// 						editable : true
				// 					}
				// 				},
				{
					dataField : "department_oid",
					headerText : "부서",
					dataType : "string",
					width : 150,
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
					editRenderer : {
						type : "ComboBoxRenderer",
						list : list,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "oid",
						valueField : "name",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = list.length; i < len; i++) {
								if (list[i]["name"] == newValue) {
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
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = list.length; i < len; i++) {
							if (list[i]["oid"] == value) {
								retStr = list[i]["name"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
				}, {
					dataField : "duty",
					headerText : "직위",
					dataType : "string",
					width : 130,
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
					editRenderer : {
						type : "DropDownListRenderer",
						showEditorBtn : false,
						showEditorBtnOver : false,
						multipleMode : false,
						showCheckAll : false,
						list : duty,
					},
				}, {
					dataField : "email",
					headerText : "이메일",
					dataType : "string",
					width : 250,
					style : "aui-left",
				}, {
					dataField : "isFire",
					headerText : "퇴사여부",
					dataType : "boolean",
					width : 100,
					renderer : {
						editable : true,
						type : "CheckBoxEditRenderer",
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					hoverMode : "singleRow",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : true,
					showInlineFilter : false,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
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
			}

			function loadGridData(movePage) {
				if (movePage === undefined) {
					document.getElementById("sessionid").value = 0;
					document.getElementById("curPage").value = 1;
				}
				let params = new Object();
				const url = getCallUrl("/org/organization");
				const field = [ "name", "userId", "oid" ];
				params = toField(params, field);
				const isFire = document.querySelector("input[name=isFire]:checked").value;
				params.isFire = JSON.parse(isFire);
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					logger(data);
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
						// 페이징처리..
						totalPage = Math.ceil(data.total / data.pageSize);
						createPagingNavigator(data.total, data.curPage, data.sessionid);
						AUIGrid.setGridData(myGridID, data.list);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			function save() {
				const editRows = AUIGrid.getEditedRowItems(myGridID);
				if (editRows.length === 0) {
					alert("변경 사항이 없습니다.");
					return false;
				}
				const url = getCallUrl("/org/save");
				const params = {
					editRows : editRows
				}
				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				logger(params);
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						// 						document.location.reload();
						loadGridData();
					}
					parent.closeLayer();
				})
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("name");
				const columns = loadColumnLayout("organization-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("_psize");
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

			function exportExcel() {
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("조직도 리스트", "조직도", "조직도 리스트", [], sessionName);
			}
		</script>
	</form>
</body>
</html>