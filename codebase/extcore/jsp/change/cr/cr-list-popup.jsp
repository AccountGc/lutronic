<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%
ArrayList<NumberCode> sectionList = (ArrayList<NumberCode>) request.getAttribute("sectionList");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
%>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<input type="hidden" name="sortKey" id="sortKey">
<input type="hidden" name="sortType" id="sortType">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				CR 검색
			</div>
		</td>
	</tr>
</table>

<table class="search-table">
	<colgroup>
		<col width="130">
		<col width="*">
		<col width="130">
		<col width="*">
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th>CR 번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" class="width-300">
		</td>
		<th>CR 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-300">
		</td>
		<th>상태</th>
		<td class="indent5">
			<select name="state" id="state" class="width-200">
				<option value="">선택</option>
				<%
				for (Map<String, String> lifecycle : lifecycleList) {
					if (!lifecycle.get("code").equals("TEMPRARY")) {
				%>
				<option value="<%=lifecycle.get("code")%>"><%=lifecycle.get("name")%></option>
				<%
				}
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th>등록자</th>
		<td class="indent5">
			<input type="text" name="creator" id="creator" data-multi="false" class="width-200">
			<input type="hidden" name="creatorOid" id="creatorOid">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
		</td>
		<th>등록일</th>
		<td class="indent5">
			<input type="text" name="createdFrom" id="createdFrom" class="width-100">
			~
			<input type="text" name="createdTo" id="createdTo" class="width-100">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
		</td>
		<th>승인일</th>
		<td class="indent5">
			<input type="text" name="approveFrom" id="approveFrom" class="width-100">
			~
			<input type="text" name="approveTo" id="approveTo" class="width-100">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('approveFrom', 'approveTo')">
		</td>
	</tr>
	<tr>
		<th>작성자</th>
		<td class="indent5">
			<input type="text" name="writer" id="writer" data-multi="false" class="width-200">
		</td>
		<th>작성부서</th>
		<td class="indent5">
			<select name="createDepart" id="createDepart" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode deptcode : deptcodeList) {
				%>
				<option value="<%=deptcode.getCode()%>"><%=deptcode.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>작성일</th>
		<td class="indent5">
			<input type="text" name="writedFrom" id="writedFrom" class="width-100">
			~
			<input type="text" name="writedTo" id="writedTo" class="width-100">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('writedFrom', 'writedTo')">
		</td>

	</tr>
	<tr>
		<!-- 		<th>제안자</th> -->
		<!-- 		<td class="indent5"> -->
		<!-- 			<input type="text" name="proposer" id="proposer" data-multi="false" class="width-200"> -->
		<!-- 			<input type="hidden" name="proposerOid" id="proposerOid"> -->
		<!-- 			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')"> -->
		<!-- 		</td> -->
		<th>변경구분</th>
		<td class="indent5">
			<select name="changeSection" id="changeSection" class="width-200">
				<option value="">선택</option>
				<option value="">영업/마케팅</option>
				<option value="">원가 절감</option>
				<option value="">기능/성능 변경</option>
				<option value="">공정 변경</option>
				<option value="">자재 변경</option>
				<option value="">허가/규제 변경</option>
				<option value="">품질 개선</option>
				<option value="">라벨링</option>
				<option value="">기타</option>
			</select>
		</td>
		<th>프로젝트 코드</th>
		<td class="indent5" colspan="3">
			<input type="text" name="model" id="model" class="width-200">
			<input type="hidden" name="modelcode" id="modelcode">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearValue('model', 'code')">
		</td>
	</tr>
</table>
<table class="button-table">
	<tr>
		<td class="right">
			<select name="_psize" id="_psize" onchange="loadGridData();">
				<option value="10">10</option>
				<option value="20" selected="selected">20</option>
				<option value="30">30</option>
				<option value="50">50</option>
				<option value="100">100</option>
			</select>
			<input type="button" value="검색" title="검색" onclick="loadGridData();">
			<input type="button" value="추가" title="추가" class="blue" onclick="<%=method%>();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 390px; border-top: 1px solid #3180c3;"></div>
<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
<%@include file="/extcore/jsp/common/aui-context.jsp"%>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "rowNum",
		headerText : "번호",
		width : 40,
		dataType : "numeric",
		filter : {
			inline : false
		},
	},{
		dataField : "number",
		headerText : "CR 번호",
		dataType : "string",
		width : 150,
	}, {
		dataField : "name",
		headerText : "CR 제목",
		dataType : "string",
		style : "aui-left",
		width : 300,
	}, {
		dataField : "model",
		headerText : "프로젝트 코드 [명]",
		dataType : "string",
		width : 220,
		style : "aui-left",
		renderer : {
			type : "TemplateRenderer"
		},
	}, {
		dataField : "changeSection",
		headerText : "변경사유",
		dataType : "string",
		style : "aui-left",
		width : 220,
	}, {
		dataField : "createDepart",
		headerText : "작성부서",
		dataType : "string",
		width : 100,
	}, {
		dataField : "writer",
		headerText : "작성자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "writeDate",
		headerText : "작성일",
		dataType : "date",
		width : 100,
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			if (value === "승인됨") {
				return "approved";
			} else if (value === "ECPR작성중") {
				return "ecprStart";
			} else if (value === "ECPR승인중") {
				return "ecprApproving";
			}
			return null;
		}
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "date",
		width : 100,
	}, {
		dataField : "approveDate",
		headerText : "승인일",
		dataType : "string",
		width : 100,
	} ]
	
	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : false,
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
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
// 		loadGridData();
		AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
		AUIGrid.bind(myGridID, "vScrollChange", function(event) {
			hideContextMenu();
		});
		AUIGrid.bind(myGridID, "hScrollChange", function(event) {
			hideContextMenu();
		});
		AUIGrid.bind(myGridID, "cellClick", auiCellClick);
		AUIGrid.bind(myGridID, "sorting", auiSortingHandler);
	}
	
	let sortCache = [];
	function auiSortingHandler(event) {
		const sortingFields = event.sortingFields;
		const key = sortingFields[0].dataField;
		const sortType = sortingFields[0].sortType; // 오름차순 1 내림 -1
		sortCache[0] = {
			dataField : key,
			sortType : sortType
		};
		
		const _sortType = document.getElementById("sortType").value;
		if(Number(_sortType) !== Number(sortType)) {
			document.getElementById("sortKey").value = key;
			document.getElementById("sortType").value = sortType;
			loadGridData();
		}
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
		<%} else {%>
		if (AUIGrid.isCheckedRowById(event.pid, item._$uid)) {
			AUIGrid.addUncheckedRowsByIds(event.pid,item._$uid);
		} else {
			AUIGrid.addCheckedRowsByIds(event.pid, item._$uid);
		}
		<%}%>
	}
	
	function loadGridData(movePage) {
		if(movePage === undefined) {
			document.getElementById("sessionid").value = 0;
			document.getElementById("curPage").value = 1;
		}
		let params = new Object();
		const url = getCallUrl("/cr/list");
		const field = [ "sortKey", "sortType", "name", "number", "state", "creatorOid", "createdFrom", "createdTo", "approveFrom", "approveTo", "writer", "createDepart", "writedFrom", "writedTo", "changeSection", "modelcode" ];
		params = toField(params, field);
		AUIGrid.showAjaxLoader(myGridID);
		openLayer();
		logger(params);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			if (data.result) {
				totalPage = Math.ceil(data.total / data.pageSize);
				createPagingNavigator(data.total, data.curPage, data.sessionid);
				AUIGrid.setGridData(myGridID, data.list);
				if (sortCache.length > 0) {
					AUIGrid.setSorting(myGridID, sortCache);
				}
			} else {
				alert(data.msg);
			}
			closeLayer();
		});
	}
	
	function <%=method%>() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length === 0) {
			alert("추가할 CR을 선택하세요.");
			return false;
		}
		
		opener.<%=method%>(checkedItems, function(res, close, msg) {
			trigger(close, msg);
		})
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		toFocus("number");
		const contenxtHeader = genColumnHtml(columns);
		$("#h_item_ul").append(contenxtHeader);
		$("#headerMenu").menu({
			select : headerMenuSelectHandler
		});
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
		selectbox("state");
		finderUser("creator");
// 		finderUser("writer");
//			finderUser("proposer");
		twindate("created");
		twindate("approve");
		twindate("writed");
		selectbox("_psize");
		selectbox("changeSection");
		selectbox("createDepart");
		finderCode("model", "MODEL");
		$("#_psize").bindSelectSetValue("20");
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