<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.rohs.dto.RohsData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
	ArrayList<RohsData> rohsList = (ArrayList<RohsData>) request.getAttribute("rohsList");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td>
					<input type="button" value="저장" title="저장" onclick="saveBtn();"> 
					<input type="button" value="추가" title="추가" class="blue" onclick="addBtn();"> 
					<input type="button" value="삭제" title="삭제" class="red" onclick="deleteBtn();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 370px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const lifecycleList = [{"code": "LC_Default", "value": "기본결재"}, {"code": "LC_Default_NonWF", "value": "일괄결재"}];
			const fileDivision = [{"code": "TR", "value": "시험성적서"}, {"code": "DOC", "value": "보증서"}, {"code": "MSDS", "value": "MSDS 성분분석표"}, {"code": "XRF", "value": "XRF분석 성적서"}];
			let manufactureList = [];
			<% for(NumberCode manufacture : manufactureList){ %>
				manufactureList.push({ "code" : "<%= manufacture.getCode() %>", "value" : "<%= manufacture.getName() %>"});
			<% } %>
			let rohsList = [];
			<% for(RohsData rohs : rohsList){ %>
				rohsList.push({ "code" : "<%= rohs.getNumber() %>", "value" : "<%= rohs.getName() %>"});
			<% } %>
			const layout = [{
				dataField : "lifecycleName",
				headerText : "결재방식",
				dataType : "string",
				width : 120,
				cellMerge: true,
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
				labelFunction: function (rowIndex, columnIndex, value, headerText, item) {
					var retStr = "";
					for (var i = 0, len = lifecycleList.length; i < len; i++) {
						if (lifecycleList[i]["code"] == value) {
							retStr = lifecycleList[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					list: lifecycleList, 
					keyField: "code", 
					valueField: "value" 
				},
			}, {
				dataField : "manufactureName",
				headerText : "협력업체",
				dataType : "string",
				width : 120,
				cellMerge: true,
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
				labelFunction: function (rowIndex, columnIndex, value, headerText, item) {
					var retStr = "";
					for (var i = 0, len = manufactureList.length; i < len; i++) {
						if (manufactureList[i]["code"] == value) {
                            AUIGrid.setCellValue(myGridID, rowIndex, "manufacture", value);
							retStr = manufactureList[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: manufactureList, 
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code", 
					valueField: "value" 
				},
			}, {
				dataField : "manufacture",
				headerText : "협력업체코드",
				dataType : "string",
				width : 120,
				cellMerge: true,
			}, {
				dataField : "rohsName",
				headerText : "물질명",
				width : 120,
				cellMerge: true,
			}, {
				dataField : "relatedRohs",
				headerText : "관련물질",
				dataType : "string",
				width : 120,
				cellMerge: true,
			}, {
				dataField : "fileTypeName",
				headerText : "파일구분",
				dataType : "string",
				width : 120,
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
				labelFunction: function (rowIndex, columnIndex, value, headerText, item) {
					var retStr = "";
					for (var i = 0, len = fileDivision.length; i < len; i++) {
						if (fileDivision[i]["code"] == value) {
							AUIGrid.setCellValue(myGridID, rowIndex, "fileType", value);
							retStr = fileDivision[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: fileDivision, 
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code", 
					valueField: "value" 
				},
			}, {
				dataField : "fileType",
				headerText : "파일구분코드",
				dataType : "string",
				width : 120,
			}, {
				dataField : "publicationDate",
				headerText : "발행일",
				dataType : "string",
				width : 120,
				dateInputFormat: "yyyy-mm-dd", // 실제 데이터의 형식 지정
				formatString: "yyyy-mm-dd", // 실제 데이터 형식을 어떻게 표시할지 지정
				width: 160,
				editRenderer: {
					type: "CalendarRenderer",
					showExtraDays: false, // 지난 달, 다음 달 여분의 날짜(days) 출력 안함
					onlyCalendar: false, // 사용자 입력 불가, 즉 달력으로만 날짜입력 (기본값 : true)
					defaultFormat: "yyyy-mm-dd", // 달력 선택 시 데이터에 적용되는 날짜 형식
					showPlaceholder: true, // defaultFormat 설정된 값으로 플래스홀더 표시
					validator: function (oldValue, newValue, item) { // 에디팅 유효성 검사
						var m, d;
						var isValid = true;
						m = parseInt(newValue.substring(5, 7));
						d = parseInt(newValue.substring(8, 10));
						if (isNaN(m) || isNaN(d) || m > 12 || d > 31) { // 월은 12월, 일은 31일을 넘지 않게.
							isValid = false;
						} else if (isNaN(Date.parse(newValue))) { // JS Date 로 파싱할 수 있는 형식인지 조사
							isValid = false;
						} else {
							isValid = true;
						}
						// 리턴값은 Object 이며 validate 의 값이 true 라면 패스, false 라면 message 를 띄움
						return { "validate": isValid, "message": "유효한 날짜 형식으로 입력해주세요." };
					}
				}
			}, {
				dataField : "fileName",
				headerText : "파일명",
				dataType : "string",
				width : 120,
				editable : false
			}, {
				dataField : "secondary",
				dataType : "string",
				width : 120,
				visible : false
			}]

			function createAUIGrid(columnLayout) {
				const props = {
					editable : true,
					headerHeight : 35,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : false,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					fillColumnSizeMode: true,
					enableCellMerge: true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.bind(myGridID, "keyDown", auiKeyDownHandler);
				auiReadyHandler();
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
// 				AUIGrid.bind(myGridID, "removeRow", auiRemoveRowHandler);
			}
			
			function auiReadyHandler() {
				var rowList = [];
				for(var i=0; i<4; i++){
					rowList[i] = {}
				}
				AUIGrid.addRow(myGridID, rowList, "last");
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
			
			function auiCellClickHandler(event){
				if(event.dataField=='fileName'){
					const url = getCallUrl("/rohs/attachFile"+"?row="+event.rowIndex+"&method=addFile");
					_popup(url, 600, 500, "n");
				}
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(layout);
				AUIGrid.resize(myGridID);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
			
			// 파일 추가
			function addFile(data){
				var fileName = "";
				const arr = new Array();
				for (let i = 0; i < data.length; i++) {
					if(i==0){
						fileName = data[i].name;
					}else{
						fileName += "/"+data[i].name;
					}
					arr.push(data[i].cacheId+"/"+data[i].saveName);
				}
				AUIGrid.setCellValue(myGridID, data.row, "secondary", arr);
				AUIGrid.setCellValue(myGridID, data.row, "fileName", fileName);
			}
			
			// 파일명 입력됨
			function fn_fileName(fileName){
				var gridList = AUIGrid.getGridData(myGridID);
				for(var i=0; i<gridList.length; i++){
					AUIGrid.setCellValue(myGridID, i, "fileName", fileName);
				}
			}
			
			// 추가
			function addBtn(){
				var rowList = [];
				for(var i=0; i<4; i++){
					rowList[i] = {}
				}
				AUIGrid.addRow(myGridID, rowList, 'last');
			}
			
			// 저장
			function saveBtn(){
				var gridList = AUIGrid.getGridData(myGridID);
				for(var i=0; i<gridList.length; i++){
					if(isEmpty(gridList[i].lifecycleName)){
						alert("선택된 결재방식이 없습니다.");
						return;
					}
					if(isEmpty(gridList[i].manufactureName)){
						alert("선택된 협력업체가 없습니다.");
						return;
					}
					if(isEmpty(gridList[i].manufacture)){
						alert("입력된 협력업체코드가 없습니다.");
						return;
					}
					if(isEmpty(gridList[i].rohsName)){
						alert("선택된 물질명이 없습니다.");
						return;
					}
// 					if(isEmpty(gridList[i].relatedRohs)){
// 						alert("입력된 관련물질이 없습니다.");
// 						return;
// 					}
// 					if(isEmpty(gridList[i].fileTypeName)){
// 						alert("선택된 파일구분이 없습니다.");
// 						return;
// 					}
// 					if(isEmpty(gridList[i].fileType)){
// 						alert("입력된 파일구분코드가 없습니다.");
// 						return;
// 					}
// 					if(isEmpty(gridList[i].publicationDate)){
// 						alert("입력된 발행일이 없습니다.");
// 						return;
// 					}
// 					if(isEmpty(gridList[i].fileName)){
// 						alert("입력된 파일명이 없습니다.");
// 						return;
// 					}
				}
				// 물질명 중복체크
				nameCheck(gridList);
			}
			
			function nameCheck(list){
				var arr = new Array();
				for(var i=0; i<list.length; i++){
					arr.push(list[i].rohsName);
				}
				var params = new Object();
				params.list = arr;
				var url = getCallUrl("/rohs/rohsNameCheck");
				call(url, params, function(data) {
					if(data.result){
						if(data.duplicate!=""){
							alert("' "+data.duplicate+" '"+"은/는 이미 등록된 물질명 입니다.");
						}else{
							// 저장 처리
							save(list);
						}
					}else{
						alert(data.msg);
					}
				});
			}
			
			function save(list){
				if (!confirm("저장하시겠습니까?")){
					return;
				}
				let params = new Object();
				params.gridList = list;
				
				const url = getCallUrl("/rohs/batch");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						location.reload();
					}else{
						alert(data.msg);
					}
				});
			}
			
			// 삭제
			function deleteBtn(){
				AUIGrid.removeCheckedRows(myGridID);
			}
		</script>
	</form>
</html>