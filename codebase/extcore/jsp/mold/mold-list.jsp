<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
ArrayList<NumberCode> moldTypeList = (ArrayList<NumberCode>) request.getAttribute("moldTypeList");
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser user = (WTUser) request.getAttribute("sessionUser");
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
		<input type="hidden" name="location" id="location" value="/Default/금형문서">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=user.getFullName()%>">
		<input type="hidden" name="sortKey" id="sortKey">
		<input type="hidden" name="sortType" id="sortType">

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						금형 검색
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
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>금형 번호</th>
				<td class="indent5">
					<input type="text" name="number" id="number" class="width-300">
				</td>
				<th>금형명</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
				<th>금형타입</th>
				<td class="indent5">
					<select name="moldtype" id="moldtype" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode moldType : moldTypeList) {
						%>
						<option value="<%=moldType.getCode()%>"><%=moldType.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th>상태</th>
				<td class="indent5">
					<select name="state" id="state" class="width-200">
						<option value="">선택</option>
						<%
						for (Map<String, String> lifecycle : lifecycleList) {
							if (!lifecycle.get("code").equals("TEMPRARY")) {
						%>
						<option value="<%=lifecycle.get("code")%>"><%=lifecycle.get("name")%></option>
						<%
						}
						}
						%>
					</select>
				</td>
				<th>등록자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" data-multi="false" class="width-200">
					<input type="hidden" name="creatorOid" id="creatorOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
				<th>등록일</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
				</td>
			</tr>
			<tr>
				<th>부서</th>
				<td class="indent5">
					<select name="deptcode" id="deptcode" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode deptcode : deptcodeList) {
						%>
						<option value="<%=deptcode.getCode()%>"><%=deptcode.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>MANUFACTURER</th>
				<td class="indent5">
					<input type="text" name="manufacture" id="manufacture" class="width-200">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('manufacture')">
				</td>
				<th>수정일</th>
				<td class="indent5">
					<input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100">
					~
					<input type="text" name="modifiedTo" id="modifiedTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('modifiedFrom', 'modifiedTo')">
				</td>
			</tr>
			<tr>
				<th>내부 문서번호</th>
				<td class="indent5">
					<input type="text" name="interalnumber" id="interalnumber" class="width-300">
				</td>
				<th>업체자체금형번호</th>
				<td class="indent5">
					<input type="text" name="moldnumber" id="moldnumber" class="width-300">
				</td>
				<th>REV</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="islastversion" value="true" checked="checked">
						<div class="state p-success">
							<label>
								<b>최신REV</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="islastversion" value="">
						<div class="state p-success">
							<label>
								<b>모든REV</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<th>금형개발비</th>
				<td class="indent5" colspan="5">
					<input type="text" name="moldcost" id="moldcost" class="width-300">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('mold-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('mold-list');">
				</td>
				<td class="right">
					<select name="_psize" id="_psize" onchange="loadGridData();">
						<option value="10">10</option>
						<option value="20" selected="selected">20</option>
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
					<input type="button" value="검색" title="검색" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<div id="grid_wrap" style="height: 500px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>
		<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "rowNum",
					headerText : "번호",
					width : 40,
					dataType : "numeric",
					filter : {
						inline : false
					},
					sortable : false
				}, {
					dataField : "number",
					headerText : "금형번호",
					dataType : "string",
					width : 200,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/mold/view?oid=" + oid);
							_popup(url, 1600, 800, "n");
						}
					},
				}, {
					dataField : "name",
					headerText : "금형명",
					dataType : "string",
					style : "aui-left",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/mold/view?oid=" + oid);
							_popup(url, 1600, 800, "n");
						}
					},
				}, {
					dataField : "version",
					headerText : "REV",
					dataType : "string",
					width : 80,
				}, {
					dataField : "stateDisplay",
					headerText : "상태",
					dataType : "string",
					width : 100,
					styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
						if (value === "승인됨") {
							return "approved";
						}
						return null;
					}
				}, {
					dataField : "creator",
					headerText : "등록자",
					dataType : "string",
					width : 100,
				}, {
					dataField : "createDate",
					headerText : "등록일",
					dataType : "string",
					width : 100,
				}, {
					dataField : "modifyDate",
					headerText : "수정일",
					dataType : "string",
					width : 100,
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					hoverMode : "singleRow",
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
				// 				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", _auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "sorting", auiSortingHandler);
			}

			let sortCache = [];
			function auiSortingHandler(event) {
				const sortingFields = event.sortingFields;
				const key = sortingFields[0].dataField;
				const sortType = sortingFields[0].sortType; // 오름차순 1 내림 -1
				sortCache[0] = {
					dataField : key,
					sortType : sortType
				};
				document.getElementById("sortKey").value = key;
				document.getElementById("sortType").value = sortType;
			}

			function _auiContextMenuHandler(event) {
				const item = event.item;
				if (event.target == "header") { // 헤더 컨텍스트
					if (nowHeaderMenuVisible) {
						hideContextMenu();
					}

					nowHeaderMenuVisible = true;

					// 컨텍스트 메뉴 생성된 dataField 보관.
					currentDataField = event.dataField;

					if (event.dataField == "id") { // ID 칼럼은 숨기기 못하게 설정
						$("#h_item_4").addClass("ui-state-disabled");
					} else {
						$("#h_item_4").removeClass("ui-state-disabled");
					}

					// 헤더 에서 사용할 메뉴 위젯 구성
					$("#headerMenu").menu({
						select : headerMenuSelectHandler
					});

					$("#headerMenu").css({
						left : event.pageX,
						top : event.pageY
					}).show();
				} else {
					hideContextMenu();
					const state = item.state;
					let withdraw = true;
					if ("승인중" === state) {
						withdraw = false;
					}
					const menu = [ {
						label : "금형 정보보기",
						callback : auiContextHandler
					}, {
						label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
					}, {
						label : "버전이력보기",
						callback : auiContextHandler
					}, {
						label : "결재이력보기",
						callback : auiContextHandler
					}, {
						label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
					}, {
						label : "결재회수(결재선유지)",
						callback : auiContextHandler,
						disable : withdraw
					}, {
						label : "결재회수(결재선초기화)",
						callback : auiContextHandler,
						disable : withdraw
					} ];
					return menu;
				}
			}

			function auiContextHandler(event) {
				const item = event.item;
				const oid = item.oid;
				const state = item.state;
				let withdraw = false;
				if ("승인중" === state) {
					withdraw = true;
				}
				let url;
				switch (event.contextIndex) {
				case 0:
					url = getCallUrl("/mold/view?oid=" + oid);
					_popup(url, "", "", "f");
					break;
				case 2:
					url = getCallUrl("/mold/iteration?oid=" + oid + "&popup=true");
					_popup(url, 1600, 600, "n");
					break;
				case 3:
					url = getCallUrl("/workspace/history?oid=" + oid + "&popup=true");
					_popup(url, 1200, 400, "n");
					break;
				case 5:
					if (!withdraw) {
						return false;
					}
					if (!confirm("기존 지정한 결재선 유지한 상태로 결재회수를 합니다.\n진행하시겠습니까?")) {
						return false;
					}
					url = getCallUrl("/workspace/withdraw?oid=" + oid + "&remove=false");
					parent.openLayer();
					call(url, null, function(data) {
						alert(data.msg);
						if (data.result) {
							parent.updateHeader();
							document.location.href = getCallUrl("/workData/list");
						} else {
							parent.closeLayer();
						}
					}, "GET");
					break;
				case 6:
					if (!withdraw) {
						return false;
					}
					if (!confirm("결재선을 초기화 상태로 결재회수를 합니다.\n진행하시겠습니까?")) {
						return false;
					}
					url = getCallUrl("/workspace/withdraw?oid=" + oid + "&remove=true");
					parent.openLayer();
					call(url, null, function(data) {
						alert(data.msg);
						if (data.result) {
							parent.updateHeader();
							document.location.href = getCallUrl("/workData/list");
						} else {
							parent.closeLayer();
						}
					}, "GET");
					break;
				}
			}

			function loadGridData(movePage) {
				if (movePage === undefined) {
					document.getElementById("sessionid").value = 0;
					document.getElementById("curPage").value = 1;
				}
				let params = new Object();
				const url = getCallUrl("/mold/list");
				const field = [ "sortKey", "sortType", "number", "name", "createdFrom", "createdTo", "modifiedFrom", "modifiedTo", "creatorOid", "state", "interalnumber", "deptcode", "description", "manufacture", "moldtype", "moldcost", "lifecycle", "location", "searchType", "moldnumber" ];
				params = toField(params, field);
				params.islastversion = $('input[name=islastversion]:checked').val();
				AUIGrid.showAjaxLoader(myGridID);
				logger(params);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
						totalPage = Math.ceil(data.total / data.pageSize);
						createPagingNavigator(data.total, data.curPage, data.sessionid);
						AUIGrid.setGridData(myGridID, data.list);
						if (sortCache.length > 0) {
							AUIGrid.setSorting(myGridID, sortCache);
						}
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("number");
				const columns = loadColumnLayout("mold-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("state");
				finderCode("manufacture", "MANUFACTURE");
				selectbox("moldtype");
				selectbox("deptcode");
				finderUser("creator");
				twindate("created");
				twindate("modified");
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
				exportToExcel("금형 리스트", "금형", "금형 리스트", [], sessionName);
			}
		</script>
	</form>
</body>
</html>