<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.part.dto.PartDTO"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String moduleType = request.getParameter("moduleType");
JSONArray json = DrawingHelper.manager.include_Reference(oid, moduleType);
JSONArray json2 = DrawingHelper.manager.include_ReferenceBy(oid);
List<PartDTO> partList = PartHelper.service.include_PartList(oid, moduleType);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 참조
			</div>
		</td>
		<td align="right">
		</td>
	</tr>
</table>
<div id="grid_ref" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 참조 항목
			</div>
		</td>
	</tr>
</table>
<div id="grid_refby" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">  참조 품목
			</div>
		</td>
		<td align="right">
		</td>
	</tr>
</table>
<div id="grid_part" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>

<script type="text/javascript">
	/**************************************** 참조 ***********************************/
	let refGridID;
	const columnRef = [ {
		dataField : "number",
		headerText : "도면 번호",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "name",
		headerText : "도면명",
		dataType : "string",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/project/info?oid=" + oid);
				popup(url);
			}
		},
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "linkRefernceType",
		headerText : "종속유형",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "modifyDate",
		headerText : "수정일",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "oid",
		visible : false
	} ];

	function createAUIGrid2(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : true,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
			enableFilter : true,
			autoGridHeight : true
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
	});
	
	
	/**************************************** 참조항목 ***********************************/
	let refbyGridID;
	const columnRefby = [ {
		dataField : "number",
		headerText : "도면 번호",
		dataType : "string",
		filter : {
			showIcon : true,
		},
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
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "linkRefernceType",
		headerText : "종속유형",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "modifyDate",
		headerText : "수정일",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "oid",
		visible : false
	} ];

	function createAUIGrid3(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : true,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
			enableFilter : true,
			autoGridHeight : true
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
	});
	

	/**************************************** 참조품목 ***********************************/
	let partGridID;
	const columnPart = [ {
		dataField : "number",
		headerText : "품목 번호",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "modifyDate",
		headerText : "수정일",
		dataType : "string",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid1(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : true,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
			enableFilter : true,
			autoGridHeight : true
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
	});
</script>