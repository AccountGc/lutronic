<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
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
<form name=updatefamilyPart id=updatefamilyPart  method=post  >
	<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
	<table width="100%" border="0" cellpadding="0" cellspacing="3" >
		<tr align=center>
		    <td valign="top" style="padding:0px 0px 0px 0px">
				<table class="button-table">
					<tr>
						<td height="30" width="93%" align="center">
							<div class="header">
								Family 테이블 수정
							</div>
						</td>
					</tr>
					<tr>
						<td class="right">
							<input type="button" value="저장" name="updatefamily" class="blue" >
							<input type="button" value="닫기" name="closeBtn" class="btnClose" onclick="self.close();">
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr align="center">
			<td valign="top" style="padding:0px 0px 0px 0px">		
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
					<tr>
						<td height=1 width=100%></td>
					</tr>
				</table>
				<div id="grid_wrap" style="height: 640px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>
<script>
let myGridID;
function _layout() {
	return [  {
		dataField : "oid",
		visible : false
	},{
		dataField : "number",
		headerText : "품목번호.",
		dataType : "string",
		width : 120,
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		width : 120,
	}, {
		dataField : "name",
		headerText : "프로젝트코드",
		dataType : "string",
		width : 120,
	}, {
		dataField : "deptcode",
		headerText : "부서",
		dataType : "string",
		width : 350,
	}, {
		dataField : "manufacture",
		headerText : "MANUFATURER",
		dataType : "string",
		width : 250,
	},{
		dataField : "finish",
		headerText : "후처리",
		dataType : "string",
		width : 250,
	},{
		dataField : "weight",
		headerText : "무게",
		dataType : "string",
		width : 250,
	},{
		dataField : "productmethod",
		headerText : "제작방법",
		dataType : "string",
		width : 250,
	},{
		dataField : "unit",
		headerText : "단위",
		dataType : "string",
		width : 250,
	},{
		dataField : "mat",
		headerText : "재질",
		dataType : "string",
		width : 250,
	},{
		dataField : "count",
		headerText : "비고",
		dataType : "string",
		width : 250,
	},{
		dataField : "count",
		headerText : "사양",
		dataType : "string",
		width : 250,
	}]
}

function createAUIGrid(columnLayout) {
	const props = {
		headerHeight : 30,
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		fillColumnSizeMode: false,
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
	};
	myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
// 	loadGridData();
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
	const params = new Object();
	const url = getCallUrl("/part/updatefamilyPart");
	const oid = document.querySelector("#oid").value;
	params.oid = oid;
	AUIGrid.showAjaxLoader(myGridID);
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(myGridID);
		if (data.result) {
			AUIGrid.setGridData(myGridID, data.list);
		} else {
			alert(data.msg);
		}
	});
}

document.addEventListener("DOMContentLoaded", function() {
	const columns = loadColumnLayout("downloadHistory");
	const contenxtHeader = genColumnHtml(columns);
	createAUIGrid(columns);
	AUIGrid.resize(myGridID);
});
</script>
</form>
</body>
</html>