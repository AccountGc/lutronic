<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean isCreate = "create".equals(mode);
%>
<input type="button" value="추가" title="추가" class="blue" onclick="addRohs();">
<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRohs();">
<div id="grid_rohs" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let rohsGridID;
	const columnsRohs = [ {
		dataField : "number",
		headerText : "물질번호",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "name",
		headerText : "물질명",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "version",
		headerText : "Rev.",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid6(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			fillColumnSizeMode: true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : true,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			showRowCheckColumn : true,
			rowCheckToRadio : true,
			enableFilter : true
		}
		rohsGridID = AUIGrid.create("#grid_rohs", columnLayout, props);
	}

	function addRohs() {
		const url = getCallUrl("/rohs/list?popup=true");
		popup(url, 1500, 700);
	}
	
	function rohsAppend(items){
		var arr = [];
		var count=0;
		var data = AUIGrid.getGridData(rohsGridID);
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
		AUIGrid.addRow(rohsGridID, arr);
	}

	function deleteRohs() {
		const checked = AUIGrid.getCheckedRowItems(rohsGridID);
		if (checked.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(rohsGridID, rowIndex);
		}
	}
	
</script>