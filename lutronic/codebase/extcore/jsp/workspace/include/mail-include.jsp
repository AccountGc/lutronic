<%@page import="wt.fc.Persistable"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean create = "create".equals(mode);
boolean update = "update".equals(mode);
%>
<div class="include">
	<input type="button" value="외부 메일 추가" title="외부 메일 추가" class="blue" onclick="popup9();">
	<input type="button" value="외부 메일 삭제" title="외부 메일 삭제" class="red" onclick="deleteRow9();">
	<div id="grid9" style="height: 100px; border-top: 1px solid #3180c3; margin: 5px;"></div>
	
	<script type="text/javascript">
		let myGridID9;
		const columns9 = [ {
			dataField : "name",
			headerText : "이름",
			dataType : "name",
		}, {
			dataField : "type",
			headerText : "이메일",
			dataType : "string",
		}, {
			dataField : "oid",
			dataType : "string",
			visible : false
		}]

		function createAUIGrid9(columnLayout) {
			const props = {
				headerHeight : 30,
				fillColumnSizeMode : false,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				showAutoNoDataMessage : false,
				enableSorting : false,
				softRemoveRowMode : false,
				selectionMode : "multipleCells",
				<%if (create || update) {%>
				showStateColumn : true,
				showRowCheckColumn : true,
				<%}%>
				enableFilter : true,
				autoGridHeight : true
			}
			myGridID9 = AUIGrid.create("#grid9", columnLayout, props);
		}

		function popup9() {
			const url = getCallUrl("/workspace/mail");
			const p = _popup(url, 1400, 860, "n");
		}

		function deleteRow9() {
			const checked = AUIGrid.getCheckedRowItems(myGridID9);
			for (let i = checked.length - 1; i >= 0; i--) {
				const rowIndex = checked[i].rowIndex;
				AUIGrid.removeRow(myGridID8, rowIndex);
			}
		}

		function insert9(arr, callBack) {
			arr.forEach(function(dd) {
				const rowIndex = dd.rowIndex;
				const item = dd.item;
				const unique = AUIGrid.isUniqueValue(myGridID9, "oid", item.oid);
				logger(item);
				if (unique) {
					AUIGrid.addRow(myGridID9, item, rowIndex);
				} else {
					// 중복은 그냥 경고 없이 처리 할지 합의?
					alert(item.name + " 메일은 이미 추가 되어있습니다.");
				}
			})
			callBack(true);
		}
	</script>
</div>