<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
List<Map<String,String>> lifecycleList = (List<Map<String,String>>) request.getAttribute("lifecycleList");
String method = (String) request.getAttribute("method");
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
		<th>물질명</th>
		<td class="indent5">
			<input type="text" name="rohsName" id="rohsName" class="width-300">
		</td>
		<th>물질 번호</th>
		<td class="indent5">
			<input type="text" name="rohsNumber" id="rohsNumber" class="width-300">
		</td>
		<th>등록일</th>
		<td class="indent5">
			<input type="text" name="createdFrom" id="createdFrom" class="width-100"> ~ <input type="text" name="createdTo" id="createdTo" class="width-100"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
		</td>
	</tr>
	<tr>
		<th>등록자</th>
		<td class="indent5">
			<input type="text" name="creator" id="creator" data-multi="false" class="width-300">
			<input type="hidden" name="creatorOid" id="creatorOid"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
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
		<th>수정일</th>
		<td class="indent5"><input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100"> ~ <input type="text" name="modifiedTo" id="modifiedTo" class="width-100"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"
			onclick="clearFromTo('createdFrom', 'createdTo')"></td>
	</tr>
	<tr>
		<th>협력업체</th>
		<td class="indent5">
			<select name="manufacture" id="manufacture" class="width-200">
				<option value="">선택</option>
				<option value="INWORK">작업 중</option>
				<option value="UNDERAPPROVAL">승인 중</option>
				<option value="APPROVED">승인됨</option>
				<option value="RETURN">반려됨</option>
			</select>
		</td>
		<th>설명</th>
		<td class="indent5" colspan="3">
			<input type="text" name="description" id="description" class="width-300">
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="추가" title="추가"  onclick="<%=method%>();">    
		</td>
		<td class="right">
			<select name="_psize" id="_psize">
				<option value="30">30</option>
				<option value="50">50</option>
				<option value="100">100</option>
				<option value="200">200</option>
				<option value="300">300</option>
			</select>
			<input type="button" value="검색" title="검색" id="searchBtn" onclick="loadGridData();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 645px; border-top: 1px solid #3180c3;"></div>
<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
<%@include file="/extcore/jsp/common/aui-context.jsp"%>

<script type="text/javascript">
let myGridID;
const columns = [ {
	dataField : "number",
	headerText : "물질번호",
	dataType : "string",
	width : 250,
	filter : {
		showIcon : true,
		inline : true
	},
	renderer : {
		type : "LinkRenderer",
		baseUrl : "javascript",
		jsCallback : function(rowIndex, columnIndex, value, item) {
			const oid = item.oid;
			const url = getCallUrl("/rohs/view?oid=" + oid);
			popup(url, 1600, 800);
		}
	},
}, {
	dataField : "manufactureDisplay",
	headerText : "협력업체",
	dataType : "string",
	width : 250,
	filter : {
		showIcon : true,
		inline : true
	},
}, {
	dataField : "name",
	headerText : "물질명",
	dataType : "string",
	width : 250,
	filter : {
		showIcon : true,
		inline : true
	},
	renderer : {
		type : "LinkRenderer",
		baseUrl : "javascript",
		jsCallback : function(rowIndex, columnIndex, value, item) {
			const oid = item.oid;
			const url = getCallUrl("/rohs/view?oid=" + oid);
			popup(url, 1600, 800);
		}
	},
}, {
	dataField : "version",
	headerText : "Rev.",
	dataType : "string",
	width : 350,
	filter : {
		showIcon : true,
		inline : true
	},
}, {
	dataField : "stateDisplay",
	headerText : "상태",
	dataType : "string",
	width : 100,
	filter : {
		showIcon : true,
		inline : true
	},
}, {
	dataField : "creator",
	headerText : "등록자",
	dataType : "string",
	width : 180,
	filter : {
		showIcon : true,
		inline : true
	},
}, {
	dataField : "createDate",
	headerText : "등록일",
	dataType : "date",
	width : 180,
	filter : {
		showIcon : true,
		inline : true,
	},
}, {
	dataField : "modifyDate",
	headerText : "수정일",
	dataType : "date",
	width : 180,
	filter : {
		showIcon : true,
		inline : true,
	},
} ]

function createAUIGrid(columnLayout) {
	const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : true,
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
	const url = getCallUrl("/rohs/list");
	const field = ["rohsName","rohsNumber","description","state","creatorOid","createdFrom","createdTo","modifiedFrom","modifiedTo","manufacture"];
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

document.addEventListener("DOMContentLoaded", function() {
	const contenxtHeader = genColumnHtml(columns);
	$("#h_item_ul").append(contenxtHeader);
	$("#headerMenu").menu({
		select : headerMenuSelectHandler
	});
	createAUIGrid(columns);
	AUIGrid.resize(myGridID);
	selectbox("state");
	selectbox("manufacture");
	finderUser("creator");
	twindate("created");
	twindate("modified");
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