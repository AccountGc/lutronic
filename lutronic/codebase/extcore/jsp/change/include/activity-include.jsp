<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.org.service.OrgHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray list = OrgHelper.manager.toJson();
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean multi = Boolean.parseBoolean(request.getParameter("multi"));
boolean view = "view".equals(mode);
boolean update = "update".equals(mode);
boolean create = "create".equals(mode);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				설계변경 활동
			</div>
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">설계변경 활동</th>
		<td class="indent5 <%if (!view) {%>pt5 <%}%>">
			<%
			if (create || update) {
			%>
			<input type="button" value="추가" title="추가" class="blue" onclick="insertRow200();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow200();">
			<%
			}
			%>
			<div id="grid200" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID200;
	const step = [ "STEP1", "STEP2" ];
	const list =
<%=list%>
	const columns200 = [ {
		dataField : "step",
		headerText : "STEP",
		dateType : "string",
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
			list : step,
			matchFromFirst : false,
			autoCompleteMode : true, // 자동완성 모드 설정
			autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = step.length; i < len; i++) {
					if (step[i] == newValue) {
						isValid = true;
						break;
					}
				}
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			}
		},
	}, {
		dataField : "step1",
		headerText : "활동면",
		dateType : "string",
		width : 150
	}, {
		dataField : "ste2p",
		headerText : "활동구분",
		dateType : "string",
		width : 150
	}, {
		dataField : "activeUser",
		headerText : "담당자",
		dateType : "string",
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
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
			let retStr = "";
			for (let i = 0, len = list.length; i < len; i++) {
				if (list[i]["key"] == value) {
					retStr = list[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
		editRenderer : {
			type : "ComboBoxRenderer",
			list : list,
			matchFromFirst : false,
			autoCompleteMode : true, // 자동완성 모드 설정
			autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			keyField : "key",
			valueField : "value",
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = list.length; i < len; i++) {
					if (list[i]["value"] == newValue) {
						isValid = true;
						break;
					}
				}
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			}
		},
	}, {
		dataField : "finishDate",
		headerText : "완료요청일",
		dataType : "date",
		dateInputFormat : "yyyy/mm/dd", // 실제 데이터의 형식 지정
		formatString : "yyyy년 mm월 dd일", // 실제 데이터 형식을 어떻게 표시할지 지정
		width : 160,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : { // icon 값 참조할 테이블 레퍼런스
				"default" : "/Windchill/extcore/component/AUIGrid/images/calendar-icon.png" // default
			},
			onClick : function(event) {
				// 달력 아이콘 클릭하면 실제로 달력을 띄움.
				// 즉, 수정으로 진입함.
				AUIGrid.openInputer(event.pid);
			}
		},
		editRenderer : {
			type : "CalendarRenderer",
			defaultFormat : "yyyy/mm/dd", // 달력 선택 시 데이터에 적용되는 날짜 형식
			showPlaceholder: true, // defaultFormat 설정된 값으로 플래스홀더 표시
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 출력 여부
			onlyCalendar : false, // 사용자 입력 불가, 즉 달력으로만 날짜입력 (기본값 : true)
			showExtraDays : true, // 지난 달, 다음 달 여분의 날짜(days) 출력
			showTodayBtn : true, // 오늘 날짜 선택 버턴 출력
			showUncheckDateBtn : true, // 날짜 선택 해제 버턴 출력
			todayText : "오늘 선택", // 오늘 날짜 버턴 텍스트
			uncheckDateText : "날짜 선택 해제", // 날짜 선택 해제 버턴 텍스트
			uncheckDateValue : "-", // 날짜 선택 해제 버턴 클릭 시 적용될 값.
		}
	}, ]

	function createAUIGrid200(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : true,
			selectionMode : "multipleCells",
			showStateColumn : true,
			showRowCheckColumn : true,
			enableFilter : true,
			autoGridHeight : true,
			editable : true
		}
		myGridID200 = AUIGrid.create("#grid200", columnLayout, props);
		auiReadyHandler();
		AUIGrid.bind(myGridID200, "keyDown", auiKeyDownHandler);
	}

	function auiKeyDownHandler(event) {
		if (event.keyCode == 13) {
			var selectedItems = AUIGrid.getSelectedItems(event.pid);
			var rowIndex = selectedItems[0].rowIndex;
			if (rowIndex === AUIGrid.getRowCount(event.pid) - 1) {
				AUIGrid.addRow(event.pid, {});
				return false;
			}
		}
		return true;
	}

	function auiReadyHandler() {
		AUIGrid.addRow(myGridID200, {}, "first");
	}

	function insertRow200() {

	}

	function deleteRow200() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID200);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID200, rowIndex);
		}
	}
</script>
