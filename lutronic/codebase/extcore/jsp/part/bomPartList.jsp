<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = (String) request.getAttribute("oid");
	String bomType = (String) request.getAttribute("bomType");
	String partNumber = (String) request.getAttribute("partNumber");
	String title = (String) request.getAttribute("title");
%>
<form name="PartTreeForm"  method="post" >
	<input type="hidden" name="oid"  id="oid" value="<%= oid %>">
	<input type="hidden" name="bomType"  id="bomType" value="<%= bomType %>">
</form>
<table border="0" cellpadding="0" cellspacing="3" >
	<tr align="center">
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><%= partNumber %><%= title %></B></td>
		   		</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
				<tr height="30">
					<td>
						<b>
							<%= title %>
						</b>
					</td>
		  			<td class="right">
						<input type="button" value="닫기" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
					</td>
		    	</tr>
		    </table>
		</td>
	</tr>
	
	<tr align="center">
		<td valign="top" style="padding:0px 0px 0px 0px">
			<div id="grid_wrap" style="height: 445px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>
<script>
	var  myGridID;
	function _layout() {
		return [ {
			dataField : "number",
			headerText : "품목번호.",
			dataType : "string",
			width : 120,
		}, {
			dataField : "name",
			headerText : "품명",
			dataType : "string",
			width : 120,
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
			width : 120,
		}, {
			dataField : "version",
			headerText : "Rev.",
			dataType : "string",
			width : 350,
		}]
	}

	function createAUIGrid(columnLayout) {
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
		const url = getCallUrl("/part/bomPartList");
		const oid = document.querySelector("#oid").value;
		const bomType = document.querySelector("#bomType").value;
		params.oid = oid;
		params.bomType = bomType;
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			if (isEmpty(data.msg)) {
				AUIGrid.setGridData(myGridID, data.list);
			} else {
				alert(data.msg);
			}
		});
	}
	$(document).ready(function() {
		const columns = loadColumnLayout("bomPartList");
		const contenxtHeader = genColumnHtml(columns);
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});
</script>

