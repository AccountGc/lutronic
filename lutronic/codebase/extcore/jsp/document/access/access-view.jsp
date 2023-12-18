<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.folder.Folder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
Folder f = (Folder) request.getAttribute("f");
String oid = (String) request.getAttribute("oid");
String location = f.getFolderPath().substring("/Default/".length());
JSONArray userList = (JSONArray) request.getAttribute("userList");
JSONArray groupList = (JSONArray) request.getAttribute("groupList");
JSONArray authList = (JSONArray) request.getAttribute("authList");
%>
<input type="hidden" name="foid" id="foid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<%=location%>
				권한정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="저장" title="저장" class="red" onclick="save();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<table>
	<colgroup>
		<col width="450">
		<col width="10">
		<col width="80">
		<col width="10">
		<col width="450">
		<col width="10">
		<col width="450">
	</colgroup>
	<tr>
		<td>
			<div id="grid_wrap1" style="height: 530px; border-top: 1px solid #3180c3;"></div>
		</td>
		<td>&nbsp;</td>
		<td valign="middle">
			<table class="button-table">
				<tr>
					<td class="center">
						<input type="button" value="설정(<-)" title="설정(<-)" class="blue" onclick="left();">
					</td>
				</tr>
				<tr>
					<td class="center">
						<input type="button" value="해체(->)" title="해제(->)" class="red" onclick="right();">
					</td>
				</tr>
			</table>
		</td>
		<td>&nbsp;</td>
		<td>
			<div id="grid_wrap2" style="height: 530px; border-top: 1px solid #3180c3;"></div>
		</td>
		<td>&nbsp;</td>
		<td>
			<div id="grid_wrap3" style="height: 530px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>


<script type="text/javascript">
	let myGridID1;
	let myGridID2;
	let myGridID3;
	const columns1 = [ {
		dataField : "name",
		dataType : "string",
		headerText : "이름 / 그룹명",
		style : "aui-left",
	}, {
		dataField : "type",
		dataType : "string",
		headerText : "타입"
	} ]

	const columns2 = [ {
		dataField : "name",
		dataType : "string",
		headerText : "이름"
	}, {
		dataField : "id",
		dataType : "string",
		headerText : "아이디"
	}, {
		dataField : "type",
		dataType : "string",
		headerText : "타입"
	} ]

	const columns3 = [ {
		dataField : "name",
		dataType : "string",
		headerText : "그룹명",
		style : "aui-left"
	}, {
		dataField : "type",
		dataType : "string",
		headerText : "타입"
	} ]

	function createAUIGrid1(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowCheckColumn : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			enableFilter : true,
			showInlineFilter : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			showDragKnobColumn : true,
			enableDrag : true,
			enableDrop : true,
			dropToOthers : true,
			enableUndoRedo : false,
			enableMultipleDrag : true,
			fillColumnSizeMode : true
		};
		myGridID1 = AUIGrid.create("#grid_wrap1", columnLayout, props);
		AUIGrid.bind(myGridID1, "cellClick", auiCellClick);
		AUIGrid.setGridData(myGridID1,
<%=authList%>
	);
		AUIGrid.bind(myGridID1, "dropEndBefore", function(event) {
			const items = event.items;
			const pidToDrop = event.pidToDrop;
			if (pidToDrop === "#grid_wrap1") {
				return false;
			}
			for (let i = 0; i < items.length; i++) {
				const item = items[i];
				const oid = item.oid;
				if (oid.indexOf("WTUser") > -1) {
					if (pidToDrop === "#grid_wrap3") {
						return false;
					}
				} else if (oid.indexOf("WTGroup") > -1) {
					if (pidToDrop === "#grid_wrap2") {
						return false;
					}
				}
			}
			return true;
		});
	}

	function createAUIGrid2(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowCheckColumn : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			enableFilter : true,
			showInlineFilter : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			showDragKnobColumn : true,
			enableDrag : true,
			enableDrop : true,
			dropToOthers : true,
			enableUndoRedo : false,
			enableMultipleDrag : true,
			fillColumnSizeMode : true
		};
		myGridID2 = AUIGrid.create("#grid_wrap2", columnLayout, props);
		AUIGrid.bind(myGridID2, "cellClick", auiCellClick);
		AUIGrid.setGridData(myGridID2,
<%=userList%>
	);
		AUIGrid.bind(myGridID2, "dropEndBefore", function(event) {
			const pidToDrop = event.pidToDrop;
			if (pidToDrop === "#grid_wrap3") {
				return false;
			}
			if (pidToDrop === "#grid_wrap2") {
				return false;
			}
			return true;
		});
	}
	function createAUIGrid3(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowCheckColumn : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			enableFilter : true,
			showInlineFilter : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			showDragKnobColumn : true,
			enableDrag : true,
			enableDrop : true,
			dropToOthers : true,
			enableUndoRedo : false,
			enableMultipleDrag : true,
			fillColumnSizeMode : true
		};
		myGridID3 = AUIGrid.create("#grid_wrap3", columnLayout, props);
		AUIGrid.bind(myGridID3, "cellClick", auiCellClick);
		AUIGrid.setGridData(myGridID3,
<%=groupList%>
	);
		AUIGrid.bind(myGridID3, "dropEndBefore", function(event) {
			const pidToDrop = event.pidToDrop;
			if (pidToDrop === "#grid_wrap2") {
				return false;
			}
			if (pidToDrop === "#grid_wrap3") {
				return false;
			}
			return true;
		});
	}

	function save() {
		if (!confirm("저장 하시겠습니까?")) {
			return false;
		}
		const foid = document.getElementById("foid").value;
		const data = AUIGrid.getGridData(myGridID1);
		const params = {
			data : data,
			foid : foid
		};
		const url = getCallUrl("/access/save");
		openLayer();
		logger(params);
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			}
			closeLayer();
		})
	}

	function left() {
		const data2 = AUIGrid.getCheckedRowItems(myGridID2);
		const data3 = AUIGrid.getCheckedRowItems(myGridID3);

		if (data2.length === 0 && data3.length === 0) {
			alert("사용자 혹은 그룹을 하나 이상 선택하세요.");
			return false;
		}

		for (let i = 0; i < data2.length; i++) {
			const item = data2[i].item;
			AUIGrid.addRow(myGridID1, item, "first");
		}

		for (let i = 0; i < data3.length; i++) {
			const item = data3[i].item;
			AUIGrid.addRow(myGridID1, item, "first");
		}

		AUIGrid.removeCheckedRows(myGridID2);
		AUIGrid.removeCheckedRows(myGridID3);
	}

	function right() {
		const data1 = AUIGrid.getCheckedRowItems(myGridID1);

		if (data1.length === 0) {
			alert("사용자 혹은 그룹을 하나 이상 선택하세요.");
			return false;
		}

		for (let i = 0; i < data1.length; i++) {
			const item = data1[i].item;
			const oid = item.oid;
			if (oid.indexOf("WTUser") > -1) {
				AUIGrid.addRow(myGridID2, item, "first");
			} else if (oid.indexOf("WTGroup") > -1) {
				AUIGrid.addRow(myGridID3, item, "first");
			}
		}

		AUIGrid.removeCheckedRows(myGridID1);
	}

	function auiCellClick(event) {
		const item = event.item;
		const rowIdField = AUIGrid.getProp(event.pid, "rowIdField"); // rowIdField 얻기
		const rowId = item[rowIdField];
		if (AUIGrid.isCheckedRowById(event.pid, rowId)) {
			AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
		} else {
			AUIGrid.addCheckedRowsByIds(event.pid, rowId);
		}
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid1(columns1);
		AUIGrid.resize(myGridID1);
		createAUIGrid2(columns2);
		AUIGrid.resize(myGridID2);
		createAUIGrid3(columns3);
		AUIGrid.resize(myGridID3);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID1);
		AUIGrid.resize(myGridID2);
		AUIGrid.resize(myGridID3);
	});
</script>