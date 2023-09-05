<%@page import="com.e3ps.org.MailUser"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// MailUser u = MailUser.newMailUser();
// u.setEmail("test@gmail.com");
// u.setIsDisable(true);
// u.setName("테스트");
// PersistenceHelper.manager.save(u);
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
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">

		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="200">
				<col width="130">
				<col width="200">
				<col width="130">
				<col width="200">
			</colgroup>
			<tr>
				<th>이름</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-200">
				</td>
				<th>이메일</th>
				<td class="indent5">
					<input type="text" name="email" id="email" class="width-200">
				</td>
				<th>활성화</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="enable" value="true" checked="checked">
						<div class="state p-success">
							<label>
								<b>ON</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="enable" value="false">
						<div class="state p-success">
							<label>
								<b>OFF</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();"> 
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('mail-list');"> 
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('mail-list');"> 
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="검색" title="검색" id="search">
					<input type="button" value="초기화" title="초기화" id="reset">
					<input type="button" value="추가" title="추가" id="add" class="blue">
					<input type="button" value="삭제" title="삭제" id="delete" class="red">
					<input type="button" value="저장" title="저장" id="save" class="blue">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
		<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "이름",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "email",
					headerText : "이메일",
					dataType : "string",
					width : 400,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "enable",
					headerText : "활성화",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					showRowCheckColumn : true,
// 					rowNumHeaderText : "번호",
					fillColumnSizeMode: true,
					showAutoNoDataMessage : true,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : false,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					editable : true,
					rowCheckToRadio : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
			}

			function loadGridData() {
				var params = new Object();
				const field = ["name","email","_psize"];
				params = toField(params, field);
				const enable = $("input[name=enable]:checked").val();
				params.enable = enable;
				var url = getCallUrl("/admin/adminMail");
				call(url, params, function(data) {
					if (data.result) {
						totalPage = Math.ceil(data.total / data.pageSize);
						document.getElementById("sessionid").value = data.sessionid;
						createPagingNavigator(data.curPage);
						AUIGrid.setGridData(myGridID, data.list);
					} else {
						alert(data.msg);
					}
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("mail-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				selectbox("_psize");
				selectbox("rootOid");
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
			$("#add").click(function() {
				var item = new Object();
				AUIGrid.addRow(myGridID, item, 'first');
			});
			
			// 삭제
			$("#delete").click(function() {
				var items = AUIGrid.getCheckedRowItems(myGridID);
				if (items.length == 0) {
					alert("삭제 할 행을 선택하세요.");
				}
				for (var i = 0; i < items.length; i++) {
					AUIGrid.removeRow(myGridID, items[i].rowIndex);
				}
			});
			
			// 저장
			$("#save").click(function() {
				// 추가된 행
				var addedRowItems = AUIGrid.getAddedRowItems(myGridID);
				// 수정된 행
				var editedRowItems = AUIGrid.getEditedRowItems(myGridID);
				// 삭제된 행
				var removedRowItems = AUIGrid.getRemovedItems(myGridID);
				
				if (addedRowItems.length == 0 && editedRowItems.length == 0 && removedRowItems.length == 0) {
					return;
				}
				
				if (!confirm("저장 하시겠습니까?")) {
					return;
				}
				
				var params = new Object();
				params.addRow = addedRowItems;
				params.editRow = editedRowItems;
				params.removeRow = removedRowItems;
				
				const url = getCallUrl("/admin/adminMailSave");
				call(url, params, function(data) {
					if (data.result) {
						alert(data.msg);
						loadGridData();
					} else {
						alert(data.msg);
					}
				});
			});
			
			$("#search").click(function() {
				loadGridData();
			});
			
			$("#reset").click(function() {
				$("input[type=text]").val("");
				$('input:radio[name="enable"]:input[value="true"]').prop('checked',true);
			});
			
		</script>
	</form>
</body>
</html>