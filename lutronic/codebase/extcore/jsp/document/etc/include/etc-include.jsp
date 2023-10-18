<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.doc.etc.service.EtcHelper"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
String method = request.getParameter("method");
String location = request.getParameter("location");
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
		width : 180,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/etc/view?oid=" + oid);
				_popup(url, "", "", "f");
			}
		},		
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "interalnumber",
		headerText : "내부 문서번호",
		dataType : "string",
		width : 120,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "model",
		headerText : "프로젝트 코드",
		dataType : "string",
		width : 120,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "name",
		headerText : "문서명",
		dataType : "string",
		style : "aui-left",
		width : 350,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/etc/view?oid=" + oid);
				_popup(url, "", "", "f");
			}
		},		
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "location",
		headerText : "문서분류",
		dataType : "string",
		style : "aui-left",
		width : 250,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
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
		dataField : "writer",
		headerText : "작성자",
		dataType : "string",
		width : 100,
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
		dataField : "modifiedDate_txt",
		headerText : "수정일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "primary",
		headerText : "주 첨부파일",
		dataType : "string",
		width : 80,
		renderer : {
			type : "TemplateRenderer"
		},
		filter : {
			showIcon : false,
		},
	}, {
		dataField : "secondary",
		headerText : "첨부파일",
		dataType : "string",
		width : 100,
		renderer : {
			type : "TemplateRenderer"
		},
		filter : {
			showIcon : false,
		},
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
			<%if (create || update) {%>
			showStateColumn : true,
			showRowCheckColumn : true,
			<%}%>
			<%if (!multi) {%>
			rowCheckToRadio : true,
			<%}%>
			enableFilter : true,
		}
		myGridID90 = AUIGrid.create("#grid90", columnLayout, props);
		<%if (view || update) {%>
		AUIGrid.setGridData(myGridID90, <%=EtcHelper.manager.reference(oid, "doc")%>);
		<%}%>
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popup90() {
		const method = "<%=method%>";
		const multi = "<%=multi%>";
		const url = getCallUrl("/etc/popup?method=" + method + "&multi=" + multi + "&location=<%= location %>");
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
			const unique = AUIGrid.isUniqueValue(myGridID90, "oid", item.oid);
			if (unique) {
				AUIGrid.addRow(myGridID90, item, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 합의?
				alert(item.number + " 문서는 이미 추가 되어있습니다.");
			}
		})
		callBack(true);
	}
</script>