<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean multi = Boolean.parseBoolean(request.getParameter("multi"));
boolean view = "view".equals(mode);
boolean update = "update".equals(mode);
boolean create = "create".equals(mode);
boolean header = Boolean.parseBoolean(request.getParameter("header"));
JSONArray data = null;
if(view || update){
	data = AUIGridUtil.include(oid, "eco");
}
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 ECO
			</div>
		</td>
	</tr>
</table>
<%
	// 테이블 처리 여부
	if(header) {
%>
<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">관련 ECO</th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup105();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow105();">
			<%
			}
			%>
			<div id="grid105" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<%
	} else {
%>
<div id="grid105" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<%
	}
%>
<script type="text/javascript">
	let myGridID105;
	const columns105 = [ {
		dataField : "number",
		headerText : "ECO 번호",
		dataType : "string",
		width : 120,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/eco/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "name",
		headerText : "ECO 제목",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/eco/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "model",
		headerText : "제품",
		dataType : "string",
		width : 250,
		style : "aui-left"
	}, {
		dataField : "sendType",
		headerText : "ECO 타입",
		dataType : "string",
		width : 80,
	}, {
		dataField : "licensing_name",
		headerText : "인허가변경",
		dataType : "string",
		width : 100,
	}, {
		dataField : "riskType_name",
		headerText : "위험 통제",
		dataType : "string",
		width : 100,
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
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
		dataField : "createdDate_txt",
		headerText : "등록일",
		dataType : "date",
		width : 100,
	}, {
		dataField : "approveDate",
		headerText : "승인일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "oid",
		dataType : "string",
		visible : false
	} ]
	
	function createAUIGrid105(columnLayout) {
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
			<%if (create || update) {%>
			showStateColumn : true,
			showRowCheckColumn : true,
			<%}%>
			<%if (!multi) {%>
			rowCheckToRadio : true,
			<%}%>
			enableFilter : true,
			autoGridHeight : true
		}
		myGridID105 = AUIGrid.create("#grid105", columnLayout, props);
		AUIGrid.setGridData(myGridID105, <%=data%>);
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup105() {
		const multi = "<%=multi%>";
		const url = getCallUrl("/eco/popup?method=insert105&multi=" + multi);
		_popup(url, 1400, 700, "n");
	}

	
	function deleteRow105() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID105);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}
		AUIGrid.removeCheckedRows(myGridID105);
	}

	function insert105(arr, callBack) {
		let checker = true;
		let number;
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID105, "oid", item.oid);
			if (!unique) {
				number = item.number;
				checker = false;
				return true;
			}
		})
		
		if(!checker) {
			callBack(true, false, number +  " ECO는 이미 추가 되어있습니다.");
		} else {
			arr.forEach(function(dd) {
				const rowIndex = dd.rowIndex;
				const item = dd.item;
				AUIGrid.addRow(myGridID105, item, rowIndex);
			})
		}
	}	
</script>