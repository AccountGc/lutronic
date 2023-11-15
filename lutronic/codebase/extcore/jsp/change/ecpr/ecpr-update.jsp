<%@page import="com.e3ps.change.ecpr.dto.EcprDTO"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> sectionList = (ArrayList<NumberCode>) request.getAttribute("sectionList");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
EcprDTO dto = (EcprDTO) request.getAttribute("dto");
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
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						ECPR 수정
					</div>
				</td>
				<td class="right">
					<input type="button" value="수정" title="수정" class="blue" onclick="update('false');">
					<input type="button" value="임시저장" title="임시저장" class="" onclick="update('true');">
					<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
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
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300" value="<%= dto.getName() %>">
				</td>
				<th class="req">ECPR 번호</th>
				<td class="indent5">
					<input type="text" name="number" id="number" class="width-300" value="<%=dto.getNumber()%>">
				</td>
			</tr>
			<tr>
				<th class="lb">작성일</th>
				<td class="indent5">
					<input type="text" name="writeDate" id="writeDate" class="width-100" value="<%=dto.getWriteDate() != null ? dto.getWriteDate() : ""%>">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearDate('writeDate');">
				</td>
				<th>승인일</th>
				<td class="indent5">
					<input type="text" name="approveDate" id="approveDate" class="width-100" value="<%= dto.getApproveDate() != null ? dto.getApproveDate() : "" %>">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="createDate('approveDate');">
				</td>
			</tr>
			<tr>
				<th class="lb">작성부서</th>
				<td class="indent5">
					<select name="createDepart" id="createDepart" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode deptcode : deptcodeList) {
							boolean selected = dto.getCreateDepart().equals(deptcode.getCode());
						%>
						<option value="<%=deptcode.getCode()%>" <% if(selected){%> selected <%} %>><%=deptcode.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="writer" id="writer" data-multi="false" class="width-200" value="<%= dto.getWriter_name() != null ? dto.getWriter_name() : ""%>">
					<input type="hidden" name="writerOid" id="writerOid"  value="<%= dto.getWriter_oid() %>">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('writer')">
				</td>
			</tr>
			<tr>
				<th class="req lb">제품명</th>
				<td colspan="3" class="indent5 pt5">
					<jsp:include page="/extcore/jsp/admin/code/include/code-include.jsp">
						<jsp:param value="<%= dto.getOid() %>" name="oid" />
						<jsp:param value="update" name="mode" />
						<jsp:param value="insert300" name="method" />
						<jsp:param value="MODEL" name="codeType" />
						<jsp:param value="true" name="multi" />
						<jsp:param value="150" name="height" />
					</jsp:include>
				</td>
			</tr>
<!-- 			<tr> -->
<!-- 				<th class="lb">제안자</th> -->
<!-- 				<td class="indent5" colspan="3"> -->
<%-- 					<input type="text" name="proposer" id="proposer" data-multi="false" class="width-200" value="<%= dto.getProposer_name() != null ? dto.getProposer_name() : ""%>"> --%>
<%-- 					<input type="hidden" name="proposerOid" id="proposerOid" value="<%= dto.getProposer_oid()%>"> --%>
<!-- 					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('proposer')"> -->
<!-- 				</td> -->
<!-- 			</tr> -->
			<tr>
				<th class="lb">변경구분</th>
				<td class="indent5" colspan="3">
					&nbsp;
					<%
					for (NumberCode section : sectionList) {
						int isInclude = -1;
						if(dto.getChangeSection()!=null){
							isInclude = dto.getChangeSection().indexOf(section.getCode());
						}
					%>
					<div class="pretty p-switch">
						<input type="checkbox" name="changeSection" value="<%=section.getCode()%>" <%if(isInclude >= 0){ %>checked<%} %>>
						<div class="state p-success">
							<label>
								<b><%=section.getName()%></b>
							</label>
						</div>
					</div>
					&nbsp;
					<%
					}
					%>
				</td>
			</tr>
			<tr>
				<th class="lb">변경사유</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentA" id="eoCommentA" rows="10"><%= dto.getEoCommentA() != null ? dto.getEoCommentA() : "" %></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">변경사항</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentB" id="eoCommentB" rows="10"><%= dto.getEoCommentB() != null ? dto.getEoCommentB() : "" %></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">참고사항</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentC" id="eoCommentC" rows="10"><%= dto.getEoCommentC() != null ? dto.getEoCommentC() : "" %></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">주 첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
						<jsp:param value="<%= dto.getOid() %>" name="oid" />
						<jsp:param value="modify" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="<%= dto.getOid() %>" name="oid" />
						<jsp:param value="modify" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">결재</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/workspace/include/approval-register.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">외부 메일 지정</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/workspace/include/mail-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="update" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>

		<!-- 	관련 CR -->
		<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
			<jsp:param value="<%= dto.getOid() %>" name="oid" />
			<jsp:param value="update" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="150" name="height" />
		</jsp:include>
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="수정" title="수정" class="blue" onclick="update('false');">
					<input type="button" value="임시저장" title="임시저장" class="" onclick="update('true');">
					<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
				</td>
			</tr>
		</table>
		
		<script type="text/javascript">
			function update(temp) {
				const oid = document.getElementById("oid");
				const name = document.getElementById("name");
				const number = document.getElementById("number");
				const temprary = JSON.parse(temp);
				const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
				
				if (temprary) {
					if (!confirm("임시저장하시겠습니까??")) {
						return false;
					}
					
					if (addRows8.length > 0) {
						alert("결재선 지정을 해지해주세요.")
						return false;
					}
					
				} else {
					if (!confirm("수정 하시겠습니까?")) {
						return false;
					}
				}			
				const primary = document.querySelector("input[name=primary]");
				// 관련CR
				const rows101 = AUIGrid.getGridDataWithState(myGridID101, "gridState");
				// 모델
				const rows300 = AUIGrid.getGridDataWithState(myGridID300, "gridState");
				// 외부 메일
				const external = AUIGrid.getGridDataWithState(myGridID9, "gridState");

				// 변경 구분 배열 처리
				const changeSection = document.querySelectorAll('input[name="changeSection"]:checked');
				const sections = [];
				changeSection.forEach(function(item) {
					sections.push(item.value);
				});
				
				if(isEmpty(name.value)){
					alert("CR 제목을 입력해주세요.");
					name.focus();
					return;
				}
				
				if(isEmpty(number.value)){
					alert("CR 번호를 선택해주세요.");
					number.focus();
					return;
				}
				
				if(rows300.length == 0){
					alert("제품명을 입력해주세요.");
					return;
				}
				
				if(primary == null){
					alert("주 첨부파일을 첨부해주세요.");
					return;
				}
				
				const params = {
					oid : oid.value,
					name : name.value,
					number : number.value,
					writeDate : toId("writeDate"),
					approveDate : toId("approveDate"),
					createDepart : toId("createDepart"),
					writer_oid : toId("writerOid"),
// 					proposer_oid : toId("proposerOid"),
					eoCommentA : toId("eoCommentA"),
					eoCommentB : toId("eoCommentB"),
					eoCommentC : toId("eoCommentC"),
					sections : sections, //변경 구분
					primary : primary.value,
					rows101 : rows101,
					rows300 : rows300,
					temprary : temprary,
					// 외부 메일
					external : external,
				}
				
				toRegister(params, addRows8); // 결재선 세팅
				const secondarys = toArray("secondarys");
				params.secondarys = secondarys;
				const url = getCallUrl("/ecpr/update");
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						opener.loadGridData();
						self.close();
					}
				});
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				toFocus("name");
				date("writeDate");
				date("approveDate");
				selectbox("createDepart");
				finderUser("writer");
// 				finderUser("proposer");
				createAUIGrid300(columns300);
				createAUIGrid101(columns101);
				createAUIGrid8(columns8);
				createAUIGrid9(columns9);
				AUIGrid.resize(myGridID300);
				AUIGrid.resize(myGridID101);
				AUIGrid.resize(myGridID8);
				AUIGrid.resize(myGridID9);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID300);
				AUIGrid.resize(myGridID101);
				AUIGrid.resize(myGridID8);
				AUIGrid.resize(myGridID9);
			});
		</script>
	</form>
</body>
</html>