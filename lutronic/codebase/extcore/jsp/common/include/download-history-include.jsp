<%@page import="com.e3ps.download.service.DownloadHistoryHelper"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%
String oid = request.getParameter("oid");
String height = StringUtil.checkReplaceStr(request.getParameter("height"), "150");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				다운로드 이력
			</div>
		</td>
	</tr>
</table>
<table class="create-table">
	<tr>
		<td class="lb">
			<div id="grid51" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID51;
	const columns51 = [ {
		dataField : "name",
		headerText : "이름",
		dataType : "string",
		width : 150,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "id",
		headerText : "아이디",
		dataType : "string",
		width : 150,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "version",
		headerText : "해당모듈",
		dataType : "string",
		width : 80,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "count",
		headerText : "다운로드횟수",
		dataType : "numeric",
		width : 150,
		postfix : "회",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "time",
		headerText : "다운로드시간",
		dataType : "date",
		dateInputFormat : "yyyy-mm-dd",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "modifier",
		headerText : "다운로드사유",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	} ]

	function createAUIGrid51(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			selectionMode : "multipleCells",
			enableFilter : true,
		}
		myGridID51 = AUIGrid.create("#grid51", columnLayout, props);
		AUIGrid.setGridData(myGridID51, <%=DownloadHistoryHelper.manager.dLogger(oid)%>);
	}
</script>