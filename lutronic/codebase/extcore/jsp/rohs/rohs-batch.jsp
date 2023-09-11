<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.rohs.beans.RohsData"%>
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
		<div id="grid_wrap" style="height: 570px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const lifecycleList = [{"code": "LC_Default", "value": "기본결재"}, {"code": "LC_Default_NonWF", "value": "일괄결재"}];
			const fileDivision = [{"code": "TR", "value": "시험성적서"}, {"code": "DOC", "value": "보증서"}, {"code": "MSDS", "value": "MSDS, 성분분석표"}, {"code": "XRF", "value": "XRF분석 성적서"}];
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
				dataField : "rohsNumber",
				headerText : "물질번호",
				width : 120,
				cellMerge: true,
			}, {
				dataField : "rohsName",
				headerText : "물질명",
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
					for (var i = 0, len = rohsList.length; i < len; i++) {
						if (rohsList[i]["code"] == value) {
							retStr = rohsList[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: rohsList, 
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code", 
					valueField: "value" 
				},
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
			}, {
				dataField : "fileName",
				headerText : "파일명",
				dataType : "string",
				width : 120,
			}]

			function createAUIGrid(columnLayout) {
				const props = {
					editable : true,
					headerHeight : 35,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					fillColumnSizeMode : false,
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
			}
			
			function auiReadyHandler() {
				AUIGrid.addRow(myGridID, {}, "first");
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

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(layout);
				AUIGrid.resize(myGridID);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
			
			// 추가
			function addBtn(){
				var gridList = AUIGrid.getGridData(myGridID);
				var rowList = [];
				for(var i=0; i<3; i++){
					for(var j=0; j<gridList.length; j++){
						rowList[i] = {
								lifecycleName : gridList[j].lifecycleName,
								manufactureName : gridList[j].manufactureName,
								manufacture : gridList[j].manufacture,
								rohsNumber : gridList[j].rohsNumber,
								rohsName : gridList[j].rohsName,
								relatedRohs : gridList[j].relatedRohs
						}
					}
				}
				AUIGrid.addRow(myGridID, rowList, 'selectionDown');
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
					if(isEmpty(gridList[i].rohsNumber)){
						alert("입력된 물질번호가 없습니다.");
						return;
					}
					if(isEmpty(gridList[i].rohsName)){
						alert("선택된 물질명이 없습니다.");
						return;
					}
					if(isEmpty(gridList[i].relatedRohs)){
						alert("입력된 관련물질이 없습니다.");
						return;
					}
					if(isEmpty(gridList[i].lifecycleName)){
						alert("선택된 파일구분이 없습니다.");
						return;
					}
					if(isEmpty(gridList[i].fileType)){
						alert("입력된 파일구분코드가 없습니다.");
						return;
					}
					if(isEmpty(gridList[i].publicationDate)){
						alert("입력된 발행일이 없습니다.");
						return;
					}
					if(isEmpty(gridList[i].fileName)){
						alert("입력된 파일명이 없습니다.");
						return;
					}
				}
				
				if (!confirm("저장하시겠습니까?")){
					return;
				}
				
				let params = new Object();
				params.gridList = gridList;
// 				const url = getCallUrl("/rohs/deleteRootDefinition");
			}
			
			// 삭제
			function deleteBtn(){
				var items = AUIGrid.getCheckedRowItemsAll(myGridID);
				if(items.length==0){
					alert("선택된 물질이 없습니다.");
					return;
				}
				
				if (!confirm("삭제하시겠습니까?")){
					return;
				}
				
// 				let params = new Object();
// 				params.activityList = items;
// 				const url = getCallUrl("/admin/deleteActivityDefinition");
// 				call(url, params, function(data) {
// 					if(data.result){
// 						alert(data.msg);
// 						loadGridData();
// 					}else{
// 						alert(data.msg);
// 					}
// 				});
			}
		</script>
	</form>
</html>