<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
List<Map<String,String>> lifecycleList = (List<Map<String,String>>) request.getAttribute("lifecycleList");
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
%>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				EO 검색
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
		<th>EO 번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" class="width-300">
		</td>
		<th>EO 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-300">
		</td>
		<th>상태</th>
		<td class="indent5">
			<select name="state" id="state" class="width-200">
				<option value="">선택</option>
				<%
				for (Map<String,String> lifecycle : lifecycleList) {
					if(!lifecycle.get("code").equals("TEMPRARY")){
				%>
				<option value="<%=lifecycle.get("code") %>"><%=lifecycle.get("name")%></option>
				<%
					}
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th>구분</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="eoType" value="" checked>
				<div class="state p-success">
					<label>
						<b>없음</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="eoType" value="DEV">
				<div class="state p-success">
					<label>
						<b>개발</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="eoType" value="PRODUCT">
				<div class="state p-success">
					<label>
						<b>양산</b>
					</label>
				</div>
			</div>
		</td>
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
	</tr>
	<tr>
		<th class="b">프로젝트 코드</th>
		<td class="indent5" colspan="3">
			<select name="model" id="model" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode model : modelList) {
				%>
				<option value="<%=model.getCode()%>"><%=model.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>승인일</th>
		<td class="indent5">
			<input type="text" name="approveForm" id="approveFrom" class="width-100">
			~
			<input type="text" name="approveTo" id="approveTo" class="width-100">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
		</td>
	</tr>
	<tr class="hidden">
		<th class="lb">완제품 품목</th>
		<td colspan="5" class="indent5 pt5">
			<jsp:include page="/extcore/jsp/change/include/complete-part-include.jsp">
				<jsp:param value="" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="▼펼치기" title="▼펼치기" class="red" onclick="spread(this);">
			<input type="button" value="추가" title="추가" onclick="<%=method%>();">
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
			<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 600px; border-top: 1px solid #3180c3;"></div>
<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
<%@include file="/extcore/jsp/common/aui-context.jsp"%>
<script type="text/javascript">
let myGridID;
const columns = [ {
	dataField : "number",
	headerText : "EO 번호",
	dataType : "string",
	width : 150,
	filter : {
		showIcon : true,
		inline : true
	},
	renderer : {
		type : "LinkRenderer",
		baseUrl : "javascript",
		jsCallback : function(rowIndex, columnIndex, value, item) {
			const oid = item.oid;
			const url = getCallUrl("/eo/view?oid=" + oid);
			popup(url, 1600, 800);
		}
	},
}, {
	dataField : "name",
	headerText : "EO 제목",
	dataType : "string",
	style : "aui-left",
	// 					width : 250,
	filter : {
		showIcon : true,
		inline : true
	},
	renderer : {
		type : "LinkRenderer",
		baseUrl : "javascript",
		jsCallback : function(rowIndex, columnIndex, value, item) {
			const oid = item.oid;
			const url = getCallUrl("/eo/view?oid=" + oid);
			popup(url, 1600, 800);
		}
	},
}, {
	dataField : "eoType",
	headerText : "구분",
	dataType : "string",
	width : 120,
	filter : {
		showIcon : true,
		inline : true
	},
}, {
	dataField : "eoType",
	headerText : "구분",
	dataType : "string",
	width : 120,
	filter : {
		showIcon : true,
		inline : true
	},
}, {
	dataField : "state",
	headerText : "상태",
	dataType : "string",
	width : 120,
	filter : {
		showIcon : true,
		inline : true
	},
}, {
	dataField : "creator",
	headerText : "등록자",
	dataType : "string",
	width : 100,
	filter : {
		showIcon : true,
		inline : true
	},
}, {
	dataField : "createdDate",
	headerText : "등록일",
	dataType : "date",
	width : 100,
	filter : {
		showIcon : true,
		inline : true
	},
}, {
	dataField : "approveDate_txt",
	headerText : "승인일",
	dataType : "date",
	width : 100,
	filter : {
		showIcon : true,
		inline : true
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
	let params = new Object();
	const url = getCallUrl("/eo/list");
	const field = [ "_psize", "name", "number", "eoType", "predate", "postdate", "creator", "state", "licensing", "model", "sortCheck", "sortValue", "riskType", "preApproveDate", "postApproveDate" ];
	const rows104 = AUIGrid.getGridDataWithState(myGridID104, "gridState");
	params.rows104 = rows104;
	params = toField(params, field);
	AUIGrid.showAjaxLoader(myGridID);
	openLayer();
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(myGridID);
		if (data.result) {
			totalPage = Math.ceil(data.total / data.pageSize);
			document.getElementById("sessionid").value = data.sessionid;
			createPagingNavigator(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
		} else {
			alert(data.msg);
		}
		closeLayer();
	});
}

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

document.addEventListener("DOMContentLoaded", function() {
	toFocus("number");
	const contenxtHeader = genColumnHtml(columns);
	$("#h_item_ul").append(contenxtHeader);
	$("#headerMenu").menu({
		select : headerMenuSelectHandler
	});
	createAUIGrid(columns);
	createAUIGrid104(columns104);
	AUIGrid.resize(myGridID);
	AUIGrid.resize(myGridID104);
	selectbox("state");
	finderUser("creator");
	twindate("created");
	twindate("approve");
	selectbox("_psize");
	selectbox("model");
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
	AUIGrid.resize(myGridID104);
});

// 등록
function create() {
	location.href = getCallUrl("/eo/create");
}

function spread(target) {
	const e = document.querySelectorAll('.hidden');
	// 버근가..
	for (let i = 0; i < e.length; i++) {
		const el = e[i];
		const style = window.getComputedStyle(el);
		const display = style.getPropertyValue("display");
		if (display === "none") {
			el.style.display = "table-row";
			target.value = "▲접기";
			selectbox("state");
			finderUser("creator");
			twindate("created");
			twindate("approve");
			selectbox("_psize");
			selectbox("model");
			AUIGrid.resize(myGridID104);
		} else {
			el.style.display = "none";
			target.value = "▼펼치기";
			selectbox("state");
			finderUser("creator");
			twindate("created");
			twindate("approve");
			selectbox("_psize");
			selectbox("model");
			AUIGrid.resize(myGridID104);
		}
	}
}
</script>