<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
String codeType = (String) request.getAttribute("codeType");
%>
<input type="hidden" name="codeType" id="codeType" value="<%=codeType%>">
<table class="search-table">
	<colgroup>
		<col width="130">
		<col width="*">
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th>이름</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-200">
		</td>
		<th>코드</th>
		<td class="indent5">
			<input type="text" name="code" id="code" class="width-200">
		</td>
	</tr>
	<tr>
		<th>설명</th>
		<td class="indent5" colspan="3">
			<input type="text" name="description" id="description" class="width-200">
		</td>
<!-- 		<th>활성화</th> -->
<!-- 		<td> -->
<!-- 			&nbsp; -->
<!-- 			<div class="pretty p-switch"> -->
<!-- 				<input type="radio" name="enabled" value="true" checked="checked"> -->
<!-- 				<div class="state p-success"> -->
<!-- 					<label> -->
<!-- 						<b>ON</b> -->
<!-- 					</label> -->
<!-- 				</div> -->
<!-- 			</div> -->
<!-- 			&nbsp; -->
<!-- 			<div class="pretty p-switch"> -->
<!-- 				<input type="radio" name="enabled" value="false"> -->
<!-- 				<div class="state p-success"> -->
<!-- 					<label> -->
<!-- 						<b>OFF</b> -->
<!-- 					</label> -->
<!-- 				</div> -->
<!-- 			</div> -->
<!-- 		</td> -->
	</tr>
</table>
<table class="button-table">
	<tr>
		<td class="right">
			<select name="_psize" id="_psize">
				<option value="30">30</option>
				<option value="50">50</option>
				<option value="100">100</option>
				<option value="200">200</option>
				<option value="300">300</option>
			</select>
			<input type="button" value="검색" title="검색" class="blue" onclick="loadGridData();">
			<input type="button" value="추가" title="추가" class="blue" onclick="<%=method%>();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 700px; border-top: 1px solid #3180c3;"></div>
<%@include file="/extcore/jsp/common/aui-context.jsp"%>
<script type="text/javascript">
	let myGridID;
	const columns= [ {
		dataField : "name",
		headerText : "이름",
		dataType : "string",
		width : 300,
	}, {
		dataField : "code",
		headerText : "코드",
		dataType : "string",
		width : 150,
	}, {
		dataField : "sort",
		headerText : "소트",
		dataType : "string",
		width : 100,
	}, {
		dataField : "description",
		headerText : "설명",
		dataType : "string",
		style : "aui-left",
	}, {
		dataField : "enabled",
		headerText : "활성화",
		dataType : "string",
		width : 120,
		renderer : {
			type : "CheckBoxEditRenderer",
			edtiable : false,
		},
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : true,
			<%if (!multi) {%>
			rowCheckToRadio : true,
			<%}%>
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableMovingColumn : true,
			enableFilter : true,
			showInlineFilter : false,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			autoGridHeight : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		loadGridData();
		AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
		AUIGrid.bind(myGridID, "vScrollChange", function(event) {
			hideContextMenu();
		});
		AUIGrid.bind(myGridID, "hScrollChange", function(event) {
			hideContextMenu();
		});
		AUIGrid.bind(myGridID, "cellClick", auiCellClick);
	}
	
	function auiCellClick(event) {
		const item = event.item;
		const rowIdField = AUIGrid.getProp(event.pid, "rowIdField"); // rowIdField 얻기
		const rowId = item[rowIdField];
		
		<%if (!multi) {%>
		// 이미 체크 선택되었는지 검사
		if (AUIGrid.isCheckedRowById(event.pid, rowId)) {
			// 엑스트라 체크박스 체크해제 추가
			AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
		} else {
			// 엑스트라 체크박스 체크 추가
			AUIGrid.setCheckedRowsByIds(event.pid, rowId);
		}
		<%}else{%>
		if (AUIGrid.isCheckedRowById(event.pid, item._$uid)) {
			AUIGrid.addUncheckedRowsByIds(event.pid,item._$uid);
		} else {
			AUIGrid.addCheckedRowsByIds(event.pid, item._$uid);
		}
		<%}%>
	}

	function loadGridData() {
		let params = new Object();
		const field = [ "name", "code", "description", "codeType" ];
		params = toField(params, field);
		const url = getCallUrl("/code/list");
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				AUIGrid.setGridData(myGridID, data.list);
			} else {
				alert(data.msg);
			}
			closeLayer();
		})
	}
	
	function <%=method%>() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length === 0) {
			alert("추가할 행을 선택하세요.");
			return false;
		}

		opener.<%=method%>(checkedItems, function(res, close, msg) {
			trigger(close, msg);
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("name");
		const contenxtHeader = genColumnHtml(columns);
		$("#h_item_ul").append(contenxtHeader);
		$("#headerMenu").menu({
			select : headerMenuSelectHandler
		});
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
		selectbox("_psize");
	});

	document.addEventListener("keydown", function(event) {
		const keyCode = event.keyCode || event.which;
		if (keyCode === 13) {
			loadGridData();
		}
	})

	document.addEventListener("click", function(event) {
		hideContextMenu();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});

</script>