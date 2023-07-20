<%@page import="com.e3ps.common.service.CommonHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.common.iba.AttributeKey"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.ownership.Ownership"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="com.e3ps.common.util.SequenceDao"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="wt.doc.DocumentType"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.rohs.ROHSMaterial"%>
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
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> RoHS 정보
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
			<th class="lb">물질번호</th>
			<td class="indent5">
				<input type="text" name="rohsNumber" id="rohsNumber" class="width-200">
				&nbsp;<button id="NumberCheck" class="btnSearch" type="button">
							번호 중복
							</button>
			</td>
			<th class="req lb">결재방식</th>
			<td>&nbsp;
					<div class="pretty p-switch">
						<input type="radio"name="lifecycle" id="lifecycle" value="LC_Default" >
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
			<th class="req lb">물질명</th>
			<td class="indent5">
				<input type="text" name="rohsName" id="rohsName" readonly="readonly" class="width-200">
				&nbsp;
				<button id="NameCheck" class="btnSearch" type="button">
					물질명 중복
				</button>
			</td>
			<th class="req lb">협력업체</th>
			<td class="indent5">
				<select name="manufacture" id="manufacture" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
				</select>
			</td>
		</tr>
		<tr>
			<th class="lb">설명</th>
			<td colspan="3" class="indent5"><textarea name="description" id="description" rows="6"></textarea></td>
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
					<img src="/Windchill/extcore/images/header.png"> 관련 품목
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
			<th class="lb">관련 품목</th>
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
					<img src="/Windchill/extcore/images/header.png"> 관련 RoHS
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
			<th class="lb">관련 RoHS</th>
			<td colspan="3">
				<jsp:include page="/extcore/jsp/rohs/include_selectRohs.jsp">
					<jsp:param value="" name="oid" />
					<jsp:param value="create" name="mode" />
				</jsp:include>
			</td>
		</tr>
	</table>
	<table class="button-table">
		<tr>
			<td class="center">
				<input type="button"  value="등록"  title="등록"  class="btnCRUD"  id="createBtn" name="createBtn">
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
			createAUIGrid2(columnsPart);
			AUIGrid.resize(partGridID);
			createAUIGrid6(columnsRohs);
			AUIGrid.resize(rohsGridID);
// 			createAUIGrid7(columns7);
// 			createAUIGrid11(columns11);
// 			createAUIGrid8(columns8);
// 			AUIGrid.resize(myGridID7);
// 			AUIGrid.resize(myGridID11);
// 			AUIGrid.resize(myGridID8);
			document.getElementById("name").focus();
		});

		window.addEventListener("resize", function() {
			AUIGrid.resize(partGridID);
			AUIGrid.resize(rohsGridID);
// 			AUIGrid.resize(myGridID7);
// 			AUIGrid.resize(myGridID11);
// 			AUIGrid.resize(myGridID8);
		});
		
	</script>
</body>
</html>