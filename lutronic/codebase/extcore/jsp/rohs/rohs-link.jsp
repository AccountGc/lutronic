<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td>
					<input type="button" value="등록" title="등록" onclick="saveBtn();"> 
					<input type="button" value="추가" title="추가" class="blue" onclick="addBtn();"> 
					<input type="button" value="삭제" title="삭제" class="red" onclick="deleteBtn();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 370px; border-top: 1px solid #3180c3; width: 50%;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "partNumber",
				headerText : "모 부품코드",
				dataType : "string",
				width : 180,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "rohsNumber",
				headerText : "자 물질코드",
				dataType : "string",
				width : 180,
				filter : {
					showIcon : true,
					inline : true
				},
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : false,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					showRowCheckColumn : true,
					fillColumnSizeMode: true,
					editable : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				auiReadyHandler();
			}
			
			function auiReadyHandler() {
				var item = new Object();
				AUIGrid.addRow(myGridID, item, 'last');
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
			});

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			document.addEventListener("click", function(event) {
				hideContextMenu();
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
			
			// 추가
			function addBtn(){
				var item = new Object();
				AUIGrid.addRow(myGridID, item, 'last');
			}
			
			// 삭제
			function deleteBtn(){
				AUIGrid.removeCheckedRows(myGridID);
			}
			
			// 저장
			function saveBtn(){
				var gridList = AUIGrid.getGridData(myGridID);
				for(var i=0; i<gridList.length; i++){
					if(isEmpty(gridList[i].partNumber)){
						alert("부품코드가 입력되지 않았습니다.");
						return;
					}
					if(isEmpty(gridList[i].rohsNumber)){
						alert("물질코드가 입력되지 않았습니다.");
						return;
					}
				}
				
				if (!confirm("등록 하시겠습니까?")){
					return;
				}
				
				let params = new Object();
				params.gridList = gridList;
				const url = getCallUrl("/rohs/link");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						location.reload();
					}else{
						alert(data.msg);
					}
				});
			}
		</script>
	</form>
</body>
</html>