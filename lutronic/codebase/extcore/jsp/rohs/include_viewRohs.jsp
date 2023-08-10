<%@page import="com.e3ps.rohs.service.RohsHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String title = request.getParameter("title");
String roleType = request.getParameter("roleType");
String module = StringUtil.checkReplaceStr(request.getParameter("module"), "rohs");
JSONArray json = RohsHelper.manager.include_RohsView(oid, module, roleType);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 
				<%=title%>
			</div>
		</td>
		<td align="right">
<!-- 			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_RohsViewToggle" alt='include_RohsView' > -->
		</td>
	</tr>
</table>
<%
if(roleType.equals("represent")){
%>
	<div id="grid_rohs" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<%
}else{
%>
	<div id="grid_rohs2" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<%
}
%>
<script type="text/javascript">
	<%
	if(roleType.equals("represent")){
	%>	
		let rohsGridID;
		const columnRohs
	<%
	}else{
	%>
		let rohs2GridID;
		const columnRohs2
	<%
	}
	%>
		= [ {
			dataField : "manufactureDisplay",
			headerText : "협력업체",
			dataType : "string",
			width : 180,
		}, {
			dataField : "number",
			headerText : "물질번호",
			dataType : "string",
			width : 180,
		}, {
			dataField : "name",
			headerText : "물질명",
			dataType : "string",
			width : 180,
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.oid;
					const url = getCallUrl("/project/info?oid=" + oid);
					popup(url);
				}
			},
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
			width : 180,
		}, {
			dataField : "version",
			headerText : "Rev.",
			dataType : "string",
			width : 180,
		}, {
			dataField : "creator",
			headerText : "등록자",
			dataType : "string",
			width : 180,
		}, {
			dataField : "modifyDate",
			headerText : "수정일",
			dataType : "string",
			width : 180,
		}, {
			dataField : "oid",
			visible : false
		} ]
	
	

	function createAUIGridRohs1(columnLayout) {
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
		rohsGridID = AUIGrid.create("#grid_rohs", columnLayout, props);
		AUIGrid.setGridData(rohsGridID, <%=json%>);
	}
	
	function createAUIGridRohs2(columnLayout) {
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
		rohs2GridID = AUIGrid.create("#grid_rohs2", columnLayout, props);
		AUIGrid.setGridData(rohs2GridID, <%=json%>);
	}
	
	//구성원 접기/펼치기
	$("#include_RohsViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
	})

</script>