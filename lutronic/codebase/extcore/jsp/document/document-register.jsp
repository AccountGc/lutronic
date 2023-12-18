<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/dext5editor/js/dext5editor.js"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						문서 일괄 결재
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">일괄 결재 제목</th>
				<td class="indent5">
					<input type="text" name="appName" id="appName" class="width-400">
				</td>
			</tr>
			<tr>
				<th class="lb">일괄 결재 설명</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="6"></textarea>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="추가" title="추가" class="red" onclick="popup();">
					<input type="button" value="삭제" title="삭제" class="blue" onclick="deleteRow90();">
				</td>
			</tr>
		</table>

		<div id="grid_wrap" style="height: 30px; border-top: 1px solid #3180c3;"></div>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<input type="button" value="뒤로" title="뒤로" class="gray" onclick="history.go(-1);">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "interalnumber",
				dataType : "string",
				width : 120,
				headerText : "내부문서번호",
			}, {
				dataField : "number",
				dataType : "string",
				headerText : "문서명",
				style : "aui-left"
			}, {
				dataField : "version",
				dataType : "string",
				headerText : "REV",
				width : 80
			}, {
				dataField : "state",
				dataType : "string",
				headerText : "상태",
				width : 100
			}, {
				dataField : "creator",
				dataType : "string",
				headerText : "등록자",
				width : 100
			}, {
				dataField : "createdDate",
				dataType : "string",
				headerText : "등록일",
				width : 100
			}, {
				dataField : "oid",
				dataType : "string",
				visible : false
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showStateColumn : true,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleRows",
					hoverMode : "singleRow",
					enableMovingColumn : true,
					autoGridHeight : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
			}
			// 등록
			function create() {
				const appName = document.getElementById("appName");
				const description = document.getElementById("description");
				const list = AUIGrid.getGridData(myGridID);

				if (appName.value === "") {
					alert("일괄결재 제목을 입력하세요.");
					appName.focus();
					return false;
				}

				if (list.length === 0) {
					alert("일괄결재 대상을 선택하세요.");
					return false;
				}

				if (!confirm("등록 하시겠습니까?")) {
					return false;
				}

				const params = {
					appName : appName.value,
					description : description.value,
					list : list,
					type : "DOC"
				}
				const url = getCallUrl("/asm/register");
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.href = getCallUrl("/workData/list");
					} else {
						parent.closeLayer();
					}
				})
			}

			function popup() {
				const url = getCallUrl("/doc/popup?method=insert90&multi=true&state=BATCHAPPROVAL");
				_popup(url, 1800, 900, "n");
			}

			function deleteRow90() {
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

			function insert90(arr, callBack) {
				arr.forEach(function(dd) {
					const rowIndex = dd.rowIndex;
					const item = dd.item;
					const unique = AUIGrid.isUniqueValue(myGridID, "oid", item.oid);
					if (unique) {
						AUIGrid.addRow(myGridID, item, rowIndex);
					} else {
						// 중복은 그냥 경고 없이 처리 할지 합의?
						alert(item.number + " 문서는 이미 추가 되어있습니다.");
					}
				})
				callBack(true);
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				toFocus("appName");
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>