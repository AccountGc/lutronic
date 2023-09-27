<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
String method = request.getParameter("method");
boolean multi = Boolean.parseBoolean(request.getParameter("multi"));
boolean view = "view".equals(mode);
String height = StringUtil.checkReplaceStr(request.getParameter("height"), "150");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 문서
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
		<th class="lb">관련 문서</th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (!view) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup90();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow90();">
			<%
			}
			%>
			<div id="grid90" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID90;
	const columns90 = [ {
		dataField : "number",
		headerText : "문서번호",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "name",
		headerText : "문서명",
		dataType : "string",
		style : "aui-left",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "date",
		filter : {
			showIcon : true,
			inline : true
		},
	} ]

	function createAUIGrid90(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			<%if (!view) {%>
			showStateColumn : true,
			showRowCheckColumn : true,
			<%}%>
			<%if (!multi) {%>
			rowCheckToRadio : true,
			<%}%>
			enableFilter : true,
		}
		myGridID90 = AUIGrid.create("#grid90", columnLayout, props);
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup90() {
		const method = "<%=method%>";
		const multi = "<%=multi%>";
		const url = getCallUrl("/doc/popup?method=" + method + "&multi=" + multi);
		_popup(url, 1800, 900, "n");
	}

	
	function deleteRow90() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID90);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID90, rowIndex);
		}
	}

	function insert90(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			AUIGrid.addRow(myGridID90, item, rowIndex);
		})
		callBack(true);
	}
</script>