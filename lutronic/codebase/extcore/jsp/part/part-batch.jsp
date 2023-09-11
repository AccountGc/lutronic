<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.common.code.beans.NumberCodeData"%>
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
ArrayList<NumberCodeData> partType1List = (ArrayList<NumberCodeData>) request.getAttribute("partType1List");
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
					<input type="button" value="추가" title="추가" class="blue" onclick=""> 
					<input type="button" value="삭제" title="삭제" class="red" onclick="">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 570px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			let partType2Map = {};
			let partType3Map = {};
			let matList =[];
			<% for(NumberCode mat : matList){ %>
				matList.push({ "code" : "<%= mat.getCode() %>", "value" : "<%= mat.getName().replaceAll("(\r\n|\r|\n|\n\r)", "") %>"});
			<% } %>
			let folderList = [];
			<% for(Folder folder : folderList){ %>
				folderList.push({ "code" : "<%= folder.getLocation() %>", "value" : "<%= folder.getFolderPath() %>"});
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
			let partType1List = [];
			<% for(NumberCodeData partType1 : partType1List){ %>
				partType1List.push({ "code" : "<%= partType1.getOid() %>", "value" : "[<%= partType1.getCode() %>]<%= partType1.getName() %>"});
			<% } %>
			
			const layout = [ {
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
				dataField : "partType1",
				headerText : "품목구분<br>(1자리)",
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
				editRenderer : {
					type: "ComboBoxRenderer",
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					matchFromFirst : false,
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					list: partType1List, 
					keyField: "code", 
					valueField: "value",
					descendants : [ "partType2" ],
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = partType1List.length; i < len; i++) {
							if (partType1List[i]["value"] == newValue) {
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
				labelFunction: function (rowIndex, columnIndex, value, headerText, item) {
					var retStr = "";
					for (var i = 0, len = partType1List.length; i < len; i++) {
						if (partType1List[i]["code"] == value) {
							retStr = partType1List[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "partType2",
				headerText : "대분류<br>(2자리)",
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
				editRenderer : {
					type: "ComboBoxRenderer",
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					matchFromFirst : false,
					showEditorBtnOver: false, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code", 
					valueField: "value",
					descendants : [ "partType3" ],
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						const param = item.partType1;
						const dd = partType2Map[param];
						if (dd === undefined)
							return;
						let isValid = false;
						for (let i = 0, len = dd.length; i < len; i++) {
							if (dd[i]["value"] == newValue) {
								isValid = true;
								break;
							}
						}
						return {
							"validate" : isValid,
							"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
						};
					},
					listFunction : function(rowIndex, columnIndex, item, dataField) {
						const param = item.partType1;
						const dd = partType2Map[param];
						if (dd === undefined) {
							return [];
						}
						let result = [];
						dd.forEach(d => {
							result.push({ "code" : d.oid, "value" : "[" + d.code + "]" + d.name});
						});
						return result;
					},
				},
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
					var retStr = "";
					const param = item.partType1;
					const dd = partType2Map[param];
					if (dd === undefined)
						return value;
					for (let i = 0, len = dd.length; i < len; i++) {
						if (dd[i]["oid"] == value) {
							retStr = "[" + dd[i]["code"] + "]" + dd[i]["name"] ;
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "partType3",
				headerText : "중분류<br>(2자리)",
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
				editRenderer : {
					type: "ComboBoxRenderer",
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					matchFromFirst : false,
					showEditorBtnOver: false, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code", 
					valueField: "value",
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						const param = item.partType2;
						const dd = partType3Map[param];
						if (dd === undefined)
							return;
						let isValid = false;
						for (let i = 0, len = dd.length; i < len; i++) {
							if (dd[i]["value"] == newValue) {
								isValid = true;
								break;
							}
						}
						return {
							"validate" : isValid,
							"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
						};
					},
					listFunction : function(rowIndex, columnIndex, item, dataField) {
						const param = item.partType2;
						const dd = partType3Map[param];
						if (dd === undefined) {
							return [];
						}
						let result = [];
						dd.forEach(d => {
							result.push({ "code" : d.oid, "value" : "[" + d.code + "]" + d.name});
						});
						return result;
					},
				},
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
					var retStr = "";
					const param = item.partType2;
					const dd = partType3Map[param];
					if (dd === undefined)
						return value;
					for (let i = 0, len = dd.length; i < len; i++) {
						if (dd[i]["oid"] == value) {
							retStr = "[" + dd[i]["code"] + "]" + dd[i]["name"] ;
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "seq",
				headerText : "SEQ<br>(3자리)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "etc",
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
					dataField : "matName",
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
								const rowIndex = AUIGrid.getRowCount();
		                        AUIGrid.setCellValue(myGridID, rowIndex, "mat", value);
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
				dataField : "partName4",
				headerText : "품목명<br>(KEY-IN)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "unit",
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
								const rowIndex = AUIGrid.getRowCount();
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
								const rowIndex = AUIGrid.getRowCount();
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
				headerText : "제작방법",
				children : [ {
					dataField : "productmethod",
					headerText : "제작방법 코드",
					width : 120,
				}, {
					dataField : "productmethodName",
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
								const rowIndex = AUIGrid.getRowCount();
								AUIGrid.setCellValue(myGridID, rowIndex, "productmethod", value);
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
				dataField : "specification",
				headerText : "사양",
				dataType : "string",
				width : 120,
			}, {
				dataField : "primary",
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
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
				AUIGrid.bind(myGridID, "keyDown", auiKeyDownHandler);
// 				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
				auiReadyHandler();
				AUIGrid.bind(myGridID, "ready", readyHandler);
			}

			function auiReadyHandler() {
				AUIGrid.addRow(myGridID, {}, "first");
			}
			
			function readyHandler() {
				const item = AUIGrid.getGridData(myGridID);
				for (let i = 0; i < item.length; i++) {
					if (partType2Map.length === undefined) {
						const partType = item[i].partType1;
						const url = getCallUrl("/common/numberCodeList");
					    const params = {
					        codeType: 'PARTTYPE',
					        parentOid: partType
					    };
					    return new Promise((resolve, reject) => {
					        call(url, params, function(dataList) {
					        	partType2Map[partType] = dataList;
					        });
					    });
					}
				}
			}
			
// 			function auiCellEditBegin(event) {
// 				const item = event.item;
// 			}
			
			function auiCellEditEndHandler(event) {
				const dataField = event.dataField;
				const item = event.item;
				const rowIndex = event.rowIndex;
				
				if (dataField === "partType1") {

					const partType = item.partType1;
					const url = getCallUrl("/common/numberCodeList");
				    const params = {
				        codeType: 'PARTTYPE',
				        parentOid: partType
				    };

				    return new Promise((resolve, reject) => {
				        call(url, params, function(dataList) {
				        	partType2Map[partType] = dataList;
				        	const item = {
									partType2 : ""
							}
				        	AUIGrid.updateRow(myGridID, item, rowIndex);
				        });
				    });
				}
				
				if (dataField === "partType2") {

					const partType = item.partType2;
					const url = getCallUrl("/common/numberCodeList");
				    const params = {
				        codeType: 'PARTTYPE',
				        parentOid: partType
				    };

				    return new Promise((resolve, reject) => {
				        call(url, params, function(dataList) {
				        	partType3Map[partType] = dataList;
				        	const item = {
									partType3 : ""
							}
				        	AUIGrid.updateRow(myGridID, item, rowIndex);
				        });
				    });
				}
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
			
			function batch(){
				const rowCount = AUIGrid.getRowCount() + 1;
				
				for(let i = 0; i < rowCount; i++){
					const location = AUIGrid.getCellFormatValue(myGridID, i, "location");
					
					const partType1 = AUIGrid.getCellFormatValue(myGridID, i, "partType1");
					const partType2 = AUIGrid.getCellFormatValue(myGridID, i, "partType2");
					const partType3 = AUIGrid.getCellFormatValue(myGridID, i, "partType3");
					const seq = AUIGrid.getCellFormatValue(myGridID, i, "seq");
					const etc = AUIGrid.getCellFormatValue(myGridID, i, "etc");
					
					const mat = AUIGrid.getCellFormatValue(myGridID, i, "mat");
					
					const partName1 = AUIGrid.getCellFormatValue(myGridID, i, "partName1");
					const partName2 = AUIGrid.getCellFormatValue(myGridID, i, "partName2");
					const partName3 = AUIGrid.getCellFormatValue(myGridID, i, "partName3");
					const partName4 = AUIGrid.getCellFormatValue(myGridID, i, "partName4");
					
					const unit = "EA";
					const deptcode = AUIGrid.getCellFormatValue(myGridID, i, "deptcode");
					const model = AUIGrid.getCellFormatValue(myGridID, i, "model");
					const productmethod = AUIGrid.getCellFormatValue(myGridID, i, "productmethod");
					const specification = AUIGrid.getCellFormatValue(myGridID, i, "specification");
					
					console.log(location);
					console.log(partType1);
					console.log(partType2);
					console.log(partType3);
					console.log(seq);
					console.log(etc);
					console.log(mat);
					console.log(partName1);
					console.log(partName2);
					console.log(partName3);
					console.log(partName4);
					console.log(unit);
					console.log(deptcode);
					console.log(model);
					console.log(productmethod);
					console.log(specification);
					
					if(isEmpty(location)){
						alert(i + "행의 품목에 저장위치를 입력하세요.");
						return;
					}
					
					if(isEmpty(partType1)){
						alert(i + "행의 품목에 품목구분을 입력하세요.");
						return;
					}
					if(isEmpty(partType2)){
						alert(i + "행의 품목에 대분류를 입력하세요.");
						return;
					}
					if(isEmpty(partType3)){
						alert(i + "행의 품목에 중분류를 입력하세요.");
						return;
					}
					
					if(isEmpty(partName1)){
						alert(i + "행의 품목에 품목명(대제목)을 입력하세요.");
						return;
					}
					if(isEmpty(partName2)){
						alert(i + "행의 품목에 품목명(중제목)을 입력하세요.");
						return;
					}
					if(isEmpty(partName3)){
						alert(i + "행의 품목에 품목명(소제목)을 입력하세요.");
						return;
					}
					if(isEmpty(partName4)){
						alert(i + "행의 품목에 품목명(KEY-IN)을 입력하세요.");
						return;
					}
					
					if(isEmpty(unit)){
						alert(i + "행의 품목에 단위를 입력하세요.");
						return;
					}
					if(isEmpty(deptcode)){
						alert(i + "행의 품목에 부서를 입력하세요.");
						return;
					}
					if(isEmpty(model)){
						alert(i + "행의 품목에 프로젝트 코드를 입력하세요.");
						return;
					}
					if(isEmpty(productmethod)){
						alert(i + "행의 품목에 제작방법을 입력하세요.");
						return;
					}
					if(isEmpty(specification)){
						alert(i + "행의 품목에 사양을 입력하세요.");
						return;
					}
					
				}
				
				
				if (!confirm("등록 하시겠습니까?")) {
					return false;
				}
				
// 				call(url, params, function(data) {
// 					alert(data.msg);
// 					if (data.result) {
// 					} else {
// 					}
// 				});
			}
		</script>
	</form>
</html>