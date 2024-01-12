<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String partNumber = request.getParameter("partNumber");
%>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<input type="hidden" name="partNumber" id="partNumber" value="<%=partNumber%>" />
<input type="hidden" name="sortKey" id="sortKey">
<input type="hidden" name="sortType" id="sortType">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				SEQ 현황
			</div>
		</td>
		<td class="right">
			<select name="_psize" id="_psize" onchange="loadGridData();">
				<option value="10">10</option>
				<option value="20" selected="selected">20</option>
				<option value="30">30</option>
				<option value="50">50</option>
				<option value="100">100</option>
			</select>
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 430px; border-top: 1px solid #3180c3;"></div>
<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		style : "aui-left",
		width : 350,
		sortable : false
	}, {
		dataField : "location",
		headerText : "품목분류",
		dataType : "string",
		width : 180,
		sortable : false
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
		sortable : false
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80,
		sortable : false
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
		sortable : false
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "string",
		width : 100,
		sortable : false
	}, {
		dataField : "modifitedDate",
		headerText : "수정일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "remarks",
		headerText : "OEM Info.",
		dataType : "string",
		width : 100,
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : false,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableFilter : true,
			showInlineFilter : false,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		loadGridData();
		AUIGrid.bind(myGridID, "sorting", auiSortingHandler);
	}
	let sortCache = [];
	let compField;
	function auiSortingHandler(event) {
		const sortingFields = event.sortingFields;
		if (sortingFields.length > 0) {
			const key = sortingFields[0].dataField;
			if (compField !== key) {
				compField = key;
				const sortType = sortingFields[0].sortType; // 오름차순 1 내림 -1
				sortCache[0] = {
					dataField : key,
					sortType : sortType
				};
				document.getElementById("sortKey").value = key;
				document.getElementById("sortType").value = sortType;
				loadGridData();
			}
		}
	}

	function loadGridData(movePage) {
		if (movePage === undefined) {
			document.getElementById("sessionid").value = 0;
			document.getElementById("curPage").value = 1;
		}
		const url = getCallUrl("/part/seq");
		let params = new Object();
		const field = [ "partNumber", "sortKey", "sortType" ];
		params = toField(params, field);
		openLayer();
		AUIGrid.showAjaxLoader(myGridID);
		logger(params);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			if (data.result) {
				totalPage = Math.ceil(data.total / data.pageSize);
				createPagingNavigator(data.curPage, data.sessionid);
				AUIGrid.setGridData(myGridID, data.list);
				if (movePage === undefined) {
					AUIGrid.setSorting(myGridID, sortCache);
					compField = null;
				}
			} else {
				alert(data.msg);
			}
			closeLayer();
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
		selectbox("_psize");
		$("#_psize").bindSelectSetValue("20");
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>