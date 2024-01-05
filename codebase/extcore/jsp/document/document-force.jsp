<%@page import="net.sf.json.JSONObject"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.doc.dto.DocumentDTO"%>
<%@page import="wt.doc.DocumentType"%>
<%@page import="com.e3ps.admin.form.FormTemplate"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
DocumentDTO dto = (DocumentDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<style type="text/css">
iframe {
	margin-top: 3px;
}
</style>
<script type="text/javascript" src="/Windchill/extcore/dext5editor/js/dext5editor.js"></script>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				문서 수정(
				<font color="red">
					<b>관리자 권한</b>
				</font>
				)
			</div>
		</td>
		<td class="right">
			<input type="button" value="관리자 권한 수정" title="관리자 권한 수정" class="red" onclick="force();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="130">
		<col width="350">
		<col width="130">
		<col width="350">
		<col width="130">
		<col width="350">
		<col width="130">
		<col width="350">
	</colgroup>
	<tr>
		<th class="lb" colspan="8">
			<%=dto.getName()%>
		</th>
	</tr>
	<tr>
		<th class="lb">문서번호</th>
		<td class="indent5"><%=dto.getNumber()%></td>
		<th>문서분류</th>
		<td class="indent5"><%=dto.getLocation()%></td>
		<th>상태</th>
		<td class="indent5"><%=dto.getState()%></td>
		<th>REV</th>
		<td class="indent5">
			<%=dto.getVersion()%>.<%=dto.getIteration()%>
		</td>
	</tr>
	<tr>
		<th class="lb">등록일</th>
		<td class="indent5"><%=dto.getCreatedDate()%></td>
		<th>수정일</th>
		<td class="indent5"><%=dto.getModifiedDate()%></td>
		<th>등록자</th>
		<td class="indent5"><%=dto.getCreator()%></td>
		<th>수정자</th>
		<td class="indent5"><%=dto.getModifier()%></td>
	</tr>
	<tr>
		<!-- 				<th class="lb">결재방식</th> -->
		<%-- 				<td class="indent5"><%=dto.getApprovaltype_name()%></td> --%>
		<th class="lb">프로젝트 코드</th>
		<td class="indent5"><%=dto.getModel_name()%></td>
		<th>보존기간</th>
		<td class="indent5"><%=dto.getPreseration_name()%></td>
		<th>부서</th>
		<td class="indent5"><%=dto.getDeptcode_name()%></td>
		<th>작성자</th>
		<td class="indent5"><%=dto.getWriter()%></td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td colspan="7" class="indent7 pb8">
			<textarea name="contents" id="contents" rows="15" style="display: none;"><%=dto.getContent() != null ? dto.getContent() : ""%></textarea>
			<script type="text/javascript">
				new Dext5editor('content');
				const content = document.getElementById("contents").value;
				DEXT5.setBodyValue(content, 'content');
			</script>
		</td>
	</tr>
	<tr>
		<th class="lb">문서설명</th>
		<td colspan="7" class="indent5">
			<div class="textarea-auto">
				<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">주 첨부파일</th>
		<td class="indent5" colspan="7">
			<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="5">
			<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="관리자 권한 수정" title="관리자 권한 수정" class="red" onclick="force();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<script type="text/javascript">
	function force() {
		if (!confirm("수정 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const secondarys = toArray("secondarys");
		const primary = document.querySelector("input[name=primary]");
		const content = DEXT5.getBodyValue("content");
		const url = getCallUrl("/doc/force");
		const params = {
			oid : oid,
			secondarys : secondarys,
			primary : primary != null ? primary.value : "",
			content : content
		};

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
	}

	document.addEventListener("DOMContentLoaded", function() {
		autoTextarea();
	});

	window.addEventListener("resize", function() {
	});
</script>