<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<input type="button" value="추가" title="추가" class="blue" onclick="popup104();">
<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow104();">
<div id="grid104" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID104;
	const columns104 = [ {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "name",
		headerText : "품명",
		dataType : "string",
		width : 300,
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
		dataField : "bom",
		headerText : "BOM 보기",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid104(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			fillColumnSizeMode : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			showRowCheckColumn : true,
			rowCheckToRadio : true,
			enableFilter : true
		}
		myGridID104 = AUIGrid.create("#grid104", columnLayout, props);
	}

	function popup104() {
		const url = getCallUrl("/part/popup?method=insert104&multi=true");
		_popup(url, 1600, 800, "n");
	}

	function insert104(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID91, "part_oid", item.part_oid);
			if (unique) {
				AUIGrid.addRow(myGridID91, item, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 협의?
				alert(item.number + " 품목은 이미 추가 되어있습니다.");
			}
		})
		callBack(true);
	}	

	function deleteRow104() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID91);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID91, rowIndex);
		}
	}
</script>