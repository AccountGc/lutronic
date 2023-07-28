<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
JSONArray json = DrawingHelper.manager.include_ReferenceBy(oid);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 참조 항목
			</div>
		</td>
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_ReferenceByViewToggle" alt='include_ReferenceByView' >
		</td>
	</tr>
</table>
<div id="grid_refby" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let refbyGridID;
	const columnRefby = [ {
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
		dataField : "version",
		headerText : "Rev.",
		dataType : "string",
		width : 180,
	}, {
		dataField : "linkRefernceType",
		headerText : "종속유형",
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

	function createAUIGrid3(columnLayout) {
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
		refbyGridID = AUIGrid.create("#grid_refby", columnLayout, props);
		AUIGrid.setGridData(refbyGridID, <%=json%>);
	}
	
	//구성원 접기/펼치기
	$("#include_ReferenceByViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
	})

</script>