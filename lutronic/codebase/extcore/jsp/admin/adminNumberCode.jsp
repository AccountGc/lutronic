<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String codeheight = request.getParameter("codeheight");
%>
<!-- 폴더 그리드 리스트 -->
<div id="code_grid" style="height: <%=codeheight%>px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let codeGridID;
	const codeColumns = [ {
		dataField : "codeName",
		headerText : "코드타입",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},				
	} ]

	function createAUIGridCode(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			selectionMode: "multipleCells",
			enableFilter : true, 
// 			showInlineFilter : true,		
			displayTreeOpen : true,
			forceTreeView : true
		}
		codeGridID = AUIGrid.create("#code_grid", columnLayout, props);
		loadCode();
// 		AUIGrid.bind(codeGridID, "selectionChange", auiGridSelectionChangeHandler);
// 		AUIGrid.bind(codeGridID, "cellDoubleClick", auiCellDoubleClick);
// 		AUIGrid.bind(codeGridID, "cellClick", auiCellClick);
// 		AUIGrid.bind(codeGridID, "ready", auiReadyHandler);
	}

// 	function auiReadyHandler() {
// 		AUIGrid.showItemsOnDepth(_myGridID, 2);
// 	}

	
	function auiCellClick(event) {
		const item = event.item;
		const oid = item.oid;
		const location = item.location;
		document.getElementById("oid").value = oid;
		document.getElementById("location").value = oid;
		document.getElementById("locationText").innerText = location;
	}
	
	function loadCode() {
		let params = new Object();
		const url = getCallUrl("/admin/numberCode");
		call(url, params, function(data) {
			if (data.result) {
				AUIGrid.setGridData(codeGridID, data.codeList);
			} else {
				alert(data.msg);
			}
		});
	}
</script>
