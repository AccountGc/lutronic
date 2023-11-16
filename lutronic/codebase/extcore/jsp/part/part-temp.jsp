<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
boolean popup = false;
if(request.getParameter("popup")!=null){
	popup = true;
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
</head>
<body>
<div id="grid_ver" style="height: 300px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let verGridID;
	const columnVer = [ {
		dataField : "seq",
		headerText : "seq",
		dataType : "number",
		width : 180,
	}, {
		dataField : "level",
		headerText : "level",
		dataType : "number",
		width : 180,
	}, {
		dataField : "number",
		headerText : "부품번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "drawingNum",
		headerText : "도면번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "name",
		headerText : "부품명",
		dataType : "string",
		width : 180,
	}, {
		dataField : "version",
		headerText : "Rev",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oem",
		headerText : "OEN info",
		dataType : "string",
		width : 180,
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 180,
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 180,
	}, {
		dataField : "spec",
		headerText : "사양",
		dataType : "string",
		width : 180,
	}, {
		dataField : "enDoc1",
		headerText : "환경규제1번",
		dataType : "string",
		width : 180,
	}, {
		dataField : "enDoc2",
		headerText : "환경규제2번",
		dataType : "string",
		width : 180,
	}, {
		dataField : "enDoc3",
		headerText : "환경규제3번",
		dataType : "string",
		width : 180,
	}, {
		dataField : "enDoc4",
		headerText : "환경규제4번",
		dataType : "string",
		width : 180,
	}, {
		dataField : "progress",
		headerText : "진행률",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGridVer(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			rowCheckToRadio : false,
			fillColumnSizeMode: true,
		}
		
		verGridID = AUIGrid.create("#grid_ver", columnLayout, props);
<%-- 		AUIGrid.setGridData(verGridID, <%=json%>); --%>
		
		var json = [{
		    "seq" : 1,
		    "level" : 0,
		    "number" : "",
		    "drawingNum" : "ND",
		    "name" : "제품",
		    "version" : "D.3",
		    "oem" : "",
		    "state" : "승인됨",
		    "modifier" : "김준호",
		    "spec" : "",
		    "enDoc1" : "",
		    "enDoc2" : "",
		    "enDoc3" : "",
		    "enDoc4" : "",
		    "progress" : "75%"
		}, {
		    "seq" : 2,
		    "level" : 1,
		    "number" : "",
		    "drawingNum" : "	ND",
		    "name" : "반제품",
		    "version" : "C.1",
		    "oem" : "",
		    "state" : "승인됨",
		    "modifier" : "박영선",
		    "spec" : "",
		    "enDoc1" : "",
		    "enDoc2" : "",
		    "enDoc3" : "",
		    "enDoc4" : "",
		    "progress" : "50%"
		}, {
		    "seq" : 3,
		    "level" : 1,
		    "number" : "",
		    "drawingNum" : "	ND",
		    "name" : "반제품",
		    "version" : "E.5",
		    "oem" : "",
		    "state" : "승인됨",
		    "modifier" : "장원정",
		    "spec" : "",
		    "enDoc1" : "",
		    "enDoc2" : "",
		    "enDoc3" : "",
		    "enDoc4" : "",
		    "progress" : "50%"
		}, {
		    "seq" : 4,
		    "level" : 2,
		    "number" : "",
		    "drawingNum" : "	ND",
		    "name" : "나사",
		    "version" : "F.6",
		    "oem" : "",
		    "state" : "승인됨",
		    "modifier" : "박영선",
		    "spec" : "",
		    "enDoc1" : "",
		    "enDoc2" : "",
		    "enDoc3" : "",
		    "enDoc4" : "",
		    "progress" : "100%"
		}, {
		    "seq" : 5,
		    "level" : 3,
		    "number" : "",
		    "drawingNum" : "ND",
		    "name" : "볼트",
		    "version" : "G.9",
		    "oem" : "",
		    "state" : "승인됨",
		    "modifier" : "김준호",
		    "spec" : "",
		    "enDoc1" : "",
		    "enDoc2" : "",
		    "enDoc3" : "",
		    "enDoc4" : "",
		    "progress" : "25%"
		}]
		AUIGrid.setGridData(verGridID, json);
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGridVer(columnVer);
		AUIGrid.resize(verGridID);
	});
</script>
</body>
</html>