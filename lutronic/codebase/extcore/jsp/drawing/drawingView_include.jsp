<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String moduleType = request.getParameter("moduleType");
	String epmType = request.getParameter("epmType");
	String oid = request.getParameter("oid");
	String title = request.getParameter("title");
	String paramName = request.getParameter("paramName");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				주 도면
			</div>
		</td>
	</tr>
</table>
<div id="grid_drawing_Wrap" style="height: 350px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "number",
				headerText : "도면번호",
				dataType : "string",
				width : 120,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
// 					jsCallback : function(rowIndex, columnIndex, value, item) {
// 						const oid = item.oid;
// 						const url = getCallUrl("/doc/view?oid=" + oid);
// 						popup(url, 1600, 800);
// 					}
				},
			}, {
				dataField : "name",
				headerText : "도면명",
				dataType : "string",
				style : "aui-left",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
// 					jsCallback : function(rowIndex, columnIndex, value, item) {
// 						const oid = item.oid;
// 						const url = getCallUrl("/doc/view?oid=" + oid);
// 						popup(url, 1600, 800);
// 					}
				},
			}, {
				dataField : "state",
				headerText : "상태",
				dataType : "string",
				width : 100,
			}, {
				dataField : "version",
				headerText : "Rev.",
				dataType : "string",
				width : 100,
			}, {
				dataField : "creator",
				headerText : "등록자",
				dataType : "string",
				width : 100,
			}, {
				dataField : "description",
				headerText : "수정일",
				dataType : "string",
				width : 100,
			}]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					softRemoveRowMode : false,
					autoGridHeight : true,
				}
				myGridID = AUIGrid.create("#grid_drawing_Wrap", columnLayout, props);
				loadGridData();
				AUIGrid.setGridData(myGridID,);
			}
			
			function loadGridData() {
 				let params = {
 						moduleType: "<%= moduleType %>",
 						oid: "<%= oid %>",
 						title: "<%= title %>",
 						paramName:  "<%= paramName %>"
 				};
 				
				const url = getCallUrl("/drawing/drawingView_include");
// 				const field = [moduleType, oid, title, paramName];
//  				params = toField(params, field);
 				AUIGrid.showAjaxLoader(myGridID);
//  				parent.openLayer();
 				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
// 						document.getElementById("sessionid").value = data.sessionid;
// 						document.getElementById("curPage").value = data.curPage;
//							document.getElementById("lastNum").value = data.list.length;
						AUIGrid.setGridData(myGridID, data.list);
					} else {
						alert(data.msg);
					}
// 					parent.closeLayer();
				});
			}
</script>