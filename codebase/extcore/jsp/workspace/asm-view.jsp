<%@page import="com.e3ps.workspace.dto.AsmDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
AsmDTO dto = (AsmDTO) request.getAttribute("dto");
String title = (String) request.getAttribute("title");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<%=title %> 일괄결재 상세보기
			</div>
		</td>
		<td class="right">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="150">
		<col width="430">
		<col width="150">
		<col width="430">
	</colgroup>
	<tr>
		<th class="lb"><%=title %> 일괄결재제목</th>
		<td class="indent5"><%=dto.getName()%></td>
		<th><%=title %> 일괄결재번호</th>
		<td class="indent5"><%=dto.getNumber()%></td>
	</tr>
	<tr>
		<th class="lb">상태</th>
		<td class="indent5"><%=dto.getState()%></td>
		<th>등록자</th>
		<td class="indent5"><%=dto.getCreator()%></td>
	</tr>
	<tr>
		<th class="lb">등록일</th>
		<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
		<th>수정일</th>
		<td class="indent5"><%=dto.getModifiedDate_text()%></td>
	</tr>
	<tr>
		<th class="lb">설명</th>
		<td class="indent5" colspan="3"><%=dto.getDescription()%></td>
	</tr>
</table>


<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				일괄결재 대상
			</div>
		</td>
	</tr>
</table>

<div id="grid90" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>

<!-- 결재 이력 -->
<jsp:include page="/extcore/jsp/workspace/include/approval-history.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
</jsp:include>

<script type="text/javascript">
	let myGridID90;
	const columns90 = [ {
		dataField : "number",
		headerText : "번호",
		dataType : "string",
		width : 180,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const url = item.viewUrl;
				_popup(url, "", "", "f");
			}
		},		
	}, {
		dataField : "name",
		headerText : "제목",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const url = item.viewUrl;
				_popup(url, "", "", "f");
			}
		},		
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80,
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "createdDate_txt",
		headerText : "등록일",
		dataType : "string",
		width : 100,
	} ]

	function createAUIGrid90(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableFilter : true,
			autoGridHeight : true
		}
		myGridID90 = AUIGrid.create("#grid90", columnLayout, props);
		AUIGrid.setGridData(myGridID90,
<%=dto.getData()%>
	);
	}

	function _delete() {
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/asm/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			}
			closeLayer();
		}, "GET");
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid90(columns90);
		createAUIGrid10000(columns10000);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID10000);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID10000);
	})
</script>