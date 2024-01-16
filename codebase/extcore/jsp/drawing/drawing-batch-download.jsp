<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				일괄다운로드
			</div>
		</td>
		<td class="right">
			<input type="button" title="STEP" value="STEP" class="" onclick="progress('STEP');">
			<input type="button" title="DXF" value="DXF" class="blue" onclick="progress('DXF');">
			<input type="button" title="PDF" value="PDF" class="red" onclick="progress('PDF');">
			<input type="button" title="닫기" value="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 50px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "icon",
		headerText : "",
		dataType : "string",
		width : 50,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 16,
		},
	}, {
		dataField : "cadType",
		headerText : "CAD타입",
		dataType : "string",
		width : 120,
	}, {
		dataField : "number",
		headerText : "도면번호",
		dataType : "string",
		width : 200,
	}, {
		dataField : "name",
		headerText : "도면명",
		dataType : "string",
		style : "aui-left",
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
		renderer : {
			type : "TemplateRenderer"
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			if (value === "승인됨") {
				return "approved";
			}
			return null;
		}
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "date",
		width : 100,
	}, {
		dataField : "epm_oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			autoGridHeight : true,
			showStateColumn : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
	}

	function setData(data) {
		const arr = new Array();
		for (let i = 0; i < data.length; i++) {
			const oid = data[i].item.epm_oid;
			const number = data[i].item.number;
			const unique = AUIGrid.isUniqueValue(myGridID, "epm_oid", oid);
			if (!unique) {
				alert("이미 등록된 도면(" + number + ") 입니다.");
				return false;
			}
			arr.push(data[i].item);
		}
		AUIGrid.addRow(myGridID, arr);
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});

	function progress(type) {
		if (!confirm("추가된 도면의 " + type + "을(를) 일괄 다운로드 하시겠습니까?")) {
			return false;
		}
		const data = AUIGrid.getGridData(myGridID);
		const url = getCallUrl("/drawing/progress");
		const params = {
			data : data,
			type : type
		};
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
			}
			closeLayer();
		})
	}
</script>