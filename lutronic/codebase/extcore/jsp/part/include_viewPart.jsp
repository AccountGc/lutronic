<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.part.dto.PartDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String title = request.getParameter("title");
String moduleType = request.getParameter("moduleType");
List<PartDTO> partList = PartHelper.service.include_PartList(oid, moduleType);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> <%=title%>
			</div>
		</td>
		<td align="right">
<!-- 			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_PartViewToggle" alt='include_PartView' > -->
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">관련 품목</th>
		<td class="indent5">
			<div id="grid_part" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let partGridID;
	const columnPart = [ {
		dataField : "number",
		headerText : "품목 번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		width : 180,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
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

	function createAUIGrid1(columnLayout) {
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
		let dataList = [];
		<% for(PartDTO part : partList) { %>
			var data = new Object();
			data.number = "<%= part.getNumber() %>"
			data.name = "<%= part.getName() %>"
			data.state = "<%= part.getState() %>"
			data.version = "<%= part.getVersion() %>"
			data.creator = "<%= part.getCreator() %>"
			data.modifyDate = "<%= part.getModifyDate() %>"
			data.oid = "<%= part.getOid() %>"
			dataList.push(data);
		<% } %>
		partGridID = AUIGrid.create("#grid_part", columnLayout, props);
		AUIGrid.setGridData(partGridID, dataList);
	}
	
	//구성원 접기/펼치기
	$("#include_PartViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
	})

</script>