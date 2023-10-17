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
JSONArray partName1List = (JSONArray) request.getAttribute("partName1List");
JSONArray partName2List = (JSONArray) request.getAttribute("partName2List");
JSONArray partName3List = (JSONArray) request.getAttribute("partName3List");
ArrayList<NumberCodeDTO> partType1List = (ArrayList<NumberCodeDTO>) request.getAttribute("partType1List");
QuantityUnit[] unitList = (QuantityUnit[]) request.getAttribute("unitList");
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
			let partType2Map = {};
			let partType3Map = {};
			let recentGridItem = null;
			let matList = <%= matList %>;
			let folderList = <%= folderList %>;
			let deptcodeList = <%= deptcodeList %>
			let productmethodList = <%= productmethodList %>
			let modelList = <%= modelList %>
			let partName1List = <%= partName1List %>
			let partName2List = <%= partName2List %>
			let partName3List = <%= partName3List %>
			let partType1List = [];
			<%for(NumberCodeDTO partType1 : partType1List){%>
				partType1List.push({"code" : "<%= partType1.getOid() %>", "value" : "[<%= partType1.getCode() %>]<%= partType1.getName() %>"});
			<% } %>
			let unitList = [];
			<% for(QuantityUnit unit : unitList){ %>
				unitList.push({ "code" : "<%= unit.toString() %>", "value" : "<%= unit.getDisplay() %>"});
			<% } %>

			const layout = [ {
				headerText : "결재",
				children : [ {
					dataField : "rows8",
					dataType : "string",
					visible : false
				}, {
					dataField : "agree",
					headerText : "협의",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					dataField : "approval",
					headerText : "결재",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					dataField : "receive",
					headerText : "수신",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					headerText : "결재선 지정",
					dataType : "string",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "결재선 지정",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const approvals = [];
							const agrees = [];
							const receives = [];
							const oid = item.id;
							const url = getCallUrl("/workspace/popup");
							const p = _popup(url, 1400, 900, "n");
							p.approvals = approvals;
							p.agrees = agrees;
							p.receives = receives;
						}
					}
				}]
			}, {
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
					matchFromFirst : false,
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = folderList.length; i < len; i++) {
							if (folderList[i]["code"] == newValue) {
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
				editRenderer : {
					type : "InputEditRenderer",
					onlyNumeric : true, // 0~9만 입력가능
					maxlength : 3, // 글자수 10으로 제한 (천단위 구분자 삽입(autoThousandSeparator=true)로 한 경우 구분자 포함해서 10자로 제한)
				}
			}, {
				dataField : "etc",
				headerText : "CUSTOM<br>(2자리)",
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
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = matList.length; i < len; i++) {
								if (matList[i] == newValue) {
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
					let retStr = "";
					for (let i = 0, len = partName1List.length; i < len; i++) {
						if (partName1List[i]["key"] == value) {
							// 								AUIGrid.setCellValue(myGridID, rowIndex, "deptcode_code", dlist[i]["key"]);
							retStr = partName1List[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: partName1List, 
					matchFromFirst : false,
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField : "key",
					valueField : "value",
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = partName1List.length; i < len; i++) {
							if (partName1List[i] == newValue) {
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
					let retStr = "";
					for (let i = 0, len = partName2List.length; i < len; i++) {
						if (partName2List[i]["key"] == value) {
							// 								AUIGrid.setCellValue(myGridID, rowIndex, "deptcode_code", dlist[i]["key"]);
							retStr = partName2List[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: partName2List, 
					matchFromFirst : false,
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField : "key",
					valueField : "value",
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = partName2List.length; i < len; i++) {
							if (partName2List[i] == newValue) {
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
					let retStr = "";
					for (let i = 0, len = partName3List.length; i < len; i++) {
						if (partName3List[i]["key"] == value) {
							// 								AUIGrid.setCellValue(myGridID, rowIndex, "deptcode_code", dlist[i]["key"]);
							retStr = partName3List[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: partName3List, 
					matchFromFirst : false,
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField : "key",
					valueField : "value",
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = partName3List.length; i < len; i++) {
							if (partName3List[i] == newValue) {
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
				labelFunction: function (rowIndex, columnIndex, value, headerText, item) {
					var retStr = "";
					for (var i = 0, len = unitList.length; i < len; i++) {
						if (unitList[i]["code"] == value) {
							retStr = unitList[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type: "ComboBoxRenderer",
					list: unitList, 
					matchFromFirst : false,
					autoCompleteMode: true, // 자동완성 모드 설정
					autoEasyMode: true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
					keyField: "code", 
					valueField: "value",
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = unitList.length; i < len; i++) {
							if (unitList[i]["value"] == newValue) {
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
					editRenderer : {
						type : "ComboBoxRenderer",
						list : deptcodeList,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = deptcodeList.length; i < len; i++) {
								if (deptcodeList[i] == newValue) {
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
					editRenderer : {
						type : "ComboBoxRenderer",
						list : modelList,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = modelList.length; i < len; i++) {
								if (modelList[i] == newValue) {
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
					editRenderer : {
						type : "ComboBoxRenderer",
						list : productmethodList,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = productmethodList.length; i < len; i++) {
								if (productmethodList[i] == newValue) {
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
					headerHeight : 35,
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
			
			// 결재선 지정
			function setLine(agree, approval, receive) {
				const rows8 = [];
				let rece = "";
				let appro = "";
				let agr = "";
				
				for (let i = receive.length - 1; i >= 0; i--) {
					const item = receive[i];
					item.type = "수신";
					rows8.push(item);
					rece += item.name + "\n";
				}

				let sort = approval.length;
				for (let i = approval.length - 1; i >= 0; i--) {
					const item = approval[i];
					item.type = "결재";
					item.sort = sort;
					rows8.push(item);
					appro += item.name + "\n";
				}

				for (let i = agree.length - 1; i >= 0; i--) {
					const item = agree[i];
					item.type = "협의";
					item.sort = sort;
					rows8.push(item);
					agr += item.name + "\n";
				}
				
				AUIGrid.updateRowsById(myGridID, {
					id : recentGridItem.id,
					rows8 : rows8,
					receive : toRowsExp(rece),
					approval : toRowsExp(appro),
					agree : toRowsExp(agr)
				});
			}
			
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
						AUIGrid.showToastMessage(myGridID, rowIndex, 5, "저장위치를 선택하세요.");
						return false;
					}

					if (isNull(item.partType1)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 6, "품목구분을 선택하세요.");
						return false;
					}

					if (isNull(item.partType2)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 7, "대분류를 입력하세요.");
						return false;
					}

					if (isNull(item.partType3)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 8, "중분류를 선택하세요.");
						return false;
					}


					if (isNull(item.partName1)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 13, "품목명(대제목)을 선택하세요.");
						return false;
					}

					if (isNull(item.partName2)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 14, "품목명(중제목)을 선택하세요.");
						return false;
					}

					if (isNull(item.partName3)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 15, "품목명(소제목)을 선택하세요.");
						return false;
					}

					if (isNull(item.partName4)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 16, "품목명(KEY-IN)을 선택하세요.");
						return false;
					}

					if (isNull(item.unit)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 17, "단위를 선택하세요.");
						return false;
					}

					if (isNull(item.deptcode)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 19, "부서를 선택하세요.");
						return false;
					}

					if (isNull(item.model)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 21, "프로젝트 코드를 선택하세요.");
						return false;
					}

					if (isNull(item.productmethod)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 23, "제작방법을 선택하세요.");
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
					params.rows8 && toRegister(param, param.rows8);	
				})
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
				AUIGrid.addRow(myGridID, {}, 'last');
			}
			
			// 삭제
			function deleteBtn(){
				var items = AUIGrid.getCheckedRowItemsAll(myGridID);
				if(items.length==0){
					alert("선택된 품목이 없습니다.");
					return;
				}
				
				if (!confirm("삭제하시겠습니까?")){
					return;
				}
				
				AUIGrid.removeCheckedRows(myGridID);
			}
			
			// 개행 처리 
			function toRowsExp(value) {
				return value.replace(/\r|\n|\r\n/g, "<br/>");
			}
		</script>
	</form>
</html>