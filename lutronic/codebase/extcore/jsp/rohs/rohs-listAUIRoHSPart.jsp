<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
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
		 <table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						부품 현황
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
				<th>부품</th>
				<td class="indent5">
					<input type="text" name="partNumber" id="partNumber" class="width-300" readonly>
					<input type="hidden" name="partOid" id="partOid" />
					<input type="button" value="조회" title="조회" id="searchPart">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();"> 
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('listAUIRoHSPart');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('listAUIRoHSPart');"> 
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="검색" title="검색" id="searchBtn">
					<input type="button" value="일괄다운" title="일괄다운" id="batchROHSDown">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 645px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>
		
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "level",
					headerText : "Level",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "partNumber",
					headerText : "품번",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "partName",
					headerText : "품목명",
					dataType : "string",
					width : 300,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "partState",
					headerText : "상태",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "rohsState",
					headerText : "ROHS 상태",
					dataType : "string",
					width : 80,
					renderer : {
						type : "ImageRenderer",
						imgHeight : 16, // 이미지 높이, 지정하지 않으면 rowHeight에 맞게 자동 조절되지만 빠른 렌더링을 위해 설정을 추천합니다.
						altField : "color", // alt(title) 속성에 삽입될 필드명, 툴팁으로 출력됨
						srcFunction : function(rowIndex, columnIndex, value, item) {
							debugger;
							switch(value) {
								case 0:
								return "/Windchill/extcore/images/task_ready.gif";
								case 1:
								return "/Windchill/extcore/images/task_orange.gif";
								case 2:
								return "/Windchill/extcore/images/task_complete.gif";
								case 3:
								return "/Windchill/extcore/images/task_delay.gif";
								default:
								return "/Windchill/extcore/images/task_ready.gif";
							}
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "rohsNumber",
					headerText : "물질번호",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "rohsName",
					headerText : "물질명",
					dataType : "string",
					width : 200,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "rohslifeState",
					headerText : "물질 상태",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "fileName",
					headerText : "파일명",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "docType",
					headerText : "파일구분",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					fillColumnSizeMode: true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : true,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : false,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					fillColumnSizeMode: true,
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
				const url = getCallUrl("/rohs/listAUIRoHSPart");
				const field = ["_psize","partOid"];
				params = toField(params, field);
				AUIGrid.showAjaxLoader(myGridID);
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
// 						totalPage = Math.ceil(data.total / data.pageSize);
// 						document.getElementById("sessionid").value = data.sessionid;
// 						createPagingNavigator(data.curPage);
						AUIGrid.setGridData(myGridID, data.partRohslist);
					} else {
						alert(data.msg);
					}
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("listAUIRoHSPart");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
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
			
			// 검색
			$("#searchBtn").click(function(){
				if(isEmpty($("#partNumber").val())){
					alert("부품번호를 선택해 주세요.");
					return false;
				}
				
				loadGridData();
			});
			
			// 부품 조회
			$("#searchPart").click(function(){
				const url = getCallUrl("/part/popup?method=append&multi=false&rowId=0");
				_popup(url, 1500, 700, "n");
			});
			
			function append(items){
				$("#partNumber").val(items[0].item.number);
				$("#partOid").val(items[0].item.part_oid);
			}
		</script>
	</form>
</body>
</html>