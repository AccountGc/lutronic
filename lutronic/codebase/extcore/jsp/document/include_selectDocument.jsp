<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean isCreate = "create".equals(mode);
%>
<input type="button" value="추가" title="추가" class="blue" onclick="addDoc();">
<input type="button" value="삭제" title="삭제" class="red" onclick="deleteDoc();">
<div id="grid_doc" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let docGridID;
	const columnsDoc = [ {
		dataField : "number",
		headerText : "문서번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "name",
		headerText : "문서명",
		dataType : "string",
		width : 180,
	}, {
		dataField : "version",
		headerText : "Rev.",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid4(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			showRowCheckColumn : true,
			showStateColumn : true,
			rowCheckToRadio : true
		}
		docGridID = AUIGrid.create("#grid_doc", columnLayout, props);
	}

	function addDoc() {
		const url = getCallUrl("/doc/list?popup=true");
		popup(url, 1500, 700);
	}
	
	function append(items){
		var arr = [];
		var count=0;
		var data = AUIGrid.getGridData(docGridID);
		for (var i=0; i<items.length; i++){
			var a=0;
			if(data.length==0){
				arr[i] = items[i];
			}else{
				for(var j=0; j<data.length; j++){
					if(data[j].oid == items[i].oid){
						a++;
					}
				}
			}
			if(a==0){
				arr[count] = items[i];
				count++;
			}
		}
		AUIGrid.addRow(docGridID, arr);
	}

	function deleteDoc() {
		const checked = AUIGrid.getCheckedRowItems(docGridID);
		if (checked.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(docGridID, rowIndex);
		}
	}
	
</script>