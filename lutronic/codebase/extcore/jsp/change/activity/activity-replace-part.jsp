<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
EChangeOrder eco = (EChangeOrder) request.getAttribute("eco");
ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) request.getAttribute("list");
%>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECO (<%=eco.getEoNumber()%>) 품목변경
			</div>
		</td>
		<td class="right">
			<input type="button" value="추가" title="추가" onclick="insert();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow();">
			<input type="button" value="저장" title="저장" class="blue" onclick="save();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 60px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		headerText : "품목번호",
		dataField : "part_number",
		dataType : "string",
		width : 140
	}, {
		headerText : "품목명",
		dataField : "part_name",
		dataType : "string",
		style : "aui-left"
	}, {
		headerText : "Rev",
		dataField : "part_version",
		dataType : "string",
		width : 140
	}, {
		headerText : "BOM 전개",
		dataField : "",
		dataType : "string",
		width : 140,
		renderer : {
			type : "ButtonRenderer",
			labelText : "BOM",
			onclick : function(rowIndex, columnIndex, value, item) {
				const oid = item.part_oid;
				const url = getCallUrl("/part/");
				_popup(url, 1600, 700, "n");
			}
		}
	} ];

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : true,
			selectionMode : "multipleCells",
			showRowCheckColumn : true,
			autoGridHeight : true,
			showStateColumn : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID,
<%=JSONArray.fromObject(list)%>
	);
	}

	function deleteRow() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	}

	function save() {
		if (!confirm("저장 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/activity/replace");
		const data = AUIGrid.getGridData(myGridID);
		const params = {
			data : data
		};
		call(url, params, function(data) {
			alert(data.msg);
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>