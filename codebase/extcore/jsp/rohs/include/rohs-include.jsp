<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.rohs.service.RohsHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean multi = Boolean.parseBoolean(request.getParameter("multi"));
boolean view = "view".equals(mode);
boolean update = "update".equals(mode);
boolean create = "create".equals(mode);
boolean header = Boolean.parseBoolean(request.getParameter("header"));
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 ROHS
			</div>
		</td>
		<%
		if (!header) {
		%>
		<td class="right">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup106();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow106();">
			<%
			}
			%>
		</td>
		<%
		}
		%>
	</tr>
</table>
<%
// 테이블 처리 여부
if (header) {
%>

<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">관련 ROHS</th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup106();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow106();">
			<%
			}
			%>
			<div id="grid106" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>

<%
} else {
%>
<div id="grid106" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<%
}
%>
<script type="text/javascript">
	let myGridID106;
	const columns106 = [ {
		dataField : "number",
		headerText : "물질번호",
		dataType : "string",
		width : 200,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/rohs/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "manufactureDisplay",
		headerText : "협력업체",
		dataType : "string",
		width : 150,
	}, {
		dataField : "name",
		headerText : "물질명",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/rohs/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
	}, {
		dataField : "stateDisplay",
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
		dataField : "createDate",
		headerText : "등록일",
		dataType : "date",
		width : 100,
	}, {
		dataField : "modifyDate",
		headerText : "수정일",
		dataType : "date",
		width : 100,
	}, {
		dataField : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid106(columnLayout) {
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
		myGridID106 = AUIGrid.create("#grid106", columnLayout, props);
		<%if (view || update) {%>
		AUIGrid.setGridData(myGridID106, <%=AUIGridUtil.include(oid, "rohs")%>);
		<%}%>
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup106() {
		const multi = "<%=multi%>";
		const url = getCallUrl("/rohs/popup?method=insert106&multi=" + multi);
		_popup(url, 1400, 700, "n");
	}
	
	function deleteRow106() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID106);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}
		AUIGrid.removeCheckedRows(myGridID106);
	}
	
	function insert106(arr, callBack){
		let checker = true;
		let number;
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID106, "oid", item.oid);
			if (!unique) {
				number = item.number;
				checker = false;
				return true;
			}
		})
		
		if(!checker) {
			callBack(true, false, number +  " ROHS는 이미 추가 되어있습니다.");
		} else {
			arr.forEach(function(dd) {
				const rowIndex = dd.rowIndex;
				const item = dd.item;
				AUIGrid.addRow(myGridID106, item, rowIndex);
			})
		}
	}

	
</script>