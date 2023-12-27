<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
EChangeOrder eco = (EChangeOrder) request.getAttribute("eco");
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECO (
				<b>
					<font color="red"><%=eco.getEoNumber()%></font>
				</b>
				) 품목변경
			</div>
		</td>
		<td class="right">
			<input type="button" value="추가" title="추가" onclick="popup100();">
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
		headerText : "REV",
		dataField : "part_version",
		dataType : "string",
		width : 100
	}, {
		headerText : "상태",
		dataField : "part_state",
		dataType : "string",
		width : 80
	}, {
		headerText : "등록자",
		dataField : "part_creator",
		dataType : "string",
		width : 100
	}, {
		headerText : "등록일",
		dataField : "part_createdDate",
		dataType : "string",
		width : 100
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
				const url = getCallUrl("/bom/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		}
	}, {
		headerText : "",
		dataField : "part_oid",
		dataType : "string",
		visible : false
	}, {
		headerText : "",
		dataField : "link_oid",
		dataType : "string",
		visible : false
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
			hoverMode : "singleRow",
			showRowCheckColumn : true,
			autoGridHeight : true,
			showStateColumn : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID, <%=list%>);
		AUIGrid.bind(myGridID, "cellClick", auiCellClick);
	}
	
	function auiCellClick(event) {
		const item = event.item;
		const rowIdField = AUIGrid.getProp(event.pid, "rowIdField"); // rowIdField 얻기
		const rowId = item[rowIdField];
		
		// 이미 체크 선택되었는지 검사
		if (AUIGrid.isCheckedRowById(event.pid, rowId)) {
			// 엑스트라 체크박스 체크해제 추가
			AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
		} else {
			// 엑스트라 체크박스 체크 추가
			AUIGrid.setCheckedRowsByIds(event.pid, rowId);
		}
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
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/activity/replace");
		const addRows = AUIGrid.getAddedRowItems(myGridID);
		const removeRows = AUIGrid.getRemovedItems(myGridID);
		const params = {
			addRows : addRows,
			removeRows : removeRows,
			oid : oid
		};
		parent.openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.document.location.reload();
				self.close();
			}
			parent.closeLayer();
		})
	}

	function popup100() {
		const url = getCallUrl("/part/popup?method=insert100&multi=true");
		_popup(url, 1600, 800, "n");
	}

	function insert100(arr, callBack) {
		let msg = "";
		for (let i = 0; i < arr.length; i++) {
			const dd = arr[i];
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const newItem = new Object();
			newItem.part_number = item.number;
			newItem.part_name = item.name;
			newItem.part_version = item.version;
			newItem.part_oid = item.part_oid;
			newItem.part_state = item.state;
			newItem.part_creator = item.creator;
			newItem.part_createdDate = item.createdDate;
			const unique = AUIGrid.isUniqueValue(myGridID, "part_oid", newItem.part_oid);
			if (unique) {
				AUIGrid.addRow(myGridID, newItem, rowIndex);
			} else {
				msg = item.number + " 품목은 이미 추가 되어있습니다.";
				break;
			}
		}
		callBack(true, false, msg);
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>