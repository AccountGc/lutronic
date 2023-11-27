<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				문서 일괄결재
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
		<th class="req lb">일괄결재 제목</th>
		<td class="indent5">
			<input type="text" name="appName" id="appName" class="width-400">
		</td>
	</tr>
	<tr>
		<th class="lb">일괄결재 설명</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="6"></textarea>
		</td>
	</tr>
</table>

<br>
<div id="grid_wrap" style="height: 300px; border-top: 1px solid #3180c3;"></div>

<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="등록" title="등록" class="blue" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "interalnumber",
		dataType : "string",
		headerText : "내부문서번호",
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
			enableMovingColumn : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
	}
	// 등록
	function create() {
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
			return false;
		}

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		const params = {
			appName : appName.value,
			description : description.value,
			list : list,
			type : "DOC"
		}
		const url = getCallUrl("/doc/register");
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function receive(data) {
		for (let i = 0; i < data.length; i++) {
			const check = AUIGrid.isUniqueValue(myGridID, "oid", data[i].item.oid);
			if (check) {
				AUIGrid.addRow(myGridID, data[i].item, "last");
			}
		}
	}

	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		toFocus("appName");
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
