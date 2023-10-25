<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.part.dto.PartDTO"%>
<%@page import="wt.iba.definition.litedefinition.IBAUtility"%>
<%@page import="com.e3ps.common.comments.beans.CommentsDTO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.doc.dto.DocumentDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String tapOid = request.getParameter("tapOid");
PartDTO dto = new PartDTO(tapOid);
boolean isAdmin = CommonUtil.isAdmin();
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
			<th class="lb">품목번호</th>
			<td class="indent5"><%=dto.getNumber()%></td>
			<th class="lb">품목명</th>
			<td class="indent5"><%=dto.getName()%></td>
			<th class="lb">품목분류</th>
			<td class="indent5"><%=dto.getLocation()%></td>
		</tr>
		<tr>
			<th class="lb">상태</th>
			<td class="indent5"><%=dto.getState()%></td>
			<th class="lb">Rev.</th>
			<td class="indent5"><%=dto.getVersion()%></td>
			<th class="lb">등록자</th>
			<td class="indent5"><%=dto.getCreator()%></td>
		</tr>
		<tr>
			<th class="lb">등록일</th>
			<td class="indent5"><%=dto.getCreateDate()%></td>
			<th class="lb">수정자</th>
			<td class="indent5"><%=dto.getModifier()%></td>
			<th class="lb">수정일</th>
			<td class="indent5"><%=dto.getModifyDate()%></td>
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
	<!-- 품목 속성 -->
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png">
					품목 속성
				</div>
			</td>
		</tr>
	</table>
	<jsp:include page="/extcore/jsp/common/attributes_include.jsp">
		<jsp:param value="<%=dto.getOid()%>" name="oid" />
		<jsp:param value="part" name="module" />
	</jsp:include>
	<!-- 	주도면 -->
	<jsp:include page="/extcore/jsp/drawing/drawingView_include.jsp">
		<jsp:param value="part" name="moduleType" />
		<jsp:param value="main" name="epmType" />
		<jsp:param value="<%=dto.getOid()%>" name="oid" />
		<jsp:param value="주도면" name="title" />
		<jsp:param value="epmOid" name="paramName" />
	</jsp:include>
	<jsp:include page="/extcore/jsp/common/attributes_include.jsp">
		<jsp:param value="<%=dto.getOid()%>" name="oid" />
		<jsp:param value="part" name="module" />
	</jsp:include>
	<!-- 참조 항목 -->
	<jsp:include page="/extcore/jsp/drawing/include_viewReferenceBy.jsp">
		<jsp:param value="part" name="moduleType" />
		<jsp:param value="<%=dto.getEpmOid()%>" name="oid" />
	</jsp:include>
	<!-- 관련 객체 -->
	<jsp:include page="/extcore/jsp/part/part-reference-include.jsp">
		<jsp:param value="<%=dto.getOid()%>" name="oid" />
	</jsp:include>
	<%
	if (isAdmin) {
	%>
	<!-- 관리자 속성 -->
	<div id="tabs-5">
		<jsp:include page="/extcore/jsp/common/adminAttributes_include.jsp">
			<jsp:param value="part" name="module" />
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
	<%
	}
	%>
	<!-- 관련품목 -->
	<jsp:include page="/extcore/jsp/part/include/part-related-include.jsp">
		<jsp:param value="<%=dto.getOid()%>" name="oid" />
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

	// 최신버전으로 페이지 이동
	function latest() {
		const url = getCallUrl("/doc/latest?oid=" + tapOid);
		document.location.href = url;
	}


	document.addEventListener("DOMContentLoaded", function() {
		$(".comment-table").show();
		const isCreated = AUIGrid.isCreated(drawingGridID); // 주도면
		if (isCreated) {
			AUIGrid.resize(drawingGridID);
		} else {
			createAUIGridDrawing(columnsDrawing);
		}
		const isCreated2 = AUIGrid.isCreated(refbyGridID); // 참조항목
		if (isCreated2) {
			AUIGrid.resize(refbyGridID);
		} else {
			createAUIGrid3(columnRefby);
		}
		const isCreated90 = AUIGrid.isCreated(myGridID90); // 문서
		if (isCreated90) {
			AUIGrid.resize(myGridID90);
		} else {
			createAUIGrid90(columns90);
		}
		const isCreated106 = AUIGrid.isCreated(myGridID106); // rohs
		if (isCreated106) {
			AUIGrid.resize(myGridID106);
		} else {
			createAUIGrid106(columns106);
		}
		const isCreatedAdmin = AUIGrid.isCreated(adminGridID); // 관리자속성
		if (isCreatedAdmin) {
			AUIGrid.resize(adminGridID);
		} else {
			createAUIGridAdmin(columnsAdmin);
		}
		const isCreated80 = AUIGrid.isCreated(myGridID80); // 상위 품목
		if (isCreated80) {
			AUIGrid.resize(myGridID80);
		} else {
			createAUIGrid80(columns80);
		}
		const isCreated81 = AUIGrid.isCreated(myGridID81); // 하위 품목
		if (isCreated81) {
			AUIGrid.resize(myGridID81);
		} else {
			createAUIGrid81(columns81);
		}
		const isCreated82 = AUIGrid.isCreated(myGridID82); // end item
		if (isCreated82) {
			AUIGrid.resize(myGridID82);
		} else {
			createAUIGrid82(columns82);
		}
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(drawingGridID);
		AUIGrid.resize(refbyGridID);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID106);
		AUIGrid.resize(adminGridID);
		AUIGrid.resize(myGridID80);
		AUIGrid.resize(myGridID81);
		AUIGrid.resize(myGridID82);
	});
</script>