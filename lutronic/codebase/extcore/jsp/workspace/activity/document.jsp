<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.workspace.dto.EcaDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcaDTO dto = (EcaDTO) request.getAttribute("dto");
JSONArray docList = dto.getDocList();
boolean complete = docList.size() > 0;
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
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
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
					<input type="button" value="직접등록" title="직접등록" class="blue" onclick="link();">
					<input type="button" value="링크등록" title="링크등록" onclick="popup00();">
				</td>
			</tr>
		</table>

		<div id="grid_wrap" style="height: 30px; border-top: 1px solid #3180c3;"></div>

		<script type="text/javascript">
			let myGridiD;
			const columns = [ {
				dataField : "number",
				dataType : "string",
				headerText : "문서번호",
				width : 150
			}, {
				dataField : "name",
				dataType : "string",
				headerText : "문서제목",
				style : "aui-left"
			}, {
				dataField : "number",
				dataType : "string",
				headerText : "문서번호",
				width : 150
			}, {
				dataField : "version",
				dataType : "string",
				headerText : "REV",
				width : 100
			}, {
				dataField : "state",
				dataType : "string",
				headerText : "상태",
				width : 100
			}, {
				dataField : "creator",
				dataType : "string",
				headerText : "작성자",
				width : 100
			}, {
				headerText : "",
				dataType : "string",
				width : 120,
				renderer : {
					type : "ButtonRenderer",
					labelText : "삭제",
					onclick : function(rowIndex, columnIndex, value, item) {
						if (!confirm("삭제 하시겠습니까?")) {
							return false;
						}
						const oid = item.oid;
						const url = getCallUrl("/activity/deleteLink?oid=" + oid);
						parent.openLayer();
						call(url, null, function(data) {
							alert(data.msg);
							if (data.result) {
								document.location.reload();
							}
							parent.closeLayer();
						}, "DELETE");
					}
				}
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					enableRowCheckShiftKey : true,
					autoGridHeight : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID,
		<%=docList%>
			);
			}

			// 추가 버튼 클릭 시 팝업창 메서드
			function popup00() {
				const url = getCallUrl("/doc/popup?method=insert00&multi=true");
				_popup(url, 1800, 900, "n");
			}

			function insert00(arr, callBack) {
				const list = new Array();
				arr.forEach(function(dd) {
					const item = dd.item;
					list.push(item.oid);
				})

				const oid = document.getElementById("oid").value;
				const url = getCallUrl("/activity/saveLink");
				const params = {
					list : list,
					oid : oid
				}
				logger(params);
				parent.openLayer();
				call(url, params, function(data) {
					const msg = data.msg;
					if (data.result) {
						document.location.reload();
						callBack(true, true, msg);
					}
					parent.closeLayer();
				})
			}

			function complete() {
		<%if (!complete) {%>
			alert("등록된 산출물이 하나도 없습니다.");
				return false;
		<%}%>
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

			function link() {
				const oid = document.getElementById("oid").value;
				const url = getCallUrl("/doc/link?oid=" + oid);
				_popup(url, 1600, 800, "n");
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