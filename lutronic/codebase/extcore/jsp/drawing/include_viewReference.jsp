<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String moduleType = request.getParameter("moduleType");
JSONArray json = DrawingHelper.manager.include_Reference(oid, moduleType);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 참조
			</div>
		</td>
		<td align="right">
<!-- 			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_ReferenceViewToggle" alt='include_ReferenceView' > -->
		</td>
	</tr>
</table>
<div id="grid_ref" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let refGridID;
	const columnRef = [ {
		dataField : "number",
		headerText : "도면 번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "name",
		headerText : "도면명",
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
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 180,
	}, {
		dataField : "linkRefernceType",
		headerText : "종속유형",
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

	function createAUIGrid2(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			rowCheckToRadio : true
		}
		refGridID = AUIGrid.create("#grid_ref", columnLayout, props);
		AUIGrid.setGridData(refGridID, <%=json%>);
	}
	
	//구성원 접기/펼치기
	$("#include_ReferenceViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
	})

</script>