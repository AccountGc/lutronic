<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.folder.Folder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Folder> folderList = (ArrayList<Folder>) request.getAttribute("folderList");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> matList = (ArrayList<NumberCode>) request.getAttribute("matList");
ArrayList<NumberCode> productmethodList = (ArrayList<NumberCode>) request.getAttribute("productmethodList");
ArrayList<NumberCode> partName1List = (ArrayList<NumberCode>) request.getAttribute("partName1List");
ArrayList<NumberCode> partName2List = (ArrayList<NumberCode>) request.getAttribute("partName2List");
ArrayList<NumberCode> partName3List = (ArrayList<NumberCode>) request.getAttribute("partName3List");
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
				<td><input type="button" value="저장" title="저장" onclick="addBtn();"> <input type="button" value="추가" title="추가" class="blue" onclick="addBtn();"> <input type="button" value="삭제" title="삭제" class="red" onclick="javascript:self.close();"></td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 570px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			let matList =[];
			<% for(NumberCode mat : matList){ %>
				matList.push({ "code" : "<%= mat.getCode() %>", "value" : "<%= mat.getName().replaceAll("(\r\n|\r|\n|\n\r)", "") %>"});
			<% } %>
			let folderList = [];
			<% for(Folder folder : folderList){ %>
				folderList.push({ "code" : "<%= folder.getLocation() %>", "value" : "<%= folder.getLocation() %>/<%= folder.getName() %>"});
			<% } %>
			let deptcodeList = [];
			<% for(NumberCode deptcode : deptcodeList){ %>
				deptcodeList.push({ "code" : "<%= deptcode.getCode() %>", "value" : "<%= deptcode.getName() %>"});
			<% } %>
			let productmethodList = [];
			<% for(NumberCode productmethod : productmethodList){ %>
				productmethodList.push({ "code" : "<%= productmethod.getCode() %>", "value" : "<%= productmethod.getName() %>"});
			<% } %>
			let modelList = [];
			<% for(NumberCode model : modelList){ %>
				modelList.push({ "code" : "<%= model.getCode() %>", "value" : "<%= model.getName() %>"});
			<% } %>
			let partName1List = [];
			<% for(NumberCode partName1 : partName1List){ %>
				partName1List.push({ "code" : "<%= partName1.getCode() %>", "value" : "<%= partName1.getName() %>"});
			<% } %>
			let partName2List = [];
			<% for(NumberCode partName2 : partName2List){ %>
				partName2List.push({ "code" : "<%= partName2.getCode() %>", "value" : "<%= partName2.getName() %>"});
			<% } %>
			let partName3List = [];
			<% for(NumberCode partName3 : partName3List){ %>
				partName3List.push({ "code" : "<%= partName3.getCode() %>", "value" : "<%= partName3.getName() %>"});
			<% } %>
			
			folderList.forEach(folder => {
				console.log(folder);
			})
			
			const layout = [ {
				dataField : "location",
				headerText : "저장위치",
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
				dataField : "g",
				headerText : "대분류<br>(2자리)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "중분류<br>(2자리)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "SEQ<br>(3자리)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "CUSTOM<br>(2자리)",
				dataType : "string",
				width : 120,
			}, {
				headerText : "MAT",
				children : [ {
					dataField : "mat",
					headerText : "재질코드",
					width : 120,
				}, {
					dataField : "mat",
					headerText : "재질명",
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
						for (var i = 0, len = matList.length; i < len; i++) {
							if (matList[i]["code"] == value) {
								retStr = matList[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type: "ComboBoxRenderer",
						list: matList, 
						autoCompleteMode: true, // 자동완성 모드 설정
						autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
						keyField: "code", 
						valueField: "value" 
					},
				} ]
			}, {
				dataField : "partName1",
				headerText : "품목명<br>(대제목)",
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
					for (var i = 0, len = partName1List.length; i < len; i++) {
						if (partName1List[i]["code"] == value) {
							retStr = partName1List[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: partName1List, 
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code",
					valueField: "value"
				},
			}, {
				dataField : "partName2",
				headerText : "품목명<br>(중제목)",
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
					for (var i = 0, len = partName2List.length; i < len; i++) {
						if (partName2List[i]["code"] == value) {
							retStr = partName2List[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: partName2List, 
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code",
					valueField: "value"
				},
			}, {
				dataField : "partName3",
				headerText : "품목명<br>(소제목)",
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
					for (var i = 0, len = partName3List.length; i < len; i++) {
						if (partName3List[i]["code"] == value) {
							retStr = partName3List[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: partName3List, 
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code",
					valueField: "value"
				},
			}, {
				dataField : "number",
				headerText : "품목명<br>(KEY-IN)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "단위",
				dataType : "string",
				width : 120,
			}, {
				headerText : "부서",
				children : [ {
					dataField : "deptcode",
					headerText : "부서코드",
					width : 120,
				}, {
					dataField : "deptcode",
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
				headerText : "프로젝트 코드",
				children : [ {
					dataField : "model",
					headerText : "프로젝트 코드",
					width : 120,
				}, {
					dataField : "model",
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
				headerText : "제작방법",
				children : [ {
					dataField : "productmethod",
					headerText : "제작방법 코드",
					width : 120,
				}, {
					dataField : "productmethod",
					headerText : "제작방법",
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
						for (var i = 0, len = productmethodList.length; i < len; i++) {
							if (productmethodList[i]["code"] == value) {
								retStr = productmethodList[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type: "ComboBoxRenderer",
						list: productmethodList, 
						autoCompleteMode: true, // 자동완성 모드 설정
						autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
						keyField: "code",
						valueField: "value"
					},
				} ]
			}, {
				dataField : "number",
				headerText : "사양",
				dataType : "string",
				width : 120,
			}, {
				dataField : "number",
				headerText : "주도면",
				dataType : "string",
				width : 120,
			}, ]

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
			
		</script>
	</form>
</html>