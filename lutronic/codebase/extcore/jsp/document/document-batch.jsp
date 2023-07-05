<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%-- <%@include file="/extcore/jsp/common/aui/auigrid.jsp"%> --%>
<%-- <input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT %>"> --%>
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
	<table class="button-table">
		<tr>
			<td class="right">
				<input type="button" value="등록" title="등록" onclick="create('false');">
				<input type="button" value="엑셀등록이동" title="엑셀등록이동" class="blue" onclick="create('true')">
			</td>
		</tr>
	</table>
	<table class="create-table">
		<colgroup>
			<col width="150">
			<col width="*">
		</colgroup>
		<tr>
			<th class="req lb">문서분류</th>
			<td class="indent5" id="locationName" name="locationName"></td>
		</tr>
		<tr>
			<th class="req lb">첨부파일</th>
			<td class="indent5" colspan="3">
			</td>
		</tr>
	</table>
	<table class="button-table">
		<tr>
			<td class="right"><input type="button" value="추가" title="추가" onclick="create('false');"><input type="button" value="삭제" title="삭제" class="red" onclick="self.close();"></td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 645px; border-top: 1px solid #3180c3;"></div>
	<%@include file="/extcore/jsp/common/aui-context.jsp"%>
	<script type="text/javascript">
		let myGridID;
		function _layout() {
			return [ {
				dataField : "model",
				headerText : "문서번호",
				dataType : "string",
				width : 120,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "name",
				headerText : "결과",
				dataType : "string",
				width : 350,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "location",
				headerText : "문서종류(*)",
				dataType : "string",
				width : 250,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "createdDate",
				headerText : "문서명",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifiedDate",
				headerText : "결재방식(*)",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifiedDate",
				headerText : "문서유형(*)",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "state",
				headerText : "설명",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "modifiedDate",
				headerText : "프로젝트 코드",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "name",
				headerText : "작성자",
				dataType : "string",
				width : 350,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "location",
				headerText : "내부문서번호",
				dataType : "string",
				width : 250,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "createdDate",
				headerText : "부서",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifiedDate",
				headerText : "보존기간(*)",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifiedDate",
				headerText : "분류체계(*)",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifiedDate",
				headerText : "파일명(*)",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifiedDate",
				headerText : "부첨부파일명1(*)",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifiedDate",
				headerText : "부첨부파일명2(*)",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifiedDate",
				headerText : "부첨부파일명3(*)",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifiedDate",
				headerText : "부첨부파일명4(*)",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifiedDate",
				headerText : "부첨부파일명5(*)",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "state",
				headerText : "관련부품",
				dataType : "string",
				width : 100,
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
			};
			myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
			// 				loadGridData();
			AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
			AUIGrid.bind(myGridID, "vScrollChange", function(event) {
				hideContextMenu();
				vScrollChangeHandler(event);
			});
			AUIGrid.bind(myGridID, "hScrollChange", function(event) {
				hideContextMenu();
			});
		}
		
		document.addEventListener("DOMContentLoaded", function() {
			const columns = loadColumnLayout("document-list");
			const contenxtHeader = genColumnHtml(columns);
			$("#h_item_ul").append(contenxtHeader);
			$("#headerMenu").menu({
				select : headerMenuSelectHandler
			});
			createAUIGrid(columns);
			AUIGrid.resize(myGridID);

		});

		/* function folder() {
			const location = decodeURIComponent("/Default/문서");
			const url = getCallUrl("/folder?location=" + location + "&container=product&method=setNumber&multi=false");
			popup(url, 500, 600);
		}

		function setNumber(item) {
			const url = getCallUrl("/doc/setNumber");
			const params = new Object();
			params.loc = item.location;
			call(url, params, function(data) {
				document.getElementById("loc").innerHTML = item.location;
				document.getElementById("location").value = item.location;
				document.getElementById("number").value = data.number;
			})
		}

		function create(isSelf) {
			const name = document.getElementById("name");
			const number = document.getElementById("number").value;
			const description = document.getElementById("description").value;
			const location = document.getElementById("location").value;
			const addRows7 = AUIGrid.getAddedRowItems(myGridID7);
			const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
			const addRows11 = AUIGrid.getAddedRowItems(myGridID11);
			const primarys = toArray("primarys");

			if (location === "/Default/문서") {
				alert("문서 저장위치를 선택하세요.");
				folder();
				return false;
			}

			if (isNull(name.value)) {
				alert("문서제목을 입력하세요.");
				name.focus();
				return false;
			}

			// 		if(addRows11.length === 0) {
			// 			alert("도번을 추가하세요.");
			// 			insert11();
			// 			return false;
			// 		}

			if (primarys.length === 0) {
				alert("첨부파일을 선택하세요.");
				return false;
			}

			if (!confirm("등록 하시겠습니까?")) {
				return false;
			}

			const params = new Object();
			const url = getCallUrl("/doc/create");
			params.name = name.value;
			params.number = number;
			params.self = JSON.parse(isSelf);
			params.description = description;
			params.location = location;
			params.addRows7 = addRows7;
			params.addRows11 = addRows11;
			params.primarys = primarys;
			toRegister(params, addRows8);
			openLayer();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					opener.loadGridData();
					self.close();
				} else {
					closeLayer();
				}
			});
		};

		// jquery 삭제를 해가는 쪽으로 한다..
		document.addEventListener("DOMContentLoaded", function() {
			// DOM이 로드된 후 실행할 코드 작성
			createAUIGrid7(columns7);
			createAUIGrid11(columns11);
			createAUIGrid8(columns8);
			AUIGrid.resize(myGridID7);
			AUIGrid.resize(myGridID11);
			AUIGrid.resize(myGridID8);
			document.getElementById("name").focus();
		});

		window.addEventListener("resize", function() {
			AUIGrid.resize(myGridID7);
			AUIGrid.resize(myGridID11);
			AUIGrid.resize(myGridID8);
		}); */
	</script>
</body>
</html>