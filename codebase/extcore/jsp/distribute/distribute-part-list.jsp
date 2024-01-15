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
// ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
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
		<input type="hidden" name="state" id="state" value="APPROVED">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=user.getFullName()%>">

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
					<input type="text" name="creator" id="creator" data-multi="false" class="width-300">
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
				<td class="indent5" colspan="5">
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
				<th>ECO No.</th>
				<td class="indent5">
					<input type="text" name="ecoNo" id="ecoNo" class="width-300">
				</td>
				<th>Eo No.</th>
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
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('distribute-part-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('distribute-part-list');">
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
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
					<div id="grid_wrap" style="height: 600px; border-top: 1px solid #3180c3;"></div>
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
				},{
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
							const url = getCallUrl("/distribute/partView?oid=" + oid);
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
							const url = getCallUrl("/distribute/partView?oid=" + oid);
							_popup(url, 1600, 800, "n");
						}
					},
				}, {
					dataField : "location",
					headerText : "품목분류",
					dataType : "string",
					width : 180,
					style : "aui-left"
				}, {
					dataField : "version",
					headerText : "REV",
					dataType : "string",
					width : 80,
					renderer : {
						type : "TemplateRenderer"
					},
				}, {
					dataField : "remarks",
					headerText : "OEM Info.",
					dataType : "string",
					width : 100,
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 100,
				}, {
					dataField : "creator",
					headerText : "등록자",
					dataType : "string",
					width : 140,
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
					enableRowCheckShiftKey : true,
					rowStyleFunction : function(rowIndex, item) {
						if (item.preOrder) {
							return "preOrder";
						} else if (item.checkout) {
							return "checkout";
						}
						return "";
					}
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
						label : "썸네일 보기(2D)",
						callback : auiContextHandler
					}, {
						label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
					}, {
						label : "일괄 다운로드",
						callback : auiContextHandler
					}, {
						label : "STEP 다운로드",
						callback : auiContextHandler
					}, {
						label : "DXF 다운로드",
						callback : auiContextHandler
					}, {
						label : "PDF 다운로드",
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
						label : "BOM 에디터",
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
					}, {
						label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
					}, {
						label : "재변환",
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
				const field = [ "location", "partNumber", "partName", "createdFrom", "createdTo", "modifiedFrom", "modifiedTo", "state", "model", "productmethod", "deptcode", "unit", "weight", "mat", "finish", "remarks", "ecoNo", "eoNo", "creatorOid", "specification" ];
				const url = getCallUrl("/part/list");
				const preOrder = document.querySelector("input[name=preOrder]:checked").value;
				params = toField(params, field);
				params.latest = true;
				params.preOrder = preOrder;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
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
				toFocus("partNumber");
				const columns = loadColumnLayout("distribute-part-list");
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
				finderCode("model", "MODEL");
				selectbox("productmethod");
				selectbox("deptcode");
				selectbox("unit");
				selectbox("mat");
				selectbox("finish");
				finderCode("manufacture", "MANUFACTURE";
				selectbox("_psize");
				finderUser("creator");
				twindate("created");
				twindate("modified");
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
				let url;
				// 				AUIGrid.setCheckedRowsByIds(myGridID, item._$uid);
				switch (event.contextIndex) {
				case 0:
					url = getCallUrl("/part/thumbnail?oid=" + part_oid);
					_popup(url, 800, 600, "n");
					break;
				case 1:
					url = getCallUrl("/part/thumbnail?oid=" + part_oid);
					_popup(url, 800, 600, "n");
					break;
				case 3:
					//일괄
					break;
				case 4:
					//STEP
					url = getCallUrl("/drawing/step?oid=" + part_oid);
					document.location.href = url;
					break;
				case 5:
					//DXF
					url = getCallUrl("/drawing/dxf?oid=" + part_oid);
					document.location.href = url;
					break;
				case 6:
					//PDF
					url = getCallUrl("/drawing/pdf?oid=" + part_oid);
					document.location.href = url;
					break;
				case 8:
					//속성
					url = getCallUrl("/part/attr?oid=" + part_oid);
					_popup(url, 1000, 500, "n");
					break;
				case 9:
					//BOM 뷰
					url = getCallUrl("/bom/view?oid=" + part_oid);
					_popup(url, 1600, 800, "n");
					break;
				case 10:
					url = getCallUrl("/bom/editor?oid=" + part_oid);
					_popup(url, "", "", "f");
					break;
				case 11:
					url = "/Windchill/netmarkets/jsp/structureCompare/StructureCompare.jsp?oid=OR:" + part_oid + "&ncId=5304500442831603818&locale=ko";
					_popup(url, 1600, 600, "n");
					break;
				case 12:
					url = getCallUrl("/part/viewHistory?oid=" + part_oid);
					_popup(url, 1200, 500, "n");
					break;
				case 14:
					url = getCallUrl("/part/upper?oid=" + part_oid);
					_popup(url, 600, 430, "n");
					break;
				case 15:
					url = getCallUrl("/part/lower?oid=" + part_oid);
					_popup(url, 600, 430, "n");
					break;
				case 16:
					url = getCallUrl("/part/end?oid=" + part_oid);
					_popup(url, 600, 430, "n");
					break;
				case 18:
					publish(part_oid);
					break;
				}
			};

			// 재변환
			function publish(oid) {
				const url = getCallUrl("/part/publish?oid=" + oid);
				parent.openLayer();
				call(url, null, function(data) {
					alert(data.msg);
					parent.closeLayer();
				}, "GET");
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
						$("#_psize").bindSelectSetValue("20");
					} else {
						el.style.display = "none";
						target.value = "▼펼치기";
						selectbox("state");
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
						$("#_psize").bindSelectSetValue("20");
					}
				}
			}

			function exportExcel() {
				const exceptColumnFields = [ "_3d", "_2d" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("품목 리스트", "품목", "품목 리스트", exceptColumnFields, sessionName);
			}
		</script>
	</form>
</body>
</html>