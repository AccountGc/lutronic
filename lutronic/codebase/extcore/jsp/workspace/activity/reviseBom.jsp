<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.workspace.dto.EcaDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcaDTO dto = (EcaDTO) request.getAttribute("dto");
ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) request.getAttribute("list");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<style type="text/css">
.merge {
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
					</div>
				</td>
				<td class="right">
					<input type="button" value="품목개정" title="품목개정" onclick="revise();">
					<input type="button" value="품목변경" title="품목변경" class="blue" onclick="replace();">
					<input type="button" value="새로고침" title="새로고침" class="orange" onclick="document.location.reload();">
				</td>
			</tr>
		</table>

		<div id="grid_wrap" style="height: 60px; border-top: 1px solid #3180c3;"></div>
		<script>
			let myGridiD;
			const columns = [ {
				headerText : "개정 전",
				children : [ {
					dataField : "part_number",
					dataType : "string",
					headerText : "품목번호",
					width : 140
				}, {
					dataField : "part_name",
					dataType : "string",
					headerText : "품목명",
				}, {
					dataField : "part_version",
					dataType : "string",
					headerText : "REV",
					width : 80
				}, {
					dataField : "part_creator",
					dataType : "string",
					headerText : "등록자",
					width : 100
				}, {
					dataField : "part_state",
					dataType : "string",
					headerText : "상태",
					width : 100
				} ]
			}, {
				headerText : "개정 후",
				children : [ {
					dataField : "next_number",
					dataType : "string",
					headerText : "품목번호",
					width : 140,
					cellColMerge : true, // 셀 가로 병합 실행
					cellColSpan : 6, // 셀 가로 병합 대상은 6개로 설정
					styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
						if (item.merge === true) {
							return "merge";
						}
						return null;
					}
				}, {
					dataField : "next_version",
					dataType : "string",
					headerText : "REV",
					width : 80
				}, {
					dataField : "next_creator",
					dataType : "string",
					headerText : "등록자",
					width : 100
				}, {
					dataField : "next_state",
					dataType : "string",
					headerText : "상태",
					width : 100
				}, {
					dataField : "epm_number",
					dataType : "string",
					headerText : "주도면",
				}, {
					dataField : "reference",
					dataType : "string",
					headerText : "참조항목",
				} ]
			}, {
				headerText : "BOM",
				children : [ {
					dataField : "number",
					dataType : "string",
					headerText : "BOM 편집",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "BOM 편집",
						onclick : function(rowIndex, columnIndex, value, item) {

						}
					}
				}, {
					dataField : "name",
					dataType : "string",
					headerText : "BOM 비교",
					width : 120,
					renderer : {
						type : "ButtonRenderer",
						labelText : "BOM 비교",
						onclick : function(rowIndex, columnIndex, value, item) {

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
					autoGridHeight : true,
					enableCellMerge : true,

					cellColMergeFunction : function(rowIndex, columnIndex, item) {
						if (item.merge === true) {
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
				const url = getCallUrl("/activity/replace?oid-" + oid);
				_popup(url, 1800, 900, "n");
			}

			function revise() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("개정할 품목을 선택하세요.");
					return false;
				}

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