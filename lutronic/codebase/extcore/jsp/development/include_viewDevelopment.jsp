<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 관련 개발업무
			</div>
		</td>
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_DevelopmentViewToggle" alt='include_DevelopmentView' >
		</td>
	</tr>
</table>
<div id="grid_dev" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let devGridID;
	const columnDev = [ {
		dataField : "masterName",
		headerText : "프로젝트명",
		dataType : "string",
		width : 180,
	}, {
		dataField : "taskName",
		headerText : "TASK",
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
		dataField : "name",
		headerText : "ACTIVITY",
		dataType : "string",
		width : 180,
	}, {
		dataField : "workerName",
		headerText : "수행자",
		dataType : "string",
		width : 180,
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid4(columnLayout) {
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
		devGridID = AUIGrid.create("#grid_dev", columnLayout, props);
<%-- 		AUIGrid.setGridData(ecrGridID, <%=ProjectHelper.manager.jsonAuiProject(oid)%>); --%>
	}
	
	//구성원 접기/펼치기
	$("#include_DevelopmentViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
	})

</script>