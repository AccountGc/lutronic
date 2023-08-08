<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.change.service.ECOHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String moduleType = request.getParameter("moduleType");
JSONArray json = null;
if(moduleType.equals("ecr")){
	json = ECOHelper.manager.getRequestOrderLinkECOData(oid);
}else{
	json = PartHelper.manager.include_ChangeECOView(oid, moduleType);
}

%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 관련 ECO
			</div>
		</td>
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_ECOViewToggle" alt='include_ECOView' >
		</td>
	</tr>
</table>
<div id="grid_eco" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let ecoGridID;
	const columnEco = [ {
		dataField : "eoNumber",
		headerText : "ECO 번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "eoName",
		headerText : "ECO 제목",
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
		dataField : "eoApproveDate",
		headerText : "완료일",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid7(columnLayout) {
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
		ecoGridID = AUIGrid.create("#grid_eco", columnLayout, props);
		debugger;
		AUIGrid.setGridData(ecoGridID, <%=json%>);
	}
	
	//구성원 접기/펼치기
	$("#include_ECOViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
	})

</script>