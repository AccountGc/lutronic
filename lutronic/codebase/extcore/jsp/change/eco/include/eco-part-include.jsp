<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.rohs.service.RohsHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.part.dto.PartDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean multi = Boolean.parseBoolean(request.getParameter("multi"));
boolean view = "view".equals(mode);
boolean update = "update".equals(mode);
boolean create = "create".equals(mode);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				설계변경 품목
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
		<th class="lb">설계변경 품목</th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup500();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow500();">
			<%
			}
			%>
			<div id="grid500" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID500;
	const columns500 = [ {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
		editable : false,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		style : "aui-left",
		editable : false,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 90,
		editable : false,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "ecoNo",
		headerText : "BOM",
		dataType : "string",
		width : 80,
		editable : false,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "part_oid",
		dataType : "string",
		visible : false
	} ]
	
	function createAUIGrid500(columnLayout) {
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
			autoGridHeight : true,
			enableCellMerge : true,
			rowSelectionWithMerge : true,
		}
		myGridID500 = AUIGrid.create("#grid500", columnLayout, props);
		<%if (view || update) {%>
		AUIGrid.setGridData(myGridID500, <%=AUIGridUtil.include(oid, "part")%>);
		<%}%>
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup500() {
		const multi = "<%=multi%>";
		const url = getCallUrl("/part/popup?method=insert500&multi=" + multi);
		_popup(url, 1800, 900, "n");
	}


	function deleteRow500() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID500);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID500, rowIndex);
		}
	}

	function insert500(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID500, "part_oid", item.part_oid);
			if (unique) {
				AUIGrid.addRow(myGridID500, item, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 합의?
				alert(item.number + " 품목은 이미 추가 되어있습니다.");
			}
		})
		callBack(true);
	}	
</script>