<%@page import="wt.doc.DocumentType"%>
<%@page import="wt.ownership.Ownership"%>
<%@page import="com.e3ps.common.util.SequenceDao"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="com.e3ps.rohs.ROHSMaterial"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.folder.FolderEntry"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.inf.container.WTContainerHelper"%>
<%@page import="wt.inf.container.WTContainerRef"%>
<%@page import="wt.org.OrganizationServicesHelper"%>
<%@page import="wt.org.WTOrganization"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.session.SessionHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%-- <%@include file="/extcore/jsp/common/aui/auigrid.jsp"%> --%>
<input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT %>">
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
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> 문서 정보
				</div>
			</td>
		</tr>
	</table>
	<table class="create-table">
		<colgroup>
			<col width="150">
			<col width="*">
			<col width="150">
			<col width="*">
		</colgroup>
		<tr>
			<th class="req lb">문서분류</th>
			<td class="indent5">
				<span id="locationName">
					/Default/Document
				</span>
			</td>
			<th class="req lb">결재방식</th>
			<td>&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default" checked="checked">
						<div class="state p-success">
							<label> <b>기본결재</b>
							</label>
						</div>
					</div> &nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default_NonWF">
						<div class="state p-success">
							<label> <b>일괄결재</b>
							</label>
						</div>
					</div>
				</td>
		</tr>
		<tr>
			<th class="req lb">문서종류</th>
			<td class="indent5"><input type="text" name="documentName" id="documentName" class="width-200"></td>
			<th class="lb">문서명</th>
			<td class="indent5"><input type="text" name="docName" id="docName" class="width-200"></td>
		</tr>
		<tr>
			<th class="req lb">문서유형</th>
			<td class="indent5">
				<select name="documentType"  id="documentType" class="width-200">
						<option value="">선택</option>
						<option value="$$Document">일반문서</option>
						<option value="$$Document">개발문서</option>
						<option value="$$Document">승인원</option>
						<option value="$$Document">인증문서</option>
						<option value="$$Document">금형문서</option>
						<option value="$$Document">개발소스</option>
						<option value="$$Document">배포자료</option>
						<option value="$$Document">ROHS</option>
						<option value="$$Document">기타문서</option>
				</select></td>
			<th class="req lb">보존기간</th>
			<td class="indent5"><select name="preseration"  id="preseration" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
				</select></td>
		</tr>
		<tr>
			<th class="lb">프로젝트코드</th>
			<td class="indent5"><select name="model" id="model" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
				</select></td>
			<th class="lb">부서</th>
			<td class="indent5"><select name="deptcode" id="deptcode" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
				</select></td>
		</tr>
		<tr>
			<th class="lb">내부 문서번호</th>
			<td class="indent5"><input type="text" name="interalnumber" id="interalnumber" class="width-200"></td>
			<th class="lb">작성자</th>
			<td class="indent5"><input type="text" name="writer" id="writer" class="width-200"></td>
		</tr>
		<tr>
			<th class="lb">문서설명</th>
			<td colspan="3" class="indent5"><textarea name="description" id="description" rows="6"></textarea></td>
		</tr>
		<tr>
			<th class="req lb">주 첨부파일</th>
			<td class="indent5" colspan="3">
				<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
					<jsp:param value="" name="oid" />
				</jsp:include>
			</td>
		</tr>
		<tr>
			<th class="lb">첨부파일</th>
			<td class="indent5" colspan="3">
				<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
					<jsp:param value="" name="oid" />
				</jsp:include>
			</td>
		</tr>
	</table>
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> 관련품목
				</div>
			</td>
		</tr>
	</table>
	<table class="create-table">
		<colgroup>
			<col width="150">
			<col width="600">
			<col width="150">
			<col width="600">
		</colgroup>
		<tr>
			<th class="lb">관련품목</th>
			<td colspan="3">
				<jsp:include page="/extcore/jsp/change/include_selectPart.jsp">
					<jsp:param value="" name="oid" />
					<jsp:param value="create" name="mode" />
				</jsp:include>
			</td>
		</tr>
	</table>
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> 관련문서
				</div>
			</td>
		</tr>
	</table>
	<table class="create-table">
		<colgroup>
			<col width="150">
			<col width="600">
			<col width="150">
			<col width="600">
		</colgroup>
		<tr>
			<th class="lb">관련문서</th>
			<td colspan="3">
				<jsp:include page="/extcore/jsp/document/include_selectDocument.jsp">
					<jsp:param value="" name="oid" />
					<jsp:param value="create" name="mode" />
				</jsp:include>
			</td>
		</tr>
	</table>
	<table class="button-table">
		<tr>
			<td class="center">
				<input type="button"  value="등록"  title="등록"  class="btnCRUD"  id="createBtn" name="createBtn" onclick="create('false');">
				<input type="button" value="초기화" title="초기화"  class="btnCRUD"  id="resetBtn" name="resetBtn">
				<input type="button" value="목록" title="목록"  class="btnCRUD"  id="listBtn" name="listBtn">
			</td>
		</tr>
	</table>
	<script type="text/javascript">
	
		function folder() {
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
		
		document.addEventListener("DOMContentLoaded", function() {
			selectbox("model");
			selectbox("preseration");
			selectbox("documentType");
			selectbox("deptcode");
		});

		function create(isSelf) {
			const lifecycle = document.getElementById("lifecycle").value;
			const documentName = document.getElementById("documentName").value;
			const docName = document.getElementById("docName");
			const documentType = document.getElementById("documentType").value;
			const preseration = document.getElementById("preseration").value;
			const model = document.getElementById("model").value;
			const deptcode = document.getElementById("deptcode").value;
			const interalnumber = document.getElementById("interalnumber").value;
			const writer = document.getElementById("writer").value;
			const description = document.getElementById("description").value;
			const location = document.getElementById("location").value;
// 			const addRows7 = AUIGrid.getAddedRowItems(myGridID7);
// 			const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
// 			const addRows11 = AUIGrid.getAddedRowItems(myGridID11);
			const primarys = toArray("primarys");

// 			if (location === "/Default/문서") {
// 				alert("문서 저장위치를 선택하세요.");
// 				folder();
// 				return false;
// 			}

			if (isNull(docName.value)) {
				alert("문서제목을 입력하세요.");
				name.focus();
				return false;
			}

			// 		if(addRows11.length === 0) {
			// 			alert("도번을 추가하세요.");
			// 			insert11();
			// 			return false;
			// 		}

// 			if (primarys.length === 0) {
// 				alert("첨부파일을 선택하세요.");
// 				return false;
// 			}

			if (!confirm("등록 하시겠습니까?")) {
				return false;
			}

			const params = new Object();
			const url = getCallUrl("/doc/create");
			params.lifecycle = lifecycle;
			params.documentName = documentName;
			params.docName = docName.value;
			params.preseration = preseration;
			params.model = model;
			params.deptcode = deptcode;
			params.interalnumber = interalnumber;
			params.writer = writer;
			params.self = JSON.parse(isSelf);
			params.description = description;
			params.location = location;
			params.documentType = documentType;
// 			params.addRows7 = addRows7;
// 			params.addRows11 = addRows11;
			params.primarys = primarys;
// 			toRegister(params, addRows8);
// 			openLayer();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
// 					opener.loadGridData();
// 					self.close();
				} else {
// 					closeLayer();
				}
			});
		};

		// jquery 삭제를 해가는 쪽으로 한다..
		document.addEventListener("DOMContentLoaded", function() {
			// DOM이 로드된 후 실행할 코드 작성
			createAUIGrid2(columnsPart);
			AUIGrid.resize(partGridID);
			createAUIGrid4(columnsDoc);
			AUIGrid.resize(docGridID);
// 			createAUIGrid7(columns7);
// 			createAUIGrid11(columns11);
// 			createAUIGrid8(columns8);
// 			AUIGrid.resize(myGridID7);
// 			AUIGrid.resize(myGridID11);
// 			AUIGrid.resize(myGridID8);
			document.getElementById("docName").focus();
		});

		window.addEventListener("resize", function() {
			AUIGrid.resize(partGridID);
			AUIGrid.resize(docGridID);
// 			AUIGrid.resize(myGridID7);
// 			AUIGrid.resize(myGridID11);
// 			AUIGrid.resize(myGridID8);
		});
	</script>
</body>
</html>