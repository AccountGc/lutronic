<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
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
				관련 ECRM
			</div>
		</td>
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
		<th class="lb">관련 ECRM</th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup110();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow110();">
			<%
			}
			%>
			<div id="grid110" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<%
} else {
%>
<div id="grid110" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<%
}
%>
<script type="text/javascript">
	let myGridID110;
	const columns110 = [ { 
		dataField : "number",
		headerText : "ECRM 번호",
		dataType : "string",
		width : 130,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/ecrm/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "name",
		headerText : "ECRM 제목",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/ecrm/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "changeSection",
		headerText : "변경구분",
		dataType : "string",
		style : "aui-left",
		width : 250
	}, {
		dataField : "model",
		headerText : "프로젝트 코드 [명]",
		dataType : "string",
		width : 220,
		style : "aui-left",
		renderer : {
			type : "TemplateRenderer"
		},
		sortable : false
	}, {
		dataField : "period",
		headerText : "보존년한",
		dataType : "string",
		width : 100
	}, {
		dataField : "createDepart",
		headerText : "작성부서",
		dataType : "string",
		width : 100
	}, {
		dataField : "writer",
		headerText : "작성자",
		dataType : "string",
		width : 100
	}, {
		dataField : "writeDate",
		headerText : "작성일",
		dataType : "string",
		width : 100
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
		width : 100
	}, {
		dataField : "createdDate_txt",
		headerText : "등록일",
		dataType : "string",
		width : 100
	}, {
		dataField : "approvedDate",
		headerText : "승인일",
		dataType : "string",
		width : 100
	} ]
	
	function createAUIGrid110(columnLayout) {
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
		myGridID110 = AUIGrid.create("#grid110", columnLayout, props);
		<%if (view || update) {%>
		AUIGrid.setGridData(myGridID110, <%=AUIGridUtil.include(oid, "ecrm")%>);
		<%}%>
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup110() {
		const multi = "<%=multi%>";
		const url = getCallUrl("/ecrm/popup?method=insert110&multi=" + multi);
		_popup(url, 1400, 700, "n");
	}

	
	function deleteRow110() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID110);
		if (checkedItems.length === 0) {
			alert("삭제할 ECRM을 선택하세요.");
			return false;
		}
		AUIGrid.removeCheckedRows(myGridID110);
	}

	function insert110(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID110, "oid", item.oid);
			if (unique) {
				AUIGrid.addRow(myGridID110, item, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 합의?
				alert(item.number + " ECRM은 이미 추가 되어있습니다.");
			}
		})
		callBack(true);
	}	
</script>