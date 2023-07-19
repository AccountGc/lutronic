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
					<img src="/Windchill/extcore/images/header.png"> 문서 정보
				</div>
			</td>
		</tr>
	</table>
	<table class="create-table">
		<colgroup>
			<col width="150">
			<col width="*">
			<col width="*">
			<col width="*">
			<col width="*">
			<col width="*">
			<col width="*">
		</colgroup>
		<tr>
			<th class="req lb">ECO 제목</th>
			<td class="indent5" colspan="7"><input type="text" name="name" id="name" class="width-200"></td>
		</tr>
		<tr>
			<th class="lb">관련 CR/ECPR</th>
			<td colspan="7">
				<jsp:include page="/extcore/jsp/change/include_ecoEcr.jsp">
					<jsp:param value="" name="oid" />
					<jsp:param value="create" name="mode" />
				</jsp:include>
			</td>
		</tr>
		<tr>
			<th class="lb">변경사유</th>
			<td class="indent5" colspan="7"><textarea name="eoCommentA" id="eoCommentA" rows="6"></textarea></td>
		</tr>
		<tr>
			<th class="lb">변경사항</th>
			<td class="indent5" colspan="7"><textarea name="eoCommentB" id="eoCommentB" rows="6"></textarea></td>
		</tr>
		<tr>
			<th class="req lb">설계변경 부품</th>
			<td colspan="7">
				<jsp:include page="/extcore/jsp/change/include_ecoPart.jsp">
					<jsp:param value="" name="oid" />
					<jsp:param value="create" name="mode" />
				</jsp:include>
			</td>
		</tr>
		<tr>
			<th class="lb">인허가변경</th>
			<td colspan="3"> &nbsp;
				<div class="pretty p-switch">
					<input type="radio" name="licensing" value="">
					<div class="state p-success">
						<label> <b>해제</b>
						</label>
					</div>
				</div> &nbsp;
				<div class="pretty p-switch">
					<input type="radio" name="licensing" value="0">
					<div class="state p-success">
						<label> <b>불필요</b>
						</label>
					</div>
				</div> &nbsp;
				<div class="pretty p-switch">
					<input type="radio" name="licensing" value="1">
					<div class="state p-success">
						<label> <b>필요</b>
						</label>
					</div>
				</div>
			</td>
			<th>위험통제</th>
			<td colspan="3"> &nbsp;
				<div class="pretty p-switch">
					<input type="radio" name="licensing" value="">
					<div class="state p-success">
						<label> <b>해제</b>
						</label>
					</div>
				</div>
				<div class="pretty p-switch">
					<input type="radio" name="riskType" value="0">
					<div class="state p-success">
						<label> <b>불필요</b>
						</label>
					</div>
				</div> &nbsp;
				<div class="pretty p-switch">
					<input type="radio" name="riskType" value="1">
					<div class="state p-success">
						<label> <b>필요</b>
						</label>
					</div>
				</div> 
			</td>
		</tr>
		<tr>
			<th class="lb">특기사항</th>
			<td class="indent5" colspan="7"><textarea name="eoCommentC" id="eoCommentC" rows="6"></textarea></td>
		</tr>
		<tr>
			<th class="lb">기타사항</th>
			<td class="indent5" colspan="7"><textarea name="eoCommentD" id="eoCommentD" rows="6"></textarea></td>
		</tr>
		<tr>
			<th class="lb">
				설계변경 부품 내역파일
				<button type="button" class="btnCustom" id="download">
					양식다운
				</button>
			</th>
			<td class="indent5" colspan="7">
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
					<img src="/Windchill/extcore/images/header.png"> 활동설정
				</div>
			</td>
			<td class="right">
				활동 불러오기 :
				<select name="state" id="state" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
				</select>
				<input type="button"  value="활동추가"  title="활동추가"  class="btnCRUD"  id="createBtn" name="createBtn">
				<input type="button" value="활동삭제" title="활동삭제"  class="btnCRUD"  id="resetBtn" name="resetBtn">
			</td>
		</tr>
	</table>
	<table class="create-table">
		<tr>
			<th>Step</th>
			<th><input type="checkbox"/></th>
			<th>활동명</th>
			<th>활동구분</th>
			<th>부서</th>
			<th>담당자</th>
			<th>완료 요청일</th>
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
			createAUIGrid1(columnsEcr);
			createAUIGrid2(columnsPart);
			AUIGrid.resize(partGridID);
			AUIGrid.resize(ecrGridID);
			document.getElementById("name").focus();
		});

		window.addEventListener("resize", function() {
			AUIGrid.resize(partGridID);
			AUIGrid.resize(ecrGridID);
		});
	</script>
</body>
</html>