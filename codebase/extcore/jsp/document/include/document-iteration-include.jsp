<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%
String oid = request.getParameter("oid");
boolean popup = Boolean.parseBoolean((String) request.getParameter("popup"));
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				버전 이력
			</div>
		</td>
		<%
		if (popup) {
		%>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
		<%
		}
		%>
	</tr>
</table>
<div id="grid50" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID50;
	const columns50 = [ {
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
				let permission = isPermission(oid);
				if (!permission) {
					authMsg();
					return false;
				}
				const url = getCallUrl("/doc/view?oid=" + oid);
				_popup(url, "", "", "f");
			}
		},
	}, {
		dataField : "number",
		headerText : "문서번호",
		dataType : "string",
		width : 180,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				let permission = isPermission(oid);
				if (!permission) {
					authMsg();
					return false;
				}
				const url = getCallUrl("/doc/view?oid=" + oid);
				_popup(url, "", "", "f");
			}
		},
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
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "modifiedDate",
		headerText : "수정일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "primary",
		headerText : "주 첨부파일",
		dataType : "string",
		width : 80,
		renderer : {
			type : "TemplateRenderer"
		},
	}, {
		dataField : "secondary",
		headerText : "첨부파일",
		dataType : "string",
		width : 100,
		renderer : {
			type : "TemplateRenderer"
		},
	}, {
		dataField : "note",
		headerText : "수정사유",
		dataType : "string",
		style : "aui-left",
	} ]

	function createAUIGrid50(columnLayout) {
		const props = {
			headerHeight : 30,
			// 			fillColumnSizeMode : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableFilter : true,
			autoGridHeight : true
		}
		myGridID50 = AUIGrid.create("#grid50", columnLayout, props);
		AUIGrid.setGridData(myGridID50, <%=DocumentHelper.manager.allIterationsOf(oid)%>);
	}
	<%
		if(popup) {
	%>
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid50(columns50);
		AUIGrid.resize(myGridID50);
	})
	
	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID50);
	})
	<%
		}
	%>
</script>