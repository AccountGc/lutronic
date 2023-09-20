<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean isView = "view".equals(mode);
boolean isCreate = "create".equals(mode);
boolean isUpdate = "update".equals(mode);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 EO
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
		<th class="lb">관련 EO</th>
		<td class="indent5 pt5">
			<input type="button" value="추가" title="추가" class="blue" onclick="insertEO();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteEO();">
			<div id="grid_eo" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let eoGridID;
	const columnsEo = [ {
		dataField : "eoNumber",
		headerText : "EO번호",
		dataType : "string",
		width : 120,
		filter : {
			showIcon : true,
			inline : true
		},
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/changeeo/view?oid=" + oid);
				popup(url, 1600, 800);
			}
		},
	}, {
		dataField : "eoName",
		headerText : "EO제목",
		dataType : "string",
		width : 120,
		filter : {
			showIcon : true,
			inline : true
		},
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/changeeo/view?oid=" + oid);
				popup(url, 1600, 800);
			}
		},
	}, {
		dataField : "licensing",
		headerText : "인허가변경",
		dataType : "string",
		width : 120,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "riskType",
		headerText : "위험 통제",
		dataType : "string",
		width : 350,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 250,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 350,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "state",
		headerText : "승인일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "modifyDate",
		headerText : "등록일",
		dataType : "string",
		width : 200,
		filter : {
			showIcon : true,
			inline : true
		},
	} ]

	function createAUIGridEO(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode: true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : true,
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			enableFilter : false,
			showInlineFilter : false,
			useontextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
		};
		eoGridID = AUIGrid.create("#grid_eo", columnLayout, props);
	}

	function insertEO() {
		const url = getCallUrl("/changeECO/listEOPopup");
		_popup(url, 1500, 700, "n");
	}
	
	// 팝업 데이터 가져오는 메서드
	function setAppendEO(items){
		const data = AUIGrid.getGridData(eoGridID);
		if (data.length != 0) {
			for (let i = 0; i < items.length; i++) {
				for (let j = 0; j < data.length; j++) {
					if (data[j].oid == items[i].oid) {
						items.splice(i, 1);
					}
				}
			}
		}
		AUIGrid.addRow(eoGridID, items);
	}

	function deleteEO() {
		const checked = AUIGrid.getCheckedRowItems(eoGridID);
		if (checked.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(partGridID, rowIndex);
		}
	}
	
</script>