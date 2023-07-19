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
<div id="grid_wrap9" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID9;
	const columns9 = [ {
		dataField : "number",
		headerText : "품목번호",
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
		dataField : "name",
		headerText : "품목명",
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
		dataField : "version",
		headerText : "Rev.",
		dataType : "string",
		width : 180,
	}, {
		dataField : "bom",
		headerText : "BOM",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid9(columnLayout) {
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
		myGridID9 = AUIGrid.create("#grid_wrap9", columnLayout, props);
		<%if (isView || isUpdate) {%>
<%-- 		AUIGrid.setGridData(myGridID9, <%=ProjectHelper.manager.jsonAuiProject(oid)%>); --%>
		<%}%>
	}

	function insert9() {
		const url = getCallUrl("/part/list?popup=true");
		popup(url, 1500, 700);
	}

	function append(data, callBack) {
		for (let i = 0; i < data.length; i++) {
			const item = data[i].item;
			const isUnique = AUIGrid.isUniqueValue(myGridID9, "oid", item.oid);
			if (isUnique) {
				// 멀티 아닐경우 그리드 데이터 클리어
				AUIGrid.clearGridData(myGridID9);
				AUIGrid.addRow(myGridID9, item, "first");
			}
		}
		callBack(true);
	}

	function deleteRow9() {
		const checked = AUIGrid.getCheckedRowItems(myGridID9);
		if (checked.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(myGridID9, rowIndex);
		}
	}
</script>