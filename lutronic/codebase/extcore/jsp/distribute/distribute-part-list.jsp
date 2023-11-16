<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.part.QuantityUnit"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> matList = (ArrayList<NumberCode>) request.getAttribute("matList");
ArrayList<NumberCode> productmethodList = (ArrayList<NumberCode>) request.getAttribute("productmethodList");
ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
ArrayList<NumberCode> finishList = (ArrayList<NumberCode>) request.getAttribute("finishList");
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
<body>
	<form>
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="state" id="state" value="APPROVED">

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
					<input type="hidden" name="location" id="location" value="<%=DrawingHelper.ROOTLOCATION%>">
					<span id="locationText"><%=DrawingHelper.ROOTLOCATION%></span>
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
					<select name="model" id="model" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode model : modelList) {
						%>
						<option value="<%=model.getCode()%>"><%=model.getName()%></option>
						<%
						}
						%>
					</select>
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
						<option value="<%=unit.toString() %>"><%=unit.getDisplay() %></option>
						<%
						}
						%>
					</select>
				</td>
				<th>무게</th>
				<td class="indent5">
					<input type="text" name="weight" id="weight" class="width-300">
				</td>
				<th>Manufacturer</th>
				<td class="indent5">
					<select name="manufacture" id="manufacture" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode manufacture : manufactureList) {
						%>
						<option value="<%=manufacture.getCode()%>"><%=manufacture.getName()%></option>
						<%
						}
						%>
					</select>
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
				<td class="indent5" colspan="3">
					<input type="text" name="eoNo" id="eoNo" class="width-300">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('distribute-part-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('distribute-part-list');">
					<input type="button" value="BOM 편집" title="BOM 편집" class="blue" onclick="editBOM();">
					<input type="button" value="▼펼치기" title="▼펼치기" class="red" onclick="spread(this);">
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100" selected="selected">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="검색" title="검색" id="searchBtn" onclick="loadGridData();">
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
						<jsp:param value="<%=DrawingHelper.ROOTLOCATION%>" name="location" />
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
					dataField : "_3d",
					headerText : "3D",
					dataType : "string",
					width : 60,
					renderer : {
						type : "ImageRenderer",
						altField : null,
						onClick : function(event) {
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "_2d",
					headerText : "2D",
					dataType : "string",
					width : 60,
					renderer : {
						type : "ImageRenderer",
						altField : null,
						onClick : function(event) {
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "step",
					headerText : "STEP",
					dataType : "string",
					width : 60,
					renderer : {
						type : "TemplateRenderer"
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "dxf",
					headerText : "DXF",
					dataType : "string",
					width : 60,
					renderer : {
						type : "TemplateRenderer"
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "pdf",
					headerText : "PDF",
					dataType : "string",
					width : 60,
					renderer : {
						type : "TemplateRenderer"
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					headerText : "변경이력",
					width : 80,
					renderer : {
						type : "IconRenderer",
						iconPosition : "aisleCenter", // 아이콘 위치
						iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
						iconHeight : 16,
						iconTableRef : { // icon 값 참조할 테이블 레퍼런스
							"default" : "/Windchill/extcore/images/help.gif" // default
						},
						onClick : function(event) {
							const oid = event.item.part_oid;
							const url = getCallUrl("/part/changeList?oid=" + oid);
							_popup(url, 1600, 800, "n");
						}
					},
					filter : {
						showIcon : false,
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
					filter : {
						showIcon : true,
						inline : true
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
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "location",
					headerText : "품목분류",
					dataType : "string",
					width : 180,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "version",
					headerText : "Rev.",
					dataType : "string",
					width : 90,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "remarks",
					headerText : "OEM Info.",
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
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "creator",
					headerText : "등록자",
					dataType : "string",
					width : 140,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createdDate",
					headerText : "등록일",
					dataType : "date",
					width : 140,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					width : 140,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "ecoNo",
					headerText : "BOM",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : true,
						inline : false
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
				$("input[name=sessionid").val(0);
				let params = new Object();
				const url = getCallUrl("/part/list");
				const field = [ "location", "partNumber", "partName", "createdFrom", "createdTo", "modifiedFrom", "modifiedTo", "creator", "model", "productmethod", "deptcode", "unit", "weight", "mat", "finish", "remarks",
						"ecoNo", "eoNo","creatorOid","specification","state"];
				params = toField(params, field);
				params.latest = true;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
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
				selectbox("model");
				selectbox("productmethod");
				selectbox("deptcode");
				selectbox("unit");
				selectbox("mat");
				selectbox("finish");
				selectbox("manufacture");
				selectbox("_psize");
				finderUser("creator");
				twindate("created");
				twindate("modified");
				$("#_psize").bindSelectSetValue(100);
			});

			function editBOM() {
				const items = AUIGrid.getCheckedRowItemsAll(myGridID);
				if (items.length == 0) {
					alert("편집할 부품을 선택하세요.");
					return false;
				}
				
				if (items.length > 1) {
					alert("한개만 선택해 주세요.");
					return false;
				}
				const oid = items[0].part_oid;
				var url = getCallUrl("/part/bomEditor") + "?oid="+oid;
				_popup(url, "1400", "600", "n");
				
			};

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
						selectbox("model");
						selectbox("productmethod");
						selectbox("deptcode");
						selectbox("unit");
						selectbox("mat");
						selectbox("finish");
						selectbox("manufacture");
						selectbox("_psize");
						finderUser("creator");
						twindate("created");
						twindate("modified");
					} else {
						el.style.display = "none";
						target.value = "▼펼치기";
						selectbox("model");
						selectbox("productmethod");
						selectbox("deptcode");
						selectbox("unit");
						selectbox("mat");
						selectbox("finish");
						selectbox("manufacture");
						selectbox("_psize");
						finderUser("creator");
						twindate("created");
						twindate("modified");
					}
				}
			}
			
			function exportExcel() {
				const exceptColumnFields = [ "_3d", "_2d", "step", "dxf", "pdf" ];
			    const sessionName = "<%=user.getFullName()%>";
			    exportToExcel("품목 리스트", "품목", "품목 리스트", exceptColumnFields, sessionName);
			}
		</script>
	</form>
</body>
</html>