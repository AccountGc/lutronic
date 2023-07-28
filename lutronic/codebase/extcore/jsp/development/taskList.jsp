<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="grid_taskWrap" style="height: 350px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
			let myGridID2;
			const columns2 = [ {
				dataField : "name",
				headerText : "TASK명",
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
				dataField : "state",
				headerText : "상태",
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
				dataField : "description",
				headerText : "설명",
				dataType : "string",
				width : 100,
			} ]

			function createAUIGrid2(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					softRemoveRowMode : false,
					autoGridHeight : true,
				}
				myGridID2 = AUIGrid.create("#grid_taskWrap", columnLayout, props);
				loadGridData2();
				AUIGrid.setGridData(myGridID2,);
			}
			
			function loadGridData2() {
 				let params = new Object();
				const url = getCallUrl("/development/taskList");
				const field = ["oid"];
 				params = toField(params, field);
 				AUIGrid.showAjaxLoader(myGridID2);
//  				parent.openLayer();
 				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID2);
					if (data.result) {
// 						document.getElementById("sessionid").value = data.sessionid;
// 						document.getElementById("curPage").value = data.curPage;
//							document.getElementById("lastNum").value = data.list.length;
						AUIGrid.setGridData(myGridID2, data.list);
					} else {
						alert(data.msg);
					}
// 					parent.closeLayer();
				});
			}
	</script>