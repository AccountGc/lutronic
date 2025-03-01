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
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 문서
			</div>
		</td>
		<%
		if (!header) {
		%>
		<td class="right">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup90();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow90();">
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
		<th class="lb">관련문서</th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup90();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow90();">
			<%
			}
			%>
			<div id="grid90" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>

<%
} else {
%>
<div id="grid90" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<%
}
%>

<script type="text/javascript">
	let myGridID90;
	const columns90 = [ {
		dataField : "name",
		headerText : "문서명",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/doc/view?oid=" + oid);
				_popup(url, "", "", "f");
			}
		},		
	}, {
		dataField : "number",
		headerText : "문서번호",
		dataType : "string",
		width : 180,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/doc/view?oid=" + oid);
				_popup(url, "", "", "f");
			}
		},
	}, {
		dataField : "model",
		headerText : "프로젝트 코드 [명]",
		dataType : "string",
		width : 120,
		style : "aui-left",
		sortable : false,
		renderer : {
			type : "TemplateRenderer"
		},
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
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
		renderer : {
			type : "TemplateRenderer"
		},
	}, {
		dataField : "writer",
		headerText : "작성자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "modifiedDate_txt",
		headerText : "수정일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid90(columnLayout) {
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
			autoGridHeight : true
		}
		myGridID90 = AUIGrid.create("#grid90", columnLayout, props);
		<%if (view || update) {%>
		AUIGrid.setGridData(myGridID90, <%=AUIGridUtil.include(oid, "doc")%>);
		<%}%>
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup90() {
		const multi = "<%=multi%>";
		const url = getCallUrl("/doc/popup?method=insert90&multi=" + multi);
		_popup(url, 1400, 700, "n");
	}

	
	function deleteRow90() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID90);
		if (checkedItems.length === 0) {
			alert("삭제할 문서를 선택하세요.");
			return false;
		}
		AUIGrid.removeCheckedRows(myGridID90);
	}

	function insert90(arr, callBack) {
		let checker = true;
		let number;
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID90, "oid", item.oid);
			if (!unique) {
				number = item.interalnumber;
				checker = false;
				return true;
			}
		})
		
		if(!checker) {
			callBack(true, false, number +  " 문서는 이미 추가 되어있습니다.");
		} else {
			arr.forEach(function(dd) {
				const rowIndex = dd.rowIndex;
				const item = dd.item;
				AUIGrid.addRow(myGridID90, item, rowIndex);
			})
		}
	}	
</script>