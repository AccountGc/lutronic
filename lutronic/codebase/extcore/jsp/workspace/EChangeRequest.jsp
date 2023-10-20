<%@page import="com.e3ps.common.code.service.NumberCodeHelper"%>
<%@page import="com.e3ps.change.cr.dto.CrDTO"%>
<%@page import="java.util.Map"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String tapOid = request.getParameter("tapOid");
CrDTO dto = new CrDTO(tapOid);
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
			<th class="lb">CR 번호</th>
			<td><%=dto.getNumber()%></td>
			<th>CR 제목</th>
			<td><%=dto.getName()%></td>
			<th>상태</th>
			<td><%=dto.getState()%></td>
		</tr>
		<tr>
			<th class="lb">등록자</th>
			<td><%=dto.getCreator()%></td>
			<th>등록일</th>
			<td><%=dto.getCreatedDate_text()%></td>
			<th>수정일</th>
			<td><%=dto.getModifiedDate_text()%></td>
		</tr>
		<tr>
			<th class="lb">작성자</th>
			<td><%=dto.getWriter_name()%></td>
			<th>작성부서</th>
			<td><%=dto.getCreateDepart_name()%></td>
			<th>작성일</th>
			<td><%=dto.getWriteDate()%></td>
		</tr>
		<tr>
			<th class="lb">제안자</th>
			<td><%=dto.getProposer_name()%></td>
			<th>변경부분</th>
			<td><%=dto.getChangeSection()%></td>
			<th>승인일</th>
			<td><%=dto.getApproveDate()%></td>
		</tr>
		<tr>
			<th class="lb">제품명</th>
			<td colspan="5"><%=dto.getModel()%></td>
		</tr>
		<tr>
			<th class="lb">변경사유</th>
			<td colspan="5">
				<textarea rows="10" readonly="readonly" id="description"><%=dto.getEoCommentA()%></textarea>
			</td>
		</tr>
		<tr>
			<th class="lb">변경사항</th>
			<td colspan="5">
				<textarea rows="10" readonly="readonly" id="description"><%=dto.getEoCommentB()%></textarea>
			</td>
		</tr>
		<tr>
			<th class="lb">참고사항</th>
			<td colspan="5">
				<textarea rows="10" readonly="readonly" id="description"><%=dto.getEoCommentC()%></textarea>
			</td>
		</tr>
		<tr>
			<th class="lb">주 첨부파일</th>
			<td colspan="5">
				<%
				Map<String, Object> contentMap = dto.getContentMap();
				if (contentMap != null) {
				%>
				<div>
					<a href="<%=contentMap.get("url")%>">
						<span style="position: relative; bottom: 2px;"><%=contentMap.get("name")%></span>
						<img src="<%=contentMap.get("fileIcon")%>" style="position: relative; top: 1px;">
					</a>
				</div>
				<%
				} else {
				%>
				<font color="red">
					<b>등록된 계변경 부품 내역파일이 없습니다.</b>
				</font>
				<%
				}
				%>
			</td>
		</tr>
		<tr>
			<th class="lb">첨부파일</th>
			<td colspan="5">
				<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
					<jsp:param value="<%= dto.getOid() %>" name="oid"/>
				</jsp:include>
			</td>
		</tr>
	</table>
	
	<jsp:include page="/extcore/jsp/change/cr/include/cr-reference-include.jsp">
		<jsp:param value="<%=dto.getOid()%>" name="oid" />
	</jsp:include>
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		const isCreated300 = AUIGrid.isCreated(myGridID300); // ECO
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
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID300);
	});
</script>
