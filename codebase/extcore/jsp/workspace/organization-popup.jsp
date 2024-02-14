<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%
String oid = (String) request.getAttribute("oid");
String multi = (String) request.getAttribute("multi");
String openerId = (String) request.getAttribute("openerId");
boolean isMulti = Boolean.parseBoolean(multi);
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
				조직도
			</div>
		</td>
	</tr>
</table>
<table class="search-table">
	<colgroup>
		<col width="174">
		<col width="*">
		<col width="174">
		<col width="*">
	</colgroup>
	<tr>
		<th>부서 및 사원 관리</th>
		<td class="indent5">
			<input type="hidden" name="oid" id="oid" value="<%=oid%>">
			<span id="locationName">루트로닉</span>
		</td>
		<th>퇴사여부</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="isFire" value="false" checked="checked">
				<div class="state p-success">
					<label>
						<b>재직</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="isFire" value="true">
				<div class="state p-success">
					<label>
						<b>퇴사</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<th>이름</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-300">
		</td>
		<th>아이디</th>
		<td class="indent5">
			<input type="text" name="userId" id="userId" class="width-300">
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
			<input type="button" value="추가" title="추가" class="red" onclick="selected();">
			<input type="button" value="검색" title="검색" onclick="loadGridData();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
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
			<jsp:include page="/extcore/jsp/workprocess/department-tree.jsp">
				<jsp:param value="428" name="height" />
			</jsp:include>
		</td>
		<td valign="top">&nbsp;</td>
		<td valign="top">
			<div id="grid_wrap" style="height: 395px; border-top: 1px solid #3180c3;"></div>
			<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
			<%@include file="/extcore/jsp/common/aui-context.jsp"%>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID;
	const list = [];
	function _layout() {
		return [ {
			dataField : "rowNum",
			headerText : "번호",
			width : 40,
			dataType : "numeric",
		},{
			dataField : "id",
			headerText : "아이디",
			dataType : "string",
			width : 120,
		}, {
			dataField : "name",
			headerText : "이름",
			dataType : "string",
			width : 120,
		}, {
			dataField : "auth",
			headerText : "메뉴권한",
			dataType : "string",
			style : "aui-left",
		}, {
			dataField : "department_name",
			headerText : "부서",
			dataType : "string",
			width : 150,
			renderer : {
				type : "IconRenderer",
				iconWidth : 16,
				iconHeight : 16,
				iconPosition : "aisleRight",
				iconTableRef : {
					"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
				},
				onClick : function(event) {
					AUIGrid.openInputer(event.pid);
				}
			},
			editRenderer : {
				type : "ComboBoxRenderer",
				list : list,
				matchFromFirst : false,
				autoCompleteMode : true, // 자동완성 모드 설정
				autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
				showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
				keyField : "oid",
				valueField : "name",
				validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
					let isValid = false;
					if (!fromClipboard) {
						for (let i = 0, len = list.length; i < len; i++) {
							if (list[i]["name"] == newValue) {
								isValid = true;
								break;
							}
						}
					} else {
						for (let i = 0, len = list.length; i < len; i++) {
							if (list[i]["oid"] == newValue) {
								isValid = true;
								break;
							}
						}
					}
					return {
						"validate" : isValid,
						"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
					};
				}
			},
			labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
				let retStr = "";
				for (let i = 0, len = list.length; i < len; i++) {
					if (list[i]["oid"] == value) {
						retStr = list[i]["name"];
						break;
					}
				}
				return retStr == "" ? value : retStr;
			},
		}, {
			dataField : "duty",
			headerText : "직위",
			dataType : "string",
			width : 130,
		}, {
			dataField : "email",
			headerText : "이메일",
			dataType : "string",
			width : 250,
			style : "aui-left",
		} ]
	}

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : false,
// 			rowNumHeaderText : "번호",
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
			showRowCheckColumn : true,
			<%if (!isMulti) {%>
			rowCheckToRadio : true
			<%}%>
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
	
	let sortCachelet sortCache = [];
	function auiSortingHandler(event) {
		const sortingFields = event.sortingFields;
		const key = sortingFields[0].dataField;
		const sortType = sortingFields[0].sortType; // 오름차순 1 내림 -1
		sortCache[0] = {
			dataField : key,
			sortType : sortType
		};
		document.getElementById("sortKey").value = key;
		document.getElementById("sortType").value = sortType;
	}

	
	function auiCellClick(event) {
		const item = event.item;
		const rowIdField = AUIGrid.getProp(event.pid, "rowIdField"); // rowIdField 얻기
		const rowId = item[rowIdField];
		
		<%if (!isMulti) {%>
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
		const url = getCallUrl("/org/organization");
		const field = [ "sortType", "sortKey", "name", "userId", "oid" ];
		params = toField(params, field);
		const isFire = document.querySelector("input[name=isFire]:checked").value;
		params.isFire = JSON.parse(isFire);
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			logger(data);
			AUIGrid.removeAjaxLoader(myGridID);
			if (data.result) {
				// 페이징처리..
				totalPage = Math.ceil(data.total / data.pageSize);
				createPagingNavigator(data.total, data.curPage, data.sessionid);
				AUIGrid.setGridData(myGridID, data.list);
				if (sortCache.length > 0) {
					AUIGrid.setSorting(myGridID, sortCache);
				}
			} else {
				alert(data.msg);
			}
			parent.closeLayer();
		});
	}
	
	function selected() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if(checkedItems.length ==0) {
			alert("추가할 사용자를 선택하세요.");
			return false;
		}
		const item = checkedItems[0];
		inputUser("<%=openerId%>", item);
	}
	

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("name");
		const columns = loadColumnLayout("organization-list");
		const contenxtHeader = genColumnHtml(columns);
		$("#h_item_ul").append(contenxtHeader);
		$("#headerMenu").menu({
			select : headerMenuSelectHandler
		});
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
		selectbox("_psize");
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
