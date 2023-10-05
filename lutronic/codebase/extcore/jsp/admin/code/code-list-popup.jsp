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
<input type="hidden" name="codeType" id="codeType" value="<%=codeType %>">
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
		<td class="indent5">
			<input type="text" name="description" id="description" class="width-200">
		</td>
		<th>활성화</th>
		<td class="indent5">
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="enabled" value="true" checked="checked">
				<div class="state p-success">
					<label>
						<b>ON</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="enabled" value="false">
				<div class="state p-success">
					<label>
						<b>OFF</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
</table>
<table class="button-table">
	<tr>
		<td class="left">
			<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
			<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('code-list');">
			<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('code-list');">
		</td>
		<td class="right">
			<select name="_psize" id="_psize">
				<option value="30">30</option>
				<option value="50">50</option>
				<option value="100">100</option>
				<option value="200">200</option>
				<option value="300">300</option>
			</select>
			<input type="button" value="검색" title="검색" class="blue" onclick="loadGridData();">
			<input type="button" value="목록 열기/닫기" title="목록 열기/닫기">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 700px; border-top: 1px solid #3180c3;"></div>
<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
<%@include file="/extcore/jsp/common/aui-context.jsp"%>
<script type="text/javascript">
	let myGridID;
	function _layout() {
		return [ {
			dataField : "name",
			headerText : "이름",
			dataType : "string",
			width : 300,
			filter : {
				showIcon : true,
			},
		}, {
			dataField : "code",
			headerText : "코드",
			dataType : "string",
			width : 150,
			filter : {
				showIcon : true,
			},
		}, {
			dataField : "sort",
			headerText : "소트",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
			},
		}, {
			dataField : "description",
			headerText : "설명",
			dataType : "string",
			style : "aui-left",
			filter : {
				showIcon : true,
			},
		}, {
			dataField : "enabled",
			headerText : "활성화",
			dataType : "string",
			width : 120,
			renderer : {
				type : "CheckBoxEditRenderer",
				edtiable : false,
			},
			filter : {
				showIcon : false,
			},
		} ]
	}

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
			enableMovingColumn : true,
			enableFilter : true,
			showInlineFilter : false,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
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
	}

	function loadGridData() {
		const type = document.getElementById("codeType").value = type;
		const params = {
			type : type
		}
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

	document.addEventListener("DOMContentLoaded", function() {
		const columns = loadColumnLayout("code-list");
		const contenxtHeader = genColumnHtml(columns);
		$("#h_item_ul").append(contenxtHeader);
		$("#headerMenu").menu({
			select : headerMenuSelectHandler
		});
		createAUIGrid(columns);
		selectbox("_psize");
	});

	document.addEventListener("keydown", function(event) {
		const keyCode = event.keyCode || event.which;
		if (keyCode === 13) {
			searchData();
		}
	})

	document.addEventListener("click", function(event) {
		hideContextMenu();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});

</script>