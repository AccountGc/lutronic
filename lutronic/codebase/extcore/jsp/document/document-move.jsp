<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.folder.Folder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
Folder f = (Folder) request.getAttribute("f");
String oid = (String) request.getAttribute("oid");
// String location = f.getFolderPath().substring("/Default/".length());
String location = f.getFolderPath();
JSONArray moveList = (JSONArray) request.getAttribute("moveList");
%>
<input type="hidden" name="location" id="location" value="<%=location%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<%=location.substring("/Default/".length())%>
				문서이동
			</div>
		</td>
		<td class="right">
			<input type="button" value="폴더선택" title="폴더선택" onclick="folder();" class="blue">
			<input type="button" value="저장" title="저장" class="red" onclick="save();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>


<table>
	<colgroup>
		<col width="695">
		<col width="10">
		<col width="695">
	</colgroup>
	<tr>
		<td>
			<div id="grid_wrap1" style="height: 530px; border-top: 1px solid #3180c3;"></div>
		</td>
		<td>&nbsp;</td>
		<td>
			<div id="grid_wrap2" style="height: 530px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>

<script type="text/javascript">
	let myGridID1;
	let myGridID2;

	const columns1 = [ {
		dataField : "name",
		dataType : "string",
		headerText : "문서명",
		style : "aui-left",
	}, {
		dataField : "number",
		headerText : "내부 문서번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "location",
		headerText : "문서분류",
		dataType : "string",
		style : "aui-left",
		width : 200,
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
	} ]

	const columns2 = [ {
		dataField : "folder",
		dataType : "string",
		headerText : "이동폴더",
		style : "aui-left",
	}, {
		dataField : "name",
		dataType : "string",
		headerText : "문서명",
		style : "aui-left",
	}, {
		dataField : "number",
		headerText : "내부 문서번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
	}, {
		dataField : "foid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid1(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
			enableMovingColumn : true,
			enableFilter : true,
			showInlineFilter : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			showDragKnobColumn : true,
			enableDrag : true,
			enableMultipleDrag : true,
			enableDrop : true,
			dropToOthers : true,
			enableUndoRedo : false,
			enableMultipleDrag : true,
			fillColumnSizeMode : true
		};
		myGridID1 = AUIGrid.create("#grid_wrap1", columnLayout, props);
		AUIGrid.setGridData(myGridID1,
<%=moveList%>
	);
		AUIGrid.bind(myGridID1, "dropEndBefore", function(event) {
			const items = event.items;
			const pidToDrop = event.pidToDrop;
			for (let i = 0; i < items.length; i++) {
				const item = items[i];
				const oid = item.oid;
			}
			return true;
		});
	}

	function createAUIGrid2(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
			enableMovingColumn : true,
			enableFilter : true,
			showInlineFilter : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			showDragKnobColumn : true,
			enableDrag : true,
			enableMultipleDrag : true,
			enableDrop : true,
			dropToOthers : true,
			enableUndoRedo : false,
			enableMultipleDrag : true,
			fillColumnSizeMode : true
		};
		myGridID2 = AUIGrid.create("#grid_wrap2", columnLayout, props);
	}

	function save() {
		const location = document.getElementById("location").value;
		const data = AUIGrid.getGridData(myGridID2);
		if (data.length === 0) {
			alert("이동 대상의 문서가 하나 이상 존재해야합니다.");
			return false;
		}

		for (let i = 0; i < data.length; i++) {
			const item = data[i];
			const foid = item.foid;
			if (foid === undefined) {
				alert((i + 1) + "행의 데이터가 폴더가 지정 안되었습니다.");
				return false;
			}
		}

		if (!confirm("문서를 이동시키겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/doc/move");
		const params = {
			data : data
		};
		logger(params);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.location.reload();
				self.close();
			}
			closeLayer();
		})
	}

	function rowsUpdate(oid, location) {
		const loc = document.getElementById("location").value;
		if (location === loc) {
			alert("이동하려는 폴더가 현재 저장된 위치와 동일합니다.");
			return false;
		}

		const data = AUIGrid.getGridData(myGridID2);
		for (let i = 0; i < data.length; i++) {
			const item = data[i];
			item.folder = location;
			item.foid = oid;
			AUIGrid.updateRow(myGridID2, item, i);
		}
	}

	function folder() {
		const data = AUIGrid.getGridData(myGridID2);
		if (data.length === 0) {
			alert("이동 대상의 문서가 하나 이상 존재해야합니다.");
			return false;
		}
		const location = decodeURIComponent("/Default/문서");
		const url = getCallUrl("/folder/popup?location=" + location + "&method=rowsUpdate");
		_popup(url, 500, 600, "n");
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid1(columns1);
		AUIGrid.resize(myGridID1);
		createAUIGrid2(columns2);
		AUIGrid.resize(myGridID2);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID1);
		AUIGrid.resize(myGridID2);
	});
</script>
