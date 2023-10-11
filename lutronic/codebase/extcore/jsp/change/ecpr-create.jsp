<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
	ArrayList<NumberCode> sectionList = (ArrayList<NumberCode>) request.getAttribute("sectionList");
%>
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
	<form>
		<input type="hidden" name="cmd"		id="cmd"		 	value="save"     />
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> ECPR 등록
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
				<th class="req lb">ECPR 제목</th>
				<td class="indent5" ><input type="text" name="eoName" id="eoName" class="width-200"></td>
				<th class="req lb">ECPR 번호</th>
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
				<th class="lb">작성자</th>
				<td class="indent5">
					<input type="text" name="writer" id="writer" data-multi="false" class="width-200"> 
					<input type="hidden" name="creatorOid" id="creatorOid"> 
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
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
				<td class="indent5">
					<input type="text" name="proposer" id="proposer" data-multi="false" class="width-200"> 
					<input type="hidden" name="creatorOid" id="creatorOid"> 
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
				<th class="lb">변경구분</th>
				<td class="indent5" >
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
			</tr>
			<tr>
				<th class="lb">변경사유</th>
				<td class="indent5" colspan="3"><textarea name="eoCommentA" id="eoCommentA" rows="10"></textarea></td>
			</tr>
			<tr>
				<th class="lb">변경사항</th>
				<td class="indent5" colspan="3"><textarea name="eoCommentB" id="eoCommentB" rows="10"></textarea></td>
			</tr>
			<tr>
				<th class="lb">관련 ECPR</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/change/include_selectEcpr.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">참고사항</th>
				<td class="indent5" colspan="3"><textarea name="eoCommentC" id="eoCommentC" rows="10"></textarea></td>
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
					<input type="button" value="기안" title="기안" class="red" id="createBtn">
					<input type="button" value="결재선 지정" title="결재선 지정" class="blue" onclick="">
					<input type="button" value="임시저장" title="임시저장" class=""  id="exBtn">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				selectbox("model");
				selectbox("preseration");
				selectbox("documentType");
				selectbox("createDepart");
				selectbox("changeSection");
				finderUser("writer");
				finderUser("proposer");
			});
	
			$("#createBtn").click(function() {
				const eoName = document.getElementById("eoName").value;
				const eoNumber = document.getElementById("eoNumber").value;
				const createDate = document.getElementById("createDate").value;
				const approveDate = document.getElementById("approveDate").value;
				const createDepart = document.getElementById("createDepart").value;
				const writer = document.getElementById("writer").value;
				const proposer = document.getElementById("proposer").value;
				const changeSection = document.getElementById("changeSection").value;
				const eoCommentA = document.getElementById("eoCommentA").value;
				const eoCommentB = document.getElementById("eoCommentB").value;
				const eoCommentC = document.getElementById("eoCommentC").value;
// 				const primarys = document.getElementsByName("primarys")[0].value;
				
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
				
				const params = new Object();
				params.eoName = eoName;
				params.eoNumber = eoNumber;
				params.createDate = createDate;
				params.approveDate = approveDate;
				params.createDepart = createDepart;
				params.writer = writer;
				params.proposer = proposer;
				params.changeSection = changeSection;
				params.eoCommentA = eoCommentA;
				params.eoCommentB = eoCommentB;
				params.eoCommentC = eoCommentC;
// 				params.primary = primarys;
				params.secondarys = toArray("secondarys");
				
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

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				createAUIGridECPR(columnsEcpr);
// 				createAUIGrid7(columns7);
// 				createAUIGrid11(columns11);
// 				createAUIGrid8(columns8);
				AUIGrid.resize(ecprGridID);
// 				AUIGrid.resize(myGridID7);
// 				AUIGrid.resize(myGridID11);
// 				AUIGrid.resize(myGridID8);
				document.getElementById("eoName").focus();
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