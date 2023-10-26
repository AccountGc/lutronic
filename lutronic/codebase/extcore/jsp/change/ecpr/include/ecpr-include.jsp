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
JSONArray data = null;
if(view|| update){
	data = AUIGridUtil.include(oid, "ecpr");
}
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 ECPR
			</div>
		</td>
	</tr>
</table>
<%
	// 테이블 처리 여부
	if(header) {
%>
<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">관련 ECPR</th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popupEcpr();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRowEcpr();">
			<%
			}
			%>
			<div id="gridEcpr" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<%
	} else {
%>
<div id="gridEcpr" style="height: <%if(data.size() == 0) { %>110px; <%} else { %>30px;<%} %> border-top: 1px solid #3180c3; margin: 5px;"></div>
<%
	}
%>
<script type="text/javascript">
	let myGridIDEcpr;
	const columnsEcpr = [ {
		dataField : "number",
		headerText : "ECPR 번호",
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
				const url = getCallUrl("/ecpr/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "name",
		headerText : "ECPR 제목",
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
				const url = getCallUrl("/ecpr/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
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
		dataField : "writeDate",
		headerText : "작성일",
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
		width : 180,
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
	
	function createAUIGridEcpr(columnLayout) {
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
		myGridIDEcpr = AUIGrid.create("#gridEcpr", columnLayout, props);
		AUIGrid.setGridData(myGridIDEcpr, <%=data%>);
	}

	// 추가 버튼 클릭 시 팝업창 메서드
	function popupEcpr() {
		const multi = "<%=multi%>";
		const url = getCallUrl("/ecpr/popup?method=insertEcpr&multi=" + multi);
		_popup(url, 1800, 900, "n");
	}

	
	function deleteRowEcpr() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridIDEcpr);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridIDEcpr, rowIndex);
		}
	}

	function insertEcpr(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridIDEcpr, "oid", item.oid);
			if (unique) {
				AUIGrid.addRow(myGridIDEcpr, item, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 합의?
				alert(item.number + " ECPR은 이미 추가 되어있습니다.");
			}
		})
		callBack(true);
	}	
</script>