<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.development.service.DevelopmentHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
// String moduleType = request.getParameter("moduleType");
// JSONArray json = DevelopmentHelper.manager.mockupVersionInfo();
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 환경규제문서
			</div>
		</td>
	</tr>
</table>
<div id="grid_enDoc" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let enDocGridID;
	const columnEnDoc = [ {
		dataField : "documentType",
		headerText : "문서유형",
		dataType : "string",
		width : 180,
	}, {
		dataField : "name",
		headerText : "문서명",
		dataType : "string",
		width : 180,
	}, {
		dataField : "version",
		headerText : "Rev",
		dataType : "string",
		width : 180,
	}, {
		dataField : "createDate",
		headerText : "등록일",
		dataType : "string",
		width : 180,
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 180,
	}, {
		dataField : "attach-secondary",
		headerText : "첨부파일",
		width : 180,
		 renderer : {
	            type : "ImageRenderer"
	      },
	      srcFunction : function(rowIndex, columnIndex, value, item) {
	    	   var src = "/Windchill/extcore/images/save.gif";
	    	   // 로직 처리
	    	   return src;
	    	}
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGridEnDoc(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			rowCheckToRadio : true,
			fillColumnSizeMode: true,
		}
		
		enDocGridID = AUIGrid.create("#grid_enDoc", columnLayout, props);
<%-- 		AUIGrid.setGridData(enDocGridID, <%=json%>); --%>
		
		const jsonEnDoc = [{
		    "documentType" : "DD-001",
		    "name" : "TEST1",
		    "version" : "A",
		    "createDate" : "2023-08-09",
		    "creator" : "김준호",
		}, {
		    "documentType" : "RD-002",
		    "name" : "TEST2",
		    "version" : "B",
		    "createDate" : "2023-08-09",
		    "creator" : "김준호",
		}, {
		    "documentType" : "RoHS-001",
		    "name" : "TEST3",
		    "version" : "C",
		    "createDate" : "2023-08-09",
		    "creator" : "김준호",
		}, {
		    "documentType" : "RoHS-002",
		    "name" : "TEST4",
		    "version" : "D",
		    "createDate" : "2023-08-09",
		    "creator" : "김준호",
		}]
		AUIGrid.setGridData(enDocGridID, jsonEnDoc);
	}
	
</script>