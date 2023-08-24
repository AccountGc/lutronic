<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
String distribute = (String) request.getAttribute("distribute");
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
<form name="projectHistory">
	<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
	<table width="100%" border="0" cellpadding="0" cellspacing="0" > 
		<tr height="5">
			<td>
				<!--//여백 테이블-->
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
					<tr> 
						<td height=30 width=99% align=center><B><font color=white></font></B></td>
					</tr>
				</table>
				<table class="button-table">
					<tr>
						<td class="left">
							<div class="header">
								<img src="/Windchill/extcore/images/header.png">&nbsp;다운로드 이력
							</div>
						</td>
						<td class="right">
							<input type="button" value="닫기" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
						</td>
					</tr>
				</table>
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
					<tr>
						<td height=1 width=100%></td>
					</tr>
				</table>
				<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>
<script>
let myGridID;
function _layout() {
	return [ {
		dataField : "dept",
		headerText : "부서",
		dataType : "string",
		width : 120,
	}, {
		dataField : "duty",
		headerText : "직급",
		dataType : "string",
		width : 120,
	}, {
		dataField : "name",
		headerText : "이름",
		dataType : "string",
		width : 120,
	}, {
		dataField : "time",
		headerText : "다운로드 시간",
		dataType : "string",
		width : 350,
	}, {
		dataField : "count",
		headerText : "다운로드 횟수",
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
	const url = getCallUrl("/common/downloadHistory");
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