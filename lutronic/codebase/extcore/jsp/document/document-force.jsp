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
		<col width="150">
		<col width="*">
		<col width="150">
		<col width="*">
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<!-- 				<th class="lb">문서번호</th> -->
		<%-- 				<td class="indent5"><%=dto.getNumber()%></td> --%>
		<th>내부문서번호</th>
		<td class="indent5"><%=dto.getInteralnumber()%></td>
		<th>문서분류</th>
		<td class="indent5"><%=dto.getLocation()%></td>
		<th>상태</th>
		<td class="indent5"><%=dto.getState()%></td>
	</tr>
	<tr>
		<th class="lb">REV</th>
		<td class="indent5">
			<%=dto.getVersion()%>.<%=dto.getIteration()%>
			<%
			if (!dto.isLatest()) {
			%>
			&nbsp;
			<b>
				<a href="javascript:latest();">(최신버전으로)</a>
			</b>
			<%
			}
			%>
		</td>
		<th>등록자</th>
		<td class="indent5"><%=dto.getCreator()%></td>
		<th>수정자</th>
		<td class="indent5"><%=dto.getModifier()%></td>
	</tr>
	<tr>
		<th class="lb">등록일</th>
		<td class="indent5"><%=dto.getCreatedDate()%></td>
		<th>수정일</th>
		<td class="indent5"><%=dto.getModifiedDate()%></td>
		<th>문서유형</th>
		<td class="indent5"><%=dto.getDocumentType_name()%></td>
	</tr>
	<tr>
		<th class="lb">작성자</th>
		<td class="indent5"><%=dto.getWriter()%></td>
		<th>보존기간</th>
		<td class="indent5"><%=dto.getPreseration_name()%></td>
		<th>부서</th>
		<td class="indent5"><%=dto.getDeptcode_name()%></td>
	</tr>
	<tr>
		<th class="lb">결재방식</th>
		<td class="indent5"><%=dto.getApprovaltype_name()%></td>
		<!-- 				<th>내부문서번호</th> -->
		<%-- 				<td class="indent5"><%=dto.getInteralnumber()%></td> --%>
		<th>프로젝트 코드</th>
		<td class="indent5" colspan="3"><%=dto.getModel_name()%></td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td colspan="5" class="indent7 pb8">
			<textarea name="contents" id="contents" rows="15" style="display: none;"><%=dto.getContent() != null ? dto.getContent() : ""%></textarea>
			<script type="text/javascript">
				// 에디터를 view 모드로 설정합니다.
				DEXT5.config.Mode = "view";

				new Dext5editor('content');
				const content = document.getElementById("contents").value;
				DEXT5.setBodyValue(content, 'content');
			</script>
		</td>
	</tr>
	<tr>
		<th class="lb">설명</th>
		<td colspan="5" class="indent5">
			<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
		</td>
	</tr>
	<tr>
		<th class="req lb">주 첨부파일</th>
		<td class="indent5" colspan="5">
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

<!-- 관련 품목 -->
<jsp:include page="/extcore/jsp/part/include/part-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
</jsp:include>

<!-- 	관련 문서 -->
<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
</jsp:include>

<!-- 	관련 EO -->
<jsp:include page="/extcore/jsp/change/eo/include/eo-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
</jsp:include>

<!-- 	관련 CR -->
<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
</jsp:include>

<!-- 	관련 ECPR -->
<jsp:include page="/extcore/jsp/change/ecpr/include/ecpr-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
</jsp:include>

<!-- 	관련 ECO -->
<jsp:include page="/extcore/jsp/change/eco/include/eco-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
</jsp:include>

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

		const primary = document.querySelector("input[name=primary]");

		if (primary == null) {
			alert("주 첨부파일을 선택하세요.");
			return false;
		}

		if (!confirm("수정 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const secondarys = toArray("secondarys");

		const url = getCallUrl("/doc/force");
		const params = {
			oid : oid,
			secondarys : secondarys,
			primary : primary.value,
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
		createAUIGrid90(columns90);
		createAUIGrid91(columns91);
		createAUIGrid100(columns100);
		createAUIGrid101(columns101);
		createAUIGrid103(columns103);
		createAUIGrid105(columns105);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID91);
		AUIGrid.resize(myGridID100);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID103);
		AUIGrid.resize(myGridID105);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID91);
		AUIGrid.resize(myGridID100);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID103);
		AUIGrid.resize(myGridID105);
	});
</script>