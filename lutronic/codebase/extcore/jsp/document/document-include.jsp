<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 문서
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
		<th class="lb">관련문서</th>
		<td class="indent5 pt5">
			<%-- 				<input type="hidden" name="lifecycle" id="lifecycle" value="<%=lifecycle%>" /> --%>
			<%-- 				<input type="hidden" name="searchType" id="searchType" value="<%=searchType%>" /> --%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup90();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow90();">
			<div id="grid90" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID90;
	const columns90 = [ {
		dataField : "number",
		headerText : "문서번호",
		dataType : "string",
	}, {
		dataField : "name",
		headerText : "문서명",
		dataType : "string",
		style : "aui-left"
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid90(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : false,
			showAutoNoDataMessage : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			showRowCheckColumn : true,
			showStateColumn : false,
		}
		myGridID90 = AUIGrid.create("#grid90", columnLayout, props);
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup90() {
		const url = getCallUrl("/doc/popup");
		_popup(url, 1800, 900, "n");
	}

	// 팝업 데이터 가져오는 메서드
	function setAppendDoc(items) {
		const data = AUIGrid.getGridData(myGridID90);

		if (data.length != 0) {
			for (let i = 0; i < items.length; i++) {
				for (let j = 0; j < data.length; j++) {
					if (data[j].oid == items[i].oid) {
						items.splice(i, 1);
					}
				}
			}
		}
		AUIGrid.addRow(myGridID90, items);
	}

	function deleteRow90() {
		const checked = AUIGrid.getCheckedRowItems(myGridID90);
		if (checked.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(myGridID90, rowIndex);
		}
	}
</script>