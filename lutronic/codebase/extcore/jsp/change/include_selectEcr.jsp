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
	<input type="button" value="추가" title="추가" class="blue" onclick="addECR();">
	<input type="button" value="삭제" title="삭제" class="red" onclick="delECR();">
<%
}
%>
<div id="grid_ecr" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let ecrGridID;
	const columnsEcr = [ {
		dataField : "eoNumber",
		headerText : "CR/ECRP번호",
		dataType : "string",
		width : 180,
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
		dataField : "eoName",
		headerText : "CR/ECPR제목",
		dataType : "string",
		width : 180,
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 180,
	}, {
		dataField : "writer",
		headerText : "등록자",
		dataType : "string",
		width : 180,
	}, {
		dataField : "createDate",
		headerText : "등록일",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid1(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			<%if (isCreate || isUpdate) {%>
			showRowCheckColumn : true,
			showStateColumn : true,
			<%}%>
			rowCheckToRadio : true
		}
		ecrGridID = AUIGrid.create("#grid_ecr", columnLayout, props);
		<%if (isView || isUpdate) {%>
<%-- 		AUIGrid.setGridData(ecrGridID, <%=ProjectHelper.manager.jsonAuiProject(oid)%>); --%>
		<%}%>
	}

	function addECR() {
		const url = getCallUrl("/changeECO/select_ecrPopup");
		popup(url, 1500, 700);
	}
	
	function append(items){
		var arr = [];
		var count=0;
		var data = AUIGrid.getGridData(ecrGridID);
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
		AUIGrid.addRow(ecrGridID, arr);
	}

	function delECR() {
		const checked = AUIGrid.getCheckedRowItems(ecrGridID);
		if (checked.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(ecrGridID, rowIndex);
		}
	}
	
</script>