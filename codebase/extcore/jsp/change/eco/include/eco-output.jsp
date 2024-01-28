<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="wt.doc.DocumentType"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String location = (String) request.getAttribute("location");
location = StringUtil.checkString(location) == true ? location : DocumentHelper.DOCUMENT_ROOT;
ArrayList<NumberCode> preserationList = (ArrayList<NumberCode>) request.getAttribute("preserationList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
String state = (String) request.getAttribute("state");
String oid = (String) request.getAttribute("oid");
%>
<style type="text/css">
.approved {
	font-weight: bold !important;
	color: red !important;
}
</style>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<input type="hidden" name="oid" id="oid" value="<%=oid %>">
<input type="hidden" name="sortKey" id="sortKey">
<input type="hidden" name="sortType" id="sortType">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 문서
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
		<th>문서 분류</th>
		<td class="indent5">
			<%
			if (StringUtil.checkString(location)) {
			%>
			<input type="hidden" name="location" id="location" value="<%=location%>">
			<span id="locationText"><%=location%></span>
			<%
			} else {
			%>
			<input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT%>">
			<span id="locationText"><%=DocumentHelper.DOCUMENT_ROOT%></span>
			<%
			}
			%>
		</td>
		<th>문서번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" class="width-300">
		</td>
		<th>문서명</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-300">
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
		<th>상태</th>
		<td class="indent5">
			<select name="state" id="state" class="width-200">
				<option value="">선택</option>
				<%
				for (Map<String, String> lifecycle : lifecycleList) {
					if (!lifecycle.get("code").equals("TEMPRARY")) {
				%>
				<%
				if (StringUtil.checkString(state)) {
				%>
				<option value="<%=lifecycle.get("code")%>" <%if (state.equals(lifecycle.get("code"))) {%> selected="selected" <%}%>><%=lifecycle.get("name")%></option>
				<%
				} else {
				%>
				<option value="<%=lifecycle.get("code")%>"><%=lifecycle.get("name")%></option>
				<%
				}
				%>
				<%
				}
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th>작성자</th>
		<td class="indent5">
			<input type="text" name="writer" id="writer" class="width-200">
		</td>
		<th>수정일</th>
		<td class="indent5">
			<input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100">
			~
			<input type="text" name="modifiedTo" id="modifiedTo" class="width-100">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('modifiedFrom', 'modifiedTo')">
		</td>
		<th>프로젝트 코드</th>
		<td class="indent5">
			<input type="text" name="model" id="model" class="width-200">
			<input type="hidden" name="modelcode" id="modelcode">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('model')">
		</td>
	</tr>
	<tr>
		<th>보존기간</th>
		<td class="indent5">
			<select name="preseration" id="preseration" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode preseration : preserationList) {
				%>
				<option value="<%=preseration.getCode()%>"><%=preseration.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>REV</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="latest" value="true" checked="checked">
				<div class="state p-success">
					<label>
						<b>최신REV</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="latest" value="false">
				<div class="state p-success">
					<label>
						<b>모든REV</b>
					</label>
				</div>
			</div>
		</td>
		<th>부서</th>
		<td class="indent5" colspan="3">
			<select name="deptcode" id="deptcode" class="width-200">
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
		<!-- 				<th>내용</th> -->
		<!-- 				<td class="indent5"> -->
		<!-- 					<input type="text" name="description" id="description" class="width-300"> -->
		<!-- 				</td> -->
	</tr>
</table>
<table class="button-table">
	<tr>
		<td class="left"></td>
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
<table>
	<colgroup>
		<col width="230">
		<col width="10">
		<col width="*">
	</colgroup>
	<tr>
		<td valign="top">
			<jsp:include page="/extcore/jsp/common/folder-include.jsp">
				<jsp:param value="<%=location%>" name="location" />
				<jsp:param value="product" name="container" />
				<jsp:param value="list" name="mode" />
				<jsp:param value="393" name="height" />
			</jsp:include>
		</td>
		<td valign="top">&nbsp;</td>
		<td valign="top">
			<div id="grid_wrap" style="height: 390px; border-top: 1px solid #3180c3;"></div>
			<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
			<%@include file="/extcore/jsp/common/aui-context.jsp"%>
		</td>
	</tr>
</table>
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
	sortable : false
}, {
	dataField : "name",
	headerText : "문서명",
	dataType : "string",
	style : "aui-left",
	width : 350,
}, {
	dataField : "number",
	headerText : "문서번호",
	dataType : "string",
	width : 180,
}, {
	dataField : "model",
	headerText : "프로젝트 코드 [명]",
	dataType : "string",
	width : 120,
	style : "aui-left",
	sortable : false,
	renderer : {
		type : "TemplateRenderer"
	},
}, {
	dataField : "location",
	headerText : "문서분류",
	dataType : "string",
	style : "aui-left",
	width : 250,
	sortable : false
}, {
//		dataField : "classType1_name",
//		headerText : "대분류",
//		dataType : "string",
//		width : 100,
//		sortable : false
//	}, {
//		dataField : "classType2_name",
//		headerText : "중분류",
//		dataType : "string",
//		width : 200,
//		sortable : false
//	}, {
//		dataField : "classType3_name",
//		headerText : "소분류",
//		dataType : "string",
//		width : 100,
//		sortable : false
//	}, {
	dataField : "version",
	headerText : "REV",
	dataType : "string",
	width : 80,
	sortable : false,
	renderer : {
		type : "TemplateRenderer"
	},
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
	dataField : "writer",
	headerText : "작성자",
	dataType : "string",
	width : 100,
	sortable : false
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
	dataField : "modifiedDate",
	headerText : "수정일",
	dataType : "date",
	width : 100,
//	}, {
//		dataField : "primary",
//		headerText : "주 첨부파일",
//		dataType : "string",
//		width : 80,
//		sortable : false,
//		renderer : {
//			type : "TemplateRenderer"
//		},
//		filter : {
//			inline : false
//		},
//	}, {
//		dataField : "secondary",
//		headerText : "첨부파일",
//		dataType : "string",
//		width : 100,
//		sortable : false,
//		renderer : {
//			type : "TemplateRenderer"
//		},
//		filter : {
//			inline : false
//		},
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
	if (movePage === undefined) {
		document.getElementById("sessionid").value = 0;
		document.getElementById("curPage").value = 1;
	}
	let params = new Object();
	const url = getCallUrl("/doc/list");
	const field = [ "sortKey", "sortType", "location", "name", "number", "state", "creatorOid", "createdFrom", "createdTo", "modifiedFrom", "modifiedTo", "preseration", "modelcode", "deptcode", "writer", "description" ];
	const latest = document.querySelector("input[name=latest]:checked").value;
	params = toField(params, field);
	params.latest = JSON.parse(latest);
	AUIGrid.showAjaxLoader(myGridID);
	openLayer();
	logger(params);
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(myGridID);
		if (data.result) {
			totalPage = Math.ceil(data.total / data.pageSize);
			createPagingNavigator(data.total, data.curPage, data.sessionid);
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
	toFocus("number");
	const contenxtHeader = genColumnHtml(columns);
	$("#h_item_ul").append(contenxtHeader);
	$("#headerMenu").menu({
		select : headerMenuSelectHandler
	});
	createAUIGrid(columns);
	AUIGrid.resize(myGridID);
	_createAUIGrid(_columns);
	AUIGrid.resize(_myGridID);
	selectbox("state");
	finderUser("creator");
	twindate("created");
	twindate("modified");
	selectbox("_psize");
	selectbox("documentType");
	selectbox("preseration");
	selectbox("deptcode");
	<%if (StringUtil.checkString(state)) {%>
	$("#state").bindSelectSetValue("<%=state%>");
	$("#state").bindSelectDisabled(true);
	<%}%>
	finderCode("model", "MODEL");
	$("#_psize").bindSelectSetValue("20");
;});

function <%=method%>() {
	const oid = document.getElementById("oid").value;
	const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
	if (checkedItems.length === 0) {
		alert("추가할 문서를 선택하세요.");
		return false;
	}
	
	opener.<%=method%>(checkedItems, oid, function(res, close, msg) {
		trigger(close, msg);
	})
}
	

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