<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getParameter("oid");
String distribute = (String) request.getAttribute("distribute");
%>
<form name=projectHistory>
	<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
	<table width="100%" border="0" cellpadding="0" cellspacing="0" > 
		<tr height="5">
			<td>
				<!--//여백 테이블-->
				<table class="button-table">
					<tr>
						<td class="left">
							<div class="header">
								<img src="/Windchill/extcore/images/header.png">&nbsp;버전 이력보기
							</div>
						</td>
						<td class="right">
							<input type="button" value="닫기" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
						</td>
					</tr>
				</table>
				<div id="grid_wrap" style="height: 500px; border-top: 1px solid #3180c3;"></div>
			</td>
		</tr>
	</table>
</form>
<script>
let myGridID;
function _layout() {
	return [ {
		dataField : "version",
		headerText : "Rev.",
		dataType : "string",
		width : 120,
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 120,
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 120,
	}, {
		dataField : "createDate",
		headerText : "등록일",
		dataType : "string",
		width : 350,
	}, {
		dataField : "modifyDate",
		headerText : "수정일",
		dataType : "string",
		width : 250,
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 350,
	}, {
		dataField : "note",
		headerText : "이력내용",
		dataType : "string",
		width : 100,
	} ]
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
	const params = new Object();
	const url = getCallUrl("/common/versionHistory");
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
	const columns = loadColumnLayout("document-list");
	const contenxtHeader = genColumnHtml(columns);
	createAUIGrid(columns);
	AUIGrid.resize(myGridID);
});
</script>

