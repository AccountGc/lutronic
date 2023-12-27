<%@page import="java.util.Map"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="com.e3ps.change.eco.dto.EcoDTO"%>
<%@page import="com.e3ps.change.eo.dto.EoDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ReferenceFactory rf = new ReferenceFactory();
String tapOid = request.getParameter("tapOid");
EChangeOrder eChangeOrder = (EChangeOrder) rf.getReference(tapOid).getObject();
String type = eChangeOrder.getEoType();
%>

<%
if ("CHANGE".equals(type)) {
	EcoDTO dto = new EcoDTO(tapOid);
%>
<table class="view-table">
	<colgroup>
		<col width="130">
		<col width="450">
		<col width="130">
		<col width="450">
	</colgroup>
	<tr>
		<th class="lb">ECO 제목</th>
		<td class="indent5"><%=dto.getName()%></td>
		<th>ECO 번호</th>
		<td class="indent5"><%=dto.getNumber()%></td>
	</tr>
	<tr>
		<th class="lb">상태</th>
		<td class="indent5"><%=dto.getState()%></td>
		<th>등록자</th>
		<td class="indent5"><%=dto.getCreator()%></td>
	</tr>
	<tr>
		<th class="lb">구분</th>
		<td class="indent5"><%=dto.getState()%></td>
		<th>등록일</th>
		<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
	</tr>
	<tr>
		<th class="lb">인허가 변경</th>
		<td class="indent5"><%=dto.getLicensing_name()%></td>
		<th>위험통제</th>
		<td class="indent5"><%=dto.getRiskType_name()%></td>
	</tr>
	<tr>
		<th class="lb">수정일</th>
		<td class="indent5"><%=dto.getModifiedDate_text()%></td>
		<th>승인일</th>
		<td class="indent5"><%=dto.getApproveDate()%></td>
	</tr>
	<tr>
		<th class="lb">변경사항</th>
		<td colspan="3" class="indent5">
			<textarea rows="5" readonly="readonly" id="eoCommentA" rows="5"><%=dto.getEoCommentA()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">변경사유</th>
		<td colspan="3" class="indent5">
			<textarea rows="5" readonly="readonly" id="eoCommentB" rows="5"><%=dto.getEoCommentB()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">특기사항</th>
		<td colspan="3" class="indent5">
			<textarea rows="5" readonly="readonly" id="eoCommentC" rows="5"><%=dto.getEoCommentC()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">기타사항</th>
		<td colspan="3" class="indent5">
			<textarea rows="5" readonly="readonly" id="eoCommentD" rows="5"><%=dto.getEoCommentD()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">설계변경 부품 내역파일</th>
		<td class="indent5" colspan="3">
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
				<b>등록된 설계변경 부품 내역파일이 없습니다.</b>
			</font>
			<%
			}
			%>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>
<!-- 설계변경 활동 -->
<jsp:include page="/extcore/jsp/change/activity/include/activity-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="250" name="height" />
</jsp:include>
<!-- 설계변경 품목 -->
<jsp:include page="/extcore/jsp/change/eco/include/eco-part-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="true" name="multi" />
</jsp:include>

<script type="text/javascript">
	function autoHeight() {
		const eoCommentC = document.getElementById("eoCommentC");
		eoCommentC.style.height = "auto";
		eoCommentC.style.height = "500px";
		// 		const style = window.getComputedStyle(eoCommentC);
		// 		console.log(style);
	}

	document.addEventListener("DOMContentLoaded", function() {
		autoHeight();
		const isCreated200 = AUIGrid.isCreated(myGridID200); // 설변 활동
		if (isCreated200) {
			AUIGrid.resize(myGridID200);
		} else {
			createAUIGrid200(columns200);
		}
		const isCreated500 = AUIGrid.isCreated(myGridID500); // 설변 품목
		if (isCreated500) {
			AUIGrid.resize(myGridID500);
		} else {
			createAUIGrid500(columns500);
		}
		const isCreated510 = AUIGrid.isCreated(myGridID510); // 설변 품목
		if (isCreated510) {
			AUIGrid.resize(myGridID510);
		} else {
			createAUIGrid510(columns510);
		}
	});
</script>

<%
} else {
EoDTO dto = new EoDTO(tapOid);
%>
<!-- 기본 정보 -->
<table class="view-table">
	<colgroup>
		<col width="130">
		<col width="350">
		<col width="130">
		<col width="350">
		<col width="130">
		<col width="350">
	</colgroup>
	<tr>
		<th class="lb">EO 번호</th>
		<td class="indent5"><%=dto.getNumber()%></td>
		<th>EO 제목</th>
		<td class="indent5"><%=dto.getName()%></td>
		<th>상태</th>
		<td class="indent5"><%=dto.getState()%></td>
	</tr>
	<tr>
		<th class="lb">제품명</th>
		<td class="indent5"><%=dto.getModel_name()%></td>
		<th>구분</th>
		<td class="indent5" colspan="3"><%=dto.getEoType()%></td>
	</tr>
	<tr>
		<th class="lb">등록자</th>
		<td class="indent5"><%=dto.getCreator()%></td>
		<th>등록일</th>
		<td class="indent5"><%=dto.getCreatedDate()%></td>
		<th>수정일</th>
		<td class="indent5"><%=dto.getModifiedDate()%></td>
	</tr>
	<tr>
		<th class="lb">완제품 품목</th>
		<td colspan="5">
			<jsp:include page="/extcore/jsp/change/include/complete-part-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="view" name="mode" />
				<jsp:param value="true" name="multi" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">제품 설계 개요</th>
		<td colspan="5" class="indent5">
			<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getEoCommentA()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">특기사항</th>
		<td colspan="5" class="indent5">
			<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getEoCommentB()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">기타사항</th>
		<td colspan="5" class="indent5">
			<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getEoCommentC()%></textarea>
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

<!-- 	관련 문서 -->
<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="insert90" name="method" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="false" name="header" />
</jsp:include>

<!-- 	관련 MODEL -->
<jsp:include page="/extcore/jsp/admin/code/include/code-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="insert300" name="method" />
	<jsp:param value="MODEL" name="codeType" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="300" name="height" />
	<jsp:param value="true" name="header" />
</jsp:include>

<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		const isCreated90 = AUIGrid.isCreated(myGridID90); // 문서
		if (isCreated90) {
			AUIGrid.resize(myGridID90);
		} else {
			createAUIGrid90(columns90);
		}
		const isCreated300 = AUIGrid.isCreated(myGridID300); // MODEL
		if (isCreated300) {
			AUIGrid.resize(myGridID300);
		} else {
			createAUIGrid300(columns300);
		}
		const isCreated104 = AUIGrid.isCreated(myGridID104); // 완제품
		if (isCreated104) {
			AUIGrid.resize(myGridID104);
		} else {
			createAUIGrid104(columns104);
		}
	});
</script>
<%
}
%>




