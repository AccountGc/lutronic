<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.part.QuantityUnit"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> matList = (ArrayList<NumberCode>) request.getAttribute("matList");
ArrayList<NumberCode> productmethodList = (ArrayList<NumberCode>) request.getAttribute("productmethodList");
ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
ArrayList<NumberCode> finishList = (ArrayList<NumberCode>) request.getAttribute("finishList");
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
QuantityUnit[] unitList = (QuantityUnit[]) request.getAttribute("unitList");
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
		<input type="hidden" name="oid" id="oid">
		<input type="hidden" name="sortKey" id="sortKey">
		<input type="hidden" name="sortType" id="sortType">

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						품목 검색
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
				<th>품목분류</th>
				<td class="indent5">
					<input type="hidden" name="oid" id="oid">
					<input type="hidden" name="location" id="location" value="<%=PartHelper.PART_ROOT%>">
					<span id="locationText"><%=PartHelper.PART_ROOT%></span>
				</td>
				<th>등록자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" class="width-200">
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
				<th>품목번호</th>
				<td class="indent5">
					<input type="text" name="partNumber" id="partNumber" class="width-300">
				</td>
				<th>품목명</th>
				<td class="indent5">
					<input type="text" name="partName" id="partName" class="width-300">
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
				<th>프로젝트코드</th>
				<td class="indent5">
					<input type="text" name="model" id="model" class="width-200">
					<input type="hidden" name="modelcode" id="modelcode">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearValue('model', 'code')">
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
				<th>REV</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="latest" value="true" checked="checked">
						<div class="state p-success">
							<label>
								<b>최신REV</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="latest" value="false">
						<div class="state p-success">
							<label>
								<b>모든REV</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
			<tr class="hidden">
				<th>제작방법</th>
				<td class="indent5">
					<select name="productmethod" id="productmethod" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode productmethod : productmethodList) {
						%>
						<option value="<%=productmethod.getCode()%>"><%=productmethod.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
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
				<th>사양</th>
				<td class="indent5">
					<input type="text" name="specification" id="specification" class="width-300">
				</td>
			</tr>
			<tr class="hidden">
				<th>단위</th>
				<td class="indent5">
					<select name="unit" id="unit" class="width-200">
						<option value="">선택</option>
						<%
						for (QuantityUnit unit : unitList) {
						%>
						<option value="<%=unit.toString()%>"><%=unit.getDisplay()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>무게</th>
				<td class="indent5">
					<input type="text" name="weight" id="weight" class="width-300">
				</td>
				<th>MANUFACTURER</th>
				<td class="indent5">
					<input type="text" name="manufacture" id="manufacture" class="width-200">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('manufacture')">
				</td>
			</tr>
			<tr class="hidden">
				<th>재질</th>
				<td class="indent5">
					<select name="mat" id="mat" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode mat : matList) {
						%>
						<option value="<%=mat.getCode()%>"><%=mat.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>후처리</th>
				<td class="indent5">
					<select name="finish" id="finish" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode finish : finishList) {
						%>
						<option value="<%=finish.getCode()%>"><%=finish.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>OEM Info.</th>
				<td class="indent5">
					<input type="text" name="remarks" id="remarks" class="width-300">
				</td>
			</tr>
			<tr class="hidden">
				<th>ECO NO</th>
				<td class="indent5">
					<input type="text" name="ecoNo" id="ecoNo" class="width-300">
				</td>
				<th>EO NO</th>
				<td class="indent5">
					<input type="text" name="eoNo" id="eoNo" class="width-300">
				</td>
				<th>선구매</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="preOrder" value="yes">
						<div class="state p-success">
							<label>
								<b>예</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="preOrder" value="no">
						<div class="state p-success">
							<label>
								<b>아니오</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="preOrder" value="all" checked="checked">
						<div class="state p-success">
							<label>
								<b>전체</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="excel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('part-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('part-list');">
				</td>
				<td class="right">
					<div class="pretty p-switch">
						<input type="checkbox" name="checkout" value="true" onclick="loadGridData();">
						<div class="state p-success">
							<label>
								<b>체크아웃</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="checkbox" name="comp" value="true" onclick="loadGridData();">
						<div class="state p-success">
							<label>
								<b>완제품</b>
							</label>
						</div>
					</div>
					&nbsp;
					<select name="_psize" id="_psize" onchange="loadGridData();">
						<option value="10">10</option>
						<option value="20" selected="selected">20</option>
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
					<input type="button" value="▼펼치기" title="▼펼치기" class="red" onclick="spread(this);">
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
					<jsp:include page="/extcore/jsp/common/folder-include.jsp">
						<jsp:param value="<%=PartHelper.PART_ROOT%>" name="location" />
						<jsp:param value="product" name="container" />
						<jsp:param value="list" name="mode" />
						<jsp:param value="605" name="height" />
					</jsp:include>
				</td>
				<td valign="top">&nbsp;</td>
				<td valign="top">
					<div id="grid_wrap" style="height: 570px; border-top: 1px solid #3180c3;"></div>
					<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
					<%@include file="/extcore/jsp/common/aui-context.jsp"%>
				</td>
			</tr>
		</table>

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
					dataField : "thumb",
					headerText : "뷰",
					dataType : "string",
					width : 50,
					renderer : {
						type : "ImageRenderer",
						altField : null,
						imgHeight : 16,
					},
					filter : {
						inline : false
					},
					sortable : false
				}, {
					dataField : "icon",
					headerText : "",
					dataType : "string",
					width : 50,
					renderer : {
						type : "ImageRenderer",
						altField : null,
						imgHeight : 16,
					},
					filter : {
						inline : false
					},
				}, {
					dataField : "number",
					headerText : "품목번호",
					dataType : "string",
					width : 180,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.part_oid;
							const url = getCallUrl("/part/view?oid=" + oid);
							_popup(url, 1600, 800, "n");
						}
					},
				}, {
					dataField : "name",
					headerText : "품목명",
					dataType : "string",
					style : "aui-left",
					width : 380,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.part_oid;
							const url = getCallUrl("/part/view?oid=" + oid);
							_popup(url, 1600, 800, "n");
						}
					},
				}, {
					dataField : "location",
					headerText : "품목분류",
					dataType : "string",
					width : 250,
					style : "aui-left",
					sortable : false
				}, {
					dataField : "version",
					headerText : "REV",
					dataType : "string",
					width : 80,
					renderer : {
						type : "TemplateRenderer"
					},
					sortable : false
				}, {
					dataField : "remarks",
					headerText : "OEM Info.",
					dataType : "string",
					width : 100,
					sortable : false
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
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
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
					enableRowCheckShiftKey : true,
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
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
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
				
				const _sortType = document.getElementById("sortType").value;
				if(Number(_sortType) !== Number(sortType)) {
					document.getElementById("sortKey").value = key;
					document.getElementById("sortType").value = sortType;
					loadGridData();
				}
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const item = event.item;
				const oid = item.part_oid;
				if ("thumb" === dataField) {
					const checkUrl = getCallUrl("/part/checkThumb?oid=" + oid);
					call(checkUrl, null, function(data) {
						if (data.result) {
							const exist = data.exist;
							if (exist) {
								const url = getCallUrl("/part/viewThumb?oid=" + oid);
								_popup(url, 800, 600, "n");
							} else {
								alert("썸네일이 없습니다.");
							}
						}
					}, "GET", false);
				}
			}

			function openCreoView(oid) {
				const callUrl = getCallUrl("/part/getCreoViewUrl?oid=" + oid);
				call(callUrl, null, function(res) {
					if (res.result) {
						const params = {
							browser : "chrome",
							linkurl : "/Windchill/wtcore/jsp/wvs/edrview.jsp?url=" + res.url
						};
						if (!checkUrl(res.url)) {
							alert("썸네일이 없습니다.\n자동 재변환을 진행합니다.");
							publish(oid);
							return false;
						}
						$.ajax({
							type : "POST",
							url : "/Windchill/netmarkets/jsp/wvs/wvsGW.jsp?class=com.ptc.wvs.server.ui.UIHelper&method=getOpenInCreoViewServiceCustomURI",
							data : jQuery.param(params, true),
							processData : false,
							async : true,
							dataType : "json",
							cache : false,
							timeout : 600000,
							success : function(res) {
								document.location.href = res.uri;
							}
						})
					}
				}, "GET");
			}

			function checkUrl(url) {
				const index = url.indexOf("ContentHolder=");
				if (index !== -1) {
					const str = url.substring(index + "ContentHolder=".length, index + "ContentHolder=".length + 1);
					if (str !== "&") {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}

			// 재변환
			function publish(oid) {
				const url = getCallUrl("/part/publish?oid=" + oid);
				parent.openLayer();
				call(url, null, function(data) {
					alert(data.msg);
					parent.closeLayer();
				}, "GET");
			}

			function _auiContextMenuHandler(event) {
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
					const menu = [ {
						label : "썸네일 보기(3D)",
						callback : auiContextHandler
					}, {
						label : "품목 정보보기",
						callback : auiContextHandler
					}, {
						label : "도면 정보보기(3D)",
						callback : auiContextHandler
					}, {
						label : "도면 정보보기(2D)",
						callback : auiContextHandler
					}, {
						label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
					}, {
						label : "속성보기",
						callback : auiContextHandler
					}, {
						label : "BOM 보기",
						callback : auiContextHandler
					}, {
						label : "BOM 편집",
						callback : auiContextHandler
					}, {
						label : "BOM 비교",
						callback : auiContextHandler
					}, {
						label : "ECO(변경)이력보기",
						callback : auiContextHandler
					}, {
						label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
					}, {
						label : "상위품목",
						callback : auiContextHandler
					}, {
						label : "하위품목",
						callback : auiContextHandler
					}, {
						label : "END ITEM",
						callback : auiContextHandler
					} ];
					return menu;
				}
			}

			function loadGridData(movePage) {
				if (movePage === undefined) {
					document.getElementById("sessionid").value = 0;
					document.getElementById("curPage").value = 1;
				}
				let params = new Object();
				const field = [ "sortKey", "sortType", "location", "partNumber", "partName", "createdFrom", "createdTo", "modifiedFrom", "modifiedTo", "state", "modelcode", "productmethod", "deptcode", "unit", "weight", "mat", "finish", "remarks", "ecoNo", "eoNo", "creatorOid", "specification" ];
				const url = getCallUrl("/part/list");
				const latest = document.querySelector("input[name=latest]:checked").value;
				const complete = document.querySelector("input[name=comp]:checked");
				const checkout = document.querySelector("input[name=checkout]:checked");
				const preOrder = document.querySelector("input[name=preOrder]:checked").value;
				params = toField(params, field);
				params.latest = JSON.parse(latest);
				params.preOrder = preOrder;
				if (complete !== null) {
					params.complete = JSON.parse(complete.value);
				} else {
					params.complete = false;
				}

				if (checkout != null) {
					params.checkout = JSON.parse(checkout.value);
				} else {
					params.checkout = false;
				}
				params.eca = false;
				logger(params);
				AUIGrid.showAjaxLoader(myGridID);
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
				toFocus("partNumber");
				const columns = loadColumnLayout("part-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				_createAUIGrid(_columns);
				AUIGrid.resize(_myGridID);
				selectbox("state");
				// 				selectbox("model");
				selectbox("productmethod");
				selectbox("deptcode");
				selectbox("unit");
				selectbox("mat");
				selectbox("finish");
				finderCode("manufacture", "MANUFACTURE");
				selectbox("_psize");
				finderUser("creator");
				twindate("created");
				twindate("modified");
				finderCode("model", "MODEL");
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

			function auiContextHandler(event) {
				const item = event.item;
				const part_oid = item.part_oid;
				const epm_oid = item.epm_oid;
				const epm_2d_oid = item.epm_2d_oid;
				let url;
				switch (event.contextIndex) {
				case 0:
					url = getCallUrl("/part/thumbnail?oid=" + part_oid);
					_popup(url, 800, 600, "n");
					break;
				case 1:
					url = getCallUrl("/part/view?oid=" + part_oid);
					_popup(url, 1600, 800, "n");
					break;
				case 2:
					if (epm_oid === null) {
						alert("도면(3D) 존재하지 않습니다.");
						return false;
					}
					url = getCallUrl("/drawing/view?oid=" + epm_oid);
					_popup(url, 1600, 800, "n");
					break;
				case 3:
					if (epm_2d_oid === null) {
						alert("도면(2D) 존재하지 않습니다.");
						return false;
					}
					url = getCallUrl("/drawing/view?oid=" + epm_2d_oid);
					_popup(url, 1600, 800, "n");
					break;
				case 5:
					//속성
					url = getCallUrl("/part/attr?oid=" + part_oid);
					_popup(url, 1000, 500, "n");
					break;
				case 6:
					//BOM 뷰
					url = getCallUrl("/bom/view?oid=" + part_oid);
					_popup(url, 1600, 800, "n");
					break;
				case 7:
					url = getCallUrl("/bom/editor?oid=" + part_oid);
					_popup(url, "", "", "f");
					break;
				case 8:
					url = "/Windchill/netmarkets/jsp/structureCompare/StructureCompare.jsp?oid=OR:" + part_oid + "&ncId=5304500442831603818&locale=ko";
					_popup(url, 1600, 600, "n");
					break;
				case 9:
					url = getCallUrl("/part/viewHistory?oid=" + part_oid);
					_popup(url, 1200, 500, "n");
					break;
				case 11:
					url = getCallUrl("/part/upper?oid=" + part_oid);
					_popup(url, 600, 430, "n");
					break;
				case 12:
					url = getCallUrl("/part/lower?oid=" + part_oid);
					_popup(url, 600, 430, "n");
					break;
				case 13:
					url = getCallUrl("/part/end?oid=" + part_oid);
					_popup(url, 600, 430, "n");
					break;
				case 15:
					publish(part_oid);
					break;
				}
			};

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
						// 						selectbox("model");
						finderCode("model", "MODEL");
						selectbox("productmethod");
						selectbox("deptcode");
						selectbox("unit");
						selectbox("mat");
						selectbox("finish");
						finderCode("manufacture", "MANUFACTURE");
						selectbox("_psize");
						finderUser("creator");
						twindate("created");
						twindate("modified");
					} else {
						el.style.display = "none";
						target.value = "▼펼치기";
						selectbox("state");
						// 						selectbox("model");
						finderCode("model", "MODEL");
						selectbox("productmethod");
						selectbox("deptcode");
						selectbox("unit");
						selectbox("mat");
						selectbox("finish");
						finderCode("manufacture", "MANUFACTURE");
						selectbox("_psize");
						finderUser("creator");
						twindate("created");
						twindate("modified");
					}
				}
			}

			function exportExcel() {
				const exceptColumnFields = [ "_3d", "_2d" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("품목 리스트", "품목", "품목 리스트", exceptColumnFields, sessionName);
			}
			
			function exportExcel() {
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("ECO 리스트", "ECO", "ECO 리스트", [], sessionName);
			}
			
			function excel() {

				if (!confirm("품목 리스트를 다운받습니다.")) {
					return false;
				}

				const url = getCallUrl("/part/excelList");
				parent.openLayer();
				call(url, null, function(data) {
					if (data.result) {
						const n = data.name;
						document.location.href = '/Windchill/extcore/jsp/common/content/FileDownload.jsp?fileName=' + n + '&originFileName=' + n;
					}
					parent.closeLayer();
				}, "GET");
			}
		</script>
	</form>
</body>
</html>