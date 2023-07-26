<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
<div id="grid_wrap" style="height: 350px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "Role",
				headerText : "역할",
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
				dataField : "department",
				headerText : "부서명",
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
				dataField : "name",
				headerText : "이름",
				dataType : "string",
				width : 100,
			}, {
				dataField : "duty",
				headerText : "직위",
				dataType : "string",
				width : 100,
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					softRemoveRowMode : false,
					autoGridHeight : true,
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.setGridData(myGridID,);
			}
			
			function loadGridData() {
 				let params = new Object();
				const url = getCallUrl("/development/userList");
				const field = ["oid"];
 				params = toField(params, field);
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