<%@page import="wt.fc.Persistable"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.workspace.service.WorkspaceHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean view = "view".equals(mode);
boolean create = "create".equals(mode);
boolean update = "update".equals(mode);
%>
<div class="include">
	<input type="button" value="외부 메일 추가" title="외부 메일 추가" class="blue" onclick="popup9();">
	<input type="button" value="외부 메일 삭제" title="외부 메일 삭제" class="red" onclick="deleteRow9();">
	<div id="grid9" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>

	<script type="text/javascript">
		let myGridID9;
		const columns9 = [ {
			dataField : "name",
			headerText : "이름",
			dataType : "string",
			width : 250,
		}, {
			dataField : "email",
			headerText : "이메일",
			dataType : "string",
			style : "aui-left",
		}, {
			dataField : "oid",
			dataType : "string",
			visible : false
		}]

		function createAUIGrid9(columnLayout) {
			const props = {
				headerHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				showAutoNoDataMessage : false,
				showAutoNoDataMessage : false,
				enableRowCheckShiftKey : true,
				enableSorting : false,
				autoGridHeight : true,
				<%if (create || update) {%>
				showStateColumn : true,
				showRowCheckColumn : true,
				<%}%>
			}
			myGridID9 = AUIGrid.create("#grid9", columnLayout, props);
			<%if (view || update) {%>
			AUIGrid.setGridData(myGridID9, <%=WorkspaceHelper.manager.getExternalMail(oid)%>);
			<%}%>
		}
		
		function load() {
			const oid = document.getElementById("oid").value;
			const url = getCallUrl("/workspace/reloadMail?oid="+oid);
			AUIGrid.showAjaxLoader(myGridID9);
			call(url, null, function(data) {
				AUIGrid.removeAjaxLoader(myGridID9);
				if(data.result) {
					AUIGrid.clearGridData(myGridID9);
					AUIGrid.setGridData(myGridID9, data.list);		
				}
			}, "GET");
		}

		function popup9() {
			const url = getCallUrl("/workspace/mail");
			const p = _popup(url, 850, 600, "n");
		}

		function deleteRow9() {
			const checked = AUIGrid.getCheckedRowItems(myGridID9);
			if (checked.length === 0) {
				alert("삭제할 행을 선택하세요.");
				return false;
			}
			
			const data = new Array();
			checked.forEach(function(dd) {
				const item = dd.item;
				data.push(item.link);
			})
			const params = {
				data : data,
				oid : "<%=oid%>"
			};
			const url = getCallUrl("/workspace/removeMail");
			parent.openLayer();
			call(url, params, function(data) {
				if(data.result) {
					load();
				} else {
					alert(data.msg);
				}
				parent.closeLayer();
			})
			
// 			AUIGrid.removeCheckedRows(myGridID9);
		}

		function insert9(arr, callBack) {
			let checker = true;
			let name;
			arr.forEach(function(dd) {
				const rowIndex = dd.rowIndex;
				const item = dd.item;
				const unique = AUIGrid.isUniqueValue(myGridID9, "oid", item.oid);
				if (!unique) {
					name = item.name;
					checker = false;
					return true;
				}
			})
			
			if(!checker) {
				callBack(true, false, name +  " 사용자는 이미 추가 되어있습니다.");
			} else {
				// 저장하는거로?
				const data = new Array();
				arr.forEach(function(dd) {
					const item = dd.item;
					data.push(item.oid);
				})
				const url = getCallUrl("/workspace/mailSave");
				const params = {
					data : data,
					oid : "<%=oid%>"
				};
				parent.openLayer();
				call(url, params, function(data) {
					if(data.result) {
						load();
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				})
			}
		}
	</script>
</div>