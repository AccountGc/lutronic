<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.workspace.dto.EcaDTO"%>
<%
String oid = request.getParameter("oid");
EcaDTO dto = new EcaDTO(oid);
JSONArray docList = (JSONArray) dto.getDocList();
%> 
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				산출물
			</div>
		</td>
		<td class="right">
			<input type="button" value="부품개정" title="부품개정" onclick="popup00();">
			<input type="button" value="품목변경" title="품목변경" class="blue" onclick="">
			<input type="button" value="새로고침" title="새로고침" class="gray" onclick="popup00();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 30px; border-top: 1px solid #3180c3;"></div>
<script>
let myGridiD;
const columns = [ {
	headerText : "개정 전",
	children : [{
		dataField : "number",
		dataType : "string",
		headerText : "품목번호",
	}, {
		dataField : "name",
		dataType : "string",
		headerText : "품목명",
	}, {
		dataField : "version",
		dataType : "string",
		headerText : "REV",
	}, {
		dataField : "creator",
		dataType : "string",
		headerText : "등록자",
	}, {
		dataField : "state",
		dataType : "string",
		headerText : "상태",
	}]
}, {
	headerText : "개정 후",
	children : [{
		dataField : "number",
		dataType : "string",
		headerText : "품목번호",
	}, {
		dataField : "name",
		dataType : "string",
		headerText : "품목명",
	}, {
		dataField : "version",
		dataType : "string",
		headerText : "REV",
	}, {
		dataField : "creator",
		dataType : "string",
		headerText : "등록자",
	}, {
		dataField : "state",
		dataType : "string",
		headerText : "상태",
	}, {
		dataField : "primary",
		dataType : "string",
		headerText : "주도면",
	}, {
		dataField : "include",
		dataType : "string",
		headerText : "참조항목",
	}]
}, {
headerText : "BOM",
children : [{
	dataField : "number",
	dataType : "string",
	headerText : "BOM 편집",
	renderer : {
		type : "ButtonRenderer",
		labelText : "BOM 편집",
		onclick : function(rowIndex, columnIndex, value, item) {
			
		}
	}
}, {
	dataField : "name",
	dataType : "string",
	headerText : "BOM 비교",
	renderer : {
		type : "ButtonRenderer",
		labelText : "BOM 비교",
		onclick : function(rowIndex, columnIndex, value, item) {
			
		}
	}
}]
}]

function createAUIGrid(columnLayout) {
	const props = {
		headerHeight : 30,
		showRowCheckColumn : true,
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		showAutoNoDataMessage : false,
		selectionMode : "multipleCells",
		enableRowCheckShiftKey : true,
		autoGridHeight : true
	};
	myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
	AUIGrid.setGridData(myGridID, <%=docList%>);
}

// 추가 버튼 클릭 시 팝업창 메서드
function popup00() {
	const url = getCallUrl("/doc/popup?method=insert00&multi=true");
	_popup(url, 1800, 900, "n");
}

function insert00(arr, callBack) {
	const list = new Array();
	arr.forEach(function(dd) {
		const item = dd.item;
		list.push(item.oid);
	})

	const oid = document.getElementById("oid").value;
	const url = getCallUrl("/activity/saveLink");
	const params = {
		list : list,
		oid : oid
	}
	logger(params);
	parent.openLayer();
	call(url, params, function(data) {
		const msg = data.msg;
		if (data.result) {
			document.location.reload();
			callBack(true, true, msg);
		}
		parent.closeLayer();
	})
}
</script>