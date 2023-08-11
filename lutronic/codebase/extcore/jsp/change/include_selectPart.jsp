<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean isView = "view".equals(mode);
boolean isCreate = "create".equals(mode);
boolean isUpdate = "update".equals(mode);
%>
<%
if (isCreate || isUpdate) {
%>
	<input type="button" value="추가" title="추가" class="blue" onclick="insert9();">
	<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow9();">
<%
}
%>
<div id="grid_part" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let partGridID;
	const columnsPart = [ {
		headerText : "품목번호",
		dataType : "string",
		children : [ {
			dataField : "numberBefore",
			headerText : "변경전",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
			editable : false
		}, {
			dataField : "numberAfter",
			headerText : "변경후",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
			editable : false,
			renderer : {
				type : "ButtonRenderer",
				labelText : "등록",
				onClick : function(event) {
				},
			}
		} ]
		<%
			if(isView) {
		%>
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/project/info?oid=" + oid);
				popup(url);
			}
		},
		<%
			}
		%>
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "version",
		headerText : "Rev.",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "bom",
		headerText : "BOM",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid2(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode: true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			<%if (isCreate || isUpdate) {%>
			showRowCheckColumn : true,
// 			showStateColumn : true,
			<%}%>
			rowCheckToRadio : true,
			enableFilter : true,
		}
		partGridID = AUIGrid.create("#grid_part", columnLayout, props);
		<%if (isView || isUpdate) {%>
<%-- 		AUIGrid.setGridData(partGridID, <%=ProjectHelper.manager.jsonAuiProject(oid)%>); --%>
		<%}%>
		var json = {
		    "numberBefore" : 3010357700,
		    "name" : "MOUNT_A-MOTOR",
		    "version" : "A.1",
		    "bom" : ""
		}
		AUIGrid.setGridData(partGridID, json);
	}

	function insert9() {
		const url = getCallUrl("/part/list?popup=true");
		popup(url, 1500, 700);
	}
	
	function append(items){
		var arr = [];
		var count=0;
		var data = AUIGrid.getGridData(partGridID);
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
		AUIGrid.addRow(partGridID, arr);
	}

	function deleteRow9() {
		const checked = AUIGrid.getCheckedRowItems(partGridID);
		if (checked.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(partGridID, rowIndex);
		}
	}
	
</script>