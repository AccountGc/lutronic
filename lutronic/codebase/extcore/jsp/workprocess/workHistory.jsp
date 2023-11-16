<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
boolean isAuth = (boolean) request.getAttribute("isAuth");
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
<form name=projectHistory>
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
								<img src="/Windchill/extcore/images/header.png">&nbsp;결재이력
							</div>
						</td>
						<td class="right">
							<% if(isAuth){ %>
								<input type="button" value="결재의견 수정" name="updateBtn" id="updateBtn" class="btnCRUD" onclick="update();">						
							<% } %>
							<input type="button" value="닫기" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
						</td>
					</tr>
					<tr align="center">
						<td valign="top" style="padding:0px 0px 0px 0px" colspan=2>
							<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
									<tr>
										<td height="1" width="100%"></td>
									</tr>
							</table>
							<div id="grid_wrap1" style="height: 300px; border-top: 1px solid #3180c3;"></div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="5">&nbsp;</td> 
		</tr>
		<tr>
			<td>
				<table class="button-table">
					<tr>
						<td class="left">
							<div class="header">
								<img src="/Windchill/extcore/images/header.png">&nbsp;외부 유저 메일
							</div>
							<div style="width:100%;overflow-x:hidden;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;">
								<div id="grid_wrap2" style="height: 300px; border-top: 1px solid #3180c3;"></div>
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
<script>
let myGridID1;
let myGridID2;
const columns1 = [ {
		dataField : "processOrder",
		headerText : "순서.",
		dataType : "string",
		width : 120,
	}, {
		dataField : "activityName",
		headerText : "구분",
		dataType : "string",
		width : 120,
	}, {
		dataField : "deptName",
		headerText : "부서",
		dataType : "string",
		width : 120,
	}, {
		dataField : "userName",
		headerText : "이름",
		dataType : "string",
		width : 350,
	}, {
		dataField : "processDate",
		headerText : "결재일",
		dataType : "string",
		width : 250,
	},{
		dataField : "state",
		headerText : "결재",
		dataType : "string",
		width : 250,
	},{
		dataField : "comment",
		headerText : "결재의견",
		dataType : "string",
		width : 250,
	}
	<% if(isAuth){ %>
// 		,{
// 			dataField : "processDate",
// 			headerText : "결재일",
// 			dataType : "string",
// 			width : 250,
// 		},{
// 			dataField : "state",
// 			headerText : "결재",
// 			dataType : "string",
// 			width : 250,
// 		}
	<% } %>
]

const columns2 = [ {
	dataField : "name",
	headerText : "이름.",
	dataType : "string",
	width : 120,
}, {
	dataField : "email",
	headerText : "이메일",
	dataType : "string",
	width : 120,
}]

function createAUIGrid1(columnLayout) {
	const props = {
		headerHeight : 30,
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		fillColumnSizeMode: true,
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
	myGridID1 = AUIGrid.create("#grid_wrap1", columnLayout, props);
	loadGridData1();
	AUIGrid.bind(myGridID1, "contextMenu", auiContextMenuHandler);
	AUIGrid.bind(myGridID1, "vScrollChange", function(event) {
		hideContextMenu();
		vScrollChangeHandler(event);
	});
	AUIGrid.bind(myGridID1, "hScrollChange", function(event) {
		hideContextMenu();
	});
}

function createAUIGrid2(columnLayout) {
	const props = {
		headerHeight : 30,
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		fillColumnSizeMode: true,
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
	myGridID2 = AUIGrid.create("#grid_wrap2", columnLayout, props);
	loadGridData2();
	AUIGrid.bind(myGridID2, "contextMenu", auiContextMenuHandler);
	AUIGrid.bind(myGridID2, "vScrollChange", function(event) {
		hideContextMenu();
		vScrollChangeHandler(event);
	});
	AUIGrid.bind(myGridID2, "hScrollChange", function(event) {
		hideContextMenu();
	});
}

function loadGridData1() {
	const params = new Object();
	const url = getCallUrl("/groupware/workHistory");
	const oid = document.querySelector("#oid").value;
	params.oid = oid;
	const listType = "appList";
	params.listType = listType;
	AUIGrid.showAjaxLoader(myGridID1);
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(myGridID1);
		if (data.result) {
			AUIGrid.setGridData(myGridID1, data.appList);
		} else {
			alert(data.msg);
		}
	});
}

function loadGridData2() {
	const params = new Object();
	const url = getCallUrl("/groupware/workHistory");
	const oid = document.querySelector("#oid").value;
	const listType = "mailList";
	params.oid = oid;
	params.listType = listType;
	AUIGrid.showAjaxLoader(myGridID2);
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(myGridID2);
		if (data.result) {
			AUIGrid.setGridData(myGridID2, data.mailList);
		} else {
			alert(data.msg);
		}
	});
}

document.addEventListener("DOMContentLoaded", function() {
	createAUIGrid1(columns1);
	createAUIGrid2(columns2);
	AUIGrid.resize(myGridID1);
	AUIGrid.resize(myGridID2);
});
</script>
</form>
</body>
</html>