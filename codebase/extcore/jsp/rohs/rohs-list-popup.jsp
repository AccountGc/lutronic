<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
String state = (String) request.getAttribute("state");
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
			<input type="text" name="createdFrom" id="createdFrom" class="width-100">
			~
			<input type="text" name="createdTo" id="createdTo" class="width-100">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
		</td>
	</tr>
	<tr>
		<th>등록자</th>
		<td class="indent5">
			<input type="text" name="creator" id="creator" data-multi="false" class="width-300">
			<input type="hidden" name="creatorOid" id="creatorOid">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
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
		<th>수정일</th>
		<td class="indent5">
			<input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100">
			~
			<input type="text" name="modifiedTo" id="modifiedTo" class="width-100">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
		</td>
	</tr>
	<tr>
		<th>협력업체</th>
		<td class="indent5">
			<input type="text" name="manufacture" id="manufacture" class="width-200">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('manufacture')">
		</td>
		<th>설명</th>
		<td class="indent5" colspan="3">
			<input type="text" name="description" id="description" class="width-300">
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="right">
			<select name="_psize" id="_psize">
				<option value="10">10</option>
				<option value="30">30</option>
				<option value="50">50</option>
				<option value="100">100</option>
			</select>
			<input type="button" value="검색" title="검색" onclick="loadGridData();">
			<input type="button" value="추가" title="추가" class="blue" onclick="<%=method%>();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
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
		width : 200,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/rohs/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "manufactureDisplay",
		headerText : "협력업체",
		dataType : "string",
		width : 150,
	}, {
		dataField : "name",
		headerText : "물질명",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/rohs/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
	}, {
		dataField : "stateDisplay",
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
		width : 100,
	}, {
		dataField : "createDate",
		headerText : "등록일",
		dataType : "date",
		width : 100,
	}, {
		dataField : "modifyDate",
		headerText : "수정일",
		dataType : "date",
		width : 100,
	} ]
	
	function createAUIGrid(columnLayout) {
		const props = {
				headerHeight : 30,
				showRowNumColumn : true,
				showRowCheckColumn : true,
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
		
		if (AUIGrid.isCheckedRowById(event.pid, item._$uid)) {
			AUIGrid.addUncheckedRowsByIds(event.pid,item._$uid);
		} else {
			AUIGrid.addCheckedRowsByIds(event.pid, item._$uid);
		}
	}
	
	function loadGridData(movePage) {
		if(movePage === undefined) {
			document.getElementById("sessionid").value = 0;
			document.getElementById("curPage").value = 1;
		}
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
		finderCode("manufacture", "MANUFACTURE");
		finderUser("creator");
		twindate("created");
		twindate("modified");
		selectbox("_psize");
		<%if (StringUtil.checkString(state)) {%>
		$("#state").bindSelectSetValue("<%=state%>");
		$("#state").bindSelectDisabled(true);
		<%}%>
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

		opener.<%=method%>(checkedItems, function(res, close, msg) {
			trigger(close, msg);
		})
	}
</script>