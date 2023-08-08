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
	<form id="form">
		<input type="hidden" name="cmd"		id="cmd"		 	value="save"     />
		<input type="hidden" name="eoType"		id="eoType"		 	value="ECR"     />
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
				<th class="req lb">CR/ECPR 제목</th>
				<td class="indent5" ><input type="text" name="eoName" id="eoName" class="width-200"></td>
				<th class="req lb">CR/ECPR 번호</th>
				<td class="indent5"><input type="text" name="eoNumber" id="eoNumber" class="width-200"></td>
			</tr>
			<tr>
				<th class="lb">작성일</th>
				<td class="indent5"><input type="text" name="createDate" id="createDate" class="width-100"><img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"></td>
				<th class="lb">승인일</th>
				<td class="indent5"><input type="text" name="approveDate" id="approveDate" class="width-100"><img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제"></td>
			</tr>
			<tr>
				<th class="lb">작성부서</th>
				<td class="indent5" ><input type="text" name="createDepart" id="createDepart" class="width-200"></td>
				<th class="lb">작성자</th>
				<td class="indent5" ><input type="text" name="writer" id="writer" class="width-200"></td>
			</tr>
			<tr>
				<th class="req lb">제품명</th>
				<td colspan="3">
					<input type="button" value="추가" title="추가" class="blue"  id="addNumberCode" name="addNumberCode" >
					<input type="button" value="삭제" title="삭제" class="red"   id="delNumberCode" name="delNumberCode"  >
				</td>
			</tr>
			<tr>
				<th class="lb">제안자</th>
				<td class="indent5" ><input type="text" name="createDepart" id="createDepart" class="width-200"></td>
				<th class="lb">변경구분</th>
				<td class="indent5" ><input type="text" name="writer" id="writer" class="width-200"></td>
			</tr>
			<tr>
				<th class="lb">변경사유</th>
				<td class="indent5" colspan="3"><textarea name="eoCommentA" id="eoCommentA" rows="6"></textarea></td>
			</tr>
			<tr>
				<th class="lb">변경사항</th>
				<td class="indent5" colspan="3"><textarea name="eoCommentB" id="eoCommentB" rows="6"></textarea></td>
			</tr>
			<tr>
				<th class="lb">관련 CR/ECPR</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/change/include_selectEcr.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">참고사항</th>
				<td class="indent5" colspan="3"><textarea name="eoCommentC" id="eoCommentC" rows="6"></textarea></td>
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
				<td class="center">
					<input type="button"  value="등록"  title="등록"  class="blue"  id="createBtn">
					<input type="button" value="초기화" title="초기화" id="resetBtn">
					<input type="button" value="목록" title="목록" id="listBtn">
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
	
			$("#createBtn").click(function() {
				if(isEmpty($("#eoName").val())) {
					alert("제목을 입력하세요.");
					return;
				}
				
				if(isEmpty($("#eoNumber").val())) {
					alert("번호를 입력하세요.");
					return;
				}
				
// 				if($("#model").val() == "") {
// 					alert("제품명을 선택하세요.");
// 					return;
// 				}
				
				if (!confirm("등록 하시겠습니까?")) {
					return;
				}
				
				var params = _data($("#form"));
				var url = getCallUrl("/changeECR/create");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						location.href = getCallUrl("/changeECR/list");
					}else{
						alert(data.msg);
					}
				});
			});
			
			$("#listBtn").click(function() {
				location.href = getCallUrl("/changeECR/list");
			});

			document.querySelector("#addNumberCode").addEventListener("click", () => {
				const url = getCallUrl("/common/popup_numberCodes?codeType=MODEL&disable=true");
				popup(url, 1500, 700);
			});
			
			document.querySelector("#delNumberCode").addEventListener("click", () => {
				
			});
	
			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				createAUIGrid1(columnsEcr);
				createAUIGrid7(columns7);
				createAUIGrid11(columns11);
				createAUIGrid8(columns8);
				AUIGrid.resize(ecrGridID);
				AUIGrid.resize(myGridID7);
				AUIGrid.resize(myGridID11);
				AUIGrid.resize(myGridID8);
				document.getElementById("name").focus();
			});
	
			window.addEventListener("resize", function() {
				AUIGrid.resize(ecrGridID);
				AUIGrid.resize(myGridID7);
				AUIGrid.resize(myGridID11);
				AUIGrid.resize(myGridID8);
			});
		</script>
	</form>	
</body>
</html>