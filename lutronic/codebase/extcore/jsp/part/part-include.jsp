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
String method = request.getParameter("method");
boolean multi = Boolean.parseBoolean(request.getParameter("multi"));
boolean view = "view".equals(mode);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련품목
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
		<th class="lb">관련품목</th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (!view) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup91();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow91();">
			<%
			}
			%>
			<div id="grid91" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID91;
	const columns91 = [ {
		dataField : "_3d",
		headerText : "3D",
		dataType : "string",
		width : 60,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			onClick : function(event) {
			}
		},
		filter : {
			showIcon : false,
			inline : false
		},
	}, {
		dataField : "_2d",
		headerText : "2D",
		dataType : "string",
		width : 60,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			onClick : function(event) {
			}
		},
		filter : {
			showIcon : false,
			inline : false
		},
	}, {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "version",
		headerText : "REV.",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "date",
	}, {
		dataField : "bom",
		headerText : "BOM",
		dataType : "string",
		width : 180,
	} ]

	function createAUIGrid91(columnLayout) {
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
		myGridID91 = AUIGrid.create("#grid91", columnLayout, props);
		<%if (view) {%>
		AUIGrid.setGridData(myGridID91, <%=DocumentHelper.manager.reference(oid)%>);
		<%}%>
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup91() {
		const method = "<%=method%>";
		const multi = "<%=multi%>";
		const url = getCallUrl("/part/popup?method=" + method + "&multi=" + multi);
		_popup(url, 1800, 900, "n");
	}

	function deleteRow91() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID91);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID91, rowIndex);
		}
	}

	function insert91(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			AUIGrid.addRow(myGridID91, item, rowIndex);
		})
		callBack(true);
	}
</script>