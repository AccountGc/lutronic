<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> sectionList = (ArrayList<NumberCode>) request.getAttribute("sectionList");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
List<Map<String,String>> lifecycleList = (List<Map<String,String>>) request.getAttribute("lifecycleList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
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
		<input type="hidden" name="sessionid" id="sessionid"> 
		<input type="hidden" name="lastNum" id="lastNum"> 
		<input type="hidden" name="curPage" id="curPage">

		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>ECPR 번호</th>
				<td class="indent5"><input type="text" name="number" id="number" class="width-300"></td>
				<th>ECPR 제목</th>
				<td class="indent5"><input type="text" name="name" id="name" class="width-300"></td>
				<th>상태</th>
				<td class="indent5">
					<select name="state" id="state" class="width-200" >
						<option value="">선택</option>
						<%
                        for (Map<String,String> lifecycle : lifecycleList) {
                        %>
                        <option value="<%=lifecycle.get("code") %>"><%=lifecycle.get("name")%></option>
                        <%
                        }
                        %>
					</select>
				</td>
			</tr>
			<tr>
				<th>등록자</th>
				<td class="indent5"><input type="text" name="creator" id="creator" data-multi="false" class="width-200"> <input type="hidden" name="creatorOid" id="creatorOid"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')"></td>
				<th>등록일</th>
				<td class="indent5"><input type="text" name="createdFrom" id="createdFrom" class="width-100"> ~ <input type="text" name="createdTo" id="createdTo" class="width-100"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"
					onclick="clearFromTo('createdFrom', 'createdTo')"></td>
				<th>승인일</th>
				<td class="indent5"><input type="text" name="approveFrom" id="approveFrom" class="width-100"> ~ <input type="text" name="approveTo" id="approveTo" class="width-100"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"
					onclick="clearFromTo('approveFrom', 'approveTo')"></td>
			</tr>
			<tr>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="writer" id="writer" data-multi="false" class="width-200">
					<input type="hidden" name="writerOid" id="writerOid"> 
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
				<th>작성부서</th>
				<td class="indent5">
					<select name="createDepart" id="createDepart" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode deptcode : deptcodeList) {
						%>
						<option value="<%=deptcode.getCode() %>"><%=deptcode.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>작성일</th>
				<td class="indent5"><input type="text" name="writedFrom" id="writedFrom" class="width-100"> ~ <input type="text" name="writedTo" id="writedTo" class="width-100"> <img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"
					onclick="clearFromTo('writedFrom', 'writedTo')"></td>
				
			</tr>
			<tr>
				<th>제안자</th>
				<td class="indent5">
					<input type="text" name="proposer" id="proposer" data-multi="false" class="width-200">
					<input type="hidden" name="proposerOid" id="proposerOid"> 
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
				<th>변경구분</th>
				<td class="indent5">
					<select name="changeSection" id="changeSection" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode section : sectionList) {
						%>
						<option value="<%=section.getCode() %>"><%=section.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th class="req lb">제품명</th>
				<td class="indent5" >
					<select name="model" id="model" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode model : modelList) {
						%>
						<option value="<%=model.getCode()%>"><%=model.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();"> 
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('ecpr-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('ecpr-list');"> 
					<input type="button" value="등록" title="등록" class="blue" id="createBtn">
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select> 
					<input type="button" value="검색" title="검색" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<div id="grid_wrap" style="height: 570px; border-top: 1px solid #3180c3;"></div> 
		<%@include file="/extcore/jsp/common/aui-context.jsp"%>
		<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "number",
					headerText : "ECPR 번호",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/ecpr/view?oid=" + oid);
							popup(url, 1600, 800);
						}
					},
				}, {
					dataField : "name",
					headerText : "ECPR 제목",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/ecpr/view?oid=" + oid);
							popup(url, 1600, 800);
						}
					},
				}, {
					dataField : "changeSection",
					headerText : "변경구분",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createDepart",
					headerText : "작성부서",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "writer",
					headerText : "작성자",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "writeDate",
					headerText : "작성일",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "creator",
					headerText : "등록자",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createdDate_txt",
					headerText : "등록일",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
		            headerHeight : 30,
		            showRowNumColumn : true,
		            showRowCheckColumn : true,
		            rowNumHeaderText : "번호",
		            showAutoNoDataMessage : false,
		            selectionMode : "multipleCells",
		            enableMovingColumn : true,
		            enableFilter : true,
		            showInlineFilter : true,
		            useContextMenu : true,
		            enableRightDownFocus : true,
		            filterLayerWidth : 320,
		            filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
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
				$("input[name=sessionid").val(0);
				let params = new Object();
				const url = getCallUrl("/ecpr/list");
				const field = ["_psize","name","number", "createdFrom", "createdTo", "creator", "state", "writedFrom", "writedTo", "approveFrom", "approveTo", "createDepart", "writer", "proposer", "model", "changeSection"];
				params = toField(params, field);
				AUIGrid.showAjaxLoader(myGridID);
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
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
				toFocus("number");
				const columns = loadColumnLayout("ecpr-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("state");
				finderUser("creator");
				finderUser("writer");
				finderUser("proposer");
				twindate("created");
				twindate("approve");
				twindate("writed");
				selectbox("_psize");
				selectbox("changeSection");
				selectbox("model");
				selectbox("createDepart");
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
			
			// 등록
			$("#createBtn").click(function(){
				location.href = getCallUrl("/ecpr/create");
			});
		</script>
	</form>
</body>
</html>