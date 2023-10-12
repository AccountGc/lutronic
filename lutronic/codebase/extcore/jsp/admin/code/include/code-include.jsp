<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
String codeType = request.getParameter("codeType");
boolean multi = Boolean.parseBoolean(request.getParameter("multi"));
boolean view = "view".equals(mode);
boolean update = "update".equals(mode);
boolean create = "create".equals(mode);
String height = StringUtil.checkReplaceStr(request.getParameter("height"), "150");
%>
<%
if (create || update) {
%>
<input type="button" value="추가" title="추가" class="blue" onclick="popup300();">
<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow300();">
<%
}
%>
<div id="grid300" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID300;
	const columns300 = [ {
		dataField : "name",
		headerText : "이름",
		dataType : "string",
		width : 300,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "code",
		headerText : "코드",
		dataType : "string",
		width : 150,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "sort",
		headerText : "소트",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "description",
		headerText : "설명",
		dataType : "string",
		style : "aui-left",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "enabled",
		headerText : "활성화",
		dataType : "string",
		width : 120,
		renderer : {
			type : "CheckBoxEditRenderer",
			edtiable : false,
		},
		filter : {
			showIcon : false,
		},
	}, {
		dataField : "oid",
		dataType : "string",
		visible : false
	} ]
	
	function createAUIGrid300(columnLayout) {
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
		}
		myGridID300 = AUIGrid.create("#grid300", columnLayout, props);
		<%if (view || update) {%>
		AUIGrid.setGridData(myGridID300, <%=AUIGridUtil.include(oid, codeType)%>);
		<%}%>
	}
	

	function popup300() {
		const multi = "<%=multi%>";
		const codeType = "<%=codeType%>";
		const url = getCallUrl("/code/popup?method=insert300&multi=" + multi + "&codeType=" + codeType);
		_popup(url, 1000, 600, "n");
	}

	function insert300(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID300, "oid", item.oid);
			const insert = {
				code : item.code,
				description : item.description,
				enabled : item.enabled,
				name : item.name,
				oid : item.oid,
				sort : item.sort
			};
			if (unique) {
				AUIGrid.addRow(myGridID300, insert, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 협의?
				alert(item.name + " 제품은 이미 추가 되어있습니다.");
			}
		})
		callBack(true);
	}

	function deleteRow300() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID300);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID300, rowIndex);
		}
	}
</script>