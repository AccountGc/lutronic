<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean multi = Boolean.parseBoolean(request.getParameter("multi"));
boolean view = "view".equals(mode);
boolean update = "update".equals(mode);
boolean create = "create".equals(mode);
boolean header = request.getParameter("header") != null ? Boolean.parseBoolean(request.getParameter("header")) : false;
%>
<div class="include">
	<%
	if (header) {
	%>
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png">
					완제품
				</div>
			</td>
		</tr>
	</table>
	<%
	}
	%>
	<%
	if (create || update) {
	%>
	<input type="button" value="추가" title="추가" class="blue" onclick="popup104();">
	<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow104();">
	<%
	}
	%>
	<div id="grid104" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
	<script type="text/javascript">
	let myGridID104;
	const columns104 = [ {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 230,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.part_oid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.part_oid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80,
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
		renderer : {
			type : "TemplateRenderer"
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "createdDate_txt",
		headerText : "등록일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "",
		headerText : "BOM",
		dataType : "string",
		width : 120,
		renderer: {
			type: "ButtonRenderer",
			labelText: "BOM",
			onClick: function (event) {
				const part_oid = event.item.part_oid;
				url = getCallUrl("/bom/view?oid=" + part_oid);
				_popup(url, 1600, 800, "n");
			}
		},
	} ]

	function createAUIGrid104(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
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
		myGridID104 = AUIGrid.create("#grid104", columnLayout, props);
		<%if (view || update) {%>
		AUIGrid.setGridData(myGridID104, <%=AUIGridUtil.include(oid, "part")%>);
		<%}%>
	}

	function popup104() {
		const url = getCallUrl("/part/popup?method=insert104&multi=true");
		_popup(url, 1600, 800, "n");
	}

	function insert104(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID104, "part_oid", item.part_oid);
			if (unique) {
				AUIGrid.addRow(myGridID104, item, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 합의?
				alert(item.number + " 품목은 이미 추가 되어있습니다.");
			}
		})
		selectbox("_psize");
		callBack(true, false, "");
	}

	function deleteRow104() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID104);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID104, rowIndex);
		}
	}
	</script>
</div>