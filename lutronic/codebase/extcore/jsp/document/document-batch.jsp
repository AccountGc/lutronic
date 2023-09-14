<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.folder.Folder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Folder> folderList = (ArrayList<Folder>) request.getAttribute("folderList");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> documentNameList = (ArrayList<NumberCode>) request.getAttribute("documentNameList");
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
					<input type="button" value="저장" title="저장" onclick="batch();">
					<input type="button" value="추가" title="추가" class="blue" onclick="addBtn();">
					<input type="button" value="삭제" title="삭제" class="red" onclick="deleteBtn();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 570px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const lifecycleList = [{"code": "LC_Default", "value": "기본결재"}, {"code": "LC_Default_NonWF", "value": "일괄결재"}];
			const preserationList = [{"code": "PR001", "value": "영구"}, {"code": "PR002", "value": "5년"}, {"code": "PR003", "value": "7년"}];
			const documentTypeList = [{"code": "$$NDDocument", "value": "일반문서"}, 
				{"code": "$$RDDocument", "value": "개발문서"}, 
				{"code": "$$RDDocument", "value": "승인원"}, 
				{"code": "$$RADocument", "value": "인증문서"}, 
				{"code": "$$DSDocument", "value": "개발소스"},
				{"code": "$$DDDocument", "value": "배포자료"},
				{"code": "$$ETDocument", "value": "기타문서"}];
			let folderList = [];
			<% for(Folder folder : folderList){ %>
				folderList.push({ "code" : "<%= folder.getFolderPath()  %>", "value" : "<%= folder.getFolderPath() %>"});
			<% } %>
			let documentNameList = [];
			<% for(NumberCode documentName : documentNameList){ %>
				documentNameList.push({ "code" : "<%= documentName.getCode() %>", "value" : "<%= documentName.getName() %>"});
			<% } %>
			let deptcodeList = [];
			<% for(NumberCode deptcode : deptcodeList){ %>
				deptcodeList.push({ "code" : "<%= deptcode.getCode() %>", "value" : "<%= deptcode.getName() %>"});
			<% } %>
			let modelList = [];
			<% for(NumberCode model : modelList){ %>
				modelList.push({ "code" : "<%= model.getCode() %>", "value" : "<%= model.getName() %>"});
			<% } %>
			const layout = [{
				dataField : "location",
				headerText : "저장위치",
				dataType : "string",
				width : 300,
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
					for (var i = 0, len = folderList.length; i < len; i++) {
						if (folderList[i]["code"] == value) {
							retStr = folderList[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: folderList, 
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code", 
					valueField: "value" 
				},
			}, {
				dataField : "documentName",
				headerText : "문서 종류",
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
					for (var i = 0, len = documentNameList.length; i < len; i++) {
						if (documentNameList[i]["code"] == value) {
							AUIGrid.setCellValue(myGridID, rowIndex, "documentName", value);
							retStr = documentNameList[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: documentNameList, 
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code",
					valueField: "value"
				},
			}, {
				dataField : "docName",
				headerText : "문서명",
				width : 120,
				dataType : "string",
				width : 120,
				cellMerge: true,
			}, {
				dataField : "lifecycle",
				headerText : "결재 방식",
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
                            AUIGrid.setCellValue(myGridID, rowIndex, "manufacture", value);
							retStr = lifecycleList[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: lifecycleList, 
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code", 
					valueField: "value" 
				},
			}, {
				dataField : "documentType",
				headerText : "문서분류",
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
					for (var i = 0, len = documentTypeList.length; i < len; i++) {
						if (documentTypeList[i]["code"] == value) {
                            AUIGrid.setCellValue(myGridID, rowIndex, "documentType", value);
							retStr = documentTypeList[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: documentTypeList, 
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code", 
					valueField: "value" 
				},
			}, {
				headerText : "프로젝트 코드",
				children : [ {
					dataField : "model",
					headerText : "프로젝트 코드",
					width : 120,
				}, {
					dataField : "modelName",
					headerText : "프로젝트 명",
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
						for (var i = 0, len = modelList.length; i < len; i++) {
							if (modelList[i]["code"] == value) {
								AUIGrid.setCellValue(myGridID, rowIndex, "model", value);
								retStr = modelList[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type: "ComboBoxRenderer",
						list: modelList, 
						autoCompleteMode: true, // 자동완성 모드 설정
						autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
						keyField: "code",
						valueField: "value"
					},
				} ]
			}, {
				dataField : "writer",
				headerText : "작성자",
				dataType : "string",
				width : 120,
				cellMerge: true,
			}, {
				headerText : "부서",
				children : [ {
					dataField : "deptcode",
					headerText : "부서코드",
					width : 120,
				}, {
					dataField : "deptcodeName",
					headerText : "부서명",
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
						for (var i = 0, len = deptcodeList.length; i < len; i++) {
							if (deptcodeList[i]["code"] == value) {
								AUIGrid.setCellValue(myGridID, rowIndex, "deptcode", value);
								retStr = deptcodeList[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type: "ComboBoxRenderer",
						list: deptcodeList, 
						autoCompleteMode: true, // 자동완성 모드 설정
						autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
						keyField: "code",
						valueField: "value"
					},
				} ]
			}, {
				dataField : "interalnumber",
				headerText : "내부 문서번호",
				width : 120,
				cellMerge: true,
			}, {
				headerText : "보존기간",
				children : [ {
					dataField : "preseration",
					headerText : "보존기간코드",
					width : 120,
				}, {
					dataField : "preserationList",
					headerText : "보존기간",
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
						for (var i = 0, len = preserationList.length; i < len; i++) {
							if (preserationList[i]["code"] == value) {
								AUIGrid.setCellValue(myGridID, rowIndex, "preseration", value);
								retStr = preserationList[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type: "ComboBoxRenderer",
						list: preserationList, 
						autoCompleteMode: true, // 자동완성 모드 설정
						autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
						keyField: "code",
						valueField: "value"
					},
				} ]
			}, {
				dataField : "division",
				headerText : "분류체계",
				width : 120,
				cellMerge: true,
			}, {
				headerText : "첨부파일",
				children : [ {
					dataField : "secondaryName",
					headerText : "파일명",
					width: 160,
					styleFunction: function (rowIndex, columnIndex, value, headerText, item, dataField) {
						if (typeof value == "undefined" || value == "") {
							return null;
						}
						return "my-file-selected";
					},
					labelFunction: function (rowIndex, columnIndex, value, headerText, item) {
						if (typeof value == "undefined" || value == "") {
							return "선택 파일 없음";
						}
						return value;
					},
				}, {
					dataField : "secondary",
					headerText : "파일",
					width: 160,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item.id;
							const url = getCallUrl("/common/attachSecondary?oid=" + oid + "&method=attach");
							_popup(url, 800, 400,"n");
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}]
			}, {
				headerText : "관련품목",
				children : [ {
					dataField : "partOids",
					dataType : "string",
					visible : false
				},{
					dataField : "partNumber",
					headerText : "품목번호",
					width: 160,
				}, {
					dataField : "part",
					headerText : "관련품목",
					dataType : "string",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "부품추가",
						onClick : function(event) {
							const parentRowIndex = event.rowIndex;
							const url = getCallUrl("/part/list?popup=true&parentRowIndex=" + parentRowIndex);
							_popup(url, 1800, 900, "n");
						}
					}
				}]
			}]

			function createAUIGrid(columnLayout) {
				const props = {
					rowIdField: "id",
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
				AUIGrid.addRow(myGridID, {}, 'last');
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
				
				AUIGrid.removeCheckedRows(myGridID);
			}
			
			// 품목 번호 할당 메서드
			function setPartNumber(partOids, partNumber, parentRowIndex){
				AUIGrid.setCellValue(myGridID, parentRowIndex, "partOids", partOids);
				AUIGrid.setCellValue(myGridID, parentRowIndex, "partNumber", partNumber);
			}
						
			function attach(data) {
				
				for(let i = 0; i < data.length; i++){
					AUIGrid.updateRowsById(myGridID, {
						id : recentGridItem.id,
						secondary : data[0].cacheId,
					});
					
					AUIGrid.updateRowsById(myGridID, {
						id: recentGridItem.id,
						secondaryName: data[0].name
					});
				}
			}
						
			
			function batch(){
				const documentList = AUIGrid.getGridData(myGridID);
				
				
				for(let i = 0; i < documentList.length; i++){
					
					const rowNum = i + 1;
					
// 					if(isEmpty(documentList[i].location)){
// 						alert(rowNum + "행의 문서에 저장위치를 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].docName)){
// 						alert(rowNum + "행의 문서에 문서명을 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].documentName)){
// 						alert(rowNum + "행의 문서에 문서종류를 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].lifecycle)){
// 						alert(rowNum + "행의 문서에 결재방식을 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].documentType)){
// 						alert(rowNum + "행의 문서에 문서분류를 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].model)){
// 						alert(rowNum + "행의 문서에 프로젝트 코드를 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].writer)){
// 						alert(rowNum + "행의 문서에 작성자를 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].deptcode)){
// 						alert(rowNum + "행의 문서에 부서를 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].interalnumber)){
// 						alert(rowNum + "행의 문서에 내부 문서번호를 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].preseration)){
// 						alert(rowNum + "행의 문서에 보존기간을 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].division)){
// 						alert(rowNum + "행의 문서에 분류체계를 입력하세요.");
// 						return;
// 					}
// 					if(isEmpty(documentList[i].partOids)){
// 						alert(rowNum + "행의 문서에 관련품목를 입력하세요.");
// 						return;
// 					}
				}
				
				
				if (!confirm("등록 하시겠습니까?")) {
					return false;
				}
				
				
				const url = getCallUrl("/doc/batch");
				let params = new Object();
				params.documentList = documentList;
				
				console.log(params.documentList);
				
				call(url, params, function(data) {
					alert(data.msg);
				});
			}
		</script>
	</form>
</html>