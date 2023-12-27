<%@page import="com.e3ps.change.ecpr.dto.EcprDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String tapOid = request.getParameter("tapOid");
EcprDTO dto = new EcprDTO(tapOid);
%>
	<!-- 기본 정보 -->
	<table class="view-table">
		<colgroup>
			<col width="130">
			<col width="450">
			<col width="130">
			<col width="450">
			<col width="130">
			<col width="450">
		</colgroup>
		<tr>
			<th class="lb">ECPR 번호</th>
			<td class="indent5"><%=dto.getNumber()%></td>
			<th>ECPR 제목</th>
			<td class="indent5"><%=dto.getName()%></td>
			<th>상태</th>
			<td class="indent5"><%=dto.getState()%></td>
		</tr>
		<tr>
			<th class="lb">등록자</th>
			<td class="indent5"><%=dto.getCreator()%></td>
			<th>등록일</th>
			<td class="indent5"><%=dto.getCreatedDate()%></td>
			<th>수정일</th>
			<td class="indent5"><%=dto.getModifiedDate_text()%></td>
		</tr>
		<tr>
			<th class="lb">작성자</th>
			<td class="indent5"><%=dto.getWriter_name()%></td>
			<th>작성부서</th>
			<td class="indent5"><%=dto.getCreateDepart_name()%></td>
			<th>작성일</th>
			<td class="indent5"><%=dto.getWriteDate()%></td>
		</tr>
		<tr>
			<th class="lb">승인일</th>
			<td class="indent5"><%=dto.getApproveDate()%></td>
			<th>변경부분</th>
			<td class="indent5" colspan="3"><%=dto.getChangeSection()%></td>
		</tr>
		<tr>
			<th class="lb">제품명</th>
			<td colspan="5" class="indent5"><%=dto.getModel()%></td>
		</tr>
		<tr>
			<th class="lb">변경사유</th>
			<td colspan="5" class="indent5">
				<textarea rows="10" readonly="readonly" id="description"><%=dto.getEoCommentA()%></textarea>
			</td>
		</tr>
		<tr>
			<th class="lb">변경사항</th>
			<td colspan="5" class="indent5">
				<textarea rows="10" readonly="readonly" id="description"><%=dto.getEoCommentB()%></textarea>
			</td>
		</tr>
		<tr>
			<th class="lb">참고사항</th>
			<td colspan="5" class="indent5">
				<textarea rows="10" readonly="readonly" id="description"><%=dto.getEoCommentC()%></textarea>
			</td>
		</tr>
		<tr>
			<th class="lb">주 첨부파일</th>
			<td colspan="5" class="indent5">
				<jsp:include page="/extcore/jsp/common/primary-view.jsp">
					<jsp:param value="<%=dto.getOid()%>" name="oid" />
				</jsp:include>
			</td>
		</tr>
		<tr>
			<th class="lb">첨부파일</th>
			<td colspan="5" class="indent5">
				<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
					<jsp:param value="<%=dto.getOid()%>" name="oid" />
				</jsp:include>
			</td>
		</tr>
	</table>
	
	<!-- 관련 객체 -->
	<jsp:include page="/extcore/jsp/change/cr/include/cr-reference-include.jsp">
		<jsp:param value="<%=dto.getOid()%>" name="oid" />
	</jsp:include>
	
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		const isCreated300 = AUIGrid.isCreated(myGridID300); // MODEL
		if (isCreated300) {
			AUIGrid.resize(myGridID300);
		} else {
			createAUIGrid300(columns300);
		}
		const isCreated101 = AUIGrid.isCreated(myGridID101); // CR
		if (isCreated101) {
			AUIGrid.resize(myGridID101);
		} else {
			createAUIGrid101(columns101);
		}
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID300);
		AUIGrid.resize(myGridID101);
	});
</script>
