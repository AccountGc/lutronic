<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.workspace.dto.EcaDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcaDTO dto = (EcaDTO) request.getAttribute("dto");
ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) request.getAttribute("list");
JSONArray clist = (JSONArray) request.getAttribute("clist");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<style type="text/css">
.aui-red {
	font-weight: bold !important;
	color: red !important;
}

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
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
</head>
<body>
	<form>
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						업무 기본정보
					</div>
				</td>
				<td class="right">
					<input type="button" title="업무완료" value="업무완료" class="red" onclick="complete();">
				</td>
			</tr>
		</table>
		<!-- 기본 정보 -->
		<table class="view-table">
			<colgroup>
				<col width="130">
				<col width="450">
				<col width="130">
				<col width="450">
			</colgroup>
			<tr>
				<th class="lb">EO/ECO 번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
				<th>작업자</th>
				<td class="indent5"><%=dto.getActivityUser_txt()%></td>
			</tr>
			<tr>
				<th class="lb">EO/ECO 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>도착일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
				<th>완료 예정일</th>
				<td class="indent5"><%=dto.getFinishDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">업무위임</th>
				<td class="indent5">
					<input type="text" name="reassignUser" id="reassignUser">
					<input type="hidden" name="reassignUserOid" id="reassignUserOid">
					<input type="button" title="위임" value="위임" onclick="reassign();">
				</td>
				<th>ECN 담당자</th>
				<td class="indent5">
					<input type="text" name="ecnUser" id="ecnUser">
					<input type="hidden" name="ecnUserOid" id="ecnUserOid">
				</td>
			</tr>
			<tr>
				<th class="lb">의견</th>
				<td class="indent5" colspan="3">
					<textarea name="description" id="description" rows="6"></textarea>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						산출물
						<input type="button" value="저장" title="저장" onclick="saveGroup();">
					</div>
				</td>
				<td class="right">
					<input type="button" value="이전품목" title="이전품목" class="red" onclick="prePart();">
					<input type="button" value="품목개정" title="품목개정" onclick="revise();">
					<input type="button" value="품목변경" title="품목변경" class="blue" onclick="replace();">
					<input type="button" value="새로고침" title="새로고침" class="orange" onclick="document.location.reload();">
				</td>
			</tr>
		</table>

		<div id="grid_wrap" style="height: 470px; border-top: 1px solid #3180c3;"></div>
		<script>
			let myGridiD;
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
			const list =
		<%=clist%>
			const columns = [ {
				headerText : "그룹핑",
				width : 150,
				dataField : "group",
				renderer : {
					type : "IconRenderer",
					iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
					iconHeight : 16,
					iconPosition : "aisleRight",
					iconTableRef : { // icon 값 참조할 테이블 레퍼런스
						"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
					},
					onClick : function(event) {
						// 아이콘을 클릭하면 수정으로 진입함.
						AUIGrid.openInputer(event.pid);
					}
				},
				editRenderer : {
					type : "DropDownListRenderer",
					showEditorBtn : false,
					showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기
					multipleMode : true, // 다중 선택 모드(기본값 : false)
					showCheckAll : true, // 다중 선택 모드에서 전체 체크 선택/해제 표시(기본값:false);
					list : list,
					keyField : "oid",
					valueField : "number",
				},
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
					if (value !== undefined) {
						let retStr = "";
						const valueArr = value.split(", "); // 구분자 기본값은 ", " 임.
						const tempValueArr = [];

						for (let i = 0, len = list.length; i < len; i++) {
							if (valueArr.indexOf(list[i]["oid"]) >= 0) {
								tempValueArr.push(list[i]["number"]);
							}
						}
						return tempValueArr.sort().join(", "); // 정렬시켜서 보여주기.
					}
				},
			}, {
				headerText : "선구매<br>여부",
				dataField : "preOrder",
				dataType : "boolean",
				width : 80,
				editable : false,
				renderer : {
					type : "CheckboxEditRenderer",
					editable : true
				}
			}, {
				headerText : "개정 전",
				children : [ {
					dataField : "part_number",
					dataType : "string",
					headerText : "품목번호",
					width : 140,
					editable : false,
					cellColMerge : true, // 셀 가로 병합 실행
					cellColSpan : 5, // 셀 가로 병합 대상은 6개로 설정
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
					editable : false,
					width : 200
				}, {
					dataField : "part_version",
					dataType : "string",
					headerText : "REV",
					width : 80
				}, {
					dataField : "part_creator",
					dataType : "string",
					headerText : "등록자",
					editable : false,
					width : 100
				}, {
					dataField : "part_state",
					dataType : "string",
					headerText : "상태",
					editable : false,
					width : 100,
					styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
						if (value == "승인됨") {
							return "aui-red";
						}
						return null;
					}

				} ]
			}, {
				headerText : "개정 후",
				children : [ {
					dataField : "next_number",
					dataType : "string",
					headerText : "품목번호",
					editable : false,
					width : 140,
					cellColMerge : true, // 셀 가로 병합 실행
					cellColSpan : 7, // 셀 가로 병합 대상은 6개로 설정
					styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
						if (item.afterMerge === true) {
							return "afterMerge";
						}
						return null;
					}
				}, {
					dataField : "next_name",
					dataType : "string",
					headerText : "품목명",
					editable : false,
					width : 200
				}, {
					dataField : "next_version",
					dataType : "string",
					headerText : "REV",
					editable : false,
					width : 80
				}, {
					dataField : "next_creator",
					dataType : "string",
					headerText : "등록자",
					editable : false,
					width : 100
				}, {
					dataField : "next_state",
					dataType : "string",
					editable : false,
					headerText : "상태",
					width : 100
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
						for (let i = 0, len = part_state_code.length; i < len; i++) {
							if (part_state_code[i]["value"] == newValue) {
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
					headerText : "납풍 장비",
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
							for (let i = 0, len = part_result_code.length; i < len; i++) {
								if (part_result_code[i]["value"] == newValue) {
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
							for (let i = 0, len = part_result_code.length; i < len; i++) {
								if (part_result_code[i]["value"] == newValue) {
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
							for (let i = 0, len = part_result_code.length; i < len; i++) {
								if (part_result_code[i]["value"] == newValue) {
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
							for (let i = 0, len = part_result_code.length; i < len; i++) {
								if (part_result_code[i]["value"] == newValue) {
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
				dataField : "",
				dataType : "string",
				width : 100
			} ];

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					fillColumnSizeMode : false,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					enableSorting : false,
					softRemoveRowMode : false,
					selectionMode : "multipleCells",
					showRowCheckColumn : true,
					enableFilter : true,
					fixedColumnCount : 2,
					editableOnFixedCell : true,
					enableCellMerge : true,
					editable : true,
					cellColMergeFunction : function(rowIndex, columnIndex, item) {
						if (item.preMerge === true) {
							return true;
						}
						if (item.afterMerge === true) {
							return true;
						}
						return false;
					},
					useContextMenu : true,
					enableRightDownFocus : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID,
		<%=JSONArray.fromObject(list)%>
			);
				AUIGrid.bind(myGridID, "contextMenu", function(event) {
					const menu = [ {
						label : "주 도면 및 참조도면",
						callback : auiContextHandler
					}, {
						label : "BOM 에디터",
						callback : auiContextHandler
					}, {
						label : "BOM 비교",
						callback : auiContextHandler
					} ];
					return menu;
				});
			}

			function auiContextHandler(event) {
				const item = event.item;
				const oid = document.getElementById("oid").value;
				const next_oid = item.next_oid; // 개정후의 데이터를 보여주는거로
				if (next_oid === "") {
					alert("개정 후 데이터가 없습니다.");
					return false;
				}
				switch (event.contextIndex) {
				case 0:
					// 도면
					_popup(getCallUrl("/activity/reference?oid=" + next_oid), 1200, 500, "n");
					break;
				case 1:
					// bom 에디터
					_popup(getCallUrl("/activity/editor?oid=" + next_oid + "&eoid=" + oid), 1800, 750, "n");
					break;
				case 2:
					// bom 비교
					break;
				}
			};

			function complete() {
				const data = AUIGrid.getGridData(myGridID);

				if (data.length === 0) {
					alert("설변 대상 품목이 하나도 없습니다.");
					return false;
				}

				for (let i = 0; i < data.length; i++) {
					const group = data[i].group;
					if (group === "") {
						alert("그룹핑이 안된 품목들이 존재합니다.");
						return false;
					}
				}
				const ecnUserOid = document.getElementById("ecnUserOid").value;
				if (ecnUserOid === "") {
					alert("ECN 담당자를 선택하세요.");
					return false;
				}

				const oid = document.getElementById("oid").value;
				const description = document.getElementById("description").value;
				if (!confirm("설변활동을 완료 하시겠습니까?")) {
					return false;
				}
				const secondarys = toArray("secondarys");
				const url = getCallUrl("/activity/complete");
				const params = {
					oid : oid,
					description : description,
					secondarys : secondarys,
					ecnUserOid : ecnUserOid
				};
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.href = getCallUrl("/activity/eca");
					}
					parent.closeLayer();
				})
			}

			// 추가 버튼 클릭 시 팝업창 메서드
			function replace() {
				const oid = document.getElementById("oid").value;
				const url = getCallUrl("/activity/replace?oid=" + oid);
				_popup(url, 1800, 900, "n");
			}

			// 이전 부품 추가..
			function prePart() {
				const oid = document.getElementById("oid").value;
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("이전품목을 추가할 품목을 선택하세요.");
					return false;
				}

				if (checkedItems.length > 1) {
					alert("이전품목을 추가할 품목은 하나만 선택이 가능합니다.");
					return false;
				}

				const item = checkedItems[0].item;
				const prev = item.prev;
				if (prev === false) {
					const number = item.next_number;
					alert(number + " 품목은 이전품목을 추가할 수 있는 품목이 아닙니다.");
					return false;
				}

				const url = getCallUrl("/part/popup?method=prev&multi=false");
				_popup(url, 1600, 800, "n");
			}

			function prev(arr, callBack) {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				const oid = checkedItems[0].item.next_oid;
				const item = arr[0].item;
				const part_oid = item.part_oid;
				const url = getCallUrl("/activity/prev");
				const soid = document.getElementById("oid").value
				const params = {
					prev : part_oid,
					after : oid,
					oid : soid
				};
				parent.openLayer();
				call(url, params, function(data) {
					callBack(true, true, data.msg);
					if (data.result) {
						document.location.reload();
					}
					parent.closeLayer();
				})
			}
			// 개정부품
			function revise() {
				const oid = document.getElementById("oid").value;
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("개정할 품목을 선택하세요.");
					return false;
				}

				const arr = new Array();
				const link = new Array();
				for (let i = 0; i < checkedItems.length; i++) {
					const item = checkedItems[i].item;
					const after = item.after;
					if (after === false) {
						const number = item.next_number;
						alert(number + " 품목은 개정 대상 품목이 아닙니다.");
						return false;
					}
					arr.push(item.part_oid);
					link.push(item.link_oid);
				}

				const url = getCallUrl("/activity/revise?oid=" + oid);
				const panel = _popup(url, 1500, 700, "n");
				panel.list = arr;
				panel.link = link;
			}

			function saveGroup() {
				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				const editRows = AUIGrid.getEditedRowItems(myGridID);

				if (editRows.length === 0) {
					alert("수정 내역이 없습니다.");
					return false;
				}

				for (let i = 0; i < editRows.length; i++) {
					const next_oid = editRows[i].next_oid;
					if (next_oid === "") {
						alert("개정 후 품목이 없습니다.");
						return false;
					}
				}

				const url = getCallUrl("/activity/saveGroup");
				const oid = document.getElementById("oid").value;
				parent.openLayer();
				const params = {
					editRows : editRows,
					oid : oid
				};
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.reload();
					}
					parent.closeLayer();
				})
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				finderUser("reassignUser");
				finderUser("ecnUser");
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>