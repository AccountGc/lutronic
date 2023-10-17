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
String height = StringUtil.checkReplaceStr(request.getParameter("height"), "150");
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
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup100();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow100();">
			<%
			}
			%>
			<div id="grid100" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID100;
	const columns100 = [ {
		dataField : "number",
		headerText : "EO 번호",
		dataType : "string",
		width : 150,
		filter : {
			showIcon : true,
		},
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/eo/view?oid=" + oid);
				popup(url, 1600, 800);
			}
		},
	}, {
		dataField : "name",
		headerText : "EO 제목",
		dataType : "string",
		style : "aui-left",
		filter : {
			showIcon : true,
		},
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/eo/view?oid=" + oid);
				popup(url, 1600, 800);
			}
		},
	}, {
		dataField : "eoType",
		headerText : "구분",
		dataType : "string",
		width : 120,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 120,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "createdDate_txt",
		headerText : "등록일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "approveDate_txt",
		headerText : "승인일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "oid",
		dataType : "string",
		visible : false
	} ]
	
	function createAUIGrid100(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : true,
			selectionMode : "multipleCells",
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
		myGridID100 = AUIGrid.create("#grid100", columnLayout, props);
		<%if (view || update) {%>
		AUIGrid.setGridData(myGridID100, <%=AUIGridUtil.include(oid, "eo")%>);
		<%}%>
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup100() {
		const multi = "<%=multi%>";
		const url = getCallUrl("/eo/popup?method=insert100&multi=" + multi);
		_popup(url, 1800, 900, "n");
	}

	
	function deleteRow100() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID100);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID100, rowIndex);
		}
	}

	function insert100(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID100, "oid", item.oid);
			if (unique) {
				AUIGrid.addRow(myGridID100, item, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 협의?
				alert(item.number + " EO는 이미 추가 되어있습니다.");
			}
		})
		callBack(true);
	}	
</script>