<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.rohs.service.RohsHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.part.dto.PartDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
boolean isAdmin = CommonUtil.isAdmin();
%>
<style type="text/css">
.preMerge {
	font-weight: bold !important;
	color: red !important;
	background-color: rgb(206, 222, 255) !important;
}

.afterMerge {
	font-weight: bold !important;
	color: red !important;
	background-color: rgb(200, 255, 203) !important;
}
</style>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				완제품 품목
			</div>
		</td>
	</tr>
</table>

<div id="grid500" style="height: 240px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID500;
	const columns500 = [
	<%if (isAdmin) {%>
		{
			dataField : "link",
			dataType : "string",
			width : 100,
		}, {
			headerText : "삭제",
			dataField : "",
			width : 100,
			renderer : {
				type : "ButtonRenderer",
				labelText : "삭제",
				onClick : function(event) {
					const link = event.item.link;
					if(!confirm("완제품 연결을 삭제 하시겠습니까?\n연결 관계만 삭제됩니다.")) {
						return false;
					}
					const url = getCallUrl("/eco/deleteLink?oid="+link);
					openLayer();
					call(url, null, function(data) {
						alert(data.msg);
						if(data.result) {
							document.location.reload();
						}
						closeLayer();
					}, "DELETE");
				}
			}	
		},
	<%}%>
		
	{
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				if (oid === "") {
					return false;
				}
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				if (oid === "") {
					return false;
				}
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			if (value === "승인됨") {
				return "approved";
			}
			return null;
		}
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "createdDate_txt",
		headerText : "등록일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "",
		headerText : "BOM",
		dataType : "string",
		width : 130,
		renderer : {
			type : "ButtonRenderer",
			labelText : "BOM 보기",
			onClick : function(event) {
				const oid = event.item.oid;
				const url = getCallUrl("/bom/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		}
	}, {
		dataField : "",
		headerText : "도면",
		dataType : "string",
		width : 80,
	} ]

	function createAUIGrid500(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : true,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			// 			autoGridHeight : true,
			enableFilter : true,
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
		}
		myGridID500 = AUIGrid.create("#grid500", columnLayout, props);
		AUIGrid.setGridData(myGridID500,
<%=AUIGridUtil.include(oid, "complete")%>
	);
	}
</script>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				설계변경 품목
				<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
				<input type="button" value="저장" title="저장" class="blue" onclick="save();">
				&nbsp;
				<div class="pretty p-switch">
					<input type="checkbox" name="dummy" value="true" onclick="reloadData();">
					<div class="state p-success">
						<label>
							<b>더미제외</b>
						</label>
					</div>
				</div>
			</div>
		</td>
	</tr>
</table>

<div id="grid510" style="height: 420px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID510;
	const part_result_code = [ {
		key : "O",
		value : "그대로 사용(O)"
	}, {
		key : "R",
		value : "수정하여 사용(R)"
	}, {
		key : "N",
		value : "신규변경품으로(N)"
	}, {
		key : "S",
		value : "폐기(S)"
	}, {
		key : "-",
		value : "-"
	}, ]

	const part_state_code = [ {
		key : "N",
		value : "신규(N)"
	}, {
		key : "D",
		value : "삭제(D)"
	}, {
		key : "R",
		value : "변경(R)"
	}, {
		key : "C",
		value : "전용(C)"
	}, ];
	const columns510 = [ {
		headerText : "개정 전",
		children : [ {
			dataField : "part_number",
			dataType : "string",
			headerText : "품목번호",
			cellColMerge : true, // 셀 가로 병합 실행
			cellColSpan : 5, // 셀 가로 병합 대상은 6개로 설정
			width : 130,
			editable : false,
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.part_oid;
					if (oid === "") {
						return false;
					}
					const url = getCallUrl("/part/view?oid=" + oid);
					_popup(url, 1600, 800, "n");
				}
			},
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				if (item.preMerge === true) {
					return "preMerge";
				}
				return null;
			},
		}, {
			dataField : "part_name",
			dataType : "string",
			headerText : "품목명",
			cellColMerge : true, // 셀 가로 병합 실행
			cellColSpan : 5, // 셀 가로 병합 대상은 6개로 설정
			width : 250,
			editable : false,
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.part_oid;
					if (oid === "") {
						return false;
					}
					const url = getCallUrl("/part/view?oid=" + oid);
					_popup(url, 1600, 800, "n");
				}
			},
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				if (item.preMerge === true) {
					return "preMerge";
				}
				return null;
			},
		}, {
			dataField : "part_state",
			dataType : "string",
			headerText : "상태",
			width : 80,
			editable : false,
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				if (value === "승인됨") {
					return "approved";
				}
				return null;
			}
		}, {
			dataField : "part_version",
			dataType : "string",
			headerText : "REV",
			width : 80,
			editable : false,
		}, {
			dataField : "part_creator",
			dataType : "string",
			headerText : "등록자",
			width : 100,
			editable : false,
		} ]
	}, {
		headerText : "개정 후",
		children : [ {
			dataField : "next_number",
			dataType : "string",
			headerText : "품목번호",
			cellColMerge : true, // 셀 가로 병합 실행
			cellColSpan : 4, // 셀 가로 병합 대상은 6개로 설정
			width : 130,
			editable : false,
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.next_oid;
					if (oid === "") {
						return false;
					}
					const url = getCallUrl("/part/view?oid=" + oid);
					_popup(url, 1600, 800, "n");
				}
			},
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				if (item.afterMerge === true) {
					return "afterMerge";
				}
				return null;
			},
		}, {
			dataField : "next_name",
			dataType : "string",
			headerText : "품목명",
			cellColMerge : true, // 셀 가로 병합 실행
			cellColSpan : 6, // 셀 가로 병합 대상은 6개로 설정
			width : 250,
			editable : false,
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.next_oid;
					if (oid === "") {
						return false;
					}
					const url = getCallUrl("/part/view?oid=" + oid);
					_popup(url, 1600, 800, "n");
				}
			},
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				if (item.afterMerge === true) {
					return "afterMerge";
				}
				return null;
			},
		}, {
			dataField : "next_state",
			dataType : "string",
			headerText : "상태",
			width : 80,
			editable : false,
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				if (value === "승인됨") {
					return "approved";
				}
				return null;
			}
		}, {
			dataField : "next_version",
			dataType : "string",
			headerText : "REV",
			width : 80,
			editable : false,
		}, {
			dataField : "next_creator",
			dataType : "string",
			headerText : "등록자",
			width : 100,
			editable : false,
		} ]
	}, {
		headerText : "BOM",
		children : [ {
			dataField : "",
			dataType : "string",
			headerText : "",
			width : 140,
			editable : false,
			renderer : {
				type : "ButtonRenderer",
				labelText : "BOM 비교",
				onClick : function(event) {
					const oid = event.item.next_oid;
					const url = "/Windchill/netmarkets/jsp/structureCompare/StructureCompare.jsp?oid=OR:" + oid + "&ncId=5304500442831603818&locale=ko";
					_popup(url, 1600, 600, "n");
				}
			}
		}, {
			dataField : "",
			dataType : "string",
			headerText : "",
			width : 140,
			editable : false,
			renderer : {
				type : "ButtonRenderer",
				labelText : "BOM 보기",
				onClick : function(event) {
					const oid = event.item.next_oid;
					if (oid === "") {
						alert("개정 후 품목이 없습니다.");
						return false;
					}
					const url = getCallUrl("/bom/view?oid=" + oid);
					_popup(url, 1600, 800, "n");
				}
			}
		} ]
	}, {
		dataField : "part_state_code",
		headerText : "부품<br>상태<br>코드",
		dataType : "string",
		width : 80,
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
			autoCompleteMode : true,
			autoEasyMode : true,
			matchFromFirst : false,
			showEditorBtnOver : false,
			list : part_state_code,
			keyField : "key",
			valueField : "value",
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				if(!fromClipboard) {
					for (let i = 0, len = part_state_code.length; i < len; i++) {
						if (part_state_code[i]["value"] == newValue) {
							isValid = true;
							break;
						}
					}
				} else {
					for (let i = 0, len = part_state_code.length; i < len; i++) {
						if (part_state_code[i]["key"] == newValue) {
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
			for (let i = 0, len = part_state_code.length; i < len; i++) {
				if (part_state_code[i]["key"] == value) {
					retStr = part_state_code[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		headerText : "기존 부품/장비",
		children : [ {
			headerText : "납품 장비",
			dataField : "delivery",
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
				type : "ComboBoxRenderer",
				autoCompleteMode : true,
				autoEasyMode : true,
				matchFromFirst : false,
				showEditorBtnOver : false,
				list : part_result_code,
				keyField : "key",
				valueField : "value",
				validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
					let isValid = false;
					if(!fromClipboard) {
						for (let i = 0, len = part_result_code.length; i < len; i++) {
							if (part_result_code[i]["value"] == newValue) {
								isValid = true;
								break;
							}
						}
					} else {
						for (let i = 0, len = part_result_code.length; i < len; i++) {
							if (part_result_code[i]["key"] == newValue) {
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
				for (let i = 0, len = part_result_code.length; i < len; i++) {
					if (part_result_code[i]["key"] == value) {
						retStr = part_result_code[i]["value"];
						break;
					}
				}
				return retStr == "" ? value : retStr;
			},
		}, {
			headerText : "완성 장비",
			dataField : "complete",
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
				type : "ComboBoxRenderer",
				autoCompleteMode : true,
				autoEasyMode : true,
				matchFromFirst : false,
				showEditorBtnOver : false,
				list : part_result_code,
				keyField : "key",
				valueField : "value",
				validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
					let isValid = false;
					if(!fromClipboard) {
						for (let i = 0, len = part_result_code.length; i < len; i++) {
							if (part_result_code[i]["value"] == newValue) {
								isValid = true;
								break;
							}
						}
					} else {
						for (let i = 0, len = part_result_code.length; i < len; i++) {
							if (part_result_code[i]["key"] == newValue) {
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
				for (let i = 0, len = part_result_code.length; i < len; i++) {
					if (part_result_code[i]["key"] == value) {
						retStr = part_result_code[i]["value"];
						break;
					}
				}
				return retStr == "" ? value : retStr;
			},
		}, {
			headerText : "사내 재고",
			dataField : "inner",
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
				type : "ComboBoxRenderer",
				autoCompleteMode : true,
				autoEasyMode : true,
				matchFromFirst : false,
				showEditorBtnOver : false,
				list : part_result_code,
				keyField : "key",
				valueField : "value",
				validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
					let isValid = false;
					if(!fromClipboard) {
						for (let i = 0, len = part_result_code.length; i < len; i++) {
							if (part_result_code[i]["value"] == newValue) {
								isValid = true;
								break;
							}
						}
					} else {
						for (let i = 0, len = part_result_code.length; i < len; i++) {
							if (part_result_code[i]["key"] == newValue) {
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
				for (let i = 0, len = part_result_code.length; i < len; i++) {
					if (part_result_code[i]["key"] == value) {
						retStr = part_result_code[i]["value"];
						break;
					}
				}
				return retStr == "" ? value : retStr;
			},
		}, {
			headerText : "발주 부품",
			dataField : "order",
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
				type : "ComboBoxRenderer",
				autoCompleteMode : true,
				autoEasyMode : true,
				matchFromFirst : false,
				showEditorBtnOver : false,
				list : part_result_code,
				keyField : "key",
				valueField : "value",
				validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
					let isValid = false;
					if(!fromClipboard) {
						for (let i = 0, len = part_result_code.length; i < len; i++) {
							if (part_result_code[i]["value"] == newValue) {
								isValid = true;
								break;
							}
						}
					} else {
						for (let i = 0, len = part_result_code.length; i < len; i++) {
							if (part_result_code[i]["key"] == newValue) {
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
				for (let i = 0, len = part_result_code.length; i < len; i++) {
					if (part_result_code[i]["key"] == value) {
						retStr = part_result_code[i]["value"];
						break;
					}
				}
				return retStr == "" ? value : retStr;
			},
		} ]
	}, {
		headerText : "중량(g)",
		dataField : "weight",
		dataType : "numeric",
		width : 100,
		formatString : "#.#"
	} ]

	function createAUIGrid510(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : true,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			// 			autoGridHeight : true,
			showRowCheckColumn : true,
			enableCellMerge : true,
			enableFilter : true,
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			editable : true,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			cellColMergeFunction : function(rowIndex, columnIndex, item) {
				if (item.preMerge === true) {
					return true;
				}
				if (item.afterMerge === true) {
					return true;
				}
				return false;
			},
		}
		myGridID510 = AUIGrid.create("#grid510", columnLayout, props);
		AUIGrid.setGridData(myGridID510,
<%=AUIGridUtil.include(oid, "part")%>
	);
	}

	function summaryData(type) {
		const gridData = AUIGrid.getCheckedRowItems(myGridID510);
		if (gridData.length === 0) {
			alert("하나 이상을 선택해주세요.");
			return false;
		}
		const params = {
			gridData : gridData,
			type : type
		};
		logger(params);
		openLayer();
		const url = getCallUrl("/eco/summaryData");
		call(url, null, params, function(data) {
			if (data.result) {

			} else {
				closeLayer()
			}
		});
	}
	
	function reloadData() {
		const oid = "<%=oid%>";
		const dummy = document.querySelector("input[name=dummy]:checked");
		let skip;
		if (dummy !== null) {
			skip = "true";
		} else {
			skip = "false";
		}
		const url = getCallUrl("/eco/reloadData?oid=" + oid + "&skip=" + skip);
		openLayer();
		call(url, null, function(data) {
			logger(data);
			if (data.result) {
				AUIGrid.clearGridData(myGridID510);
				AUIGrid.setGridData(myGridID510, data.list);
			} else {
				alert(data.msg);
			}
			closeLayer();
		}, "GET");
	}

	function save() {
		const editRows = AUIGrid.getEditedRowItems(myGridID510);
		if (editRows.length === 0) {
			alert("수정 사항이 없습니다.");
			return false;
		}

		if (!confirm("저장하시겠습니까?")) {
			return false;
		}

		const url = getCallUrl("/eco/save");
		const params = {
			editRows : editRows
		};
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			} else {
				closeLayer();
			}
		})
	}

	function exportExcel() {
		_export("설변품목 리스트", "설변품목", "설변품목 리스트", [], "");
	}

	function _export(fileName, headerName, sheetName, exceptColumnFields, creator) {
		const date = new Date();
		const year = date.getFullYear();
		const month = (date.getMonth() + 1).toString().padStart(2, '0');
		const day = date.getDate().toString().padStart(2, '0');
		const today = year + "/" + month + "/" + day;
		AUIGrid.exportToXlsx(myGridID510, {
			// 저장하기 파일명
			fileName : fileName,
			progressBar : true,
			sheetName : sheetName,
			exportWithStyle : true,
			exceptColumnFields : exceptColumnFields,
			// 헤더 내용
			headers : [ {
				text : "",
				height : 20
			// 첫행 빈줄
			}, {
				text : headerName,
				height : 36,
				style : {
					fontSize : 20,
					textAlign : "center",
					fontWeight : "bold",
					background : "#DAD9FF"
				}
			}, {
				text : "작성자 : " + creator,
				style : {
					textAlign : "right",
					fontWeight : "bold"
				}
			}, {
				text : "작성일 : " + today,
				style : {
					textAlign : "right",
					fontWeight : "bold"
				}
			}, {
				text : "",
				height : 5,
				style : {
					background : "#555555"
				}
			// 빈줄 색깔 경계 만듬
			} ],
			// 푸터 내용
			footers : [ {
				text : "",
				height : 5,
				style : {
					background : "#555555"
				}
			// 빈줄 색깔 경계 만듬
			}, {
				text : "COPYRIGHT 2023 LUTRONIC",
				height : 24,
				style : {
					textAlign : "right",
					fontWeight : "bold",
					color : "#ffffff",
					background : "#222222"
				}
			} ]
		});
	}
</script>