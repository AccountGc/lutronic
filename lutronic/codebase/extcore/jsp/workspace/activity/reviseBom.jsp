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
				<td class="indent5" colspan="3">
					<input type="text" name="reassignUser" id="reassignUser">
					<input type="hidden" name="reassignUserOid" id="reassignUserOid">
					<input type="button" title="위임" value="위임" onclick="reassign();">
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
						<input type="button" value="그룹저장" title="그룹저장" onclick="saveGroup();">
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
					logger(value);
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
					width : 100
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
				}, {
					dataField : "epm_number",
					dataType : "string",
					headerText : "주도면",
					editable : false,
				}, {
					dataField : "reference",
					dataType : "string",
					editable : false,
					headerText : "참조항목",
				} ]
			}, {
				headerText : "BOM",
				children : [ {
					dataField : "",
					dataType : "string",
					editable : false,
					headerText : "BOM 편집",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "BOM 편집",
						onclick : function(rowIndex, columnIndex, value, item) {
							const next_oid = item.next_oid;
							alert(next_oid);
							if (next_oid === "") {
								return false;
							}
						}
					}
				}, {
					dataField : "",
					dataType : "string",
					editable : false,
					headerText : "BOM 비교",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "BOM 비교",
						onclick : function(rowIndex, columnIndex, value, item) {
							const oid = item.part_oid;
							const url = getCallUrl("/bom/view?oid=" + oid);
							_popup(url, "", "", "f");
						}
					}
				} ]
			} ]

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
					fixedColumnCount : 1,
					editableOnFixedCell : true,
					enableCellMerge : true,
					editable : true,
					// 					rowCheckableFunction : function(rowIndex, isChecked, item) {
					// 						if (item.part_state !== "승인됨") {
					// 							return false;
					// 						}
					// 						return true;
					// 					},

					// 					rowCheckDisabledFunction : function(rowIndex, isChecked, item) {
					// 						if (item.part_state !== "승인됨") {
					// 							return false; // false 반환하면 disabled 처리됨
					// 						}
					// 						return true;
					// 					},

					cellColMergeFunction : function(rowIndex, columnIndex, item) {
						if (item.preMerge === true) {
							return true;
						}
						if (item.afterMerge === true) {
							return true;
						}
						return false;
					},
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID,
		<%=JSONArray.fromObject(list)%>
			);
			}

			function complete() {
				const data = AUIGrid.getGridData(myGridID);
				let merge = false;
				for (let i = 0; i < data.length; i++) {
					if (data[i].merge === true) {
						merge = true;
						break;
					}
				}

				if (merge) {
					alert("개정작업을 안한 품목이 존재합니다.");
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
					secondarys : secondarys
				};
				logger(params);
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
				logger(params);
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
				logger(editRows);
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
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>