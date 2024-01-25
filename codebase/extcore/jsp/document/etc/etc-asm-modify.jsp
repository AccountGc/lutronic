<%@page import="com.ptc.windchill.uwgm.proesrv.createesrdoc.AsmDocUtility"%>
<%@page import="com.e3ps.workspace.dto.AsmDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
AsmDTO dto = (AsmDTO) request.getAttribute("dto");
String location = (String) request.getAttribute("location");
String title = (String) request.getAttribute("title");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<input type="hidden" name="location" id="location" value="<%=location%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<%=title%>
				일괄 결재 수정
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
		<th class="req lb">일괄 결재 제목</th>
		<td class="indent5">
			<input type="text" name="appName" id="appName" class="width-400" value="<%=dto.getName()%>">
		</td>
	</tr>
	<tr>
		<th class="lb">일괄 결재 설명</th>
		<td class="indent5">
			<div class="textarea-auto">
				<textarea name="description" id="description" rows="6"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
			</div>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="추가" title="추가" class="red" onclick="popup();">
			<input type="button" value="삭제" title="삭제" class="blue" onclick="deleteRow90();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 30px; border-top: 1px solid #3180c3;"></div>

<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<input type="button" value="뒤로" title="뒤로" class="gray" onclick="history.go(-1);">
		</td>
	</tr>
</table>

<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "number",
		dataType : "string",
		width : 120,
		headerText : "문서번호",
	}, {
		dataField : "number",
		dataType : "string",
		headerText : "문서명",
		style : "aui-left"
	}, {
		dataField : "version",
		dataType : "string",
		headerText : "REV",
		width : 80
	}, {
		dataField : "state",
		dataType : "string",
		headerText : "상태",
		width : 100
	}, {
		dataField : "creator",
		dataType : "string",
		headerText : "등록자",
		width : 100
	}, {
		dataField : "createdDate",
		dataType : "string",
		headerText : "등록일",
		width : 100
	}, {
		dataField : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showStateColumn : true,
			showRowNumColumn : true,
			showRowCheckColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			softRemoveRowMode : false,
			enableMovingColumn : true,
			autoGridHeight : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID,
<%=dto.getData()%>
	);
	}
	// 등록
	function modify() {
		const appName = document.getElementById("appName");
		const description = document.getElementById("description");
		const list = AUIGrid.getGridData(myGridID);

		if (appName.value === "") {
			alert("일괄결재 제목을 입력하세요.");
			appName.focus();
			return false;
		}

		if (list.length === 0) {
			alert("일괄결재 대상을 선택하세요.");
			popup();
			return false;
		}

		if (!confirm("수정 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const params = {
			oid : oid,
			name : appName.value,
			description : description.value,
			list : list,
		}
		const url = getCallUrl("/asm/modify");
		parent.openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				parent.closeLayer();
			}
		})
	}

	function popup() {
		const location = document.getElementById("location").value;
		const url = getCallUrl("/doc/popup?method=insert90&multi=true&state=BATCHAPPROVAL&location=" + location);
		_popup(url, 1400, 700, "n");
	}

	function deleteRow90() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}
		AUIGrid.removeCheckedRows(myGridID);
	}

	function insert90(arr, callBack) {
		let checker = true;
		let number;
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID, "oid", item.oid);
			if (!unique) {
				number = item.interalnumber;
				checker = false;
				return true;
			}
		})

		if (!checker) {
			callBack(true, false, number + " 물질은 이미 추가 되어있습니다.");
		} else {
			arr.forEach(function(dd) {
				const rowIndex = dd.rowIndex;
				const item = dd.item;
				AUIGrid.addRow(myGridID, item, rowIndex);
			})
		}
	}

	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		toFocus("appName");
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
		autoTextarea();
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>