<%@page import="com.e3ps.rohs.dto.RohsData"%>
<%@page import="wt.iba.definition.litedefinition.IBAUtility"%>
<%@page import="com.e3ps.common.comments.beans.CommentsDTO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.doc.dto.DocumentDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String tapOid = request.getParameter("tapOid");
RohsData dto = new RohsData(tapOid);

ArrayList<CommentsDTO> list = dto.getComments();
%>
<style type="text/css">
iframe {
	margin-top: 3px;
}
</style>
<script type="text/javascript" src="/Windchill/extcore/smarteditor2/js/HuskyEZCreator.js"></script>

<input type="hidden" name="tapOid" id="tapOid" value="<%=dto.getOid()%>">

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
			<th class="lb">물질명</th>
			<td class="indent5"><%=dto.getName()%></td>
			<th>물질 번호</th>
			<td class="indent5"><%=dto.getNumber()%></td>
			<th>협력업체</th>
			<td class="indent5"><%=dto.getManufactureDisplay()%></td>
		</tr>
		<tr>
			<th class="lb">상태</th>
			<td class="indent5"><%=dto.getStateDisplay()%></td>
			<th>REV</th>
			<td class="indent5"><%=dto.getVersion()%></td>
			<th>등록자</th>
			<td class="indent5"><%=dto.getCreator()%></td>
		</tr>
		<tr>
			<th class="lb">등록일</th>
			<td class="indent5"><%=dto.getCreateDate()%></td>
			<th>수정자</th>
			<td class="indent5"><%=dto.getCreateDate()%></td>
			<th>수정일</th>
			<td class="indent5"><%=dto.getModifyDate()%></td>
		</tr>
		<tr>
			<th class="lb">결재방식</th>
			<td class="indent5" colspan="5"><%=dto.getApprovalTypeDisplay() == null ? "" : dto.getApprovalTypeDisplay()%></td>
		</tr>
		<tr>
			<th class="lb">설명</th>
			<td colspan="5" class="indent5">
				<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
			</td>
		</tr>
		<tr>
			<th class="lb">첨부파일</th>
			<td class="indent5" colspan="5">
				<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
					<jsp:param value="<%=dto.getOid()%>" name="oid" />
				</jsp:include>
			</td>
		</tr>
	</table>
	
	<!-- 관련 품목 -->
	<jsp:include page="/extcore/jsp/part/include_viewPart.jsp" flush="false" >
		<jsp:param value="<%=dto.getOid() %>" name="oid" />
		<jsp:param value="관련 품목" name="title" />
		<jsp:param value="rohs" name="moduleType"/>
	</jsp:include>
	<!-- 관련 대표 물질 -->
	<jsp:include page="/extcore/jsp/rohs/include_viewRohs.jsp" flush="false">
		<jsp:param value="<%=dto.getOid() %>" name="oid" />
		<jsp:param value="view" name="mode" />
		<jsp:param value="represent" name="roleType"/>
		<jsp:param value="관련 대표 물질" name="title"/>
	</jsp:include>
	<!-- 관련 물질 -->
	<jsp:include page="/extcore/jsp/rohs/include_viewRohs.jsp" flush="false">
		<jsp:param value="<%=dto.getOid() %>" name="oid" />
		<jsp:param value="view" name="mode" />
		<jsp:param value="composition" name="roleType"/>
		<jsp:param value="관련 물질" name="title"/>
	</jsp:include>
	
	<%
		if (list.size() !=0) {
	%>
	<div id="comments-layer">
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						댓글
					</div>
				</td>
			</tr>
		</table>
		<%
		for (CommentsDTO cm : list) {
			int depth = cm.getDepth();
			ArrayList<CommentsDTO> reply = cm.getReply();
		%>
		<table class="view-table">
			<tr>
				<th class="lb" style="background-color: rgb(193, 235, 255); width: 100px">
					<%=cm.getCreator()%>
					<br>
					<%=cm.getCreatedDate()%>
				</th>
				<td class="indent5">
					<textarea rows="5" readonly="readonly" style="resize: none;"><%=cm.getComment()%></textarea>
				</td>
			</tr>
		</table>
		<br>
		<!-- 답글 -->
		<%
		for (CommentsDTO dd : reply) {
			int width = dd.getDepth() * 25;
		%>
		<table class="view-table" style="border-top: none;">
			<tr>
				<td style="width: <%=width%>px; border-bottom: none; border-left: none; text-align: left; text-align: right; font-size: 22px;">⤷&nbsp;</td>
				<th class="lb" style="background-color: rgb(193, 235, 255); border-top: 2px solid #86bff9; width: 100px">
					<%=dd.getCreator()%>
					<br>
					<%=dd.getCreatedDate()%>
				</th>
				<td class="indent5" style="border-top: 2px solid #86bff9;">
					<textarea rows="5" readonly="readonly" style="resize: none;"><%=dd.getComment()%></textarea>
				</td>
			</tr>
		</table>
		<br>
		<%
		}
		%>
		<%
		}
		%>
	</div>
	<%
		}
	%>
	
	<!-- 댓글 모달 -->
	<%@include file="/extcore/jsp/common/include/comments-include.jsp"%>

<script type="text/javascript">
	const tapOid = document.getElementById("oid").value;

	document.addEventListener("DOMContentLoaded", function() {
		const isCreated1 = AUIGrid.isCreated(partGridID); // 품목
		if (isCreated1) {
			AUIGrid.resize(partGridID);
		} else {
			createAUIGrid1(columnPart);
		}
		const isCreated2 = AUIGrid.isCreated(rohsGridID); // 관련대표물질
		if (isCreated2) {
			AUIGrid.resize(rohsGridID);
		} else {
			createAUIGridRohs1(columnRohs);
		}
		const isCreated3 = AUIGrid.isCreated(rohs2GridID); // 관련물질
		if (isCreated3) {
			AUIGrid.resize(rohs2GridID);
		} else {
			createAUIGridRohs2(columnRohs2);
		}
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(partGridID);
		AUIGrid.resize(rohsGridID);
		AUIGrid.resize(rohs2GridID);
	});
</script>