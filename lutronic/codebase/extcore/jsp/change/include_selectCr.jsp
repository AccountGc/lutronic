<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean isView = "view".equals(mode);
boolean isCreate = "create".equals(mode);
boolean isUpdate = "update".equals(mode);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 CR
			</div>
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">관련 CR</th>
		<td class="indent5 pt5">
			<input type="button" value="추가" title="추가" class="blue" onclick="addCR();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="delCR();">
			<div id="grid_cr" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let crGridID;
	const columnsCr = [ {
		dataField : "eoNumber",
		headerText : "CR 번호",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		}
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
		headerText : "CR 제목",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "writer",
		headerText : "등록자",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "createDate",
		headerText : "등록일",
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

	function createAUIGridCR(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			fillColumnSizeMode: true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			<%if (isCreate || isUpdate) {%>
			showRowCheckColumn : true,
			showStateColumn : true,
			<%}%>
			rowCheckToRadio : true,
			enableFilter : true
		}
		crGridID = AUIGrid.create("#grid_cr", columnLayout, props);
		<%if (isView || isUpdate) {%>
<%-- 		AUIGrid.setGridData(crGridID, <%=ProjectHelper.manager.jsonAuiProject(oid)%>); --%>
		<%}%>
	}

	function addCR() {
		const url = getCallUrl("/changeCR/listPopup");
		_popup(url, 1500, 700,"n");
	}
	
	function append(items){
		var arr = [];
		var count=0;
		var data = AUIGrid.getGridData(crGridID);
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
		AUIGrid.addRow(crGridID, arr);
	}

	function delCR() {
		const checked = AUIGrid.getCheckedRowItems(crGridID);
		if (checked.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(crGridID, rowIndex);
		}
	}
	
</script>