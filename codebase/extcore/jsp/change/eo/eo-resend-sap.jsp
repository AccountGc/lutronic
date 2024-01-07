<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.change.eo.dto.EoDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EoDTO dto = (EoDTO) request.getAttribute("dto");
JSONArray data = (JSONArray) request.getAttribute("data");
%>

<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				EO 재전송
			</div>
		</td>
		<td class="right">
			<input type="button" value="검증" title="검증" class="red" onclick="validate();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="130">
		<col width="350">
		<col width="130">
		<col width="350">
	</colgroup>
	<tr>
		<th class="lb">EO 번호</th>
		<td class="indent5"><%=dto.getNumber()%></td>
		<th>EO 제목</th>
		<td class="indent5"><%=dto.getName()%></td>
	</tr>
	<tr>
		<th class="lb">재전송품목 목록</th>
		<td colspan="3" class="pt5">
			<div id="grid_wrap" style="margin-left: 5px; margin-bottom: 5px; height: 530px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>

<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "_3d",
		headerText : "3D",
		dataType : "string",
		width : 40,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 16,
			onClick : function(event) {
			}
		},
	}, {
		dataField : "_2d",
		headerText : "2D",
		dataType : "string",
		width : 40,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 16,
			onClick : function(event) {
			}
		},
	}, {
		dataField : "icon",
		headerText : "",
		dataType : "string",
		width : 40,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 16,
		},
	}, {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		style : "aui-left",
		width : 380,
	}, {
		dataField : "location",
		headerText : "품목분류",
		dataType : "string",
		width : 250,
		style : "aui-left"
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
		renderer : {
			type : "TemplateRenderer"
		},
	}, {
		dataField : "remarks",
		headerText : "OEM Info.",
		dataType : "string",
		width : 100,
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			if (value === "승인됨") {
				return "approved";
			}
			return null;
		}
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 140,
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "date",
		width : 100,
	}, {
		dataField : "modifiedDate",
		headerText : "수정일",
		dataType : "date",
		width : 100,
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID,
<%=data%>
	);
	}

	function validate() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/eo/sendValidate?oid=" + oid);
		call(url, null, function(data) {
			alert(data.msg);
			if(data.result) {
				
			}
		}, "GET");
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>