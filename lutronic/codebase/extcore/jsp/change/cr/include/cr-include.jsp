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
				관련 CR
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
		<th class="lb">관련 CR</th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup101();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow101();">
			<%
			}
			%>
			<div id="grid101" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID101;
	const columns101 = [ {
		dataField : "number",
		headerText : "CR 번호",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/cr/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "name",
		headerText : "CR 제목",
		dataType : "string",
		style : "aui-left",
		filter : {
			showIcon : true,
			inline : true
		},
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/cr/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "model",
		headerText : "제품",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "changeSection",
		headerText : "변경구분",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "createDepart",
		headerText : "작성부서",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "writer",
		headerText : "작성자",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "approveDate",
		headerText : "승인일",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "state",
		headerText : "상태",
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
		dataField : "createdDate_txt",
		headerText : "등록일",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	} ]
	
	function createAUIGrid101(columnLayout) {
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
		myGridID101 = AUIGrid.create("#grid101", columnLayout, props);
		<%if (view || update) {%>
		AUIGrid.setGridData(myGridID101, <%=AUIGridUtil.include(oid, "cr")%>);
		<%}%>
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup101() {
		const multi = "<%=multi%>";
		const url = getCallUrl("/cr/popup?method=insert101&multi=" + multi);
		_popup(url, 1800, 900, "n");
	}

	
	function deleteRow101() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID101);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID101, rowIndex);
		}
	}

	function insert101(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID101, "oid", item.oid);
			if (unique) {
				AUIGrid.addRow(myGridID101, item, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 협의?
				alert(item.number + " CR은 이미 추가 되어있습니다.");
			}
		})
		callBack(true);
	}	
</script>