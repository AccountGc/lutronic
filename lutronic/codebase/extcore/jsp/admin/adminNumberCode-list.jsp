<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// NumberCode c = NumberCode.newNumberCode();
// c.setCode("01");
// NumberCodeType NCodeType = NumberCodeType.toNumberCodeType("PARTTYPE");
// c.setCodeType(NCodeType);
// c.setName("Nd:YAG Laser");
// NumberCode p= (NumberCode)CommonUtil.getObject("com.e3ps.common.code.NumberCode:156904");
// c.setParent(p);
// c.setSort("001");
// PersistenceHelper.manager.save(c);
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
	<form id="form">
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">

		<table class="search-table">
<!-- 			<colgroup> -->
<!-- 				<col width="130"> -->
<!-- 				<col width="*"> -->
<!-- 				<col width="130"> -->
<!-- 				<col width="*"> -->
<!-- 			</colgroup> -->
			<tr>
				<th>이름(국문)</th>
				<td>
					<input type="text" name="name" id="name" class="width-200">
				</td>
				<th>이름(영문)</th>
				<td>
					<input type="text" name="engName" id="engName" class="width-200">
				</td>
				<th>코드</th>
				<td>
					<input type="text" name="code" id="code" class="width-200">
				</td>
			</tr>
			<tr>
				<th>소트</th>
				<td>
					<input type="text" name="sort" id="sort" class="width-200">
				</td>
				<th>설명</th>
				<td>
					<input type="text" name="description" id="description" class="width-200">
				</td>
				<th>활성화</th>
				<td>
					<input type="checkbox" name="enabled" id="enabled">
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();"> 
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('numberCode-list');"> 
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('numberCode-list');"> 
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="등록" title="등록" id="create" class="blue">
					<input type="button" value="수정" title="수정" id="update" style="display: none;">
					<input type="button" value="삭제" title="삭제" id="delete" style="display: none;">
					<input type="button" value="초기화" title="초기화" id="reset">
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
					<jsp:include page="/extcore/jsp/admin/adminNumberCode.jsp">
						<jsp:param value="670" name="codeheight" />
					</jsp:include>
				</td>
				<td valign="top">&nbsp;</td>
				<td valign="top">
					<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
					<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
					<%@include file="/extcore/jsp/common/aui-context.jsp"%>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "이름(국문)",
					dataType : "string",
					width : 160,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "engName",
					headerText : "이름(영문)",
					dataType : "string",
					width : 160,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "code",
					headerText : "코드",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "sort",
					headerText : "소트",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "description",
					headerText : "설명",
					dataType : "string",
					width : 400,
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
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					showRowCheckColumn : false,
// 					rowNumHeaderText : "번호",
					fillColumnSizeMode: true,
					showAutoNoDataMessage : true,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : false,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
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
				params.codeType = 'PARTTYPE';
				const url = getCallUrl("/admin/numberCodeTree");
				call(url, params, function(data) {
					if (data.result) {
						AUIGrid.setGridData(myGridID, data.treeList);
					} else {
						alert(data.msg);
					}
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("numberCode-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				createAUIGridCode(codeColumns);
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
				AUIGrid.resize(codeGridID);
			});
			
			function loadGridData2(type) {
				let params = new Object();
				params.codeType = type;
				const url = getCallUrl("/admin/numberCodeTree");
				call(url, params, function(data) {
					if (data.result) {
						AUIGrid.setGridData(myGridID, data.treeList);
					} else {
						alert(data.msg);
					}
				});
			}
			
		</script>
	</form>
</body>
</html>