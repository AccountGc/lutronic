<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.folder.Folder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray flist = (JSONArray) request.getAttribute("flist"); // 폴더
JSONArray mlist = (JSONArray) request.getAttribute("mlist"); // 프로젝트 코드
JSONArray dlist = (JSONArray) request.getAttribute("dlist"); // 부서
JSONArray nlist = (JSONArray) request.getAttribute("nlist"); // 문서종류
JSONArray plist = (JSONArray) request.getAttribute("plist"); // 보존기간
JSONArray tlist = (JSONArray) request.getAttribute("tlist"); // 보존기간
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
					<input type="button" value="등록" title="등록" onclick="batch();">
					<input type="button" value="추가" title="추가" class="blue" onclick="addRow();">
					<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 570px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			let recentGridItem;
			const flist =
		<%=flist%>
			const dlist =
		<%=dlist%>
			const mlist =
		<%=mlist%>
			const nlist =
		<%=nlist%>
			const tlist =
		<%=tlist%>
			const plist =
		<%=plist%>
			const llist = [ {
				key : "LC_Default",
				value : "기본결재"
			}, {
				key : "LC_Default_NonWF",
				value : "일괄결재"
			} ]
			const layout = [ {
				headerText : "결재",
				children : [ {
					dataField : "rows8",
					dataType : "string",
					visible : false
				}, {
					dataField : "agree",
					headerText : "합의",
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
							const _$uid = item._$uid;
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
					type : "ComboBoxRenderer",
					list : flist,
					matchFromFirst : false,
					autoCompleteMode : true, // 자동완성 모드 설정
					autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = flist.length; i < len; i++) {
							if (flist[i] == newValue) {
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
				dataField : "documentName",
				headerText : "문서 종류",
				width : 250,
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
					for (let i = 0, len = nlist.length; i < len; i++) {
						if (nlist[i]["key"] == value) {
							retStr = nlist[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type : "ComboBoxRenderer",
					list : nlist,
					matchFromFirst : false,
					autoCompleteMode : true, // 자동완성 모드 설정
					autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
					keyField : "key",
					valueField : "value",
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = nlist.length; i < len; i++) {
							if (nlist[i] == newValue) {
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
				dataField : "name",
				headerText : "문서명",
				width : 120,
				dataType : "string",
				width : 120,
				cellMerge : true,
			}, {
				dataField : "lifecycle",
				headerText : "결재 방식",
				dataType : "string",
				width : 120,
				cellMerge : true,
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
					for (let i = 0, len = llist.length; i < len; i++) {
						if (llist[i]["key"] == value) {
							retStr = llist[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type : "ComboBoxRenderer",
					list : llist,
					matchFromFirst : false,
					autoCompleteMode : true, // 자동완성 모드 설정
					autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
					keyField : "key",
					valueField : "value",
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = llist.length; i < len; i++) {
							if (llist[i] == newValue) {
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
				dataField : "documentType_code",
				headerText : "문서유형",
				dataType : "string",
				width : 120,
				cellMerge : true,
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
					for (let i = 0, len = tlist.length; i < len; i++) {
						if (tlist[i]["key"] == value) {
							retStr = tlist[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
				editRenderer : {
					type : "ComboBoxRenderer",
					list : tlist,
					matchFromFirst : false,
					autoCompleteMode : true, // 자동완성 모드 설정
					autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
					showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
					keyField : "key",
					valueField : "value",
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = tlist.length; i < len; i++) {
							if (tlist[i] == newValue) {
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
				headerText : "프로젝트 코드",
				children : [ {
					dataField : "model_code",
					headerText : "프로젝트 코드",
					width : 100,
					editable : false
				}, {
					dataField : "model",
					headerText : "프로젝트 명",
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
						for (let i = 0, len = mlist.length; i < len; i++) {
							if (mlist[i]["key"] == value) {
								// 								AUIGrid.setCellValue(myGridID, rowIndex, "model_code", mlist[i]["key"]);
								retStr = mlist[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						list : mlist,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = mlist.length; i < len; i++) {
								if (mlist[i] == newValue) {
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
				dataField : "writer",
				headerText : "작성자",
				dataType : "string",
				width : 120,
				cellMerge : true,
			}, {
				headerText : "부서",
				children : [ {
					dataField : "deptcode_code",
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
						for (let i = 0, len = dlist.length; i < len; i++) {
							if (dlist[i]["key"] == value) {
								// 								AUIGrid.setCellValue(myGridID, rowIndex, "deptcode_code", dlist[i]["key"]);
								retStr = dlist[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						list : dlist,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = dlist.length; i < len; i++) {
								if (dlist[i] == newValue) {
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
				dataField : "interalnumber",
				headerText : "내부문서 번호",
				width : 160,
			}, {
				headerText : "보존기간",
				children : [ {
					dataField : "preseration_code",
					headerText : "보존기간코드",
					width : 120,
					editable : false
				}, {
					dataField : "preseration",
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
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = plist.length; i < len; i++) {
							if (plist[i]["key"] == value) {
								// 								AUIGrid.setCellValue(myGridID, rowIndex, "preseration_code", plist[i]["key"]);
								retStr = plist[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						list : plist,
						matchFromFirst : false,
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
						showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = plist.length; i < len; i++) {
								if (plist[i] == newValue) {
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
				headerText : "주 첨부파일",
				children : [ {
					dataField : "primaryName",
					headerText : "파일명",
					width : 160,
					editable : false,
				}, {
					dataField : "primary",
					headerText : "주 첨부파일",
					width : 160,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/aui/primary?oid=" + oid + "&method=primary");
							_popup(url, 800, 200, "n");
						}
					},
				} ]
			}, {
				headerText : "첨부파일",
				children : [ {
					dataField : "secondaryName",
					headerText : "파일명",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					dataField : "secondary",
					headerText : "파일",
					width : 160,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/aui/secondary?oid=" + oid + "&method=secondary");
							_popup(url, 800, 400, "n");
						}
					},
				} ]
			}, {
				headerText : "관련품목",
				children : [ {
					dataField : "rows91",
					dataType : "string",
					visible : false
				}, {
					dataField : "partNumber",
					headerText : "품목번호",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					headerText : "관련품목",
					dataType : "string",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "부품추가",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/part/popup?method=insert91&multi=true");
							_popup(url, 1800, 900, "n");
						}
					}
				} ]
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
							const oid = item._$uid;
							const url = getCallUrl("/doc/popup?method=insert90&multi=true");
							_popup(url, 1800, 900, "n");
						}
					}
				} ]
			}, {
				headerText : "관련EO",
				children : [ {
					dataField : "rows100",
					dataType : "string",
					visible : false
				}, {
					dataField : "eoNumber",
					headerText : "EO번호",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					headerText : "관련EO",
					dataType : "string",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "EO추가",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/eo/popup?method=insert100&multi=true");
							_popup(url, 1800, 900, "n");
						}
					}
				} ]
			}, {
				headerText : "관련CR",
				children : [ {
					dataField : "rows101",
					dataType : "string",
					visible : false
				}, {
					dataField : "crNumber",
					headerText : "CR번호",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					headerText : "관련CR",
					dataType : "string",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "CR추가",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/cr/popup?method=insert101&multi=true");
							_popup(url, 1800, 900, "n");
						}
					}
				} ]
			}, {
				headerText : "관련ECPR",
				children : [ {
					dataField : "rowsEcpr",
					dataType : "string",
					visible : false
				}, {
					dataField : "ecprNumber",
					headerText : "ECPR번호",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					dataField : "ecpr",
					headerText : "관련ECPR",
					dataType : "string",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "ECPR추가",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/ecpr/popup?method=insertEcpr&multi=true");
							_popup(url, 1800, 900, "n");
						}
					}
				} ]
			}, {
				headerText : "관련ECO",
				children : [ {
					dataField : "rows105",
					dataType : "string",
					visible : false
				}, {
					dataField : "ecoNumber",
					headerText : "ECO번호",
					width : 160,
					editable : false,
					renderer : {
						type : "TemplateRenderer"
					}
				}, {
					headerText : "관련ECO",
					dataType : "string",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "ECO추가",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/eco/popup?method=insert105&multi=true");
							_popup(url, 1800, 900, "n");
						}
					}
				} ]
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
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
			}

			function auiCellEditEndHandler(event) {
				const dataField = event.dataField;
				if (dataField === "model") {
					AUIGrid.setCellValue(event.pid, event.rowIndex, "model_code", event.value);
				} else if (dataField === "preseration") {
					AUIGrid.setCellValue(event.pid, event.rowIndex, "preseration_code", event.value);
				} else if (dataField === "deptcode") {
					AUIGrid.setCellValue(event.pid, event.rowIndex, "deptcode_code", event.value);
				}
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
			function addRow() {
				AUIGrid.addRow(myGridID, {}, 'last');
			}

			// 삭제
			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("삭제할 행을 선택하세요.");
					return false;
				}

				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}
			
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
					item.type = "합의";
					item.sort = sort;
					rows8.push(item);
					agr += item.name + "\n";
				}
				
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					rows8 : rows8,
					receive : toRowsExp(rece),
					approval : toRowsExp(appro),
					agree : toRowsExp(agr)
				});
			}

			// 품목 추가 메소드
			function insert91(arr, callBack) {
				const rows91 = [];
				let number = "";
				arr.forEach(function(dd) {
					const item = dd.item;
					rows91.push(item);
					number += item.number + "\n";
					// 중복 체크?
					// 					const unique = AUIGrid.isUniqueValue(myGridID91, "part_oid", item.part_oid);
					// 					if (unique) {
					// 						AUIGrid.addRow(myGridID91, item, rowIndex);
					// 					} else {
					// 						// 중복은 그냥 경고 없이 처리 할지 합의?
					// 						alert(item.number + " 품목은 이미 추가 되어있습니다.");
					// 					}
				})
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					rows91 : rows91,
					partNumber : toRowsExp(number)
				});
				callBack(true);
			}

			// 문서추가
			function insert90(arr, callBack) {
				const rows90 = [];
				let number = "";
				arr.forEach(function(dd) {
					const item = dd.item;
					rows90.push(item);
					number += item.number + "\n";
					// 중복 체크?
					// 					const unique = AUIGrid.isUniqueValue(myGridID91, "part_oid", item.part_oid);
					// 					if (unique) {
					// 						AUIGrid.addRow(myGridID91, item, rowIndex);
					// 					} else {
					// 						// 중복은 그냥 경고 없이 처리 할지 합의?
					// 						alert(item.number + " 품목은 이미 추가 되어있습니다.");
					// 					}
				})
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					rows90 : rows90,
					docNumber : toRowsExp(number)
				});
				callBack(true);
			}
			
			// EO 추가
			function insert100(arr, callBack) {
				const rows100 = [];
				let number = "";
				arr.forEach(function(dd) {
					const item = dd.item;
					rows100.push(item);
					number += item.number + "\n";
				})
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					rows100 : rows100,
					eoNumber : toRowsExp(number)
				});
				callBack(true);
			}	
			
			// CR 추가
			function insert101(arr, callBack) {
				const rows101 = [];
				let number = "";
				arr.forEach(function(dd) {
					const item = dd.item;
					rows101.push(item);
					number += item.number + "\n";
				})
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					rows101 : rows101,
					crNumber : toRowsExp(number)
				});
				callBack(true);
			}	

			// ECPR 추가
			function insertEcpr(arr, callBack) {
				const rowsEcpr = [];
				let number = "";
				arr.forEach(function(dd) {
					const item = dd.item;
					rowsEcpr.push(item);
					number += item.number + "\n";
				})
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					rowsEcpr : rowsEcpr,
					ecprNumber : toRowsExp(number)
				});
				callBack(true);
			}	

			// ECO 추가
			function insert105(arr, callBack) {
				const rows105 = [];
				let number = "";
				arr.forEach(function(dd) {
					const item = dd.item;
					rows105.push(item);
					number += item.number + "\n";
				})
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					rows105 : rows105,
					ecoNumber : toRowsExp(number)
				});
				callBack(true);
			}	

			function primary(data) {
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					primary : data.cacheId,
					primaryName : data.name
				});
				recentGridItem = undefined;
			}
			// 첨부파일
			function secondary(data) {
				const cacheId = [];
				let name = "";
				for (let i = 0; i < data.length; i++) {
					cacheId.push(data[i].cacheId);
					name += data[i].name + "\n";
				}
				// 개행 처리
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					secondary : cacheId,
					secondaryName : toRowsExp(name)
				});
				// 초기화
				recentGridItem = undefined;
			}

			// 등록
			function batch() {
				const gridData = AUIGrid.getGridData(myGridID);
				for (let i = 0; i < gridData.length; i++) {
					const item = gridData[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);

					if (isNull(item.location)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 5, "저장위치를 선택하세요.");
						return false;
					}

					if (isNull(item.documentName)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 6, "문서종류를 선택하세요.");
						return false;
					}

					if (isNull(item.lifecycle)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 8, "결재방식을 선택하세요.");
						return false;
					}

					if (isNull(item.documentType_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 9, "문서유형을 선택하세요.");
						return false;
					}

					if (isNull(item.preseration_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 17, "보존기간을 선택하세요.");
						return false;
					}

					if (isNull(item.primary)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 19, "주 첨부파일을 선택하세요.");
						return false;
					}
				}

				if (!confirm("등록 하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/doc/batch");
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
						document.location.href = getCallUrl("/doc/list");
					} else {
						closeLayer();
					}
				});
			}
			// 개행 처리 
			function toRowsExp(value) {
				return value.replace(/\r|\n|\r\n/g, "<br/>");
			}
		</script>
	</form>
</html>