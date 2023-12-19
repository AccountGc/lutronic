<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean isView = "view".equals(mode);
boolean isCreate = "create".equals(mode);
boolean isUpdate = "update".equals(mode);
%>
<%
if (isCreate || isUpdate) {
%>
	<input type="button" value="추가" title="추가" class="blue" onclick="insert7();">
	<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow7();">
<%
}
%>
<div id="grid_eco" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let ecoGridID;
	const columnsEco = [ {
		dataField : "eoNumber",
		headerText : "ECO번호",
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
				const url = getCallUrl("/changeECO/view?oid=" + oid);
				popup(url, 1600, 800);
			}
		},
	}, {
		dataField : "eoName",
		headerText : "ECO제목",
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
				const url = getCallUrl("/changeECO/view?oid=" + oid);
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

	function createAUIGrid1(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode: true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : true,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableMovingColumn : true,
			enableFilter : false,
			showInlineFilter : false,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
		};
		ecoGridID = AUIGrid.create("#grid_eco", columnLayout, props);
	}

	function insert7() {
		const url = getCallUrl("/changeECO/list?popup=true");
		popup(url, 1500, 700);
	}
	
	function appendECO(items){
		var arr = [];
		var count=0;
		var data = AUIGrid.getGridData(ecoGridID);
		for (var i=0; i<items.length; i++){
			var a=0;
			if(data.length==0){
				arr[i] = items[i];
			}else{
				for(var j=0; j<data.length; j++){
					if(data[j].oid == items[i].oid){
						a++;
					}
				}
			}
			if(a==0){
				arr[count] = items[i];
				count++;
			}
		}
		AUIGrid.addRow(ecoGridID, arr);
	}

	function deleteRow7() {
		const checked = AUIGrid.getCheckedRowItems(ecoGridID);
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