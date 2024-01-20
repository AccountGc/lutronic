<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page import="wt.doc.DocumentType"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> preserationList = (ArrayList<NumberCode>) request.getAttribute("preserationList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
List<Map<String, String>> lifecycleList = (List<Map<String, String>>) request.getAttribute("lifecycleList");
ArrayList<Map<String, String>> classTypes1 = (ArrayList<Map<String, String>>) request.getAttribute("classTypes1");
WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
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
		<input type="hidden" name="sessionName" id="sessionName" value="<%=user.getFullName()%>">
		<input type="hidden" name="oid" id="oid">
		<!-- 다운로드 링크 위한 프레임 -->
		<!-- 		<iframe id="download" name="download" style="display: none;"></iframe> -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						문서 검색
					</div>
				</td>
			</tr>
		</table>

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
				<th>문서 분류</th>
				<td class="indent5">
					<input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT%>">
					<span id="locationText"><%=DocumentHelper.DOCUMENT_ROOT%></span>
				</td>
				<th>문서번호</th>
				<td class="indent5">
					<input type="text" name="number" id="number" class="width-300">
				</td>
				<th>문서명</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
			</tr>
			<tr>
				<th class="lb req">대분류</th>
				<td class="indent5">
					<select name="classType1" id="classType1" class="width-200" onchange="first(this);">
						<option value="">선택</option>
						<%
						for (Map<String, String> map : classTypes1) {
							String value = map.get("value");
							String name = map.get("name");
							String clazz = map.get("clazz");
						%>
						<option value="<%=value%>" data-clazz="<%=clazz%>"><%=name%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>중분류</th>
				<td class="indent5">
					<select name="classType2" id="classType2" class="width-300" onchange="second(this);">
						<option value="">선택</option>
					</select>
				</td>
				<th>소분류</th>
				<td class="indent5">
					<select name="classType3" id="classType3" class="width-300">
						<option value="">선택</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>등록자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" data-multi="false" class="width-200">
					<input type="hidden" name="creatorOid" id="creatorOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
				<th>등록일</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
				</td>
				<th>상태</th>
				<td class="indent5">
					<select name="state" id="state" class="width-200">
						<option value="">선택</option>
						<%
						for (Map<String, String> lifecycle : lifecycleList) {
							if (!lifecycle.get("code").equals("TEMPRARY")) {
						%>
						<option value="<%=lifecycle.get("code")%>"><%=lifecycle.get("name")%></option>
						<%
						}
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="writer" id="writer" class="width-200">
				</td>
				<th>수정일</th>
				<td class="indent5">
					<input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100">
					~
					<input type="text" name="modifiedTo" id="modifiedTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('modifiedFrom', 'modifiedTo')">
				</td>
				<th>프로젝트 코드</th>
				<td class="indent5">
					<input type="text" name="model" id="model" class="width-200">
					<input type="hidden" name="modelcode" id="modelcode">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearValue('model', 'code')">
				</td>
			</tr>
			<tr>
				<th>보존기간</th>
				<td class="indent5">
					<select name="preseration" id="preseration" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode preseration : preserationList) {
						%>
						<option value="<%=preseration.getCode()%>"><%=preseration.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>REV</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="latest" value="true" checked="checked">
						<div class="state p-success">
							<label>
								<b>최신REV</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="latest" value="false">
						<div class="state p-success">
							<label>
								<b>모든REV</b>
							</label>
						</div>
					</div>
				</td>
				<th>부서</th>
				<td class="indent5" colspan="3">
					<select name="deptcode" id="deptcode" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode deptcode : deptcodeList) {
						%>
						<option value="<%=deptcode.getCode()%>"><%=deptcode.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<!-- 				<th>내용</th> -->
				<!-- 				<td class="indent5"> -->
				<!-- 					<input type="text" name="description" id="description" class="width-300"> -->
				<!-- 				</td> -->
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('document-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('document-list');">
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
						<option value="10">10</option>
						<option value="20" selected="selected">20</option>
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
					<input type="button" value="검색" title="검색" onclick="loadGridData();">
					<!-- 					<input type="button" value="일괄 다운로드" title="일괄 다운로드" onclick="download();"> -->
				</td>
			</tr>
		</table>
		<table>
			<colgroup>
				<col width="270">
				<col width="10">
				<col width="*">
			</colgroup>
			<tr>
				<td valign="top">
					<jsp:include page="/extcore/jsp/common/folder-include.jsp">
						<jsp:param value="<%=DocumentHelper.DOCUMENT_ROOT%>" name="location" />
						<jsp:param value="product" name="container" />
						<jsp:param value="list" name="mode" />
						<jsp:param value="535" name="height" />
						<jsp:param value="doc" name="type" />
					</jsp:include>
				</td>
				<td valign="top">&nbsp;</td>
				<td valign="top">
					<div id="grid_wrap" style="height: 500px; border-top: 1px solid #3180c3;"></div>
					<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
					<%@include file="/extcore/jsp/common/aui-context.jsp"%>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "rowNum",
					headerText : "번호",
					width : 40,
					dataType : "numeric",
					filter : {
						inline : false
					},
				}, {
					dataField : "name",
					headerText : "문서명",
					dataType : "string",
					style : "aui-left",
					width : 350,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							let permission = isPermission(oid);
							if (!permission) {
								authMsg();
								return false;
							}
							const url = getCallUrl("/doc/view?oid=" + oid);
							_popup(url, "", "", "f");
						}
					},
				}, {
					dataField : "number",
					headerText : "문서번호",
					dataType : "string",
					width : 180,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							let permission = isPermission(oid);
							if (!permission) {
								authMsg();
								return false;
							}
							const url = getCallUrl("/doc/view?oid=" + oid);
							_popup(url, "", "", "f");
						}
					},
				}, {
					dataField : "model",
					headerText : "프로젝트 코드 [명]",
					dataType : "string",
					width : 120,
					style : "aui-left",
					renderer : {
						type : "TemplateRenderer"
					},
				}, {
					dataField : "location",
					headerText : "문서분류",
					dataType : "string",
					style : "aui-left",
					width : 250,
				}, {
					dataField : "classType1_name",
					headerText : "대분류",
					dataType : "string",
					width : 100,
				}, {
					dataField : "classType2_name",
					headerText : "중분류",
					dataType : "string",
					width : 200,
				}, {
					dataField : "classType3_name",
					headerText : "소분류",
					dataType : "string",
					width : 100,
				}, {
					dataField : "version",
					headerText : "REV",
					dataType : "string",
					width : 80,
					renderer : {
						type : "TemplateRenderer"
					},
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 100,
					styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
						if (value === "승인됨") {
							return "approved";
						}
						return null;
					}
				}, {
					dataField : "writer",
					headerText : "작성자",
					dataType : "string",
					width : 100,
				}, {
					dataField : "creator",
					headerText : "등록자",
					dataType : "string",
					width : 100,
				}, {
					dataField : "createdDate",
					headerText : "등록일",
					dataType : "date",
					width : 100,
				}, {
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					width : 100,
				}, {
					dataField : "primary",
					headerText : "주 첨부파일",
					dataType : "string",
					width : 80,
					renderer : {
						type : "TemplateRenderer"
					},
					filter : {
						inline : false
					},
				}, {
					dataField : "secondary",
					headerText : "첨부파일",
					dataType : "string",
					width : 100,
					renderer : {
						type : "TemplateRenderer"
					},
					filter : {
						inline : false
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : false,
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					hoverMode : "singleRow",
					enableMovingColumn : true,
					enableFilter : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					enableRowCheckShiftKey : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", _auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
			}

			function _auiContextMenuHandler(event) {
				const item = event.item;
				if (event.target == "header") { // 헤더 컨텍스트
					if (nowHeaderMenuVisible) {
						hideContextMenu();
					}

					nowHeaderMenuVisible = true;

					// 컨텍스트 메뉴 생성된 dataField 보관.
					currentDataField = event.dataField;

					if (event.dataField == "id") { // ID 칼럼은 숨기기 못하게 설정
						$("#h_item_4").addClass("ui-state-disabled");
					} else {
						$("#h_item_4").removeClass("ui-state-disabled");
					}

					// 헤더 에서 사용할 메뉴 위젯 구성
					$("#headerMenu").menu({
						select : headerMenuSelectHandler
					});

					$("#headerMenu").css({
						left : event.pageX,
						top : event.pageY
					}).show();
				} else {
					hideContextMenu();
					const state = item.state;
					let withdraw = true;
					if ("승인중" === state) {
						withdraw = false;
					}
					let print = true;
					if ("승인됨" === state) {
						print = false;
					}
					const menu = [ {
						label : "문서 정보보기",
						callback : auiContextHandler
					}, {
						label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
					}, {
						label : "버전이력보기",
						callback : auiContextHandler
					}, {
						label : "결재이력보기",
						callback : auiContextHandler
					}, {
						label : "_$line" // label 에 _$line 을 설정하면 라인을 긋는 아이템으로 인식합니다.
					}, {
						label : "결재회수(결재선유지)",
						callback : auiContextHandler,
						disable : withdraw
					}, {
						label : "결재회수(결재선초기화)",
						callback : auiContextHandler,
						disable : withdraw
					}, {
						label : "인쇄하기",
						callback : auiContextHandler,
						disable : print
					} ];
					return menu;
				}
			}

			function auiContextHandler(event) {
				const item = event.item;
				const oid = item.oid;
				const state = item.state;
				let withdraw = false;
				if ("승인중" === state) {
					withdraw = true;
				}
				let print = false;
				if ("승인됨" === state) {
					print = true;
				}
				let permission = isPermission(oid);
				if (!permission) {
					authMsg();
					return false;
				}

				let url;
				switch (event.contextIndex) {
				case 0:
					url = getCallUrl("/doc/view?oid=" + oid);
					_popup(url, "", "", "f");
					break;
				case 2:
					url = getCallUrl("/doc/iteration?oid=" + oid + "&popup=true");
					_popup(url, 1600, 600, "n");
					break;
				case 3:
					url = getCallUrl("/workspace/history?oid=" + oid + "&popup=true");
					_popup(url, 1200, 400, "n");
					break;
				case 5:
					if (!withdraw) {
						return false;
					}
					if (!confirm("기존 지정한 결재선 유지한 상태로 결재회수를 합니다.\n진행하시겠습니까?")) {
						return false;
					}
					url = getCallUrl("/workspace/withdraw?oid=" + oid + "&remove=false");
					parent.openLayer();
					call(url, null, function(data) {
						alert(data.msg);
						if (data.result) {
							parent.updateHeader();
							document.location.href = getCallUrl("/workData/list");
						} else {
							parent.closeLayer();
						}
					}, "GET");
					break;
				case 6:
					if (!withdraw) {
						return false;
					}
					if (!confirm("결재선을 초기화 상태로 결재회수를 합니다.\n진행하시겠습니까?")) {
						return false;
					}
					url = getCallUrl("/workspace/withdraw?oid=" + oid + "&remove=true");
					parent.openLayer();
					call(url, null, function(data) {
						alert(data.msg);
						if (data.result) {
							parent.updateHeader();
							document.location.href = getCallUrl("/workData/list");
						} else {
							parent.closeLayer();
						}
					}, "GET");
					break;
				case 7:
					if (!print) {
						return false;
					}
					url = getCallUrl("/doc/print?oid=" + oid);
					const p = _popup(url, "", "", "f");
					break;
				}
			}

			function loadGridData(movePage) {
				if (movePage === undefined) {
					document.getElementById("sessionid").value = 0;
					document.getElementById("curPage").value = 1;
				}

				let params = new Object();
				const url = getCallUrl("/doc/list");
				const field = [ "location", "classType1", "classType2", "classType3", "name", "number", "state", "creatorOid", "createdFrom", "createdTo", "modifiedFrom", "modifiedTo", "preseration", "modelcode", "deptcode", "writer", "description" ];
				params = toField(params, field);
				const latest = document.querySelector("input[name=latest]:checked").value;
				params.latest = JSON.parse(latest);
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					logger(data);
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
						totalPage = Math.ceil(data.total / data.pageSize);
						createPagingNavigator(data.total, data.curPage, data.sessionid);
						AUIGrid.setGridData(myGridID, data.list);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			function first(obj) {
				const classType1 = obj.value;
				if (classType1 !== "") {
					loadClassType2(classType1);
				}
			}

			function loadClassType2(classType1) {
				const url = getCallUrl("/class/classType2?classType1=" + classType1);
				call(url, null, function(data) {
					const classType2 = data.classType2;
					if (data.result) {
						document.querySelector("#classType2 option").remove();
						document.querySelector("#classType2").innerHTML = "<option value=\"\">선택</option>";
						for (let i = 0; i < classType2.length; i++) {
							const value = classType2[i].value;
							const clazz = classType2[i].clazz;
							const tag = "<option data-clazz=\"" + clazz + "\" value=\"" + value + "\">" + classType2[i].name + "</option>";
							document.querySelector("#classType2").innerHTML += tag;
						}
					}
				}, "GET", false);
				selectbox("classType2");
			}

			function second(obj) {
				const classType1 = document.getElementById("classType1").value;
				const classType2 = obj.value;
				if (classType2 !== "") {
					loadClassType3(classType1, classType2);
				}
			}

			// 소분류 세팅
			function loadClassType3(classType1, classType2) {
				const url = getCallUrl("/class/classType3?classType1=" + classType1 + "&classType2=" + classType2);
				call(url, null, function(data) {
					const classType3 = data.classType3;
					if (data.result) {
						document.querySelector("#classType3 option").remove();
						document.querySelector("#classType3").innerHTML = "<option value=\"\">선택</option>";
						for (let i = 0; i < classType3.length; i++) {
							const value = classType3[i].value;
							const clazz = classType3[i].clazz;
							const tag = "<option data-clazz=\"" + clazz + "\" value=\"" + value + "\">" + classType3[i].name + "</option>";
							document.querySelector("#classType3").innerHTML += tag;
						}
					}
				}, "GET", false);
				selectbox("classType3");
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("number");
				const columns = loadColumnLayout("document-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				_createAUIGrid(_columns);
				AUIGrid.resize(_myGridID);
				selectbox("state");
				finderUser("creator");
				twindate("created");
				twindate("modified");
				selectbox("_psize");
				selectbox("preseration");
				selectbox("deptcode");
				// 				finderUser("writer");
				selectbox("classType1");
				selectbox("classType2");
				selectbox("classType3");
				finderCode("model", "MODEL");
				$("#_psize").bindSelectSetValue("20");
			});

			function exportExcel() {
				const exceptColumnFields = [ "primary", "secondary" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("문서 리스트", "문서", "문서 리스트", exceptColumnFields, sessionName);
			}

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
		</script>
	</form>
</body>
</html>