<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> sectionList = (ArrayList<NumberCode>) request.getAttribute("sectionList");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
%>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="lastNum" id="lastNum">
<input type="hidden" name="curPage" id="curPage">

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
		<th>ECPR 번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" class="width-300">
		</td>
		<th>ECPR 제목</th>
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
			<input type="hidden" name="writerOid" id="writerOid">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('writer')">
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
		<!-- 			<input type="hidden" name="proposerOid" id="proposerOid">  -->
		<!-- 			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')"> -->
		<!-- 		</td> -->
		<th>변경구분</th>
		<td class="indent5">
			<select name="changeSection" id="changeSection" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode section : sectionList) {
				%>
				<option value="<%=section.getCode()%>"><%=section.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>프로젝트 코드</th>
		<td class="indent5" colspan="3">
			<input type="text" name="model" id="model" class="width-200">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('model')">
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="right">
			<select name="_psize" id="_psize">
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

<div id="grid_wrap" style="height: 450px; border-top: 1px solid #3180c3;"></div>
<%@include file="/extcore/jsp/common/aui-context.jsp"%>
<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
<script type="text/javascript">
let myGridID;
const columns = [ {
	dataField : "number",
	headerText : "ECPR 번호",
	dataType : "string",
	width : 140,
	renderer : {
		type : "LinkRenderer",
		baseUrl : "javascript",
		jsCallback : function(rowIndex, columnIndex, value, item) {
			const oid = item.oid;
			const url = getCallUrl("/ecpr/view?oid=" + oid);
			popup(url, 1600, 800);
		}
	},
}, {
	dataField : "name",
	headerText : "ECPR 제목",
	dataType : "string",
	style : "aui-left",
	renderer : {
		type : "LinkRenderer",
		baseUrl : "javascript",
		jsCallback : function(rowIndex, columnIndex, value, item) {
			const oid = item.oid;
			const url = getCallUrl("/ecpr/view?oid=" + oid);
			popup(url, 1600, 800);
		}
	},
}, {
	dataField : "changeSection",
	headerText : "변경구분",
	dataType : "string",
	width : 120,
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
	dataType : "string",
	width : 100,
}, {
	dataField : "state",
	headerText : "상태",
	dataType : "string",
	width : 100,
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
	const url = getCallUrl("/ecpr/list");
	const field = ["name","number", "createdFrom", "createdTo", "creatorOid", "state", "writedFrom", "writedTo", "approveFrom", "approveTo", "createDepart", "writerOid", "model", "changeSection"];
	params = toField(params, field);
	AUIGrid.showAjaxLoader(myGridID);
	openLayer();
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(myGridID);
		if (data.result) {
			totalPage = Math.ceil(data.total / data.pageSize);
			createPagingNavigator(data.curPage, data.sessionid);
			AUIGrid.setGridData(myGridID, data.list);
		} else {
			alert(data.msg);
		}
		closeLayer();
	});
}

document.addEventListener("DOMContentLoaded", function() {
	const contenxtHeader = genColumnHtml(columns);
	$("#h_item_ul").append(contenxtHeader);
	$("#headerMenu").menu({
		select : headerMenuSelectHandler
	});
	createAUIGrid(columns);
	AUIGrid.resize(myGridID);
	selectbox("state");
	finderUser("creator");
// 	finderUser("writer");
//		finderUser("proposer");
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

// 추가 버튼
function <%=method%>() {
	const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
	if (checkedItems.length === 0) {
		alert("추가할 행을 선택하세요.");
		return false;
	}
	
	openLayer();
	opener.<%=method%>(checkedItems, function(res) {
		if(res) {
			setTimeout(function() {
				closeLayer();
			}, 500);
		}
	})
}
</script>