<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
ArrayList<NumberCode> moldTypeList = (ArrayList<NumberCode>) request.getAttribute("moldTypeList");
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser user = (WTUser) request.getAttribute("sessionUser");
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
String state = (String) request.getAttribute("state");
%>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<input type="hidden" name="location" id="location" value="/Default/금형문서">
<input type="hidden" name="sessionName" id="sessionName" value="<%=user.getFullName()%>">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				금형 검색
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
		<th>금형 번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" class="width-300">
		</td>
		<th>금형명</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-300">
		</td>
		<th>금형타입</th>
		<td class="indent5">
			<select name="moldtype" id="moldtype" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode moldType : moldTypeList) {
				%>
				<option value="<%=moldType.getCode()%>"><%=moldType.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
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
		<th>부서</th>
		<td class="indent5">
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
		<th>MANUFACTURER</th>
		<td class="indent5">
			<select name="manufacture" id="manufacture" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode manufacture : manufactureList) {
				%>
				<option value="<%=manufacture.getCode()%>"><%=manufacture.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>수정일</th>
		<td class="indent5">
			<input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100">
			~
			<input type="text" name="modifiedTo" id="modifiedTo" class="width-100">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('modifiedFrom', 'modifiedTo')">
		</td>
	</tr>
	<tr>
		<th>내부 문서번호</th>
		<td class="indent5">
			<input type="text" name="interalnumber" id="interalnumber" class="width-300">
		</td>
		<th>업체자체금형번호</th>
		<td class="indent5">
			<input type="text" name="moldnumber" id="moldnumber" class="width-300">
		</td>
		<th>REV</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="islastversion" value="true" checked="checked">
				<div class="state p-success">
					<label>
						<b>최신REV</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="islastversion" value="">
				<div class="state p-success">
					<label>
						<b>모든REV</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<th>금형개발비</th>
		<td class="indent5" colspan="5">
			<input type="text" name="moldcost" id="moldcost" class="width-300">
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
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

<div id="grid_wrap" style="height: 380px; border-top: 1px solid #3180c3;"></div>
<%@include file="/extcore/jsp/common/aui-context.jsp"%>
<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
<script type="text/javascript">
	let myGridID;
	function _layout() {
		return [ {
			dataField : "number",
			headerText : "금형번호",
			dataType : "string",
			width : 200,
		}, {
			dataField : "name",
			headerText : "금형명",
			dataType : "string",
			style : "aui-left",
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
			dataType : "string",
			width : 100,
		}, {
			dataField : "modifyDate",
			headerText : "수정일",
			dataType : "string",
			width : 100,
		} ]
	}

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
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			enableRowCheckShiftKey : true
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
		if (movePage === undefined) {
			document.getElementById("sessionid").value = 0;
		}
		let params = new Object();
		const url = getCallUrl("/mold/list");
		const field = [ "number", "name", "createdFrom", "createdTo", "modifiedFrom", "modifiedTo", "creatorOid", "state", "interalnumber", "deptcode", "description", "manufacture", "moldtype", "moldcost", "lifecycle", "location", "searchType", "moldnumber" ];
		params = toField(params, field);
		params.islastversion = $('input[name=islastversion]:checked').val();
		AUIGrid.showAjaxLoader(myGridID);
		logger(params);
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			if (data.result) {
				totalPage = Math.ceil(data.total / data.pageSize);
				createPagingNavigator(data.curPage, data.sessionid);
				AUIGrid.setGridData(myGridID, data.list);
			} else {
				alert(data.msg);
			}
			parent.closeLayer();
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("number");
		const columns = loadColumnLayout("mold-list");
		const contenxtHeader = genColumnHtml(columns);
		$("#h_item_ul").append(contenxtHeader);
		$("#headerMenu").menu({
			select : headerMenuSelectHandler
		});
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
		selectbox("state");
		selectbox("manufacture");
		selectbox("moldtype");
		selectbox("deptcode");
		finderUser("creator");
		twindate("created");
		twindate("modified");
		selectbox("_psize");
		<%if (StringUtil.checkString(state)) {%>
		$("#state").bindSelectSetValue("<%=state%>");
		$("#state").bindSelectDisabled(true);
		<%}%>
	});

	function <%=method%>() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length === 0) {
			alert("추가할 금형을 선택하세요.");
			return false;
		}
		
		opener.<%=method%>(checkedItems, function(res, close, msg) {
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
</script