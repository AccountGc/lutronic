<%@page import="com.e3ps.change.beans.ROOTData"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
List<ROOTData> rootList = (List<ROOTData>) request.getAttribute("rootList");
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
				<col width="50">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>ROOT :</th>
				<td class="indent5">
					<select name="rootOid" id="rootOid" class="width-200">
						<option value="">선택</option>
						<%
						if(rootList.size()>0){
							for (ROOTData data : rootList) {
							%>
							<option value="<%=data.getOid()%>"><%=data.getName()%></option>
							<%
							}
						}
						%>
					</select>
				</td>
				<td class="right">
					<input type="button" value="Root 추가" title="Root 추가" class="blue" id="createRootDefinition">
					<input type="button" value="Root 수정" title="Root 수정" class="" id="updateRootDefinition">
					<input type="button" value="Root 삭제" title="Root 삭제" class="red" id="deleteRootDefinition">
					<input type="button" value="활동추가" title="활동추가" class="blue" id="createActivity">
					<input type="button" value="활동삭제" title="활동삭제" class="red" id="deleteActivity">
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();"> 
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('changeActivity-list');"> 
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('changeActivity-list');"> 
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
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
					dataField : "stepName",
					headerText : "단계",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name",
					headerText : "활동명",
					dataType : "string",
					width : 400,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "activityName",
					headerText : "활동구분",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "departName",
					headerText : "담당부서",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "activeUserName",
					headerText : "담당자",
					dataType : "string",
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "enabled",
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
					rowCheckToRadio : true,
					enableRowCheckShiftKey : true
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
				const field = ["rootOid","_psize"];
				params = toField(params, field);
				
				var url = getCallUrl("/admin/changeActivityList");
				call(url, params, function(data) {
					if (data.result) {
						totalPage = Math.ceil(data.total / data.pageSize);
						document.getElementById("sessionid").value = data.sessionid;
						createPagingNavigator(data.curPage);
						AUIGrid.setGridData(myGridID, data.list);
						setButtonControl();
					} else {
						alert(data.msg);
					}
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("changeActivity-list");
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
			
			// 버튼 제어
			function setButtonControl(){
				if($("#rootOid").val() ==""){
					$("#updateRootDefinition").hide();
					$("#deleteRootDefinition").hide();
					$("#createActivity").hide();
					$("#deleteActivity").hide();
				}else{
					$("#updateRootDefinition").show();
					$("#deleteRootDefinition").show();
					$("#createActivity").show();
					$("#deleteActivity").show();
				}
			}
			
			// Root 추가
			$("#createRootDefinition").click(function() {
				const url = getCallUrl("/admin/createRootDefinition");
				_popup(url, 600, 500, "n");
			})
			
			// Root 수정
			$("#updateRootDefinition").click(function() {
				const url = getCallUrl("/admin/updateRootDefinition")+"?oid="+$("#rootOid").val();
				_popup(url, 600, 500, "n");
			})
			
			// Root 삭제
			$("#deleteRootDefinition").click(function() {
				var gridList = AUIGrid.getGridData(myGridID);
				if(gridList.length>0){
					alert("활동이 있을경우 삭제할 수 없습니다.");
					return;
				}
				
				if (!confirm("삭제 하시겠습니까?")) {
					return false;
				}

				let params = new Object();
				const oid = $("#rootOid").val();
				params.oid = oid;
				const url = getCallUrl("/admin/deleteRootDefinition");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						location.reload();
					}else{
						alert(data.msg);
					}
				});
			})
			
			// 활동 추가 
			$("#createActivity").click(function() {
				var url = getURLString("admin", "createActivityDefinition", "do") + "?oid="+$("#rootOid").val();
				openOtherName(url,"window","500","500","status=no,scrollbars=yes,resizable=yes");
			})
			
			// 활동 삭제
			$("#deleteActivity").click(function() {
				var checked = documentListGrid.getCheckedRows(1);
				if(checked == '') {
					alert("선택된 데이터가 없습니다.");
					return;
				}
				
				if (!confirm("삭제하시겠습니까?")){
					return;
				}
				var array = checked.split(",");
				var returnArr = new Array();
				var deleteOid ="";
				for(var i =0; i < array.length; i++) {
					var oid = array[i];	
					deleteOid = deleteOid + oid+","
				}
				
				$("#deleteOid").val(deleteOid);
				
				var form = $("form[name=admin_listChangeActivity]").serialize();
				var url	= getURLString("admin", "deleteActivityDefinition", "do");
				
				$.ajax({
					type:"POST",
					url: url,
					data:form,
					dataType:"json",
					async: true,
					cache: false,
					error: function(data) {
						alert("삭제 오류 발생");
					},
					success:function(data){
						if(data.result) {
							lfn_Search();
						}else {
							alert(data.msg);
						}
					}
				});
			})
			
			// Root 변경 시
			$("#rootOid").change(function() {
				loadGridData();
			})
		</script>
	</form>
</body>
</html>