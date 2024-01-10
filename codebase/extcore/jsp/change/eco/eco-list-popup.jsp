<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
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
				ECO 검색
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
		<th>ECO 번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" class="width-300">
		</td>
		<th>ECO 제목</th>
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
		<th class="lb">프로젝트 코드</th>
		<td class="indent5">
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
		<th>인허가변경</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="licensing" value="" checked="checked">
				<div class="state p-success">
					<label>
						<b>전체</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="licensing" id="licensing" value="NONE">
				<div class="state p-success">
					<label>
						<b>N/A</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="licensing" value="0">
				<div class="state p-success">
					<label>
						<b>불필요</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="licensing" value="1">
				<div class="state p-success">
					<label>
						<b>필요</b>
					</label>
				</div>
			</div>
		</td>
		<th>위험통제</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="riskType" value="" checked="checked">
				<div class="state p-success">
					<label>
						<b>전체</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="riskType" id="riskType" value="NONE">
				<div class="state p-success">
					<label>
						<b>N/A</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="riskType" value="0">
				<div class="state p-success">
					<label>
						<b>불필요</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="riskType" value="1">
				<div class="state p-success">
					<label>
						<b>필요</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
	<tr>
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

<div id="grid_wrap" style="height: 410px; border-top: 1px solid #3180c3;"></div>
<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
<%@include file="/extcore/jsp/common/aui-context.jsp"%>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "number",
		headerText : "ECO 번호",
		dataType : "string",
		width : 120,
	}, {
		dataField : "name",
		headerText : "ECO 제목",
		dataType : "string",
		style : "aui-left",
	}, {
		dataField : "model",
		headerText : "제품",
		dataType : "string",
		width : 250,
		style : "aui-left"
	}, {
		dataField : "sendType",
		headerText : "ECO 타입",
		dataType : "string",
		width : 80,
	}, {
		dataField : "licensing_name",
		headerText : "인허가변경",
		dataType : "string",
		width : 100,
	}, {
		dataField : "riskType_name",
		headerText : "위험 통제",
		dataType : "string",
		width : 100,
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
		}
		let params = new Object();
		const url = getCallUrl("/eco/list");
		const field = [ "name", "number", "creatorOid", "createdFrom", "createdTo", "approveFrom", "approveTo", "state" ];
		const rows104 = AUIGrid.getGridDataWithState(myGridID104, "gridState");
		params.rows104 = rows104;
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
		twindate("modified");
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
	
</script>