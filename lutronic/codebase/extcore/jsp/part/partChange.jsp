<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<form name="partChange">
	<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
	<table width="100%" border="0" cellpadding="0" cellspacing="0" > 
		<tr height="5">
			<td>
				<table class="button-table">
					<tr>
						<td class="left">
							<div class="header">
								<img src="/Windchill/extcore/images/header.png">&nbsp;부품 진채번
							</div>
						</td>
						<td class="right">
							<input type="button" value="저장" name="setNumber" id="setNumber" class="btnClose" >
							<input type="button" value="닫기" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr align=center>
			<td valign="top" style="padding:0px 0px 0px 0px">
				<div id="grid_wrap" style="height: 500px; border-top: 1px solid #3180c3;"></div>
			</td>
		</tr>
	</table>
</form>
<script>
	var  myGridID;
	var columnLayout =[ {
			dataField : "number",
			headerText : "가도번",
			dataType : "string",
			width : 120,
		}, {
			dataField : "name",
			headerText : "품목명",
			dataType : "string",
			width : 120,
		}, {
			dataField : "number",
			headerText : "진도번",
			dataType : "string",
			width : 120,
		}, {
			dataField : "level",
			headerText : "부품구분",
			dataType : "string",
			width : 350,
		}, {
			dataField : "count",
			headerText : "대분류",
			dataType : "string",
			width : 250,
		},{
			dataField : "count",
			headerText : "중분류",
			dataType : "string",
			width : 250,
		},{
			dataField : "count",
			headerText : "SEQ",
			dataType : "string",
			width : 250,
		},{
			dataField : "count",
			headerText : "기타",
			dataType : "string",
			width : 250,
		},{
			dataField : "count",
			headerText : "부품명1",
			dataType : "string",
			width : 250,
		},{
			dataField : "count",
			headerText : "부품명2",
			dataType : "string",
			width : 250,
		},{
			dataField : "count",
			headerText : "부품명3",
			dataType : "string",
			width : 250,
		},{
			dataField : "count",
			headerText : "부품명4",
			dataType : "string",
			width : 250,
		}];

	function createAUIGrid() {
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
		var params = new Object();
		var  url = getCallUrl("/part/partChange");
		params.oid =  $("#oid").val();
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

	$(document).ready(function () {
		createAUIGrid();
		
		AUIGrid.resize(myGridID);
	});
</script>

