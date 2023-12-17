<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.common.code.dto.NumberCodeDTO"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.part.QuantityUnit"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray folderList = (JSONArray) request.getAttribute("folderList");
JSONArray modelList = (JSONArray) request.getAttribute("modelList");
JSONArray deptcodeList = (JSONArray) request.getAttribute("deptcodeList");
JSONArray matList = (JSONArray) request.getAttribute("matList");
JSONArray productmethodList = (JSONArray) request.getAttribute("productmethodList");
JSONArray partType1List = (JSONArray) request.getAttribute("partType1List");
JSONArray unitList = (JSONArray) request.getAttribute("unitList");
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
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						품목 일괄 등록
					</div>
				</td>
				<td class="right">
					<input type="button" value="저장" title="저장" onclick="batch();">
					<input type="button" value="추가" title="추가" class="blue" onclick="addBtn();">
					<input type="button" value="삭제" title="삭제" class="red" onclick="deleteBtn();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 785px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			let partType1List = <%=partType1List%>
			let partType2Map = {};
			let partType3Map = {};
			let recentGridItem = null;
			let matList = <%=matList%>;
			let folderList = <%=folderList%>;
			let deptcodeList = <%=deptcodeList%>
			let productmethodList = <%=productmethodList%>
			let modelList = <%=modelList%>
			let unitList = <%=unitList%>
			const columns = [{ 
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
				editRenderer : {
					type: "ComboBoxRenderer",
					list: folderList, 
					keyField : "oid",
					valueField : "name",
					matchFromFirst : false,
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver : false,
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						// 공백 선책가능하게
						if(newValue == "") {
							isValid = true;
						}
						
						if(!fromClipboard) {
							for (let i = 0, len = folderList.length; i < len; i++) {
								if (folderList[i]["name"] == newValue) {
									isValid = true;
									break;
								}
							}
						} else {
							// 복붙일경우.. 키값으로
							for (let i = 0, len = folderList.length; i < len; i++) {
								if (folderList[i]["oid"] == newValue) {
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
					for (let i = 0, len = folderList.length; i < len; i++) {
						if (folderList[i]["oid"] == value) {
							retStr = folderList[i]["name"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
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
					list: partType1List, 
					keyField : "key",
					valueField : "value",
					matchFromFirst : false,
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver : false,
					descendants : [ "partType2" ],
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						// 공백 선책가능하게
						if(newValue == "") {
							isValid = true;
						}
						
						if(!fromClipboard) {
							for (let i = 0, len = partType1List.length; i < len; i++) {
								if (partType1List[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
						} else {
							for (let i = 0, len = partType1List.length; i < len; i++) {
								if (partType1List[i]["key"] == newValue) {
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
					for (let i = 0, len = partType1List.length; i < len; i++) {
						if (partType1List[i]["key"] == value) {
							retStr = partType1List[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
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
					list: partType1List, 
					keyField : "key",
					valueField : "value",
					matchFromFirst : false,
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver : false,
					descendants : [ "partType3" ],
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;  
						if(newValue == "") {
							isValid = true;
						}

						const key = item.partType1;
						const dd = partType2Map[key];
						if(dd !== undefined) {
							if(!fromClipboard) {
								for (let i = 0, len = dd.length; i < len; i++) {
									if (dd[i]["value"] == newValue) {
										isValid = true;
										break;
									}
								}
							} else {
								for (let i = 0, len = dd.length; i < len; i++) {
									if (dd[i]["key"] == newValue) {
										isValid = true;
										break;
									}
								}
							}
						}
						return {
							"validate" : isValid,
							"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
						};
					},
					listFunction : function(rowIndex, columnIndex, item, dataField) {
						const key = item.partType1;
						const dd = partType2Map[key];
						if (dd === undefined) {
							return [];
						}
						return dd;
					},
				},
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
					let retStr = "";
					const key = item.partType1;
					const dd = partType2Map[key];
					if (dd === undefined) {
						return "";
					}
					
					for (let i = 0, len = dd.length; i < len; i++) {
						if (dd[i]["key"] == value) {
							retStr = dd[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
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
					list: partType1List, 
					keyField : "key",
					valueField : "value",
					matchFromFirst : false,
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver : false,
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;  
						if(newValue == "") {
							isValid = true;
						}

						const key = item.partType2;
						const dd = partType3Map[key];
						if(dd !== undefined) {
							if(!fromClipboard) {
								for (let i = 0, len = dd.length; i < len; i++) {
									if (dd[i]["value"] == newValue) {
										isValid = true;
										break;
									}
								}
							} else {
								for (let i = 0, len = dd.length; i < len; i++) {
									if (dd[i]["key"] == newValue) {
										isValid = true;
										break;
									}
								}
							}
						}
						return {
							"validate" : isValid,
							"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
						};
					},
					listFunction : function(rowIndex, columnIndex, item, dataField) {
						const key = item.partType2;
						const dd = partType3Map[key];
						if (dd === undefined) {
							return [];
						}
						return dd;
					},
				},
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
					let retStr = "";
					const key = item.partType2;
					const dd = partType3Map[key];
					if (dd === undefined) {
						return "";
					}
					
					for (let i = 0, len = dd.length; i < len; i++) {
						if (dd[i]["key"] == value) {
							retStr = dd[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
			}, {
				dataField : "seq",
				headerText : "SEQ<br>(3자리)",
				dataType : "string",
				width : 120,
				editRenderer : {
					type : "InputEditRenderer",
					onlyNumeric : true, // 0~9만 입력가능
					maxlength : 3, // 글자수 10으로 제한 (천단위 구분자 삽입(autoThousandSeparator=true)로 한 경우 구분자 포함해서 10자로 제한)
				}
			}, {
				dataField : "etc",
				headerText : "기타<br>(2자리)",
				dataType : "string",
				width : 120,
				editRenderer : {
					type : "InputEditRenderer",
					onlyNumeric : true, // 0~9만 입력가능
					maxlength : 2, // 글자수 10으로 제한 (천단위 구분자 삽입(autoThousandSeparator=true)로 한 경우 구분자 포함해서 10자로 제한)
				}
			}, {
				headerText : "MAT",
				children : [ {
					dataField : "mat",
					headerText : "재질코드",
					width : 120,
					editable : false
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
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = matList.length; i < len; i++) {
							if (matList[i]["key"] == value) {
								retStr = matList[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						list : matList,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							if(newValue == "") {
								isValid = true;
							}
							
							if(!fromClipboard) {
								for (let i = 0, len = matList.length; i < len; i++) {
									if (matList[i]["value"] == newValue) {
										isValid = true;
										break;
									}
								}
							} else {
								for (let i = 0, len = matList.length; i < len; i++) {
									if (matList[i]["key"] == newValue) {
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
				} ]
			}, {
				dataField : "partName1",
				headerText : "품목명<br>(대제목)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "partName2",
				headerText : "품목명<br>(중제목)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "partName3",
				headerText : "품목명<br>(소제목)",
				dataType : "string",
				width : 120,
			}, {
				dataField : "partName4",
				headerText : "품목명<br>(KEY-IN)",
				dataType : "string",
				width : 120,
				labelFunction: function (rowIndex, columnIndex, value, headerText, item) {
					if(value != null){
						value = value.toUpperCase()
						item.partName4 = value;
						return value;
					}
				},
			}, {
				dataField : "unit",
				headerText : "단위",
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
					list: unitList, 
					keyField : "key",
					valueField : "value",
					matchFromFirst : false,
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver : false,
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						// 공백 선책가능하게
						if(newValue == "") {
							isValid = true;
						}
						if(!fromClipboard) {
							for (let i = 0, len = unitList.length; i < len; i++) {
								if (unitList[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
						} else {
							for (let i = 0, len = unitList.length; i < len; i++) {
								if (unitList[i]["key"] == newValue) {
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
					for (let i = 0, len = unitList.length; i < len; i++) {
						if (unitList[i]["key"] == value) {
							retStr = unitList[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},					
			}, {
				headerText : "부서",
				children : [ {
					dataField : "deptcode",
					headerText : "부서코드",
					width : 120,
					editable : false
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
					editRenderer : {
						type: "ComboBoxRenderer",
						list: deptcodeList, 
						keyField : "key",
						valueField : "value",
						matchFromFirst : false,
						autoCompleteMode: true, // 자동완성 모드 설정
						autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : false,
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							// 공백 선책가능하게
							if(newValue == "") {
								isValid = true;
							}
							if(!fromClipboard) {
								for (let i = 0, len = deptcodeList.length; i < len; i++) {
									if (deptcodeList[i]["value"] == newValue) {
										isValid = true;
										break;
									}
								}
							} else {
								for (let i = 0, len = deptcodeList.length; i < len; i++) {
									if (deptcodeList[i]["key"] == newValue) {
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
						for (let i = 0, len = deptcodeList.length; i < len; i++) {
							if (deptcodeList[i]["key"] == value) {
								retStr = deptcodeList[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},		
				} ]
			}, {
				headerText : "프로젝트 코드",
				children : [ {
					dataField : "model",
					headerText : "프로젝트 코드",
					width : 120,
					editable : false
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
					editRenderer : {
						type: "ComboBoxRenderer",
						list: modelList, 
						keyField : "key",
						valueField : "value",
						matchFromFirst : false,
						autoCompleteMode: true, // 자동완성 모드 설정
						autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : false,
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							// 공백 선책가능하게
							if(newValue == "") {
								isValid = true;
							}
							if(!fromClipboard) {
								for (let i = 0, len = modelList.length; i < len; i++) {
									if (modelList[i]["value"] == newValue) {
										isValid = true;
										break;
									}
								}
							} else {
								for (let i = 0, len = modelList.length; i < len; i++) {
									if (modelList[i]["key"] == newValue) {
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
						for (let i = 0, len = modelList.length; i < len; i++) {
							if (modelList[i]["key"] == value) {
								retStr = modelList[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
				} ]
			}, {
				headerText : "제작방법",
				children : [ {
					dataField : "productmethod",
					headerText : "제작방법 코드",
					width : 120,
					editable : false
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
					editRenderer : {
						type: "ComboBoxRenderer",
						list: productmethodList, 
						keyField : "key",
						valueField : "value",
						matchFromFirst : false,
						autoCompleteMode: true, // 자동완성 모드 설정
						autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : false,
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							// 공백 선책가능하게
							if(newValue == "") {
								isValid = true;
							}
							
							if(!fromClipboard) {
								for (let i = 0, len = productmethodList.length; i < len; i++) {
									if (productmethodList[i]["value"] == newValue) {
										isValid = true;
										break;
									}
								}
							} else {
								for (let i = 0, len = productmethodList.length; i < len; i++) {
									if (productmethodList[i]["key"] == newValue) {
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
						for (let i = 0, len = productmethodList.length; i < len; i++) {
							if (productmethodList[i]["key"] == value) {
								retStr = productmethodList[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
				} ]
			}, {
				dataField : "specification",
				headerText : "사양",
				dataType : "string",
				width : 120,
			}, {
				headerText : "주도면",
				children : [ {
					dataField : "primaryName",
					headerText : "주도면 파일명",
					width: 160,
					editable : false,
				}, {
					dataField : "primary",
					headerText : "주도면",
					width: 160,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item.id;
							const url = getCallUrl("/aui/primaryDrawing?oid=" + oid + "&method=attach");
							_popup(url, 800, 400,"n");
						}
					},
				}]
			}, {
				headerText : "관련문서",
				children : [ {
					dataField : "rows90",
					dataType : "string",
					visible : false
				}, {
					dataField : "docNumber",
					headerText : "문서번호",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					headerText : "관련문서",
					dataType : "string",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "문서추가",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item.id;
							const url = getCallUrl("/doc/popup?method=insert90&multi=true");
							_popup(url, 1800, 900, "n");
						}
					}
				}]
			}, {
				headerText : "관련RoHS",
				children : [ {
					dataField : "rows106",
					dataType : "string",
					visible : false
				}, {
					dataField : "rohsNumber",
					headerText : "RoHs 번호",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					headerText : "관련RoHS",
					dataType : "string",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "물질추가",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item.id;
							const url = getCallUrl("/rohs/listPopup?method=insert106&multi=true");
							_popup(url, 1800, 900, "n");
						}
					}
				}]
			}]

			function createAUIGrid(columnLayout) {
				const props = {
					rowIdField: "id",
					editable : true,
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					wordWrap : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.bind(myGridID, "keyDown", auiKeyDownHandler);
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
				auiReadyHandler();
			}

			function auiReadyHandler() {
				const item = new Object();
				item.unit = "ea";
				AUIGrid.addRow(myGridID, item, "first");
			}
			
			function auiCellEditEndHandler(event) {
				const dataField = event.dataField;
				const item = event.item;
				const rowIndex = event.rowIndex;
				
				if (dataField === "partType1") {
					const partType = item.partType1;
					if(partType === "") {
						partType2Map = {};
			        	const item = {
								partType2 : ""
						}
			        	AUIGrid.updateRow(myGridID, item, rowIndex);
					} else {
						const url = getCallUrl("/code/getChildCodeByParent");
					    const params = {
				    		codeType: "PARTTYPE",
					        parentOid: partType
					    };

					    parent.openLayer();
				        call(url, params, function(data) {
				        	if(data.result) {
					        	partType2Map[partType] = data.list;
					        	const item = {
									partType2 : ""
								}
					        	AUIGrid.updateRow(myGridID, item, rowIndex);
				        	} else {
				        		alert(data.msg);
				        	}
				        	parent.closeLayer();
				        });
					}
				}
				
				if (dataField === "partType2") {
					const partType = item.partType2;
					const url = getCallUrl("/code/getChildCodeByParent");
				    const params = {
				        codeType: "PARTTYPE",
				        parentOid: partType
				    };

				    parent.openLayer();
			        call(url, params, function(data) {
			        	if(data.result) {
				        	partType3Map[partType] = data.list;
				        	const item = {
								partType3 : ""
							}
				        	AUIGrid.updateRow(myGridID, item, rowIndex);
			        	} else {
			        		alert(data.msg);
			        	}
			        	parent.closeLayer();
			        });
				}
			}

			function auiKeyDownHandler(event) {
				if (event.keyCode == 13) {
					const selectedItems = AUIGrid.getSelectedItems(event.pid);
					const rowIndex = selectedItems[0].rowIndex;
					if (rowIndex === AUIGrid.getRowCount(event.pid) - 1) {
						const item = new Object();
						item.unit = "ea";
						AUIGrid.addRow(event.pid, item);
						return false;
					}
				}
				return true;
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
			
			// 문서추가
			function insert90(arr, callBack) {
				const rows90 = [];
				let number = "";
				arr.forEach(function(dd) {
					const item = dd.item;
					rows90.push(item);
					number += item.number + "\n";
				})
				AUIGrid.updateRowsById(myGridID, {
					id : recentGridItem.id,
					rows90 : rows90,
					docNumber : toRowsExp(number)
				});
				callBack(true);
			}
			
			// 물질 추가
			function insert106(arr, callBack) {
				const rows106 = [];
				let number = "";
				arr.forEach(function(dd) {
					const item = dd.item;
					rows106.push(item);
					number += item.number + "\n";
				})
				AUIGrid.updateRowsById(myGridID, {
					id : recentGridItem.id,
					rows106 : rows106,
					rohsNumber : toRowsExp(number)
				});
				callBack(true);
			}
			
			// 주 도면
			function attach(data) {
				AUIGrid.updateRowsById(myGridID, {
					id : recentGridItem.id,
					primary : data.cacheId,
					primaryName : data.name
				});
				recentGridItem = undefined;
			}
			
			function batch(){
				const gridData = AUIGrid.getGridData(myGridID);
				for (let i = 0; i < gridData.length; i++) {
					const item = gridData[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item.id);
					
					if (isNull(item.location)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "저장위치를 선택하세요.");
						return false;
					}

					if (isNull(item.partType1)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "품목구분을 선택하세요.");
						return false;
					}

					if (isNull(item.partType2)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 2, "대분류를 입력하세요.");
						return false;
					}

					if (isNull(item.partType3)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 3, "중분류를 선택하세요.");
						return false;
					}
					
					if (isNull(item.seq)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 4, "SEQ(3자리)를 입력하세요.");
						return false;
					}
					
					if (isNull(item.etc)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 5, "ETC(2자리)를 입력하세요.");
						return false;
					}

					if (isNull(item.partName1)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 8, "품목명(대제목)을 입력하세요.");
						return false;
					}
					
					if (isNull(item.partName2)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 9, "품목명(중제목)을 입력하세요.");
						return false;
					}
					
					if (isNull(item.partName3)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 10, "품목명(소제목)을 입력하세요.");
						return false;
					}
					
					if (isNull(item.partName4)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 11, "품목명(KEY-IN)을 입력하세요.");
						return false;
					}

					if (isNull(item.unit)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 12, "단위를 선택하세요.");
						return false;
					}

					if (isNull(item.deptcode)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 14, "부서를 선택하세요.");
						return false;
					}

					if (isNull(item.model)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 16, "프로젝트 코드를 선택하세요.");
						return false;
					}

					if (isNull(item.productmethod)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 18, "제작방법을 선택하세요.");
						return false;
					}

				}
				
				if (!confirm("등록 하시겠습니까?")) {
					return false;
				}
				
				const url = getCallUrl("/part/batch");
				const params = {
						gridData : gridData
					}
				
				params.gridData.forEach((param)=>{
					if(isEmpty(param.rows90)){
						param.rows90=[];
					}
				});
				
				params.gridData.forEach((param)=>{
					if(isEmpty(param.rows106)){
						param.rows106=[];
					}
				});
				
				params.gridData.forEach((param)=>{
					if(isEmpty(param.secondary)){
						param.secondary=[];
					}
				});
				
				
				parent.openLayer();
				logger(params);
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
	 					document.location.href = getCallUrl("/part/list");
					} else {
						parent.closeLayer();
					}
				});
			}
			
			// 추가
			function addBtn(){
				const item = new Object();
				item.unit = "ea";
				AUIGrid.addRow(myGridID, item, 'last');
			}
			
			// 삭제
			function deleteBtn(){
				const items = AUIGrid.getCheckedRowItemsAll(myGridID);
				if(items.length==0){
					alert("선택된 품목이 없습니다.");
					return;
				}
				
// 				if (!confirm("삭제하시겠습니까?")){
// 					return;
// 				}
				
				AUIGrid.removeCheckedRows(myGridID);
			}
			
			// 개행 처리 
			function toRowsExp(value) {
				return value.replace(/\r|\n|\r\n/g, "<br/>");
			}
		</script>
	</form>
</html>