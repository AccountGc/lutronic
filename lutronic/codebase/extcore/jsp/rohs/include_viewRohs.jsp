<%@page import="com.e3ps.rohs.service.RohsHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String title = request.getParameter("title");
String mode = request.getParameter("mode");
String roleType = request.getParameter("roleType");
String module = StringUtil.checkReplaceStr(request.getParameter("module"), "rohs");
boolean view = "view".equals(mode);
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

<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb"><%=title%></th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (!view) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="popup91();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow91();">
			<%
			}
			%>
			<%
			if(roleType.equals("represent")){
			%>
				<div id="grid_rohs" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
			<%
			}else{
			%>
				<div id="grid_rohs2" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
			<%
			}
			%>
		</td>
	</tr>
</table>
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
		}, {
			dataField : "number",
			headerText : "물질번호",
			dataType : "string",
		}, {
			dataField : "name",
			headerText : "물질명",
			dataType : "string",
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.oid;
					const url = getCallUrl("/rohs/view?oid=" + oid);
					popup(url, 1600, 800);
				}
			},
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
		}, {
			dataField : "version",
			headerText : "Rev.",
			dataType : "string",
		}, {
			dataField : "creator",
			headerText : "등록자",
			dataType : "string",
		}, {
			dataField : "modifyDate",
			headerText : "수정일",
			dataType : "string",
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
			softRemoveRowMode : true,
			selectionMode : "multipleCells",
			rowCheckToRadio : true,
			fillColumnSizeMode : false,
			autoGridHeight : true
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
			softRemoveRowMode : true,
			selectionMode : "multipleCells",
			rowCheckToRadio : true,
			fillColumnSizeMode : false,
			autoGridHeight : true
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