<%@page import="wt.session.SessionHelper"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
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
<body style="overflow-x: hidden;">
	<form>
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=user.getFullName()%>">
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						ECO 검색
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
				<th>ECO 번호</th>
				<td class="indent5">
					<input type="text" name="number" id="number" class="width-300">
				</td>
				<th>ECO 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
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
			</tr>
			<tr>
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
				<th>승인일</th>
				<td class="indent5">
					<input type="text" name="approveFrom" id="approveFrom" class="width-100">
					~
					<input type="text" name="approveTo" id="approveTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('approveFrom', 'approveTo')">
				</td>
			</tr>
			<tr>
				<th>인허가변경</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing" value="" checked="checked">
						<div class="state p-success">
							<label>
								<b>전체</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing" id="licensing" value="NONE">
						<div class="state p-success">
							<label>
								<b>N/A</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing" value="0">
						<div class="state p-success">
							<label>
								<b>불필요</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing" value="1">
						<div class="state p-success">
							<label>
								<b>필요</b>
							</label>
						</div>
					</div>
				</td>
				<th>위험통제</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="riskType" value="" checked="checked">
						<div class="state p-success">
							<label>
								<b>전체</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="riskType" id="riskType" value="NONE">
						<div class="state p-success">
							<label>
								<b>N/A</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="riskType" value="0">
						<div class="state p-success">
							<label>
								<b>불필요</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="riskType" value="1">
						<div class="state p-success">
							<label>
								<b>필요</b>
							</label>
						</div>
					</div>
				</td>
				<th>프로젝트코드</th>
				<td class="indent5">
					<input type="text" name="model" id="model" class="width-200">
					<input type="hidden" name="modelcode" id="modelcode">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearValue('model', 'code')">
				</td>
			</tr>
			<tr>
				<th class="lb pt5">완제품 품목</th>
				<td colspan="5" class="indent5 pt5">
					<jsp:include page="/extcore/jsp/change/include/complete-part-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('eco-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('eco-list');">
					<!-- 					<input type="button" value="▼펼치기" title="▼펼치기" class="red" onclick="spread(this);"> -->
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
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

		<div id="grid_wrap" style="height: 490px; border-top: 1px solid #3180c3;"></div>
		<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>
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
				},{
					dataField : "number",
					headerText : "ECO 번호",
					dataType : "string",
					width : 120,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/eco/view?oid=" + oid);
							_popup(url, 1600, 800, "n");
						}
					},
				}, {
					dataField : "name",
					headerText : "ECO 제목",
					dataType : "string",
					style : "aui-left",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/eco/view?oid=" + oid);
							_popup(url, 1600, 800, "n");
						}
					},
				}, {
					dataField : "model",
					headerText : "제품명",
					dataType : "string",
					width : 250,
					style : "aui-left"
				}, {
					dataField : "sendType",
					headerText : "ECO 타입",
					dataType : "string",
					width : 80,
				}, {
					dataField : "licensing_name",
					headerText : "인허가변경",
					dataType : "string",
					width : 100,
				}, {
					dataField : "riskType_name",
					headerText : "위험 통제",
					dataType : "string",
					width : 100,
				}, {
					dataField : "state",
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
					dataField : "createdDate",
					headerText : "등록일",
					dataType : "date",
					width : 100,
				}, {
					dataField : "approveDate",
					headerText : "승인일",
					dataType : "string",
					width : 100,
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
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", _auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
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
						label : "ECO 정보보기",
						callback : auiContextHandler
					}, {
						label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
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
				let url;
				let withdraw = false
				if ("승인중" === state) {
					withdraw = true;
				}
				switch (event.contextIndex) {
				case 0:
					url = getCallUrl("/eco/view?oid=" + oid);
					_popup(url, "", "", "f");
					break;
				case 1:
					break;
				case 2:
					url = getCallUrl("/workspace/history?oid=" + oid + "&popup=true");
					_popup(url, 1200, 400, "n");
					break;
				case 4:
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
				case 5:
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
				const url = getCallUrl("/eco/list");
				const field = [ "name", "number", "model", "creatorOid", "createdFrom", "createdTo", "approveFrom", "approveTo", "state" ];
				const rows104 = AUIGrid.getGridDataWithState(myGridID104, "gridState");
				params.rows104 = rows104;
				params.licensing = $('input[name=licensing]:checked').val();
				params.riskType = $('input[name=riskType]:checked').val();
				params = toField(params, field);
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				logger(params);
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
						totalPage = Math.ceil(data.total / data.pageSize);
						createPagingNavigator(data.total, data.curPage, data.sessionid);
						AUIGrid.setGridData(myGridID, data.list);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("number");
				const columns = loadColumnLayout("eco-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				createAUIGrid104(columns104);
				AUIGrid.resize(myGridID);
				AUIGrid.resize(myGridID104);
				selectbox("state");
				finderUser("creator");
				twindate("created");
				twindate("approve");
				selectbox("_psize");
				finderCode("model", "MODEL", "code");
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
				AUIGrid.resize(myGridID104);
			});

			// 등록
			function create() {
				location.href = getCallUrl("/eco/create");
			}
			function spread(target) {
				const e = document.querySelectorAll('.hidden');
				// 버근가..
				for (let i = 0; i < e.length; i++) {
					const el = e[i];
					const style = window.getComputedStyle(el);
					const display = style.getPropertyValue("display");
					if (display === "none") {
						el.style.display = "table-row";
						target.value = "▲접기";
						selectbox("state");
						finderUser("creator");
						twindate("created");
						twindate("modified");
						selectbox("_psize");
						selectbox("model");
						AUIGrid.resize(myGridID104);
					} else {
						el.style.display = "none";
						target.value = "▼펼치기";
						selectbox("state");
						finderUser("creator");
						twindate("created");
						twindate("modified");
						selectbox("_psize");
						selectbox("model");
						AUIGrid.resize(myGridID104);
					}
				}
			}

			function exportExcel() {
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("ECO 리스트", "ECO", "ECO 리스트", [], sessionName);
			}
		</script>
	</form>
</body>
</html>